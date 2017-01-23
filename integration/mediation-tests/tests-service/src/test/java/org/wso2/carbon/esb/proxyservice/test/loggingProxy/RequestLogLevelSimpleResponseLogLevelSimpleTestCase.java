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
package org.wso2.carbon.esb.proxyservice.test.loggingProxy;

import org.apache.axiom.om.OMElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

public class RequestLogLevelSimpleResponseLogLevelSimpleTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath(
                "/artifacts/ESB/proxyconfig/proxy/loggingProxy/request_log_level_simple_response_log_level_simple.xml");
        isProxyDeployed("StockQuoteProxy");

    }

    @Test(groups = "wso2.esb", description = "- Logging proxy -Request log level simple response log level simple",
            enabled = false)
    public void testLoggingProxyLoggingLevel() throws Exception {

        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("StockQuoteProxy"), null, "WSO2");
        //ToDo Assert Logs
    }


    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}
