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

package org.wso2.carbon.esb.mediator.test.enrich;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.servers.WireMonitorServer;

import static org.testng.Assert.assertTrue;

/*Test for enrich mediator to add sibling to body of out message*/
public class EnrichIntegrationAddSiblingInOutMessageTestCase extends ESBIntegrationTest {

    public WireMonitorServer wireMonitorServer;
    @BeforeClass(alwaysRun = true)
    public void uploadSynapseConfig() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/enrich/enrich_add_sibling_in_out_msg.xml");
        wireMonitorServer = new WireMonitorServer(8991);

    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Enrich mediator:Add as a sibling to message body")
    public void addAsSiblingToMessageBody() throws Exception {
        wireMonitorServer.start();
        String payload = "<m:getQuote xmlns:m=\"http://services.samples\">" +
                         "<m:request>" +
                         "</m:request>" +
                         "</m:getQuote>";
        OMElement payloadOM = AXIOMUtil.stringToOM(payload);
        try {
            OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("enrichSample3")
                    , null, payloadOM);
        } catch (Exception e) {

        }
        String wireResponse = wireMonitorServer.getCapturedMessage();
        String expectedSoapBody = "<soapenv:Body>"+
                                  "<m:symbol1 xmlns:m=\"http://services.samples\">IBM</m:symbol1>"+
                                  "<m:symbol2 xmlns:m=\"http://services.samples\">WSO2</m:symbol2>"+
                                  "</soapenv:Body>" ;
        assertTrue(wireResponse.contains(expectedSoapBody),"Invalid soap body");


    }

    @AfterClass
    private void destroy() throws Exception {
        super.cleanup();
    }

}
