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

package org.wso2.carbon.esb.mediator.test.iterate;

import org.apache.axiom.om.OMElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

/* Tests continue parent property of Iterate mediator */

public class IterateContinueParentTest extends ESBIntegrationTest {


	@BeforeClass(alwaysRun = true)
	public void setEnvironment() throws Exception {
		super.init();
	}

	@Test(groups = "wso2.esb", description = "Tests with continue parent=true", enabled = false)
	public void testContinueParentTrue() throws Exception, InterruptedException {
		loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/iterate/iterate_continue_parent_true.xml");
		OMElement response =
		                     axis2Client.sendMultipleQuoteRequest(getMainSequenceURL(), null, "WSO2", 2);
		//TODO log must be checked and verify the log has printed

	}
	
	@Test(groups = "wso2.esb", description = "Tests with continue parent=false", enabled = false)
	public void testContinueParentFalse() throws Exception, InterruptedException {
		loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/iterate/iterate_continue_parent_false.xml");
		OMElement response =
                axis2Client.sendMultipleQuoteRequest(getMainSequenceURL(), null, "WSO2", 2);
		//TODO log must be checked and verify the log has not printed

	}


	@AfterClass(alwaysRun = true)
	public void close() throws Exception {
		super.cleanup();
	}

}
