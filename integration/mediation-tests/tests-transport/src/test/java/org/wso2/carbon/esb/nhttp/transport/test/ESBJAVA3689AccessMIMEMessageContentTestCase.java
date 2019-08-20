/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.nhttp.transport.test;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.data.xsd.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.common.ServerConfigurationManager;

import java.io.File;

import static org.testng.Assert.assertTrue;

/**
 * Test case for the access content of MIME messages using nhttp transport
 */
public class ESBJAVA3689AccessMIMEMessageContentTestCase extends ESBIntegrationTest {
    private ServerConfigurationManager serverConfigurationManager;
    private LogViewerClient logViewer;
    private final String API_NAME = "MimeAttachmentAPI";

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        serverConfigurationManager = new ServerConfigurationManager(context);
        serverConfigurationManager.applyConfiguration(new File(getESBResourceLocation() + File.separator
                + "nhttp" + File.separator + "transport" + File.separator + "axis2.xml"));
        super.init();
        verifyAPIExistence(API_NAME);
        logViewer = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
    }

    @Test(groups = "wso2.esb", description = "Access content of MIME in NHTTP transport")
    public void accessMIMEMessages() throws Exception {

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(getApiInvocationURL(API_NAME));

        File fileToUse = new File(getESBResourceLocation() + File.separator
                + "nhttp" + File.separator + "transport" + File.separator + "mime" + File.separator
                + "attachment.xml");
        FileBody data = new FileBody(fileToUse);

        String file_type = "xml";
        String description = "Simple HTTP POST request with an attachment.";

        MultipartEntity reqEntity = new MultipartEntity();
        reqEntity.addPart("file_name", new StringBody(fileToUse.getName()));
        reqEntity.addPart("description", new StringBody(description));
        reqEntity.addPart("file_type", new StringBody(file_type));
        reqEntity.addPart("data", data);

        httppost.setEntity(reqEntity);
        httpclient.execute(httppost);

       
        String expectedMessage = "<soapenv:Body><mediate><file_type>xml</file_type><description>Simple HTTP POST " +
                "request with an attachment.</description><file_name>attachment.xml</file_name><data " +
                "filename=\"attachment.xml\" content-type=\"application/octet-stream\">" +
                "PG1pbWU+CiAgICA8aWQ+MDAwMTwvaWQ+CiAgICA8bmFtZT5NaW1lIGNvbnRlbnQgYWNjZXNzIHRlc3Q8L25hbWU" +
                "+CiAgICA8dG9rZW4" + "+d3NvMl9hY2Nlc3NfMDAxPC90b2tlbj4KPC9taW1lPg==</data></mediate></soapenv:Body"
                + "></soapenv:Envelope>";

        LogEvent[] logs = logViewer.getAllRemoteSystemLogs();
        boolean LogFound = false;
        for (LogEvent log : logs) {
            if (log.getMessage().contains(expectedMessage)) {
                LogFound = true;
                break;
            }
        }
        assertTrue(LogFound, "MIME message build was not successful.");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
        serverConfigurationManager.restoreToLastConfiguration();
    }

}