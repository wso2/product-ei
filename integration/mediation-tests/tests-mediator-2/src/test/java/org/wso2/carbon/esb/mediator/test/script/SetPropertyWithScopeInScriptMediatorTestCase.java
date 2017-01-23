package org.wso2.carbon.esb.mediator.test.script;

import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.stockquoteclient.StockQuoteClient;

public class SetPropertyWithScopeInScriptMediatorTestCase extends ESBIntegrationTest {

    private LogViewerClient logViewerClient;

    @BeforeClass(alwaysRun = true) public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/script_mediator/propertyWithScope.xml");
        logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
    }

    @AfterClass(alwaysRun = true) public void destroy() throws Exception {
        super.cleanup();
    }

    @Test(groups = "wso2.esb", description = "Set a property with axis2 scope in script mediator") public void testSetPropertyWithAxis2ScopeInScript()
            throws Exception {

        StockQuoteClient axis2Client1 = new StockQuoteClient();
        boolean setPropertyInLog = false;
        boolean removePropertyInLog = false;
        OMElement response;
        response = axis2Client1.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("TestProxy"), null, "WSO2");
        Thread.sleep(10000);
        LogEvent[] logs = logViewerClient.getAllRemoteSystemLogs();

        for (LogEvent logEvent : logs) {
            String message = logEvent.getMessage();
            if (message.contains("Axis2_Property = AXIS2_PROPERTY")) {
                setPropertyInLog = true;
                break;
            }
        }

        Assert.assertTrue(setPropertyInLog, " The property with axis2 scope is not set ");

        for (LogEvent logEvent : logs) {
            String message = logEvent.getMessage();
            if (message.contains("Axis2_Property_After_Remove = null")) {
                removePropertyInLog = true;
                break;
            }
        }

        Assert.assertTrue(removePropertyInLog, " The property with axis2 scope is not remove ");
    }

    @Test(groups = "wso2.esb", description = "Set a property with transport scope in script mediator") public void testSetPropertyWithTransportScopeInScript()
            throws Exception {

        StockQuoteClient axis2Client1 = new StockQuoteClient();
        boolean setPropertyInLog = false;
        boolean removePropertyInLog = false;
        OMElement response;
        response = axis2Client1.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("TestProxy"), null, "WSO2");
        Thread.sleep(10000);
        LogEvent[] logs = logViewerClient.getAllRemoteSystemLogs();

        for (LogEvent logEvent : logs) {
            String message = logEvent.getMessage();
            if (message.contains("Transport_Property = TRANSPORT_PROPERTY")) {
                setPropertyInLog = true;
                break;
            }
        }
        Assert.assertTrue(setPropertyInLog, " The property with transport scope is not set ");

        for (LogEvent logEvent : logs) {
            String message = logEvent.getMessage();
            if (message.contains("Transport_Property_After_Remove = null")) {
                removePropertyInLog = true;
                break;
            }
        }
        Assert.assertTrue(removePropertyInLog, " The property with axis2 transport is not remove ");
    }

    @Test(groups = "wso2.esb", description = "Set a property with operation scope in script mediator") public void testSetPropertyWithOperationScopeInScript()
            throws Exception {

        StockQuoteClient axis2Client1 = new StockQuoteClient();
        boolean setPropertyInLog = false;
        boolean removePropertyInLog = false;
        OMElement response;
        response = axis2Client1.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("TestProxy"), null, "WSO2");
        Thread.sleep(10000);
        LogEvent[] logs = logViewerClient.getAllRemoteSystemLogs();

        for (LogEvent logEvent : logs) {
            String message = logEvent.getMessage();
            if (message.contains("Operation_Property = OPERATION_PROPERTY")) {
                setPropertyInLog = true;
                break;
            }
        }
        Assert.assertTrue(setPropertyInLog, " The property with operation scope is not set ");

        for (LogEvent logEvent : logs) {
            String message = logEvent.getMessage();
            if (message.contains("Operation_Property_After_Remove = null")) {
                removePropertyInLog = true;
                break;
            }
        }
        Assert.assertTrue(removePropertyInLog, " The property with operation scope is not remove ");
    }
}
