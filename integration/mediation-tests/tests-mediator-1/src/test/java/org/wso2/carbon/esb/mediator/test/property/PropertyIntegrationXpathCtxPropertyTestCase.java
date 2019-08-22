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

package org.wso2.carbon.esb.mediator.test.property;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.data.xsd.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.axis2client.AxisServiceClient;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

import static org.testng.Assert.assertTrue;

public class PropertyIntegrationXpathCtxPropertyTestCase extends ESBIntegrationTest {

    private static LogViewerClient logViewer;
    private boolean isBody = false;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath
                ("/artifacts/ESB/mediatorconfig/property/XPATHCTX.xml");
        logViewer = new LogViewerClient(context.getContextUrls().getBackEndUrl(), sessionCookie);

    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        super.cleanup();
    }

    @Test(groups = {"wso2.esb"}, description = "Endpoint is a non-existent endpoint reference key , " +
                                               "$ctx:ERROR_MESSAGE scenario", enabled = false)
    public void testRESPONSETEnabledTrue() throws IOException, XMLStreamException {

        int beforeLogSize = logViewer.getAllRemoteSystemLogs().length;

        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement putRequest = AXIOMUtil.stringToOM("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
                                                    " xmlns:ser=\"http://services.samples\"\n" +
                                                    " xmlns:xsd=\"http://services.samples/xsd\">\n" +
                                                    "   <soapenv:Header/>\n" +
                                                    "   <soapenv:Body>\n" +
                                                    "      <ser:getQuote>\n" +
                                                    "         <!--Optional:-->\n" +
                                                    "         <ser:request>\n" +
                                                    "            <!--Optional:-->\n" +
                                                    "            <xsd:symbol>IBM</xsd:symbol>\n" +
                                                    "         </ser:request>\n" +
                                                    "      </ser:getQuote>\n" +
                                                    "   </soapenv:Body>\n" +
                                                    "</soapenv:Envelope>");


        axisServiceClient.fireAndForget(putRequest, getProxyServiceURLHttp("StockQuoteProxy"), "getQuote");

        LogEvent[] logs = logViewer.getAllRemoteSystemLogs();
        int afterLogSize = logs.length;

        for (int i = (afterLogSize - beforeLogSize); i >= 0; i--) {
            String msg = "stockerrorprop = Couldn't find the endpoint with the key : ep2";
            if (logs[i].getMessage().contains(msg)) {  // first - endpoint deployment
                isBody = true;
                break;
            }
        }

        assertTrue(isBody, "Message Not found");

    }
}
