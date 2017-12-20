package samples.messaging;

import ballerina.net.http;
import ballerina.net.jms;
import ballerina.log;

@Description{value:"Place a coffee order reliably to queue"}
@http:configuration {basePath:"/coffee"}
service<http> coffeeOrderService {

    endpoint<jms:JmsClient> coffeeOrderJMSQueueEp {
        create jms:JmsClient(getConnectorConfig());
    }

    @http:resourceConfig {
        methods:["POST"],
        path:"/order"
    }
    resource coffeeResource (http:Request req, http:Response res) {
        json orderPayload = req.getJsonPayload();
        // Create a coffee order message
        jms:JMSMessage coffeeOrderMessage = jms:createTextMessage(getConnectorConfig());
        //Set the order content to the JMS message
        //Note : in this sample we do not validate the incoming message since that is not the scope of this sample.
        coffeeOrderMessage.setTextMessageContent(orderPayload.toString());
        //Send the order message to a JMS queue.
        coffeeOrderJMSQueueEp.send("CoffeeOrders", coffeeOrderMessage);
        //Set the response payload and send it to the caller. By this time the message is added to the queue and
        //would provide a guarantee to the caller on successful delivery of the message.
        json responsePayload = {"Status":"Coffee Order Processed"};
        http:HttpConnectorError respondError = null;
        res.setJsonPayload(responsePayload);
        respondError = res.send();

        if (respondError != null) {
            log:printError("Error while responding back to the client");
        }
    }
}

@Description{value:"Retreive the broker configuration"}
function getConnectorConfig () (jms:ClientProperties) {
    jms:ClientProperties properties = {initialContextFactory:"wso2mbInitialContextFactory",
                                       providerUrl:"amqp://admin:admin@carbon/carbon?brokerlist='tcp://localhost:5672'",
                                       connectionFactoryName:"QueueConnectionFactory",
                                       connectionFactoryType:"queue"};
    return properties;
}
