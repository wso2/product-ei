/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/
package org.wso2.carbon.esb.mediator.test.enrich;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.CloneClient;


import java.io.IOException;

public class EnrichAndDoubleDropIntegrationTestCase extends ESBIntegrationTest {

    private CloneClient cloneClient;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/config11/synapse.xml");
        cloneClient = new CloneClient();

    }

    @Test(groups = {"wso2.esb"}, description = "Enrichment of response message")
    public void enrichMediatorTest() throws IOException {

        String response = cloneClient.getResponse(getProxyServiceURLHttp("SplitAggregateProxy"), "IBM");


        Assert.assertTrue(response.contains("<urn:userName>foo</urn:userName>"), "Header mismatched");
        Assert.assertTrue(response.contains("<urn:password>bar</urn:password>"), "Header mismatched");
        Assert.assertTrue(response.contains("urn:AuthInf"), "Header mismatched");
    }

    @Test(groups = {"wso2.esb"}, description = "including two drop mediators")
    public void dropMediatorTest() throws IOException {
        String response;

        response = cloneClient.getResponse(getProxyServiceURLHttp("SplitAggregateProxy"), "IBM");

    }

    @AfterClass(alwaysRun = true)
    public void closeTestArtifacts() throws Exception {
        try {
            super.cleanup();
        } finally {
            cloneClient.destroy();
            cloneClient = null;
        }

    }


}
