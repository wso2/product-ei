/*
 * Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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


package org.wso2.carbon.esb.json.test;

import org.apache.http.HttpResponse;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.automation.extensions.servers.httpserver.SimpleHttpClient;
import org.wso2.carbon.endpoint.stub.types.EndpointAdminEndpointAdminException;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

import static org.testng.Assert.assertEquals;

public class JsonTestCase extends ESBIntegrationTest {

    private SimpleHttpClient httpClient;

    String epr ;

    @BeforeClass(alwaysRun = true)
    protected void init() throws Exception {
        super.init();
        epr =this.getProxyServiceURLHttp("Version") ;
        httpClient = new SimpleHttpClient();
    }

    @Test(groups = {"wso2.esb"}, description = "Sending a Message Via REST with empty payload")
    public void testEmptyPayloadJson() throws IOException, EndpointAdminEndpointAdminException,
            LoginAuthenticationExceptionException,
            XMLStreamException {

        String payload = "";
        HttpResponse response = httpClient.doPost(epr,
                null, payload, "application/json");

        assertEquals(response.getStatusLine().getStatusCode(), 200);

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}
