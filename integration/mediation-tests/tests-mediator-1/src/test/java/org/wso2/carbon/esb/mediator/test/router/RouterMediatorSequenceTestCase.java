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

package org.wso2.carbon.esb.mediator.test.router;

import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.activation.DataHandler;
import java.net.URL;

/* Tests different types of sequences in router mediator */

public class RouterMediatorSequenceTestCase extends ESBIntegrationTest {
    private ResourceAdminServiceClient resourceAdminServiceClient;

    @BeforeClass
    public void setEnvironment() throws Exception {
        init();
        resourceAdminServiceClient = new ResourceAdminServiceClient(contextUrls.getBackEndUrl(),
                        getSessionCookie());
    }

    @Test(groups = "wso2.esb", description = "Tests different kinds of sequences in target")
    public void testSequences() throws Exception {
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/router/router_sequence_test.xml");
        URL url =
                new URL("file:///" + getESBResourceLocation() +"/mediatorconfig/router/router_sequence.xml");
        resourceAdminServiceClient.addResource("/_system/governance/sequences/router/routerSequence",
                "application/vnd.wso2.sequence", "configuration",
                setEndpoints(new DataHandler(url)));
        //test for named sequences
        OMElement response =
                axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "WSO2");
        Assert.assertTrue(response.toString().contains("WSO2"));
        //test for sequences in registries
        response=null;
        response =
                axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "IBM");
        Assert.assertTrue(response.toString().contains("IBM"));
    }

    @AfterClass
    public void close() throws Exception {
        resourceAdminServiceClient.deleteResource("/_system/governance/sequences/router/routerSequence");
        super.cleanup();
    }

}


