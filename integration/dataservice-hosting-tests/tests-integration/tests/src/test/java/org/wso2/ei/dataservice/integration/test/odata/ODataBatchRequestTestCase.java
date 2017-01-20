/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.ei.dataservice.integration.test.odata;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.wso2.ei.dataservice.integration.test.odata.ODataTestUtils.sendPOST;

/**
 * This class contains OData specific test cases. to verify the functionality of odata batch requests.
 */
public class ODataBatchRequestTestCase  extends DSSIntegrationTest {
	private final String serviceName = "ODataBatchRequestSampleService";
	private final String configId = "default";
	private String webappURL;

	@BeforeClass(alwaysRun = true)
	public void serviceDeployment() throws Exception {
		super.init();
		List<File> sqlFileLis = new ArrayList<>();
		sqlFileLis.add(selectSqlFile("CreateODataTables.sql"));
		sqlFileLis.add(selectSqlFile("Customers.sql"));
		sqlFileLis.add(selectSqlFile("FIlesWithFIlesRecords.sql"));
		deployService(serviceName, createArtifact(getResourceLocation() + File.separator + "dbs" + File.separator +
		                                          "odata" + File.separator + "ODataBatchRequestSampleService.dbs",
		                                          sqlFileLis));
		webappURL = dssContext.getContextUrls().getWebAppURL();
	}

	@AfterClass(alwaysRun = true)
	public void destroy() throws Exception {
		deleteService(serviceName);
		cleanup();
	}

	@Test(groups = "wso2.dss", description = "test the service document retrieval")
	public void validateBatchRequestTestCase() throws Exception {
		String endpoint = webappURL + "/odata/" + serviceName + "/" + configId + "/$batch";
		String content = "--batch_36522ad7-fc75-4b56-8c71-56071383e77b\n" +
		                 "Content-Type: application/http\n" +
		                 "Content-Transfer-Encoding:binary\n" +
		                 "\n" +
		                 "GET " + webappURL + "/odata/" + serviceName + "/" + configId + "/FILES HTTP/1.1\n" +
		                 "Accept: application/json;odata.metadata=minimal\n" +
		                 "\n" +
		                 "--batch_36522ad7-fc75-4b56-8c71-56071383e77b\n" +
		                 "Content-Type: multipart/mixed;boundary=changeset_77162fcd-b8da-41ac-a9f8-9357efbbd\n" +
		                 "\n" +
		                 "--changeset_77162fcd-b8da-41ac-a9f8-9357efbbd \n" +
		                 "Content-Type: application/http \n" +
		                 "Content-Transfer-Encoding: binary \n" +
		                 "Content-ID: 1\n" +
		                 "\n" +
		                 "POST " + webappURL + "/odata/" + serviceName + "/" + configId + "/FILES HTTP/1.1\n" +
		                 "OData-Version: 4.0\n" +
		                 "Content-Type: application/json;odata.metadata=minimal\n" +
		                 "Accept: application/json;odata.metadata=minimal\n" +
		                 "\n" +
		                 "{\n" +
		                 "\"FILENAME\":\"WSO2MB\",\n" +
		                 "\"TYPE\":\"WSO2 Message Broker\"\n" +
		                 "}\n" +
		                 "\n" +
		                 "--changeset_77162fcd-b8da-41ac-a9f8-9357efbbd \n" +
		                 "Content-Type: application/http \n" +
		                 "Content-Transfer-Encoding: binary \n" +
		                 "Content-ID: 2\n" +
		                 "\n" +
		                 "PATCH " + webappURL + "/odata/" + serviceName + "/" + configId +
		                 "/FILES('WSO2DAS') HTTP/1.1\n" +
		                 "OData-Version: 4.0\n" +
		                 "Content-Type: application/json;odata.metadata=minimal\n" +
		                 "Accept: application/json;odata.metadata=minimal\n" +
		                 "\n" +
		                 "{\n" +
		                 "\"TYPE\":\"WSO2 Business Activity Monitor\"\n" +
		                 "}\n" +
		                 "\n" +
		                 "--changeset_77162fcd-b8da-41ac-a9f8-9357efbbd--\n" +
		                 "--batch_36522ad7-fc75-4b56-8c71-56071383e77b\n" +
		                 "Content-Type: application/http\n" +
		                 "Content-Transfer-Encoding:binary\n" +
		                 "\n" +
		                 "GET " + webappURL + "/odata/" + serviceName + "/" + configId + "/FILES HTTP/1.1\n" +
		                 "Accept: application/json;odata.metadata=minimal\n" +
		                 "\n" +
		                 "--batch_36522ad7-fc75-4b56-8c71-56071383e77b--";
		Map<String, String> headers = new HashMap<>();
		headers.put("Accept", "application/json");
		headers.put("OData-Version", "4.0");
		headers.put("Content-Type", "multipart/mixed;boundary=batch_36522ad7-fc75-4b56-8c71-56071383e77b");
		Object[] response = sendPOST(endpoint, content, headers);
		Assert.assertEquals(response[0], 202);
		Assert.assertTrue(response[1].toString().contains("WSO2 Business Activity Monitor") &&
		                  response[1].toString().contains("WSO2 Message Broker"));
	}

