/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/
package org.wso2.carbon.esb.samples.test.mediation;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.tcpmon.client.TCPMonListener;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.clients.mediation.SynapseConfigAdminClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.servers.axis2.SampleAxis2Server;

import java.io.File;

/**
 * Sample 10: Introduction to Dynamic Endpoints with the Registry
 */
public class Sample10TestCase extends ESBIntegrationTest {

    private SampleAxis2Server axis2Server1;
    private SampleAxis2Server axis2Server2;

    private String oldSynapseConfig;
    private ServerConfigurationManager serverConfigurationManager;

    private TCPMonListener listener1;
    private TCPMonListener listener2;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();

        File sourceFile = new File(getESBResourceLocation() + File.separator +
                                   "samples" + File.separator + "synapse_sample_10.xml");

        SynapseConfigAdminClient synapseConfigAdminClient =
            new SynapseConfigAdminClient(contextUrls.getBackEndUrl(), getSessionCookie());
        oldSynapseConfig = synapseConfigAdminClient.getConfiguration();
        synapseConfigAdminClient.updateConfiguration(sourceFile);

        axis2Server1 = new SampleAxis2Server("test_axis2_server_9001.xml");
        axis2Server2 = new SampleAxis2Server("test_axis2_server_9002.xml");

        axis2Server1.deployService(SampleAxis2Server.SIMPLE_STOCK_QUOTE_SERVICE);
        axis2Server2.deployService(SampleAxis2Server.SIMPLE_STOCK_QUOTE_SERVICE_2);

        axis2Server1.start();
        axis2Server2.start();

        listener1 = new TCPMonListener(9100, "localhost", 9001);
        listener1.start();

        listener2 = new TCPMonListener(9200, "localhost", 9002);
        listener2.start();
    }

    @Test(groups = { "wso2.esb" }, description = "Dynamic Endpoints with the Registry" , enabled = false)
    public void testDynamicEndpoints() throws Exception {

        updateEndpointFile("1");
        // Have to wait 15000ms until the updated endpoint come to work
        do {
            Thread.sleep(3000);
            axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "WSO2");
        } while (listener1.getConnectionData().size() == 0 &&
                 listener2.getConnectionData().size() == 0);

        // 9001 called
        Assert.assertEquals(listener1.getConnectionData().size(), 1, "9001 should be called");

        // 9002 not called
        Assert.assertEquals(listener2.getConnectionData().size(), 0, "9002 should not be called");

        serverConfigurationManager.restoreToLastConfiguration(false);
        updateEndpointFile("2");
        // Have to wait 15000ms until the updated endpoint come to work

        do {
            listener1.clear();
            Thread.sleep(3000);
            axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "WSO2");
        } while (listener1.getConnectionData().size() == 1 &&
                 listener2.getConnectionData().size() == 0);

        // 9001 not called
        Assert.assertEquals(listener1.getConnectionData().size(), 0, "9001 should not be called");

        // 9002 called
        Assert.assertEquals(listener2.getConnectionData().size(), 1, "9002 should be called");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();

        SynapseConfigAdminClient synapseConfigAdminClient =
            new SynapseConfigAdminClient(contextUrls.getBackEndUrl(), getSessionCookie());
        synapseConfigAdminClient.updateConfiguration(oldSynapseConfig);

        serverConfigurationManager.restoreToLastConfiguration(false);

        axis2Server1.stop();
        axis2Server2.stop();

        listener1.stop();
        listener2.stop();
    }

    private void updateEndpointFile(String fileLocation) throws Exception {

        serverConfigurationManager = new ServerConfigurationManager(context);

        File sourceFile = new File(
            getESBResourceLocation() + File.separator + "sample_10" + File.separator +
            fileLocation + File.separator + "dynamic_endpt_1.xml"
        );
        File targetFile = new File(
            ServerConfigurationManager.getCarbonHome() + File.separator + "repository" +
            File.separator + "samples" + File.separator + "resources" + File.separator +
            "endpoint" + File.separator + "dynamic_endpt_1.xml"
        );

        serverConfigurationManager.applyConfiguration(sourceFile, targetFile, true, false);
    }
}
