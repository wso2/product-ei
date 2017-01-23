package org.wso2.carbon.esb.message.processor.test.forwarding;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.esb.integration.common.utils.servers.axis2.SampleAxis2Server;
import org.wso2.carbon.automation.test.utils.axis2client.AxisServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;


public class ESBJAVA2006RetryOnSOAPFaultTestCase extends ESBIntegrationTest {

    private SampleAxis2Server axis2Server;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        axis2Server = new SampleAxis2Server("test_axis2_server_9003.xml");
        axis2Server.deployService("RetryOnSoapFault");
        axis2Server.start();

    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.esb", description = "<property name=\"RETRY_ON_SOAPFAULT\" value=\"false\"/>")
    public void testRetryOnSOAPFaultWithInOutFalse() throws Exception {

        loadESBConfigurationFromClasspath(File.separator + "artifacts" + File.separator + "ESB" + File.separator
                                          + "synapseconfig" + File.separator + "processor" + File.separator +
                                          "forwarding" + File.separator + "Retry_On_SOAPFault_In_Out.xml");

        AxisServiceClient serviceClient = new AxisServiceClient();
        serviceClient.fireAndForget(clearCountRequest(), getBackEndServiceUrl(), "clearRequestCount");
        OMElement requestCount = serviceClient.sendReceive(getCountRequest(), getBackEndServiceUrl(), "getRequestCount");
        Assert.assertEquals(requestCount.getFirstElement().getText(), "0", "Request Cunt not clear");

        serviceClient.sendRobust(getThrowAxisFaultRequest(), getMainSequenceURL(), "urn:throwAxisFault");
        Thread.sleep(5000);
        requestCount = serviceClient.sendReceive(getCountRequest(), getBackEndServiceUrl(), "getRequestCount");
        Assert.assertEquals(requestCount.getFirstElement().getText(), "1", "Request Count mismatched. Sent more than one request");

    }

    //@SetEnvironment(executionEnvironments = {ExecutionEnvironment.integration_all})
    //@Test(groups = "wso2.esb", description = "<property name=\"RETRY_ON_SOAPFAULT\" value=\"true\"/>")
    public void testRetryOnSOAPFaultWithInOutTrue() throws Exception {

        loadESBConfigurationFromClasspath(File.separator + "artifacts" + File.separator + "ESB" + File.separator
                                          + "synapseconfig" + File.separator + "processor" + File.separator +
                                          "forwarding" + File.separator + "Retry_On_SOAPFault_true_In_Out.xml");

        AxisServiceClient serviceClient = new AxisServiceClient();
        serviceClient.fireAndForget(clearCountRequest(), getBackEndServiceUrl(), "clearRequestCount");
        OMElement requestCount = serviceClient.sendReceive(getCountRequest(), getBackEndServiceUrl(), "getRequestCount");
        Assert.assertEquals(requestCount.getFirstElement().getText(), "0", "Request Cunt not clear");

        serviceClient.sendRobust(getThrowAxisFaultRequest(), getMainSequenceURL(), "throwAxisFault");
        Thread.sleep(5000);
        requestCount = serviceClient.sendReceive(getCountRequest(), getBackEndServiceUrl(), "getRequestCount");
        Assert.assertEquals(requestCount.getFirstElement().getText(), "5", "Request Count mismatched. Not sent all request");

    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        try {
            super.cleanup();
        } finally {
            axis2Server.stop();
        }


    }

    private OMElement getCountRequest() {

        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMNamespace ns = factory.createOMNamespace("http://processor.message.mediator.carbon.wso2.org", "proc");
        OMElement getCount = factory.createOMElement("getRequestCount", ns);
        return getCount;

    }

    private OMElement clearCountRequest() {

        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMNamespace ns = factory.createOMNamespace("http://processor.message.mediator.carbon.wso2.org", "proc");
        OMElement getCount = factory.createOMElement("clearRequestCount", ns);
        return getCount;

    }

    public OMElement getThrowAxisFaultRequest() {
        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMNamespace ns = factory.createOMNamespace("http://processor.message.mediator.carbon.wso2.org", "proc");
        OMElement throwFault = factory.createOMElement("throwAxisFault", ns);
        OMElement str = factory.createOMElement("s", ns);
        str.setText("Throw_Fault");
        throwFault.addChild(str);

        return throwFault;
    }

    private String getBackEndServiceUrl() {
        return "http://localhost:9003/services/SOAPFaultSample";
    }
}
