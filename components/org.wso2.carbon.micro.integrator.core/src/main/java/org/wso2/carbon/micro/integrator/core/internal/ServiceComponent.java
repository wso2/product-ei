/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.micro.integrator.core.internal;


import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.clustering.ClusteringConstants;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.description.TransportInDescription;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.axis2.engine.ListenerManager;
import org.apache.axis2.transport.base.threads.ThreadCleanupContainer;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.TreeBidiMap;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.adaptor.EclipseStarter;
import org.eclipse.equinox.http.helper.FilterServletAdaptor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.wso2.carbon.CarbonConstants;
import org.wso2.carbon.application.deployer.AppDeployerConstants;
import org.wso2.carbon.application.deployer.AppDeployerUtils;
import org.wso2.carbon.application.deployer.Feature;
import org.wso2.carbon.application.deployer.handler.AppDeploymentHandler;
import org.wso2.carbon.application.deployer.service.ApplicationManagerService;
import org.wso2.carbon.base.CarbonBaseConstants;
import org.wso2.carbon.base.api.ServerConfigurationService;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.core.CarbonAxisConfigurator;
import org.wso2.carbon.core.CarbonConfigurationContextFactory;
import org.wso2.carbon.core.CarbonThreadCleanup;
import org.wso2.carbon.core.CarbonThreadFactory;
import org.wso2.carbon.core.ServerInitializer;
import org.wso2.carbon.core.ServerManagement;
import org.wso2.carbon.core.ServerStartupObserver;
import org.wso2.carbon.core.ServerStatus;
import org.wso2.carbon.core.deployment.OSGiAxis2ServiceDeployer;
import org.wso2.carbon.core.init.PreAxis2ConfigItemListener;
import org.wso2.carbon.core.internal.DeploymentServerStartupObserver;
import org.wso2.carbon.core.internal.HTTPGetProcessorListener;
import org.wso2.carbon.core.multitenancy.GenericArtifactUnloader;
import org.wso2.carbon.core.transports.CarbonServlet;
import org.wso2.carbon.core.util.HouseKeepingTask;
import org.wso2.carbon.core.util.ParameterUtil;
import org.wso2.carbon.core.util.Utils;
import org.wso2.carbon.utils.Axis2ConfigItemHolder;
import org.wso2.carbon.utils.CarbonUtils;
import org.wso2.carbon.utils.ConfigurationContextService;
import org.wso2.carbon.utils.NetworkUtils;
import org.wso2.carbon.utils.ServerConstants;
import org.wso2.carbon.utils.ServerException;
import org.wso2.carbon.utils.deployment.Axis2ServiceRegistry;
import org.wso2.carbon.utils.deployment.GhostDeployerUtils;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

import javax.servlet.Filter;
import javax.servlet.ServletException;
import java.io.File;
import java.io.InputStream;
import java.lang.management.ManagementPermission;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.apache.axis2.transport.TransportListener.HOST_ADDRESS;

//import org.wso2.carbon.core.CarbonAxisConfigurator;

/**
 * @scr.component name="micro.server.dscomponent"" immediate="true"
 * @scr.reference name="server.configuration.service" interface="org.wso2.carbon.base.api.ServerConfigurationService"
 * cardinality="1..1" policy="dynamic"  bind="setServerConfigurationService" unbind="unsetServerConfigurationService"
 * @scr.reference name="http.service" interface="org.osgi.service.http.HttpService"
 * cardinality="1..1" policy="dynamic"  bind="setHttpService" unbind="unsetHttpService"
 * @scr.reference name="application.manager"
 * interface="org.wso2.carbon.application.deployer.service.ApplicationManagerService"
 * cardinality="0..1" policy="dynamic" bind="setAppManager" unbind="unsetAppManager"
 **/
public class ServiceComponent {

    private static Log log = LogFactory.getLog(ServiceComponent.class);

    private final Map<String, String> pendingItemMap = new ConcurrentHashMap<String, String>();

    private static BundleContext bundleContext;
    private PreAxis2ConfigItemListener configItemListener;
    private Timer timer = new Timer();

