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
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.xml.namespace.QName;
import java.io.File;

/**
 * This test case tests whether the setting of properties
 * in the default scope is working fine.
 */

public class PropertyIntegrationDefaultScopeTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
    }

    @Test(groups = "wso2.esb",
          description = "Set action as \"value\" and type Double (defaultscope)")
    public void testDoubleVal() throws Exception {
        OMElement response = axis2Client
                .sendSimpleStockQuoteRequest(getProxyServiceURLHttp("propertyDoubleDefaultTestProxy"), null,
                        "123123.123123");
        Assert.assertTrue(
                response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "name"))
                        .toString().contains("123123.123123 Company"), "Double Property not set in the Default scope!");

    }

    @Test(groups = "wso2.esb",
          description = "Set action as \"value\" and type Integer (defaultscope)")
    public void testIntVal() throws Exception {
        OMElement response = axis2Client
                .sendSimpleStockQuoteRequest(getProxyServiceURLHttp("propertyIntDefaultTestProxy"), null, "123");
        Assert.assertTrue(
                response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "name"))
                        .toString().contains("123 Company"), "Integer Property not set in the Default scope!");

    }

    @Test(groups = "wso2.esb",
          description = "Set action as \"value\" and type String (defaultscope)")
    public void testStringVal() throws Exception {
        OMElement response = axis2Client
                .sendSimpleStockQuoteRequest(getProxyServiceURLHttp("propertyStringDefaultTestProxy"), null, "WSO2");
        Assert.assertTrue(
                response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "name"))
                        .toString().contains("WSO2 Company"), "String Property not set in the Default scope!");

    }

    @Test(groups = "wso2.esb",
          description = "Set action as \"value\" and type Boolean (defaultscope)")
    public void testBooleanVal() throws Exception {
        OMElement response = axis2Client
                .sendSimpleStockQuoteRequest(getProxyServiceURLHttp("propertyBooleanDefaultTestProxy"), null, "TRUE");
        Assert.assertTrue(
                response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "name"))
                        .toString().contains("TRUE Company"), "Boolean Property not set in the Default scope!");

    }

    @Test(groups = "wso2.esb",
          description = "Set action as \"value\" and type Float (defaultscope)")
    public void testFloatVal() throws Exception {
        OMElement response = axis2Client
                .sendSimpleStockQuoteRequest(getProxyServiceURLHttp("propertyFloatDefaultTestProxy"), null, "123.123");
        Assert.assertTrue(
                response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "name"))
                        .toString().contains("123.123 Company"), "Float Property not set in the Default scope!");

    }

    @Test(groups = "wso2.esb",
          description = "Set action as \"value\" and type Short (defaultscope)")
    public void testShortVal() throws Exception {
        OMElement response = axis2Client
                .sendSimpleStockQuoteRequest(getProxyServiceURLHttp("propertyShortDefaultTestProxy"), null, "12");
        Assert.assertTrue(
                response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "name"))
                        .toString().contains("12 Company"), "Short Property not set in the Default scope!");

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        cleanup();
    }
}