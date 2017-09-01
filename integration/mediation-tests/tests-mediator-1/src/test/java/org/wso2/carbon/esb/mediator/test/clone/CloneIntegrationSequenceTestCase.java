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
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import java.net.URL;
import java.util.Iterator;

/*
 * This tests tests endpoints from governors registry and configuration registry
 * for the clone mediator
 */

public class CloneIntegrationSequenceTestCase extends ESBIntegrationTest {

    private ResourceAdminServiceClient resourceAdminServiceClient;
    private CloneClient client;

    @BeforeClass(groups = "wso2.esb")
    public void setEnvironment() throws Exception {
        init();
        client = new CloneClient();
        resourceAdminServiceClient = new ResourceAdminServiceClient(contextUrls.getBackEndUrl(), getSessionCookie());
        URL url = new URL("file:///" + getESBResourceLocation() + "/mediatorconfig/clone/cloneLogAndSendSequence.xml");
        resourceAdminServiceClient.addResource("/_system/governance/sequences/clone/cloneLogAndSendSequence",
                                               "application/vnd.wso2.sequence", "configuration",
                                               setEndpoints(new DataHandler(url)));
        resourceAdminServiceClient.addResource("/_system/config/sequences/clone/cloneLogAndSendSequence",
                                               "application/vnd.wso2.sequence", "configuration",
                                               setEndpoints(new DataHandler(url)));
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/clone/clone_sequence.xml");
    }

    @Test(groups = "wso2.esb", description = "Tests SEQUENCES from  the governance registry and configuration registry")
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
        Assert.assertEquals(i, 2, "Child Element count mismatched");

    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        resourceAdminServiceClient.deleteResource("/_system/governance/sequences/clone/cloneLogAndSendSequence");
        resourceAdminServiceClient.deleteResource("/_system/config/sequences/clone/cloneLogAndSendSequence");
        resourceAdminServiceClient = null;
        client.destroy();
        client = null;
        super.cleanup();
    }

}
