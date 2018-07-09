/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.datamapper;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.carbon.esb.datamapper.common.DataMapperIntegrationTest;

import java.io.File;

/**
 * Test cases for github.com/wso2/product-ei/issues/2391
 */
public class NestedElementsWithSameNameTest extends DataMapperIntegrationTest {

    private final String DM_ARTIFACT_ROOT_PATH = "/artifacts/ESB/mediatorconfig/datamapper/nestedElements/";
    private final String DM_REGISTRY_ROOT_PATH = "datamapper/";

    @Test(groups = {"wso2.esb"}, description = "Datamapper : test nested elements with same name")
    public void testNestedElementsWithSameName() throws Exception {
        loadESBConfigurationFromClasspath(DM_ARTIFACT_ROOT_PATH + File.separator + "synapse.xml");
        uploadResourcesToGovernanceRegistry(DM_REGISTRY_ROOT_PATH, DM_ARTIFACT_ROOT_PATH,
                "NestedElementConfig.dmc",
                "NestedElementConfig_inputSchema.json",
                "NestedElementConfig_outputSchema.json");

        String requestMsg = "{\n" +
                            "    \"chartfield\": [ { \n" +
                            "        \"chartfield\": true\n" +
                            "    } ] \n" +
                            "}";

        String response = sendRequest(getApiInvocationURL("sampleNestedElementAPI"), requestMsg, "application/json");
        Assert.assertEquals(response, "<jsonObject><chartfield><chartfield>true</chartfield></chartfield></jsonObject" +
                                      ">", "unexpected response for data-mapper nested element test");
    }
}
