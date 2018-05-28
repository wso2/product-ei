/*
 * Copyright 2005-2007 WSO2, Inc. (http://wso2.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.micro.integrator.core.internal;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.description.AxisDescription;
import org.apache.axis2.description.AxisModule;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.AxisServiceGroup;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.axis2.engine.AxisEvent;
import org.apache.axis2.engine.AxisObserver;
import org.apache.axis2.util.JavaUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.CarbonConstants;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.core.RegistryResources;
import org.wso2.carbon.core.Resources;
import org.wso2.carbon.core.util.SystemFilter;

import org.wso2.carbon.micro.integrator.core.init.CoreComponent;
import org.wso2.carbon.utils.CarbonUtils;
import org.wso2.carbon.utils.deployment.GhostDeployerUtils;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This deployment interceptor will be called whenever before a module is initialized or service is
 * deployed.
 *
 * @see AxisObserver
 */
public class DeploymentInterceptor implements AxisObserver {
    private static final Log log = LogFactory.getLog(DeploymentInterceptor.class);

    private static volatile String[] httpAdminServicesList = null;
    private static volatile boolean allAdminServicesHttp = false;
    private static volatile boolean isFirstCheck = true;

    private final Map<String, Parameter> paramMap = new HashMap<String, Parameter>();

    private final HashMap<String, HashMap<String, AxisDescription>> faultyServicesDueToModules =
            new HashMap<String, HashMap<String, AxisDescription>>();


    private int tenantId = -1;
    private String tenantDomain = MultitenantConstants.SUPER_TENANT_DOMAIN_NAME; // TODO: intitializing the tenant domain
//    private CarbonCoreDataHolder dataHolder = CarbonCoreDataHolder.getInstance();


    public void init(AxisConfiguration axisConfig) {
        extractTenantInfo(axisConfig);
    }

    private void extractTenantInfo(AxisConfiguration axisConfig) {
        PrivilegedCarbonContext carbonContext = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        tenantId = carbonContext.getTenantId();
        tenantDomain = carbonContext.getTenantDomain();
    }

    private String getTenantIdAndDomainString() {
        return (tenantId != -1 && tenantId != MultitenantConstants.SUPER_TENANT_ID) ?
               " {" + tenantDomain + "[" + tenantId + "]}" : " {super-tenant}";
    }

    public void serviceGroupUpdate(AxisEvent axisEvent, AxisServiceGroup axisServiceGroup) {
        if (CarbonUtils.isWorkerNode()) {
            if (log.isDebugEnabled()) {
                log.debug("Skip deployment intercepting in worker nodes.");
            }
            return;
        }
        PrivilegedCarbonContext.startTenantFlow();
        try {
            PrivilegedCarbonContext carbonContext = PrivilegedCarbonContext.getThreadLocalCarbonContext();
            carbonContext.setTenantId(tenantId);
            carbonContext.setTenantDomain(tenantDomain);
            carbonContext.setApplicationName(axisServiceGroup.getServiceGroupName());
            // We do not persist Admin service events
            if (SystemFilter.isFilteredOutService(axisServiceGroup)) {
                return;
            }

            boolean isClientSide = true;

            Iterator<AxisService> axisServiceGroupIterator = axisServiceGroup.getServices();
            while (axisServiceGroupIterator.hasNext()) {
                AxisService axisService = axisServiceGroupIterator.next();
                if (!axisService.isClientSide()) {
                    isClientSide = false;
                    break;
                }
            }

            if (isClientSide) {
                return;
            }

            int eventType = axisEvent.getEventType();
            // we only process ghost services when it is removed..
            if (SystemFilter.isGhostServiceGroup(axisServiceGroup) &&
                eventType != AxisEvent.SERVICE_REMOVE) {
                return;
            }
            if (eventType == AxisEvent.SERVICE_DEPLOY) {
                if (log.isDebugEnabled()) {
                    log.debug("Deploying service group : " +
                              axisServiceGroup.getServiceGroupName() + getTenantIdAndDomainString());
                }


            } else if (eventType == AxisEvent.SERVICE_REMOVE) {
                if (log.isDebugEnabled()) {
                    log.debug("Removing service group : " +
                              axisServiceGroup.getServiceGroupName() +
                              getTenantIdAndDomainString());
                }


            }
        } finally {
            PrivilegedCarbonContext.endTenantFlow();
        }
    }


