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

package org.wso2.carbon.esb.mediator.test.iterate;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestConstant;
import static org.testng.Assert.assertEquals;

/**
 * This class will test iterator mediator when invalid name space is set for both fields 'attachPath'
 * and 'iterate expression'
 */
public class InvalidNamespaceTestCase extends ESBIntegrationTest {
    @BeforeClass(alwaysRun = true)
    public void uploadSynapseConfig() throws Exception{
        super.init();
    }

    /**
     * This  test sets invalid name space specified for 'AttachPath' field and mediate the messages and verify
     * error handling by sending stock quote requests. Error in attaching the splitted elements,Unable to get
     * the attach path specified by the expression
     */
    @Test(groups = {"wso2.esb"}, description = "Testing invalid namespace for 'attachPath' field")
    public void testInvalidNameSpaceForAttachPath() throws Exception {
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/iterate/invalid_name_space_attachpath.xml");
        OMElement response;
        try {
            response =axis2Client.sendMultipleQuoteRequest(getMainSequenceURL(), null, "WSO2",5);
            Assert.fail("This Request must throw AxisFault"); // This will execute when the exception is not thrown as expected
        } catch (AxisFault message) {
            assertEquals(message.getReason(), ESBTestConstant.READ_TIME_OUT,
                    "Iterator mediator worked even with an invalid name space for 'attachPath' field.");
        }
    }

    /**
     * This test sets invalid name space specified for 'AttachPath' field and mediate the message and verify error
     * handling by sending stock quote requests. Evaluation of the XPath expression will not throw exceptions, but
     * it will time out
     */

    @Test(groups = {"wso2.esb"}, description = "Testing invalid namespace for 'iterate expression' field")
    public void testInvalidNameSpaceForIterateExpression() throws Exception {
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/iterate/invalid_namespace_iterateexpression.xml");
         try {
            OMElement response = axis2Client.sendMultipleQuoteRequest(getMainSequenceURL(), null, "WSO2", 5);
            Assert.fail("This Request must throw AxisFault"); // This will execute when the exception is not thrown as expected
        } catch (AxisFault message) {
            assertEquals(message.getReason(),ESBTestConstant.READ_TIME_OUT,
                    "Iterator mediator worked even with an invalid name space for 'iterate expression' field.");
          }

        }

    /**
     *  This test define a valid iterate expression which does NOT match with the original message and do mediation
     *  to see error handling.No exceptions will be thrown but request will time out.
     *
     */
    @Test(groups = {"wso2.esb"}, description = "Testing valid expression which mismatch the original message in 'iterate expression' field")
    public void testValidIterateExpressionMismatchOriginalMessage() throws Exception {
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/iterate/valid_iterate_exp_mismatch_original_message.xml");
        try {
            OMElement response = axis2Client.sendMultipleQuoteRequest(getMainSequenceURL(), null, "WSO2", 10);
            Assert.fail("This Request must throw AxisFault"); // This will execute when the exception is not thrown as expected
        } catch (AxisFault message) {
            assertEquals(message.getReason(),ESBTestConstant.READ_TIME_OUT,
                    "Iterator mediator worked with a valid iterate expression which mismatches the original message.");
        }

    }

    @AfterClass(groups = "wso2.esb")
    public void close() throws Exception {
        super.cleanup();
    }
}
