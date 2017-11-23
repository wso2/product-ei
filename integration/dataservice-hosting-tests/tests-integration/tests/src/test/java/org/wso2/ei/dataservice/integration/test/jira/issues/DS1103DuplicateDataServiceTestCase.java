/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.ei.dataservice.integration.test.jira.issues;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.ei.dataservice.integration.common.utils.DSSTestCaseUtils;
import org.wso2.ei.dataservice.integration.common.utils.SqlDataSourceUtil;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;

import javax.activation.DataHandler;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This test was written to verify the fix for https://wso2.org/jira/browse/DS-1103.
 */
public class DS1103DuplicateDataServiceTestCase extends DSSIntegrationTest {
	private final String serviceName = "DuplicateDataServiceTest";
	private List<File> sqlFileList;

	@BeforeClass(alwaysRun = true)
	public void serviceDeployment() throws Exception {
		super.init();
		sqlFileList = new ArrayList<>();
		sqlFileList.add(selectSqlFile("CreateTables.sql"));
		sqlFileList.add(selectSqlFile("Students.sql"));
		SqlDataSourceUtil dataSource = new SqlDataSourceUtil(sessionCookie,
				dssContext.getContextUrls().getBackEndUrl());
		dataSource.createDataSource("duplicate_test", sqlFileList);
	}

	@Test(groups = "wso2.dss", description = "Testing the duplicate data service deployment fail test case.")
	public void testForDuplicateDataServiceDeployment() throws Exception {
		DataHandler dssConfig = createArtifact(getResourceLocation() + File.separator + "dbs" + File.separator +
		                                       "rdbms" + File.separator + "h2" + File.separator +
		                                       "H2SimpleJsonTestDuplicate.dbs", sqlFileList);
		DSSTestCaseUtils dssTest = new DSSTestCaseUtils();
		Assert.assertTrue(dssTest.uploadArtifact(dssContext.getContextUrls().getBackEndUrl(), sessionCookie,
		                                         "H2SimpleJsonTestDuplicate", dssConfig),
		                  "Service File Uploading failed");
		Assert.assertTrue(isServiceFaulty("H2SimpleJsonTestDuplicate"), "Service is Found");
	}

	@AfterClass
	public void clean() throws Exception {
		cleanup();
	}

}
