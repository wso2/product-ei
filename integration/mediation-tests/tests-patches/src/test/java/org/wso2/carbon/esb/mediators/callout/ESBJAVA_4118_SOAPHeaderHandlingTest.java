/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
package org.wso2.carbon.esb.mediators.callout;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.FileRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.common.TestConfigurationProvider;

import java.io.File;

import static org.testng.Assert.assertFalse;

public class ESBJAVA_4118_SOAPHeaderHandlingTest extends ESBIntegrationTest {

    private LogViewerClient logViewerClient;
    @BeforeClass(alwaysRun = true)
    public void deployService () throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/callout/CalloutMediatorSoapHeaderTest.xml");
        logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
    }

    @Test(groups = "wso2.esb", description = "Test whether the callout mediator successfully handle SOAP messages " +
            "Having SOAP header")
    public void testSOAPHeaderHandling () throws Exception{
        String endpoint = "http://localhost:8480/services/TestCallout";
        String soapRequest = TestConfigurationProvider.getResourceLocation() + "artifacts" + File.separator +
                "ESB" + File.separator + "mediatorconfig" + File.separator + "callout" +
                File.separator + "SOAPRequestWithHeader.xml";
        File input = new File(soapRequest);
        PostMethod post = new PostMethod(endpoint);
        RequestEntity entity = new FileRequestEntity(input, "text/xml");
        post.setRequestEntity(entity);
        post.setRequestHeader("SOAPAction","getQuote");
        HttpClient httpClient = new HttpClient();
        boolean errorLog = false;

        try {
            int result = httpClient.executeMethod(post);
            String responseBody = post.getResponseBodyAsString();
            log.info("Response Status: " + result);
            log.info("Response Body: "+ responseBody);

            LogEvent[] logs = logViewerClient.getAllSystemLogs();
            for (LogEvent logEvent : logs) {
                if (logEvent.getPriority().equals("ERROR")) {
                    String message = logEvent.getMessage();
                    if (message.contains("Unable to convert to SoapHeader Block")) {
                        errorLog = true;
                        break;
                    }
                }
            }
        } finally {
            post.releaseConnection();
        }
        assertFalse(errorLog, "Mediator Hasn't invoked successfully.");
    }

    @AfterClass(alwaysRun = true)
    public void close () throws Exception {
        super.cleanup();
    }
}
