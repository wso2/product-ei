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
 *
 */
package org.wso2.carbon.esb.mediator.test.call;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;
import java.net.URL;

import static org.wso2.carbon.esb.mediator.test.call.Util.executeAndAssert;

public class CallMediatorHttpStatusResponseTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void deployService() throws Exception {
        super.init();
        String[] configs = {"HTTPStatusResponseAPI", "AcceptedEndpointProxy", "BadRequestEndpointProxy",
                "ConflictEndpointProxy", "ForbiddenEndpointProxy", "NotFoundEndpointProxy",
                "ServerErrorEndpointProxy", "UnauthorizedEndpointProxy"};
        for (String configName : configs) {
            loadESBConfigurationFromClasspath(File.separator + "artifacts" + File.separator + "ESB"
                    + File.separator + "mediatorconfig" + File.separator + "call" + File.separator
                    + configName + ".xml");
            if(configName.equals("HTTPStatusResponseAPI")){
               verifyAPIExistence(configName);
            } else
                verifyProxyServiceExistence(configName);
        }
    }

    @Test(groups = {"wso2.esb"},
            description = "Test invoking accepted http status endpoint with blocking call", enabled = true)
    public void testAcceptedHttpStatusEndpoint() throws Exception {
        URL endpoint = new URL(getProxyServiceURLHttp("AcceptedEndpointProxy"));
        executeAndAssert(endpoint, 202, "Accepted");
    }


    @Test(groups = {"wso2.esb"},
            description = "Test invoking bad request http status endpoint with blocking call", enabled = true)
    public void testBadRequestHttpStatusEndpoint() throws Exception {
        URL endpoint = new URL(getProxyServiceURLHttp("BadRequestEndpointProxy"));
        executeAndAssert(endpoint, 400, "Bad_Request");
    }

    @Test(groups = {"wso2.esb"},
            description = "Test invoking unauthorized http status endpoint with blocking call", enabled = true)
    public void testUnauthorizedHttpStatusEndpoint() throws Exception {
        URL endpoint = new URL(getProxyServiceURLHttp("UnauthorizedEndpointProxy"));
        executeAndAssert(endpoint, 401, "Unauthorized");
    }

    @Test(groups = {"wso2.esb"},
            description = "Test invoking forbidden http status endpoint with blocking call", enabled = true)
    public void testForbiddenHttpStatusEndpoint() throws Exception {
        URL endpoint = new URL(getProxyServiceURLHttp("ForbiddenEndpointProxy"));
        executeAndAssert(endpoint, 403, "Forbidden");
    }

    @Test(groups = {"wso2.esb"},
            description = "Test invoking not found http status endpoint with blocking call", enabled = true)
    public void testNotFoundHttpStatusEndpoint() throws Exception {
        URL endpoint = new URL(getProxyServiceURLHttp("NotFoundEndpointProxy"));
        executeAndAssert(endpoint, 404, "Not_Found");
    }

    @Test(groups = {"wso2.esb"},
            description = "Test invoking conflict http status endpoint with blocking call", enabled = true)
    public void testConflictHttpStatusEndpoint() throws Exception {
        URL endpoint = new URL(getProxyServiceURLHttp("ConflictEndpointProxy"));
        executeAndAssert(endpoint, 409, "Conflict");
    }


    @Test(groups = {"wso2.esb"},
            description = "Test invoking server error http status endpoint with blocking call", enabled = true)
    public void testServerErrorHttpStatusEndpoint() throws Exception {
        URL endpoint = new URL(getProxyServiceURLHttp("ServerErrorEndpointProxy"));
        executeAndAssert(endpoint, 500, "Internal_Server_Error");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}