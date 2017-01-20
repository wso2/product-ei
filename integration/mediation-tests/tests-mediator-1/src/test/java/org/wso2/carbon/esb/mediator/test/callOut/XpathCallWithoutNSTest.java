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
package org.wso2.carbon.esb.mediator.test.callOut;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;


public class XpathCallWithoutNSTest extends ESBIntegrationTest {

    private String proxyServiceName = "SplitAggregateProxy";

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/config10/synapse.xml");


    }

    @Test(expectedExceptions = AxisFault.class, groups = {"wso2.esb"}, description = "Sample 750 Call Template Test")
    public void test() throws AxisFault {

        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp(proxyServiceName), null, "IBM");
        Assert.assertNotNull(response, "Response message is null");
        Assert.assertTrue(response.toString().contains("CheckPriceResponse"));
        Assert.assertTrue(response.toString().contains("Price"));
        Assert.assertTrue(response.toString().contains("Code"));

    }

    @AfterClass(alwaysRun = true)
    public void closeTestArtifacts() throws Exception {
        super.cleanup();
    }


}
