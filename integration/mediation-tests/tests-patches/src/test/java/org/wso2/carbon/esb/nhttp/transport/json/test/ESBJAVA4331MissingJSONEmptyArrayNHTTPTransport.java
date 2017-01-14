/*
*  Copyright (c) 2015.year, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.nhttp.transport.json.test;


import junit.framework.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;

public class ESBJAVA4331MissingJSONEmptyArrayNHTTPTransport extends ESBIntegrationTest {

    private ServerConfigurationManager serverConfigurationManager;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        serverConfigurationManager =
                new ServerConfigurationManager(new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN));
        serverConfigurationManager.applyConfiguration(new File(getESBResourceLocation() + File.separator
                + "nhttp" + File.separator + "transport" +
                File.separator + "json" + File.separator + "axis2.xml"));
        serverConfigurationManager.applyConfiguration(new File(getESBResourceLocation() + File.separator
                + "nhttp" + File.separator + "transport" +
                File.separator + "json" + File.separator + "synapse.properties"));
        super.init();
        loadESBConfigurationFromClasspath(File.separator + "artifacts" + File.separator + "ESB"
                + File.separator + "nhttp" + File.separator + "transport"
                + File.separator + "json" + File.separator + "synapse_config.xml");

    }

    @Test(groups = "wso2.esb", description = "check whether Backend JSON payload is missing Json empty array elements " +
            "after flowing through NHTTP transport in response path back to client")
    public void testJSONEmptyArrayMissingNHTTPTransport() throws Exception {
        HttpResponse response = HttpRequestUtil.sendGetRequest(getApiInvocationURL("test/testapi1"), null);
        Assert.assertTrue("Backend JSON payload is missing [] in Json empty array units after flowing through NHTTP transport"
                + "in response path back to client", response.getData()
                .contains("\"zeroElementArrayField\": []"));

        response = HttpRequestUtil.sendGetRequest(getApiInvocationURL("test/testapi2"), null);
        Assert.assertTrue(
                "All number fields are not treated equally by Auto primitive function after flowing through NHTTP transport"
                        + "in response path back to client", response.getData().replaceAll("\\s", "").contains(
                        "[{\"numField1\":\"1\"},{\"numField2\":\"2\"},{\"numField3\":\"3\"},{\"numField4\":\"4\"}]"));

        response = HttpRequestUtil.sendGetRequest(getApiInvocationURL("test/testapi3"), null);
        Assert.assertTrue("Backend JSON payload is missing [] in Json single element array units after flowing through NHTTP transport"
                + "in response path back to client", response.getData().replaceAll("\\s", "")
                .contains("\"singleElementArrayField\":[{\"numField1\":\"1\"}]"));

        response = HttpRequestUtil.sendGetRequest(getApiInvocationURL("test/testapi4"), null);
        Assert.assertTrue("Backend JSON payload is missing [] in Json multiple element array units after flowing through NHTTP transport"
                + "in response path back to client", response.getData().replaceAll("\\s", "")
                .contains("\"multipleElementArrayField\":[{\"numField1\":\"1\"},{\"numField2\":\"2\"},"
                        + "{\"numField3\":\"3\"}]"));

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
        serverConfigurationManager.restoreToLastConfiguration();
    }

}
