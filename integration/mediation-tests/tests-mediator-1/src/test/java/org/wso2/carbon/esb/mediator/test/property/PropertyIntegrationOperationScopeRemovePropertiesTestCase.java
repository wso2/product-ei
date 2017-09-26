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
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.xml.namespace.QName;
import java.io.File;

import static org.testng.Assert.assertTrue;

public class PropertyIntegrationOperationScopeRemovePropertiesTestCase extends ESBIntegrationTest{

    private static LogViewerClient logViewer;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        logViewer = new LogViewerClient(context.getContextUrls().getBackEndUrl(), sessionCookie);
    }

    public boolean isPropertySet(String proxy, String matchStr) throws Exception{
        boolean isSet = false;
        int beforeLogSize = logViewer.getAllSystemLogs().length;
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp(proxy), null, "Random Symbol");
        LogEvent[] logs = logViewer.getAllSystemLogs();
        int afterLogSize = logs.length;
        for (int i = (afterLogSize - beforeLogSize); i >= 0; i--) {
            if (logs[i].getMessage().contains("symbol = null") && logs[i+1].getMessage().contains(matchStr)) {
                isSet = true;
                break;
            }
        }
        return isSet;
    }

    @Test(groups = "wso2.esb", description = "Remove action as \"value\" and type Integer (operation scope)")
    public void testIntVal() throws Exception {
        assertTrue(isPropertySet("propertyIntOperationRemoveTestProxy", "symbol = 123"), "Property Not Either Set or Removed!");
    }

    @Test(groups = "wso2.esb", description = "Remove action as \"value\" and type String (operation scope)")
    public void testStringVal() throws Exception {
        assertTrue(isPropertySet("propertyStringOperationRemoveTestProxy", "symbol = WSO2 Lanka"), "Property Not Either Set or Removed!");
    }

    @Test(groups = "wso2.esb", description = "Remove action as \"value\" and type Float (operation scope)")
    public void testFloatVal() throws Exception {
        assertTrue(isPropertySet("propertyFloatOperationRemoveTestProxy", "symbol = 123.123"), "Property Not Either Set or Removed!");
    }

    @Test(groups = "wso2.esb", description = "Remove action as \"value\" and type Long (operation scope)")
    public void testLongVal() throws Exception {
        assertTrue(isPropertySet("propertyLongOperationRemoveTestProxy", "symbol = 123123123"), "Property Not Either Set or Removed!");
    }

    @Test(groups = "wso2.esb", description = "Remove action as \"value\" and type Short (operation scope)")
    public void testShortVal() throws Exception {
        assertTrue(isPropertySet("propertyShortOperationRemoveTestProxy", "symbol = 12"), "Property Not Either Set or Removed!");
    }

    @Test(groups = "wso2.esb", description = "Remove action as \"value\" and type OM (operation scope)")
    public void testOMVal() throws Exception {
        assertTrue(isPropertySet("propertyOMOperationRemoveTestProxy", "symbol = OMMMMM"), "Property Not Either Set or Removed!");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        cleanup();
    }
}