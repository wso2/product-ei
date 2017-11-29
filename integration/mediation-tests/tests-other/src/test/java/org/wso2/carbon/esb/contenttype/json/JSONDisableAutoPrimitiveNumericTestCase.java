/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.contenttype.json;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.http.HttpResponse;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.SimpleHttpClient;
import org.wso2.esb.integration.common.utils.common.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.common.TestConfigurationProvider;

import java.io.File;

/**
 * This class tests filter mediator behaviour using json path - on JSON request payloads. The following property has
 * been added to synapse.properties file for this test case
 * <p>
 * synapse.commons.json.output.disableAutoPrimitive.regex = ^-?(0|[1-9][0-9]*)(\\.[0-9]+)?([eE][+-]?[0-9]+)?$
 */
public class JSONDisableAutoPrimitiveNumericTestCase extends ESBIntegrationTest {

    private final SimpleHttpClient httpClient = new SimpleHttpClient();
    ServerConfigurationManager serverConfigurationManager;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        serverConfigurationManager = new ServerConfigurationManager(context);
        //Need to apply the configuration here since this changes default synapse properties which could affect other
        // test cases
        serverConfigurationManager.applyConfiguration(new File(
                TestConfigurationProvider.getResourceLocation() + File.separator + "artifacts" + File.separator + "ESB"
                + File.separator + "json" + File.separator + "disableAutoPrimitiveNumeric" + File.separator
                + "synapse.properties"));
        super.init();
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.ALL})
    @Test(groups = "wso2.esb", description = "disabling auto primitive option with a given regex pattern in synapse "
                                             + "properties  ")
    public void testDisablingAutoConversionToScientificNotationInJsonStreamFormatter() throws Exception {
        String payload = "<coordinates>\n"
                         + "   <location>\n"
                         + "       <name>Bermuda Triangle</name>\n"
                         + "       <n>25e1</n>\n"
                         + "       <w>7.1e1</w>\n"
                         + "   </location>\n"
                         + "   <location>\n"
                         + "       <name>Eiffel Tower</name>\n"
                         + "       <n>4.8e3</n>\n"
                         + "       <e>1.8e2</e>\n"
                         + "   </location>\n"
                         + "</coordinates>";
        HttpResponse response = httpClient.doPost(getProxyServiceURLHttp("JSONDisableAutoPrimitiveNumericTestProxy"),
                null, payload, "application/xml");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        response.getEntity().writeTo(bos);
        String actualResult = new String(bos.toByteArray());
        String expectedPayload = "{\"coordinates\":{\"location\":[{\"name\":\"Bermuda Triangle\",\"n\":\"25e1\""
                                 + ",\"w\":\"7.1e1\"},{\"name\":\"Eiffel Tower\",\"n\":\"4.8e3\",\"e\":\"1.8e2\"}]}}";
        Assert.assertEquals(actualResult, expectedPayload);
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
        serverConfigurationManager.restoreToLastConfiguration();
    }
}
