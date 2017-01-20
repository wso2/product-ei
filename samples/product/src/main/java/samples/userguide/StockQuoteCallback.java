package samples.userguide;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.client.async.AxisCallback;

/**
 * 
 */
public class StockQuoteCallback implements AxisCallback {

    public void onMessage(org.apache.axis2.context.MessageContext messageContext) {
        System.out.println("Response received to the callback");
        OMElement result
                = messageContext.getEnvelope().getBody().getFirstElement();
        // Detach the result to make sure that the element we return to the sample client
        // is completely built
        result.detach();
        StockQuoteClient.InnerStruct.RESULT = result;
    }

    public void onFault(org.apache.axis2.context.MessageContext messageContext) {
        System.out.println("Fault received to the callback : " + messageContext.getEnvelope().
                getBody().getFault());
    }

    public void onError(Exception e) {
        System.out.println("Error inside callback : " + e);
    }

    public void onComplete() {
        StockQuoteClient.InnerStruct.COMPLETED = true;
    }
}
