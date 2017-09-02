/*
*Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.esb.mediator.test.send;

import junit.framework.Assert;
import org.apache.http.HttpResponse;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.SimpleHttpClient;

import java.util.HashMap;
import java.util.Map;

/**
 * Set the enableMTOM property to false in out sequence and see if correct content type is received for MTOM backend
 * responses.
 *
 * Related JIRA : https://wso2.org/jira/browse/ESBJAVA-5097
 */
public class MTOMEnableDisableSendMediatorTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void uploadSynapseConfig() throws Exception {
        super.init();
        loadESBConfigurationFromClasspathIfNotExists
                ("/artifacts/ESB/mediatorconfig/send/mtom_enable_disable_config.xml");
    }

    @Test(groups = {"wso2.esb"}, description = "disable mtom before send mediator", enabled = true)
    public void disableMtomBeforeSendMediatorTest() throws Exception {
        String proxyHttpUrl = getProxyServiceURLHttp("MtomEnabledBackEndOUT");

        SimpleHttpClient simpleHttpClient = new SimpleHttpClient();
        Map<String, String> headers = new HashMap<>();
        headers.put("SOAPAction","urn:mediate");
        String body = "<?xml version='1.0' encoding='UTF-8'?>"
                + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                + "  <soapenv:Body>"
                + "    <echo:echoString xmlns:echo=\"http://echo.services.core.carbon.wso2.org\">Test Message</echo:echoString>"
                + "  </soapenv:Body>"
                + "</soapenv:Envelope>";

        HttpResponse httpResponse = simpleHttpClient.doPost(proxyHttpUrl, headers, body, "text/xml; charset=UTF-8");

        Assert.assertEquals("text/xml; charset=UTF-8", httpResponse.getFirstHeader("Content-Type").getValue());
    }

    @AfterClass(alwaysRun = true)
    private void destroy() throws Exception {
        super.cleanup();
    }
}
