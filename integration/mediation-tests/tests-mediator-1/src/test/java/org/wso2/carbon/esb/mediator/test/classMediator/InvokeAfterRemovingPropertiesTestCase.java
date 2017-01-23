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

package org.wso2.carbon.esb.mediator.test.classMediator;

import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.xml.namespace.QName;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class InvokeAfterRemovingPropertiesTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadSampleESBConfiguration(380);

    }

    @Test(groups = "wso2.esb", description = "Invoke after removing some properties")
    public void testAfterRemovingProperties() throws Exception {

        OMElement response, editedResponse;

        response = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "WSO2");
        assertNotNull(response, "Response message null");
        String responseString = response.getFirstElement().getFirstChildWithName
                (new QName("http://services.samples/xsd", "last")).getText().toString();
        double discountedPrice = Double.parseDouble(responseString);
        assertTrue(discountedPrice > 0);

        String filePath = "/artifacts/ESB/synapseconfig/class_mediator/synapse.xml";
        loadESBConfigurationFromClasspath(filePath);

        editedResponse = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "WSO2");
        assertNotNull(editedResponse, "Response message null");


        String editResponse = editedResponse.getFirstElement().getFirstChildWithName
                (new QName("http://services.samples/xsd", "last")).getText().toString();

        Assert.assertEquals(editResponse, "0.0", "Value mismatched" );

    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        super.cleanup();
    }

}
