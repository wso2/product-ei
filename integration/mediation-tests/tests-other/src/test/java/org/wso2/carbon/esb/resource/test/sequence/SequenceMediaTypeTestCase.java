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
package org.wso2.carbon.esb.resource.test.sequence;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.registry.resource.stub.beans.xsd.MetadataBean;
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;
import org.wso2.esb.integration.common.clients.sequences.SequenceAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

public class SequenceMediaTypeTestCase extends ESBIntegrationTest {
    private Log log = LogFactory.getLog(SequenceMediaTypeTestCase.class);

    private SequenceAdminServiceClient sequenceAdminServiceClient;
    private ResourceAdminServiceClient resourceAdmin;
    private boolean isDynamicSequenceExist = false;
    private boolean isDefinedSequenceExist = false;
    private final String KEY = "conf:/sequence/testAutomationSequence";
    private final String DEFINED_SEQUENCE_NAME = "definedSequence";

    @BeforeClass
    public void init() throws Exception {
        super.init();
        sequenceAdminServiceClient = new SequenceAdminServiceClient(contextUrls.getBackEndUrl(),getSessionCookie());
        resourceAdmin = new ResourceAdminServiceClient(contextUrls.getBackEndUrl(), getSessionCookie());
    }

    @Test(groups = {"wso2.esb"}, description = "Test dynamic Sequence media type - application/vnd.wso2.sequence")
    public void dynamicSequenceMediaTypeTest() throws Exception {
        final String name = "automationDynamicSequence";

        OMElement sequence = AXIOMUtil.stringToOM("<sequence xmlns=\"http://ws.apache.org/ns/synapse\" name=\"" + name + "\">" +
                                                  " <in>" +
                                                  "<log level=\"full\"/>" +
                                                  "<send>" +
                                                  "<endpoint> " +
                                                  "<address uri=\"http://localhost:9000/services/SimpleStockQuoteService\"/>" +
                                                  "</endpoint> " +
                                                  "</send> " +
                                                  "</in> " +
                                                  "<out> " +
                                                  "<send/> " +
                                                  "</out> " +
                                                  "</sequence>");

        sequenceAdminServiceClient.addDynamicSequence(KEY, sequence);
        isDynamicSequenceExist = true;
        MetadataBean metadata = resourceAdmin.getMetadata("/_system/config/sequence/testAutomationSequence");
        Assert.assertEquals(metadata.getMediaType(), "application/vnd.wso2.sequence", "Media Type mismatched for Dynamic Sequence");


    }
    //since Registry persistence is no longer available
    @Test(groups = {"wso2.esb"}, description = "Test defined Sequence media type - text/xml", enabled = false)
    public void definedSequenceMediaTypeTest() throws Exception {

        OMElement sequence = AXIOMUtil.stringToOM("<sequence xmlns=\"http://ws.apache.org/ns/synapse\" name=\"" + DEFINED_SEQUENCE_NAME + "\">" +
                                                  " <in>" +
                                                  "<log level=\"full\"/>" +
                                                  "<send>" +
                                                  "<endpoint> " +
                                                  "<address uri=\"http://localhost:9000/services/SimpleStockQuoteService\"/>" +
                                                  "</endpoint> " +
                                                  "</send> " +
                                                  "</in> " +
                                                  "<out> " +
                                                  "<send/> " +
                                                  "</out> " +
                                                  "</sequence>");
        sequenceAdminServiceClient.addSequence(sequence);
        isDefinedSequenceExist = true;
        //addEndpoint is a a asynchronous call, it will take some time to write to a registry
        Thread.sleep(10000);
        MetadataBean metadata = resourceAdmin.getMetadata("/_system/config/repository/synapse/default/sequences/" + DEFINED_SEQUENCE_NAME);
        Assert.assertEquals(metadata.getMediaType(), "text/xml", "Media Type mismatched for Defined sequence");

    }

    @AfterClass
    public void destroy() throws Exception {
        if (isDefinedSequenceExist) {
            sequenceAdminServiceClient.deleteSequence(DEFINED_SEQUENCE_NAME);
        }
        if (isDynamicSequenceExist) {
            sequenceAdminServiceClient.deleteDynamicSequence(KEY);
        }
        super.cleanup();
    }
}
