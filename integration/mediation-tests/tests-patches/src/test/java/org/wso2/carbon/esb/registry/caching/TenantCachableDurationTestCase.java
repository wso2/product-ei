/**
 * Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.registry.caching;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;
import java.io.FileInputStream;

/**
 * ESBJAVA-3039 cachableDuration is not configured by default for tenants in the WSO2Registry in the synapse
 * configuration
 */
public class TenantCachableDurationTestCase extends ESBIntegrationTest {

	Log logger = LogFactory.getLog(TenantCachableDurationTestCase.class);
	private String registryContents;

	@BeforeClass(alwaysRun = true)
	protected void init() throws Exception {

		super.init(userMode.TENANT_USER);

		String sourceFile = System.getProperty("carbon.home") + File.separator +
		                    "repository/tenants/" +
		                    "1" +
		                    "/synapse-configs/default/registry.xml";

		FileInputStream inputStream = new FileInputStream(sourceFile);
		try {
			registryContents = IOUtils.toString(inputStream);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
	}

	@Test(groups = "wso2.esb", description = "cachableDuration property test for tenants")
	public void testSynapseConfig() throws Exception {
		boolean status = false;
		if (registryContents.contains("cachableDuration")) {
			status = true;
		}
		Assert.assertTrue(status);
	}

	@AfterClass(alwaysRun = true)
	public void unDeployService() throws Exception {
		//un deploying deployed artifact
		super.cleanup();
	}
}
