/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.carbon.esb.json;

import org.apache.http.HttpResponse;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.clients.SimpleHttpClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;

public class ESBJAVA4270TestCase extends ESBIntegrationTest {

    private ServerConfigurationManager serverConfigurationManager;
    private final Map<String, String> headers = new HashMap<String, String>(1);
    private final SimpleHttpClient httpClient = new SimpleHttpClient();
    private static String url = "http://localhost:8480/stockquote/view?Name=MSFT";

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init(TestUserMode.SUPER_TENANT_ADMIN);
        serverConfigurationManager = new ServerConfigurationManager(context);
        serverConfigurationManager.applyConfiguration(new File(getESBResourceLocation() + File.separator +
                "nhttp" + File.separator + "transport" + File.separator + "rest" + File.separator + "axis2.xml"));
        super.init(TestUserMode.SUPER_TENANT_ADMIN);
        loadESBConfigurationFromClasspath("artifacts" + File.separator + "ESB" + File.separator +
                "json" + File.separator + "stock-quote-json.xml");
    }

    @Test(groups = "wso2.esb", description = "Call API with GET request and content type = application/json in NHTTP " +
            "transport")
    public void getRequestContentTypeJSONTest() throws Exception {
        headers.put("Content-Type", "application/json");
        HttpResponse response = httpClient.doGet(url, headers);
        Thread.sleep(2000);
        assertEquals(response.getStatusLine().getStatusCode(), 200);

    }

    @Test(groups = "wso2.esb", description = "Call API with GET request and content type = application/xml in NHTTP " +
            "transport")
    public void getRequestContentTypeXMLTest() throws Exception {
        headers.put("Content-Type", "application/xml");
        HttpResponse response = httpClient.doGet(url, headers);
        Thread.sleep(2000);
        assertEquals(response.getStatusLine().getStatusCode(), 200);

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
        serverConfigurationManager.restoreToLastConfiguration();
        serverConfigurationManager = null;
    }
}
