package samples.reception;

import ballerina.net.jms;
import ballerina.net.http;
import ballerina.util;

@http:configuration {
    basePath:"/coffeeshop"
}
service<http> coffeeShop  {

    endpoint<jms:JmsClient> jmsEP {
        create jms:JmsClient(getConnectorConfig());
    }

    @http:resourceConfig {
        methods:["POST"],
        path:"/order"
    }
    resource placeOrder (http:Request httpReq, http:Response httpRes) {

        string requestQueueName = "RequestQueue";
        string responseQueueName = "ResponseQueue";
        int pollWaitTimeout = 2000;
        int orderRequestTimeToLive = 2000;

        // correlation ID is used as the order ID
        string correlationID = util:uuid();
        string messageSelector = "JMSCorrelationID = '" + correlationID + "'";

        // creating the payload
        json coffeeOrderJson = httpReq.getJsonPayload();

        // create the jms message with ReplyTo header and correlationID
        jms:JMSMessage jmsReq = jms:createTextMessage(getConnectorConfig());
        jmsReq.setTextMessageContent(coffeeOrderJson.toString());
        jmsReq.setCorrelationID(correlationID);
        // its important to set expiration in dual-channel scenarios
        jmsReq.setExpiration(orderRequestTimeToLive);
        jmsReq.setReplyTo(responseQueueName);

        // send to jms request queue
        jmsEP.send(requestQueueName, jmsReq);

        // poll and wait for response message using the correlationID selector
        jms:JMSMessage jmsRes = jmsEP.pollWithSelector(responseQueueName, pollWaitTimeout, messageSelector);

        // respond to the client accordingly
        // if the backend didn't reply within interval
        if (jmsRes == null) {
            httpRes.setStatusCode(504);
            httpRes.setJsonPayload({CutomerInfo:coffeeOrderJson["CustomerInfo"], OrderInfo:coffeeOrderJson["OrderInfo"], Error:"Coffee order has been decliend"});
            _ = httpRes.send();
            return;
        }

        // if the backend response received
        string responseBody = jmsRes.getTextMessageContent();
        var jsonContent,_ = <json> responseBody;
        jsonContent["CustomerInfo"] = coffeeOrderJson["CustomerInfo"];
        jsonContent["OrderInfo"] = coffeeOrderJson["OrderInfo"];
        httpRes.setJsonPayload(jsonContent);
        httpRes.setStatusCode(200);
        _ = httpRes.send();
    }
}

function getConnectorConfig () (jms:ClientProperties) {
    jms:ClientProperties properties = {   initialContextFactory:"wso2mbInitialContextFactory",
                                          providerUrl:"amqp://admin:admin@carbon/carbon?brokerlist='tcp://localhost:5672'",
                                          connectionFactoryName: "QueueConnectionFactory"};
    return properties;
}
