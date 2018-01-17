package samples.downstream;

import ballerina.net.jms;

@jms:configuration {
    initialContextFactory:"wso2mbInitialContextFactory",
    providerUrl:"amqp://admin:admin@carbon/carbon?brokerlist='tcp://localhost:5672'",
    connectionFactoryName: "QueueConnectionFactory",
    destination:"RequestQueue"
}
service<jms> jmsService  {

    endpoint<jms:JmsClient> jmsEP {
        create jms:JmsClient(getConnectorConfig());
    }

    resource jmsResource (jms:JMSMessage request) {

        print("Coffee service backend processing the request: ");
        println(request.getTextMessageContent());

        int orderResponseTimeToLive = 2000;

        // process the request and create the response
        jms:JMSMessage response = jms:createTextMessage(getConnectorConfig());
        json coffeeResponse = {OrderID:request.getCorrelationID(), Price:"$10.50", OrderReady:true};
        response.setTextMessageContent(coffeeResponse.toString());
        response.setExpiration(orderResponseTimeToLive);
        response.setCorrelationID(request.getCorrelationID());

        // push response to the ReplyTo queue
        jmsEP.send(request.getReplyTo(), response);
    }
}

function getConnectorConfig () (jms:ClientProperties) {
    jms:ClientProperties properties = {   initialContextFactory:"wso2mbInitialContextFactory",
                                          providerUrl:"amqp://admin:admin@carbon/carbon?brokerlist='tcp://localhost:5672'",
                                          connectionFactoryName: "QueueConnectionFactory"};
    return properties;
}
