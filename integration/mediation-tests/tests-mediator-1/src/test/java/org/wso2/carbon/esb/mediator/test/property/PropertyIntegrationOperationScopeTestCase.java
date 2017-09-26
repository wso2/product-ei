/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

public class PropertyIntegrationOperationScopeTestCase extends ESBIntegrationTest{

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath(File.separator + "artifacts" + File.separator + "ESB" + File.separator + "synapseconfig" + File.separator + "propertyMediatorConfig" + File.separator + "scope_operation_config.xml");
    }

    @Test(groups = "wso2.esb", description = "Set action as \"value\" and type Double (operation scope)")
    public void testDoubleVal() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("doubleVal_operation_scope"), null, "123123.123123");
        Assert.assertTrue(response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "name")).toString().contains("123123.123123 Company"));

    }

    @Test(groups = "wso2.esb", description = "Set action as \"value\" and type Integer (operation scope)")
    public void testIntVal() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("integerVal_operation_scope"), null, "123");
        Assert.assertTrue(response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "name")).toString().contains("123 Company"));

    }

    @Test(groups = "wso2.esb", description = "Set action as \"value\" and type String (operation scope)")
    public void testStringVal() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("stringVal_operation_scope"), null, "WSO2");
        Assert.assertTrue(response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "name")).toString().contains("WSO2 Company"));

    }

    @Test(groups = "wso2.esb", description = "Set action as \"value\" and type Boolean (operation scope)")
    public void testBooleanVal() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("booleanVal_operation_scope"), null, "TRUE");
        Assert.assertTrue(response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "name")).toString().contains("TRUE Company"));

    }

    @Test(groups = "wso2.esb", description = "Set action as \"value\" and type Float (operation scope)")
    public void testFloatVal() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("floatVal_operation_scope"), null, "123.123");
        Assert.assertTrue(response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "name")).toString().contains("123.123 Company"));

    }

    @Test(groups = "wso2.esb", description = "Set action as \"value\" and type Short (operation scope)")
    public void testShortVal() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("shortVal_operation_scope"), null, "12");
        Assert.assertTrue(response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "name")).toString().contains("12 Company"));

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        cleanup();
    }
}