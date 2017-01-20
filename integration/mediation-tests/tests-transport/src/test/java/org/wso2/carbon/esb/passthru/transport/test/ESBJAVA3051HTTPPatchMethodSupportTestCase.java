/**
 *  Copyright (c) 2005-2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.SimpleHttpClient;


import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ESBJAVA3051HTTPPatchMethodSupportTestCase extends ESBIntegrationTest {

    private LogViewerClient logViewer;


    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/http_transport/HTTPPatchMethodSupportTestSynapse.xml");
        logViewer = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
    }


    @Test(groups = "wso2.esb", description = " Checking Listener side for the HTTP Patch method for Proxy services")
    public void testHTTPPatchListenerProxy() throws Exception {
        SimpleHttpClient httpClient = new SimpleHttpClient();
        String requestXML = "<echo>wso2</echo>";
        String requestContentType = "application/xml";
        HttpResponse httpResponse = httpClient.doPatch(contextUrls.getServiceUrl() + "/PatchRespondProxy", null,requestXML ,requestContentType);
        HttpEntity resEntity = httpResponse.getEntity();
        BufferedReader rd = new BufferedReader(new InputStreamReader(resEntity.getContent()));
        String result = "";
        String line;
        while ((line = rd.readLine()) != null) {
            result += line;
        }
        Assert.assertTrue(result.contains("wso2"));

    }

    @Test(groups = "wso2.esb", description = " Checking Listener side for the HTTP Patch method for REST API ")
    public void testHTTPPatchListenerAPI() throws Exception {
        SimpleHttpClient httpClient = new SimpleHttpClient();
        String requestXML = "<echo>wso2</echo>";
        String requestContentType = "application/xml";
        HttpResponse httpResponse = httpClient.doPatch(contextUrls.getServiceUrl().replace("/services", "") + "/test", null,requestXML ,requestContentType);
        HttpEntity resEntity = httpResponse.getEntity();
        BufferedReader rd = new BufferedReader(new InputStreamReader(resEntity.getContent()));
        String result = "";
        String line;
        while ((line = rd.readLine()) != null) {
            result += line;
        }
        Assert.assertTrue(result.contains("wso2"));

    }

    @Test(groups = "wso2.esb", description = " Checking Listener and sender for the HTTP Patch method")
    public void testHTTPPatchSenderListener() throws Exception {
        SimpleHttpClient httpClient = new SimpleHttpClient();
        String requestXML = "<echo>wso2</echo>";
        String requestContentType = "application/xml";
        HttpResponse httpResponse = httpClient.doPatch(contextUrls.getServiceUrl() + "/PatchProxy", null,requestXML ,requestContentType);
        HttpEntity resEntity = httpResponse.getEntity();
        BufferedReader rd = new BufferedReader(new InputStreamReader(resEntity.getContent()));
        String result = "";
        String line;
        while ((line = rd.readLine()) != null) {
            result += line;
        }
        Assert.assertTrue(result.contains("wso2"));

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}
