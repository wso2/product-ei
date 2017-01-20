package org.wso2.carbon.esb.mediator.test.xquery;

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

import org.apache.axis2.AxisFault;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestConstant;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;

public class XquaryInvaluedKeyTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/configForInvaliedXquaryKey/synapse.xml");

    }


    @Test(groups = {"wso2.esb"}, description = "more number of messages than maximum count")
    public void testXquaryInvaluedKey() throws IOException, XMLStreamException {

        try {
            axis2Client.sendCustomQuoteRequest(getProxyServiceURLHttp("StockQuoteProxy")
                    , null, "IBM");
            Assert.fail("Did not throw expected AxisFaultException with the reason Unable to execute the query");

        } catch (AxisFault fault) {
            Assert.assertTrue(fault.getReason().toString().contains("Unable to execute the query") , "Expected Error message not found");
        }

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}
