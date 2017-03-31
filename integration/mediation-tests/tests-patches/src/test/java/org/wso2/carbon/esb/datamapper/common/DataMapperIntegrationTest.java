/*
 *     Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *     WSO2 Inc. licenses this file to you under the Apache License,
 *     Version 2.0 (the "License"); you may not use this file except
 *     in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing,
 *    software distributed under the License is distributed on an
 *    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *    KIND, either express or implied.  See the License for the
 *    specific language governing permissions and limitations
 *    under the License.
 */

package org.wso2.carbon.esb.datamapper.common;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import javax.activation.DataHandler;

public class DataMapperIntegrationTest extends ESBIntegrationTest {

	private ResourceAdminServiceClient resourceAdminServiceClient;

	@BeforeClass(alwaysRun = true)
	public void setEnvironment() throws Exception {
		super.init();
		resourceAdminServiceClient = new ResourceAdminServiceClient(contextUrls.getBackEndUrl(),
		                                                            context.getContextTenant().getContextUser()
		                                                                   .getUserName(),
		                                                            context.getContextTenant().getContextUser()
		                                                                   .getPassword());
	}

	protected String sendRequest(String addUrl, String request, String contentType) throws IOException {
		String charset = "UTF-8";
		URLConnection connection = new URL(addUrl).openConnection();
		connection.setDoOutput(true);
		connection.setRequestProperty("Accept-Charset", charset);
		connection.setRequestProperty("Content-Type", contentType + ";charset=" + charset);
		OutputStream output = null;
		try {
			output = connection.getOutputStream();
			output.write(request.getBytes(charset));
		} finally {
			if (output != null) {
				output.close();
			}
		}
		InputStream response = connection.getInputStream();
		String out = "[Fault] No Response.";
		if (response != null) {
			StringBuilder sb = new StringBuilder();
			byte[] bytes = new byte[1024];
			int len;
			while ((len = response.read(bytes)) != -1) {
				sb.append(new String(bytes, 0, len));
			}
			out = sb.toString();
		}
		return out;
	}

	protected void uploadResourcesToGovernanceRegistry(String registryRoot, String artifactRoot, String dmConfig,
                                                        String inSchema, String outSchema) throws Exception {
		resourceAdminServiceClient.addCollection("/_system/governance/", registryRoot, "", "");

		resourceAdminServiceClient.addResource("/_system/governance/" + registryRoot + dmConfig, "text/plain", "",
		                                       new DataHandler(new URL("file:///" + getClass()
				                                       .getResource(artifactRoot + dmConfig).getPath())));

		resourceAdminServiceClient.addResource("/_system/governance/" + registryRoot + inSchema, "", "",
		                                       new DataHandler(new URL("file:///" + getClass()
				                                       .getResource(artifactRoot + inSchema).getPath())));

		resourceAdminServiceClient.addResource("/_system/governance/" + registryRoot + outSchema, "", "",
		                                       new DataHandler(new URL("file:///" + getClass()
				                                       .getResource(artifactRoot + outSchema).getPath())));
	}

	@AfterClass(alwaysRun = true)
	public void close() throws Exception {
		try {
			resourceAdminServiceClient.deleteResource("/_system/governance/datamapper");
		} finally {
			super.cleanup();
			Thread.sleep(3000);
			resourceAdminServiceClient = null;
		}
	}

}

