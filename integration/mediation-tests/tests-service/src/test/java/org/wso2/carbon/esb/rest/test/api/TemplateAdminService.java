/*
 *Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.esb.rest.test.api;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axis2.AxisFault;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.clients.template.SequenceTemplateAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

/**
 * Test templateAdminService related operations
 */
public class TemplateAdminService extends ESBIntegrationTest {
    private SequenceTemplateAdminServiceClient templateAdmin;
    private static final String templateName = "TemplateAdminService";
    private  static final String dynamicName = "TemplateUserService";
    private final String KEY = "conf:/template/dynamicAdminTemplate";
    private static final String template1 =
            "<template  xmlns=\"http://ws.apache.org/ns/synapse\" name=\"TemplateAdminService\">\n" + "<parameter name=\"message\"/>\n" + " <sequence>\n" + "<log level=\"custom\">\n"
                    + "<property name=\"TMPL_MSG \" value= \"in Template Service..\" />\n" + "</log>\n" + "   </sequence>\n" + "</template>";
    private static final String template2 =
            "<template  xmlns=\"http://ws.apache.org/ns/synapse\" name=\"TemplateAdminService\">\n" + "<parameter name=\"message\"/>\n" + " <sequence>\n" + "<log level=\"custom\">\n"
                    + "<property name=\"TMPL_MSG \" value= \"in Template Service Updated..\" />\n" + "</log>\n" + "   </sequence>\n" + "</template>";
    private static final String template3 =
            "<template  xmlns=\"http://ws.apache.org/ns/synapse\" name=\"TemplateUserService\">\n" + "<parameter name=\"message\"/>\n" + " <sequence>\n" + "<log level=\"custom\">\n"
                    + "<property name=\"TMPL_MSG \" value= \"in Template Service Updated..\" />\n" + "</log>\n" + "   </sequence>\n" + "</template>";
    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        templateAdmin = new SequenceTemplateAdminServiceClient(contextUrls.getBackEndUrl(), getSessionCookie());
    }

    @Test(groups = { "wso2.esb" },
          description = "Test template creation service",
          priority = 1)
    public void testCreateTemplate() throws Exception {
        int prevCount = templateAdmin.getTemplatesCount();
        templateAdmin.addSequenceTemplate(AXIOMUtil.stringToOM(template1));
        int latestCount = templateAdmin.getTemplatesCount();
        org.testng.Assert.assertTrue(prevCount < latestCount, "New template not added");
    }

    @Test(groups = { "wso2.esb" },
          description = "Test template list service",
          priority = 2)
    public void testGetTemplate() throws Exception {
        OMElement elm = templateAdmin.getTemplate(templateName);
        Assert.assertNotNull(elm, "Unable to get the template");
        Assert.assertTrue(elm.toString().contains(templateName), "Requested template name is invalid");
    }

    @Test(groups = { "wso2.esb" },
          description = "Test template save service",
          priority = 3)
    public void testUpdateTemplate() throws Exception {
        templateAdmin.saveTemplate(AXIOMUtil.stringToOM(template2));
        OMElement elm = templateAdmin.getTemplate(templateName);
        Assert.assertTrue(elm.toString().contains("in Template Service Updated"), "Template not updated");
    }

    @Test(groups = { "wso2.esb" },
          description = "Test template enable statistics",
          priority = 4)
    public void testEnableStatisticsTemplate() throws Exception {
        templateAdmin.enableStatistics(templateName);
        OMElement elm = templateAdmin.getTemplate(templateName);
        Assert.assertTrue(elm.toString().contains("statistics=\"enable\""), "Stats not enabled for template");
    }

    @Test(groups = { "wso2.esb" },
          description = "Test template disable statistics",
          priority = 5)
    public void testDisableStatisticsTemplate() throws Exception {
        templateAdmin.disableStatistics(templateName);
        OMElement elm = templateAdmin.getTemplate(templateName);
        Assert.assertFalse(elm.toString().contains("statistics=\"enable\""), "Stats not disabled for template");
    }

    @Test(groups = { "wso2.esb" },
          description = "Test template enable tracing",
          priority = 6)
    public void testEnableTracingTemplate() throws Exception {
        templateAdmin.enableTracing(templateName);
        OMElement elm = templateAdmin.getTemplate(templateName);
        Assert.assertTrue(elm.toString().contains("trace=\"enable\""), "Tracing not enabled for template");
    }

    @Test(groups = { "wso2.esb" },
          description = "Test template disable tracing",
          priority = 7)
    public void testDisableTracingTemplate() throws Exception {
        templateAdmin.disableTracing(templateName);
        OMElement elm = templateAdmin.getTemplate(templateName);
        Assert.assertFalse(elm.toString().contains("trace=\"enable\""), "Tracing not disabled for template");
    }

    @Test(groups = { "wso2.esb" },
          description = "Test template delete service",
          priority = 8)
    public void testDeleteTemplate() throws Exception {
        int prevCount = templateAdmin.getTemplatesCount();
        templateAdmin.deleteTemplate(templateName);
        int latestCount = templateAdmin.getTemplatesCount();
        Assert.assertTrue(prevCount > latestCount, "New template not removed");

    }

    @Test(groups = { "wso2.esb" },
          description = "Test list invalid template",
          priority = 9)
    public void testGetInvalidTemplate() throws Exception {
        try {
            templateAdmin.getTemplate("invalid");
            Assert.fail("Expected exception not thrown when listing invalid template");
        } catch (AxisFault e) {
            Assert.assertTrue(e.getMessage().contains("Couldn't get the Synapse Configuration to get the Template"),
                    "Expected exception not thrown for invalid template");
        }
    }

    @Test(groups = { "wso2.esb" },
          description = "Test dynamic template creation service",
          priority = 10)
    public void testCreateDynamicTemplate() throws Exception {
        int prevCount = templateAdmin.getDynamicTemplateCount();
        templateAdmin.addDynamicSequenceTemplate(KEY,AXIOMUtil.stringToOM(template1));
        int latestCount = templateAdmin.getDynamicTemplateCount();
        org.testng.Assert.assertTrue(prevCount < latestCount, "Dynamic template not added");
    }

    @Test(groups = { "wso2.esb" },
          description = "Test dynamic template list service",
          priority = 11)
    public void testGetDynamicTemplate() throws Exception {
        OMElement elm = templateAdmin.getDynamicTemplate(KEY);
        Assert.assertNotNull(elm, "Unable to get dynamic template");
        Assert.assertTrue(elm.toString().contains(templateName), "Requested template name is invalid");
    }

    @Test(groups = { "wso2.esb" },
          description = "Test dynamic template creation service",
          priority = 12)
    public void testUpdateDynamicTemplate() throws Exception {
        templateAdmin.updateDynamicTemplate(KEY,AXIOMUtil.stringToOM(template3));
        templateAdmin.saveDynamicTemplate(KEY,AXIOMUtil.stringToOM(template3));
        OMElement elm = templateAdmin.getDynamicTemplate(KEY);
        Assert.assertTrue(elm.toString().contains(dynamicName), "Dynamic template not updated");

    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        templateAdmin = null;
        super.cleanup();
    }

}