    public void serviceUpdate(AxisEvent axisEvent, AxisService axisService) {
        if (CarbonUtils.isWorkerNode() && axisEvent.getEventType() != AxisEvent.SERVICE_DEPLOY) {
            if (log.isDebugEnabled()) {
                log.debug("Skip deployment intercepting in worker nodes.");
            }
            return;
        }
        PrivilegedCarbonContext.startTenantFlow();
        try {
            PrivilegedCarbonContext carbonContext = PrivilegedCarbonContext.getThreadLocalCarbonContext();
            carbonContext.setTenantId(tenantId);
            carbonContext.setTenantDomain(tenantDomain);
            carbonContext.setApplicationName(axisService.getName());
            // We do not persist Admin service events
            if (SystemFilter.isFilteredOutService((AxisServiceGroup) axisService.getParent())) {
                // here we expose some admin services in HTTP
                if (isHttpAdminService(axisService.getName())) {
                    changeAdminServiceTransport(axisService);
                }
                return;
            }

            if (axisService.isClientSide()) {
                return;
            }
            int eventType = axisEvent.getEventType();
            // we only process ghost services when it is removed..
            if (GhostDeployerUtils.isGhostService(axisService) &&
                eventType != AxisEvent.SERVICE_REMOVE) {
                return;
            }
            String serviceName = axisService.getName();
            try {

                // if (eventType == AxisEvent.SERVICE_STOP) do nothing

                if (eventType == AxisEvent.SERVICE_DEPLOY) {
                    axisService.setActive(getPersistedServiceStatus(axisService));
                    if (!JavaUtils.isTrue(axisService.getParameterValue(
                            CarbonConstants.HIDDEN_SERVICE_PARAM_NAME))) {
                        log.info("Deploying Axis2 service: " + serviceName +
                                 getTenantIdAndDomainString());
                    } else if (log.isDebugEnabled()) {
                        log.debug("Deploying hidden Axis2 service : " + serviceName +
                                  getTenantIdAndDomainString());
                    }


                } /*else if (eventType == AxisEvent.SERVICE_START) {
                    removeServiceStatus(axisService);
                } else if (eventType == AxisEvent.SERVICE_STOP) {
                    persistServiceStatus(axisService);
                } else if (eventType == AxisEvent.SERVICE_REMOVE) {

                    log.info("Removing Axis2 Service: " + axisService.getName() +
                             getTenantIdAndDomainString());
                    if (!keepHistory(axisService)) {
                        deleteServiceResource(axisService);
                    }
                }*/
            } catch (Exception e) {
                String msg = "Exception occurred while handling service update event." +
                             getTenantIdAndDomainString();
                log.error(msg, e);
            }
        } finally {
            PrivilegedCarbonContext.endTenantFlow();
        }
    }

    private String getServiceResourcePath(AxisService axisService) {
        return RegistryResources.SERVICE_GROUPS + axisService.getAxisServiceGroup()
                .getServiceGroupName() + RegistryResources.SERVICES + axisService.getName();
    }

    private boolean getPersistedServiceStatus(AxisService axisService) {
        String serviceResourcePath = getServiceResourcePath(axisService);
        boolean isServerActive = axisService.isActive();
        return isServerActive;
    }

/*    private void deleteServiceResource(AxisService axisService) {
        String serviceResourcePath = getServiceResourcePath(axisService);
        try {
            if (registry.resourceExists(serviceResourcePath)) {
                registry.delete(serviceResourcePath);
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Service [" + axisService.getName() + "] doesn't have any resource or resource path ["
                            + serviceResourcePath + "] has already been deleted.");
                }
            }
        } catch (RegistryException e) {
            log.error("Failed to delete service resource.", e);
        }
    }*/

/*    private void persistServiceStatus(AxisService axisService) {
        String serviceResourcePath = getServiceResourcePath(axisService);
        Resource serviceResource;
        try {
            if (registry.resourceExists(serviceResourcePath)) {
                serviceResource = registry.get(serviceResourcePath);
            } else {
                serviceResource = registry.newCollection();
            }
            serviceResource.setProperty(RegistryResources.ServiceProperties.ACTIVE, Boolean.toString(axisService.isActive()));
            registry.put(serviceResourcePath, serviceResource);
        } catch (RegistryException e) {
            log.error("Failed to persist service status.", e);
        }
    }*/

/*    private void removeServiceStatus(AxisService axisService) {
        String serviceResourcePath = getServiceResourcePath(axisService);
        try {
            if (registry.resourceExists(serviceResourcePath)) {
                Resource serviceResource = registry.get(serviceResourcePath);
                serviceResource.removeProperty(RegistryResources.ServiceProperties.ACTIVE);
                registry.put(serviceResourcePath, serviceResource);
            }
        } catch (RegistryException e) {
            log.error("Failed to remove service status.", e);
        }
    }*/

