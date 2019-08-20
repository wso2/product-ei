/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.esb.mediator.test.script;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
//import org.wso2.carbon.logging.view.stub.LogViewerLogViewerException;
import org.wso2.carbon.logging.view.data.xsd.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import static org.testng.Assert.assertNotNull;
import static org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil.doPost;

/**
 * This test case verifies that, native json operations are supported by Script Mediator.
 */
public class JsonSupportByScriptMediatorTestCase extends ESBIntegrationTest {

    private LogViewerClient logViewerClient;

    @BeforeClass(alwaysRun = true)
    protected void init() throws Exception {
        super.init();
        logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());

    }

    @Test(groups = {"wso2.esb"}, description = "Sending a JSON message Via REST and manipulate with JS")
    public void testSendingPayloadJson() throws Exception {

        Map<String, String> httpHeaders = new HashMap<>();
        httpHeaders.put("Content-Type", "application/json");
        String payload = "{\n" + "\"name\": \"John Doe\",\n" + "\"dob\": \"1990-03-19\",\n" + "\"ssn\": "
                + "\"234-23" + "-525\",\n" + "\"address\": \"California\",\n" + "\"phone\": \"8770586755\",\n"
                + "\"email\":" + " \"johndoe@gmail.com\",\n" + "\"doctor\": \"thomas collins\",\n" + "\"hospital\": "
                + "\"grand oak " + "community hospital\",\n" + "\"cardNo\": \"7844481124110331\",\n"
                + "\"appointment_date\": " + "\"2017-04-02\"\n" + "}";
        HttpResponse response = doPost(new URL(getApiInvocationURL("scriptMediatorNativeJSONSupportAPI")), payload,
                httpHeaders);
        Assert.assertTrue((response.getData().contains("California")), "Response does not contain "
                + "the keyword \"California\". Response: " + response.getData());

    }

    @Test(groups = {"wso2.esb"}, description = "Serialize JSON payload with JS")
    public void testSerializingJson() throws Exception {
        logViewerClient.clearLogs();
        Map<String, String> httpHeaders = new HashMap<>();
        httpHeaders.put("Content-Type", "application/json");
        String payload = "{\n" + "\"name\": \"John Doe\",\n" + "\"dob\": \"1990-03-19\",\n" + "\"ssn\": "
                + "\"234-23" + "-525\",\n" + "\"address\": \"California\",\n" + "\"phone\": \"8770586755\",\n"
                + "\"email\":" + " \"johndoe@gmail.com\",\n" + "\"doctor\": \"thomas collins\",\n" + "\"hospital\": "
                + "\"grand oak " + "community hospital\",\n" + "\"cardNo\": \"7844481124110331\",\n"
                + "\"appointment_date\": " + "\"2017-04-02\"\n" + "}";
        HttpResponse response = doPost(new URL(getApiInvocationURL("scriptMediatorJsStringifyAPI")), payload,
                httpHeaders);
        boolean propertySet;
        assertNotNull(response, "Response message null");
        propertySet = isPropertyContainedInLog("JSON_TEXT = {\"name\":\"John Doe\",\"address\":\"California\","
                + "\"dob\":\"1990-03-19\"}");
        Assert.assertTrue(propertySet, " The serialized json payload is not set as a property ");

    }

    @Test(groups = {"wso2.esb"}, description = "Parsing serialized JSON payload with JS")
    public void testParsingSerializedJson() throws Exception {

        Map<String, String> httpHeaders = new HashMap<>();
        httpHeaders.put("Content-Type", "application/json");
        String payload = "{\n" + "\"name\": \"John Doe\",\n" + "\"dob\": \"1990-03-19\",\n" + "\"ssn\": "
                + "\"234-23" + "-525\",\n" + "\"address\": \"California\",\n" + "\"phone\": \"8770586755\",\n"
                + "\"email\":" + " \"johndoe@gmail.com\",\n" + "\"doctor\": \"thomas collins\",\n" + "\"hospital\": "
                + "\"grand oak " + "community hospital\",\n" + "\"cardNo\": \"7844481124110331\",\n"
                + "\"appointment_date\": " + "\"2017-04-02\"\n" + "}";
        HttpResponse response = doPost(new URL(getApiInvocationURL("scriptMediatorJsParseAPI")), payload,
                httpHeaders);
        Assert.assertTrue((response.getData().contains("California")), "Response does not contain "
                + "the keyword \"California\". Response: " + response.getData());

    }

    @Test(groups = {"wso2.esb"}, description = "Handling null JSON objects with JS")
    public void testHandlingNullJsonObjects() throws Exception {

        Map<String, String> httpHeaders = new HashMap<>();
        httpHeaders.put("Content-Type", "application/json");
        String payload = "{\n" + "\"name\": \"John Doe\",\n" + "\"dob\": \"1990-03-19\",\n" + "\"ssn\": "
                + "\"234-23" + "-525\",\n" + "\"address\": \"California\",\n" + "\"phone\": \"8770586755\",\n"
                + "\"email\":" + " \"johndoe@gmail.com\",\n" + "\"doctor\": \"thomas collins\",\n" + "\"hospital\": "
                + "\"grand oak " + "community hospital\",\n" + "\"cardNo\": \"7844481124110331\",\n"
                + "\"appointment_date\": " + "\"2017-04-02\"\n" + "}";
        HttpResponse response = doPost(new URL(getApiInvocationURL("scriptMediatorJsHandlingNullJsonObjectAPI")),
                payload,
                httpHeaders);
        Assert.assertTrue((response.getData().contains("{}")), "Response does not contain "
                + "the keyword \"{}\". Response: " + response.getData());

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }

    /**
     * This method check whether given property contains in the logs.
     *
     * @param property required property which needs to be validate if exists or not.
     * @return A Boolean
     */
    private boolean isPropertyContainedInLog(String property) throws RemoteException {
        LogEvent[] logs = logViewerClient.getAllRemoteSystemLogs();
        boolean containsProperty = false;
        for (LogEvent logEvent : logs) {
            String message = logEvent.getMessage();
            if (message.contains(property)) {
                containsProperty = true;
                break;
            }
        }
        return containsProperty;
    }
}
