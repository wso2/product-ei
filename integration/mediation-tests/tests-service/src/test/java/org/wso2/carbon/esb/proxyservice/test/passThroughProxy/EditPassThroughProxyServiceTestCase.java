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
package org.wso2.carbon.esb.proxyservice.test.passThroughProxy;

import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.proxyadmin.stub.types.carbon.ProxyData;
import org.wso2.esb.integration.common.clients.proxy.admin.ProxyServiceAdminClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.xml.namespace.QName;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class EditPassThroughProxyServiceTestCase extends ESBIntegrationTest {
    private final String proxyName = "EditStockQuotePassThroughProxy";

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath(
                "/artifacts/ESB/proxyconfig/proxy/passThroughProxy/EditPassThroughProxy.xml");

    }

    @Test(groups = "wso2.esb", description = "Edit Pass through proxy ")
    public void editProxyService() throws Exception {
        ProxyServiceAdminClient proxyServiceAdminClient = new ProxyServiceAdminClient(context.getContextUrls().getBackEndUrl(), getSessionCookie());
        ProxyData prxy = proxyServiceAdminClient.getProxyDetails(proxyName);
        prxy.setWsdlURI("file:repository/samples/resources/proxy/sample_proxy_1.wsdl");
        proxyServiceAdminClient.updateProxy(prxy);
        Thread.sleep(1000);
        isProxyDeployed(proxyName);
        prxy = proxyServiceAdminClient.getProxyDetails(proxyName);
        Assert.assertNotNull(prxy);
        Assert.assertEquals(prxy.getWsdlURI(), "file:repository/samples/resources/proxy/sample_proxy_1.wsdl", "WSDl Url invalid");
    }

    @Test(groups = "wso2.esb", description = "Invoking Pass through proxy http", dependsOnMethods = {"editProxyService"})
    public void testHttpPassThroughProxyAfterEditing() throws Exception {

        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp(proxyName),
                                                                     null, "WSO2");

        String lastPrice = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "last"))
                .getText();
        assertNotNull(lastPrice, "Fault: response message 'last' price null");

        String symbol = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "symbol"))
                .getText();
        assertEquals(symbol, "WSO2", "Fault: value 'symbol' mismatched");


    }


    @Test(groups = "wso2.esb", description = "Invoking Pass through proxy https", dependsOnMethods = {"editProxyService"})
    public void testHttpsPassThroughProxyAfterEditing() throws Exception {

        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttps(proxyName),
                                                                     null, "WSO2");

        String lastPrice = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "last"))
                .getText();
        assertNotNull(lastPrice, "Fault: response message 'last' price null");

        String symbol = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "symbol"))
                .getText();
        assertEquals(symbol, "WSO2", "Fault: value 'symbol' mismatched");


    }


    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}