    private static final String CLIENT_REPOSITORY_LOCATION = "Axis2Config.ClientRepositoryLocation";
    private static final String CLIENT_AXIS2_XML_LOCATION = "Axis2Config.clientAxis2XmlLocation";

    private static final String SERVICE_PATH = "service-path";
    private static final String BUNDLE_CONTEXT_ROOT = "bundleContext-root";
    private static final String HOST_NAME = "host-name";

    private GenericArtifactUnloader genericArtifactUnloader = new GenericArtifactUnloader();
    private static final ScheduledExecutorService artifactsCleanupExec =
            Executors.newScheduledThreadPool(1, new CarbonThreadFactory(new ThreadGroup("ArtifactCleanupThread")));
    protected String serverName;

    private String carbonHome;
    private ServerConfigurationService serverConfig;
    private Thread shutdownHook;
    public boolean isEmbedEnv = false;

    public String serverWorkDir;

    public String axis2RepoLocation;

    private ConfigurationContext serverConfigContext;
    private ConfigurationContext clientConfigContext;

    private static ServiceRegistration appManagerRegistration;
    private static ApplicationManagerService applicationManager;
    private static Map<String, List<Feature>> requiredFeatures;
    private List<AppDeploymentHandler> appHandlers = new ArrayList<AppDeploymentHandler>();

    private List<String> requiredServices = new ArrayList<String>();

    /**
     * Indicates whether the shutdown of the server was triggered by the Carbon shutdown hook
     */
    private volatile boolean isShutdownTriggeredByShutdownHook = false;

    public static BundleContext getBundleContext() {
        return bundleContext;
    }

    protected void activate(ComponentContext ctxt) {
        try {
            // for new caching, every thread should has its own populated CC. During the deployment time we assume super tenant
            ctxt.getBundleContext().registerService(ServerStartupObserver.class.getName(),
                    new DeploymentServerStartupObserver(), null) ;
            bundleContext = ctxt.getBundleContext();
            ApplicationManager applicationManager = ApplicationManager.getInstance();
            applicationManager.init(); // this will allow application manager to register deployment handlers

            // register ApplicationManager as a service
            appManagerRegistration = ctxt.getBundleContext().registerService(
                    ApplicationManagerService.class.getName(), applicationManager, null);

            // read required-features.xml
            URL reqFeaturesResource = bundleContext.getBundle()
                    .getResource(AppDeployerConstants.REQ_FEATURES_XML);
            if (reqFeaturesResource != null) {
                InputStream xmlStream = reqFeaturesResource.openStream();
                requiredFeatures = AppDeployerUtils
                        .readRequiredFeaturs(new StAXOMBuilder(xmlStream).getDocumentElement());
            }
            CarbonCoreDataHolder.getInstance().setBundleContext(ctxt.getBundleContext());
            //Initializing ConfigItem Listener - Modules and Deployers
            configItemListener = new PreAxis2ConfigItemListener(bundleContext);
            initializeCarbon();
        } catch (Throwable e) {
            log.error("Failed to activate Carbon Core bundle ", e);
        }
    }

    protected void deactivate(ComponentContext ctxt) {
        try {
        } catch (Throwable e) {
            log.error("Failed clean up Carbon core", e);
        }

        try {
            if ("false".equals(serverConfigurationService.getFirstProperty("RequireCarbonServlet"))) {
                return;
            }
        } catch (Exception e) {
            log.debug("Error while retrieving serverConfiguration instance", e);
        }

        log.debug("Carbon Core bundle is deactivated ");
    }

    public static Map<String, List<Feature>> getRequiredFeatures() {
        return requiredFeatures;
    }

