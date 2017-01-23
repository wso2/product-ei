package org.wso2.carbon.esb.message.processor.test.forwarding;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;

import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import org.wso2.esb.integration.common.utils.servers.WireMonitorServer;
import static java.io.File.separator;


/**
 * This test case test a test related to Forwarding Message Processor and In-Memory Message Store
 */
public class WithInMemoryStoreTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        super.init();
        loadESBConfigurationFromClasspath(separator + "artifacts" + separator + "ESB" + separator
                                          + "synapseconfig" + separator + "processor" + separator +
                                          "forwarding" + separator + "InMemoryStoreSynapse1.xml");

    }

    /**
     * Create a message processor which processes messages that are in a In memory message store
     * Test artifact: /artifacts/ESB/synapseconfig/processor/forwarding/InMemoryStoreSynapse1.xml
     *
     * @throws Exception
     */
    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.ALL
})
    @Test(groups = "wso2.esb")
    public void testForwardingWithInMemoryStore() throws Exception {

        //Setting up Wire Monitor Server
        WireMonitorServer wireServer = new WireMonitorServer(9500);
        wireServer.start();

        try {
            axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "WSO2");
            Assert.fail("Unexpected reply received !!!");
        } catch (Exception e) {
            // Axis Fault Expected
        }

        String serverResponse = wireServer.getCapturedMessage();

        Assert.assertTrue(serverResponse.contains("WSO2"), "'WSO2 Company' String not found at backend port listener! ");
        Assert.assertTrue(serverResponse.contains("request"), "'getQuoteResponse' String not found at backend port listener !");


    }


    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }


}
