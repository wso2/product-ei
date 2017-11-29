package samples.order;

import ballerina.net.fs;
import ballerina.file;
import ballerina.io;
import ballerina.net.http;

@fs:configuration {
    dirURI:"../samples/file-connector/resources/in/",
    events:"create,delete,modify",
    recursive:false
}
service<fs> FileProcessor {

    endpoint<http:HttpClient> orderProcessorServiceEP {
        create http:HttpClient("http://localhost:9090/orders", {});
    }

    resource fileResource (fs:FileSystemEvent fsEvent) {
        string filename = fsEvent.name;

        println(filename);
        io:TextRecordChannel srcRecordChannel = getFileRecordChannel(filename, "r", "UTF-8", "\\r?\\n", ",");

        // ignore first row of csv as it contains the header
        string[] records = srcRecordChannel.readTextRecord();

        while (true) {
            records = srcRecordChannel.readTextRecord();
            if (lengthof records == 0) {
                break;
            }

            json payload = {
                               "OrderId":records[0],
                               "Date":records[1],
                               "CustomerName":records[2],
                               "PaymentStatus":records[3],
                               "ItemId":records[4]
                           };

            http:Request orderProcessReq = {};
            orderProcessReq.setJsonPayload(payload);

            http:Response orderProcessResponse = {};
            orderProcessResponse, _ = orderProcessorServiceEP.post("/", orderProcessReq);
            var orderProcessResponseJsonPayload = orderProcessResponse.getJsonPayload();
            println(orderProcessResponseJsonPayload);

        }
        srcRecordChannel.closeTextRecordChannel();
    }
}

function getFileRecordChannel (string filePath, string permission, string encoding,
                               string rs, string fs) (io:TextRecordChannel) {
    file:File src = {path:filePath};
    io:ByteChannel channel = src.openChannel(permission);
    io:CharacterChannel characterChannel = channel.toCharacterChannel(encoding);
    io:TextRecordChannel textRecordChannel = characterChannel.toTextRecordChannel(rs, fs);
    return textRecordChannel;
}


