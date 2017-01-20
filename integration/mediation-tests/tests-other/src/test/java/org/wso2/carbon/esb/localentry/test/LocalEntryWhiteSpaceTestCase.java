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

package org.wso2.carbon.esb.localentry.test;


import org.apache.axiom.om.OMElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.servers.axis2.SampleAxis2Server;

import javax.xml.namespace.QName;

import static org.testng.Assert.assertNotNull;

/**
 * Test case related to ESBJAVA-3101
 */
public class LocalEntryWhiteSpaceTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void deployService() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath
                ("artifacts/ESB/proxyconfig/proxy/proxyservice/localentry_url_proxy.xml");
    }

    @Test(groups = "wso2.esb", description = "Test local entry value retrieval for endpoint url")
    public void testLocalEntryProxy() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest("http://localhost:8480/services/LocalEntryTestProxy", null, "IBM");
        String lastPrice = response.getFirstElement()
                .getFirstChildWithName(new QName("http://services.samples/xsd", "last")).getText();
        assertNotNull(lastPrice, "Fault: response message 'last' price null");
    }

    @AfterClass(alwaysRun = true)
    public void unDeployService() throws Exception {
        super.cleanup();
    }
}
