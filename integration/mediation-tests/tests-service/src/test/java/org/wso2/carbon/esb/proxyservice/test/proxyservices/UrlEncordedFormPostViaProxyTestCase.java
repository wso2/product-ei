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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.esb.proxyservice.test.proxyservices;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.servers.WireMonitorServer;

import java.io.IOException;
import java.net.URLEncoder;


/**
 * Test class for issue
 * https://wso2.org/jira/browse/ESBJAVA-1696
 */
public class UrlEncordedFormPostViaProxyTestCase extends ESBIntegrationTest {

    private WireMonitorServer wireMonitorServer;


    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        init();
        uploadSynapseConfig();
        wireMonitorServer = new WireMonitorServer(8991);
        wireMonitorServer.start();
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Patch : ESBJAVA-1696 : Encoded Special characters in the URL is decoded at the Gateway and not re-encoded", enabled = true)
    public void testEncodingSpecialCharacterViaHttpProxy() throws IOException {
        HttpClient client = new HttpClient();
        client.getParams().setParameter(
                HttpMethodParams.RETRY_HANDLER,
                new DefaultHttpMethodRetryHandler(0, false));
        client.getParams().setSoTimeout(5000);
        client.getParams().setConnectionManagerTimeout(5000);

        // Create POST method
        String url = getProxyServiceURLHttp("MyProxy");
        PostMethod method = new PostMethod(url);
        // Set parameters on POST
        String value1 = "Hello World";
        String value2 = "This is a Form Submission containing %";
        String value3 = URLEncoder.encode("This is an encoded value containing %");
        method.setParameter("test1", value1);
        method.addParameter("test2", value2);
        method.addParameter("test3", value3);


        // Execute and print response
        try {
            client.executeMethod(method);
        } catch (Exception e) {

        } finally {
            method.releaseConnection();
        }
        String response = wireMonitorServer.getCapturedMessage();
        String[] responseArray = response.split("test");

        if (responseArray.length < 3) {
            Assert.fail("All attributes are not sent");
        }
        for (String res : responseArray) {
            if (res.startsWith("1")) {
                Assert.assertTrue(res.startsWith("1=" + URLEncoder.encode(value1).replace("+", "%20")));
            } else if (res.startsWith("2")) {
                Assert.assertTrue(res.startsWith("2=" + URLEncoder.encode(value2).replace("+", "%20")));
            } else if (res.startsWith("3")) {
                Assert.assertTrue(res.startsWith("3=" + URLEncoder.encode(value3)));
            }
        }

    }

    @AfterClass(alwaysRun = true)
    public void afterClass() throws Exception {
        cleanup();
    }

    private void uploadSynapseConfig() throws Exception {
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/patchAutomation/url_encoded_form_post_proxy.xml");
    }


}
