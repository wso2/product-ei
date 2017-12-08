package org.wso2.carbon.esb.tcp.transport.test;

import org.apache.commons.lang.StringUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.esb.tcp.transport.test.util.NativeTCPClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

/**
 * Test TCP session persistance splitting by a string.
 */
public class TCPSessionPersistenceSplitByStringTestCase extends ESBIntegrationTest {

    private static String message = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap"
            + ".org/soap/envelope/\"><soapenv:Header/><soapenv:Body/></soapenv:Envelope>";

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/tcp/transport/tcpProxy_splitByString.xml");
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.esb", description = "Tcp proxy service which configured to split by string")
    public void tcpTransportSplitByStringProxy() throws Exception {
        int messageCount = 3;
        NativeTCPClient tcpClient = new NativeTCPClient(NativeTCPClient.DelimiterTypeEnum.STRING.getDelimiterType(), messageCount);
        tcpClient.setMessage(message);
        tcpClient.setStringDelimiter("split");
        tcpClient.sendToServer();
        String[] responses = tcpClient.receiveStringTypeDelimiterResonse();
        Assert.assertEquals(messageCount, responses.length);
        for(String response: responses) {
            Assert.assertNotNull(response);
            Assert.assertNotEquals(StringUtils.EMPTY, response);
        }
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}