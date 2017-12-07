package samples.topic.common;

import ballerina.net.http;
import ballerina.net.jms;

@Description {value:"Place a travel request"}
@http:configuration {basePath:"/travel"}
service<http> travelRequestGateway {

    endpoint<jms:JmsClient> travelRequestJMSTopicEp {
        create jms:JmsClient(getConnectorConfig());
    }

    @http:resourceConfig {
        methods:["POST"],
        path:"/itinerary"
    }
    resource travelRequestResource (http:Request req, http:Response res) {
        json travelRequestPayload = req.getJsonPayload();
        // Create a travel order message
        jms:JMSMessage travelRequestMessage = jms:createTextMessage(getConnectorConfig());
        //Set the order content to the JMS message
        //Note : in this sample we do not validate the incoming message since that is not the scope of this sample.
        travelRequestMessage.setTextMessageContent(travelRequestPayload.toString());
        //Send the order message to a JMS topic.
        travelRequestJMSTopicEp.send("TravelRequestTopic", travelRequestMessage);
        //Set the response payload and send it to the caller. By this time the message is added to the topic and
        //would provide a guarantee to the caller on successful delivery of the message.
        json responsePayload = {"Status":"travel request processed"};
        res.setJsonPayload(responsePayload);
        res.send();
    }
}

@Description {value:"Retreive the broker configuration"}
function getConnectorConfig () (jms:ClientProperties) {
    jms:ClientProperties properties = {initialContextFactory:"wso2mbInitialContextFactory",
                                       providerUrl:"amqp://admin:admin@carbon/carbon?brokerlist='tcp://localhost:5672'",
                                       connectionFactoryName:"TopicConnectionFactory",
                                       connectionFactoryType:"topic"};
    return properties;
}
