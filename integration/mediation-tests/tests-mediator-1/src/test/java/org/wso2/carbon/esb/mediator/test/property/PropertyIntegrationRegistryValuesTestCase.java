package org.wso2.carbon.esb.mediator.test.property;

import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import org.wso2.esb.integration.common.clients.registry.PropertiesAdminServiceClient;
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;
import javax.activation.DataHandler;
import javax.xml.namespace.QName;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class PropertyIntegrationRegistryValuesTestCase extends ESBIntegrationTest{

    @BeforeClass(alwaysRun = true)
    public void uploadSynapseConfig() throws Exception {
        super.init();
        uploadResourcesToRegistry();
        loadESBConfigurationFromClasspath(
                File.separator + "artifacts" + File.separator + "ESB" + File.separator + "synapseconfig" + File.separator + "propertyMediatorConfig" + File.separator + "registry_tests_config.xml");
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
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("conf_registry_test"), null, "WSO2");
        Assert.assertTrue(response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "name")).toString().contains("WSO2 Company"));

    }

    @Test(groups = "wso2.esb", description = "Set value from goverance registry (default scope)")
    public void testGovVal() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("gov_registry_test"), null, "WSO2");
        Assert.assertTrue(response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "name")).toString().contains("WSO2 Company"));

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