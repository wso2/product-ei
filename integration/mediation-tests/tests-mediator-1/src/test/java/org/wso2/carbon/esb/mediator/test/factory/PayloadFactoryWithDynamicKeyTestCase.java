package org.wso2.carbon.esb.mediator.test.factory;

import org.apache.axiom.om.OMElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.carbon.registry.resource.stub.ResourceAdminServiceExceptionException;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.net.URL;
import java.rmi.RemoteException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;


public class PayloadFactoryWithDynamicKeyTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        uploadResourcesToConfigRegistry();
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/payload/factory/payload_factory_dynamic_key.xml");
    }


    @Test(groups = "wso2.esb", description = "Payload Factory invocation with a key which its format can be saved as a local entry or registry resource")
    public void testInvokeAScriptWithDynamicKey() throws Exception {

        OMElement response = axis2Client.sendSimpleQuoteRequest(getMainSequenceURL(), null, "WSO2");

        assertEquals(response.getFirstElement().getText(), "IBM", "Fault value mismatched");
        assertNotEquals(response.getFirstElement().getText(), "WSO2", "Fault value mismatched");

        assertNotNull(response.getFirstChildWithName(new QName("http://services.samples/xsd", "Price")), "Fault response : doesn't contain element \'Price\'");
        assertNotNull(response.getFirstChildWithName(new QName("http://services.samples/xsd", "Code")), "Fault response : doesn't contain element \'Code\'");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        clearUploadedResource();
        super.cleanup();
    }


    private void uploadResourcesToConfigRegistry() throws Exception {

        ResourceAdminServiceClient resourceAdminServiceStub =
                new ResourceAdminServiceClient(contextUrls.getBackEndUrl(), context.getContextTenant().getContextUser().getUserName(),
                        context.getContextTenant().getContextUser().getPassword());

        resourceAdminServiceStub.addCollection("/_system/config/", "payloadFactory", "",
                "Contains test files for payload factory mediator");

        resourceAdminServiceStub.addResource(
                "/_system/config/payloadFactory/payload-in.xml", "application/xml", "payload format",
                new DataHandler(new URL("file://" + getESBResourceLocation() + File.separator +
                        "mediatorconfig/payload/factory/payload-in.xml")));


    }

    private void clearUploadedResource()
            throws InterruptedException, ResourceAdminServiceExceptionException, RemoteException, XPathExpressionException {

        ResourceAdminServiceClient resourceAdminServiceStub =
                new ResourceAdminServiceClient(contextUrls.getBackEndUrl(), context.getContextTenant().getContextUser().getUserName(),
                        context.getContextTenant().getContextUser().getPassword());

        resourceAdminServiceStub.deleteResource("/_system/config/payloadFactory");
        Thread.sleep(1000);
    }
}
