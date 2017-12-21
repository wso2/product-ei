package samples.billing;

import ballerina.net.fs;
import ballerina.file;
import ballerina.io;
import ballerina.net.http;

@fs:configuration {
    dirURI:"../samples/file-gateway/resources/bills/",
    events:"create,delete,modify",
    recursive:false
}
service<fs> FileProcessingGateway {

    endpoint<jms:JmsClient> BillsJmsEp {
        create jms:JmsClient(getConnectorConfig());
    }

    resource fileResource (fs:FileSystemEvent fsEvent) {
        string filename = fsEvent.name;

        println(filename);
        io:TextRecordChannel srcRecordChannel = getFileRecordChannel(filename, "r", "UTF-8", "\\r?\\n", ",");

        string[] records = srcRecordChannel.readTextRecord();

        while (true) {
            records = srcRecordChannel.readTextRecord();
            if (lengthof records < 3) {
                break;
            }
            json payload = {
                               "name":records[0],
                               "id":records[1],
                               "amount":records[2]
                           };
            // Create a billing order message
            jms:JMSMessage userBillingInformation = jms:createTextMessage(getConnectorConfig());
            //Set the order content to the JMS message
            //Note : in this sample we do not validate the incoming message since that is not the scope of this sample.
            userBillingInformation.setTextMessageContent(payload.toString());
            //Send the billing message to a JMS topic.
            BillsJmsEp.send("BillingTopic", userBillingInformation);
        }

        srcRecordChannel.closeTextRecordChannel();
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

@Description {value:"Retreive the record channel of the file"}
function getFileRecordChannel (string filePath, string permission, string encoding,
                               string rs, string fs) (io:TextRecordChannel) {
    file:File src = {path:filePath};
    io:ByteChannel channel = src.openChannel(permission);
    io:CharacterChannel characterChannel = channel.toCharacterChannel(encoding);
    io:TextRecordChannel textRecordChannel = characterChannel.toTextRecordChannel(rs, fs);
    return textRecordChannel;
}
