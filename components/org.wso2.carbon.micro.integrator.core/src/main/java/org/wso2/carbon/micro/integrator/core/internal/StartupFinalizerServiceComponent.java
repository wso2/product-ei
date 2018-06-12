/*
 * Copyright 2004,2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.carbon.micro.integrator.core.internal;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.engine.ListenerManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.CarbonConstants;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.core.ServerStatus;
import org.wso2.carbon.micro.integrator.core.deployment.DeploymentService;
import org.wso2.carbon.utils.CarbonUtils;
import org.wso2.carbon.utils.ConfigurationContextService;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Responsible for finalizing startup of the Carbon server. This component will run after all other
 * components & service required for the Carbon server to reach a stable state become available.
 *
 * This service component is mainly responsible for starting the Axis2 Transport ListenerManager
 * once all the required OSGi services in the system become available. This is because of the
 * fact  that requests from external parties should only be serviced after the Axis2 engine
 * & Carbon has  reached a stable and consistent state.
 *
 * @scr.component name="org.wso2.carbon.core.internal.StartupFinalizerServiceComponent"
 * immediate="true"
 * @scr.reference name="org.wso2.carbon.configCtx"
 * interface="org.wso2.carbon.utils.ConfigurationContextService" cardinality="1..1"
 * policy="dynamic" bind="setConfigurationContext" unbind="unsetConfigurationContext"
 * @scr.reference name="org.wso2.carbon.micro.integrator.core.deployment.DeploymentService"
 * interface="org.wso2.carbon.micro.integrator.core.deployment.DeploymentService"
 * cardinality="1..1" policy="dynamic" bind="setDeploymentService"
 * unbind="unsetDeploymentService"
 **/
public class StartupFinalizerServiceComponent implements ServiceListener {
    private static final Log log = LogFactory.getLog(StartupFinalizerServiceComponent.class);
    private static final String TRANSPORT_MANAGER =
            "org.wso2.carbon.tomcat.ext.transport.ServletTransportManager";

    private ConfigurationContext configCtx;
    private List<String> requiredServices = new ArrayList<String>();
    private BundleContext bundleContext;

    private Timer pendingServicesObservationTimer = new Timer();
    private CarbonCoreDataHolder dataHolder = CarbonCoreDataHolder.getInstance();
    private ServiceRegistration listerManagerServiceRegistration;

