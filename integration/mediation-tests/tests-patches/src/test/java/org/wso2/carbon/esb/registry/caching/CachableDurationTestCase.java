package org.wso2.carbon.esb.registry.caching;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.ReporterConfig;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.registry.properties.stub.utils.xsd.Property;
//import org.wso2.carbon.logging.view.stub.LogViewerLogViewerException;
import org.wso2.esb.integration.common.clients.registry.PropertiesAdminServiceClient;
import org.wso2.carbon.logging.view.data.xsd.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.Utils;

import java.rmi.RemoteException;
import java.util.Properties;

/**
 * ESBJAVA-3267
 * When cachableDuration is 0 or not specified in the tag, the resources are cached forever.
 * This needs to be corrected as "no caching" if the entry is 0.
 */

public class CachableDurationTestCase extends ESBIntegrationTest {

    Log logger = LogFactory.getLog(CachableDurationTestCase.class);

    private PropertiesAdminServiceClient propertyPropertiesAdminServiceClient;

    private LogViewerClient cli;

    private static final String NAME = "cache_test";
    private static final String PATH = "/_system/config";
    private static final String OLD_VALUE = "123456789";
    private static final String NEW_VALUE = "987654321";
    private static final String ADD_URL = "http://localhost:9000/services/SimpleStockQuoteService";

    private String trpUrl;

    @BeforeClass(alwaysRun = true)
    protected void init() throws Exception {

        super.init();

        trpUrl = contextUrls.getServiceUrl();

        propertyPropertiesAdminServiceClient = new PropertiesAdminServiceClient(contextUrls.getBackEndUrl(),getSessionCookie());

        cli = new LogViewerClient(contextUrls.getBackEndUrl(),getSessionCookie());

        uploadResourcesToConfigRegistry();
        loadESBConfigurationFromClasspath( "artifacts/ESB/synapseconfig/registry/caching/synapse.xml");
    }

    @Test(groups = "wso2.esb", description = "ESBRegistry cachableDuration 0 property test")
    public void testCachableDuration() throws Exception {

        //invoking the service
        SendRequest(ADD_URL, trpUrl);

        //Check if the property we set is used
        boolean validLogMessage = validateLogMessage(OLD_VALUE);
        Assert.assertTrue(validLogMessage);

        //Update the registry value
        updateResourcesInConfigRegistry();

        for (Property registryResourceProperty : propertyPropertiesAdminServiceClient.getProperty(PATH, NAME).
                getProperties()) {
            if (registryResourceProperty.getKey().equals(NAME)) {
                Assert.assertTrue(registryResourceProperty.getValue().equals(NEW_VALUE));
            }
        }

        SendRequest(ADD_URL, trpUrl);

        //Check if the new value is being used
        boolean validChangedLogMessage = validateLogMessage(NEW_VALUE);

        Assert.assertTrue(validChangedLogMessage);

    }


    private boolean validateLogMessage(String value) throws RemoteException, InterruptedException {

        LogEvent[] logs = cli.getAllRemoteSystemLogs();
        Assert.assertNotNull(logs, "No logs found");
        Assert.assertTrue(logs.length > 0, "No logs found");
        return Utils.checkForLog(cli, value, 2);
    }

    private void SendRequest(String addurl, String trpurl) {
        try{
            cli.clearLogs();
            axis2Client.sendSimpleStockQuoteRequest(trpurl, addurl ,"IBM");
        }catch (Exception e){
            logger.debug(e.getMessage());
        }
    }

    private void uploadResourcesToConfigRegistry() throws Exception {
        propertyPropertiesAdminServiceClient.setProperty(PATH, NAME, OLD_VALUE);
    }

    private void updateResourcesInConfigRegistry() throws Exception {

        try{

            Thread.sleep(5000);

            propertyPropertiesAdminServiceClient.removeProperty(PATH, NAME);

            Thread.sleep(5000);

            propertyPropertiesAdminServiceClient.setProperty(PATH, NAME, NEW_VALUE);

            Thread.sleep(5000);

        }catch (Exception e){
            logger.error("Error while updating the registry property",e);
        }
    }

    @AfterClass(alwaysRun = true)
    public void unDeployService() throws Exception {
     /* un deploying deployed artifact */
        propertyPropertiesAdminServiceClient.removeProperty(PATH, NAME);
        super.cleanup();
    }

}
