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

package org.wso2.carbon.esb.mediator.test.clone;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.common.FileManager;

import java.io.File;

public class CloneIntegrationEndpointsTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void setEnvironmentHTTP() throws Exception {
        init();
    }

    @Test(groups = "wso2.esb", description = "Tests http address")
    public void testHTTP() throws Exception {
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/clone/clone_http.xml");
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "WSO2");

        Assert.assertTrue(response.toString().contains("WSO2"));

    }

    @Test(groups = "wso2.esb", description = "Tests https address")
    public void testHTTPS() throws Exception {
        String sqn = FileManager.readFile(getESBResourceLocation() + File.separator + "mediatorconfig" + File.separator
                + "clone" + File.separator + "clone_https_sequence.xml");
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/clone/clone_https.xml");

        sqn = sqn.replace("httpsEndpoint",getProxyServiceURLHttps("StockQuoteProxy"));
        addSequence(AXIOMUtil.stringToOM(sqn));
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "WSO2");
        Assert.assertTrue(response.toString().contains("WSO2"));

    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        super.cleanup();
    }

}
