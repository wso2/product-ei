/*
 * Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.mediator.test.property;


import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

/**
 * https://wso2.org/jira/browse/ESBJAVA-956
 *
 */
public class NonAsciiCharactersForValuesOfPropertiesTestCase extends ESBIntegrationTest {

	@BeforeClass
	public void init() throws Exception {
		super.init();
	}

	@Test(groups = { "wso2.esb" }, description = "Patch : ESBJAVA-956 : Put non-ascii characters as the values of the properties of the proxy services")
	public void testNonAsciiCharactersInProperties() throws Exception {
		String filePath =
		                  "/artifacts/ESB/synapseconfig/patchAutomation/non_ascii_value_properties_synapse_.xml";
		loadESBConfigurationFromClasspath(filePath);
	}

	@AfterClass(alwaysRun = true)
	public void afterClass() throws Exception {
		super.cleanup();
	}

}
