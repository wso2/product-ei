/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.ei.scenario.test;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.esb.scenario.test.common.ScenarioConstants;
import org.wso2.carbon.esb.scenario.test.common.http.HTTPUtils;
import org.wso2.carbon.esb.scenario.test.common.ScenarioTestBase;

import java.io.IOException;
import javax.xml.stream.XMLStreamException;

/**
 * This class is to test if xml payload can be enriched before it goes to the backend server by removing elements in
 * various ways, such as removing elements completely, removing contents of elements and etc. They can be
 * achieved using script mediator, xslt mediator and enrich mediator.
 */
public class RemoveElementsTest extends ScenarioTestBase {

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
    }

    //This test is to verify if payload can be modified by removing an element using script mediator.
    @Test(description = "1.6.4.1")
    public void removeElementsUsingScriptMediator() throws IOException, XMLStreamException {
        String url = getProxyServiceURLHttp("1_6_4_1_Proxy_RemoveElements_ScriptMediator");

        String request =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                + "   <soapenv:Header />\n"
                + "   <soapenv:Body>\n"
                + "      <company>WSO2</company>\n"
                + "      <first_name>Jay</first_name>\n"
                + "      <last_name>Cleark</last_name>\n"
                + "   </soapenv:Body>\n"
                + "</soapenv:Envelope>";

        String expectedResponse =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                + "   <soapenv:Header />\n"
                + "   <soapenv:Body>\n"
                + "      <first_name>Jay</first_name>\n"
                + "      <last_name>Cleark</last_name>\n"
                + "   </soapenv:Body>\n"
                + "</soapenv:Envelope>";

        HTTPUtils.invokeSoapActionAndAssert(url, request, ScenarioConstants.MESSAGE_ID, expectedResponse, 200,
                "urn:mediate", "RemoveElementsUsingScriptMediator");
    }

    //This test is to verify if payload can be modified by removing content of an element using enrich mediator.
    @Test(description = "1.6.4.2")
    public void removeContentOfElementUsingEnrichMediator() throws IOException, XMLStreamException {
        String url = getProxyServiceURLHttp("1_6_4_2_Proxy_RemoveContentofElements_EnrichMediator");

        String request =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                + "   <soapenv:Header />\n"
                + "   <soapenv:Body>\n"
                + "      <BANK_TR>\n"
                + "         <TIMESTAMP>1437038356</TIMESTAMP>\n"
                + "         <TRANSACTION_ID>TR10035918373588</TRANSACTION_ID>\n"
                + "         <TRANSACTION_TYPE>ONLINE</TRANSACTION_TYPE>\n"
                + "         <BANKCODE>BNK001</BANKCODE>\n"
                + "         <PROCESSED>TRUE</PROCESSED>\n"
                + "      </BANK_TR>\n"
                + "   </soapenv:Body>\n"
                + "</soapenv:Envelope>";

        String expectedResponse =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                        + "<soapenv:Body>\n"
                        + "      <BANK_TR>\n"
                        + "         <TIMESTAMP>1437038356</TIMESTAMP>\n"
                        + "         <TRANSACTION_ID>TR10035918373588</TRANSACTION_ID>\n"
                        + "         <TRANSACTION_TYPE>ONLINE</TRANSACTION_TYPE>\n"
                        + "         <BANKCODE>BNK001</BANKCODE>\n"
                        + "         <PROCESSED/>\n"
                        + "      </BANK_TR>\n"
                        + "   </soapenv:Body>"
                        + "</soapenv:Envelope>";

        HTTPUtils.invokeSoapActionAndAssert(url, request, ScenarioConstants.MESSAGE_ID, expectedResponse, 200,
                "urn:mediate", "RemoveContentOfElementUsingEnrichMediator");
    }

    /**
     * This test is to verify if payload can be modified by removing
     * content of an element using xslt mediator.
     */
    @Test(description = "1.6.4.3")
    public void removeContentOfElementUsingXsltMediator() throws IOException, XMLStreamException {
        String url = getProxyServiceURLHttp("1_6_4_3_Proxy_removeElementUsingXsltMediator");
        String testCaseId = "1.6.4.3";
        String request =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                        + "   <soap:Header />\n"
                        + "   <soap:Body>\n"
                        + "      <Employee>\n"
                        + "         <firstName>Isuru</firstName>\n"
                        + "         <lastName>Uyanage</lastName>\n"
                        + "         <city>Colombo</city>\n"
                        + "         <preferredName>Isuru</preferredName>\n"
                        + "      </Employee>\n"
                        + "   </soap:Body>\n"
                        + "</soap:Envelope>";

        String expectedResponse =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                        + "   <soap:Header />\n"
                        + "   <soap:Body>\n"
                        + "      <Employee>\n"
                        + "         <firstName>Isuru</firstName>\n"
                        + "         <lastName>Uyanage</lastName>\n"
                        + "         <city>Colombo</city>\n"
                        + "      </Employee>\n"
                        + "   </soap:Body>\n"
                        + "</soap:Envelope>";

        HTTPUtils.invokeSoapActionAndAssert(url, request, testCaseId, expectedResponse, 200,
                "urn:mediate", "removeContentOfElementUsingXsltMediator");
    }

    @AfterClass(description = "Server Cleanup", alwaysRun = true)
    public void cleanup() throws Exception {
        super.cleanup();
    }

}

