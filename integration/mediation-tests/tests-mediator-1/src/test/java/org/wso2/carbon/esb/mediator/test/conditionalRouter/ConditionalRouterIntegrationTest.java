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
package org.wso2.carbon.esb.mediator.test.conditionalRouter;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.commons.lang.ArrayUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.clients.sequences.SequenceAdminServiceClient;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
//import org.wso2.carbon.automation.core.utils.endpointutils.EsbEndpointSetter;
//import org.wso2.carbon.automation.utils.esb.ArtifactReaderUtil;
//import org.wso2.carbon.automation.utils.esb.StockQuoteClient;
import org.wso2.esb.integration.common.utils.ArtifactReaderUtil;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestConstant;
import org.wso2.esb.integration.common.utils.clients.stockquoteclient.StockQuoteClient;
//import org.wso2.esb.integration.common.utils.clientsutil.EndpointGenerator;

import java.io.File;
import java.net.URL;


public class ConditionalRouterIntegrationTest extends ESBIntegrationTest {

    private String toUrl = null;
    private String mainSeqUrl;
    private SequenceAdminServiceClient sequenceAdminServiceClient;


    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        mainSeqUrl = getMainSequenceURL();
        sequenceAdminServiceClient = new SequenceAdminServiceClient(contextUrls.getBackEndUrl(),
                                                                    getSessionCookie());
        toUrl = getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE);

    }

    /**
     * Test for Conditional Router mediator  - Continue After=TRUE
     * configuration: synapseconfig/conditional_router/synapse1.xml
     *
     * @throws Exception
     */
   /* https://wso2.org/jira/browse/STRATOS-2239*/
    @Test(groups = {"wso2.esb"})
    public void conditionalRouterMediatorWithContinueAfterTrueTest() throws Exception {

        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/filters/conditional_router/synapse1.xml");

        OMElement response = axis2Client.sendSimpleStockQuoteRequest(mainSeqUrl, toUrl, "WSO2");

        Assert.assertTrue(response.toString().contains("GetQuoteResponse"), "GetQuoteResponse not found in response");
        Assert.assertTrue(response.toString().contains("WSO2 Company"), "GetQuoteResponse not found in response");

    }

    /**
     * Test for Conditional Router mediator  - Continue After=FALSE
     * configuration: synapseconfig/conditional_router/synapse2.xml
     *
     * @throws Exception
     */
     /* https://wso2.org/jira/browse/STRATOS-2239*/
    @Test(groups = {"wso2.esb"})
    public void conditionalRouterMediatorWithContinueAfterFalseTest() throws Exception {

        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/filters/conditional_router/synapse2.xml");

        OMElement response = axis2Client.sendSimpleStockQuoteRequest(mainSeqUrl, toUrl, "WSO2");

        Assert.assertTrue(response.toString().contains("GetQuoteResponse"));
        Assert.assertTrue(response.toString().contains("WSO2 Company"));

        OMElement response2 = null;
        try {
            response2 = axis2Client.sendSimpleStockQuoteRequest(mainSeqUrl, null, "WSO2");
            Assert.fail("This Request should throw AxisFault");
        } catch (AxisFault e) {

        }

        Assert.assertEquals(response2, null);

    }

    /**
     * Test for conditional router mediator with multiple conditional routes
     * Test Artifact: Sample 157 - Conditional Router for Routing Messages based on HTTP URL, HTTP Headers and Query Parameters
     * Note: since toUrl is not given, it will take "/services/StockQuoteProxy" (from transport address) as To address by default
     *
     * @throws Exception
     */
    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.PLATFORM,ExecutionEnvironment.PLATFORM})
    @Test(groups = {"wso2.esb"})
    public void conditionalRouterMediatorWithMultiRoutesTest() throws Exception {
        loadSampleESBConfiguration(157);
        StockQuoteClient client1 = new StockQuoteClient();
        StockQuoteClient client2 = new StockQuoteClient();
        StockQuoteClient client3 = new StockQuoteClient();

        client1.addHttpHeader("foo", "bar");

        client2.addHttpHeader("my_custom_header1", "foo1");

        client3.addHttpHeader("my_custom_header2", "bar");
        client3.addHttpHeader("my_custom_header3", "foo");

        OMElement response1 = client1.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("StockQuoteProxy"), null, "WSO2");

        Assert.assertTrue(response1.toString().contains("GetQuoteResponse"));
        Assert.assertTrue(response1.toString().contains("WSO2 Company"));

        OMElement response2 = client2.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("StockQuoteProxy"), null, "WSO2");

        Assert.assertTrue(response2.toString().contains("GetQuoteResponse"));
        Assert.assertTrue(response2.toString().contains("WSO2 Company"));

        OMElement response3 = client3.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("StockQuoteProxy") + "?qparam1=qpv_foo&qparam2=qpv_foo2", null, "WSO2");

        Assert.assertTrue(response3.toString().contains("GetQuoteResponse"));
        Assert.assertTrue(response3.toString().contains("WSO2 Company"));

    }

    /**
     * Test for conditional mediator with 2 conditional-routes with first one break route=true
     * Test artifact: synapseconfig/conditional_router/synapse3.xml
     *
     * @throws Exception
     */
    @Test(groups = {"wso2.esb"})
    public void conditionalRouterMediatorWithBreakRouteTrueTest() throws Exception {

        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/filters/conditional_router/synapse3.xml");
        StockQuoteClient client = new StockQuoteClient();

        client.addHttpHeader("foo", "bar");
        OMElement response = null;

        try {
            response = client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("StockQuoteProxy1"), null, "WSO2");
            Assert.fail("This Request Should throw AxisFault");
        } catch (AxisFault e) {

        }

        Assert.assertEquals(response, null);

    }

    /**
     * Test for conditional mediator with 2 conditional-routes with first one break route=false
     * Test artifact: synapseconfig/conditional_router/synapse3.xml
     *
     * @throws Exception
     */
    @Test(groups = {"wso2.esb"}, enabled = false)
    public void conditionalRouterMediatorWithBreakRouteFalseTest() throws Exception {
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/filters/conditional_router/synapse3.xml");
        StockQuoteClient client = new StockQuoteClient();

        client.addHttpHeader("foo", "bar");

        OMElement response = client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("StockQuoteProxy2"), null, "WSO2");


        Assert.assertTrue(response.toString().contains("GetQuoteResponse"));
        Assert.assertTrue(response.toString().contains("WSO2 Company"));

    }

    /**
     * The test is for dynamically load a xml file from WSO2registry
     * Test artifacts: synapseconfig/conditional_router/synapse4.xml AND synapseconfig/conditional_router/dynamic_seq1.xml
     *
     * @throws Exception
     */
    @Test(groups = {"wso2.esb"})
    public void conditionalRouterMediatorWithDynamicSequenceTest() throws Exception {


        ArtifactReaderUtil artifactReaderUtil = new ArtifactReaderUtil();

        // Get an OMElement from xml
        OMElement omElement = artifactReaderUtil.getOMElement(getESBResourceLocation() + File.separator
                                                              + "synapseconfig" + File.separator
                                                              + "filters" + File.separator
                                                              + "conditional_router" + File.separator + "dynamic_seq1.xml");


        // Add dynamic sequence to WSO2registry    config/filters/dynamic_seq1
        if (ArrayUtils.contains(sequenceAdminServiceClient.getDynamicSequences(), "conf:/filters/dynamic_seq1")) {
            sequenceAdminServiceClient.deleteDynamicSequence("conf:/filters/dynamic_seq1");
            for (int i = 0; i < 5; i++) {
                if (!ArrayUtils.contains(sequenceAdminServiceClient.getDynamicSequences(), "conf:/filters/dynamic_seq1")) {
                    break;
                }
                Thread.sleep(500);
            }
        }
        sequenceAdminServiceClient.addDynamicSequence("conf:filters/dynamic_seq1", setEndpoints(omElement));
        //load it to esb
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/filters/conditional_router/synapse4.xml");

        OMElement response = axis2Client.sendSimpleStockQuoteRequest(mainSeqUrl, toUrl, "WSO2");

        Assert.assertTrue(response.toString().contains("GetQuoteResponse"));
        Assert.assertTrue(response.toString().contains("WSO2 Company"));

        sequenceAdminServiceClient.deleteDynamicSequence("conf:/filters/dynamic_seq1");

    }

    /**
     * This test is to test large number of conditional routes in a conditional router
     * and this will also tests for Specify match element with param type (<match type="param") test scenario
     * Test artifact:  synapseconfig/conditional_router/synapse5.xml
     *
     * @throws Exception
     */
    @Test(groups = {"wso2.esb"})
    public void conditionalRouterMediatorWithManyRoutesTest() throws Exception {
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/filters/conditional_router/synapse5.xml");
        StockQuoteClient client = new StockQuoteClient();
        // Note: toUrl is set to null -
        OMElement response = client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("conditionalRouterWithManyRoutesProxy")
                                                                + "?qparam1=qpv_foo", null, "WSO2");


        Assert.assertTrue(response.toString().contains("GetQuoteResponse"), "Response message mismatched");
        Assert.assertTrue(response.toString().contains("WSO2 Company"), "Requested Symbol not found in response");

    }

    /**
     * This test is to test 'Specify equal element with url type (<equal type="url")' - a negative test
     * Test artifact: synapseconfig/conditional_router/synapse6.xml  - ServiceProxy1
     *
     * @throws Exception
     */
    //disabled since it is negative
    @Test(groups = {"wso2.esb"}, enabled = false)
    public void conditionalRouterMediatorWithEqualUrlTest() throws Exception {
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/filters/conditional_router/synapse6.xml");
        StockQuoteClient client = new StockQuoteClient();

        OMElement response = null;
        try {
            response = client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("ServiceProxy1")
                                                          + "?qparam1=qpv_foo", null, "WSO2");
            Assert.fail("This Request Should throw AxisFault");
        } catch (AxisFault e) {
        }

        Assert.assertNull(response);

    }

    /**
     * This test is to test 'Specify equal element with header type (<equal type="header")' - a negative test
     * Test artifact: synapseconfig/conditional_router/synapse6.xml - ServiceProxy2
     *
     * @throws Exception
     */
    //disabled since it is negative
    @Test(groups = {"wso2.esb"}, enabled = false)
    public void conditionalRouterMediatorWithEqualHeaderTest() throws Exception {
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/filters/conditional_router/synapse6.xml");
        StockQuoteClient client = new StockQuoteClient();

        client.addHttpHeader("foo", "bar");

        OMElement response = null;
        try {
            response = client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("ServiceProxy2")
                                                          + "?qparam1=qpv_foo", null, "WSO2");
            Assert.fail("This Request Should throw AxisFault");
        } catch (AxisFault e) {
        }

        Assert.assertNull(response);

    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        sequenceAdminServiceClient = null;
        mainSeqUrl = null;
        toUrl = null;
        super.cleanup();
    }
}
