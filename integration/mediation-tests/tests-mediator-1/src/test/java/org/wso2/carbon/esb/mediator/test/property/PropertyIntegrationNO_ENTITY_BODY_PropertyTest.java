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
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.util.AXIOMUtil;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.test.utils.http.client.HttpClientUtil;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Property Mediator NO_ENTITY_BODY Property Test
 */

public class PropertyIntegrationNO_ENTITY_BODY_PropertyTest extends ESBIntegrationTest {

    private OMElement response1;
    private HttpClientUtil client;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        OMElement config = esbUtils.loadResource("/artifacts/ESB/mediatorconfig/property/NO_ENTITY_BODY.xml");
        config = AXIOMUtil.stringToOM(config.toString().replace("http://localhost:8280/services/", getProxyServiceURLHttp("")));
        updateESBConfiguration(config);
        client = new HttpClientUtil();
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.esb", description = "Test-Without No_ENTITY_BODY Property")
    public void testWithoutNoEntityBodyPropertTest() throws Exception {
        response1 = client.get(getProxyServiceURLHttp("Axis2ProxyService1") + "/echoString?in=IBM");
        assertNotNull(response1, "Response is null");
        assertEquals(response1.getFirstElement().getText(), "IBM", "Text does not match");
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.esb", expectedExceptions = OMException.class,
          description = "Test-With NO_ENTITY_BODY")
    public void testWithNoEntityBodyPropertTest() throws Exception {
        client.get(getProxyServiceURLHttp("Axis2ProxyService2") + "/echoString?in=IBM");
    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        cleanup();
    }
}
