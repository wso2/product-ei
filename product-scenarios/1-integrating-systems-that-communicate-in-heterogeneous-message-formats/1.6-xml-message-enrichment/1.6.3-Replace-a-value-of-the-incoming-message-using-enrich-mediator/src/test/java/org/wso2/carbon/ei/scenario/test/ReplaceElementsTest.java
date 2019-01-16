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

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.esb.scenario.test.common.ScenarioConstants;
import org.wso2.carbon.esb.scenario.test.common.ScenarioTestBase;
import org.wso2.carbon.esb.scenario.test.common.http.HTTPUtils;

import java.io.IOException;
import javax.xml.stream.XMLStreamException;

/**
 * This class is to test if xml payload can be enriched before it goes to the backend server by replacing elements
 * in the payload. This can be done using enrich mediator. This class focusses on various ways of achieving that.
 */
public class ReplaceElementsTest extends ScenarioTestBase {

    @BeforeClass
    public void init() throws Exception {
        super.init();
    }

    //This test is to verify if payload can be modified by replacing the body of payload using a payload stored in a property
    @Test(description = "1.6.3.1 - Replacing body of payload using a payload stored in a property")
    public void ReplaceMessageBodyUsingPayloadStoredInProperty() throws IOException, XMLStreamException {
        String url = getProxyServiceURLHttp("1_6_3_1_Proxy_replace_messageBody_usingPayloadStoredInProperty");
        String testCaseID = "1.6.3.1";
        String expectedResponse =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sam=\"http://sample.wso2.org\" xmlns:xsd=\"http://sample.wso2.org/xsd\">\n"
                        + "   <soapenv:Body>\n"
                        + "      <sam:order>\n"
                        + "         <xsd:price>12</xsd:price>\n"
                        + "         <xsd:productid>IC002</xsd:productid>\n"
                        + "         <xsd:quantity>2</xsd:quantity>\n"
                        + "         <xsd:reference>ref</xsd:reference>\n"
                        + "      </sam:order>\n"
                        + "   </soapenv:Body>\n"
                        + "</soapenv:Envelope>";

        HTTPUtils.invokeSoapActionAndAssert(url, ScenarioConstants.SIMPLE_QUOTE_PLACEORDER, testCaseID, expectedResponse, 200,
                "urn:mediate", "ReplaceMessageBodyUsingPayloadStoredInProperty");
    }

    //This test is to verify if payload can be modified by replacing the target message defined through xpath by source body
    @Test(description = "1.6.3.2 - Replacing target message defined through xpath by source body")
    public void ReplaceTargetBySourceBodyDefinedThoughXpath() throws IOException, XMLStreamException {
        String url = getProxyServiceURLHttp("1_6_3_2_Proxy_replace_targetBySourceBodyDefinedThroughXpath");
        String testCaseID = "1.6.3.2";
        String expectedResponse =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sam=\"http://sample.wso2.org\" xmlns:xsd=\"http://sample.wso2.org/xsd\">\n"
                        + "   <soapenv:Body>\n"
                        + "      <sam:placeOrder>\n"
                        + "         <sam:order>\n"
                        + "            <sam:placeOrder>\n"
                        + "               <sam:order>\n"
                        + "                  <xsd:price>12</xsd:price>\n"
                        + "                  <xsd:productid>IC002</xsd:productid>\n"
                        + "                  <xsd:quantity>2</xsd:quantity>\n"
                        + "                  <xsd:reference>ref</xsd:reference>\n"
                        + "               </sam:order>\n"
                        + "            </sam:placeOrder>\n"
                        + "            <xsd:productid>IC002</xsd:productid>\n"
                        + "            <xsd:quantity>2</xsd:quantity>\n"
                        + "            <xsd:reference>ref</xsd:reference>\n"
                        + "         </sam:order>\n"
                        + "      </sam:placeOrder>\n"
                        + "   </soapenv:Body>\n"
                        + "</soapenv:Envelope>";

        HTTPUtils.invokeSoapActionAndAssert(url, ScenarioConstants.SIMPLE_QUOTE_PLACEORDER, testCaseID, expectedResponse, 200,
                "urn:mediate", "ReplaceTargetBySourceBodyDefinedThoughXpath");
    }

    //This test is to verify if payload can be modified by replacing the target message defined through xpath by source property
    @Test(description = "1.6.3.3 - Replacing target message defined through xpath by source property")
    public void ReplaceTargetDefinedThroughXpathBySourceProperty() throws IOException, XMLStreamException {
        String url = getProxyServiceURLHttp("1_6_3_3_Proxy_replace_targetDefinedThroughXpathBySourceProperty");
        String testCaseID = "1.6.3.3";
        String expectedResponse =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sam=\"http://sample.wso2.org\" xmlns:xsd=\"http://sample.wso2.org/xsd\">\n"
                        + "   <soapenv:Body>\n"
                        + "      <sam:placeOrder>\n"
                        + "         <xsd:price>12</xsd:price>\n"
                        + "      </sam:placeOrder>\n"
                        + "   </soapenv:Body>\n"
                        + "</soapenv:Envelope>";

        HTTPUtils.invokeSoapActionAndAssert(url, ScenarioConstants.SIMPLE_QUOTE_PLACEORDER, testCaseID, expectedResponse, 200,
                "urn:mediate", "ReplaceTargetBySourceBodyDefinedThoughXpath");
    }

    //This test is to verify if payload can be modified by replacing target message defined through xpath by source inline content
    @Test(description = "1.6.3.4 - Replacing target message defined through xpath by source inline content")
    public void ReplaceTargetDefinedThroughXpathBySourceInlineContent() throws IOException, XMLStreamException {
        String url = getProxyServiceURLHttp("1_6_3_4_Proxy_replace_targetDefinedThroughXpathBySourceInlineContent");
        String testCaseID = "1.6.3.4";
        String expectedResponse =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sam=\"http://sample.wso2.org\" xmlns:xsd=\"http://sample.wso2.org/xsd\">\n"
                        + "   <soapenv:Body>\n"
                        + "      <manufacture>Nike</manufacture>\n"
                        + "   </soapenv:Body>\n"
                        + "</soapenv:Envelope>";

        HTTPUtils.invokeSoapActionAndAssert(url, ScenarioConstants.SIMPLE_QUOTE_PLACEORDER, testCaseID, expectedResponse, 200,
                "urn:mediate", "ReplaceTargetDefinedThroughXpathBySourceInlineContent");
    }


    @AfterClass(description = "Server Cleanup",
                alwaysRun = true)
    public void cleanup() throws Exception {
        super.cleanup();
    }

}

