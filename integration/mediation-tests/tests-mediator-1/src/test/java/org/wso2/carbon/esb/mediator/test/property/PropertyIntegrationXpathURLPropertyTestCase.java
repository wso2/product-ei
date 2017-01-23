/*
*Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.esb.mediator.test.property;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.stub.LogViewerLogViewerException;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertTrue;

/**
 * This class tests the functionality of the XPATHURL property
 */

public class PropertyIntegrationXpathURLPropertyTestCase extends ESBIntegrationTest {

    private static LogViewerClient logViewer;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath
                ("/artifacts/ESB/mediatorconfig/property/XPATH_URL_PROPERTY.xml");
        logViewer = new LogViewerClient(context.getContextUrls().getBackEndUrl(), sessionCookie);

    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        super.cleanup();
    }

    @Test(groups = {"wso2.esb"}, description = "Test getting the  URI element of a request URL")
    public void testXpathURLProperty() throws IOException, XMLStreamException, LogViewerLogViewerException {
        boolean isUri = false;
        logViewer.clearLogs();
        HttpRequestUtil.sendGetRequest(getApiInvocationURL("editing") + "/edit?a=wso2&b=2.4", null);

        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            log.warn("Sleep Inturrupted : logs may not updated with required text");
        }

        String msg = "SYMBOL = wso2, VALUE = 2.4";
        // after sending the message reading the log file
        LogEvent[] logs = logViewer.getAllRemoteSystemLogs();
        for (LogEvent logEvent : logs) {
            String message = logEvent.getMessage();
            if (message.contains(msg)) {
                isUri = true;
                break;
            }
        }

        assertTrue(isUri, "Message expected (SYMBOL = wso2, VALUE = 2.4) Not found");
    }
}
