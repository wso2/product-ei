/**
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.esb.mediators.enrich;

import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

/**
 * Test case for https://wso2.org/jira/browse/ESBJAVA-4892
 */
public class ESBJAVA4892EnrichPropertyTestCase extends ESBIntegrationTest {
    private static final String PROXY_NAME = "OperationContextService";

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/enrich/OperationContextService.xml");
    }

    @Test(groups = "wso2.esb", description = "Tests Enriching Property in Operation context")
    public void testEnrichOperationContextProperty() throws Exception {

        OMElement response1 = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp(PROXY_NAME), null, "WSO2");
        OMElement response2 = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp(PROXY_NAME), null, "WSO2");
        //When enriching a Property from operation context, it keeps value during multiple requests. Therefore checking
        // equality of multiple responses.
        Assert.assertEquals(response1.toString(), response2.toString(), "Response messages are not equal.   ");
    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        super.cleanup();
    }
}