    public void moduleUpdate(AxisEvent axisEvent, AxisModule axisModule) {
        if (CarbonUtils.isWorkerNode()) {
            if (log.isDebugEnabled()) {
                log.debug("Skip deployment intercepting in worker nodes.");
            }
            return;
        }
        PrivilegedCarbonContext.startTenantFlow();
        try {
            PrivilegedCarbonContext carbonContext = PrivilegedCarbonContext.
                    getThreadLocalCarbonContext();
            carbonContext.setTenantId(tenantId);
            carbonContext.setTenantDomain(tenantDomain);
            //TODO: Check whether we can ignore AdminModules - SystemFilter.isFilteredOutModule
            String moduleName = axisModule.getName();
            /*if (moduleName.equals(ServerConstants.ADMIN_MODULE) ||
                moduleName.equals(ServerConstants.TRACER_MODULE) ||
                moduleName.equals(ServerConstants.STATISTICS_MODULE)) {
                return;
            }*/

            // Handle.MODULE_DEPLOY event. This may be a new or existing module
            if (axisEvent.getEventType() == AxisEvent.MODULE_DEPLOY) {
                String moduleVersion;
                if (axisModule.getVersion() == null) {
                    log.warn("A valid Version not found for the module : '" + moduleName + "'" +
                             getTenantIdAndDomainString());
                    moduleVersion = Resources.ModuleProperties.UNDEFINED;
                } else {
                    moduleVersion = axisModule.getVersion().toString();
                }
                if (!SystemFilter.isFilteredOutModule(axisModule)) {
                    if (log.isDebugEnabled()) {
                        log.debug("Deploying Axis2 module: " + axisModule.getArchiveName() +
                                  getTenantIdAndDomainString());
                    }
                }

                // check whether the module is globally engaged
/*                boolean globallyEngaged = getPersistedModuleGloballyEngagedStatus(axisModule);
                if (globallyEngaged) {
                    axisModule.addParameter(new Parameter(RegistryResources.ModuleProperties.GLOBALLY_ENGAGED,
                                                          Boolean.TRUE.toString()));
                    axisModule.getParent().engageModule(axisModule);
                }*/
            }
        } finally {
            PrivilegedCarbonContext.endTenantFlow();
        }
    }

/*    *//**
     * This method reads the module globally engaged status from the registry
     * @param axisModule
     * @return
     *//*
    private boolean getPersistedModuleGloballyEngagedStatus(AxisModule axisModule) {
        boolean globallyEngagedModule = false;
        String moduleResourcePath = getModuleResourcePath(axisModule);

        try {
            if (registry.resourceExists(moduleResourcePath)) {
                Resource moduleResource = registry.get(moduleResourcePath);
                if (moduleResource.getProperty(RegistryResources.ModuleProperties.GLOBALLY_ENGAGED) != null) {
                    globallyEngagedModule = Boolean.valueOf(moduleResource.getProperty(
                            RegistryResources.ModuleProperties.GLOBALLY_ENGAGED));
                }
            }
        } catch (org.wso2.carbon.registry.core.exceptions.RegistryException e) {
            log.error("Failed to read persisted module globally engaged status.", e);
        }

        return globallyEngagedModule;
    }*/

    private String getModuleResourcePath(AxisModule axisModule) {
        return RegistryResources.MODULES + axisModule.getName() + "/" + axisModule.getVersion();
    }

    public void addParameter(Parameter parameter) throws AxisFault {
        paramMap.put(parameter.getName(), parameter);
    }

    public void removeParameter(Parameter param) throws AxisFault {
        paramMap.remove(param.getName());
    }

    public void deserializeParameters(OMElement omElement) throws AxisFault {
        //No need to do anything here
    }

    public Parameter getParameter(String paramName) {
        return paramMap.get(paramName);
    }

    public ArrayList<Parameter> getParameters() {
        Collection<Parameter> collection = paramMap.values();
        ArrayList<Parameter> arr = new ArrayList<Parameter>();
        for (Parameter aCollection : collection) {
            arr.add(aCollection);
        }
        return arr;
    }

    public boolean isParameterLocked(String paramName) {
        return (paramMap.get(paramName)).isLocked();
    }

    /**
     * Updates the map that keeps track of faulty services due to modules
     *
     * @param moduleName      This service has become faulty due this module.
     * @param axisDescription Data that are required when recovering the faulty service.
     */
    private void addFaultyServiceDueToModule(String moduleName, AxisDescription axisDescription) {
        HashMap<String, AxisDescription> faultyServicesMap;
        synchronized (faultyServicesDueToModules) {
            if (faultyServicesDueToModules.containsKey(moduleName)) {
                faultyServicesMap = faultyServicesDueToModules.get(moduleName);
                faultyServicesMap.put((String) axisDescription.getKey(), axisDescription);
            } else {
                faultyServicesMap = new HashMap<String, AxisDescription>();
                faultyServicesMap.put((String) axisDescription.getKey(), axisDescription);
                faultyServicesDueToModules.put(moduleName, faultyServicesMap);
            }
        }
    }

