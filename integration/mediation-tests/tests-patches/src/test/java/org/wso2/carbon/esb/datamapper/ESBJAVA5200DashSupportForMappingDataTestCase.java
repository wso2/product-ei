/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

public class ESBJAVA5200DashSupportForMappingDataTestCase extends DataMapperIntegrationTest {

    private final String DM_ARTIFACT_ROOT_PATH = "/artifacts/ESB/mediatorconfig/datamapper/dashSupport/";
    private final String DM_REGISTRY_ROOT_PATH = "datamapper/";


    @Test(groups = { "wso2.esb" }, description = "Datamapper : test including dash for mapping data")
    public void testMultiplePrefixesToSameNamespace() throws Exception {
        loadESBConfigurationFromClasspath(DM_ARTIFACT_ROOT_PATH + File.separator + "synapse.xml");
        uploadResourcesToGovernanceRegistry(DM_REGISTRY_ROOT_PATH + "dashSupport/", DM_ARTIFACT_ROOT_PATH,
                "DashSupportConfig.dmc",
                "DashSupportConfig_inputSchema.json",
                "DashSupportConfig_outputSchema.json");

        String requestMsg = "<ns:IPU-58_Output xmlns:ns=\"http://systemeu.fr\">\n" +
                "<ns:InitialisationCodePin>1</ns:InitialisationCodePin>\n" +
                "<ns:CodeRetour>2</ns:CodeRetour>\n" +
                "<ns:NbrSaisiesRestantes>3</ns:NbrSaisiesRestantes>\n" +
                "</ns:IPU-58_Output>";

        String response = sendRequest(getApiInvocationURL("dashSupport"), requestMsg, "text/xml");
        Assert.assertEquals(response, "<root>" +
                "<IPU58_Output>" +
                "<InitialisationCodePin>1</InitialisationCodePin>" +
                "<CodeRetour>2</CodeRetour>" +
                "<NbrSaisiesRestantes>3</NbrSaisiesRestantes>" +
                "</IPU58_Output>" +
                "</root>", "Fail to map data having dashes");

    }
}