    private void initializeCarbon() {

        // Reset the SAAJ Interfaces
        System.getProperties().remove("javax.xml.soap.MessageFactory");
        System.getProperties().remove("javax.xml.soap.SOAPConnectionFactory");


        try {
            if (log.isDebugEnabled()) {
                log.debug("Starting Carbon initialization...");
            }

            ThreadCleanupContainer.addThreadCleanup(new CarbonThreadCleanup());

            // Location for expanding web content within AAR files
            String webLocation = System.getProperty(CarbonConstants.WEB_RESOURCE_LOCATION);
            if (webLocation == null) {
                webLocation = System.getProperty("carbon.home") + File.separator +
                        "repository" + File.separator + "deployment" +
                        File.separator + "server" +
                        File.separator + "webapps" +
                        File.separator + "wservices";
            }

            String temp = System.getProperty(ServerConstants.STANDALONE_MODE);
            if (temp != null) {
                temp = temp.trim();
                isEmbedEnv = temp.equals("true");
            } else {
                isEmbedEnv = false;
            }

            serverConfig = getServerConfigurationService();
            //Checking Carbon home
            carbonHome = System.getProperty(ServerConstants.CARBON_HOME);
            if (carbonHome == null) {
                String msg = ServerConstants.CARBON_HOME +
                        "System property has not been set.";
                log.fatal(msg);
                log.fatal(serverName + " startup failed.");
                throw new ServletException(msg);
            }

            try {
                System.setProperty(ServerConstants.LOCAL_IP_ADDRESS, NetworkUtils.getLocalHostname());
            } catch (SocketException ignored) {
            }
            /* we create the serverconfiguration in the carbon base. There we don't know the local.ip property.
            hence we are setting it manually here */
            //TODO: proper fix would be to move the networkUtil class to carbon.base level
            String serverURL = serverConfig.getFirstProperty(CarbonConstants.SERVER_URL);
            serverURL = Utils.replaceSystemProperty(serverURL);
            serverConfig.overrideConfigurationProperty(CarbonConstants.SERVER_URL,serverURL);
            serverName = serverConfig.getFirstProperty("Name");


            String hostName = serverConfig.getFirstProperty("ClusteringHostName");
            if (System.getProperty(ClusteringConstants.LOCAL_IP_ADDRESS) == null &&
                    hostName != null && hostName.trim().length() != 0) {
                System.setProperty(ClusteringConstants.LOCAL_IP_ADDRESS, hostName);
            }

            // Set the JGroups bind address for the use of the Caching Implementation based on
            // Infinispan.
            if (System.getProperty("bind.address") == null) {
                System.setProperty("bind.address",
                        (hostName != null && hostName.trim().length() != 0) ?
                                hostName.trim() : NetworkUtils.getLocalHostname());
            }

            serverWorkDir =
                    new File(serverConfig.getFirstProperty("WorkDirectory")).getAbsolutePath();
            System.setProperty("axis2.work.dir", serverWorkDir);

            setAxis2RepoLocation();

            Axis2ConfigItemHolder configItemHolder = new Axis2ConfigItemHolder();
            //TODO need to write deploy bundles
            configItemHolder.setDeployerBundles(configItemListener.getDeployerBundles());
            configItemHolder.setModuleBundles(configItemListener.getModuleBundles());

            String carbonContextRoot = serverConfig.getFirstProperty("WebContextRoot");

            CarbonAxisConfigurator carbonAxisConfigurator = new CarbonAxisConfigurator();
            carbonAxisConfigurator.setAxis2ConfigItemHolder(configItemHolder);
            carbonAxisConfigurator.setBundleContext(bundleContext);
            carbonAxisConfigurator.setCarbonContextRoot(carbonContextRoot);
            if (!carbonAxisConfigurator.isInitialized()) {
                carbonAxisConfigurator.init(axis2RepoLocation, webLocation);
            }

            //This is the point where we initialize Axis2.
            long start = System.currentTimeMillis();
            if (log.isDebugEnabled()) {
                log.debug("Creating super-tenant Axis2 ConfigurationContext");
            }
            serverConfigContext =
                    CarbonConfigurationContextFactory.
                            createNewConfigurationContext(carbonAxisConfigurator, bundleContext);
            long end = System.currentTimeMillis();
            if (log.isDebugEnabled()) {
                log.debug("Completed super-tenant Axis2 ConfigurationContext creation in " +
                        ((double) (end - start) / 1000) + " sec");
            }

            // Initialize ListenerManager
            ListenerManager listenerManager = serverConfigContext.getListenerManager();
            if (listenerManager == null) {
                listenerManager = new ListenerManager();
                listenerManager.init(serverConfigContext);
            }


            // At this point all the services and modules are deployed
            // Therefore it is time to allows other deployers to be registered.
            carbonAxisConfigurator.addAxis2ConfigServiceListener();

            initNetworkUtils(serverConfigContext.getAxisConfiguration());


            // Enabling http binding generation
            Parameter enableHttp = new Parameter("enableHTTP", "true");
            AxisConfiguration axisConfig = serverConfigContext.getAxisConfiguration();
            axisConfig.addParameter(enableHttp);

/*            new TransportPersistenceManager(axisConfig).
                    updateEnabledTransports(axisConfig.getTransportsIn().values(),
                            axisConfig.getTransportsOut().values());*/

            runInitializers();

            serverConfigContext.setProperty(Constants.CONTAINER_MANAGED, "true");
            serverConfigContext.setProperty(ServerConstants.WORK_DIR, serverWorkDir);
            // This is used inside the sever-admin component.
            serverConfigContext.setProperty(ServerConstants.CARBON_INSTANCE, this);

            //TODO As a tempory solution this part is added here. But when ui bundle are seperated from the core bundles
            //TODO this should be fixed.
            ServerConfigurationService config = getServerConfigurationService();
            String type = config.getFirstProperty("Security.TrustStore.Type");
            String password = config.getFirstProperty("Security.TrustStore.Password");
            String storeFile = new File(config.getFirstProperty("Security.TrustStore.Location")).getAbsolutePath();

            System.setProperty("javax.net.ssl.trustStore", storeFile);
            System.setProperty("javax.net.ssl.trustStoreType", type);
            System.setProperty("javax.net.ssl.trustStorePassword", password);

            addShutdownHook();
//            registerHouseKeepingTask(serverConfigContext);

            // Creating the Client side configuration context
            clientConfigContext = getClientConfigurationContext();

            //TOa house keeping taskDO add this map to a house keeping task
            //Adding FILE_RESOURCE_MAP
            Object property = new TreeBidiMap();
            clientConfigContext.setProperty(ServerConstants.FILE_RESOURCE_MAP, property);
            clientConfigContext.setContextRoot(carbonContextRoot);

            // If Carbon Kernel is running in the optimized mode, we do not deploy service resided in bundles.
            // Most of these services are either admin services or hidden services.
            if (!CarbonUtils.isOptimized()) {
                //Deploying Web service which resides in bundles
                Axis2ServiceRegistry serviceRegistry = new Axis2ServiceRegistry(serverConfigContext);
                serviceRegistry.register(bundleContext.getBundles());
                new OSGiAxis2ServiceDeployer(serverConfigContext, bundleContext).registerBundleListener(); // This will register the OSGi bundle listener
            }

            HttpService httpService = getHttpService();
            HttpContext defaultHttpContext = httpService.createDefaultHttpContext();

            registerCarbonServlet(httpService, defaultHttpContext);


            log.info("Repository       : " + axis2RepoLocation);

            // schedule the services cleanup task
            if (GhostDeployerUtils.isGhostOn()) {
                artifactsCleanupExec.scheduleAtFixedRate(genericArtifactUnloader,
                        CarbonConstants.SERVICE_CLEANUP_PERIOD_SECS,
                        CarbonConstants.SERVICE_CLEANUP_PERIOD_SECS, TimeUnit.SECONDS);
            }

            //Exposing metering.enabled system property. This is needed by the
            //tomcat.patch bundle to decide whether or not to publish bandwidth stat data
            String isMeteringEnabledStr = serverConfig.getFirstProperty("EnableMetering");
            if(isMeteringEnabledStr!=null){
                System.setProperty("metering.enabled", isMeteringEnabledStr);
            }else{
                System.setProperty("metering.enabled", "false");
            }

            //Registering the configuration contexts as an OSGi service.
            if (log.isDebugEnabled()) {
                log.debug("Registering ConfigurationContextService...");
            }
            bundleContext.registerService(ConfigurationContextService.class.getName(),
                    new ConfigurationContextService(serverConfigContext,
                            clientConfigContext),
                    null);

        } catch (Throwable e) {
            log.fatal("WSO2 Carbon initialization Failed", e);
        }
    }

