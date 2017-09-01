package org.wso2.carbon.esb.mediator.test.fault;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.axis2.AxisFault;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

public class ESBJAVA2615TestCase  extends ESBIntegrationTest {
    
    private static String faultMessage = "<faultcode xmlns:soap11Env=\"http://schemas.xmlsoap.org/soap/envelope/\">soap11Env:Server</faultcode><faultstring>Test Only to see if there are two envelopes.</faultstring></soapenv:Fault></soapenv:Body></soapenv:Envelope>";
    
    private ProtocolViolationServer server =  null;
    
    @BeforeClass(alwaysRun = true)
    public void uploadSynapseConfig() throws Exception {
        super.init();
        server = new ProtocolViolationServer();
        server.runServer();
        loadESBConfigurationFromClasspath("/artifacts/ESB/proxyconfig/proxy/protocolViolationProxy/synapse.xml");
    }
    
    @Test(groups = {"wso2.esb"}, description = "Creating Protocol Violation test",enabled=true)
    public void testSOAP11FaultActor() throws AxisFault {
        String messageBody = createRequest();
        String reposnce = httpClient(getProxyServiceURLHttp("HelloProxy"), messageBody);
        if (reposnce.contains(messageBody) && reposnce.contains(faultMessage)) {
            Assert.fail("client received two SOAP envelops");
        }

    }
    
    private String createRequest() {
        String message ="<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:typ=\"http://www.wso2.org/types\">"
                                 + "<soapenv:Header/><soapenv:Body>"
                                 + " <typ:greet>"
                                 + "<!--Optional:-->"
                                 + "<name>hello</name>"
                                 + "</typ:greet>"
                                 + "</soapenv:Body>" + "</soapenv:Envelope>";

        return message;
    }
    
    private String httpClient(String proxyLocation, String xml) {
        try {
            HttpClient httpclient = new HttpClient();
            PostMethod post = new PostMethod(proxyLocation);

            post.setRequestEntity(new StringRequestEntity(xml));
            post.setRequestHeader("Content-type", "text/xml; charset=ISO-8859-1");
            post.setRequestHeader("SOAPAction", "urn:mediate");
            httpclient.executeMethod(post);

            InputStream in = post.getResponseBodyAsStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            StringBuffer buffer = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
                buffer.append("\n");
            }
            reader.close();
            return buffer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    @AfterClass(alwaysRun = true)
    private void destroy() throws Exception {
        super.cleanup();
        server.stopServer();
        server = null;
    }

}
