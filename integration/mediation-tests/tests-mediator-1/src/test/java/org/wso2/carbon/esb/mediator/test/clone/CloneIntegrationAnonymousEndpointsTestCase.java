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

package org.wso2.carbon.esb.mediator.test.clone;

import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.servers.axis2.SampleAxis2Server;

import javax.xml.namespace.QName;
import java.util.Iterator;

/*
 * Create a sequence with a clone mediator that calls only 'anonymous' endpoints
 * in the clone target
 */

public class CloneIntegrationAnonymousEndpointsTestCase extends ESBIntegrationTest {

    private SampleAxis2Server axis2Server1;
    private SampleAxis2Server axis2Server2;
    private CloneClient client;

    @BeforeClass()
    public void setEnvironment() throws Exception {
        init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/clone/clone_unknown_endpoints.xml");
        client = new CloneClient();
        axis2Server1 = new SampleAxis2Server("test_axis2_server_9001.xml");
        axis2Server2 = new SampleAxis2Server("test_axis2_server_9002.xml");

        axis2Server1.deployService(SampleAxis2Server.SIMPLE_STOCK_QUOTE_SERVICE);
        axis2Server1.start();
        axis2Server2.deployService(SampleAxis2Server.SIMPLE_STOCK_QUOTE_SERVICE);
        axis2Server2.start();

    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.esb", description = "Tests http address")
    public void testSequence() throws Exception {

        String response = client.getResponse(getMainSequenceURL(), "WSO2");
        Assert.assertNotNull(response);
        OMElement envelope = client.toOMElement(response);
        OMElement soapBody = envelope.getFirstElement();
        Iterator iterator =
                soapBody.getChildrenWithName(new QName("http://services.samples",
                                                       "getQuoteResponse"));
        int i = 0;
        while (iterator.hasNext()) {
            i++;
            OMElement getQuote = (OMElement) iterator.next();
            Assert.assertTrue(getQuote.toString().contains("WSO2"));
        }
        Assert.assertEquals(i , 2, "Child Element count mismatched");
    }

    @AfterClass()
    public void close() throws Exception {
        axis2Server1.stop();
        axis2Server2.stop();
        client.destroy();
        client = null;
        super.cleanup();
        axis2Server1 = null;
        axis2Server2 = null;
    }

}
