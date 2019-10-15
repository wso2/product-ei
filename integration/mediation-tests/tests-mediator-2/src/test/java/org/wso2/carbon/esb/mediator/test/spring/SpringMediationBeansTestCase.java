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
package org.wso2.carbon.esb.mediator.test.spring;

import org.apache.axis2.AxisFault;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.data.xsd.LogEvent;
import org.wso2.carbon.registry.resource.stub.ResourceAdminServiceExceptionException;
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;
import java.net.URL;
import java.rmi.RemoteException;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;

/**
 * Related to https://wso2.org/jira/browse/ESBJAVA-3291
 * This class tests whether the SpringMediator throws proper error while the 
 * custom java class having any problem. 
 */
public class SpringMediationBeansTestCase extends ESBIntegrationTest {

	private final String SPRING_XML_LOCATION =  "/artifacts/ESB/mediatorconfig/spring";
 
	private LogViewerClient logViewerClient;

	@BeforeClass(alwaysRun = true)
	public void setEnvironment() throws Exception {

		super.init();
		clearUploadedResource();
		uploadResourcesToConfigRegistry();
		loadESBConfigurationFromClasspath(SPRING_XML_LOCATION + "/spring_mediation_error.xml");
		logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());

	}

	@AfterClass(alwaysRun = true)
	public void destroy() throws Exception {
		try {
			clearUploadedResource();
		} finally {
            super.cleanup();
		}
	}

	@SetEnvironment(executionEnvironments = { ExecutionEnvironment.STANDALONE })
	@Test(groups = { "wso2.esb", "localOnly" }, description = "Spring Mediator - Provide proper error message when problem in the custom java class")
	public void testBeanSpringMediation() throws AxisFault {

		// To check whether the correct error message is getting printed 
		boolean responseInLog = false;

		try {
			axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null,
					"IBM");

		} catch (Exception axisFault) {
			try {
				LogEvent[] logs = logViewerClient.getAllRemoteSystemLogs();
				for (LogEvent logEvent : logs) {
					String message = logEvent.getMessage();
					if (message
							.contains("Error in org.wso2.carbon.test.mediator.errorMediator.ErrorMediator")) {
						responseInLog = true;
						break;
					}
				}

				Assert.assertTrue(
						responseInLog,
						" Fault: Error message mismatched, expected 'Error in org.wso2.carbon.test.mediator.errorMediator.ErrorMediator'");

			} catch (RemoteException e) {}
		}

	}

	private void uploadResourcesToConfigRegistry() throws Exception {

		ResourceAdminServiceClient resourceAdminServiceStub = new ResourceAdminServiceClient(
				contextUrls.getBackEndUrl(), getSessionCookie());

		resourceAdminServiceStub.deleteResource("/_system/config/spring");
		resourceAdminServiceStub.addCollection("/_system/config/", "spring",
				"", "Contains spring bean config files");
		
		resourceAdminServiceStub.addResource(
				"/_system/config/spring/spring_bean_for_error_client.xml","application/xml", "spring bean config files",
                new DataHandler(new FileDataSource( new File(getClass().getResource(
                				SPRING_XML_LOCATION +  "/utils/spring_bean_for_error_client.xml").getPath()))));
	}

	private void clearUploadedResource() throws InterruptedException,
			ResourceAdminServiceExceptionException, RemoteException {

		ResourceAdminServiceClient resourceAdminServiceStub = new ResourceAdminServiceClient(
				contextUrls.getBackEndUrl(), getSessionCookie());

		resourceAdminServiceStub.deleteResource("/_system/config/spring");
	}
}
