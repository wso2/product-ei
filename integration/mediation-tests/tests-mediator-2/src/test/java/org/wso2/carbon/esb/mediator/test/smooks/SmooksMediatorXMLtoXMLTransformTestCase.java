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
package org.wso2.carbon.esb.mediator.test.smooks;

import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;

import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.activation.DataHandler;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class SmooksMediatorXMLtoXMLTransformTestCase extends ESBIntegrationTest {
    private ServerConfigurationManager serverConfigurationManager;
    private ResourceAdminServiceClient resourceAdminServiceStub;
    private final String COMMON_FILE_LOCATION = getClass().getResource(File.separator + "artifacts" + File.separator + "ESB" + File.separator + "synapseconfig" + File.separator + "vfsTransport" + File.separator).getPath();
    private final String ORDER_ID = "332";
    private final String TARGET_FILE_LOCATION = COMMON_FILE_LOCATION + "test" + File.separator + "xmlOut";
    private final String[] ORDER_ITEMS = {"Pen", "Book", "Bottle", "Note Book", "Pencil", "Chocolate", "Bun", "Banana", "Hat", "Toffee"};
    private boolean isProxyDeployed = false;

//    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/vfsTransport/vfs_xml_to_xml.xml");
        resourceAdminServiceStub = new ResourceAdminServiceClient(contextUrls.getBackEndUrl(), context.getContextTenant().getContextUser().getUserName()
, context.getContextTenant().getContextUser().getPassword());
        serverConfigurationManager = new ServerConfigurationManager(context);
        serverConfigurationManager.applyConfiguration(new File(COMMON_FILE_LOCATION + "axis2.xml"));
        super.init();
        setSmooksSampleConfigFileLocations();
        uploadResourcesToConfigRegistry();
        addVFSProxy();
    }

     /* Commenting out this test as it is incomplete and its purpose is unclear. */
     /* IMPORTANT: Do not uncomment this test case if do not know how to fix it. */
//    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.ALL
//    @Test(groups = {"wso2.esb", "local only"}, description = "XML to XML transformation using smooks mediator")
    public void testXMLtoXMLTransformationUsingSmooksMeidator() throws Exception {
        new File(COMMON_FILE_LOCATION + "test" + File.separator + "out" + File.separator).mkdir();
        new File(COMMON_FILE_LOCATION + "test" + File.separator + "xmlOut" + File.separator).mkdir();
        Thread.sleep(2000);
        File afile = new File(COMMON_FILE_LOCATION + File.separator + "synapse_sample_658_input.xml");
        File bfile = new File(COMMON_FILE_LOCATION + "test" + File.separator + "in" + File.separator + "synapse_sample_658_input.xml");
        FileUtils.copyFile(afile, bfile);

        Thread.sleep(30000);
        String fileContents;
        for (int i = 0; i < 10; i++) {
            fileContents = FileUtils.readFileToString(new File(TARGET_FILE_LOCATION + File.separator + "order-" + ORDER_ID + "-" + (i + 1) + ".xml"));
            assertNotNull(fileContents, "File contents in null");
            assertTrue(fileContents.toString().contains(ORDER_ITEMS[i]), ORDER_ITEMS[i] + fileContents);
        }


    }

    private void setSmooksSampleConfigFileLocations() throws IOException, InterruptedException {
        String fileContents = FileUtils.readFileToString(new File(COMMON_FILE_LOCATION + "synapse_config_658.xml"));
        fileContents = fileContents.replace("/home/harsha/smooks", TARGET_FILE_LOCATION);
        FileUtils.writeStringToFile(new File(COMMON_FILE_LOCATION + "synapse_config_658.xml"), fileContents);
        Thread.sleep(2000);
    }

    private void addVFSProxy() throws Exception {

        addProxyService(AXIOMUtil.stringToOM("<proxy xmlns=\"http://ws.apache.org/ns/synapse\" name=\"StockQuoteProxy\" transports=\"vfs\">\n" +
                                             "        <parameter name=\"transport.vfs.ContentType\">text/plain</parameter>\n" +
                                             "        <!--CHANGE-->\n" +
                                             "        <parameter name=\"transport.vfs.FileURI\">file://" + COMMON_FILE_LOCATION + "test" + File.separator + "in" + File.separator + "</parameter>\n" +
                                             "        <parameter name=\"transport.vfs.ContentType\">application/xml</parameter>\n" +
                                             "        <parameter name=\"transport.vfs.FileNamePattern\">.*\\.xml</parameter>\n" +
                                             "        <parameter name=\"transport.PollInterval\">5</parameter>\n" +
                                             "        <!--CHANGE-->\n" +
                                             "        <parameter name=\"transport.vfs.MoveAfterProcess\">file://" + COMMON_FILE_LOCATION + "test" + File.separator + "out" + File.separator + "</parameter>\n" +
                                             "        <!--CHANGE-->\n" +
                                             "        <parameter name=\"transport.vfs.MoveAfterFailure\">file://" + COMMON_FILE_LOCATION + "test" + File.separator + "out" + File.separator + "</parameter>\n" +
                                             "        <parameter name=\"transport.vfs.ActionAfterProcess\">MOVE</parameter>\n" +
                                             "        <parameter name=\"transport.vfs.ActionAfterFailure\">MOVE</parameter>\n" +
                                             "        <parameter name=\"Operation\">urn:placeOrder</parameter>\n" +
                                             "        <target>\n" +
                                             "            <inSequence>\n" +
                                             "                <smooks config-key=\"conf:/smooks/synapse_config_658.xml\">\n" +
                                             "                    <input type=\"xml\"/>\n" +
                                             "                    <output type=\"xml\"/>\n" +
                                             "                </smooks>\n" +
                                             "                <log level=\"full\"/>\n" +
                                             "            </inSequence>\n" +
                                             "            <outSequence/>\n" +
                                             "        </target>\n" +
                                             "    </proxy>\n"));
        isProxyDeployed = true;
    }

    private void uploadResourcesToConfigRegistry() throws Exception {
        resourceAdminServiceStub.deleteResource("/_system/config/smooks");
        resourceAdminServiceStub.addCollection("/_system/config/", "smooks", "",
                                               "Contains smooks config files");
        resourceAdminServiceStub.addResource(
                "/_system/config/smooks/synapse_config_658.xml", "application/xml", "xml files",
                new DataHandler(new URL("file:///" + COMMON_FILE_LOCATION + "synapse_config_658.xml")));
    }

//    @AfterClass(alwaysRun = true)
    public void restoreServerConfiguration() throws Exception {
        try {
            if (isProxyDeployed) {
                deleteProxyService("StockQuoteProxy");
            }
            resourceAdminServiceStub.deleteResource("/_system/config/smooks");
        } finally {
            super.cleanup();
            serverConfigurationManager.restoreToLastConfiguration();
            resourceAdminServiceStub = null;
            serverConfigurationManager = null;

        }

    }
}
