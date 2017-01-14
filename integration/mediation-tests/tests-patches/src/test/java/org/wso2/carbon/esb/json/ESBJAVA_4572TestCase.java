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

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.http.HttpResponse;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.SimpleHttpClient;

import java.io.File;


public class ESBJAVA_4572TestCase extends ESBIntegrationTest {
    private ServerConfigurationManager serverConfigurationManager;
    private final SimpleHttpClient httpClient = new SimpleHttpClient();

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        serverConfigurationManager = new ServerConfigurationManager(context);
        serverConfigurationManager.applyConfiguration(new File(getESBResourceLocation() + File.separator
                                                               + "json" + File.separator + "synapse.properties"));
        super.init();
        loadESBConfigurationFromClasspath("artifacts" + File.separator + "ESB" + File.separator +
                                          "json" + File.separator + "TestApi.xml");


    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.ALL})
    @Test(groups = "wso2.esb", description = "disabling auto primitive option in synapse properties ", enabled = false)
    public void testDisablingAutoConversionToScientificNotationInJsonStreamFormatter() throws Exception {
        String payload =
                   "{\"state\":[{\"path\":\"user_programs_progress\",\"entry\":" +
                   "[{\"value\":\"false\",\"key\":\"testJson14\"}]}]}";

        HttpResponse response = httpClient.doPost("http://localhost:8280/abc/dd",
                                                  null, payload, "application/json");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        response.getEntity().writeTo(bos);
        String exPayload = new String(bos.toByteArray());
        String val = "{\"state\":[{\"path\":\"user_programs_progress\",\"entry\":" +
                     "[{\"value\":\"false\",\"key\":\"testJson14\"}]}]}";
        Assert.assertEquals(val, exPayload);
    }


    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
        serverConfigurationManager.restoreToLastConfiguration();
    }
}
