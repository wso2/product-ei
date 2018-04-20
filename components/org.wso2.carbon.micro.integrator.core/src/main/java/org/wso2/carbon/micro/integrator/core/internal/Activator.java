package org.wso2.carbon.micro.integrator.core.internal;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.wso2.carbon.base.api.ServerConfigurationService;

public class Activator implements BundleActivator {

    private static Log log = LogFactory.getLog(Activator.class);

    private static boolean serverStarted;



    protected String serverName;

    private String carbonHome;
    private ServerConfigurationService serverConfig;
    private Thread shutdownHook;
    private ServiceRegistration registration;

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        log.info("Activated***************************");
        try {
            log.info("Registry***************************");


        } catch (Throwable e) {
            log.info("Error***************************");
        }
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        registration.unregister();
        log.info("De-activated ***************************");
    }

}
