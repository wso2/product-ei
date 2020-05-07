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
import org.wso2.carbon.esb.scenario.test.common.http.HTTPUtils;
import org.wso2.carbon.esb.scenario.test.common.ScenarioTestBase;

import java.io.IOException;
import javax.xml.stream.XMLStreamException;

/**
 * This class is to test if xml payload can be enriched before it goes to the backend server
 * by using script mediator.
 **/
public class AlterRequestWithScriptTest extends ScenarioTestBase {

     private  static final String REQUEST_1_6_10 =
             "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                     + "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                     + "   <soap:Header />\n"
                     + "   <soap:Body>\n"
                     + "      <employees>\n"
                     + "         <employee>\n"
                     + "            <age>25</age>\n"
                     + "            <firstName>John</firstName>\n"
                     + "            <lastName>Doe</lastName>\n"
                     + "         </employee>\n"
                     + "         <employee>\n"
                     + "            <age>45</age>\n"
                     + "            <firstName>Anna</firstName>\n"
                     + "            <lastName>Smith</lastName>\n"
                     + "         </employee>\n"
                     + "         <employee>\n"
                     + "            <age>35</age>\n"
                     + "            <firstName>Peter</firstName>\n"
                     + "            <lastName>Jones</lastName>\n"
                     + "         </employee>\n"
                     + "      </employees>\n"
                     + "   </soap:Body>\n"
                     + "</soap:Envelope>";

     @BeforeClass
     public void init() throws Exception {
        super.init();
     }

     /**
      * This test is to verify if payload can be modified by removing first element using inline groovy script.
      * This testcase has been disabled since we need to manually paste the groovy-all-dependency jar
      * into $EI_HOME/dropins.
      */
    @Test(description = "1.6.10.1")
    public void alterPayloadByInlineGroovyScript() throws IOException, XMLStreamException {
        String url = getProxyServiceURLHttp("1_6_10_1_Proxy_AlterPayloadWithInlineGroovyScript");
        String testCaseID = "1.6.10.1";
        String expectedResponse =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                        + "   <soap:Header />\n"
                        + "   <soap:Body>\n"
                        + "      <employees>\n"
                        + "         <employee>\n"
                        + "            <age>45</age>\n"
                        + "            <firstName>Anna</firstName>\n"
                        + "            <lastName>Smith</lastName>\n"
                        + "         </employee>\n"
                        + "         <employee>\n"
                        + "            <age>35</age>\n"
                        + "            <firstName>Peter</firstName>\n"
                        + "            <lastName>Jones</lastName>\n"
                        + "         </employee>\n"
                        + "      </employees>\n"
                        + "   </soap:Body>\n"
                        + "</soap:Envelope>";

        log.info("Prior to invoking backend " + url + " in alterPayloadByInlineGroovyScriptTestCase ");
        HTTPUtils.invokeSoapActionAndAssert(url, REQUEST_1_6_10, testCaseID, expectedResponse, 200,
                "urn:mediate", "alterPayloadByInlineGroovyScript", 60000,
                60000);
        log.info("After invoking backend " + url + " in alterPayloadByInlineGroovyScriptTestCase ");
    }

     //This test is to verify if payload can be modified by removing the last element using inline javascript.
     @Test(description = "1.6.10.2")
     public void alterPayloadByInlineJavaScript() throws IOException, XMLStreamException {
         String url = getProxyServiceURLHttp("1_6_10_2_Proxy_alterPayloadWithInlineJavaScript");
         String testCaseID = "1.6.10.2";
         String expectedResponse =
                 "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                         + "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                         + "   <soap:Header />\n"
                         + "   <soap:Body>\n"
                         + "      <employees>\n"
                         + "         <employee>\n"
                         + "            <age>25</age>\n"
                         + "            <firstName>John</firstName>\n"
                         + "            <lastName>Doe</lastName>\n"
                         + "         </employee>\n"
                         + "         <employee>\n"
                         + "            <age>45</age>\n"
                         + "            <firstName>Anna</firstName>\n"
                         + "            <lastName>Smith</lastName>\n"
                         + "         </employee>\n"
                         + "      </employees>\n"
                         + "   </soap:Body>\n"
                         + "</soap:Envelope>";

         HTTPUtils.invokeSoapActionAndAssert(url, REQUEST_1_6_10, testCaseID, expectedResponse, 200,
                 "urn:mediate", "alterPayloadByInlineJavaScript");
     }

    @AfterClass(description = "Server Cleanup", alwaysRun = true)
    public void cleanup() throws Exception {
        super.cleanup();
    }

}

