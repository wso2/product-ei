/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
import org.wso2.carbon.automation.extensions.servers.httpserver.SimpleHttpClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.util.HashMap;
import java.util.Map;

/**
 *  This class tests the functionality of the FORCE_ERROR_ON_SOAP_FAULT property
 * TODO -  JIRA : ESBJAVA-3386  , ESBJAVA-3384
 */
public class PropertyIntegrationForceErrorOnSOAPFaultPropertyTestCase extends
                                                                          ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath
                ("/artifacts/ESB/mediatorconfig/property/FORCE_ERROR_ON_SOAP_FAULT.xml");
    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        super.cleanup();
    }

    @Test(groups = "wso2.esb", description = "Test-Without No_ENTITY_BODY Property",enabled = false)
    public void testWithoutOutOnlyPropertyTest() throws Exception {


        SimpleHttpClient httpClient = new SimpleHttpClient();
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "text/xml");
        headers.put("SOAPAction", "urn:mediate");
        String payload = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/" +
                         "envelope/\">\n" +
                         "   <soapenv:Header/>\n" +
                         "   <soapenv:Body/>My Request</soapenv:Envelope>\n" +
                         "</soapenv:Envelope>";
        log.info(payload);
        httpClient.doGet(getProxyServiceURLHttp("MyProxy"), headers);

        // Assert should go here
    }
}
