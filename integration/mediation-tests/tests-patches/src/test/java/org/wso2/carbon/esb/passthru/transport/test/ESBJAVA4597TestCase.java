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

package org.wso2.carbon.esb.passthru.transport.test;

import junit.framework.Assert;
import org.apache.axiom.om.OMElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

public class ESBJAVA4597TestCase extends ESBIntegrationTest {



    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/passthru/transport/ESBJAVA4597/TestProxy.xml");
        loadESBConfigurationFromClasspath("/artifacts/ESB/passthru/transport/ESBJAVA4597/ReceiveSeq.xml");
        loadESBConfigurationFromClasspath("/artifacts/ESB/passthru/transport/ESBJAVA4597/MyFaultSequency.xml");

    }

    @Test(groups = "wso2.esb",
          description = " Checking response for HEAD request contains a body")
    public void testResponseBodyOfHEADRequest() throws Exception {
        OMElement response = axis2Client.
                sendSimpleStockQuoteRequest("http://localhost:8280/services/TestProxy", null, "IBM");
        Assert.assertNotNull(response);

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}