    private HashMap<String, AxisDescription> getFaultyServicesDueToModule(String moduleName) {
        if (faultyServicesDueToModules.containsKey(moduleName)) {
            return faultyServicesDueToModules.get(moduleName);
        }
        return new HashMap<String, AxisDescription>(1);
    }


    private void removeFaultyServiceDueToModule(String moduleName, String serviceGroupName) {
        synchronized (faultyServicesDueToModules) {
            HashMap<String, AxisDescription> faultyServices =
                    faultyServicesDueToModules.get(moduleName);
            if (faultyServices != null) {
                faultyServices.remove(serviceGroupName);
                if (faultyServices.isEmpty()) {
                    faultyServicesDueToModules.remove(moduleName);
                }
            }
        }
    }

    public void startServiceGroup(AxisServiceGroup serviceGroup,
                                  AxisConfiguration axisConfiguration) {
        for (Iterator itr = serviceGroup.getServices(); itr.hasNext(); ) {
            startService((AxisService) itr.next(), axisConfiguration);
        }
    }

    public void stopServiceGroup(AxisServiceGroup serviceGroup,
                                 AxisConfiguration axisConfiguration) {
        for (Iterator itr = serviceGroup.getServices(); itr.hasNext(); ) {
            stopService((AxisService) itr.next(), axisConfiguration);
        }
    }

    public void startService(AxisService axisService, AxisConfiguration axisConfiguration) {
        String serviceName = axisService.getName();

        if (log.isDebugEnabled()) {
            log.debug("Activating service: " + serviceName + getTenantIdAndDomainString());
        }

        try {
            axisConfiguration.startService(serviceName);
            //Removing the special special property
            Parameter param = axisService.getParameter(CarbonConstants.CARBON_FAULTY_SERVICE);
            if (param != null) {
                axisService.removeParameter(param);
            }
        } catch (AxisFault e) {
            String msg = "Cannot start service : " + serviceName + getTenantIdAndDomainString();
            log.error(msg, e);
        }
    }

    public void stopService(AxisService axisService, AxisConfiguration axisConfiguration) {
        String serviceName = axisService.getName();

        if (log.isDebugEnabled()) {
            log.debug("Deactivating service: " + serviceName + getTenantIdAndDomainString());
        }

        try {
            axisConfiguration.stopService(serviceName);
            axisService.addParameter(CarbonConstants.CARBON_FAULTY_SERVICE,
                                     CarbonConstants.CARBON_FAULTY_SERVICE_DUE_TO_MODULE);
        } catch (AxisFault e) {
            String msg = "Cannot stop service: " + serviceName + getTenantIdAndDomainString();
            log.error(msg, e);
        }
    }

    /**
     * This method is used to expose admin services in HTTP
     *
     * @param axisService
     */
    private void changeAdminServiceTransport(AxisService axisService) {
        axisService.addExposedTransport("http");
        if (log.isDebugEnabled()) {
            log.debug("AdminService " + axisService.getName() + " exposed in HTTP");
        }
    }

    /**
     * This method checks the service name against the list of services to be
     * exposed in HTTP
     *
     * @param serviceName
     * @return
     */
    private boolean isHttpAdminService(String serviceName) {

        if (!isFirstCheck && !allAdminServicesHttp && httpAdminServicesList == null) {
            return false;
        }
        // set all admin services to http
        if (allAdminServicesHttp) {
            return true;
        }
        // in this if block we set the config
        if (isFirstCheck) {
            String httpAdminServices =
                    CoreComponent
                            .getServerConfigurationService()
                            .getFirstProperty(CarbonConstants.AXIS2_CONFIG_PARAM +
                                              "." +
                                              CarbonConstants.HTTP_ADMIN_SERVICES);
            // here we set the configs in memory
            if (httpAdminServices != null && !"".equals(httpAdminServices)) {
                if (httpAdminServices.equals("*")) {
                    allAdminServicesHttp = true;
                    isFirstCheck = false;
                    return true;
                }
                httpAdminServicesList = httpAdminServices.split(",");
                isFirstCheck = false;
            } else {
                return isFirstCheck = false;
            }
        }
        // no admin service will be exposed in http
        if (httpAdminServicesList == null) {
            return false;
        }
        // now look in the list
        for (String httpAdminService : httpAdminServicesList) {
            if (serviceName.equals(httpAdminService)) {
                return true; // this is a http admin service
            }
        }

        return false;
    }

    private boolean keepHistory(AxisService axisService) {
        Parameter keepHistoryParam = axisService.getParameter(CarbonConstants.KEEP_SERVICE_HISTORY_PARAM);
        if (keepHistoryParam == null) {
            return false;
        }
        Object value = keepHistoryParam.getValue();
        return (value instanceof String && Boolean.valueOf((String) value));
    }


}
