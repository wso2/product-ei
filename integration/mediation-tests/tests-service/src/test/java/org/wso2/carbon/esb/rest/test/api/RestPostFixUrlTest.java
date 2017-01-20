package org.wso2.carbon.esb.rest.test.api;

import java.io.File;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;

/**
 * Related to Patch Automation https://wso2.org/jira/browse/ESBJAVA-3260 This
 * class test Target URL not appending the Context URL in REST_URL_POSTFIX.
 */

public class RestPostFixUrlTest extends ESBIntegrationTest {

	private LogViewerClient logViewerClient;

	@BeforeClass(alwaysRun = true)
	public void init() throws Exception {
		super.init();
		loadESBConfigurationFromClasspath(File.separator + "artifacts"
				+ File.separator + "ESB" + File.separator + "synapseconfig"
				+ File.separator + "rest" + File.separator
				+ "RestPostFixUrl.xml");
		logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(),
				getSessionCookie());
	}

	@Test(groups = { "wso2.esb" }, description = "Sending a Message Via REST with additional resource")
	public void testRESTURITemplateWithContextURL() throws Exception {

		/** To check whether the Context URL part "anotherParam" available **/
		boolean responseInLog = false;
		/*
		 * sending request from Client API with additional resource
		 * "anotherParam" services/client/anotherParam
		 */

		HttpResponse response = HttpRequestUtil.sendGetRequest(
				getApiInvocationURL("services/client/anotherParam"), null);
		
		LogEvent[] logs = logViewerClient.getAllRemoteSystemLogs();

		for (LogEvent logEvent : logs) {
			String message = logEvent.getMessage();

			/**
			 * If "To : /services/test/anotherParam" will be printed in the Log,
			 * the Target URL is appending the Context URL
			 **/
			if (message.contains("anotherParam")) {
				responseInLog = true;
				break;
			}

		}

		Assert.assertFalse(responseInLog,
				" Target URL is wrong. It appends the context URL part also.");

	}
	
	@Test(groups = { "wso2.esb" }, description = "Sending a Message Via REST with additional resource")
	public void testRESTURITemplateWithAdditionalParam() throws Exception {

		/** To check whether the Context URL part "anotherParam" available **/
		boolean responseInLog = false;
		/*
		 * sending request from Client API with additional resource - "anotherParam" 
		 * & prameter - "foo"
		 *  services/client/anotherParam/foo
		 */

		HttpResponse response = HttpRequestUtil.sendGetRequest(
				getApiInvocationURL("services/client/anotherParam/foo"), null);
		
		LogEvent[] logs = logViewerClient.getAllRemoteSystemLogs();

		for (LogEvent logEvent : logs) {
			String message = logEvent.getMessage();
			/**
			 * "To : /services/test/foo" should be printed in the Log,
			 **/
			if (message.contains("/services/test/foo")) {
				responseInLog = true;
				break;
			}
		}

		Assert.assertTrue(responseInLog,
				" Target URL is wrong. expected /services/test/foo ");

	}

	@AfterClass(alwaysRun = true)
	public void destroy() throws Exception {
		super.cleanup();
	}

}