    protected void activate(ComponentContext ctxt) {
        try {

            bundleContext = ctxt.getBundleContext();
            PrivilegedCarbonContext privilegedCarbonContext = PrivilegedCarbonContext
                    .getThreadLocalCarbonContext();
            privilegedCarbonContext.setTenantDomain(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
            privilegedCarbonContext.setTenantId(MultitenantConstants.SUPER_TENANT_ID);
            populateRequiredServices();
            if (requiredServices.isEmpty()) {
                completeInitialization(bundleContext);
                return;
            }

            StringBuffer ldapFilter = new StringBuffer("(|");
            for (String service : requiredServices) {
                ldapFilter.append("(").append(Constants.OBJECTCLASS).append("=").append(service).append(")");
            }
            ldapFilter.append(")");

            bundleContext.addServiceListener(this, ldapFilter.toString());
            ServiceReference[] serviceReferences =
                    bundleContext.getServiceReferences((String)null, ldapFilter.toString());
            if (serviceReferences != null) {
                for (ServiceReference reference : serviceReferences) {
                    String service = ((String[]) reference.getProperty(Constants.OBJECTCLASS))[0];
                    requiredServices.remove(service);
                    if (log.isDebugEnabled()) {
                        log.debug("Removed pending service " + service);
                    }
                }
            }
            if (requiredServices.isEmpty()) {
                completeInitialization(bundleContext);
            } else {
                schedulePendingServicesObservationTimer();
            }
        } catch (Throwable e) {
            log.fatal("Cannot activate StartupFinalizerServiceComponent", e);
        }
    }

    protected void deactivate(ComponentContext ctxt) {
        listerManagerServiceRegistration.unregister();
    }

    private void populateRequiredServices() {
        Bundle[] bundles = bundleContext.getBundles();
        for (Bundle bundle : bundles) {
            String requiredServiceList =
                    (String) bundle.getHeaders().
                            get(CarbonConstants.CarbonManifestHeaders.LISTENER_MANAGER_INIT_REQUIRED_SERVICE);
            if (requiredServiceList != null) {
                String[] values = requiredServiceList.split(",");
                for (String value : values) {
                    requiredServices.add(value);
                }
            }
        }
    }

    private void schedulePendingServicesObservationTimer() {
        pendingServicesObservationTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (!requiredServices.isEmpty()) {
                    StringBuffer services = new StringBuffer();
                    for (String service : requiredServices) {
                        services.append(service).append(",");
                    }
                    log.warn("Waiting for required OSGi services: " + services.toString());
                }
            }
        }, 60000, 60000);
    }

    private void completeInitialization(BundleContext bundleContext) {
        //Add CAppDeployer to deployment engine
        /* notify listeners of server startup before transport starts */
//        CarbonCoreServiceComponent.notifyBefore();

        bundleContext.removeServiceListener(this);
        pendingServicesObservationTimer.cancel();
        ListenerManager listenerManager = configCtx.getListenerManager();
        if (listenerManager == null) {
            listenerManager = new ListenerManager();
        }
        listenerManager.setShutdownHookRequired(false);
        listenerManager.startSystem(configCtx);

        if (CarbonUtils.isRunningInStandaloneMode()) {
            try {
                Class<?> transportManagerClass = Class.forName(TRANSPORT_MANAGER);
                Object transportManager = transportManagerClass.newInstance();
                Method method = transportManagerClass.getMethod("startTransports");
                method.invoke(transportManager);
            } catch (Exception e) {
                String msg = "Cannot start transports";
                log.fatal(msg, e);
                return;
            }
        }
        listerManagerServiceRegistration =
                bundleContext.registerService(ListenerManager.class.getName(), listenerManager, null);
/*        try {
            new JMXServerManager().startJMXService();
        } catch (ServerException e) {
            log.error("Cannot start JMX service", e);
        }*/
        if (log.isDebugEnabled()) {
            log.debug("Started Transport Listener Manager");
        }
        /* notify listeners of server startup after transport starts */
//        CarbonCoreServiceComponent.startup();
//        CarbonCoreServiceComponent.notifyAfter();
        setServerStartTimeParam();
        printInfo();
    }
    
    private void setServerStartTimeParam() {
        Parameter startTimeParam = new Parameter();
        startTimeParam.setName(CarbonConstants.SERVER_START_TIME);
        startTimeParam.setValue(System.getProperty(CarbonConstants.START_TIME));
        try {
            configCtx.getAxisConfiguration().addParameter(startTimeParam);
        } catch (AxisFault e) {
            log.error("Could not set the  server start time parameter", e);
        }
    }
    
    private void setServerStartUpDurationParam(String startupTime) {
        Parameter startupDurationParam = new Parameter();
        startupDurationParam.setName(CarbonConstants.START_UP_DURATION);
        startupDurationParam.setValue(startupTime);
        try {
            configCtx.getAxisConfiguration().addParameter(startupDurationParam);
        } catch (AxisFault e) {
            log.error("Could not set the  server start up duration parameter", e);
        }
    }

    private void printInfo() {
        long startTime = Long.parseLong(System.getProperty(CarbonConstants.START_TIME));
        long startupTime = (System.currentTimeMillis() - startTime) / 1000;
        try {
            log.info("Server           :  " + dataHolder.getServerConfigurationService().getFirstProperty("Name") + "-" +
                    dataHolder.getServerConfigurationService().getFirstProperty("Version"));
        } catch (Exception e) {
            log.debug("Error while retrieving server configuration",e);
        }
        try {
            ServerStatus.setServerRunning();
        } catch (AxisFault e) {
            String msg = "Cannot set server to running mode";
            log.error(msg, e);
        }
        log.info("WSO2 Carbon started in " + startupTime + " sec");
        setServerStartUpDurationParam(String.valueOf(startupTime));
//        System.getProperties().remove(CarbonConstants.START_TIME);
        System.getProperties().remove("setup"); // Clear the setup System property
    }

    protected void setConfigurationContext(ConfigurationContextService configCtx) {
        this.configCtx = configCtx.getServerConfigContext();
    }

    protected void unsetConfigurationContext(ConfigurationContextService configCtx) {
        this.configCtx = null;
    }

    protected void setDeploymentService(DeploymentService deploymentService) {
        log.debug("Set DeploymentService");
    }

    protected void unsetDeploymentService(DeploymentService deploymentService) {
        log.debug("Unset DeploymentService");
    }

    public synchronized void serviceChanged(ServiceEvent event) {
        if (event.getType() == ServiceEvent.REGISTERED) {
            String service =
                    ((String[]) event.getServiceReference().getProperty(Constants.OBJECTCLASS))[0];
            requiredServices.remove(service);
            if (log.isDebugEnabled()) {
                log.debug("Removed pending service " + service);
            }
            if (requiredServices.isEmpty()) {
                completeInitialization(bundleContext);
            }
        }
    }
}
