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

/**
 * This test case tests whether the setting of properties
 * in the Axis2-client scope is working fine.
 */
public class PropertyIntegrationAxis2ClientScopeTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
    }

    @Test(groups = "wso2.esb",
          description = "Set action as \"value\" and type Double (axis2-client scope)")
    public void testDoubleVal() throws Exception {
        OMElement response = axis2Client
                .sendSimpleStockQuoteRequest(getProxyServiceURLHttp("propertyDoubleAxis2ClientTestProxy"), null, "Random Symbol");
        assertTrue(response.toString().contains("123123.123123"),
                "Double Property Not Set in the Axis2-client scope!");
    }

    @Test(groups = "wso2.esb",
          description = "Set action as \"value\" and type Integer (axis2-client scope)")
    public void testIntVal() throws Exception {
        OMElement response = axis2Client
                .sendSimpleStockQuoteRequest(getProxyServiceURLHttp("propertyIntAxis2ClientTestProxy"), null, "Random Symbol");
        assertTrue(response.toString().contains("123"),
                "Integer Property Not Set in the Axis2-client scope!");
    }

    @Test(groups = "wso2.esb",
          description = "Set action as \"value\" and type String (axis2-client scope)")
    public void testStringVal() throws Exception {
        OMElement response = axis2Client
                .sendSimpleStockQuoteRequest(getProxyServiceURLHttp("propertyStringAxis2ClientTestProxy"), null, "Random Symbol");
        assertTrue(response.toString().contains("WSO2 Lanka"),
                "String Property Not Set in the Axis2-client scope!");
    }

    @Test(groups = "wso2.esb",
          description = "Set action as \"value\" and type Boolean (axis2-client scope)")
    public void testBooleanVal() throws Exception {
        OMElement response = axis2Client
                .sendSimpleStockQuoteRequest(getProxyServiceURLHttp("propertyBooleanAxis2ClientTestProxy"), null, "Random Symbol");
        assertTrue(response.toString().contains("true"),
                "Boolean Property Not Set in the Axis2-client scope!");
    }

    @Test(groups = "wso2.esb",
          description = "Set action as \"value\" and type Float (axis2-client scope)")
    public void testFloatVal() throws Exception {
        OMElement response = axis2Client
                .sendSimpleStockQuoteRequest(getProxyServiceURLHttp("propertyFloatAxis2ClientTestProxy"), null, "Random Symbol");
        assertTrue(response.toString().contains("123.123"),
                "Float Property Not Set in the Axis2-client scope!");
    }

    @Test(groups = "wso2.esb",
          description = "Set action as \"value\" and type Short (axis2-client scope)")
    public void testShortVal() throws Exception {
        OMElement response = axis2Client
                .sendSimpleStockQuoteRequest(getProxyServiceURLHttp("propertyShortAxis2ClientTestProxy"), null, "Random Symbol");
        assertTrue(response.toString().contains("12"),
                "Short Property Not Set in the Axis2-client scope!");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        cleanup();
    }
}
