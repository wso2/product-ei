/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.

 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at

 *      http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.ei.scenario.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.esb.scenario.test.common.ScenarioConstants;
import org.wso2.carbon.esb.scenario.test.common.http.HTTPUtils;
import org.wso2.carbon.esb.scenario.test.common.ScenarioTestBase;
import org.wso2.carbon.esb.scenario.test.common.http.SOAPClient;
import org.wso2.carbon.esb.scenario.test.common.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.stream.XMLStreamException;

/**
 * This test is to verify if payload can be modified by removing an element using script mediator.
 */

public class RemoveElementsTest extends ScenarioTestBase {

    private String sourcesFilePath;
    private static final Log log = LogFactory.getLog(RemoveElementsTest.class);

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        sourcesFilePath = testResourcesDir + File.separator + "source_files";
    }

    @Test(description = "1.6.4-Modify-payload-by-removing-elements-using-script-mediator")
    public void RemoveElementsUsingScriptMediator() throws IOException, XMLStreamException{
        String url = getProxyServiceURLHttp("1_6_4_1_Proxy_RemoveElements_ScriptMediator");
        log.info("url is: " + url);

        String request = FileUtils.readFile(sourcesFilePath + File.separator + "request_1_6_4_1.xml");
        Map<String, String> headers = new HashMap<>(1);
        headers.put(ScenarioConstants.MESSAGE_ID, "1_6_4_1");

        //create a SOAP client and send the payload to proxy
        SOAPClient soapClient = new SOAPClient();
        HTTPUtils httpUtils=new HTTPUtils();

        HttpResponse actualResponse = soapClient.sendSimpleSOAPMessage(url, request, "urn:mediate", headers);
        log.info("actual response is: \n"+actualResponse);

        String Payload = httpUtils.getResponsePayload(actualResponse);
        String actualPayload = Payload.substring(38).replaceAll("\n[ \t]*\n", "\n");
        log.info("Actual enriched payload: "+actualPayload);

        String expectedResponse = FileUtils.readFile(sourcesFilePath + File.separator + "response_1_6_4_1.xml");
        log.info("Expected Response: "+expectedResponse);
        Assert.assertEquals(actualPayload,expectedResponse);
    }

    @AfterClass(description = "Server Cleanup", alwaysRun = true)
    public void cleanup() throws Exception {
        super.cleanup();
    }

}
