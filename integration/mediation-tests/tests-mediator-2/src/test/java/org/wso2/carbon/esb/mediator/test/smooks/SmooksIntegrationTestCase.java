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
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;

import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;import org.wso2.carbon.endpoint.stub.types.EndpointAdminEndpointAdminException;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class SmooksIntegrationTestCase extends ESBIntegrationTest {


    private ResourceAdminServiceClient resourceAdminServiceClient;
    private ServerConfigurationManager serverConfigurationManager;
    private boolean isProxyDeployed = false;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        super.init();
        serverConfigurationManager = new ServerConfigurationManager(context);
        serverConfigurationManager.applyConfiguration(new File(getClass().getResource(File.separator + "artifacts" + File.separator + "ESB" + File.separator + "synapseconfig" + File.separator + "smooks" + File.separator + "axis2.xml").getPath()));
        super.init();
        loadESBConfigurationFromClasspath(File.separator + "artifacts" + File.separator + "ESB" + File.separator + "synapseconfig" + File.separator + "smooks" + File.separator + "smooks_synapse.xml");

        resourceAdminServiceClient = new ResourceAdminServiceClient
                (contextUrls.getBackEndUrl(), context.getContextTenant().getContextUser().getUserName()
, context.getContextTenant().getContextUser().getPassword());

        uploadResourcesToConfigRegistry();
        addSmooksProxy();
        addSmooksSequence();
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
            Thread.sleep(3000);
            serverConfigurationManager.restoreToLastConfiguration();
            resourceAdminServiceClient = null;
            serverConfigurationManager = null;

        }
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = {"wso2.esb"}, description = "Sending a Large File To Smooks Mediator")
    public void testSendingToSmooks() throws IOException, EndpointAdminEndpointAdminException,
                                             LoginAuthenticationExceptionException,
                                             XMLStreamException, InterruptedException {

        File afile = new File(getClass().getResource(File.separator + "artifacts" + File.separator + "ESB" + File.separator + "synapseconfig" + File.separator + "smooks" + File.separator + "person.csv").getPath());
        File bfile = new File(getClass().getResource(File.separator + "artifacts" + File.separator + "ESB" + File.separator + "synapseconfig" + File.separator + "smooks" + File.separator).getPath() + "test" + File.separator + "in" + File.separator + "person.csv");

        FileUtils.copyFile(afile, bfile);
        Thread.sleep(40000);

        File outfile = new File(getClass().getResource(File.separator + "artifacts" + File.separator + "ESB" + File.separator + "synapseconfig" + File.separator + "smooks" + File.separator).getPath() + "test" + File.separator + "out" + File.separator + "Out.xml");

        String smooksOut = FileUtils.readFileToString(outfile);
        Assert.assertTrue(smooksOut.contains("<csv-record number=\"160\"><firstname>Andun</firstname><lastname>Sameera</lastname><gender>Male</gender><age>4</age><country>SriLanka</country></csv-record>"));

    }

    private void uploadResourcesToConfigRegistry() throws Exception {
        resourceAdminServiceClient.addResource(
                "/_system/config/smooks_config.xml", "application/xml", "xml files",
                new DataHandler(new URL("file:///" + getClass().getResource(
                        "/artifacts/ESB/synapseconfig/smooks/smooks_config.xml").getPath())));
    }

    private void addSmooksProxy() throws Exception {

        addProxyService(AXIOMUtil.stringToOM("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                             "<proxy xmlns=\"http://ws.apache.org/ns/synapse\" name=\"SmooksProxy\" transports=\"vfs\" startOnLoad=\"true\">\n" +
                                             "    <target inSequence=\"Smooks\"/>\n" +
                                             "    <parameter name=\"transport.PollInterval\">15</parameter>\n" +
                                             "    <parameter name=\"transport.vfs.FileURI\">file://" + getClass().getResource(File.separator + "artifacts" + File.separator + "ESB" + File.separator + "synapseconfig" + File.separator + "smooks" + File.separator).getPath() + "test" + File.separator + "in" + File.separator + "</parameter>\n" +
                                             "    <parameter name=\"transport.vfs.FileNamePattern\">.*\\.csv</parameter>\n" +
                                             "    <parameter name=\"transport.vfs.ContentType\">text/plain</parameter>\n" +
                                             "</proxy>"));
        isProxyDeployed = true;
    }

    private void addSmooksSequence()
            throws Exception {
        addSequence(AXIOMUtil.stringToOM("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                         "<sequence xmlns=\"http://ws.apache.org/ns/synapse\" name=\"Smooks\">\n" +
                                         "    <log level=\"full\"/>\n" +
                                         "    <smooks config-key=\"conf:/smooks_config.xml\">\n" +
                                         "        <input type=\"text\"/>\n" +
                                         "        <output type=\"xml\"/>\n" +
                                         "    </smooks>\n" +
                                         "    <property name=\"OUT_ONLY\" value=\"true\"/>\n" +
                                         "    <send>\n" +
                                         "        <endpoint name=\"FileEpr\">\n" +
                                         "            <address uri=\"vfs:file://" + getClass().getResource(File.separator + "artifacts" + File.separator + "ESB" + File.separator + "synapseconfig" + File.separator + "smooks" + File.separator).getPath() + "test" + File.separator + "out" + File.separator + "Out.xml\" format=\"soap11\"/>\n" +
                                         "        </endpoint>\n" +
                                         "    </send>\n" +
                                         "    <log level=\"full\"/>\n" +
                                         "</sequence>"));
    }

}