    private void addShutdownHook() {
        if (shutdownHook != null) {
            return;
        }
        shutdownHook = new Thread() {
            public void run() {
                // During shutdown we assume it is triggered by super tenant
                PrivilegedCarbonContext privilegedCarbonContext = PrivilegedCarbonContext
                        .getThreadLocalCarbonContext();
                privilegedCarbonContext
                        .setTenantDomain(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
                privilegedCarbonContext.setTenantId(MultitenantConstants.SUPER_TENANT_ID);

                log.info("Shutdown hook triggered....");
                isShutdownTriggeredByShutdownHook = true;
                shutdownGracefully();
            }
        };
        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }

    private void createSuperTenantCarbonContext() {
        PrivilegedCarbonContext carbonContext = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        carbonContext.setTenantId(MultitenantConstants.SUPER_TENANT_ID);
        carbonContext.setTenantDomain(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
    }

    /**
     * Forced shutdown
     */
    public void shutdown() {
        createSuperTenantCarbonContext();
        SecurityManager secMan = System.getSecurityManager();
        if (secMan != null) {
            secMan.checkPermission(new ManagementPermission("control"));
        }
        log.info("Shutting down " + serverName + "...");
        if (!isShutdownTriggeredByShutdownHook) {
            Runtime.getRuntime().removeShutdownHook(shutdownHook);
        }
        try {
            try {
                ServerStatus.setServerShuttingDown();
            } catch (AxisFault e) {
                String msg = "Cannot set server to shutdown mode";
                log.error(msg, e);
            }
//            CarbonCoreServiceComponent.shutdown();
//            stopListenerManager();
            log.info("Shutting down OSGi framework...");
            EclipseStarter.shutdown();
            log.info("Shutdown complete");
            log.info("Halting JVM");
            if (!isShutdownTriggeredByShutdownHook) {
                System.exit(0);
            }
        } catch (Exception e) {
            log.error("Error occurred while shutting down " + serverName, e);
            if (!isShutdownTriggeredByShutdownHook) {
                System.exit(1);
            }
        }
    }

    /**
     * Graceful shutdown
     */
    public void shutdownGracefully() {
        createSuperTenantCarbonContext();
        try {
            ServerStatus.setServerShuttingDown();
        } catch (Exception e) {
            String msg = "Cannot set server to shutdown mode";
            log.error(msg, e);
        }
        try {
            log.info("Gracefully shutting down " + serverName + "...");
            Map<String, TransportInDescription> inTransports =
                    serverConfigContext.getAxisConfiguration().getTransportsIn();
            new ServerManagement(inTransports, serverConfigContext).startMaintenanceForShutDown();
        } catch (Exception e) {
            String msg = "Cannot put transports into maintenance mode";
            log.error(msg, e);
        }
        shutdown();
    }

    private ConfigurationContext getClientConfigurationContext() throws AxisFault {
        String clientRepositoryLocation =
                serverConfig.getFirstProperty(CLIENT_REPOSITORY_LOCATION);
        String clientAxis2XmlLocationn = serverConfig.getFirstProperty(CLIENT_AXIS2_XML_LOCATION);
        ConfigurationContext clientConfigContextToReturn =
                ConfigurationContextFactory.createConfigurationContextFromFileSystem(
                        clientRepositoryLocation, clientAxis2XmlLocationn);
        MultiThreadedHttpConnectionManager httpConnectionManager = new MultiThreadedHttpConnectionManager();
        HttpConnectionManagerParams params = new HttpConnectionManagerParams();

        // Set the default max connections per host
        int defaultMaxConnPerHost = 500;
        Parameter defaultMaxConnPerHostParam =
                clientConfigContextToReturn.getAxisConfiguration().getParameter("defaultMaxConnPerHost");
        if(defaultMaxConnPerHostParam != null){
            defaultMaxConnPerHost = Integer.parseInt((String)defaultMaxConnPerHostParam.getValue());
        }
        params.setDefaultMaxConnectionsPerHost(defaultMaxConnPerHost);

        // Set the max total connections
        int maxTotalConnections = 15000;
        Parameter maxTotalConnectionsParam =
                clientConfigContextToReturn.getAxisConfiguration().getParameter("maxTotalConnections");
        if(maxTotalConnectionsParam != null){
            maxTotalConnections = Integer.parseInt((String)maxTotalConnectionsParam.getValue());
        }
        params.setMaxTotalConnections(maxTotalConnections);

        params.setSoTimeout(600000);
        params.setConnectionTimeout(600000);

        httpConnectionManager.setParams(params);
        clientConfigContextToReturn.setProperty(HTTPConstants.MULTITHREAD_HTTP_CONNECTION_MANAGER,
                httpConnectionManager);
//        registerHouseKeepingTask(clientConfigContextToReturn);
        clientConfigContextToReturn.setProperty(ServerConstants.WORK_DIR, serverWorkDir);
        return clientConfigContextToReturn;
    }

    private void registerHouseKeepingTask(ConfigurationContext configurationContext) {
        if (Boolean.valueOf(serverConfig.getFirstProperty("HouseKeeping.AutoStart"))) {
            Timer houseKeepingTimer = new Timer();
            long houseKeepingInterval =
                    Long.parseLong(serverConfig.
                            getFirstProperty("HouseKeeping.Interval")) * 60 * 1000;
            Object property =
                    configurationContext.getProperty(ServerConstants.FILE_RESOURCE_MAP);
            if (property == null) {
                property = new TreeBidiMap();
                configurationContext.setProperty(ServerConstants.FILE_RESOURCE_MAP, property);
            }
            houseKeepingTimer.
                    scheduleAtFixedRate(new HouseKeepingTask(serverWorkDir, (BidiMap) property),
                            houseKeepingInterval,
                            houseKeepingInterval);
        }
    }

    private void runInitializers() throws ServerException {

        String[] initializers =
                serverConfig.getProperties("ServerInitializers.Initializer");
        for (String clazzName : initializers) {
            try {
                Class clazz = bundleContext.getBundle().loadClass(clazzName);
                ServerInitializer intializer = (ServerInitializer) clazz.newInstance();
                if (log.isDebugEnabled()) {
                    log.debug("Using ServerInitializer " + intializer.getClass().getName());
                }
                intializer.init(serverConfigContext);
            } catch (Exception e) {
                throw new ServerException(e);
            }
        }
    }
    private void setAxis2RepoLocation() {
        if (System.getProperty("axis2.repo") != null) {
            axis2RepoLocation = System.getProperty("axis2.repo");
            /* First see whether this is the -n scenario */
            if (CarbonUtils.isMultipleInstanceCase()) {
                /* Now check whether this is a ChildNode or not, if this is the
                   a ChildNode we do not deploy services to this */
                if (!CarbonUtils.isChildNode()) {
                    axis2RepoLocation = CarbonUtils.getCarbonHome();
                }
            }
            serverConfig.setConfigurationProperty(CarbonBaseConstants.AXIS2_CONFIG_REPO_LOCATION,
                    axis2RepoLocation);
        } else {
            axis2RepoLocation = serverConfig
                    .getFirstProperty(CarbonBaseConstants.AXIS2_CONFIG_REPO_LOCATION);
        }

        if (!axis2RepoLocation.endsWith("/")) {
            serverConfig.setConfigurationProperty(CarbonBaseConstants.AXIS2_CONFIG_REPO_LOCATION,
                    axis2RepoLocation + "/");
            axis2RepoLocation = axis2RepoLocation + "/";
        }
    }



    private void registerCarbonServlet(HttpService httpService, HttpContext defaultHttpContext)
            throws ServletException, NamespaceException, InvalidSyntaxException {
        if (!"false".equals(serverConfig.getFirstProperty("RequireCarbonServlet"))) {
            CarbonServlet carbonServlet = new CarbonServlet(serverConfigContext);
            String servicePath = "/services";
            String path = serverConfigContext.getServicePath();
            if (path != null) {
                servicePath = path.trim();
            }
            if (!servicePath.startsWith("/")) {
                servicePath = "/" + servicePath;
            }
            ServiceReference filterServiceReference = bundleContext.getServiceReference(Filter.class.getName());
            if (filterServiceReference != null) {
                Filter filter = (Filter) bundleContext.getService(filterServiceReference);
                httpService.registerServlet(servicePath, new FilterServletAdaptor(filter, null, carbonServlet), null, defaultHttpContext);
            } else {
                httpService.registerServlet(servicePath, carbonServlet, null, defaultHttpContext);
            }
            HTTPGetProcessorListener getProcessorListener =
                    new HTTPGetProcessorListener(carbonServlet, bundleContext);
            // Check whether there are any services that expose HTTPGetRequestProcessors
            ServiceReference[] getRequestProcessors =
                    bundleContext.getServiceReferences((String)null,
                            "(" + CarbonConstants.HTTP_GET_REQUEST_PROCESSOR_SERVICE + "=*)");

            // If there are any we need to register them explicitly
            if (getRequestProcessors != null) {
                for (ServiceReference getRequestProcessor : getRequestProcessors) {
                    getProcessorListener.addHTTPGetRequestProcessor(getRequestProcessor,
                            ServiceEvent.REGISTERED);
                }
            }

            // We also add a service listener to make sure we react to changes in the bundles that
            // expose HTTPGetRequestProcessors
            bundleContext.addServiceListener(getProcessorListener,
                    "(" + CarbonConstants.HTTP_GET_REQUEST_PROCESSOR_SERVICE + "=*)");
        }
    }

    private void initNetworkUtils(AxisConfiguration axisConfiguration)
            throws AxisFault, SocketException {
        String hostName = serverConfig.getFirstProperty("HostName");
        String mgtHostName = serverConfig.getFirstProperty("MgtHostName");
        if (hostName != null) {
            Parameter param = axisConfiguration.getParameter(HOST_ADDRESS);
            if (param != null) {
                param.setEditable(true);
                param.setValue(hostName);
            } else {
                param = ParameterUtil.createParameter(HOST_ADDRESS, hostName);
                axisConfiguration.addParameter(param);
            }
        } else {
            Parameter param = axisConfiguration.getParameter(HOST_ADDRESS);
            if (param != null) {
                hostName = (String) param.getValue();
                log.info(HOST_ADDRESS + " has been selected from Axis2.xml.");
            }
        }
        NetworkUtils.init(hostName, mgtHostName);
    }

    public static ServerConfigurationService getServerConfigurationService() {
        return serverConfigurationService;
    }

    public static HttpService getHttpService() {
        return httpService;
    }

    protected static ServerConfigurationService serverConfigurationService;

    protected static HttpService httpService;

    protected void setServerConfigurationService(ServerConfigurationService serverConfigurationService) {
        this.serverConfigurationService = serverConfigurationService;
        CarbonCoreDataHolder.getInstance().setServerConfigurationService(serverConfigurationService);
    }

    protected void unsetServerConfigurationService(ServerConfigurationService serverConfigurationService) {
        this.serverConfigurationService = null;
    }

    protected void setHttpService(HttpService httpService) {
        this.httpService = httpService;
        CarbonCoreDataHolder.getInstance().setHttpService(httpService);
    }

    protected void unsetHttpService(HttpService httpService) {
        this.httpService = null;
    }


    protected void setAppManager(ApplicationManagerService applicationManager) {
        this.applicationManager = applicationManager;
        CarbonCoreDataHolder.getInstance().setApplicationManager(applicationManager);
    }

    protected void unsetAppManager(ApplicationManagerService applicationManager) {
        this.applicationManager = null;
    }

}
