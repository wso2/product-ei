package org.wso2.carbon.esb.mediators.clone;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.IOException;
import java.rmi.RemoteException;

/**
 * Test clone mediator if it reach error sequence on error.
 * This test case is for fix done for ESBjAVA-4913
 */
public class ESBJAVA4913HandleExceptionTest extends ESBIntegrationTest {
    private LogViewerClient logViewerClient = null;

    @BeforeClass(alwaysRun = true)
    public void deployService() throws Exception {
        // Initializing server configuration
        super.init();
        // Deploying the artifact defined in the cloneMediatorHandleException.xml.
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/clone/cloneMediatorHandleException.xml");
        // Initialize log viewer client
        logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
        // Clear all system logs
        logViewerClient.clearLogs();
    }

    @AfterClass(alwaysRun = true)
    public void unDeployService() throws Exception {
        // Remove deployed artifact
        super.cleanup();
        // Cleanup all system logs
        logViewerClient.clearLogs();
    }

    /**
     * Verifies whether the mediator reach error sequence on error while executing.
     *
     * @throws InterruptedException
     * @throws RemoteException
     */
    @Test(groups = "wso2.esb", description = "Check if clone mediator reach error sequence on error.")
    public void testExceptionHandlingInCloneMediator()
            throws InterruptedException, IOException {

        final String expectedErrorMsg = "This is error sequence from sequenceOne";
        boolean isExpectedErrorMessage = false;
        // invoking the service through the test api.
        try {
            HttpRequestUtil.sendGetRequest(getApiInvocationURL("clonetest"), "");
        } catch (Exception e) {
            // Ignore read timeout from get request.
        }
        Thread.sleep(2000);
        LogEvent[] logs = logViewerClient.getAllSystemLogs();

        for (LogEvent logEvent : logs) {
            String message = logEvent.getMessage();
            if (message.contains(expectedErrorMsg)) {
                isExpectedErrorMessage = true;
                break;
            }
        }
        /*
         * Asserting the results here. If there's no logs from error sequence, then the
         * assertion should fail.
         */
        Assert.assertTrue(isExpectedErrorMessage, "Error sequence logs not found in the LOG stream.");
    }

}
