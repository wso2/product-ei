package org.wso2.carbon.esb.mediator.test.callOut;

/*
* Copyright 2015 The Apache Software Foundation.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import org.apache.axiom.om.OMElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import static org.testng.Assert.assertTrue;

public class CalloutStatusCodeTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void deployService() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/callout/CalloutStatusCodeTest.xml");
    }

    @Test(groups = "wso2.esb", description = "Test for check http status code can be retrived form HTTP_SC")
    public void testCalloutStatusCode() throws Exception {

        OMElement response = axis2Client.
                sendSimpleStockQuoteRequest(getProxyServiceURLHttp("StatusCodeTestClientProxy"), null, "IBM");
        assertTrue(response.toString().contains("200"));

    }

    @AfterClass(alwaysRun = true)
    public void unDeployService() throws Exception {
        super.cleanup();
    }


}
