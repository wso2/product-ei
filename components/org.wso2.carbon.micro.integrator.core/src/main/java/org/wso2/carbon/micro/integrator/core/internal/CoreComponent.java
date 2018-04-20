package org.wso2.carbon.micro.integrator.core.internal;

/**
 * Created by madhawa on 5/24/18.
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;
import org.wso2.carbon.base.api.ServerConfigurationService;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.core.ServerRestartHandler;
import org.wso2.carbon.core.ServerShutdownHandler;
import org.wso2.carbon.core.ServerStartupHandler;
import org.wso2.carbon.core.ServerStartupObserver;
import org.wso2.carbon.core.encryption.SymmetricEncryption;
import org.wso2.carbon.core.internal.CarbonCoreServiceComponent;
import org.wso2.carbon.core.internal.DeploymentServerStartupObserver;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * @scr.component name="micro.core.dscomponent"" immediate="true"
 * @scr.reference name="server.configuration.service" interface="org.wso2.carbon.base.api.ServerConfigurationService"
 * cardinality="1..1" policy="dynamic"  bind="setServerConfigurationService" unbind="unsetServerConfigurationService"
 * @scr.reference name="http.service" interface="org.osgi.service.http.HttpService"
 * cardinality="1..1" policy="dynamic"  bind="setHttpService" unbind="unsetHttpService"
 * @scr.reference name="serverShutdownHandler" interface="org.wso2.carbon.core.ServerShutdownHandler"
 * cardinality="0..n" policy="dynamic"  bind="addServerShutdownHandler" unbind="removeServerShutdownHandler"
 * @scr.reference name="serverRestartHandler" interface="org.wso2.carbon.core.ServerRestartHandler"
 * cardinality="0..n" policy="dynamic"  bind="addServerRestartHandler" unbind="removeServerRestartHandler"
 * @scr.reference name="serverStartupHandler" interface="org.wso2.carbon.core.ServerStartupHandler"
 * cardinality="0..n" policy="dynamic"  bind="addServerStartupHandler" unbind="removeServerStartupHandler"
 * @scr.reference name="serverStartupObserver" interface="org.wso2.carbon.core.ServerStartupObserver"
 * cardinality="0..n" policy="dynamic"  bind="addServerStartupObserver" unbind="removeServerStartupObserver"
 **/
public class CoreComponent {

    private static List<ServerShutdownHandler> shutdownHandlers = new ArrayList<ServerShutdownHandler>();

    private static List<ServerRestartHandler> restartHandlers = new ArrayList<ServerRestartHandler>();

    private static List<ServerStartupHandler> startupHandlers = new ArrayList<ServerStartupHandler>();

    private static List<ServerStartupObserver> serverStartupObservers = new ArrayList<ServerStartupObserver>();

    private static Log log = LogFactory.getLog(CarbonCoreServiceComponent.class);

    private static boolean serverStarted;

    protected void activate(ComponentContext ctxt) {
        try {
            // for new caching, every thread should has its own populated CC. During the deployment time we assume super tenant
            PrivilegedCarbonContext carbonContext = PrivilegedCarbonContext.getThreadLocalCarbonContext();
            carbonContext.setTenantDomain(org.wso2.carbon.base.MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
            carbonContext.setTenantId(org.wso2.carbon.base.MultitenantConstants.SUPER_TENANT_ID);
            ctxt.getBundleContext().registerService(ServerStartupObserver.class.getName(),
                    new DeploymentServerStartupObserver(), null) ;
            SymmetricEncryption encryption = SymmetricEncryption.getInstance();
            encryption.generateSymmetricKey();
            log.info("ffsdfsfsfsfsfsfsfsfsfsfsf");
        } catch (Throwable e) {
            log.error("Failed to activate Carbon Core bundle ", e);
        }
    }

    protected void deactivate(ComponentContext ctxt) {
        try {
            // We assume it's super tenant during component deactivate time
            PrivilegedCarbonContext privilegedCarbonContext = PrivilegedCarbonContext
                    .getThreadLocalCarbonContext();
            privilegedCarbonContext.setTenantDomain(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
            privilegedCarbonContext.setTenantId(MultitenantConstants.SUPER_TENANT_ID);

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
        serverStarted = false;
        log.debug("Carbon Core bundle is deactivated ");
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
    }

    protected void unsetServerConfigurationService(ServerConfigurationService serverConfigurationService) {
        this.serverConfigurationService = null;
    }

    protected void setHttpService(HttpService httpService) {
        this.httpService = httpService;
    }

    protected void unsetHttpService(HttpService httpService) {
        this.httpService = null;
    }


    public static void shutdown() {
        for (ServerShutdownHandler shutdownHandler : shutdownHandlers) {
            shutdownHandler.invoke();
        }
    }

    public static void restart() {
        for (ServerRestartHandler restartHandler : restartHandlers) {
            restartHandler.invoke();
        }
    }

    public static synchronized void startup() {
        for (ServerStartupHandler startupHandler : startupHandlers) {
            startupHandler.invoke();
        }
        startupHandlers.clear();
        serverStarted = true;
    }

    public static synchronized void notifyBefore() {
        for (ServerStartupObserver observer : serverStartupObservers) {
            observer.completingServerStartup();
        }
    }

    public static synchronized void notifyAfter(){
        for (ServerStartupObserver observer : serverStartupObservers) {
            observer.completedServerStartup();
        }
        serverStarted = true;
        startupHandlers.clear();

    }


    protected void addServerShutdownHandler(ServerShutdownHandler shutdownHandler) {
        shutdownHandlers.add(shutdownHandler);
    }

    protected void removeServerShutdownHandler(ServerShutdownHandler shutdownHandler) {
        shutdownHandlers.remove(shutdownHandler);
    }

    protected void addServerRestartHandler(ServerRestartHandler restartHandler) {
        restartHandlers.add(restartHandler);
    }

    protected void removeServerRestartHandler(ServerRestartHandler restartHandler) {
        restartHandlers.remove(restartHandler);
    }

    protected void addServerStartupHandler(ServerStartupHandler startupHandler) {
        synchronized (this.getClass()) {
            if (serverStarted) {
                startupHandler.invoke();
            } else {
                startupHandlers.add(startupHandler);
            }
        }
    }

    protected void removeServerStartupHandler(ServerStartupHandler startupHandler) {
        startupHandlers.remove(startupHandler);
    }

    protected void addServerStartupObserver(ServerStartupObserver startupObserver) {
        synchronized (this.getClass()) {
            if (serverStarted) {
                startupObserver.completedServerStartup();
            } else {
                serverStartupObservers.add(startupObserver);
            }
        }
    }

    protected void removeServerStartupObserver(ServerStartupObserver startupObserver) {
        serverStartupObservers.remove(startupObserver);
    }

}
