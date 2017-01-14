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
import org.wso2.esb.integration.common.clients.endpoint.EndPointAdminClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.activation.DataHandler;
import java.net.URL;

/**
 * Tests different types of endpoints in router mediator
 */

public class RouterMediatorEndpointsTestCase extends ESBIntegrationTest {
    private EndPointAdminClient endPointAdminClient;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        init();
        endPointAdminClient = new EndPointAdminClient(contextUrls.getBackEndUrl(), getSessionCookie());
    }

    @Test(groups = "wso2.esb", description = "Tests different types of endpoints")
    public void testEndpoints() throws Exception {
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/router/router_endpoint_test.xml");
        URL url =  new URL("file:///" + getESBResourceLocation() + "/mediatorconfig/router/router_endpoint.xml");
        endPointAdminClient.addDynamicEndPoint("gov:/myEndpoint/routerEndpoint", setEndpoints(new DataHandler(url)));

        //Tests with named endpoints
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "WSO2");
        Assert.assertTrue(response.toString().contains("WSO2"));

        //Tests with endpoints in the registry
        response = null;
        response = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "IBM");
        Assert.assertTrue(response.toString().contains("IBM"));
    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        endPointAdminClient.deleteDynamicEndpoint("gov:/myEndpoint/routerEndpoint");
        endPointAdminClient = null;
        super.cleanup();
    }

}


