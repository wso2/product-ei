/*
*Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.contenttype.json;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.tcpmon.client.TCPMonListener;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * This class tests the behaviour of the clone mediator against a single json request cloning three equal
 * requests to the same endpoint
 */
public class JSONWithCloneMediatorTestCase extends ESBIntegrationTest {

    private TCPMonListener tcpMonListenerOne;
    private TCPMonListener tcpMonListenerTwo;
    private TCPMonListener tcpMonListenerThree;
    private Client client = Client.create();

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        super.init();

        loadESBConfigurationFromClasspath("/artifacts/ESB/jaxrs/jsonclonemediator.xml");

        // Initiating three tcpmon listener instances
        tcpMonListenerOne = new TCPMonListener(9005, context.getDefaultInstance().getHosts().
                get("default"), 8080);
        tcpMonListenerTwo = new TCPMonListener(9006, context.getDefaultInstance().getHosts().
                get("default"), 8080);
        tcpMonListenerThree = new TCPMonListener(9007, context.getDefaultInstance().getHosts().
                get("default"), 8080);

        tcpMonListenerOne.start();
        tcpMonListenerTwo.start();
        tcpMonListenerThree.start();
    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {

        client.destroy();
        super.cleanup();

        tcpMonListenerOne.stop();
        tcpMonListenerTwo.stop();
        tcpMonListenerThree.stop();
    }

    @Test(groups = {"wso2.esb"}, description = "Tests JSON requests behaviour with clone mediator")
    public void testJSONWithCloneMediatorTestScenario() throws Exception {

        WebResource webResource = client
                .resource(getProxyServiceURLHttp("JsonWithCloneMediatorProxy"));

        // Calling the GET request - Calling default Music album "Gold"
        ClientResponse getResponse = webResource.type("application/json")
                .get(ClientResponse.class);

        assertEquals(getResponse.getType().toString(), "application/json", "Content-Type Should be application/json");
        assertEquals(getResponse.getStatus(), 200, "Response status should be 200");


        // Analyzing tcp-mon inputs

        String esbOutGoingMsgTcpMonOne = tcpMonListenerOne.getConnectionData().get(1).
                getInputText().toString();

        assertNotNull(esbOutGoingMsgTcpMonOne, "Received Null response from the tcpMonListenerOne");
        assertTrue(esbOutGoingMsgTcpMonOne.contains("GET /rest/api/music/get?album=Gold"), "Error : TcpMonListenerOne " +
                "received - outgoing GET request ");
        assertTrue(esbOutGoingMsgTcpMonOne.contains("Content-Type: application/json"), "TcpMonListenerOne received - " +
                "outgoing Get request content-type mismatch");

        String esbOutGoingMsgTcpMonTwo = tcpMonListenerTwo.getConnectionData().get(1).
                getInputText().toString();

        assertNotNull(esbOutGoingMsgTcpMonTwo, "Received Null response from the tcpMonListenerTwo");
        assertTrue(esbOutGoingMsgTcpMonTwo.contains("GET /rest/api/music/get?album=Gold"), "Error : TcpMonListenerTwo " +
                "received - outgoing GET request ");
        assertTrue(esbOutGoingMsgTcpMonTwo.contains("Content-Type: application/json"), "TcpMonListenerTwo received - " +
                "outgoing Get request content-type mismatch");

        String esbOutGoingMsgTcpMonThree = tcpMonListenerThree.getConnectionData().get(1).
                getInputText().toString();

        assertNotNull(esbOutGoingMsgTcpMonThree, "Received Null response from the tcpMonListenerThree");
        assertTrue(esbOutGoingMsgTcpMonTwo.contains("GET /rest/api/music/get?album=Gold"), "Error : TcpMonListenerThree " +
                "received - outgoing GET request ");
        assertTrue(esbOutGoingMsgTcpMonTwo.contains("Content-Type: application/json"), "TcpMonListenerThree received - " +
                "outgoing Get request content-type mismatch");
    }
}
