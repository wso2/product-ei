/*
*Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import static org.testng.Assert.assertTrue;

/**
 * This test case tests whether the removing of properties
 * in the Axis2-client scope is working fine.
 */

public class propertyIntegrationAxis2ClientRemovePropertiesTestCase extends ESBIntegrationTest {

    private static LogViewerClient logViewer;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        logViewer = new LogViewerClient(context.getContextUrls().getBackEndUrl(), sessionCookie);
    }


    @Test(groups = "wso2.esb",
          description = "Remove action as \"value\" and type String (axis2-client scope)")
    public void testStringVal() throws Exception {
        logViewer.clearLogs();
        OMElement response = axis2Client
                .sendSimpleStockQuoteRequest(getProxyServiceURLHttp("propertyAxis2ClientRemoveTestProxy"), null, "Random Symbol");
        assertTrue(response.toString().contains("Property Set and Removed"),
                "Proxy Invocation Failed!");
        assertTrue(isMatchFound("symbol = TestValue"),
                "Integer Property Not Either Set or Removed in the Axis2 Client scope!!");
    }

    private boolean isMatchFound(String matchStr) throws Exception {
        boolean isSet = false;
        LogEvent[] logs = logViewer.getAllSystemLogs();
        int size = logs.length;
        for (int i = size-1; i >= 0; i--) {
            if (logs[i].getMessage().contains(matchStr)) {
                for (int j = i; j >= 0; j--) {
                    if (logs[j].getMessage().contains("symbol = null")) {
                        isSet = true;
                        break;
                    }
                }
                break;
            }
        }
        return isSet;
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        cleanup();
    }
}