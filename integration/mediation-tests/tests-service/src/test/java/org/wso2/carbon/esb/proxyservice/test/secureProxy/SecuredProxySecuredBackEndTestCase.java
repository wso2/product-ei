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

import org.apache.axiom.om.OMElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.SecurityAdminServiceClient;
import org.wso2.carbon.security.mgt.stub.config.SecurityAdminServiceSecurityConfigExceptionException;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.SecureServiceClient;

import javax.xml.namespace.QName;
import java.rmi.RemoteException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class SecuredProxySecuredBackEndTestCase extends ESBIntegrationTest {

    private SecureServiceClient secureAxisServiceClient;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        super.init();
        loadESBConfigurationFromClasspath(
                "/artifacts/ESB/proxyconfig/proxy/secureProxy/secured_proxy_secured_backend.xml");
        secureAxisServiceClient = new SecureServiceClient();

    }

    @Test(groups = "wso2.esb", description = "- Secure proxy" +
                                             "- Secured service secured proxy" +
                                             "- used scenario1-policy(UsernameToken) to secure proxy and scenario3-policy(Integrity) to secure backend")
    public void testSecuredProxySecuredService() throws Exception {

        OMElement response;
        String lastPrice;
        String symbol;

        applySecurity(); //only https available

        response = secureAxisServiceClient.sendSecuredStockQuoteRequest(userInfo, getProxyServiceURLHttps("StockQuoteProxy"), 1, "WSO2");

        lastPrice = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "last"))
                .getText();
        assertNotNull(lastPrice, "Fault: response message 'last' price null");

        symbol = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "symbol"))
                .getText();
        assertEquals(symbol, "WSO2", "Fault: value 'symbol' mismatched");
        disableSecurity();

    }


    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }

    private void applySecurity()
            throws SecurityAdminServiceSecurityConfigExceptionException, RemoteException,
                   InterruptedException {
        applySecurity("StockQuoteProxy", 1, getUserRole());
    }

    private void disableSecurity()
            throws SecurityAdminServiceSecurityConfigExceptionException, RemoteException {
        SecurityAdminServiceClient securityAdminServiceClient = new SecurityAdminServiceClient(contextUrls.getBackEndUrl(), sessionCookie);
        securityAdminServiceClient.disableSecurity("StockQuoteProxy");
    }

}
