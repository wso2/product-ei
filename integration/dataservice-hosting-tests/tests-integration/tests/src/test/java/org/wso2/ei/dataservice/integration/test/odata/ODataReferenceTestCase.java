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

import static org.wso2.ei.dataservice.integration.test.odata.ODataTestUtils.sendDELETE;
import static org.wso2.ei.dataservice.integration.test.odata.ODataTestUtils.sendGET;
import static org.wso2.ei.dataservice.integration.test.odata.ODataTestUtils.sendPOST;
import static org.wso2.ei.dataservice.integration.test.odata.ODataTestUtils.sendPUT;

/**
 * This class contains OData specific test cases. to verify the functionality of odata navigation references.
 */
public class ODataReferenceTestCase extends DSSIntegrationTest {
	private final String serviceName = "ODataSampleService";
	private final String configId = "default";
	private String webAppUrl;

	@BeforeClass(alwaysRun = true)
	public void serviceDeployment() throws Exception {
		super.init();
		List<File> sqlFileLis = new ArrayList<>();
		sqlFileLis.add(selectSqlFile("CreateODataTables.sql"));
		sqlFileLis.add(selectSqlFile("Customers.sql"));
		sqlFileLis.add(selectSqlFile("FIlesWithFIlesRecords.sql"));
		deployService(serviceName, createArtifact(getResourceLocation() + File.separator + "dbs" + File.separator +
		                                          "odata" + File.separator + "ODataSampleService.dbs", sqlFileLis));
		webAppUrl = dssContext.getContextUrls().getWebAppURL();
	}

	@AfterClass(alwaysRun = true)
	public void destroy() throws Exception {
		deleteService(serviceName);
		cleanup();
	}

	@Test(groups = "wso2.dss", description = "testing the entity navigation references retrieval")
	public void validateNavigationPropertyReferencesTestCase() throws Exception {
		String endpoint = webAppUrl + "/odata/" + serviceName + "/" + configId + "/FILES('WSO2DSS')/FILERECORDS/$ref";
		Map<String, String> headers = new HashMap<>();
		headers.put("Accept", "application/json");
		Object[] response = sendGET(endpoint, headers);
		Assert.assertEquals(response[0], ODataTestUtils.OK);
		Assert.assertTrue(
				response[1].toString().contains("FILERECORDS(1)") && response[1].toString().contains("FILERECORDS(4)"),
				"Navigation property reference retrieval failed");
		endpoint = webAppUrl + "/odata/" + serviceName + "/" + configId + "/FILERECORDS(2)/FILES/$ref";
		response = sendGET(endpoint, headers);
		Assert.assertEquals(response[0], ODataTestUtils.OK);
		Assert.assertTrue(response[1].toString().contains("FILES('WSO2DSS')"),
		                  "Navigation property reference retrieval failed");
	}

	@Test(groups = "wso2.dss", description = "testing the the entity navigation property retrieval")
	public void validateNavigationPropertyTestCase() throws Exception {
		String endpoint = webAppUrl + "/odata/" + serviceName + "/" + configId + "/FILES('WSO2DSS')/FILERECORDS";
		Map<String, String> headers = new HashMap<>();
		headers.put("Accept", "application/json");
		Object[] response = sendGET(endpoint, headers);
		Assert.assertEquals(response[0], ODataTestUtils.OK);
	}

