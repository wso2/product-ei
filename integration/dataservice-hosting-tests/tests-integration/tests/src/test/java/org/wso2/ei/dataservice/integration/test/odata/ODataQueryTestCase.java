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

import static org.wso2.ei.dataservice.integration.test.odata.ODataTestUtils.sendGET;

/**
 * This class contains OData specific test cases. to verify the functionality of odata query requests.
 */
public class ODataQueryTestCase extends DSSIntegrationTest {
	private final String serviceName = "ODataBatchRequestSampleService";
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
		                                          "odata" + File.separator + "ODataBatchRequestSampleService.dbs",
		                                          sqlFileLis));
		webAppUrl = dssContext.getContextUrls().getWebAppURL();
	}

	@AfterClass(alwaysRun = true)
	public void destroy() throws Exception {
		deleteService(serviceName);
		cleanup();
	}

	@Test(groups = "wso2.dss", description = "select query test")
	public void validateSelectQueryTestCase() throws Exception {
		String endpoint = webAppUrl + "/odata/" + serviceName + "/" + configId + "/FILES?$select=TYPE";
		Map<String, String> headers = new HashMap<>();
		headers.put("Accept", "application/json");
		Object[] response = sendGET(endpoint, headers);
		Assert.assertEquals(response[0], ODataTestUtils.OK);
		Assert.assertTrue(!response[1].toString().contains("FILENAME"));
	}

	@Test(groups = "wso2.dss", description = "top query test")
	public void validateTopQueryTestCase() throws Exception {
		String endpoint = webAppUrl + "/odata/" + serviceName + "/" + configId + "/CUSTOMERS?$top=1&$orderby=CONTACTFIRSTNAME%20desc";
		Map<String, String> headers = new HashMap<>();
		headers.put("Accept", "application/json");
		Object[] response = sendGET(endpoint, headers);
		Assert.assertEquals(response[0], ODataTestUtils.OK);
		Assert.assertTrue(response[1].toString().contains("Zbyszek"));
		Assert.assertTrue(!response[1].toString().contains("Yu"));
	}

	@Test(groups = "wso2.dss", description = "order by query test")
	public void validateOrderByQueryTestCase() throws Exception {
		String endpoint = webAppUrl + "/odata/" + serviceName + "/" + configId + "/CUSTOMERS?$top=1&$orderby=length(ADDRESSLINE1)";
		Map<String, String> headers = new HashMap<>();
		headers.put("Accept", "application/json");
		Object[] response = sendGET(endpoint, headers);
		Assert.assertEquals(response[0], ODataTestUtils.OK);
		Assert.assertTrue(response[1].toString().contains("Singapore"));
	}

	@Test(groups = "wso2.dss", description = "count query test")
	public void validateCountQueryTestCase() throws Exception {
		String endpoint = webAppUrl + "/odata/" + serviceName + "/" + configId + "/FILES/$count";
		Map<String, String> headers = new HashMap<>();
		headers.put("Accept", "application/json");
		Object[] response = sendGET(endpoint, headers);
		Assert.assertEquals(response[0], ODataTestUtils.OK);
		Assert.assertTrue(response[1].toString().contains("4"));
	}

	@Test(groups = "wso2.dss", description = "filter query test")
	public void validateFilterQueryTestCase() throws Exception {
		String endpoint = webAppUrl + "/odata/" + serviceName + "/" + configId + "/CUSTOMERS?$filter=COUNTRY%20eq%20%27Singapore%27";
		Map<String, String> headers = new HashMap<>();
		headers.put("Accept", "application/json");
		Object[] response = sendGET(endpoint, headers);
		Assert.assertEquals(response[0], ODataTestUtils.OK);
		Assert.assertTrue(response[1].toString().contains("Singapore"));
		Assert.assertTrue(!response[1].toString().contains("France"));
	}

	@Test(groups = "wso2.dss", description = "skip query test")
	public void validateSkipQueryTestCase() throws Exception {
		String endpoint = webAppUrl + "/odata/" + serviceName + "/" + configId + "/CUSTOMERS?$skip=1&$top=2&$orderby=CONTACTFIRSTNAME%20desc";
		Map<String, String> headers = new HashMap<>();
		headers.put("Accept", "application/json");
		Object[] response = sendGET(endpoint, headers);
		Assert.assertEquals(response[0], ODataTestUtils.OK);
		Assert.assertTrue(!response[1].toString().contains("Zbyszek"));
		Assert.assertTrue(response[1].toString().contains("Yu"));
		Assert.assertTrue(response[1].toString().contains("Yoshi"));
	}

	@Test(groups = "wso2.dss", description = "skip token query test")
	public void validateSkipTokenQueryTestCase() throws Exception {
		String endpoint = webAppUrl + "/odata/" + serviceName + "/" + configId + "/CUSTOMERS?$skiptoken=2";
		Map<String, String> headers = new HashMap<>();
		headers.put("Prefer", "odata.maxpagesize=5");
		headers.put("Accept", "application/json");
		Object[] response = sendGET(endpoint, headers);
		Assert.assertEquals(response[0], ODataTestUtils.OK);
		Assert.assertTrue(response[1].toString().contains("CUSTOMERS?$skiptoken=3"));
	}

	@Test(groups = "wso2.dss", description = "filter query test with not operator")
	public void validateFilterWithNotQueryTestCase() throws Exception {
		String endpoint = webAppUrl + "/odata/" + serviceName + "/" + configId + "/CUSTOMERS?$filter=not%20(COUNTRY%20eq%20%27Singapore%27)";
		Map<String, String> headers = new HashMap<>();
		headers.put("Accept", "application/json");
		Object[] response = sendGET(endpoint, headers);
		Assert.assertEquals(response[0], 200);
		Assert.assertFalse(response[1].toString().contains("Singapore"));
	}

	@Test(groups = "wso2.dss", description = "filter query test with not equal operator")
	public void validateFilterWithNotEqualQueryTestCase() throws Exception {
		String endpoint = webAppUrl + "/odata/" + serviceName + "/" + configId + "/CUSTOMERS?$filter=(COUNTRY%20ne%20%27Singapore%27)";
		Map<String, String> headers = new HashMap<>();
		headers.put("Accept", "application/json");
		Object[] response = sendGET(endpoint, headers);
		Assert.assertEquals(response[0], 200);
		Assert.assertFalse(response[1].toString().contains("Singapore"));
	}

	@Test(groups = "wso2.dss", description = "filter query test with endswith operator")
	public void validateFilterWith_EndsWithQueryTestCase() throws Exception {
		String endpoint = webAppUrl + "/odata/" + serviceName + "/" + configId + "/CUSTOMERS?$filter=endswith(CUSTOMERNAME,'graphique')";
		Map<String, String> headers = new HashMap<>();
		headers.put("Accept", "application/json");
		Object[] response = sendGET(endpoint, headers);
		Assert.assertEquals(response[0], 200);
		Assert.assertTrue(response[1].toString().contains("graphique"));
	}

	@Test(groups = "wso2.dss", description = "filter query test with greater than operator")
	public void validateFilterWithGreaterThanQueryTestCase() throws Exception {
		String endpoint = webAppUrl + "/odata/" + serviceName + "/" + configId + "/CUSTOMERS?$filter=CREDITLIMIT%20gt%2021000";
		Map<String, String> headers = new HashMap<>();
		headers.put("Accept", "application/json");
		Object[] response = sendGET(endpoint, headers);
		Assert.assertEquals(response[0], 200);
		Assert.assertFalse(response[1].toString().contains("21000"));
		Assert.assertFalse(response[1].toString().contains("\"CREDITLIMIT\":0.0"));
	}

	@Test(groups = "wso2.dss", description = "filter query test with greater than or equal operator")
	public void validateFilterWithGreaterThanOrEqualQueryTestCase() throws Exception {
		String endpoint = webAppUrl + "/odata/" + serviceName + "/" + configId + "/CUSTOMERS?$filter=CREDITLIMIT%20ge%2021000";
		Map<String, String> headers = new HashMap<>();
		headers.put("Accept", "application/json");
		Object[] response = sendGET(endpoint, headers);
		Assert.assertEquals(response[0], 200);
		Assert.assertTrue(response[1].toString().contains("21000"));
		Assert.assertFalse(response[1].toString().contains("\"CREDITLIMIT\":0.0"));
	}

	@Test(groups = "wso2.dss", description = "filter query test with less than operator")
	public void validateFilterWithLessThanQueryTestCase() throws Exception {
		String endpoint = webAppUrl + "/odata/" + serviceName + "/" + configId + "/CUSTOMERS?$filter=CREDITLIMIT%20lt%2021000";
		Map<String, String> headers = new HashMap<>();
		headers.put("Accept", "application/json");
		Object[] response = sendGET(endpoint, headers);
		Assert.assertEquals(response[0], 200);
		Assert.assertFalse(response[1].toString().contains("21000"));
		Assert.assertTrue(response[1].toString().contains("\"CREDITLIMIT\":0.0"));
	}

	@Test(groups = "wso2.dss", description = "filter query test with less than or equal operator")
	public void validateFilterWithLessThanOrEqualQueryTestCase() throws Exception {
		String endpoint = webAppUrl + "/odata/" + serviceName + "/" + configId + "/CUSTOMERS?$filter=CREDITLIMIT%20le%2021000";
		Map<String, String> headers = new HashMap<>();
		headers.put("Accept", "application/json");
		Object[] response = sendGET(endpoint, headers);
		Assert.assertEquals(response[0], 200);
		Assert.assertTrue(response[1].toString().contains("21000"));
		Assert.assertTrue(response[1].toString().contains("\"CREDITLIMIT\":0.0"));
	}

	@Test(groups = "wso2.dss", description = "filter query test with and operator")
	public void validateFilterWithAndQueryTestCase() throws Exception {
		String endpoint = webAppUrl + "/odata/" + serviceName + "/" + configId + "/CUSTOMERS?$filter=CREDITLIMIT%20lt%2021000%20and%20CREDITLIMIT%20gt%20500";
		Map<String, String> headers = new HashMap<>();
		headers.put("Accept", "application/json");
		Object[] response = sendGET(endpoint, headers);
		Assert.assertEquals(response[0], 200);
		Assert.assertTrue(response[1].toString().contains("11000"));
		Assert.assertFalse(response[1].toString().contains("\"CREDITLIMIT\":0.0"));
	}

	@Test(groups = "wso2.dss", description = "filter query test with or operator")
	public void validateFilterWithOrQueryTestCase() throws Exception {
		String endpoint = webAppUrl + "/odata/" + serviceName + "/" + configId + "/CUSTOMERS?$filter=CREDITLIMIT%20lt%2021000%20or%20CREDITLIMIT%20gt%20500";
		Map<String, String> headers = new HashMap<>();
		headers.put("Accept", "application/json");
		Object[] response = sendGET(endpoint, headers);
		Assert.assertEquals(response[0], 200);
		Assert.assertTrue(response[1].toString().contains("71800"));
		Assert.assertTrue(response[1].toString().contains("\"CREDITLIMIT\":0.0"));
	}


	@Test(groups = "wso2.dss", description = "filter query test with addition operator")
	public void validateFilterWithAdditionQueryTestCase() throws Exception {
		String endpoint = webAppUrl + "/odata/" + serviceName + "/" + configId + "/CUSTOMERS?$filter=(CREDITLIMIT%20add%205)%20lt%206%20and%20(CREDITLIMIT%20add%205)%20gt%202";
		Map<String, String> headers = new HashMap<>();
		headers.put("Accept", "application/json");
		Object[] response = sendGET(endpoint, headers);
		Assert.assertEquals(response[0], 200);
		Assert.assertTrue(response[1].toString().contains("\"CREDITLIMIT\":0.0"));
	}

	@Test(groups = "wso2.dss", description = "filter query test with subtraction operator")
	public void validateFilterWithSubtractionQueryTestCase() throws Exception {
		String endpoint = webAppUrl + "/odata/" + serviceName + "/" + configId + "/CUSTOMERS?$filter=(CREDITLIMIT%20sub%205)%20lt%20-1";
		Map<String, String> headers = new HashMap<>();
		headers.put("Accept", "application/json");
		Object[] response = sendGET(endpoint, headers);
		Assert.assertEquals(response[0], 200);
		Assert.assertTrue(response[1].toString().contains("\"CREDITLIMIT\":0.0"));
	}

	@Test(groups = "wso2.dss", description = "filter query test with multiplication operator")
	public void validateFilterWithMultiplicationQueryTestCase() throws Exception {
		String endpoint = webAppUrl + "/odata/" + serviceName + "/" + configId + "/CUSTOMERS?$filter=(CREDITLIMIT%20add%201)%20mul%205%20lt%206%20and%20(CREDITLIMIT%20add%201)%20mul%205%20gt%204";
		Map<String, String> headers = new HashMap<>();
		headers.put("Accept", "application/json");
		Object[] response = sendGET(endpoint, headers);
		Assert.assertEquals(response[0], 200);
		Assert.assertTrue(response[1].toString().contains("\"CREDITLIMIT\":0.0"));
	}

	@Test(groups = "wso2.dss", description = "filter query test with division operator")
	public void validateFilterWithDivisionQueryTestCase() throws Exception {
		String endpoint = webAppUrl + "/odata/" + serviceName + "/" + configId + "/CUSTOMERS?$filter=(CREDITLIMIT%20add%2010)%20div%205%20gt%201%20and%20(CREDITLIMIT%20add%2010)%20div%205%20lt%203";
		Map<String, String> headers = new HashMap<>();
		headers.put("Accept", "application/json");
		Object[] response = sendGET(endpoint, headers);
		Assert.assertEquals(response[0], 200);
		Assert.assertTrue(response[1].toString().contains("\"CREDITLIMIT\":0.0"));
	}

	@Test(groups = "wso2.dss", description = "filter query test with modulus operator")
	public void validateFilterWithModulusQueryTestCase() throws Exception {
		String endpoint = webAppUrl + "/odata/" + serviceName + "/" + configId + "/CUSTOMERS?$filter=(CREDITLIMIT%20add%2010)%20div%205%20gt%201%20and%20(CREDITLIMIT%20add%2010)%20div%205%20lt%203";
		Map<String, String> headers = new HashMap<>();
		headers.put("Accept", "application/json");
		Object[] response = sendGET(endpoint, headers);
		Assert.assertEquals(response[0], 200);
		Assert.assertTrue(response[1].toString().contains("\"CREDITLIMIT\":0.0"));
	}
}
