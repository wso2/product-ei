package samples.messaging;

import ballerina.net.jms;
import ballerina.net.http;
import ballerina.runtime;

@jms:configuration {
    initialContextFactory:"wso2mbInitialContextFactory",
    providerUrl:"amqp://admin:admin@carbon/carbon?brokerlist='tcp://localhost:5672'",
    connectionFactoryName:"QueueConnectionFactory",
    concurrentConsumers:300,
    acknowledgementMode:jms:CLIENT_ACKNOWLEDGE,
    destination:"CoffeeOrders"
}
service<jms> coffeeConsumerService {

    endpoint<http:HttpClient> orderDispatchEp {
        create http:HttpClient("http://localhost:9091/dispatch", {});
    }

    resource onMessage (jms:JMSMessage m) {
        // Retrieve the string payload using native function.
        string stringPayload = m.getTextMessageContent();
        http:HttpConnectorError pizzaSendError;
        // Print the retrieved payload.
        println("Payload: " + stringPayload + " received by processing service");

        http:Request pizzaOrderRequest = {};
        pizzaOrderRequest.setJsonPayload(stringPayload);

        http:Response pizzaResponse = {};
        pizzaResponse, pizzaSendError = orderDispatchEp.post("/order", pizzaOrderRequest);
        if (pizzaSendError != null) {
            println("Server responded with error, the downsteam service is not available");
            runtime:sleepCurrentThread(6000);
            m.acknowledge(jms:DELIVERY_ERROR);
        }
    }
}