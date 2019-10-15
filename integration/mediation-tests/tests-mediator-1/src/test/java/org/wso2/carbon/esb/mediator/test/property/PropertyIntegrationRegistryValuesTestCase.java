package org.wso2.carbon.esb.mediator.test.property;

import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.data.xsd.LogEvent;
import org.wso2.esb.integration.common.clients.registry.PropertiesAdminServiceClient;
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;
import javax.activation.DataHandler;
import javax.xml.namespace.QName;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * This test case tests the setting of properties
 * from the registries
 */
public class PropertyIntegrationRegistryValuesTestCase extends ESBIntegrationTest {

    private static final String CONF_PATH = "/_system/config/custom";
    private static final String GOV_PATH = "/_system/governance/custom";

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        uploadResourcesToRegistry();
    }

    /**
     * This method uploads the resources to the registry
     * @throws Exception
     */
    private void uploadResourcesToRegistry() throws Exception {
        ResourceAdminServiceClient resourceAdminServiceClient = new ResourceAdminServiceClient(
                contextUrls.getBackEndUrl(), getSessionCookie());
        PropertiesAdminServiceClient propertiesAdminServiceClient = new PropertiesAdminServiceClient(
                contextUrls.getBackEndUrl(), getSessionCookie());

        resourceAdminServiceClient.deleteResource("/_system/config/custom");
        resourceAdminServiceClient.deleteResource("/_system/governance/custom");

        resourceAdminServiceClient
                .addCollection(CONF_PATH, "test", "", "Contains property mediator test resources");

        resourceAdminServiceClient
                .addCollection(GOV_PATH, "test", "", "Contains property mediator test resources");

        resourceAdminServiceClient
                .addResource(CONF_PATH+"/test/property_mediator_test.txt", "text/plain", "text files",
                        new DataHandler("Property mediator test resources".getBytes(), "application/text"));
        propertiesAdminServiceClient
                .setProperty(CONF_PATH+"/test/property_mediator_test.txt", "resourceName",
                        "Config Reg Test String");

        resourceAdminServiceClient
                .addResource(GOV_PATH+"/test/property_mediator_test.txt", "text/plain", "text files",
                        new DataHandler("Property mediator test resources".getBytes(), "application/text"));
        propertiesAdminServiceClient
                .setProperty(GOV_PATH+"/test/property_mediator_test.txt", "resourceName",
                        "Gov Reg Test String");

        Thread.sleep(1000);
    }

    @Test(groups = "wso2.esb",
          description = "Set value from config registry (default scope)")
    public void testConfVal() throws Exception {
        OMElement response = axis2Client
                .sendSimpleStockQuoteRequest(getProxyServiceURLHttp("propertyConfRegistryTestProxy"), null, "Random Symbol");
        assertTrue(response.toString().contains("Config Reg Test String"),
                "Property Not Set");
    }

    @Test(groups = "wso2.esb",
          description = "Set value from goverance registry (default scope)")
    public void testGovVal() throws Exception {
        OMElement response = axis2Client
                .sendSimpleStockQuoteRequest(getProxyServiceURLHttp("propertyGovRegistryTestProxy"), null, "Random Symbol");
        assertTrue(response.toString().contains("Gov Reg Test String"),
                "Property Not Set");
    }

    private void clearRegistry() throws Exception {
        ResourceAdminServiceClient resourceAdminServiceClient = new ResourceAdminServiceClient(
                contextUrls.getBackEndUrl(), getSessionCookie());

        resourceAdminServiceClient.deleteResource("/_system/config/custom/test");

        resourceAdminServiceClient.deleteResource("/_system/governance/custom/test");
    }

    @AfterClass(alwaysRun = true)
    private void destroy() throws Exception {
        try {
            clearRegistry();
        } finally {
            super.cleanup();
        }

    }
}