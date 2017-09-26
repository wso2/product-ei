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

public class PropertyIntegrationTransportScopeTestCase extends ESBIntegrationTest{

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
            if (logs[i].getMessage().contains(matchStr)) {
                isSet = true;
                break;
            }
        }
        return isSet;
    }

    @Test(groups = "wso2.esb", description = "Set action as \"value\" and type Double (transport scope)")
    public void testDoubleVal() throws Exception {
        assertTrue(isPropertySet("propertyDoubleTransportTestProxy", "symbol = 123123.123123"), "Property Not Set!");
    }

    @Test(groups = "wso2.esb", description = "Set action as \"value\" and type Integer (transport scope)")
    public void testIntVal() throws Exception {
        assertTrue(isPropertySet("propertyIntTransportTestProxy", "symbol = 123"), "Property Not Set!");
    }

    @Test(groups = "wso2.esb", description = "Set action as \"value\" and type String (transport scope)")
    public void testStringVal() throws Exception {
        assertTrue(isPropertySet("propertyStringTransportTestProxy", "symbol = WSO2 Lanka"), "Property Not Set!");
    }

    @Test(groups = "wso2.esb", description = "Set action as \"value\" and type Boolean (transport scope)")
    public void testBooleanVal() throws Exception {
        assertTrue(isPropertySet("propertyBooleanTransportTestProxy", "symbol = true"), "Property Not Set!");
    }

    @Test(groups = "wso2.esb", description = "Set action as \"value\" and type Float (transport scope)")
    public void testFloatVal() throws Exception {
        assertTrue(isPropertySet("propertyFloatTransportTestProxy", "symbol = 123.123"), "Property Not Set!");
    }

    @Test(groups = "wso2.esb", description = "Set action as \"value\" and type Short (transport scope)")
    public void testShortVal() throws Exception {
        assertTrue(isPropertySet("propertyShortTransportTestProxy", "symbol = 12"), "Property Not Set!");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        cleanup();
    }
}