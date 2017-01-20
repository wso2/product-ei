package org.wso2.carbon.esb.mediators.clone;

import java.rmi.RemoteException;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.axis2client.AxisServiceClient;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

/**
 * 
 * This test is used to verify that the EmptyStackException does NOT occur when
 * Iterate and clone mediators are used together with
 * <code>continueParent</code> attribute is set to <code>true</code>
 *
 */
public class ESBJAVA3412EmptyStackExceptionTest extends ESBIntegrationTest {
	private LogViewerClient logViewerClient = null;

	@BeforeClass(alwaysRun = true)
	public void deployService() throws Exception {
		/* initializing server configuration */
		super.init();
		/* deploying the artifact defined in the proxy_service.xml */

		loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/clone/cloneMediatorEmptyStackException.xml");

		logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(),
				getSessionCookie());
	}

	/**
	 * Checks and verifies whether the EmptyStackException is NOT thrown when
	 * <code>Iterate</code> and <code>Clone</code> mediators are used in
	 * combination with the continueParent attribute set to <code>true</code>
	 * 
	 * @throws InterruptedException
	 * @throws RemoteException
	 */
	@Test(groups = "wso2.esb", description = "Checking for Empty Stack Exception when clone mediators are used with continue parent attribute set to true")
	public void testForEmptyStackAfterCloned_With_ContinueParent()
			throws InterruptedException, RemoteException {
		final String expectedErrorMsg = "Unexpected error executing task/async inject";
		final String expectedStackTrace = "java.util.Stack.peek";

		boolean isEmptyStackError = false;
		OMElement request = getSecurityRequest();

		// invoking the service through the proxy service
		AxisServiceClient client = new AxisServiceClient();

		final String proxyUrl = contextUrls.getServiceUrl() + "/TestProxy ";

		try {
			client.sendRobust(request, proxyUrl, "mediate");
		} catch (Exception e) {
			// Ignore it.
		}

		Thread.sleep(2000);

		LogEvent[] logs = logViewerClient.getAllSystemLogs();

		// Asserting both the ERROR message and the stack trace.

		for (LogEvent logEvent : logs) {
			String message = logEvent.getMessage();
			String stackTrace = logEvent.getStacktrace();
			if (message.contains(expectedErrorMsg)
					&& stackTrace.contains(expectedStackTrace)) {
				isEmptyStackError = true;
				break;
			}
		}

		/*
		 * Asserting the results here. If there's an Empty stack error, then the
		 * assertion should fail.
		 */
		Assert.assertTrue(!isEmptyStackError,
				"Empty Stack ERROR message was found in the LOG stream.");
	}

	@AfterClass(alwaysRun = true)
	public void unDeployService() throws Exception {
		/* undeploying deployed artifact */
		super.cleanup();
	}

	/**
	 * Builds the request in the required format.
	 * 
	 * @return request in custom format.
	 */
	private static OMElement getSecurityRequest() {
		OMFactory fac = OMAbstractFactory.getOMFactory();
		OMElement request = fac.createOMElement("security", null);
		OMElement securityRowOne = fac.createOMElement("security_row", null);
		OMElement securityRowTwo = fac.createOMElement("security_row", null);

		request.addChild(securityRowOne);
		request.addChild(securityRowTwo);

		securityRowOne.setText("111");
		securityRowTwo.setText("222");

		return request;
	}

}
