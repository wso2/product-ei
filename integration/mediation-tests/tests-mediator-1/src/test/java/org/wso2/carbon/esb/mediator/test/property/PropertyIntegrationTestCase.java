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


public class PropertyIntegrationTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath(File.separator + "artifacts" + File.separator + "ESB" + File.separator + "synapseconfig" + File.separator + "propertyMediatorConfig" + File.separator + "synapse.xml");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        cleanup();
    }

    @Test(groups = "wso2.esb", description = "Set a new property value (static text value) and retrieve it using get-property(property-name) Xpath function  (in default scope)")
    public void testStaticValue() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("static"), null
                , "MSFT");
        Assert.assertTrue(response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "name")).toString().contains("MSFT Company"));

    }

    @Test(groups = "wso2.esb", description = "Set a new property - Select \"Set Action As\" expression and give an Xpath expression (in default scope)")
    public void testXpath() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("static"), null, "MSFT");
        Assert.assertTrue(response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "name")).toString().contains("MSFT Company"));

    }

    @Test(groups = "wso2.esb", description = "Set a new property - Select \"Set Action As\" expression and give an Xpath expression - use name spaces (in default scope)")
    public void testXpathWithNameSpace() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("static"), null, "WSO2");
        Assert.assertTrue(response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "name")).toString().contains("WSO2 Company"));

    }


    @Test(groups = "wso2.esb", description = "Set action as \"experssion\" and type STRING (default scope)")
    public void testStringXpath() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("stringXpathProperty"), null, "WSO2");
        Assert.assertTrue(response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "name")).toString().contains("WSO2 Company"));

    }


    @Test(groups = "wso2.esb", description = "Set action as \"value\" and type STRING (default scope)")
    public void testStringValue() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("stringValProperty"), null, "WSO2");
        Assert.assertTrue(response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "name")).toString().contains("WSO2 Company"));


    }

    @Test(groups = "wso2.esb", description = "Set action as \"expression\" and type INTEGER (default scope)")
    public void testIntegerXpath() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("integerXpathProperty"), null, "88888888");
        Assert.assertTrue(response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "name")).toString().contains("88888888 Company"));

    }

    @Test(groups = "wso2.esb", description = "Set action as \"value\" and type INTEGER (default scope)")
    public void testIntegerVal() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("integerValProperty"), null, "88888888");
        Assert.assertTrue(response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "name")).toString().contains("88888888 Company"));

    }

    @Test(groups = "wso2.esb", description = "Set action as \"expression\" and type Boolean (default scope)")
    public void testBooleanXpath() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("booleanXpath"), null, "TRUE");
        Assert.assertTrue(response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "name")).toString().contains("TRUE Company"));

    }

    @Test(groups = "wso2.esb", description = "Set action as \"value\" and type BOOLEAN (default scope)")
    public void testBooleanVal() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("booleanVal"), null, "FALSE");
        Assert.assertTrue(response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "name")).toString().contains("FALSE Company"));

    }

    @Test(groups = "wso2.esb", description = "Set action as \"expression\" and type Short (default scope)")
    public void testShortXpath() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("shortXpath"), null, "88");
        Assert.assertTrue(response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "name")).toString().contains("88 Company"));

    }

    @Test(groups = "wso2.esb", description = "Set action as \"value\" and type Short (default scope)")
    public void testShortVal() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("shortVal"), null, "88");
        Assert.assertTrue(response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "name")).toString().contains("88 Company"));


    }

    @Test(groups = "wso2.esb", description = "Set action as \"expression\" and type Long (default scope)")
    public void testLongXpath() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("longXpath"), null, "8888888888");
        Assert.assertTrue(response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "name")).toString().contains("8888888888 Company"));


    }

    @Test(groups = "wso2.esb", description = "Set action as \"value\" and type Long (default scope)")
    public void testLongVal() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("longVal"), null, "8888888888");
        Assert.assertTrue(response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "name")).toString().contains("8888888888 Company"));

    }

    @Test(groups = "wso2.esb", description = "Set action as \"expression\" and type Double (default scope)")
    public void testDoubleXpath() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("doubleXpath"), null, "8888888888.8888888888888");
        Assert.assertTrue(response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "name")).toString().contains("8888888888.8888888888888 Company"));

    }

    @Test(groups = "wso2.esb", description = "Set action as \"value\" and type Double (default scope)")
    public void testDoubletVal() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("doubleVal"), null, "8888888888.8888888888888");
        Assert.assertTrue(response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "name")).toString().contains("8888888888.8888888888888 Company"));

    }

    @Test(groups = "wso2.esb", description = "Set action as \"expression\" and type Float (default scope)")
    public void testFloatXpath() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("floatXpath"), null, "8888.8888");
        Assert.assertTrue(response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "name")).toString().contains("8888.8888 Company"));
    }

    @Test(groups = "wso2.esb", description = "Set action as \"value\" and type Float (default scope)")
    public void testFloatVal() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("floatVal"), null, "8888.8888");
        Assert.assertTrue(response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "name")).toString().contains("8888.8888 Company"));


    }

    @Test(groups = "wso2.esb", description = "SOAP header specific properties")
    public void testSOAPHeaders() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("Axis2ProxyService"), null, "WSO2");
        Assert.assertTrue(response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "name")).toString().contains("WSO2 Company"));


    }

    @Test(groups = "wso2.esb", description = "Specify invalid Xpath function when setting a property")
    public void testInvalidXpath() throws Exception {
        try {
            OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("negative"), null, "MSFT");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof org.apache.axis2.AxisFault);
        }
    }

    @Test(groups = "wso2.esb", description = "Set the property in one scope and read it from another scope")
    public void testInvalidScope() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("negative"), null, "WSO2");
        Assert.assertTrue(response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "name")).toString().contains("WSO2 Company"));


    }

    @Test(groups = "wso2.esb", description = "Synapse Xpath variables")
    public void testSynapseXpathVariables() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("SynapseXpathvariables"), null, "WSO2");
        Assert.assertTrue(response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "name")).toString().contains("WSO2 Company"));


    }

    @Test(groups = "wso2.esb", description = "Synapse Properties")
    public void testSynapseProperties() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("SynapseProperties"), null, "WSO2");
        Assert.assertTrue(response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "name")).toString().contains("WSO2 Company"));


    }

    @Test(groups = "wso2.esb", description = "HTTP Properties")
    public void testHttpProperties() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("HttpProperties"), null, "WSO2");
        Assert.assertTrue(response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "name")).toString().contains("WSO2 Company"));


    }

    @Test(groups = "wso2.esb", description = "Generic Properties")
    public void testGenericProperties() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("GenericProperties"), null, "WSO2");
        Assert.assertTrue(response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "name")).toString().contains("WSO2 Company"));
    }
}


