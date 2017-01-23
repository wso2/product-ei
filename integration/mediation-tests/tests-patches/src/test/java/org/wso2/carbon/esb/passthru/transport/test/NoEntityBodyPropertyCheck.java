/*
*  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/


package org.wso2.carbon.esb.passthru.transport.test;


import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.http.HttpResponse;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.extensions.servers.httpserver.SimpleHttpClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;


public class NoEntityBodyPropertyCheck extends ESBIntegrationTest {
    private final SimpleHttpClient httpClient = new SimpleHttpClient();


    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/passthru/transport/property/Test.xml");
    }


    @Test(groups = "wso2.esb", description = " Checking NO_ENTITY_BODY_PROPERTY", enabled = true)
    public void testNoEntityBodyPropertyTestCase() throws Exception {
        String payload =
                   "      <Person>" +
                   "         <ID>12999E105</ID>" +
                   "      </Person>";

        HttpResponse response = httpClient.doPost("http://localhost:8480/services/Test",
                                                  null, payload, "application/xml");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        response.getEntity().writeTo(bos);
        String exPayload = new String(bos.toByteArray());
        Assert.assertEquals("", exPayload);
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}
