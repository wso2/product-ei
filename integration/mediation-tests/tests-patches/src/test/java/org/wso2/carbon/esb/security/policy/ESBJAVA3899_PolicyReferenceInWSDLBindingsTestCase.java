/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.esb.security.policy;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import javax.activation.DataHandler;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.http.HttpResponse;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.extensions.servers.httpserver.SimpleHttpClient;
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

public class ESBJAVA3899_PolicyReferenceInWSDLBindingsTestCase extends ESBIntegrationTest {
	String carFileName = "SecurityPolicyWSDLBindingCapp_1.0.0.car";

	@BeforeClass
	protected void init() throws Exception {
		super.init();
		uploadResourcesToConfigRegistry();
		uploadCapp(carFileName, new DataHandler(new URL("file:" + File.separator + File.separator +
		                                                getESBResourceLocation() + File.separator +
		                                                "car" + File.separator + carFileName)));
		TimeUnit.SECONDS.sleep(5);
		log.info(carFileName + " uploaded successfully");

	}

	@Test(groups = "wso2.esb", description = "Verify whether the WSDL Bindings contain Policyreference element when security policy is added via capp.")
	public void testPolicyReferenceInWSDLBindings() throws IOException, InterruptedException {
		String epr = "http://localhost:8280/services/SecpolicyCappTest?wsdl";
		final SimpleHttpClient httpClient = new SimpleHttpClient();
		HttpResponse response = httpClient.doGet(epr, null);
		Thread.sleep(4000);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		response.getEntity().writeTo(bos);
		String wsdlResponse = new String(bos.toByteArray());

		CharSequence expectedTag = "PolicyReference";
		Assert.assertTrue(wsdlResponse.contains(expectedTag));
	}

	private void uploadResourcesToConfigRegistry() throws Exception {
		ResourceAdminServiceClient resourceAdminServiceStub =
		                                                      new ResourceAdminServiceClient(
		                                                                                     contextUrls.getBackEndUrl(),
		                                                                                     getSessionCookie());

		resourceAdminServiceStub.addResource("/_system/config/repository/server-policy.xml",
		                                     "application/xml",
		                                     "policy file",
		                                     new DataHandler(
		                                                     new URL("file:///" +
		                                                             getESBResourceLocation() +
		                                                             "/security/ESBJAVA3899/server-policy.xml")));
		Thread.sleep(4000);
	}

	@AfterClass(alwaysRun = true)
	public void destroy() throws Exception {
		super.cleanup();
	}

}
