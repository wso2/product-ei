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

package org.wso2.carbon.esb.nhttp.transport.test;


import org.apache.http.HttpResponse;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.extensions.servers.httpserver.SimpleHttpClient;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ContentTypeCharsetTestCase extends ESBIntegrationTest {
    private ServerConfigurationManager serverManager;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        serverManager = new ServerConfigurationManager(new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN));
        serverManager.applyConfiguration(new File(getClass().getResource
                ("/artifacts/ESB/nhttp/transport/contenttypecharset/axis2.xml").getPath()));
        super.init();

        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/nhttp_transport"
                                          + "/content_type_charset_synapse.xml");
    }

    @Test(groups = { "wso2.esb" }, description = "Test for charset value proprty in the header response")
    public void testReturnContentType() throws Exception {

        String contentType = "application/xml;charset=UTF-8";
        String charset = "charset";

        SimpleHttpClient httpClient = new SimpleHttpClient();
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("content-type", contentType);
        HttpResponse response = httpClient.doGet(getProxyServiceURLHttp("FooProxy"), headers);
        String contentTypeData = response.getEntity().getContentType().getValue();
        Assert.assertTrue(contentTypeData.contains(charset));

        if (contentTypeData.contains(charset)) {
            String[] pairs = contentTypeData.split(";");
            for (String pair : pairs) {
                if (pair.contains(charset)) {
                    String[] charsetDetails = pair.split("=");
                    Assert.assertTrue(!charsetDetails[1].equals(""));
                }
            }
        }
    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        try {
            cleanup();
        } finally {
            Thread.sleep(3000);
            serverManager.restoreToLastConfiguration();
            serverManager=null;
        }

    }
}
