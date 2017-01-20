/*
*  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.carbon.esb.jms.transport.test;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

public class JMSMessageStoreUnavailableTestCase extends ESBIntegrationTest {
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath(
                "/artifacts/ESB/jms/transport/jms_message_store_unavailable_service.xml");
    }

    @Test(groups = "wso2.esb", description = "Test proxy service with unavailable jms message store")
    public void testCustomProxy() throws Exception {
        OMElement response = null;
        try {
            response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("JMSStoreNotAvailableTestCaseProxy"), null, "WSO2");
            fail("Must throw AxisFault");
        } catch (AxisFault axisFault) {
            assertEquals("Fault sequence invoked", axisFault.getMessage(), "Fault sequence is not called.");
        }
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}
