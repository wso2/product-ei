/*
 *  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.carbon.esb.rest.test.api;

import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.data.xsd.LogEvent;
import org.wso2.carbon.utils.ServerConstants;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.Utils;
import org.wso2.esb.integration.common.utils.common.TestConfigurationProvider;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ESBJAVA4519TestCase extends ESBIntegrationTest {

	private LogViewerClient logViewerClient;

	@BeforeClass(alwaysRun = true) public void init() throws Exception {
		super.init();
		loadESBConfigurationFromClasspath(
				File.separator + "artifacts" + File.separator + "ESB" + File.separator + "synapseconfig" +
				File.separator + "rest" + File.separator + "ESBJAVA4519synapseConfig.xml");
		logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
		logViewerClient.clearLogs();
	}

	@Test(groups = {"wso2.esb" }, description = "Test whether file get restored after deployment failure")
	public void testRestoringToPreviousConfigurationOnHotDeploymentFailure()
			throws Exception {

		String esbApiPath = System.getProperty(ServerConstants.CARBON_HOME) + File.separator +
		                    "repository" + File.separator + "deployment" + File.separator + "server" + File.separator +
		                    "synapse-configs" + File.separator + "default" + File.separator + "api" + File.separator +
		                    "CorruptedApi.xml";
		File esbApiFile = new File(esbApiPath);

		String corruptedApiPath =
				TestConfigurationProvider.getResourceLocation("ESB") + File.separator + "synapseconfig" +
				File.separator + "rest" + File.separator + "CorruptedApi.xml";
		File corruptedApiFile = new File(corruptedApiPath);

		String validApiPath = TestConfigurationProvider.getResourceLocation("ESB") + File.separator + "synapseconfig" +
		                      File.separator + "rest" + File.separator + "CorrectApi.xml";
		File validApiFile = new File(validApiPath);

		long startTime = System.currentTimeMillis();

		while ((startTime + 60000) > System.currentTimeMillis()) {
			log.info("Waiting for esb to persist the api config file...");
			if (esbApiFile.exists() && FileUtils.contentEquals(validApiFile, esbApiFile)) {
				log.info("Api Config is written to file system by esb. Waiting for hot deployment to pick up the " +
				         "resorted file as it will be taken as a restored file from esb.");
				Thread.sleep(30000);
				break;
			} else {
				Thread.sleep(20000);
			}
		}

		log.info("Copying the corrupted api config file...");
		Files.copy(corruptedApiFile.toPath(), esbApiFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		log.info("Copied the corrupted api config file to config folder.");

		boolean messageInLog = Utils.checkForLog(logViewerClient, "Deployment of synapse artifact failed", 100);
		Assert.assertTrue(messageInLog, "synapse artifact failed message was not found in logs");
		messageInLog = Utils.checkForLog(logViewerClient, "Restoring the existing artifact into the file", 10);
		Assert.assertTrue(messageInLog, "Original xml is not restored.");
	}

	@AfterClass(alwaysRun = true) public void destroy() throws Exception {
		super.cleanup();
	}

}
