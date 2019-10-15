/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.esb.mediator.test.script;

import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
//import org.wso2.carbon.logging.view.stub.LogViewerLogViewerException;
import org.wso2.carbon.logging.view.data.xsd.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import java.rmi.RemoteException;
import static org.testng.Assert.assertNotNull;

/**
 * This test case verifies that, setting and removing properties with NashornJs happens correctly.
 */
public class SetRemovePropertiesWithNashornJsTestCase extends ESBIntegrationTest {

    private LogViewerClient logViewerClient;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }

    @Test(groups = "wso2.esb", description = "Set a property with axis2 scope in script mediator")
    public void testSetPropertyWithAxis2ScopeInScript() throws Exception {

        boolean propertySet;
        boolean propertyRemoved;
        logViewerClient.clearLogs();
        OMElement response = axis2Client.sendCustomQuoteRequest(getProxyServiceURLHttp
                ("setRemovePropertiesWithNashornJsTestProxy"), null, "inlineTest");
        assertNotNull(response, "Response message null");
        propertySet = isPropertyContainedInLog("Axis2_Property = AXIS2_PROPERTY");
        Assert.assertTrue(propertySet, " The property with axis2 scope is not set ");
        propertyRemoved = isPropertyContainedInLog("Axis2_Property_After_Remove = null");
        Assert.assertTrue(propertyRemoved, " The property with axis2 scope is not removed ");
    }

    @Test(groups = "wso2.esb", description = "Set a property with transport scope in script mediator")
    public void testSetPropertyWithTransportScopeInScript() throws Exception {

        boolean propertySet;
        boolean propertyRemoved;
        logViewerClient.clearLogs();
        OMElement response = axis2Client.sendCustomQuoteRequest(getProxyServiceURLHttp
                ("setRemovePropertiesWithNashornJsTestProxy"), null, "inlineTest");
        assertNotNull(response, "Response message null");
        propertySet = isPropertyContainedInLog("Transport_Property = TRANSPORT_PROPERTY");
        Assert.assertTrue(propertySet, " The property with transport scope is not set ");
        propertyRemoved = isPropertyContainedInLog("Transport_Property_After_Remove = null");
        Assert.assertTrue(propertyRemoved, " The property with transport scope is not removed ");
    }

    @Test(groups = "wso2.esb", description = "Set a property with operation scope in script mediator")
    public void testSetPropertyWithOperationScopeInScript() throws Exception {

        boolean propertySet;
        boolean propertyRemoved;
        logViewerClient.clearLogs();
        OMElement response = axis2Client.sendCustomQuoteRequest(getProxyServiceURLHttp
                ("setRemovePropertiesWithNashornJsTestProxy"), null, "inlineTest");
        assertNotNull(response, "Response message null");
        propertySet = isPropertyContainedInLog("Operation_Property = OPERATION_PROPERTY");
        Assert.assertTrue(propertySet, " The property with operation scope is not set ");
        propertyRemoved = isPropertyContainedInLog("Operation_Property_After_Remove = null");
        Assert.assertTrue(propertyRemoved, " The property with operation scope is not removed ");
    }

    /**
     * This method check whether given property contains in the logs.
     * @param property required property which needs to be validate if exists or not.
     * @return A Boolean
     */
    private boolean isPropertyContainedInLog(String property) throws RemoteException {
        LogEvent[] logs = logViewerClient.getAllRemoteSystemLogs();
        boolean containsProperty = false;
        for (LogEvent logEvent : logs) {
            String message = logEvent.getMessage();
            if (message.contains(property)) {
                containsProperty = true;
                break;
            }
        }
        return containsProperty;
    }
}
