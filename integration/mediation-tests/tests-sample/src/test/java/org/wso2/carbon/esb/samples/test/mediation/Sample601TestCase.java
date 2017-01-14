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

package org.wso2.carbon.esb.samples.test.mediation;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestConstant;

import javax.xml.namespace.QName;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class Sample601TestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadSampleESBConfiguration(601);
    }

//    @Test(groups = "wso2.esb", description = " Rule Mediator as a Filter - Simple Rule Based " +
//                                             "Routing " +
//                                             "(Keeping Ruleset in the Registry)-using a string" +
//                                             " other" +
//                                             " than IBM")
    public void testLocalEntryWithWrongInput() throws Exception {


        try {
            axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null,
                                                    "WSO2");
            Assert.fail("This Request must throws a AxisFault");

        } catch (AxisFault e) {
            assertEquals(e.getMessage(), ESBTestConstant.INCOMING_MESSAGE_IS_NULL, "Invalid exception");
        }


    }


//    @Test(groups = "wso2.esb", description = " Rule Mediator as a Filter - Simple Rule Based " +
//                                             "Routing " +
//                                             "(Keeping Ruleset in the Registry)- using string IBM")
    public void testLocalEntryWithCorrectInput() throws Exception {


        OMElement response;

        response = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null,
                                                           "IBM");
        assertNotNull(response, "Response message null");
        OMElement returnElement = response.getFirstElement();

        OMElement symbolElement = returnElement.getFirstChildWithName(
                new QName("http://services.samples/xsd", "symbol"));

        assertEquals(symbolElement.getText(), "IBM", "Fault, invalid response");


    }


    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        super.cleanup();
    }
}
