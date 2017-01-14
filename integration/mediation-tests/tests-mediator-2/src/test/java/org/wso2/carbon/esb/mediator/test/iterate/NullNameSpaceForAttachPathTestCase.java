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
import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestConstant;
import java.io.File;
import java.net.URL;

import static org.testng.Assert.assertEquals;

/**
 * This tests Iterator Mediator, when no name space is specified for 'attachPath' field  and
 * mediate the messages and verify error handling
 */
public class NullNameSpaceForAttachPathTestCase extends ESBIntegrationTest {
    private String symbol;

    @BeforeClass(alwaysRun = true)
    public void uploadSynapseConfig() throws Exception{
        super.init();
        symbol = FileUtils.readFileToString(new File(getESBResourceLocation() + "/mediatorconfig/iterate/iterate1.txt"));
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/iterate/null_namespace.xml");
    }


    /**
     * These tests set no name space specified for 'AttachPath' field and mediate the messages
     * and verify error handling by sending stock quote requests. Evaluation of the XPath expression will result
     * in an error
     */
    @Test(groups = {"wso2.esb"}, description = "Testing null namespace for 'attachPath' field")
    public void testNullNameSpaceForAttachPath() throws Exception{

        OMElement response;
        try {
            response =axis2Client.sendMultipleQuoteRequest(getMainSequenceURL(), symbol , "WSO2",5);
            Assert.fail("This Request must throw AxisFault"); // This will execute when the exception is not thrown as expected
        } catch (AxisFault message) {
            System.out.print(message.getReason());
            assertEquals(message.getReason(), ESBTestConstant.UNEXPECTED_SENDING_OUT,
              "Iterator mediator worked even with an null name space for 'attachPath' field.");
        }

    }

    @AfterClass(groups = "wso2.esb", alwaysRun = true)
    public void close() throws Exception {
        symbol = null;
        super.cleanup();
    }
}


