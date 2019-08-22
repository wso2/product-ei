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

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.data.xsd.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.IOException;

import static org.testng.Assert.assertTrue;

/**
 * This class tests the functionality of  xpath Prefix for Axis2 MessageContext properties
 */
public class PropertyIntegrationXpathAxis2PropertyTestCase extends ESBIntegrationTest {

    private static LogViewerClient logViewer;
    private Boolean isBody = false;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        init();
        loadESBConfigurationFromClasspath
                ("/artifacts/ESB/mediatorconfig/property/XPATHAXIS2.xml");
        logViewer = new LogViewerClient(context.getContextUrls().getBackEndUrl(), sessionCookie);

    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        super.cleanup();
    }

    @Test(groups = {"wso2.esb"}, description = "Test getting the property value at the axis2 scope")
    public void testRESPONSETEnabledTrue() throws IOException {

        int beforeLogSize = logViewer.getAllRemoteSystemLogs().length;

        axis2Client.sendSimpleStockQuoteRequest
                (getProxyServiceURLHttp("StockQuoteProxy") + "/test/prefix", null, "WSO2");

        LogEvent[] logs = logViewer.getAllRemoteSystemLogs();

        int afterLogSize = logs.length;

        for (int i = (afterLogSize - beforeLogSize); i >= 0; i--) {

            if (logs[i].getMessage().contains("stockprop = /test/prefix")) {
                isBody = true;
                break;
            }
        }

        assertTrue(isBody, "Message defined in axis2 scope Not found");
    }
}
