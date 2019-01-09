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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.stream.XMLStreamException;

public class RemoveElementsTest extends ScenarioTestBase {

    private static final Log log = LogFactory.getLog(RemoveElementsTest.class);

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
    }

    //This test is to verify if payload can be modified by removing an element using script mediator.
    @Test(description = "1.6.4.1-Modify-payload-by-removing-elements-using-script-mediator")
    public void RemoveElementsUsingScriptMediator() throws IOException, XMLStreamException {
        String url = getProxyServiceURLHttp("1_6_4_1_Proxy_RemoveElements_ScriptMediator");

        String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                + "    <soapenv:Header />\n" + "    <soapenv:Body>\n" + "        <company>WSO2</company>\n"
                + "        <first_name>Jay</first_name>\n" + "        <last_name>Cleark</last_name>\n"
                + "    </soapenv:Body>\n" + "</soapenv:Envelope>";

        String expectedResponse = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                + "    <soapenv:Header/>\n" + "    <soapenv:Body>\n" + "        <first_name>Jay</first_name>\n"
                + "        <last_name>Cleark</last_name>\n" + "    </soapenv:Body>\n" + "</soapenv:Envelope>";

        Map<String, String> headers = new HashMap<>(1);
        headers.put(ScenarioConstants.MESSAGE_ID, "1_6_4_1");

        //create a SOAP client and send the payload to proxy
        SOAPClient soapClient = new SOAPClient();
        HttpResponse actualResponse = soapClient.sendSimpleSOAPMessage(url, request, "urn:mediate", headers);
        String payload = HTTPUtils.getResponsePayload(actualResponse);
        String actualPayload = payload.substring(38).replaceAll("\n[ \t]*\n", "\n");

        Assert.assertEquals(actualPayload, expectedResponse, "The payload is not properly enriched");
    }

    //This test is to verify if payload can be modified by removing content of an element using enrich mediator.
    @Test(description = "1.6.4.2-Modify-payload-by-removing-content-of-elements-using-enrich-mediator")
    public void RemoveContentOfElementUsingEnrichMediator() throws IOException, XMLStreamException {
        String url = getProxyServiceURLHttp("1_6_4_2_Proxy_RemoveContentofElements_EnrichMediator");

        String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                + "   <soapenv:Header />\n" + "   <soapenv:Body>\n" + "      <BANK_TR>\n"
                + "         <TIMESTAMP>1437038356</TIMESTAMP>\n"
                + "         <TRANSACTION_ID>TR10035918373588</TRANSACTION_ID>\n"
                + "         <TRANSACTION_TYPE>ONLINE</TRANSACTION_TYPE>\n" + "         <BANKCODE>BNK001</BANKCODE>\n"
                + "         <PROCESSED>TRUE</PROCESSED>\n" + "      </BANK_TR>\n" + "   </soapenv:Body>\n"
                + "</soapenv:Envelope>";

        String expectedResponse =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">" + "<soapenv:Body>\n"
                        + "      <BANK_TR>\n" + "         <TIMESTAMP>1437038356</TIMESTAMP>\n"
                        + "         <TRANSACTION_ID>TR10035918373588</TRANSACTION_ID>\n"
                        + "         <TRANSACTION_TYPE>ONLINE</TRANSACTION_TYPE>\n"
                        + "         <BANKCODE>BNK001</BANKCODE>\n" + "         <PROCESSED/>\n" + "      </BANK_TR>\n"
                        + "   </soapenv:Body>" + "</soapenv:Envelope>";
        log.info("Expected Response: " + expectedResponse);

        Map<String, String> headers = new HashMap<>(1);
        headers.put(ScenarioConstants.MESSAGE_ID, "1_6_4_2");

        //create a SOAP client and send the payload to proxy
        SOAPClient soapClient = new SOAPClient();
        HttpResponse actualResponse = soapClient.sendSimpleSOAPMessage(url, request, "urn:mediate", headers);
        String payload = HTTPUtils.getResponsePayload(actualResponse);
        String actualPayload = payload.substring(38);

        Assert.assertEquals(actualPayload, expectedResponse, "The payload is not properly enriched");
    }

    @AfterClass(description = "Server Cleanup",
                alwaysRun = true)
    public void cleanup() throws Exception {
        super.cleanup();
    }

}

