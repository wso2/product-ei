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
package org.wso2.carbon.esb.mediator.test.aggregate;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.net.URL;

public class OnCompleteSequenceFromGreg extends ESBIntegrationTest {
    private ResourceAdminServiceClient resourceAdminServiceStub;
    private AggregatedRequestClient aggregatedRequestClient;
    private final int no_of_requests = 5;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        resourceAdminServiceStub = new ResourceAdminServiceClient
                (contextUrls.getBackEndUrl(),getSessionCookie());
        uploadResourcesToConfigRegistry();
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/onCompleteSequenceFromGreg/synapse.xml");
        aggregatedRequestClient = new AggregatedRequestClient();
        aggregatedRequestClient.setProxyServiceUrl(getProxyServiceURLHttp("aggregateMediatorTestProxy"));
        aggregatedRequestClient.setSymbol("IBM");
        aggregatedRequestClient.setNoOfIterations(no_of_requests);
    }

    @Test(groups = {"wso2.esb"}, description = "pick up a sequence from registry conf on onComplete action of aggregate mediator")
    public void test() throws IOException, XMLStreamException {


        String Response = aggregatedRequestClient.getResponse();
        Assert.assertNotNull(Response, "Response message is null");
        Assert.assertTrue(Response.contains("getQuoteResponse"), "payload factory in registry sequence has not run");
        Assert.assertTrue(Response.contains("responseFromPayloadFactory"), "payload factory in registry sequence has not run");
        Assert.assertTrue(Response.contains("WSO2"), "payload factory in registry sequence has not run");
    }

    private void uploadResourcesToConfigRegistry() throws Exception {
        resourceAdminServiceStub.deleteResource("/_system/governance/sequences");
        resourceAdminServiceStub.addCollection("/_system/governance/", "sequences", "",
                                               "Contains test sequence containing payload factory");
        resourceAdminServiceStub.addResource(
                "/_system/governance/sequences/dynamic_seq1.xml", "application/xml", "xml files",
                new DataHandler(new URL("file:///" + getESBResourceLocation() +
                                        "/synapseconfig/onCompleteSequenceFromGreg/sequences/dynamic_seq1.xml")));
    }


    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        try {
            resourceAdminServiceStub.deleteResource("/_system/governance/sequences");
        } finally {
            super.cleanup();

            resourceAdminServiceStub = null;
            aggregatedRequestClient = null;
        }
    }

}
