package org.wso2.carbon.esb.mediator.test.callOut;

import org.apache.axiom.om.*;
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
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.esb.integration.common.utils.servers.axis2.SampleAxis2Server;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import java.io.*;

public class CallOutMediatorWithMTOMTestCase extends ESBIntegrationTest {
    private final String MTOM_SERVICE = "MTOMSwASampleService";
    private SampleAxis2Server axis2Server;
    private String relativeFilePath = "/artifacts/ESB/mediatorconfig/callout/CallOutMediatorWithMTOMTest.xml";

        @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();

//        if (FrameworkFactory.getFrameworkProperties(
//                ProductConstant.ESB_SERVER_NAME).getEnvironmentSettings().is_builderEnabled()) {
            axis2Server = new SampleAxis2Server("test_axis2_server_9001.xml");
            axis2Server.start();
            axis2Server.deployService(MTOM_SERVICE);
            loadESBConfigurationFromClasspath(relativeFilePath);

//        } else {
//            builder = new EnvironmentBuilder().as(ProductConstant.ADMIN_USER_ID);
//            appServer = builder.build().getAs();
//            int deploymentDelay = builder.getFrameworkSettings().getEnvironmentVariables().getDeploymentDelay();
//            String serviceFilePath = ProductConstant.getResourceLocations(ProductConstant.AXIS2_SERVER_NAME)
//                                     + File.separator + "aar" + File.separator + MTOM_SERVICE + ".aar";
//            ServiceDeploymentUtil deployer = new ServiceDeploymentUtil();
//            deployer.deployArrService(appServer.getBackEndUrl(), appServer.getSessionCookie()
//                    , MTOM_SERVICE, serviceFilePath, deploymentDelay);
//            updateESBConfiguration(replaceEndpoints(relativeFilePath, MTOM_SERVICE, "9001"));
//        }

    }

    @Test(groups = {"wso2.esb"}, description = "callOutMediatorWithMTOMTest")
    public void callOutMediatorWithMTOMTest() throws IOException {
        String targetEPR = getMainSequenceURL();
        String fileName = FrameworkPathUtil.getSystemResourceLocation() +
                          "artifacts" + File.separator + "ESB"
                          + File.separator + "mtom" + File.separator + "asf-logo.gif";
        sendUsingMTOM(fileName, targetEPR);
    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        if (axis2Server != null && axis2Server.isStarted()) {
            axis2Server.stop();

//        } else {
//            ServiceDeploymentUtil deployer = new ServiceDeploymentUtil();
//            deployer.unDeployArrService(appServer.getBackEndUrl(), appServer.getSessionCookie()
//                    , MTOM_SERVICE, 30000);
       }
        super.cleanup();
    }

    public void sendUsingMTOM(String fileName, String targetEPR) throws IOException {
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
        Assert.assertTrue(response.toString().contains("<m:testMTOM xmlns:m=\"http://services.samples/xsd\">" +
                                                       "<m:test1>testMTOM</m:test1></m:testMTOM>"));
    }
}
