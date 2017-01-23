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
package org.wso2.carbon.esb.resource.test.template;

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
import org.wso2.esb.integration.common.clients.template.SequenceTemplateAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;


public class SequenceTemplateMediaTypeTestCase extends ESBIntegrationTest{
    private Log log = LogFactory.getLog(SequenceTemplateMediaTypeTestCase.class);

    private SequenceTemplateAdminServiceClient sequenceTemplateAdminServiceClient;
    private ResourceAdminServiceClient resourceAdmin;
    private boolean isDynamicSequenceTemplateExist = false;
    private boolean isDefinedSequenceTemplateExist = false;
    private final String KEY = "conf:/template/registrySequenceTemplate";
    private final String DEFINED_SEQUENCE_TEMPLATE_NAME = "automationSequenceTemplate";

    @BeforeClass
    public void init() throws Exception {
        super.init();
        sequenceTemplateAdminServiceClient = new SequenceTemplateAdminServiceClient(contextUrls.getBackEndUrl(), getSessionCookie());
        resourceAdmin = new ResourceAdminServiceClient(contextUrls.getBackEndUrl(), getSessionCookie());
    }

    @Test(groups = {"wso2.esb"}, description = "Test dynamic Sequence Template media type - application/vnd.wso2.template")
    public void dynamicSequenceTemplateMediaTypeTest() throws Exception {
        final String name = "registrySequenceTemplate";

        OMElement sequence = AXIOMUtil.stringToOM("<template xmlns=\"http://ws.apache.org/ns/synapse\" name=\"" + name + "\">" +
                                                  "<parameter name=\"message\"/>" +
                                                  "<sequence> " +
                                                  "<log level=\"custom\">" +
                                                  "<property name=\"GREETING_MESSAGE\" expression=\"$func:message\"/>" +
                                                  "</log>" +
                                                  "</sequence>" +
                                                  "</template>");
        sequenceTemplateAdminServiceClient.addDynamicSequenceTemplate(KEY, sequence);
        isDynamicSequenceTemplateExist = true;

        MetadataBean metadata = resourceAdmin.getMetadata("/_system/config/template/registrySequenceTemplate");
        Assert.assertEquals(metadata.getMediaType(), "application/vnd.wso2.template", "Media Type mismatched for Dynamic Sequence Template");


    }
    //since Registry persistence is no longer available
    @Test(groups = {"wso2.esb"}, description = "Test defined Sequence Template media type - text/xml", enabled = false)
    public void definedSequenceTemplateMediaTypeTest() throws Exception {

        OMElement sequence = AXIOMUtil.stringToOM("<template xmlns=\"http://ws.apache.org/ns/synapse\" name=\"" + DEFINED_SEQUENCE_TEMPLATE_NAME + "\">" +
                                                  "<parameter name=\"message\"/>" +
                                                  "<sequence> " +
                                                  "<log level=\"custom\">" +
                                                  "<property name=\"GREETING_MESSAGE\" expression=\"$func:message\" />" +
                                                  "</log>" +
                                                  "</sequence>" +
                                                  "</template>");
        sequenceTemplateAdminServiceClient.addSequenceTemplate(sequence);
        isDefinedSequenceTemplateExist = true;

        //addEndpoint is a a asynchronous call, it will take some time to write to a registry
        Thread.sleep(10000);
        MetadataBean metadata = resourceAdmin.getMetadata("/_system/config/repository/synapse/default/templates/" + DEFINED_SEQUENCE_TEMPLATE_NAME);
        Assert.assertEquals(metadata.getMediaType(), "text/xml", "Media Type mismatched for Defined Sequence Template");

    }

    @AfterClass
    public void destroy() throws  Exception {
        if (isDefinedSequenceTemplateExist) {
            sequenceTemplateAdminServiceClient.deleteTemplate(DEFINED_SEQUENCE_TEMPLATE_NAME);
        }
        if (isDynamicSequenceTemplateExist) {
            sequenceTemplateAdminServiceClient.deleteDynamicTemplate(KEY);
        }
        super.cleanup();
    }
}
