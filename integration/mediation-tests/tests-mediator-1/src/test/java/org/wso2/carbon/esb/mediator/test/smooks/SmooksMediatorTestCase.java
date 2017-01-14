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
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.servers.MultiMessageReceiver;

import javax.activation.DataHandler;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class SmooksMediatorTestCase extends ESBIntegrationTest {
    private ServerConfigurationManager serverConfigurationManager;
    private ResourceAdminServiceClient resourceAdminServiceStub;
    private final String COMMON_FILE_LOCATION = File.separator + "artifacts" + File.separator + "ESB" + File.separator + "synapseconfig" + File.separator + "vfsTransport";
    private boolean isProxyDeployed = false;
    private final String proxyName = "StockQuoteProxySmookTest";

    /*@BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        serverConfigurationManager = new ServerConfigurationManager(esbServer.getBackEndUrl());
        serverConfigurationManager.applyConfiguration(new File(getClass().getResource(COMMON_FILE_LOCATION + File.separator + "axis2.xml").getPath()));
        super.init();
        resourceAdminServiceStub = new ResourceAdminServiceClient(esbServer.getBackEndUrl(), esbServer.getSessionCookie());
    }

    @AfterClass
    public void restoreServerConfiguration() throws Exception {
        try {
            if (isProxyDeployed) {
                deleteProxyService(proxyName);
            }
            loadSampleESBConfiguration(0);

        } finally {
            serverConfigurationManager.restoreToLastConfiguration();
            serverConfigurationManager = null;
            super.cleanup();
        }
    }

    @AfterMethod()
    public void deleteProxyService() throws Exception {
        if (isProxyDeployed) {
            deleteProxyService(proxyName);
            isProxyDeployed = false;
        }
    }

    @Test(groups = {"wso2.esb", "local only"}, description = "Testing Smooks configuration from local entry")
    public void testSmookConfigFromLocalEntry() throws Exception {
        final int port = 8201;
        final int messageCount = 5;
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/vfsTransport/vfs_test_synapse.xml");
        addVFSProxySmookConfigFromLocalEntry(port);
        MultiMessageReceiver multiMessageReceiver = new MultiMessageReceiver(port);
        multiMessageReceiver.startServer();
        try {
            File afile = new File(getClass().getResource(COMMON_FILE_LOCATION + File.separator + "edi.txt").getPath());
            File bfile = new File(getClass().getResource(COMMON_FILE_LOCATION).getPath() + "test" + File.separator + "in" + File.separator + "edi.txt");
            FileUtils.copyFile(afile, bfile);
            new File(getClass().getResource(COMMON_FILE_LOCATION).getPath() + "test" + File.separator + "out" + File.separator).mkdir();
            Thread.sleep(30000);
        } catch (Exception e) {

        }

        List<String> response = null;
        int breakCount = 0;
        while (multiMessageReceiver.getMessageQueueSize() < messageCount) {
            log.info("Waiting for fill up the list");
            Thread.sleep(1000);
            breakCount++;
            if (breakCount > 30) {
                break;
            }
        }
        response = multiMessageReceiver.getIncomingMessages();
        multiMessageReceiver.stopServer();
        String totalResponse = "";
        for (String temp : response) {
            totalResponse += temp;
        }
        assertNotNull(response, "Response is null");
        assertEquals(response.size(), messageCount, "Message count is mis matching");
        assertTrue(totalResponse.contains("IBM"), "IBM is not in the response");
        assertTrue(totalResponse.contains("MSFT"), "MSFT is not in the response");
        assertTrue(totalResponse.contains("SUN"), "SUN is not in the response");


    }

    @Test(groups = {"wso2.esb", "local only"}, description = "Smooks configuration refer form configuration registry")
    public void testSmookConfigFromConfigRegistry() throws Exception {
        final int messageCount = 5;
        final int port = 7896;
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/vfsTransport/vfs_test_smook_config_at_registry.xml");
        uploadResourcesToConfigRegistry();
        addVFSProxySmookConfigFromConfigRegistry(port);
        MultiMessageReceiver multiMessageReceiver = new MultiMessageReceiver(port);

        multiMessageReceiver.startServer();
        try {
            File afile = new File(getClass().getResource(COMMON_FILE_LOCATION + File.separator + "edi.txt").getPath());
            File bfile = new File(getClass().getResource(COMMON_FILE_LOCATION).getPath() + "test" + File.separator + "in" + File.separator + "edi.txt");
            System.out.println("***********************"+ bfile.getPath());
            FileUtils.copyFile(afile, bfile);
            new File(getClass().getResource(COMMON_FILE_LOCATION).getPath() + "test" + File.separator + "out" + File.separator).mkdir();
            Thread.sleep(30000);
        } catch (Exception e) {
        }
        List<String> response = null;
        int breakCount = 0;
        while (multiMessageReceiver.getMessageQueueSize() < messageCount) {
            log.info("Waiting for fill up the list");
            Thread.sleep(1000);
            breakCount++;
            if (breakCount > 30) {
                break;
            }
        }
        response = multiMessageReceiver.getIncomingMessages();
        multiMessageReceiver.stopServer();
        String totalResponse = "";
        for (String temp : response) {
            totalResponse += temp;
        }
        assertNotNull(response, "Response is null");
        assertEquals(response.size(), messageCount, "Message count is mis matching");
        assertTrue(totalResponse.contains("IBM"), "IBM is not in the response");
        assertTrue(totalResponse.contains("MSFT"), "MSFT is not in the response");
        assertTrue(totalResponse.contains("SUN"), "SUN is not in the response");

        resourceAdminServiceStub.deleteResource("/_system/config/smooks");
    }

    @Test(groups = {"wso2.esb", "local only"}, description = "XML to XML transformation using smooks mediator")
    public void testXMLtoXMLTransformationUsingSmooksMeidator() throws Exception {
        final String orderId = "332";
        final String targetFileLocation = SmooksMediatorXMLtoXMLTransformTestCase.class.getResource(COMMON_FILE_LOCATION).getPath() + "test" + File.separator + "xmlOut";
        final String[] orderItems = {"Pen", "Book", "Bottle", "Note Book", "Pencil", "Chocolate", "Bun", "Banana", "Hat", "Toffee"};
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/vfsTransport/vfs_xml_to_xml.xml");
        setSmooksSampleConfigFileLocations(targetFileLocation);
        uploadXMLTransformationResourcesToConfigRegistry();
        addVFSProxyXMLtoXMLTransformation();
        File afile = new File(getClass().getResource(COMMON_FILE_LOCATION + File.separator + "synapse_sample_658_input.xml").getPath());
        File bfile = new File(getClass().getResource(COMMON_FILE_LOCATION).getPath() + "test" + File.separator + "in" + File.separator + "synapse_sample_658_input.xml");
        FileUtils.copyFile(afile, bfile);
        new File(getClass().getResource(COMMON_FILE_LOCATION).getPath() + "test" + File.separator + "out" + File.separator).mkdir();
        new File(getClass().getResource(COMMON_FILE_LOCATION).getPath() + "test" + File.separator + "xmlOut" + File.separator).mkdir();
        Thread.sleep(30000);
        String fileContents;
        for (int i = 0; i < 10; i++) {
            fileContents = FileUtils.readFileToString(new File(targetFileLocation + File.separator + "order-" + orderId + "-" + (i + 1) + ".xml"));
            assertNotNull(fileContents, "File contents in null");
            assertTrue(fileContents.toString().contains(orderItems[i]), orderItems[i] + fileContents);
        }

        resourceAdminServiceStub.deleteResource("/_system/config/smooks");
    }

    private void addVFSProxySmookConfigFromLocalEntry(int port) throws Exception {
        addProxyService(AXIOMUtil.stringToOM("<proxy xmlns=\"http://ws.apache.org/ns/synapse\" name=\"" + proxyName + "\" transports=\"vfs\">\n" +
                                             "        <parameter name=\"transport.vfs.ContentType\">text/plain</parameter>\n" +
                                             "        <!--CHANGE-->\n" +
                                             "        <parameter name=\"transport.vfs.FileURI\">file://" + getClass().getResource(COMMON_FILE_LOCATION).getPath() + "test" + File.separator + "in" + File.separator + "</parameter>\n" +
                                             "        <parameter name=\"transport.vfs.ContentType\">text/plain</parameter>\n" +
                                             "        <parameter name=\"transport.vfs.FileNamePattern\">.*\\.txt</parameter>\n" +
                                             "        <parameter name=\"transport.PollInterval\">5</parameter>\n" +
                                             "        <!--CHANGE-->\n" +
                                             "        <parameter name=\"transport.vfs.MoveAfterProcess\">file://" + getClass().getResource(COMMON_FILE_LOCATION).getPath() + "test" + File.separator + "out" + File.separator + "</parameter>\n" +
                                             "        <!--CHANGE-->\n" +
                                             "        <parameter name=\"transport.vfs.MoveAfterFailure\">file://" + getClass().getResource(COMMON_FILE_LOCATION).getPath() + "test" + File.separator + "out" + File.separator + "</parameter>\n" +
                                             "        <parameter name=\"transport.vfs.ActionAfterProcess\">MOVE</parameter>\n" +
                                             "        <parameter name=\"transport.vfs.ActionAfterFailure\">MOVE</parameter>\n" +
                                             "        <parameter name=\"Operation\">urn:placeOrder</parameter>\n" +
                                             "        <target>\n" +
                                             "            <inSequence>\n" +
                                             "                <smooks config-key=\"smooks-key\">\n" +
                                             "                    <input type=\"text\"/>\n" +
                                             "                    <output type=\"xml\"/>\n" +
                                             "                </smooks>\n" +
                                             "                <xslt key=\"transform-xslt-key\"/>\n" +
                                             "                <log level=\"full\"/>\n" +
                                             "                <!--<property name=\"ContentType\" value=\"text/xml\" scope=\"axis2-client\"/>-->\n" +
                                             "                <!--<property name=\"messageType\" value=\"text/xml\" scope=\"axis2\"/>-->\n" +
                                             "                <iterate expression=\"//m0:placeOrder/m0:order\" preservePayload=\"true\" attachPath=\"//m0:placeOrder\" xmlns:m0=\"http://services.samples\">\n" +
                                             "                    <target>\n" +
                                             "                        <sequence>\n" +
                                             "                            <header name=\"Action\" value=\"urn:placeOrder\"/>\n" +
                                             "                            <property action=\"set\" name=\"OUT_ONLY\" value=\"true\"/>\n" +
                                             "                            <send>\n" +
                                             "                                <endpoint>\n" +
                                             "                                    <address format=\"soap11\"\n" +
                                             "                                             uri=\"http://localhost:" + port + "\"/>\n" +
                                             "                                </endpoint>\n" +
                                             "                            </send>\n" +
                                             "                        </sequence>\n" +
                                             "                    </target>\n" +
                                             "                </iterate>\n" +
                                             "            </inSequence>\n" +
                                             "            <outSequence/>\n" +
                                             "        </target>\n" +
                                             "        <publishWSDL uri=\"file:repository/samples/resources/smooks/PlaceStockOrder.wsdl\"/>\n" +
                                             "    </proxy>\n"));
        isProxyDeployed = true;
    }

    private void addVFSProxySmookConfigFromConfigRegistry(int port)
            throws Exception {

        addProxyService(AXIOMUtil.stringToOM("<proxy xmlns=\"http://ws.apache.org/ns/synapse\" name=\"" + proxyName + "\" transports=\"vfs\">\n" +
                                             "        <parameter name=\"transport.vfs.ContentType\">text/plain</parameter>\n" +
                                             "        <!--CHANGE-->\n" +
                                             "        <parameter name=\"transport.vfs.FileURI\">file://" + getClass().getResource(File.separator + "artifacts" + File.separator + "ESB" + File.separator + "synapseconfig" + File.separator + "vfsTransport" + File.separator).getPath() + "test" + File.separator + "in" + File.separator + "</parameter>\n" +
                                             "        <parameter name=\"transport.vfs.ContentType\">text/plain</parameter>\n" +
                                             "        <parameter name=\"transport.vfs.FileNamePattern\">.*\\.txt</parameter>\n" +
                                             "        <parameter name=\"transport.PollInterval\">5</parameter>\n" +
                                             "        <!--CHANGE-->\n" +
                                             "        <parameter name=\"transport.vfs.MoveAfterProcess\">file://" + getClass().getResource(COMMON_FILE_LOCATION).getPath() + "test" + File.separator + "out" + File.separator + "</parameter>\n" +
                                             "        <!--CHANGE-->\n" +
                                             "        <parameter name=\"transport.vfs.MoveAfterFailure\">file://" + getClass().getResource(COMMON_FILE_LOCATION).getPath() + "test" + File.separator + "out" + File.separator + "</parameter>\n" +
                                             "        <parameter name=\"transport.vfs.ActionAfterProcess\">MOVE</parameter>\n" +
                                             "        <parameter name=\"transport.vfs.ActionAfterFailure\">MOVE</parameter>\n" +
                                             "        <parameter name=\"Operation\">urn:placeOrder</parameter>\n" +
                                             "        <target>\n" +
                                             "            <inSequence>\n" +
                                             "                <smooks config-key=\"conf:/smooks/smooks-config.xml\">\n" +
                                             "                    <input type=\"text\"/>\n" +
                                             "                    <output type=\"xml\"/>\n" +
                                             "                </smooks>\n" +
                                             "                <xslt key=\"transform-xslt-key\"/>\n" +
                                             "                <log level=\"full\"/>\n" +
                                             "                <!--<property name=\"ContentType\" value=\"text/xml\" scope=\"axis2-client\"/>-->\n" +
                                             "                <!--<property name=\"messageType\" value=\"text/xml\" scope=\"axis2\"/>-->\n" +
                                             "                <iterate expression=\"//m0:placeOrder/m0:order\" preservePayload=\"true\" attachPath=\"//m0:placeOrder\" xmlns:m0=\"http://services.samples\">\n" +
                                             "                    <target>\n" +
                                             "                        <sequence>\n" +
                                             "                            <header name=\"Action\" value=\"urn:placeOrder\"/>\n" +
                                             "                            <property action=\"set\" name=\"OUT_ONLY\" value=\"true\"/>\n" +
                                             "                            <send>\n" +
                                             "                                <endpoint>\n" +
                                             "                                    <address format=\"soap11\"\n" +
                                             "                                             uri=\"http://localhost:" + port + "\"/>\n" +
                                             "                                </endpoint>\n" +
                                             "                            </send>\n" +
                                             "                        </sequence>\n" +
                                             "                    </target>\n" +
                                             "                </iterate>\n" +
                                             "            </inSequence>\n" +
                                             "            <outSequence/>\n" +
                                             "        </target>\n" +
                                             "        <publishWSDL uri=\"file:repository/samples/resources/smooks/PlaceStockOrder.wsdl\"/>\n" +
                                             "    </proxy>\n"));
        isProxyDeployed = true;
    }

    private void addVFSProxyXMLtoXMLTransformation() throws Exception {

        addProxyService(AXIOMUtil.stringToOM("<proxy xmlns=\"http://ws.apache.org/ns/synapse\" name=\"" + proxyName + "\" transports=\"vfs\">\n" +
                                             "        <parameter name=\"transport.vfs.ContentType\">text/plain</parameter>\n" +
                                             "        <!--CHANGE-->\n" +
                                             "        <parameter name=\"transport.vfs.FileURI\">file://" + getClass().getResource(COMMON_FILE_LOCATION).getPath() + "test" + File.separator + "in" + File.separator + "</parameter>\n" +
                                             "        <parameter name=\"transport.vfs.ContentType\">application/xml</parameter>\n" +
                                             "        <parameter name=\"transport.vfs.FileNamePattern\">.*\\.xml</parameter>\n" +
                                             "        <parameter name=\"transport.PollInterval\">5</parameter>\n" +
                                             "        <!--CHANGE-->\n" +
                                             "        <parameter name=\"transport.vfs.MoveAfterProcess\">file://" + getClass().getResource(COMMON_FILE_LOCATION).getPath() + "test" + File.separator + "out" + File.separator + "</parameter>\n" +
                                             "        <!--CHANGE-->\n" +
                                             "        <parameter name=\"transport.vfs.MoveAfterFailure\">file://" + getClass().getResource(COMMON_FILE_LOCATION).getPath() + "test" + File.separator + "out" + File.separator + "</parameter>\n" +
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
                "/_system/config/smooks/smooks-config.xml", "application/xml", "xml files",
                new DataHandler(new URL("file:///" + getClass().getResource(
                        "/artifacts/ESB/synapseconfig/vfsTransport/smooks-config.xml").getPath())));
    }

    private void setSmooksSampleConfigFileLocations(String targetFileLocation) throws IOException {
        String fileContents = FileUtils.readFileToString(new File(getClass().getResource(COMMON_FILE_LOCATION + File.separator + "synapse_config_658.xml").getPath()));
        fileContents = fileContents.replace("/home/harsha/smooks", targetFileLocation);
        FileUtils.writeStringToFile(new File(getClass().getResource(COMMON_FILE_LOCATION + File.separator + "synapse_config_658.xml").getPath()), fileContents);
    }

    private void uploadXMLTransformationResourcesToConfigRegistry() throws Exception {
        resourceAdminServiceStub.deleteResource("/_system/config/smooks");
        resourceAdminServiceStub.addCollection("/_system/config/", "smooks", "",
                                               "Contains smooks config files");
        resourceAdminServiceStub.addResource(
                "/_system/config/smooks/synapse_config_658.xml", "application/xml", "xml files",
                new DataHandler(new URL("file:///" + getClass().getResource(
                        "/artifacts/ESB/synapseconfig/vfsTransport/synapse_config_658.xml").getPath())));
    }*/
}
