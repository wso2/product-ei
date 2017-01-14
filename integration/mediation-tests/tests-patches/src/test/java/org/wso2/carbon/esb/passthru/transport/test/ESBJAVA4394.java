package org.wso2.carbon.esb.passthru.transport.test;

import org.apache.axis2.AxisFault;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.test.utils.tcpmon.client.TCPMonListener;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;

import static org.testng.Assert.assertTrue;

/**
 * Automation test for https://wso2.org/jira/browse/ESBJAVA-4394
 */
public class ESBJAVA4394 extends ESBIntegrationTest {

    private TCPMonListener tcpMonListener1;
    private TCPMonListener tcpMonListener2;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        /**
         * The configuration contains 'simpleStockPassthroug' proxy, which connects to port 5000
         * here two tcpmon listener are started in port 5000 target port 8000, in port 8000 target port 9001
         * the 2nd listeners target port is not the actual service port, because by making the 2nd tcpmon listener to
         * connect to a non existing port we could simulate the 101508 error.
         *
         * Without the fix of ESBJAVA-4394, when a 101508 happens the response is not send back
         * to the client. (client gets empty response)
         */
        init();
        loadESBConfigurationFromClasspath(
                "artifacts" + File.separator + "ESB" + File.separator + "passthru" + File.separator + "transport"
                        + File.separator + "ESBJAVA4394-config.xml");

        tcpMonListener1 = new TCPMonListener(9200, "localhost", 9300);
        tcpMonListener1.start();
        tcpMonListener2 = new TCPMonListener(9300, "localhost", 9001);
        tcpMonListener2.start();
    }

    @SetEnvironment(executionEnvironments = { ExecutionEnvironment.STANDALONE })
    @Test(groups = "wso2.esb",
          description = "Test Error response created via makefault is never sent to the client "
                  + "when the error connection timeout occurs by closing the TCP mon connection")
    public void testMakeFaultForConnectionTimeoutResponse() {
        try {
            axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("simpleStockPassthroug"), null, "WSO2");
        } catch (AxisFault axisFault) {
            /**
             * since we are making a soap fault in the configuration axis2 client receives axis fault.
             */
            String axisFaultMessage = axisFault.getMessage();
            assertTrue(axisFaultMessage.contains("101508"));
        }
    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        cleanup();
        tcpMonListener1.stop();
        tcpMonListener2.stop();
    }

}
