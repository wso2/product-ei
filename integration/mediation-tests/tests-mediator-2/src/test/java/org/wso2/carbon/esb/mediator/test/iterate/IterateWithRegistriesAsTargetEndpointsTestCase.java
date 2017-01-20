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

package org.wso2.carbon.esb.mediator.test.iterate;

import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import org.wso2.esb.integration.common.clients.endpoint.EndPointAdminClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import java.net.URL;
import java.util.Iterator;

/**
 * Tests iterate mediator that calls endpoints in Governors
 * and configuration registers
 */

public class IterateWithRegistriesAsTargetEndpointsTestCase extends ESBIntegrationTest {

    private IterateClient client;
    private EndPointAdminClient endPointAdminClient;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        client = new IterateClient();
        endPointAdminClient = new EndPointAdminClient(contextUrls.getBackEndUrl(), getSessionCookie());
    }

    @Test(groups = "wso2.esb", description = "Tests for sequence from governors registry ")
    public void testGovernanceEndPoint() throws Exception {
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/iterate/iterate_govners_endpoint.xml");
        URL url =
                new URL("file:///" + getESBResourceLocation() + "/mediatorconfig/iterate/iterateEndpoint.xml");
        endPointAdminClient.addDynamicEndPoint("gov:/myEndpoint/iterateEndpoint", setEndpoints(new DataHandler(url)));
        String response = client.getMultipleResponse(getMainSequenceURL(), "WSO2", 2);
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
        endPointAdminClient.deleteDynamicEndpoint("gov:/myEndpoint/iterateEndpoint");
    }

    @Test(groups = "wso2.esb", description = "Tests for sequence from configuration registry")
    public void testConfigurationEndPoint() throws Exception {
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/iterate/iterate_configuration_endpoint.xml");
        URL url =
                new URL("file:///" + getESBResourceLocation() + "/mediatorconfig/iterate/iterateEndpoint.xml");
        endPointAdminClient.addDynamicEndPoint("conf:/myEndpoint/iterateEndpoint", setEndpoints(new DataHandler(url)));
        String response = client.getMultipleResponse(getMainSequenceURL(), "WSO2", 2);
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
        endPointAdminClient.deleteDynamicEndpoint("conf:/myEndpoint/iterateEndpoint");
    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        client = null;
        endPointAdminClient = null;
        super.cleanup();
    }

}
