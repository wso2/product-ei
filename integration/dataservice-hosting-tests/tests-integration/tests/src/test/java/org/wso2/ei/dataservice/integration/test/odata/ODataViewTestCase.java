/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

import ca.uhn.hl7v2.model.v21.datatype.ST;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class contains OData View Test case, Where you can retrieve data from a VIEW.
 */
public class ODataViewTestCase extends DSSIntegrationTest {
	private final String serviceName = "ODataSampleSuperTenantService";
	private final String configId = "default";
	private final String viewName = "USACUSTOMERS";
	private String webAppUrl;

	@BeforeClass(alwaysRun = true)
	public void serviceDeployment() throws Exception {
		super.init();
		List<File> sqlFileLis = new ArrayList<>();
		sqlFileLis.add(selectSqlFile("CreateODataTables.sql"));
		sqlFileLis.add(selectSqlFile("Customers.sql"));
		deployService(serviceName,
		              createArtifact(getResourceLocation() + File.separator + "dbs" + File.separator + "odata" +
		                             File.separator + "ODataSampleSuperTenantService.dbs", sqlFileLis));
		webAppUrl = dssContext.getContextUrls().getWebAppURL();
	}

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        deleteService(serviceName);
        cleanup();
    }

	@Test(groups = { "wso2.dss" }, description = "OData View Test")
	public void validateViewTestCase() throws Exception {
		String endpoint = webAppUrl + "/odata/" + serviceName + "/" + configId + "/" + viewName;
		Map<String, String> headers = new HashMap<>();
		headers.put("Accept", "application/json");
		Object[] response = ODataTestUtils.sendGET(endpoint, headers);
		Assert.assertEquals(response[0], ODataTestUtils.OK);
		endpoint = webAppUrl + "/odata/" + serviceName + "/" + configId + "/";
		response = ODataTestUtils.sendGET(endpoint, headers);
		Assert.assertEquals(response[0], ODataTestUtils.OK);
		Assert.assertTrue(response[1].toString().contains("USA"));
		Assert.assertTrue(!response[1].toString().contains("France"));
		Assert.assertTrue(!response[1].toString().contains("Finland"));
	}

}