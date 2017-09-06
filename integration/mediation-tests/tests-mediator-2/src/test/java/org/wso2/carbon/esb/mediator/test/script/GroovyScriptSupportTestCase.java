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
package org.wso2.carbon.esb.mediator.test.script;

import org.apache.axiom.om.OMElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.JSONClient;

import javax.xml.namespace.QName;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class GroovyScriptSupportTestCase extends ESBIntegrationTest {
    private JSONClient jsonclient;

    @BeforeClass(alwaysRun = true)
    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    public void setEnvironment() throws Exception {
        super.init();
        jsonclient = new JSONClient();
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = {"wso2.esb", "localOnly"}, description = "Script Mediator -Run a Groovy script with the mediator",enabled =false)
    public void testGroovyScriptMediation() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(
                getProxyServiceURLHttp("scriptMediatorGroovyBasicTestProxy"), null,
                "IBM");

        String lastPrice = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "last"))
                .getText();
        assertNotNull(lastPrice, "Fault: response message 'last' price null");

        String symbol = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "symbol"))
                .getText();
        assertEquals(symbol, "IBM", "Fault: value 'symbol' mismatched");
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = {"wso2.esb", "localOnly"}, description = "Script Mediator -Run a Groovy script with setPayloadJson",enabled =false)
    public void testGroovySetPayloadJson() throws Exception {

        String query = "{\"key\":\"value\"}";
        String addUrl = getProxyServiceURLHttps("scriptMediatorGroovySetJsonPayloadTestProxy");
        String expectedResult = "{\"fileID\":\"89265\",\"mySiteID\":\"54571\"}";

        String actualResult = jsonclient.sendUserDefineRequest(addUrl, query).toString();

        assertEquals(actualResult, expectedResult, "Fault: value 'symbol' mismatched");
    }

    @AfterClass(alwaysRun = true)
    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    public void destroy() throws Exception {
        super.cleanup();
    }

}
