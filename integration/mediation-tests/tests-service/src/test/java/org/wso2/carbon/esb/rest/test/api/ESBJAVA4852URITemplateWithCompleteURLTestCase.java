/*
*  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/
package org.wso2.carbon.esb.rest.test.api;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.data.xsd.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;

/**
 * https://wso2.org/jira/browse/ESBJAVA-4852
 * This test cass will test the URITemplate dispatcher when HTTP method having complete url with
 * query parameters.
 */

public class ESBJAVA4852URITemplateWithCompleteURLTestCase extends ESBIntegrationTest {
    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath(File.separator + "artifacts" + File.separator + "ESB"
                                          + File.separator + "synapseconfig" + File.separator + "rest"
                                          + File.separator + "ESBJAVA4852APIConfig.xml");
    }

    @Test(groups = {"wso2.esb"}, description = "Sending complete URL to API and for dispatching")
    public void testCompleteURLWithHTTPMethod() throws Exception {

        DeleteMethod delete = new DeleteMethod(getApiInvocationURL("myApi1/order/21441/item/17440079" +
                                                                   "?message_id=41ec2ec4-e629-4e04-9fdf-c32e97b35bd1"));
        HttpClient httpClient = new HttpClient();
        LogViewerClient logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
        logViewerClient.clearLogs();

        try {
            httpClient.executeMethod(delete);
            Assert.assertEquals(delete.getStatusLine().getStatusCode(), 202, "Response code mismatched");
        } finally {
            delete.releaseConnection();
        }

        LogEvent[] logEvents = logViewerClient.getAllRemoteSystemLogs();
        boolean isLogMessageFound = false;

        for (LogEvent log : logEvents) {
            if (log != null && log.getMessage().contains("order API INVOKED")) {
                isLogMessageFound = true;
                break;
            }
        }
        Assert.assertTrue(isLogMessageFound, "Request Not Dispatched to API when HTTP method having full url");

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}
