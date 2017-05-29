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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * This test is written to verify the fix for https://wso2.org/jira/browse/DS-879
 * Retrieving JSON objects for all the methods
 * CARBON-15262 need carbon4.4.2
 *
 */
public class CARBON15262JsonGsonFormatterTenantModeTest extends DSSIntegrationTest {
	private final String serviceName = "H2SimpleJsonTest2";
	private String serviceEndPoint;

	private static final Log log = LogFactory.getLog(CARBON15262JsonGsonFormatterTenantModeTest.class);

	@BeforeClass(alwaysRun = true)
	public void serviceDeployment() throws Exception {
		super.init(TestUserMode.TENANT_ADMIN);
		List<File> sqlFileLis = new ArrayList<>();
		sqlFileLis.add(selectSqlFile("CreateTables.sql"));
		sqlFileLis.add(selectSqlFile("Offices.sql"));
		deployService(serviceName, createArtifact(getResourceLocation() + File.separator + "dbs" + File.separator +
		                                          "rdbms" + File.separator + "h2" + File.separator +
		                                          "H2SimpleJsonTest2.dbs", sqlFileLis));
		serviceEndPoint = getServiceUrlHttp(serviceName) + "/";

	}

	@AfterClass(alwaysRun = true) public void destroy() throws Exception {
		deleteService(serviceName);
		cleanup();
	}

	@Test(groups = "wso2.dss", description = "Invoking Request with PUT method", dependsOnMethods = "performJsonPostMethodWithRequestResponseTest")
	public void performJsonPutMethodTest() {
		String postUpdateOfficePayload = "{ \"_put_updateoffice\" : { \"officeCode\" : \"1\" , \"city\" : \"Colombo\"" +
		                                 " , \"telephone\" : \"+94 112954273\" , \"address1\" : \"Trace Expert City\" " +
		                                 ", \"address2\" : \"Colombo\" , \"state\" : null , \"country\" : \"LK\" , " +
		                                 "\"postalcode\" : \"10110\" , \"territory\" : \"Western\" } }";
		String response = getHttpResponse(serviceEndPoint + "updateOffice", "PUT", postUpdateOfficePayload);
		Assert.assertTrue(response.contains("SUCCESSFUL"), "PUT method failed");
	}

	@Test(groups = "wso2.dss", description = "Invoking Request with POST method with request response")
	public void performJsonPostMethodWithRequestResponseTest() {
		String postInsertPaymentPayload = "{ \"_post_insertoffice\" : { \"officeCode\" : \"9\" , \"city\" : \"Colombo\"" +
		                                  " , \"telephone\" : \"+94 112954273\" , \"address1\" : \"No 20, Palm grove\" " +
		                                  ", \"address2\" : \"Colombo\" , \"state\" : null , \"country\" : \"LK\" , " +
		                                  "\"postalcode\" : \"10110\" , \"territory\" : \"Western\" } }";
		String response = getHttpResponse(serviceEndPoint + "insertOffice", "POST", postInsertPaymentPayload);
		Assert.assertTrue(response.contains("SUCCESSFUL"), "POST method failed");

	}

	@Test(groups = "wso2.dss", description = "Invoking Request with POST method with response")
	public void performJsonPostMethodTest() {
		String postInsertPaymentPayload = "{ \"_post_insertbalance\" : { \"balance\" :12555.23 } }";
		String response = getHttpResponse(serviceEndPoint + "insertBalance", "POST", postInsertPaymentPayload);
		Assert.assertTrue(response.contains("GeneratedKeys"), "POST method failed");

	}

	@Test(groups = "wso2.dss", description = "Invoking Request with DELETE method")
	public void performJsonDeleteMethodTest() {
		String response = getHttpResponse(serviceEndPoint + "deleteOffice/" +Integer.toString(2), "DELETE", null);
		Assert.assertTrue(response.contains("SUCCESSFUL"), "DELETE method failed");
	}

	@Test(groups = "wso2.dss", description = "Invoking Request with GET method")
	public void performJsonGetMethodTest() {
		String response = getHttpResponse(serviceEndPoint + "getCountries", "GET", null);
		Assert.assertTrue(response.contains("country"), "GET method failed");
	}

	private String getHttpResponse(String endpoint, String requestMethod, String payload) {
		StringBuilder jsonString = new StringBuilder();
		BufferedReader br= null;
		try {
			String line;
			URL url = new URL(endpoint);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestProperty("charset", "UTF-8");
			connection.setReadTimeout(10000);
			connection.setRequestMethod(requestMethod);
			connection.setRequestProperty("Accept", "application/json");
			if (null != payload) {
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setRequestProperty("Content-Length", String.valueOf(payload.length()));
				OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
				writer.write(payload);
				writer.close();
			}
			br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			while (null != (line = br.readLine())) {
				jsonString.append(line);
			}
			connection.disconnect();
		} catch (IOException e) {
			log.error("IO exception occurred, " + e.getMessage());
		} finally {
			try {
				if (null != br) {
					br.close();
				}
			} catch (IOException e) {
				log.error("IO exception occurred while closing the reader, " + e.getMessage());
			}
		}
		return jsonString.toString();
	}
}
