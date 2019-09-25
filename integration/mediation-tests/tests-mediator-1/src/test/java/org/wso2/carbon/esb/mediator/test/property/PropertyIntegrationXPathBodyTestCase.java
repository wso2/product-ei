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
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.data.xsd.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import static org.testng.Assert.assertTrue;


public class PropertyIntegrationXPathBodyTestCase extends ESBIntegrationTest {

    private static LogViewerClient logViewer;
    boolean isBody = false;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath
                ("/artifacts/ESB/mediatorconfig/property/Synapse_XPath_ Variables_Body.xml");
        logViewer = new LogViewerClient(context.getContextUrls().getBackEndUrl(), sessionCookie);

    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        super.cleanup();
    }

    @Test(groups = "wso2.esb", description = "Tests when connections exist")
    public void testXpathBodyProperty() throws Exception {

        OMElement response = axis2Client.sendSimpleStockQuoteRequest
                (getProxyServiceURLHttp("StockQuoteProxy"), null, "WSO2");
        assertTrue(response.toString().contains("GetQuoteResponse"));
        assertTrue(response.toString().contains("WSO2 Company"));


        int beforeLogSize = logViewer.getAllRemoteSystemLogs().length;
        axis2Client.sendSimpleStockQuoteRequest
                (getProxyServiceURLHttp("StockQuoteProxy"), null, "WSO2");


        LogEvent[] logs = logViewer.getAllRemoteSystemLogs();
        int afterLogSize = logs.length;

        String msg = "stockprop = <ns:getQuote xmlns:ns=\"http://services.samples\">" +
                     "<ns:request><ns:symbol>WSO2</ns:symbol></ns:request></ns:getQuote>";

        for (int i = (afterLogSize - beforeLogSize); i >= 0; i--) {
            if (logs[i].getMessage().contains(msg)) {
                isBody = true;
                break;
            }
        }

        assertTrue(isBody, "Response does not contain first getQuote element in the SOAP body");
    }

}
