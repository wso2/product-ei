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
package org.wso2.carbon.esb.mediator.test.xquery;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.carbon.esb.mediator.test.xquery.util.RequestUtil;

import javax.xml.namespace.QName;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class XQueryCustomVariableAsInt extends ESBIntegrationTest {
    @BeforeClass(alwaysRun = true)
    public void uploadSynapseConfig() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/xquery/xquery_variable_type_int_synapse101.xml");
    }


    @Test(groups = {"wso2.esb"},
          description = "Do XQuery transformation with target attribute specified as XPath value and variable as Int")
    public void testXQueryTransformationWithIntValue() throws AxisFault {
        OMElement response;
        RequestUtil getQuoteCustomRequest = new RequestUtil();
        response = getQuoteCustomRequest.sendReceive(
                getProxyServiceURLHttp("StockQuoteProxy"),
                "WSO2");
        assertNotNull(response, "Response message null");
        assertEquals(response.getFirstElement().getFirstChildWithName(
                new QName("http://services.samples/xsd", "symbol", "ax21")).getText(), "512337203", "Symbol name mismatched");

    }

    @AfterClass(alwaysRun = true)
    private void destroy() throws Exception {
        super.cleanup();
    }


}
