package org.wso2.carbon.esb.mediator.test.property;

import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.esb.integration.common.clients.registry.PropertiesAdminServiceClient;
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;
import javax.activation.DataHandler;
import javax.xml.namespace.QName;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class PropertyIntegrationRegistryValuesTestCase extends ESBIntegrationTest{

    private static LogViewerClient logViewer;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        uploadResourcesToRegistry();
        logViewer = new LogViewerClient(context.getContextUrls().getBackEndUrl(), sessionCookie);
    }

    public boolean isPropertySet(String proxy, String matchStr) throws Exception{
        boolean isSet = false;
        int beforeLogSize = logViewer.getAllSystemLogs().length;
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp(proxy), null, "Random Symbol");
        LogEvent[] logs = logViewer.getAllSystemLogs();
        int afterLogSize = logs.length;
        for (int i = (afterLogSize - beforeLogSize); i >= 0; i--) {
            if (logs[i].getMessage().contains(matchStr)) {
                isSet = true;
                break;
            }
        }
        return isSet;
    }

    private void uploadResourcesToRegistry() throws Exception {
        ResourceAdminServiceClient resourceAdminServiceClient =
                new ResourceAdminServiceClient(contextUrls.getBackEndUrl(), getSessionCookie());
        PropertiesAdminServiceClient propertiesAdminServiceClient =
                new PropertiesAdminServiceClient(contextUrls.getBackEndUrl(), getSessionCookie());

        resourceAdminServiceClient.deleteResource("/_system/config/custom");
        resourceAdminServiceClient.deleteResource("/_system/governance/custom");

        resourceAdminServiceClient.addCollection("/_system/config/custom", "test", "",
                "Contains property mediator test resources");

        resourceAdminServiceClient.addCollection("/_system/governance/custom", "test", "",
                "Contains property mediator test resources");

        resourceAdminServiceClient.addResource(
                "/_system/config/custom/test/property_mediator_test.txt", "text/plain", "text files",
                new DataHandler("Property mediator test resources".getBytes(), "application/text"));
        propertiesAdminServiceClient.setProperty("/_system/config/custom/test/property_mediator_test.txt",
                "resourceName", "Config Reg Test String");

        resourceAdminServiceClient.addResource(
                "/_system/governance/custom/test/property_mediator_test.txt", "text/plain", "text files",
                new DataHandler("Property mediator test resources".getBytes(), "application/text"));
        propertiesAdminServiceClient.setProperty("/_system/governance/custom/test/property_mediator_test.txt",
                "resourceName", "Gov Reg Test String");

        Thread.sleep(1000);
    }

    @Test(groups = "wso2.esb", description = "Set value from config registry (default scope)")
    public void testConfVal() throws Exception {
        assertTrue(isPropertySet("propertyConfRegistryTestProxy", "symbol = Config Reg Test String"), "Property Not Set!");
    }

    @Test(groups = "wso2.esb", description = "Set value from goverance registry (default scope)")
    public void testGovVal() throws Exception {
        assertTrue(isPropertySet("propertyGovRegistryTestProxy", "symbol = Gov Reg Test String"), "Property Not Set!");
    }

    private void clearRegistry() throws Exception {
        ResourceAdminServiceClient resourceAdminServiceClient =
                new ResourceAdminServiceClient(contextUrls.getBackEndUrl(), getSessionCookie());

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