	@Test(groups = "wso2.dss", description = "test the service document retrieval", dependsOnMethods = "validateBatchRequestTestCase")
	public void validateBatchRequestRollBackTestCase() throws Exception {
		String endpoint = webappURL + "/odata/" + serviceName + "/" + configId + "/$batch";
		String content = "--batch_36522ad7-fc75-4b56-8c71-56071383e77b\n" +
		                 "Content-Type: application/http\n" +
		                 "Content-Transfer-Encoding:binary\n" +
		                 "\n" +
		                 "GET " + webappURL + "/odata/" + serviceName + "/" + configId + "/FILES HTTP/1.1\n" +
		                 "Accept: application/json;odata.metadata=minimal\n" +
		                 "\n" +
		                 "--batch_36522ad7-fc75-4b56-8c71-56071383e77b\n" +
		                 "Content-Type: multipart/mixed;boundary=changeset_77162fcd-b8da-41ac-a9f8-9357efbbd\n" +
		                 "\n" +
		                 "--changeset_77162fcd-b8da-41ac-a9f8-9357efbbd \n" +
		                 "Content-Type: application/http \n" +
		                 "Content-Transfer-Encoding: binary \n" +
		                 "Content-ID: 1\n" +
		                 "\n" +
		                 "POST " + webappURL + "/odata/" + serviceName + "/" + configId + "/FILES HTTP/1.1\n" +
		                 "OData-Version: 4.0\n" +
		                 "Content-Type: application/json;odata.metadata=minimal\n" +
		                 "Accept: application/json;odata.metadata=minimal\n" +
		                 "\n" +
		                 "{\n" +
		                 "\"FILENAME\":\"WSO2MB-Test\",\n" +
		                 "\"TYPE\":\"WSO2 Message Broker-Test\"\n" +
		                 "}\n" +
		                 "\n" +
		                 "--changeset_77162fcd-b8da-41ac-a9f8-9357efbbd \n" +
		                 "Content-Type: application/http \n" +
		                 "Content-Transfer-Encoding: binary \n" +
		                 "Content-ID: 2\n" +
		                 "\n" +
		                 "PATCH " + webappURL + "/odata/" + serviceName + "/" + configId +
		                 "/FILES('WSO2DAS') HTTP/1.1\n" +
		                 "OData-Version: 4.0\n" +
		                 "Content-Type: application/json;odata.metadata=minimal\n" +
		                 "Accept: application/json;odata.metadata=minimal\n" +
		                 "\n" +
		                 "{\n" +
		                 "\"TEXT\":\"WSO2 Business Activity Monitor\"\n" +
		                 "}\n" +
		                 "\n" +
		                 "--changeset_77162fcd-b8da-41ac-a9f8-9357efbbd--\n" +
		                 "--batch_36522ad7-fc75-4b56-8c71-56071383e77b\n" +
		                 "Content-Type: application/http\n" +
		                 "Content-Transfer-Encoding:binary\n" +
		                 "\n" +
		                 "GET " + webappURL + "/odata/" + serviceName + "/" + configId + "/FILES HTTP/1.1\n" +
		                 "Accept: application/json;odata.metadata=minimal\n" +
		                 "\n" +
		                 "--batch_36522ad7-fc75-4b56-8c71-56071383e77b--";
		Map<String, String> headers = new HashMap<>();
		headers.put("Accept", "application/json");
		headers.put("OData-Version", "4.0");
		headers.put("Content-Type", "multipart/mixed;boundary=batch_36522ad7-fc75-4b56-8c71-56071383e77b");
		Object[] response = sendPOST(endpoint, content, headers);
		Assert.assertEquals(response[0], 202);

	}

