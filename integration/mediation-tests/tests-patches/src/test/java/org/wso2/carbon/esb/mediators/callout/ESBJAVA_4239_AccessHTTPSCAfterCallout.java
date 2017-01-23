package org.wso2.carbon.esb.mediators.callout;

import java.rmi.RemoteException;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.axis2client.AxisServiceClient;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

public class ESBJAVA_4239_AccessHTTPSCAfterCallout extends ESBIntegrationTest {
    private LogViewerClient logViewerClient;

    private static final String PROXY_SERVICE_NAME = "HTTPSCProxy";
    private static final String EXPECTED_LOG_MESSAGE = "Status Code inSequence = 500"; 

    @BeforeClass(alwaysRun = true)
    public void deployeService() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/callout/FetchHTTPSCWithCalloutMediator.xml");
        isProxyDeployed(PROXY_SERVICE_NAME);

    }

    @Test(groups = { "wso2.esb" }, description = "Test whether an HTTP SC can be retrieved after the callout mediator.")
    public void testFetchHTTP_SC_After_Callout_Mediator() throws RemoteException,
                                                         InterruptedException {
        final String proxyUrl = getProxyServiceURLHttp(PROXY_SERVICE_NAME);
        AxisServiceClient client = new AxisServiceClient();
        client.sendRobust(createPlaceOrderRequest(3.141593E0, 4, "IBM"), proxyUrl, "placeOrder");

        logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());

        // Wait till the log appears
        Thread.sleep(20000);
        
        LogEvent[] logs = logViewerClient.getAllSystemLogs();
        boolean isScFound = false;
        for (LogEvent logEvent : logs) {
            String message = logEvent.getMessage();
            if (message.contains(EXPECTED_LOG_MESSAGE)) {
                isScFound = true;
            }
        }
        
        Assert.assertTrue(isScFound, "The HTTP Status Code was not found in the log.");

    }

    /*
     * This method will create a request required for place orders
     */
    public static OMElement createPlaceOrderRequest(double purchPrice, int qty, String symbol) {
        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMNamespace ns = factory.createOMNamespace("http://services.samples", "m0");
        OMElement placeOrder = factory.createOMElement("placeOrder", ns);
        OMElement order = factory.createOMElement("order", ns);
        OMElement price = factory.createOMElement("price", ns);
        OMElement quantity = factory.createOMElement("quantity", ns);
        OMElement symb = factory.createOMElement("symbol", ns);
        price.setText(Double.toString(purchPrice));
        quantity.setText(Integer.toString(qty));
        symb.setText(symbol);
        order.addChild(price);
        order.addChild(quantity);
        order.addChild(symb);
        placeOrder.addChild(order);
        return placeOrder;
    }

    @AfterClass(alwaysRun = true)
    public void UndeployeService() throws Exception {
        super.cleanup();
    }

}
