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
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.activation.DataHandler;

public class SmooksMediatorConfigFromConfigRegistryTestCase extends ESBIntegrationTest {
    private ResourceAdminServiceClient resourceAdminServiceClient;
    private boolean isProxyDeployed = false;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        super.init();
        resourceAdminServiceClient = new ResourceAdminServiceClient
                (contextUrls.getBackEndUrl(), context.getContextTenant().getContextUser().getUserName()
                        , context.getContextTenant().getContextUser().getPassword());

        uploadResourcesToConfigRegistry();
        addSmooksProxy();
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE })
    @Test(groups = {"wso2.esb"}, description = "Transform from a Smook mediator config picked out of config registry")
    public void testSendingToSmooks() throws Exception {
        String smooksResourceDirstr = getClass().getResource("/artifacts/ESB/synapseconfig/smooks/").getFile();
        File fileSmook = new File(smooksResourceDirstr);
        String smooksResourceDir = fileSmook.getAbsolutePath();
        Path source = Paths.get(smooksResourceDir, "data.csv");
        Path destination = Paths.get(smooksResourceDir,  "test", "in", "data.csv");
        Files.createDirectories(Paths.get(smooksResourceDir, "test", "in"));
        Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
        /*
         * The polling interval of the VFS proxy is 1000 ms. Therefore 2000ms waiting time was added to provide
         * enough time for the processing
         */
        Thread.sleep(2000);

        Path outPutFilePath = Paths.get(smooksResourceDir, "test", "out", "config-reg-test-out.xml");
        Assert.assertTrue(Files.exists(outPutFilePath), "output file has not been created, there could be an issue "
                + "in picking up smooks configuration from registry");
        String smooksOut = new String(
                Files.readAllBytes(outPutFilePath));
        Assert.assertTrue(smooksOut.contains("<csv-record "
                + "number=\"1\"><firstname>Tom</firstname><lastname>Fennelly</lastname><gender>Male</gender><age>4"
                + "</age><country>Ireland</country></csv-record>"), "Transformation may not have happened as "
                + "expected from the config picked from registry");

    }


    private void uploadResourcesToConfigRegistry() throws Exception {
        resourceAdminServiceClient.addResource(
                "/_system/config/smooks_config.xml", "application/xml", "xml files",
                new DataHandler(new URL("file:///" + getClass().getResource(
                        "/artifacts/ESB/synapseconfig/smooks/smooks_config.xml").getPath())));
    }

    private void addSmooksProxy() throws Exception {
        addProxyService(AXIOMUtil.stringToOM("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<proxy xmlns=\"http://ws.apache.org/ns/synapse\" name=\"SmooksProxy\" transports=\"vfs\" "
                + "startOnLoad=\"true\">\n"
                + "    <target>\n" + "    <inSequence>\n" + "    <log level=\"full\"/>\n"
                + "    <smooks config-key=\"conf:/smooks_config.xml\">\n" + "        <input type=\"text\"/>\n"
                + "        <output type=\"xml\"/>\n" + "    </smooks>\n"
                + "    <property name=\"OUT_ONLY\" value=\"true\"/>\n" + "    <send>\n"
                + "        <endpoint name=\"FileEpr\">\n" + "            <address uri=\"vfs:file://" + getClass()
                .getResource("/artifacts/ESB/synapseconfig/smooks/").getPath() + "test/out/config-reg-test-out.xml\" format=\"soap11\"/>\n" + "        </endpoint>\n"
                + "    </send>\n" + "    <log level=\"full\"/>\n" + "    </inSequence>\n" + "    </target>\n"
                + "    <parameter name=\"transport.PollInterval\">1</parameter>\n"
                + "    <parameter name=\"transport.vfs.FileURI\">file://" + getClass().getResource("/artifacts/ESB/synapseconfig/smooks/").getPath() + "test/in/</parameter>\n"
                + "    <parameter name=\"transport.vfs.FileNamePattern\">.*\\.csv</parameter>\n"
                + "    <parameter name=\"transport.vfs.ContentType\">text/plain</parameter>\n" + "</proxy>"));
        isProxyDeployed = true;
    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        try {
            if (isProxyDeployed) {
                deleteProxyService("SmooksProxy");
            }
            resourceAdminServiceClient.deleteResource("/_system/config/smooks_config.xml");
        } finally {
            super.cleanup();
            resourceAdminServiceClient = null;
        }
    }
}

