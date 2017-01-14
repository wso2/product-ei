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
package org.wso2.carbon.esb.samples.test.transport;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.UDPClient;

import java.io.File;

/**
 * Sample 267: Switching from UDP to HTTP/S
 */
public class Sample267TestCase extends ESBIntegrationTest {

    private ServerConfigurationManager serverManager;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        AutomationContext context = new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN);
        serverManager = new ServerConfigurationManager(context);
        serverManager.applyConfiguration(new File(getESBResourceLocation() + File.separator +
                                                  "sample_267" + File.separator + "axis2.xml"));
        super.init();
        updateESBConfiguration(loadAndEditSample(267));
    }

    @SetEnvironment(executionEnvironments = { ExecutionEnvironment.STANDALONE })
    @Test(groups = { "wso2.esb" }, description = "Switching from UDP to HTTP/S")
    public void testUdpTransport() throws Exception {

        LogViewerClient logViewerClient =
                new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());

        logViewerClient.clearLogs();

        String message =
            " <soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
            "      <soapenv:Header xmlns:wsa=\"http://www.w3.org/2005/08/addressing\">\n" +
            "         <wsa:To>udp://localhost:9999?contentType=text/xml</wsa:To>\n" +
            "         <wsa:ReplyTo>\n" +
            "            <wsa:Address>http://www.w3.org/2005/08/addressing/none</wsa:Address>\n" +
            "         </wsa:ReplyTo>\n" +
            "         <wsa:MessageID>urn:uuid:956ecfca-cbc1-44e9-b7fc-ccf13d91cad4</wsa:MessageID>\n" +
            "         <wsa:Action>urn:placeOrder</wsa:Action>\n" +
            "      </soapenv:Header>\n" +
            "      <soapenv:Body>\n" +
            "         <m0:placeOrder xmlns:m0=\"http://services.samples\">\n" +
            "            <m0:order>\n" +
            "               <m0:price>154.13027523452166</m0:price>\n" +
            "               <m0:quantity>7015</m0:quantity>\n" +
            "               <m0:symbol>WSO2UDP</m0:symbol>\n" +
            "            </m0:order>\n" +
            "         </m0:placeOrder>\n" +
            "      </soapenv:Body>\n" +
            "   </soapenv:Envelope>";

        UDPClient client = new UDPClient("localhost", 9999);
        client.sendMessage(message);

        Thread.sleep(30000);

        LogEvent[] getLogsInfo = logViewerClient.getAllRemoteSystemLogs();
        boolean assertValue = false;
        for (LogEvent event : getLogsInfo) {
            if (event.getMessage().contains("WSO2UDP")) {
                assertValue = true;
                break;
            }
        }
        Assert.assertTrue(assertValue, "'WSO2UDP' not found");
    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        try {
            super.cleanup();
        } finally {
            Thread.sleep(3000);
            serverManager.restoreToLastConfiguration();
            serverManager = null;
        }

    }

    /**
     * This method is to update the original Sample 267 configuration Proxy Name to Sample267Proxy. When changing
     * transport to udp, it tries to apply it for all available proxy. If StockQuoteProxy is already exists, it gives
     * error in starting up with UDP transport and therefore sample configuration apply procedure failed in test cases.
     */
    private OMElement loadAndEditSample(int sampleNo) throws Exception {
        OMElement synapseConfig = loadSampleESBConfigurationWithoutApply(sampleNo);
        String updatedConfig = synapseConfig.toString().replace("StockQuoteProxy", "Sample267Proxy");
        synapseConfig = AXIOMUtil.stringToOM(updatedConfig);
        return synapseConfig;
    }
}
