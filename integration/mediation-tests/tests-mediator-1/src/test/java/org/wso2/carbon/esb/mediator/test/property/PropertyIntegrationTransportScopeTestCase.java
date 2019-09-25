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
import org.wso2.carbon.logging.view.data.xsd.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.xml.namespace.QName;
import java.io.File;

import static org.testng.Assert.assertTrue;

/**
 * This test case tests whether the setting of properties
 * in the Transport scope is working fine.
 */

public class PropertyIntegrationTransportScopeTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
    }

    @Test(groups = "wso2.esb",
          description = "Set action as \"value\" and type Double (transport scope)")
    public void testDoubleVal() throws Exception {
        OMElement response = axis2Client
                .sendSimpleStockQuoteRequest(getProxyServiceURLHttp("propertyDoubleTransportTestProxy"), null, "Random Symbol");
        assertTrue(response.toString().contains("123123.123123"),
                "Double Property Not Set in the Transport scope!");
    }

    @Test(groups = "wso2.esb",
          description = "Set action as \"value\" and type Integer (transport scope)")
    public void testIntVal() throws Exception {
        OMElement response = axis2Client
                .sendSimpleStockQuoteRequest(getProxyServiceURLHttp("propertyIntTransportTestProxy"), null, "Random Symbol");
        assertTrue(response.toString().contains("123"),
                "Integer Property Not Set in the Transport scope!");
    }

    @Test(groups = "wso2.esb",
          description = "Set action as \"value\" and type String (transport scope)")
    public void testStringVal() throws Exception {
        OMElement response = axis2Client
                .sendSimpleStockQuoteRequest(getProxyServiceURLHttp("propertyStringTransportTestProxy"), null, "Random Symbol");
        assertTrue(response.toString().contains("WSO2 Lanka"),
                "String Property Not Set in the Transport scope!");
    }

    @Test(groups = "wso2.esb",
          description = "Set action as \"value\" and type Boolean (transport scope)")
    public void testBooleanVal() throws Exception {
        OMElement response = axis2Client
                .sendSimpleStockQuoteRequest(getProxyServiceURLHttp("propertyBooleanTransportTestProxy"), null, "Random Symbol");
        assertTrue(response.toString().contains("true"),
                "Boolean Property Not Set in the Transport scope!");
    }

    @Test(groups = "wso2.esb",
          description = "Set action as \"value\" and type Float (transport scope)")
    public void testFloatVal() throws Exception {
        OMElement response = axis2Client
                .sendSimpleStockQuoteRequest(getProxyServiceURLHttp("propertyFloatTransportTestProxy"), null, "Random Symbol");
        assertTrue(response.toString().contains("123.123"),
                "Float Property Not Set in the Transport scope!");
    }

    @Test(groups = "wso2.esb",
          description = "Set action as \"value\" and type Short (transport scope)")
    public void testShortVal() throws Exception {
        OMElement response = axis2Client
                .sendSimpleStockQuoteRequest(getProxyServiceURLHttp("propertyShortTransportTestProxy"), null, "Random Symbol");
        assertTrue(response.toString().contains("12"),
                "Short Property Not Set in the Transport scope!");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        cleanup();
    }
}