/*
*  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.wso2.carbon.esb.passthru.transport.test;

import org.apache.axiom.om.OMElement;
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

public class ESBJAVA4328RestUrlPostFixValueEncodeTestCase extends ESBIntegrationTest {
    private OMElement response;
    private HttpClientUtil client;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        OMElement config = esbUtils.loadResource("/artifacts/ESB/mediatorconfig/property/REST_URL_postfix_encode.xml");
        config = AXIOMUtil.stringToOM(config.toString().replace("http://localhost:8280/services/", getProxyServiceURLHttp("")));
        updateESBConfiguration(config);
        client= new HttpClientUtil();
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.esb", description = "Test-REST URL Postfix Encode")
    public void testRESTUrlPostFix() throws Exception {
        response = client.get(getProxyServiceURLHttp("Axis2RestServiceEncoded")+"/echoString?in=wso2");
        assertNotNull(response,"Response is null");
        assertEquals(response.getQName().getLocalPart(),"echoStringResponse","Tag does not match");
        assertEquals(response.getFirstElement().getLocalName(),"return","Tag does not match");
        assertEquals(response.getFirstElement().getText(),"wso2%20lanka","Text does not match");

    }
    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        cleanup();
    }
}
