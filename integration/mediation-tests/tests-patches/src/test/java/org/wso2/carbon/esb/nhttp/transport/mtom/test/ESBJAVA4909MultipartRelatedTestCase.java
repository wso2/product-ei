package org.wso2.carbon.esb.nhttp.transport.mtom.test;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMText;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.servers.axis2.SampleAxis2Server;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import java.io.File;
import java.io.IOException;

/**
 * Test case check whether attachment is received by the client
 */
public class ESBJAVA4909MultipartRelatedTestCase extends ESBIntegrationTest {
    private ServerConfigurationManager serverManager;
    private final String MTOM_SERVICE = "MTOMSwASampleService";
    private SampleAxis2Server axis2Server;
    private String relativeFilePath = "/artifacts/ESB/nhttp/transport/mtom/ESBJAVA4909MultipartRelatedTest.xml";

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        serverManager = new ServerConfigurationManager(new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN));
        serverManager.applyConfiguration(new File(getClass().getResource
                ("/artifacts/ESB/nhttp/transport/mtom/axis2.xml").getPath()));
        super.init();
        axis2Server = new SampleAxis2Server("test_axis2_server_9001.xml");
        axis2Server.start();
        axis2Server.deployService(MTOM_SERVICE);
        loadESBConfigurationFromClasspath(relativeFilePath);
    }

    @Test(groups = {"wso2.esb"}, description = "ESBJAVA4909MultipartTest")
    public void callOutMediatorWithMTOMTest() throws IOException {
        String targetEPR = getProxyServiceURLHttp("MTOMChecker");
        String fileName = FrameworkPathUtil.getSystemResourceLocation() +
                "artifacts" + File.separator + "ESB"
                + File.separator + "mtom" + File.separator + "content.xml";
        sendUsingMTOM(fileName, targetEPR);
    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {

        try {
            if (axis2Server != null && axis2Server.isStarted()) {
                axis2Server.stop();
            }
            super.cleanup();
        } finally {
            Thread.sleep(3000);
            serverManager.restoreToLastConfiguration();
            serverManager = null;
        }
    }

    public void sendUsingMTOM(String fileName, String targetEPR) throws IOException {
        final String EXPECTED = "<m0:uploadFileUsingMTOMResponse xmlns:m0=\"http://services.samples\"><m0:response>" +
                "<m0:image>PHByb3h5PkFCQzwvcHJveHk+</m0:image></m0:response></m0:uploadFileUsingMTOMResponse>";
        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMNamespace ns = factory.createOMNamespace("http://services.samples", "m0");
        OMElement payload = factory.createOMElement("uploadFileUsingMTOM", ns);
        OMElement request = factory.createOMElement("request", ns);
        OMElement image = factory.createOMElement("image", ns);

        FileDataSource fileDataSource = new FileDataSource(new File(fileName));
        DataHandler dataHandler = new DataHandler(fileDataSource);
        OMText textData = factory.createOMText(dataHandler, true);
        image.addChild(textData);
        request.addChild(image);
        payload.addChild(request);

        ServiceClient serviceClient = new ServiceClient();
        Options options = new Options();
        options.setTo(new EndpointReference(targetEPR));
        options.setAction("urn:uploadFileUsingMTOM");
        options.setProperty(Constants.Configuration.ENABLE_MTOM, Constants.VALUE_TRUE);
        options.setCallTransportCleanup(true);
        serviceClient.setOptions(options);
        OMElement response = serviceClient.sendReceive(payload);
        Assert.assertTrue(response.toString().contains(EXPECTED), "Attachment is missing in the response");
    }
}
