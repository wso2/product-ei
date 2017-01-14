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
package org.wso2.carbon.esb.proxyservice.test.customProxy;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.registry.resource.stub.ResourceAdminServiceExceptionException;
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPathExpressionException;
import java.net.URL;
import java.rmi.RemoteException;

import static org.testng.Assert.*;

public class OutSequenceFaultSequenceTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        uploadResourcesToConfigRegistry();
        loadESBConfigurationFromClasspath(
                "/artifacts/ESB/proxyconfig/proxy/customProxy/outsequence_faultsequence.xml");

    }

    /**
     * Symbol WSO2 dropped and MSFT is given in out sequence
     *
     * @throws Exception
     */
    @Test(groups = "wso2.esb", description = "- Custom proxy -Out sequence inline")
    public void testCustomProxyOutSequenceInline() throws Exception {

        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("StockQuoteProxyOne"), null, "WSO2");

        String symbol = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "symbol"))
                .getText();
        assertEquals(symbol, "MSFT", "Fault: value 'symbol' mismatched");
    }

    /**
     * Symbol WSO2 dropped and IBM is given in out sequence
     *
     * @throws Exception
     */
    @Test(groups = "wso2.esb", description = "- Custom proxy -Out sequence existing")
    public void testCustomProxyOutSequenceExistingSequence() throws Exception {

        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("StockQuoteProxyTwo"), null, "WSO2");

        String symbol = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "symbol"))
                .getText();
        assertEquals(symbol, "IBM", "Fault: value 'symbol' mismatched");
    }

    /**
     * Symbol IBM dropped and WSO2 is given in out sequence
     *
     * @throws Exception
     */
    @Test(groups = "wso2.esb", description = "- Custom proxy -Out sequence -from registry")
    public void testCustomProxyOutSequenceFromRegistry() throws Exception {

        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("StockQuoteProxyThree"), null, "IBM");

        String symbol = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "symbol"))
                .getText();
        assertEquals(symbol, "WSO2", "Fault: value 'symbol' mismatched");
    }

    /**
     * Invalid service referred
     *
     * @throws Exception
     */
    @Test(groups = "wso2.esb", description = "- Custom proxy -Fault sequence inline")
    public void testCustomProxyFaultInline() throws Exception {

        try {
            axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("StockQuoteProxyFour"), null, "WSO2");
            fail("AxisFault Expected");
        } catch (AxisFault axisFault) {
            assertTrue(axisFault.getReason().contains("Fault sequence invoked"), "Fault: value 'reason' mismatched");
        }

    }

    /**
     * Invalid service referred
     *
     * @throws Exception
     */
    @Test(groups = "wso2.esb", description = "- Custom proxy -Fault sequence From registry")
    public void testCustomProxyFaultFromRegistry() throws Exception {

        try {
            axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("StockQuoteProxyFour"), null, "WSO2");
            fail();
        } catch (AxisFault axisFault) {
            assertTrue(axisFault.getReason().contains("Fault sequence invoked"), "Fault: value 'reason' mismatched");
        }
    }

    /**
     * Invalid service referred
     *
     * @throws Exception
     */
    @Test(groups = "wso2.esb", description = "- Custom proxy -Fault sequence existing fault sequence")
    public void testCustomProxyFaultExistingFaultSequence() throws Exception {

        try {
            axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("StockQuoteProxyFour"), null, "WSO2");
            fail();
        } catch (AxisFault axisFault) {
            assertTrue(axisFault.getReason().contains("Fault sequence invoked"), "Fault: value 'reason' mismatched");
        }
    }


    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        clearUploadedResource();
        super.cleanup();
    }

    private void uploadResourcesToConfigRegistry() throws Exception {

        ResourceAdminServiceClient resourceAdminServiceStub =
                new ResourceAdminServiceClient(context.getContextUrls().getBackEndUrl(), getSessionCookie());

        resourceAdminServiceStub.deleteResource("/_system/config/sequence");
        resourceAdminServiceStub.addCollection("/_system/config/", "sequence", "",
                                               "Contains test sequence files");

        resourceAdminServiceStub.addResource(
                "/_system/config/sequence/out_sequence.xml", "application/vnd.wso2.sequence", "application/vnd.wso2.sequence files",
                new DataHandler(new URL("file:///" + getESBResourceLocation() +
                                        "/proxyconfig/proxy/utils/out_sequence.xml")));

        Thread.sleep(2000);

        resourceAdminServiceStub.addResource(
                "/_system/config/sequence/fault_sequence.xml", "application/vnd.wso2.sequence", "application/vnd.wso2.sequence files",
                new DataHandler(new URL("file:///" + getESBResourceLocation() +
                                        "/proxyconfig/proxy/utils/fault_sequence.xml")));

        Thread.sleep(1000);

    }

    private void clearUploadedResource()
            throws InterruptedException, ResourceAdminServiceExceptionException, RemoteException, XPathExpressionException {

        ResourceAdminServiceClient resourceAdminServiceStub =
                new ResourceAdminServiceClient(context.getContextUrls().getBackEndUrl(), getSessionCookie());

        resourceAdminServiceStub.deleteResource("/_system/config/sequence");

        Thread.sleep(1000);
    }

}
