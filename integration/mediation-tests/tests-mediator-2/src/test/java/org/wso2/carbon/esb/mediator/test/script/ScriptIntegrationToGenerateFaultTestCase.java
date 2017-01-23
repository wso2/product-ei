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
package org.wso2.carbon.esb.mediator.test.script;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPFaultDetail;
import org.apache.axis2.AxisFault;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.registry.resource.stub.ResourceAdminServiceExceptionException;
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;
import java.net.URL;
import java.rmi.RemoteException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class ScriptIntegrationToGenerateFaultTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        uploadResourcesToConfigRegistry();
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/script_mediator/synapse_generate_fault.xml");
    }

    //check the fault response using "MSFT" as input .
    @Test(groups = "wso2.esb",
          description = "Using script mediator to generate faults. Test 1.")
    public void testGenerateFaults1() throws XMLStreamException {
        try {

            axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "MSFT");

            Assert.fail("Request must throw a AxisFault");

        } catch (AxisFault e) {


            SOAPFaultDetail faultDetail = e.getFaultDetailElement();
            assertNotNull(faultDetail, "Fault response message null");

            OMElement firstElemnt = faultDetail.getFirstElement();
            OMElement errorCode = firstElemnt.getFirstChildWithName(new QName("ErrorCode"));
            OMElement errorText = firstElemnt.getFirstChildWithName(new QName("ErrorText"));
            assertEquals(faultDetail.getFirstElement().getLocalName(), "AppErrorCode",
                         "Fault detail element");
            assertEquals(errorCode.getText(), "8719",
                         "Fault detail element");
            assertEquals(errorText.getText(), "Issue has",
                         "Fault detail element");

        }
    }


    //check the fault response using "SUN" as input .
    @Test(groups = "wso2.esb",
          description = "Using script mediator to generate faults.Test 2. "
    )
    public void testGenerateFaults2() throws XMLStreamException {
        try {

            axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "SUN");
            Assert.fail("Request must throw a AxisFault");

        } catch (AxisFault e) {

            SOAPFaultDetail faultDetail = e.getFaultDetailElement();
            assertNotNull(faultDetail, "Fault response message null");
            OMElement firstElemnt = faultDetail.getFirstElement();
            OMElement errorCode = firstElemnt.getFirstChildWithName(new QName("ErrorCode"));
            OMElement errorText = firstElemnt.getFirstChildWithName(new QName("ErrorText"));
            assertEquals(faultDetail.getFirstElement().getLocalName(), "AppErrorCode",
                         "Fault detail element");
            assertEquals(errorCode.getText(), "8719",
                         "Fault detail element");
            assertEquals(errorText.getText(), "Issue has",
                         "Fault detail element");


        }
    }


    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        clearUploadedResource();
        super.cleanup();
    }


    private void uploadResourcesToConfigRegistry() throws Exception {

        ResourceAdminServiceClient resourceAdminServiceStub =
                new ResourceAdminServiceClient(contextUrls.getBackEndUrl(), context.getContextTenant().getContextUser().getUserName()
,
                                               context.getContextTenant().getContextUser().getPassword());

        resourceAdminServiceStub.deleteResource("/_system/config/script_js");
        resourceAdminServiceStub.addCollection("/_system/config/", "script_js", "",
                                               "Contains test js files");

        resourceAdminServiceStub.addResource(
                "/_system/config/script_js/detailTransform.js", "application/x-javascript", "js files",
                new DataHandler(new URL("file:///" + getESBResourceLocation() +
                                        "/mediatorconfig/script_js/detailTransform.js")));

    }

    private void clearUploadedResource()
            throws InterruptedException, ResourceAdminServiceExceptionException, RemoteException, XPathExpressionException {

        ResourceAdminServiceClient resourceAdminServiceStub =
                new ResourceAdminServiceClient(contextUrls.getBackEndUrl(), context.getContextTenant().getContextUser().getUserName()
,
                                               context.getContextTenant().getContextUser().getPassword());

        resourceAdminServiceStub.deleteResource("/_system/config/script_js");

    }


}
