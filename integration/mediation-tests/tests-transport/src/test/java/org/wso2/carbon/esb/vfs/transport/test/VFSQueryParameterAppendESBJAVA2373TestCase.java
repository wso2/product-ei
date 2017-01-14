/**
 * Copyright (c) 2005-2013, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.esb.vfs.transport.test;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axis2.AxisFault;
import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.Assert;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestConstant;

import java.io.File;

public class VFSQueryParameterAppendESBJAVA2373TestCase extends ESBIntegrationTest {
    private ServerConfigurationManager serverConfigurationManager;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();

        serverConfigurationManager = new ServerConfigurationManager(new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN));
        serverConfigurationManager.applyConfiguration(new File(getClass().getResource(File.separator + "artifacts" + File.separator + "ESB" + File.separator + "synapseconfig" + File.separator + "vfsTransport" + File.separator + "axis2.xml").getPath()));
        super.init();

        File outfolder = new File(getClass().getResource(File.separator + "artifacts" + File.separator + "ESB" + File.separator + "synapseconfig" + File.separator + "vfsTransport" + File.separator).getPath() + "test" + File.separator + "out" + File.separator);
        outfolder.mkdirs();
    }

    @AfterClass(alwaysRun = true)
    public void restoreServerConfiguration() throws Exception {
        try {
            super.cleanup();
        } finally {
            Thread.sleep(3000);
            serverConfigurationManager.restoreToLastConfiguration();
            serverConfigurationManager = null;
        }
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Sending a file through VFS Transport : transport.vfs.Append added to FileURI, tests if file gets saved with correct file name")
    public void testVFSFileURI() throws Exception {
        //<header name="To" value="vfs:file:///home/ravi/SupportProjects/carbon/4.0.0/platform/trunk/trunk/products/esb/4.5.1/modules/integration/tests/target/test-classes/artifacts/ESB/synapseconfig/vfsTransport/out/vfs-ESBJAVA2373-file?transport.vfs.Append=true"/>
        //System.out.println("<header name=\"To\" value=\"vfs:file://" + getClass().getResource(File.separator + "artifacts" + File.separator + "ESB" + File.separator + "synapseconfig" + File.separator + "vfsTransport" + File.separator).getPath() + "out/vfs-ESBJAVA2373-file?transport.vfs.Append=true" + "\"/>");
        addProxyService(AXIOMUtil.stringToOM("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "    <proxy name=\"VFSProxy\"\n" +
                "           xmlns=\"http://ws.apache.org/ns/synapse\"" +
                "           transports=\"https http\"\n" +
                "           startOnLoad=\"true\"\n" +
                "           trace=\"disable\">\n" +
                "        <target>\n" +
                "            <inSequence>\n" +
                "                <header name=\"To\" value=\"vfs:file://" + getClass().getResource(File.separator + "artifacts" + File.separator + "ESB" + File.separator + "synapseconfig" + File.separator + "vfsTransport" + File.separator).getPath() + "out/vfs-ESBJAVA2373-append-true?transport.vfs.Append=true" + "\"/>" +
                "                <log level=\"full\"/>" +
                "                <property name=\"OUT_ONLY\" value=\"true\"/>\n" +
                "                <property name=\"FORCE_SC_ACCEPTED\" value=\"true\" scope=\"axis2\"/>\n" +
                "                <send>\n" +
                "                    <endpoint>\n" +
                "                        <default trace=\"disable\" format=\"pox\">\n" +
                "                            <timeout>\n" +
                "                                <duration>30000</duration>\n" +
                "                                <responseAction>discard</responseAction>\n" +
                "                            </timeout>\n" +
                "                            <suspendOnFailure>\n" +
                "                                <initialDuration>0</initialDuration>\n" +
                "                                <progressionFactor>1.0</progressionFactor>\n" +
                "                                <maximumDuration>0</maximumDuration>\n" +
                "                            </suspendOnFailure>\n" +
                "                        </default>\n" +
                "                    </endpoint>\n" +
                "                </send>\n" +
                "            </inSequence>\n" +
                "            <outSequence>\n" +
                "                <drop/>\n" +
                "            </outSequence>\n" +
                "            <faultSequence/>\n" +
                "        </target>\n" +
                "    </proxy>"));

        try {
            OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("VFSProxy")
                , getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE), "WSO2");
        } catch(AxisFault e) {}

        File appendTrueFile = new File(getClass().getResource(File.separator + "artifacts" + File.separator + "ESB" + File.separator + "synapseconfig" + File.separator + "vfsTransport" + File.separator).getPath() + "out/vfs-ESBJAVA2373-append-true");
        Assert.assertTrue(appendTrueFile.exists(), "File with transport.vfs.Append=true file has been created?");

        long fileSize = appendTrueFile.length();

        try {
            OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("VFSProxy")
                    , getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE), "WSO2");
        } catch(AxisFault e) {}

        Thread.sleep(5000);
        Assert.assertTrue(fileSize < appendTrueFile.length(), "File has been appended to");

        deleteProxyService("VFSProxy");

        // Adding append = false proxy.
        addProxyService(AXIOMUtil.stringToOM("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "    <proxy name=\"VFSProxy\"\n" +
                "           xmlns=\"http://ws.apache.org/ns/synapse\"" +
                "           transports=\"https http\"\n" +
                "           startOnLoad=\"true\"\n" +
                "           trace=\"disable\">\n" +
                "        <target>\n" +
                "            <inSequence>\n" +
                "                <header name=\"To\" value=\"vfs:file://" + getClass().getResource(File.separator + "artifacts" + File.separator + "ESB" + File.separator + "synapseconfig" + File.separator + "vfsTransport" + File.separator).getPath() + "out/vfs-ESBJAVA2373-append-false?transport.vfs.Append=false" + "\"/>" +
                "                <log level=\"full\"/>" +
                "                <property name=\"OUT_ONLY\" value=\"true\"/>\n" +
                "                <property name=\"FORCE_SC_ACCEPTED\" value=\"true\" scope=\"axis2\"/>\n" +
                "                <send>\n" +
                "                    <endpoint>\n" +
                "                        <default trace=\"disable\" format=\"pox\">\n" +
                "                            <timeout>\n" +
                "                                <duration>30000</duration>\n" +
                "                                <responseAction>discard</responseAction>\n" +
                "                            </timeout>\n" +
                "                            <suspendOnFailure>\n" +
                "                                <initialDuration>0</initialDuration>\n" +
                "                                <progressionFactor>1.0</progressionFactor>\n" +
                "                                <maximumDuration>0</maximumDuration>\n" +
                "                            </suspendOnFailure>\n" +
                "                        </default>\n" +
                "                    </endpoint>\n" +
                "                </send>\n" +
                "            </inSequence>\n" +
                "            <outSequence>\n" +
                "                <drop/>\n" +
                "            </outSequence>\n" +
                "            <faultSequence/>\n" +
                "        </target>\n" +
                "    </proxy>"));

        try {
            OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("VFSProxy")
                    , getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE), "WSO2");
        } catch(AxisFault e) {}

        File appendFalseFile = new File(getClass().getResource(File.separator + "artifacts" + File.separator + "ESB" + File.separator + "synapseconfig" + File.separator + "vfsTransport" + File.separator).getPath() + "out/vfs-ESBJAVA2373-append-false");
        Assert.assertTrue(appendFalseFile.exists(), "File with transport.vfs.Append=false file has been created?");

        try {
            OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("VFSProxy")
                    , getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE), "WSO2");
        } catch(AxisFault e) {}

        Thread.sleep(5000);
        Assert.assertTrue(fileSize == appendFalseFile.length(), "File has been overwritten - no appending");

        deleteProxyService("VFSProxy");
    }


}
