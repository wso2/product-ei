package samples.messaging;

import ballerina.net.http;
import ballerina.net.jms;

@http:configuration {basePath:"/coffee"}
service<http> coffeeOrderService {

    endpoint<jms:JmsClient> jmsEP {
        create jms:JmsClient(getConnectorConfig());
    }

    @http:resourceConfig {
        methods:["POST"],
        path:"/order"
    }
    resource coffeeResource (http:Request req, http:Response res) {
        json orderPayload = req.getJsonPayload();
        // Create an empty Ballerina message.
        jms:JMSMessage queueMessage = jms:createTextMessage(getConnectorConfig());
        // Set a string payload to the message.
        queueMessage.setTextMessageContent(orderPayload.toString());
        // Send the Ballerina message to the JMS provider.
        jmsEP.send("CoffeeOrders", queueMessage);
        json responsePayload = {"Status":"Coffee Order Processed"};
        res.setJsonPayload(responsePayload);
        res.send();
    }
}

function getConnectorConfig () (jms:ClientProperties) {
    jms:ClientProperties properties = {initialContextFactory:"wso2mbInitialContextFactory",
                                       providerUrl:"amqp://admin:admin@carbon/carbon?brokerlist='tcp://localhost:5672'",
                                       connectionFactoryName:"QueueConnectionFactory",
                                       connectionFactoryType:"queue"};
    return properties;
}
