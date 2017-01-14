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

package org.wso2.carbon.esb.securevalut.test;


import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.base.CarbonBaseUtils;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.*;

import static java.io.File.separator;
import static org.testng.Assert.assertTrue;

public class CustomSSLProfileWithSecureVault extends ESBIntegrationTest {
    private ServerConfigurationManager serverConfigurationManager;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();

        //cipher-text.properties file update
        serverConfigurationManager = new ServerConfigurationManager(context);
        String sourceCText = FrameworkPathUtil.getSystemResourceLocation() + "artifacts" + File.separator + "ESB" +
                             separator + "synapseconfig" + separator + "customSSLprofileWithsecurevault" + separator
                             + "cipher-text.properties";
        String targetCText = CarbonBaseUtils.getCarbonHome() + File.separator + "repository" +
                             File.separator + "conf" + File.separator + "security" + File.separator + "cipher-text.properties";
        File sourceFileCText = new File(sourceCText);
        File targetFileCText = new File(targetCText);
        serverConfigurationManager.applyConfigurationWithoutRestart(sourceFileCText, targetFileCText, true);

        //cipher-tool.properties file update
        String sourceCTool = FrameworkPathUtil.getSystemResourceLocation() + "artifacts" + File.separator + "ESB" +
                             separator + "synapseconfig" + separator + "customSSLprofileWithsecurevault" + separator
                             + "cipher-tool.properties";
        String targetCTool = CarbonBaseUtils.getCarbonHome() + File.separator + "repository" + File.separator +
                             "conf" + File.separator + "security" + File.separator + "cipher-tool.properties";
        File sourceFileCTool = new File(sourceCTool);
        File targetFileCTool = new File(targetCTool);
        serverConfigurationManager.applyConfigurationWithoutRestart(sourceFileCTool, targetFileCTool, true);

        //secret-conf.properties file update
        String sourceSecret = FrameworkPathUtil.getSystemResourceLocation() + "artifacts" + File.separator + "ESB" +
                              separator + "synapseconfig" + separator + "customSSLprofileWithsecurevault" + separator
                              + "secret-conf.properties";
        String targetSecret = CarbonBaseUtils.getCarbonHome() + File.separator + "repository" + File.separator +
                              "conf" + File.separator + "security" + File.separator + "secret-conf.properties";
        File sourceFileSecret = new File(sourceSecret);
        File targetFileSecret = new File(targetSecret);
        serverConfigurationManager.applyConfigurationWithoutRestart(sourceFileSecret, targetFileSecret, true);

        //axis2.xml file update
        String sourceAxis2 = FrameworkPathUtil.getSystemResourceLocation() + "artifacts" + File.separator + "ESB" +
                             separator + "synapseconfig" + separator + "customSSLprofileWithsecurevault" + separator
                             + "axis2.xml";
        String targetAxis2 = CarbonBaseUtils.getCarbonHome() + File.separator + "repository" + File.separator +
                             "conf" + File.separator + "axis2" + File.separator + "axis2.xml";
        File sourceFileAxis2 = new File(sourceAxis2);
        File targetFileAxis2 = new File(targetAxis2);
        serverConfigurationManager.applyConfigurationWithoutRestart(sourceFileAxis2, targetFileAxis2, true);

        //catalina-server.xml file update - this is required after carbon kernel 4.4.1
        String sourceTomcat = FrameworkPathUtil.getSystemResourceLocation() + "artifacts" + File.separator + "ESB" +
                              separator + "synapseconfig" + separator + "customSSLprofileWithsecurevault" + separator
                              + "catalina-server.xml";
        String targetTomcat = CarbonBaseUtils.getCarbonHome() + File.separator + "repository" + File.separator +
                              "conf" + File.separator + "tomcat" + File.separator + "catalina-server.xml";
        File sourceFileTomcat = new File(sourceTomcat);
        File targetFileTomcat = new File(targetTomcat);
        serverConfigurationManager.applyConfigurationWithoutRestart(sourceFileTomcat, targetFileTomcat, true);

        //password-tmp file update
        String sourcePassTmp = FrameworkPathUtil.getSystemResourceLocation() + "artifacts" + File.separator + "ESB" +
                               separator + "synapseconfig" + separator + "customSSLprofileWithsecurevault" + separator
                               + "password-tmp";
        String targetPassTmp = CarbonBaseUtils.getCarbonHome() + separator + "password-tmp";
        File sourceFilePassTmp = new File(sourcePassTmp);
        File targetFilePassTmp = new File(targetPassTmp);
        serverConfigurationManager.applyConfigurationWithoutRestart(sourceFilePassTmp, targetFilePassTmp, false);
        serverConfigurationManager.restartGracefully();

        super.init();
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.esb", description = "Tests Secure Vault Protected Custom SSL Profiles")
    public void testCustomSSLProfileWithSecureVaultPasswords() throws InterruptedException {
        String logString = "java.io.IOException: Keystore was tampered with, or password was incorrect";

        try {
            BufferedReader bf = new BufferedReader(new FileReader(CarbonBaseUtils.getCarbonHome() + File.separator +
                                                                  "repository" + File.separator + "logs" + File.separator + "wso2carbon.log"));

            String line;
            boolean found = false;
            while ((line = bf.readLine()) != null) {
                int indexfound = line.indexOf(logString);
                if (indexfound > -1) {
                    found = true;
                    break;
                }
            }
            assertTrue(!found, "Server started successfully with Custom SSL Profile using Secure Vault.");
            bf.close();
        } catch (FileNotFoundException e) {
            assertTrue(true, "wso2carbon.log file not found.");
        } catch (IOException e) {
            assertTrue(true, "Error reading log file wso2carbon.log");
        }
    }

    /**
     * Reset configurations
     *
     * @throws Exception
     */
    @AfterClass(alwaysRun = true)
    public void atEnd() throws Exception {
        try {
            super.cleanup();
        } finally {
            Thread.sleep(3000);
            serverConfigurationManager.restoreToLastConfiguration();
            serverConfigurationManager = null;
        }
    }
}
