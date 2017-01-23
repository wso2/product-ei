/*
 * Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.samples.test.mediation.priorityexecutor;

import junit.framework.Assert;
import org.apache.axiom.om.OMElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;


public class Sample652TestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadSampleESBConfiguration(652);
    }

    @Test(groups = {"wso2.esb"}, description = "Test log mediator")
    public void testSample652SingleRequest() throws Exception {

        axis2Client.addHttpHeader("priority", "1");
        OMElement response = axis2Client.sendSimpleQuoteRequest(
                "http://localhost:8480/services/StockQuoteProxy", null, "IBM");
        Assert.assertNotNull(response);

        axis2Client.clearHttpHeader();
        axis2Client.addHttpHeader("priority", "10");
        OMElement resOmElement = axis2Client.sendSimpleQuoteRequest(
                "http://localhost:8480/services/StockQuoteProxy", null, "MSFT");
        Assert.assertNotNull(resOmElement);
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }


}
