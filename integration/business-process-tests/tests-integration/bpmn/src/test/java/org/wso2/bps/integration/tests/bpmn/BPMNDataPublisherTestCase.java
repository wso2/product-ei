/*
 * Copyright (c)  2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
Uncomment when testing bpmn data publisher, commented our since we cannot have a dependency on snapshot of another repo
package org.wso2.bps.integration.tests.bpmn;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.bps.integration.common.utils.BPSMasterTest;
import org.wso2.carbon.analytics.stream.persistence.stub.dto.AnalyticsTable;
import org.wso2.das.integration.common.clients.AnalyticsWebServiceClient;
import org.wso2.das.integration.common.clients.EventStreamPersistenceClient;
*/
/**
 * This class is used to test BPMN data publisher
 */
/*
public class BPMNDataPublisherTestCase extends BPSMasterTest {
	private static final Log log = LogFactory.getLog(BPMNDataPublisherTestCase.class);
	private AnalyticsWebServiceClient webServiceClient;
	private EventStreamPersistenceClient persistenceClient;

	private static final String PROCESS_INSTANCE_TABLE = "BPMN_Process_Instance_Data_Publish";
	private static final String TASK_INSTANCE_TABLE = "BPMN_Task_Instance_Data_Publish";
	private static final String STREAM_VERSION = "1.0.0";
	private String dataReceiverUrl = "https://localhost:9445/services/";

	@BeforeClass(alwaysRun = true) public void init() throws Exception {
		super.init();
		persistenceClient = new EventStreamPersistenceClient(dataReceiverUrl, "admin", "admin");
		webServiceClient = new AnalyticsWebServiceClient(dataReceiverUrl, "admin", "admin");
	}

	@Test(groups = "wso2.bpmn.data.publisher", description = "Test backend availability of persistence service") public void testBackendAvailability()
			throws Exception {
		init();
		Assert.assertTrue(persistenceClient.isBackendServicePresent(),
		                  "Method returns value other than true");
	}

	@Test(groups = "wso2.bpmn.data.publisher", description = "Get process instance table", dependsOnMethods = "testBackendAvailability") public void getProcessInstanceTable()
			throws Exception {
		AnalyticsTable analyticsTable =
				persistenceClient.getAnalyticsTable(PROCESS_INSTANCE_TABLE, STREAM_VERSION);
		Assert.assertEquals(analyticsTable.getAnalyticsTableRecords().length, 8,
		                    "Table column count is wrong");
		Assert.assertEquals(analyticsTable.getPersist(), true, "Table persistence state is wrong");
	}

	@Test(groups = "wso2.bpmn.data.publisher", description = "Get task instance table", dependsOnMethods = "testBackendAvailability") public void getTaskInstanceTable()
			throws Exception {
		AnalyticsTable analyticsTable =
				persistenceClient.getAnalyticsTable(TASK_INSTANCE_TABLE, STREAM_VERSION);
		Assert.assertEquals(analyticsTable.getAnalyticsTableRecords().length, 8,
		                    "Table column count is wrong");
		Assert.assertEquals(analyticsTable.getPersist(), true, "Table persistence state is wrong");
	}

	@Test(groups = "wso2.bpmn.data.publisher", description = "Check event stream for process instances persistence", dependsOnMethods = "getProcessInstanceTable") public void checkProcessDataPersistence()
			throws Exception {
		Assert.assertTrue(
				webServiceClient.getRecordCount(PROCESS_INSTANCE_TABLE, 0, Long.MAX_VALUE) > 0,
				"Process instances record count is valid");
	}

	@Test(groups = "wso2.bpmn.data.publisher", description = "Check event stream for process instances persistence", dependsOnMethods = "getTaskInstanceTable") public void checkTaskDataPersistence()
			throws Exception {
		Assert.assertTrue(
				webServiceClient.getRecordCount(TASK_INSTANCE_TABLE, 0, Long.MAX_VALUE) > 0,
				"Task instances record count is valid");
	}
}

*/
