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

package org.wso2.carbon.esb.proxyservice.test.secureProxy;

import org.apache.axiom.om.util.AXIOMUtil;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.proxyadmin.stub.types.carbon.ProxyData;
import org.wso2.esb.integration.common.clients.proxy.admin.ProxyServiceAdminClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

public class EditSecuredProxyTestCase extends ESBIntegrationTest {

    private ProxyServiceAdminClient proxyServiceAdminClient;


    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        proxyServiceAdminClient = new ProxyServiceAdminClient(context.getContextUrls().getBackEndUrl(), sessionCookie);
        addProxy();
    }

    //Related to Patch Automation https://wso2.org/jira/browse/CARBON-5551
    @Test(groups = {"wso2.esb"}, description = "Adding a WSDL to a Proxy Service with Security")
    public void testEditSecuredProxy() throws Exception {

        applySecurity("EditSecuredProxy", 1, new String[]{"admin"});
        Thread.sleep(5000);
        ProxyData prxy = proxyServiceAdminClient.getProxyDetails("EditSecuredProxy");
        prxy.setWsdlURI("file:repository/samples/resources/proxy/sample_proxy_1.wsdl");
        proxyServiceAdminClient.updateProxy(prxy);
        Thread.sleep(1000);
        isProxyDeployed("EditSecuredProxy");
        prxy = proxyServiceAdminClient.getProxyDetails("EditSecuredProxy");
        Assert.assertNotNull(prxy);
        Assert.assertEquals(prxy.getWsdlURI(), "file:repository/samples/resources/proxy/sample_proxy_1.wsdl");
    }

    @AfterClass(alwaysRun = true)
    public void clear() throws Exception {
        super.cleanup();
        proxyServiceAdminClient = null;

    }

    private void addProxy() throws Exception {

        addProxyService(AXIOMUtil.stringToOM("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                             "<proxy xmlns=\"http://ws.apache.org/ns/synapse\" name=\"EditSecuredProxy\">\n" +
                                             "        <target>\n" +
                                             "            <endpoint>\n" +
                                             "                <address uri=\"http://localhost:9000/services/SimpleStockQuoteService\"/>\n" +
                                             "            </endpoint>\n" +
                                             "            <outSequence>\n" +
                                             "                <send/>\n" +
                                             "            </outSequence>\n" +
                                             "        </target>\n" +
                                             "    </proxy>"));
    }
}

