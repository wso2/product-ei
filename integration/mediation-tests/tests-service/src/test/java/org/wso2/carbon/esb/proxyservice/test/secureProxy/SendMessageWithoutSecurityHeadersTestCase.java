/*
 * Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.proxyservice.test.secureProxy;

import org.apache.axis2.AxisFault;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.security.mgt.stub.config.SecurityAdminServiceSecurityConfigExceptionException;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.rmi.RemoteException;

import static org.testng.Assert.fail;

public class SendMessageWithoutSecurityHeadersTestCase extends ESBIntegrationTest {


    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        super.init();
        loadESBConfigurationFromClasspath(
                "/artifacts/ESB/proxyconfig/proxy/secureProxy/stockquote_proxy_unsecured.xml");
    }

    @Test(groups = "wso2.esb", description = "- Secure proxy" +
                                             "- Send a message without security headers to a secure proxy", expectedExceptions = {AxisFault.class})
    public void testSendingMessagesWithoutSecHeaders() throws Exception {

        applySecurity();
        axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("StockQuoteProxy"), null, "WSO2");
        fail("Fault: Should be an AxisFault");
        // TODO - Assert the synapse log for " org.apache.axis2.AxisFault: SOAP header missing"

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }

    private void applySecurity()
            throws SecurityAdminServiceSecurityConfigExceptionException, RemoteException,
                   InterruptedException {
        applySecurity("StockQuoteProxy", 5, getUserRole());
    }

}
