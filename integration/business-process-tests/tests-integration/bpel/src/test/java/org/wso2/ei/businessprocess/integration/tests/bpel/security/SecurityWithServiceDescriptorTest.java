/*
*Copyright (c) 2005-2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/

package org.wso2.ei.businessprocess.integration.tests.bpel.security;

import junit.framework.Assert;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.neethi.Policy;
import org.apache.neethi.PolicyEngine;
import org.apache.rampart.RampartMessageData;
import org.apache.rampart.policy.model.CryptoConfig;
import org.apache.rampart.policy.model.RampartConfig;
import org.apache.ws.security.WSPasswordCallback;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.ei.businessprocess.integration.common.clients.bpel.BpelInstanceManagementClient;
import org.wso2.ei.businessprocess.integration.common.clients.bpel.BpelPackageManagementClient;
import org.wso2.ei.businessprocess.integration.common.clients.bpel.BpelProcessManagementClient;
import org.wso2.ei.businessprocess.integration.common.utils.BPSMasterTest;
import org.wso2.ei.businessprocess.integration.common.utils.BPSTestConstants;
import org.wso2.carbon.authenticator.stub.LogoutAuthenticationExceptionException;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.ei.businessprocess.integration.common.utils.RequestSender;
import org.wso2.carbon.bpel.stub.mgt.PackageManagementException;
import org.wso2.carbon.bpel.stub.mgt.types.LimitedInstanceInfoType;
import org.wso2.carbon.utils.CarbonUtils;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Properties;

public class SecurityWithServiceDescriptorTest extends BPSMasterTest implements CallbackHandler {

	private static final Log log = LogFactory.getLog(SecurityWithServiceDescriptorTest.class);

	LimitedInstanceInfoType instanceInfo = null;
	BpelPackageManagementClient bpelPackageManagementClient;
	BpelProcessManagementClient bpelProcessManagementClient;
	BpelInstanceManagementClient bpelInstanceManagementClient;
	RequestSender requestSender;

	public void setEnvironment() throws Exception {
		init();
		bpelPackageManagementClient = new BpelPackageManagementClient(backEndUrl, sessionCookie);
		bpelProcessManagementClient = new BpelProcessManagementClient(backEndUrl, sessionCookie);
		bpelInstanceManagementClient = new BpelInstanceManagementClient(backEndUrl, sessionCookie);
		requestSender = new RequestSender();
	}

	@BeforeClass(alwaysRun = true) public void deployArtifact() throws Exception {
		setEnvironment();
		uploadBpelForTest("SecuredWithServiceDescriptorProcess");
	}

	@Test(groups = { "wso2.bps",
	                 "wso2.bps.security" }, description = "BPEL security test scenario - secure BPEL process with service.xml file") public void securityWithServiceDescriptorTest()
			throws Exception {
		requestSender.waitForProcessDeployment(backEndUrl + "SWSDPService");
//		FrameworkConstants.start();

		String securityPolicyPath =
				FrameworkPathUtil.getSystemResourceLocation() + BPSTestConstants.DIR_ARTIFACTS +
				File.separator + BPSTestConstants.DIR_POLICY + File.separator + "utpolicy.xml";

		String endpointHttpS = "https://localhost:9645/services/SWSDPService";

		String trustStore =
				CarbonUtils.getCarbonHome() + File.separator + "repository" + File.separator +
				"resources" +
				File.separator + "security" + File.separator + "wso2carbon.jks";
		String clientKey = trustStore;
		OMElement result;

		System.setProperty("javax.net.ssl.trustStore", trustStore);
		System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");

		if (log.isDebugEnabled()) {
			log.debug("Carbon Home: " + CarbonUtils.getCarbonHome());
		}
		ConfigurationContext ctx = ConfigurationContextFactory
				.createConfigurationContextFromFileSystem(
						CarbonUtils.getCarbonHome() + File.separator + "repository" +
						File.separator + "deployment" + File.separator + "client", null);
		ServiceClient sc = new ServiceClient(ctx, null);
		sc.engageModule("addressing");
		sc.engageModule("rampart");

		Options opts = new Options();

		opts.setTo(new EndpointReference(endpointHttpS));
		log.info(endpointHttpS);

		opts.setAction("urn:swsdp");

		log.info("SecurityPolicyPath " + securityPolicyPath);
		opts.setProperty(RampartMessageData.KEY_RAMPART_POLICY,
		                 loadPolicy(securityPolicyPath, clientKey, "admin"));
		sc.setOptions(opts);
		result = sc.sendReceive(
				AXIOMUtil.stringToOM("<p:swsdp xmlns:p=\"http://wso2.org/bpel/sample.wsdl\">\n" +
				                     "      <TestPart>ww</TestPart>\n" +
				                     "   </p:swsdp>"));
		log.info(result.getFirstElement().getText());
		Assert.assertFalse("Incorrect Test Result: " + result.toString(),
		                   !result.toString().contains("ww World"));
	}

	@AfterClass(alwaysRun = true) public void cleanup()
			throws PackageManagementException, InterruptedException, RemoteException,
			       LogoutAuthenticationExceptionException {
		bpelPackageManagementClient.undeployBPEL("SecuredWithServiceDescriptorProcess");
		this.loginLogoutClient.logout();
	}

	private static Policy loadPolicy(String xmlPath, String clientKey, String userName)
			throws Exception {

		StAXOMBuilder builder = new StAXOMBuilder(xmlPath);
		Policy policy = PolicyEngine.getPolicy(builder.getDocumentElement());

		RampartConfig rc = new RampartConfig();

		rc.setUser(userName);
		rc.setUserCertAlias("wso2carbon");
		rc.setEncryptionUser("wso2carbon");
		rc.setPwCbClass(SecurityWithServiceDescriptorTest.class.getName());

		CryptoConfig sigCryptoConfig = new CryptoConfig();
		sigCryptoConfig.setProvider("org.apache.ws.security.components.crypto.Merlin");

		Properties prop1 = new Properties();
		prop1.put("org.apache.ws.security.crypto.merlin.keystore.type", "JKS");
		prop1.put("org.apache.ws.security.crypto.merlin.file", clientKey);
		prop1.put("org.apache.ws.security.crypto.merlin.keystore.password", "wso2carbon");
		sigCryptoConfig.setProp(prop1);

		CryptoConfig encrCryptoConfig = new CryptoConfig();
		encrCryptoConfig.setProvider("org.apache.ws.security.components.crypto.Merlin");

		Properties prop2 = new Properties();
		prop2.put("org.apache.ws.security.crypto.merlin.keystore.type", "JKS");
		prop2.put("org.apache.ws.security.crypto.merlin.file", clientKey);
		prop2.put("org.apache.ws.security.crypto.merlin.keystore.password", "wso2carbon");
		encrCryptoConfig.setProp(prop2);

		rc.setSigCryptoConfig(sigCryptoConfig);
		rc.setEncrCryptoConfig(encrCryptoConfig);

		policy.addAssertion(rc);
		return policy;
	}

	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {

		WSPasswordCallback pwcb = (WSPasswordCallback) callbacks[0];
		String id = pwcb.getIdentifier();
		int usage = pwcb.getUsage();

		if (usage == WSPasswordCallback.USERNAME_TOKEN) {

			if ("admin".equals(id)) {
				pwcb.setPassword("admin");
			} else if ("admin@wso2.com".equals(id)) {
				pwcb.setPassword("admin123");
			}

		} else if (usage == WSPasswordCallback.SIGNATURE || usage == WSPasswordCallback.DECRYPT) {

			if ("wso2carbon".equals(id)) {
				pwcb.setPassword("wso2carbon");
			}
		}
	}
}