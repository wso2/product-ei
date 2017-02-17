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
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * This test case is written to verify the fix for https://wso2.org/jira/browse/DS-1042
 */
public class DS1042GetContentTypeSpecifiedTestCase extends DSSIntegrationTest {
	private static final Log log = LogFactory.getLog(DS1042GetContentTypeSpecifiedTestCase.class);

	private final String serviceName = "ResourcesSample";
	private String serviceEndPoint;

	@BeforeClass(alwaysRun = true)
	public void serviceDeployment() throws Exception {
		super.init();
		List<File> sqlFileLis = new ArrayList<>();
		sqlFileLis.add(selectSqlFile("CustomProducts.sql"));
		deployService(serviceName, createArtifact(getResourceLocation() + File.separator +	"samples" + File.separator +
		                                          "dbs" + File.separator + "rdbms" + File.separator +
		                                          "ResourcesSample.dbs",	sqlFileLis));
		serviceEndPoint = getServiceUrlHttp(serviceName) + "/";

	}

	@AfterClass(alwaysRun = true)
	public void destroy() throws Exception {
		deleteService(serviceName);
		cleanup();
	}

	@Test(groups = "wso2.dss", description = "test the service when Content-Type is specified in GET resource requests - xml",
			alwaysRun = true)
	public void testWithXML() throws Exception {
		HttpResponse result = getHttpResponse(serviceEndPoint + "products", "application/xml");
		Assert.assertNotNull(result, "Response null");
		Assert.assertTrue(result.getData().contains("<productCode>S24_3816</productCode><productName>1940 Ford Delivery " +
				                          "Sedan</productName><productLine>Vintage Cars</productLine>" +
				                          "<quantityInStock>6621</quantityInStock><buyPrice>48.64</buyPrice>" +
				                          "</Product>"), "Expected result not found");
		log.info("data service returns correct response when \"application-xml\" content-type is specified in" +
		         " GET-method");
	}

	@Test(groups = "wso2.dss", description = "test the service when Content-Type is specified in GET resource requests - json",
			alwaysRun = true, dependsOnMethods = "testWithXML")
	public void contentTypetestWithJson() throws Exception {
		HttpResponse result = getHttpResponse(serviceEndPoint + "products", "application/json");
		Assert.assertNotNull(result, "Response null");
		Assert.assertTrue(result.getData().contains("{\"productCode\":\"S10_1678\",\"productName\":\"1969 Harley " +
		                                            "Davidson Ultimate Chopper\",\"productLine\":\"Motorcycles\",\"" +
		                                            "quantityInStock\":7933,\"buyPrice\":48.81}"),
		                  "Expected result not found");
		log.info("data service returns correct response when \"application-json\" content-type is specified in" +
		         " GET-method");
	}
	/**
	 * This method will "Accept" header Types "application/json", etc..
	 * @param endpoint service endpoint
	 * @param acceptType header type
	 * @return HttpResponse
	 * @throws Exception
	 */
	private HttpResponse getHttpResponse(String endpoint, String acceptType) throws Exception {

		if (endpoint.startsWith("http://")) {
			URL url = new URL(endpoint);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			conn.setRequestProperty("Accept", acceptType);
			conn.setRequestProperty("Content-Type", acceptType);
			conn.setRequestProperty("charset", "UTF-8");
			conn.setReadTimeout(10000);
			conn.connect();
			// Get the response
			StringBuilder sb = new StringBuilder();
			BufferedReader rd = null;
			try {
				rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line;
				while ((line = rd.readLine()) != null) {
					sb.append(line);
				}
			} catch (FileNotFoundException ignored) {
			} finally {
				if (rd != null) {
					rd.close();
				}
			}
			return new HttpResponse(sb.toString(), conn.getResponseCode());
		}
		return null;
	}
}