	@Test(groups = "wso2.dss", description = "testing the update operation of the entity navigation property", dependsOnMethods = {
			"validateNavigationPropertyReferencesTestCase", "validateNavigationPropertyTestCase" })
	public void validateUpdateReferenceTestCase() throws Exception {
		String endpoint = webAppUrl + "/odata/" + serviceName + "/" + configId + "/FILES('WSO2DSS')/FILERECORDS/$ref";
		Map<String, String> headers = new HashMap<>();
		String content = "{\"@odata.id\": \"" + webAppUrl + "/odata/" + serviceName + "/" + configId +
		                 "/FILERECORDS(5)\"}";
		headers.put("Accept", "application/json");
		Object[] response = sendPOST(endpoint, content, headers);
		Assert.assertEquals(response[0], ODataTestUtils.NO_CONTENT);
		endpoint = webAppUrl + "/odata/" + serviceName + "/" + configId + "/FILERECORDS(5)/FILES/$ref";
		response = sendGET(endpoint, headers);
		Assert.assertEquals(response[0], ODataTestUtils.OK);
		Assert.assertTrue(response[1].toString().contains("FILES('WSO2DSS')"),
		                  "Navigation property reference retrieval failed");
		endpoint = webAppUrl + "/odata/" + serviceName + "/" + configId + "/FILERECORDS(5)/FILES/$ref";
		content = " {	\"@odata.id\": \"" + webAppUrl + "/odata/" + serviceName + "/" + configId +
		          "/FILES('WSO2ML')\"} ";
		headers.put("Accept", "application/json");
		int responseCode = sendPUT(endpoint, content, headers);
		Assert.assertEquals(responseCode, ODataTestUtils.NO_CONTENT);
		endpoint = webAppUrl + "/odata/" + serviceName + "/" + configId + "/FILES('WSO2ML')/FILERECORDS/$ref";
		response = sendGET(endpoint, headers);
		Assert.assertEquals(response[0], ODataTestUtils.OK);
		Assert.assertTrue(response[1].toString().contains("FILERECORDS(5)"),
		                  "Navigation property reference retrieval failed");
	}

	@Test(groups = "wso2.dss", description = "testing the delete operation of the entity navigation property", dependsOnMethods = "validateUpdateReferenceTestCase")
	public void validateDeleteReferenceTestCase() throws Exception {
		String endpoint = webAppUrl + "/odata/" + serviceName + "/" + configId + "/FILES('WSO2DSS')/FILERECORDS/$ref" +
		                  "?$id=" + webAppUrl + "/odata/" + serviceName + "/" + configId + "/FILERECORDS(5)";
		Map<String, String> headers = new HashMap<>();
		headers.put("Accept", "application/json");
		int responseCode = sendDELETE(endpoint, headers);
		Assert.assertEquals(responseCode, ODataTestUtils.NO_CONTENT);
		endpoint = webAppUrl + "/odata/" + serviceName + "/" + configId + "/FILERECORDS(5)/FILES/$ref";
		Object[] response = sendGET(endpoint, headers);
		Assert.assertEquals(response[0], ODataTestUtils.NO_CONTENT);
		endpoint = webAppUrl + "/odata/" + serviceName + "/" + configId + "/FILERECORDS(1)/FILES/$ref";
		responseCode = sendDELETE(endpoint, headers);
		Assert.assertEquals(responseCode, ODataTestUtils.NO_CONTENT);
		response = sendGET(endpoint, headers);
		Assert.assertEquals(response[0], ODataTestUtils.NO_CONTENT);
	}

	@Test(groups = { "wso2.dss" }, description = "testing the add entity with navigation bindings ")
	public void validateAddEntityWithReferenceTestCase() throws Exception {
		String endpoint = webAppUrl + "/odata/" + serviceName + "/" + configId + "/FILERECORDS";
		String content = "{\n" +
						 "\"FILERECORDID\":11,\n" +
						 "\"DATA\":\"AEIATABPAEIAMQ==\",\n" +
						 "\"FILES@odata.bind\" : \"http://localhost:10963/odata/ODataSampleService/default/FILES('WSO2DSS')\"\n" +
						 "}";
		Map<String, String> headers = new HashMap<>();
		headers.put("Accept", "application/json");
		Object[] response = sendPOST(endpoint, content, headers);
		Assert.assertEquals(response[0], ODataTestUtils.CREATED);
		endpoint = webAppUrl + "/odata/" + serviceName + "/" + configId + "/FILERECORDS(11)/FILES/$ref";
		response = sendGET(endpoint, headers);
		Assert.assertEquals(response[0], ODataTestUtils.OK);
		Assert.assertTrue(response[1].toString().contains("FILES('WSO2DSS')"));
		endpoint = webAppUrl + "/odata/" + serviceName + "/" + configId + "/FILERECORDS(11)";
		response = sendGET(endpoint, headers);
		Assert.assertEquals(response[0], ODataTestUtils.OK);
		Assert.assertTrue(response[1].toString().contains("\"FILENAME\":\"WSO2DSS\""));
	}
}