	@Test(groups = "wso2.dss", description = "test the service document retrieval", dependsOnMethods = "validateBatchRequestTestCase")
	public void validateBatchRequestContinueOnErrorTestCase() throws Exception {
		String endpoint = webappURL + "/odata/" + serviceName + "/" + configId + "/$batch";
		String content = "--batch_36522ad7-fc75-4b56-8c71-56071383e77b\n" +
		                 "Content-Type: application/http\n" +
		                 "Content-Transfer-Encoding:binary\n" +
		                 "\n" +
		                 "GET " + webappURL + "/odata/" + serviceName + "/" + configId + "/FILES HTTP/1.1\n" +
		                 "Accept: application/json;odata.metadata=minimal\n" +
		                 "\n" +
		                 "--batch_36522ad7-fc75-4b56-8c71-56071383e77b\n" +
		                 "Content-Type: multipart/mixed;boundary=changeset_77162fcd-b8da-41ac-a9f8-9357efbbd\n" +
		                 "\n" +
		                 "--changeset_77162fcd-b8da-41ac-a9f8-9357efbbd \n" +
		                 "Content-Type: application/http \n" +
		                 "Content-Transfer-Encoding: binary \n" +
		                 "Content-ID: 1\n" +
		                 "\n" +
		                 "POST " + webappURL + "/odata/" + serviceName + "/" + configId + "/FILES HTTP/1.1\n" +
		                 "OData-Version: 4.0\n" +
		                 "Content-Type: application/json;odata.metadata=minimal\n" +
		                 "Accept: application/json;odata.metadata=minimal\n" +
		                 "\n" +
		                 "{\n" +
		                 "\"FILENAME\":\"WSO2MB-Test\",\n" +
		                 "\"TYPE\":\"WSO2 Message Broker-Test\"\n" +
		                 "}\n" +
		                 "\n" +
		                 "--changeset_77162fcd-b8da-41ac-a9f8-9357efbbd \n" +
		                 "Content-Type: application/http \n" +
		                 "Content-Transfer-Encoding: binary \n" +
		                 "Content-ID: 2\n" +
		                 "\n" +
		                 "PATCH " + webappURL + "/odata/" + serviceName + "/" + configId +
		                 "/FILES('WSO2DAS') HTTP/1.1\n" +
		                 "OData-Version: 4.0\n" +
		                 "Content-Type: application/json;odata.metadata=minimal\n" +
		                 "Accept: application/json;odata.metadata=minimal\n" +
		                 "\n" +
		                 "{\n" +
		                 "\"TEXT\":\"WSO2 Business Activity Monitor\"\n" +
		                 "}\n" +
		                 "\n" +
		                 "--changeset_77162fcd-b8da-41ac-a9f8-9357efbbd--\n" +
		                 "--batch_36522ad7-fc75-4b56-8c71-56071383e77b\n" +
		                 "Content-Type: application/http\n" +
		                 "Content-Transfer-Encoding:binary\n" +
		                 "\n" +
		                 "GET " + webappURL + "/odata/" + serviceName + "/" + configId + "/FILES HTTP/1.1\n" +
		                 "Accept: application/json;odata.metadata=minimal\n" +
		                 "\n" +
		                 "--batch_36522ad7-fc75-4b56-8c71-56071383e77b--";
		Map<String, String> headers = new HashMap<>();
		headers.put("Prefer", "odata.continue-on-error");
		headers.put("Accept", "application/json");
		headers.put("OData-Version", "4.0");
		headers.put("Content-Type", "multipart/mixed;boundary=batch_36522ad7-fc75-4b56-8c71-56071383e77b");
		Object[] response = sendPOST(endpoint, content, headers);
		Assert.assertEquals(response[0], ODataTestUtils.ACCEPTED);
		Assert.assertTrue(response[1].toString().indexOf("WSO2 Message Broker") !=
		                  response[1].toString().lastIndexOf("WSO2 Message Broker"));
	}

}
