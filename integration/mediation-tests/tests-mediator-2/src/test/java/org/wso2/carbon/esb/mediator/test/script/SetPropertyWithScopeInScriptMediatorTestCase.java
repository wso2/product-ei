package org.wso2.carbon.esb.mediator.test.script;

import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.data.xsd.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.Utils;
import org.wso2.esb.integration.common.utils.clients.stockquoteclient.StockQuoteClient;

public class SetPropertyWithScopeInScriptMediatorTestCase extends ESBIntegrationTest {

    private LogViewerClient logViewerClient;

    @BeforeClass(alwaysRun = true) public void setEnvironment() throws Exception {
        super.init();
        logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
    }

    @AfterClass(alwaysRun = true) public void destroy() throws Exception {
        super.cleanup();
    }

    @Test(groups = "wso2.esb", description = "Set a property with axis2 scope in script mediator") public void testSetPropertyWithAxis2ScopeInScript()
            throws Exception {

        StockQuoteClient axis2Client1 = new StockQuoteClient();
        axis2Client1.sendSimpleStockQuoteRequest(
                getProxyServiceURLHttp("scriptMediatorSetPropertyWithScopeTestProxy"), null,
                "WSO2");

        boolean setPropertyInLog = Utils.checkForLog(logViewerClient, "Axis2_Property = AXIS2_PROPERTY", 10);
        Assert.assertTrue(setPropertyInLog, " The property with axis2 scope is not set ");

        boolean removePropertyInLog = Utils.checkForLog(logViewerClient, "Axis2_Property_After_Remove = null", 10);
        Assert.assertTrue(removePropertyInLog, " The property with axis2 scope is not remove ");
    }

    @Test(groups = "wso2.esb", description = "Set a property with transport scope in script mediator") public void testSetPropertyWithTransportScopeInScript()
            throws Exception {

        StockQuoteClient axis2Client1 = new StockQuoteClient();

        axis2Client1.sendSimpleStockQuoteRequest(
                getProxyServiceURLHttp("scriptMediatorSetPropertyWithScopeTestProxy"), null,
                "WSO2");

        boolean setPropertyInLog = Utils.checkForLog(logViewerClient, "Transport_Property = TRANSPORT_PROPERTY", 10);
        Assert.assertTrue(setPropertyInLog, " The property with transport scope is not set ");

        boolean removePropertyInLog = Utils.checkForLog(logViewerClient, "Transport_Property_After_Remove = null", 10);
        Assert.assertTrue(removePropertyInLog, " The property with axis2 transport is not remove ");
    }

    @Test(groups = "wso2.esb", description = "Set a property with operation scope in script mediator") public void testSetPropertyWithOperationScopeInScript()
            throws Exception {

        StockQuoteClient axis2Client1 = new StockQuoteClient();

        axis2Client1.sendSimpleStockQuoteRequest(
                getProxyServiceURLHttp("scriptMediatorSetPropertyWithScopeTestProxy"), null,
                "WSO2");

        boolean setPropertyInLog = Utils.checkForLog(logViewerClient, "Operation_Property = OPERATION_PROPERTY", 10);
        Assert.assertTrue(setPropertyInLog, " The property with operation scope is not set ");

        boolean removePropertyInLog = Utils.checkForLog(logViewerClient, "Operation_Property_After_Remove = null", 10);
        Assert.assertTrue(removePropertyInLog, " The property with operation scope is not remove ");
    }
}
