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
import org.apache.synapse.MessageContext;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.data.xsd.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.xml.namespace.QName;
import java.io.File;

import static org.testng.Assert.assertTrue;

/**
 * This test case tests whether the removing of properties
 * in the default scope is working fine.
 */
public class PropertyIntegrationDefaultScopeRemovePropertiesTestCase extends ESBIntegrationTest {

    private static LogViewerClient logViewer;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        logViewer = new LogViewerClient(context.getContextUrls().getBackEndUrl(), sessionCookie);
    }


    @Test(groups = "wso2.esb",
          description = "Remove action as \"value\" and type Integer (default scope)")
    public void testIntVal() throws Exception {
        logViewer.clearLogs();
        OMElement response = axis2Client
                .sendSimpleStockQuoteRequest(getProxyServiceURLHttp("propertyIntDefaultRemoveTestProxy"), null, "Random Symbol");
        assertTrue(response.toString().contains("Property Set and Removed"),
                "Proxy Invocation Failed!");
        assertTrue(isMatchFound("symbol = 123"),
                "Integer Property Not Either Set or Removed in the Axis2 scope!!");
    }

    @Test(groups = "wso2.esb",
          description = "Remove action as \"value\" and type String (default scope)")
    public void testStringVal() throws Exception {
        logViewer.clearLogs();
        OMElement response = axis2Client
                .sendSimpleStockQuoteRequest(getProxyServiceURLHttp("propertyStringDefaultRemoveTestProxy"), null, "Random Symbol");
        assertTrue(response.toString().contains("Property Set and Removed"),
                "Proxy Invocation Failed!");
        assertTrue(isMatchFound("symbol = WSO2 Lanka"),
                "String Property Not Either Set or Removed in the Axis2 scope!!");
    }

    @Test(groups = "wso2.esb",
          description = "Remove action as \"value\" and type Float (default scope)")
    public void testFloatVal() throws Exception {
        logViewer.clearLogs();
        OMElement response = axis2Client
                .sendSimpleStockQuoteRequest(getProxyServiceURLHttp("propertyFloatDefaultRemoveTestProxy"), null, "Random Symbol");
        assertTrue(response.toString().contains("Property Set and Removed"),
                "Proxy Invocation Failed!");
        assertTrue(isMatchFound("symbol = 123.123"),
                "Float Property Not Either Set or Removed in the Axis2 scope!!");
    }

    @Test(groups = "wso2.esb",
          description = "Remove action as \"value\" and type Long (default scope)")
    public void testLongVal() throws Exception {
        logViewer.clearLogs();
        OMElement response = axis2Client
                .sendSimpleStockQuoteRequest(getProxyServiceURLHttp("propertyLongDefaultRemoveTestProxy"), null, "Random Symbol");
        assertTrue(response.toString().contains("Property Set and Removed"),
                "Proxy Invocation Failed!");
        assertTrue(isMatchFound("symbol = 123123123"),
                "Long Property Not Either Set or Removed in the Axis2 scope!!");
    }

    @Test(groups = "wso2.esb",
          description = "Remove action as \"value\" and type Short (default scope)")
    public void testShortVal() throws Exception {
        logViewer.clearLogs();
        OMElement response = axis2Client
                .sendSimpleStockQuoteRequest(getProxyServiceURLHttp("propertyShortDefaultRemoveTestProxy"), null, "Random Symbol");
        assertTrue(response.toString().contains("Property Set and Removed"),
                "Proxy Invocation Failed!");
        assertTrue(isMatchFound("symbol = 12"),
                "Short Property Not Either Set or Removed in the Axis2 scope!!");
    }

    @Test(groups = "wso2.esb",
          description = "Remove action as \"value\" and type OM (default scope)")
    public void testOMVal() throws Exception {
        logViewer.clearLogs();
        OMElement response = axis2Client
                .sendSimpleStockQuoteRequest(getProxyServiceURLHttp("propertyOMDefaultRemoveTestProxy"), null, "Random Symbol");
        assertTrue(response.toString().contains("Property Set and Removed"),
                "Proxy Invocation Failed!");
        assertTrue(isMatchFound("symbol = OMMMMM"),
                "OM Property Not Either Set or Removed in the Axis2 scope!!");
    }

    /**
     * The method that checks whether the particular
     * match string is available in the sysytem logs
     */
    private boolean isMatchFound(String matchStr) throws Exception {
        boolean isSet = false;
        LogEvent[] logs = logViewer.getAllRemoteSystemLogs();
        int size = logs.length;
        for (int i = 0; i < size; i++) {
            if (logs[i].getMessage().contains(matchStr)) {
                for (int j = i; j < size; j++) {
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
