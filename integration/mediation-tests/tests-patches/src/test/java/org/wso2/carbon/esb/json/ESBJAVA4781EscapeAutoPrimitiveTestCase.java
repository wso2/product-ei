/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.json;

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

public class ESBJAVA4781EscapeAutoPrimitiveTestCase extends ESBIntegrationTest {

  private ServerConfigurationManager serverConfigurationManager;

  @BeforeClass(alwaysRun = true)
  public void setEnvironment() throws Exception {
    super.init();
    serverConfigurationManager =
            new ServerConfigurationManager(new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN));
    serverConfigurationManager.applyConfiguration(new File(getESBResourceLocation() + File.separator
            + "json" + File.separator + "autoprimitive" + File.separator + "synapse.properties"));
    super.init();
    loadESBConfigurationFromClasspath(File.separator + "artifacts" + File.separator + "ESB"
            + File.separator + "json" + File.separator + "autoprimitive" + File.separator
            + "FormatterEscapePrimitiveSequence_synapse_config.xml");

  }

  @Test(groups = "wso2.esb", description = "Check whether JSON field value auto primitive is escaped if field value starting region " +
          "is matched with replace regex after flowing through Staxon formatter in passthrough transport | matched starting region" +
          "will be replaced")
  public void testJSONEmptyArrayMissingNHTTPTransport() throws Exception {
    HttpResponse response = HttpRequestUtil.sendGetRequest(getApiInvocationURL("formatter/escapePrimitive"), null);
    String expected = "{\"testEscapePrimitive\":{\"integer\":1989,\"float\":1989.9,\"null\":null,\"boolean_true\":true," +
            "\"boolean_false\":false,\"string\":\"string\",\"integer_escaped\":\"1989\",\"float_escaped\":\"1989.9\",\"null_escaped\":\"null\"," +
            "\"boolean_true_escaped\":\"true\",\"boolean_false_escaped\":\"false\",\"string_escaped\":\"string\"}}";

    Assert.assertTrue("JSON field value auto primitive has not not been escaped from formatter" +
            " when if field value starting region is matched with replace regex.", expected.equals(response.getData()));

  }

  @AfterClass(alwaysRun = true)
  public void destroy() throws Exception {
    super.cleanup();
    serverConfigurationManager.restoreToLastConfiguration();
  }

}
