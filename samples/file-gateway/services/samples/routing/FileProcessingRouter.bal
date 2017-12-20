package samples.routing;

import ballerina.net.fs;
import ballerina.file;
import ballerina.io;
import ballerina.net.http;

@fs:configuration {
    dirURI:"../samples/file-gateway/resources/connections/",
    events:"create,delete,modify",
    recursive:false
}
service<fs> FileProcessingRouter {

    endpoint<http:HttpClient> NewConnectionProcessingServiceEp {
        create http:HttpClient("http://localhost:9090/connection", {});
    }

    resource fileResource (fs:FileSystemEvent fsEvent) {
        string filename = fsEvent.name;

        println(filename);
        io:CharacterChannel characterChannel = getFileCharacterChannel(filename, "r", "UTF-8");
        //The first 8 characters will hold the status
        //We do not need to read the entire file if we're to reject
        string status = characterChannel.readCharacters(8);
        //We'll ignore the new line
        _ = characterChannel.readCharacters(1);

        if (status == "Approved"){
            //Read data from file block by block, 10 characters at a given time
            int readChunkSize = 10;
            string content = characterChannel.readCharacters(readChunkSize);
            string itrReadContent = content;
            while(itrReadContent.length() > 0){
                itrReadContent = characterChannel.readCharacters(readChunkSize);
                content = content + itrReadContent;
            }
            json payload = content;
            //Create a http message based on the content specified in JMS message
            http:Request newConnectionRequest = {};
            newConnectionRequest.setJsonPayload(payload);
            //Create a place holder to retrieve the response obtained from travel order service
            http:Response newConnectionResponse = {};
            //Dispatch the order message to the respective endpoint.
            newConnectionResponse, _ = NewConnectionProcessingServiceEp.post("/register", newConnectionRequest);

        }else {
          println(filename+" will not be processed any further, since this is rejected.");
        }
    }
}

@Description{value:"This function will return a CharacterChannel from a given file location according to the specified
permissions and encoding."}
function getFileCharacterChannel (string filePath, string permission, string encoding)
(io:CharacterChannel) {
    file:File src = {path:filePath};
    //First we get the ByteChannel representation of the file.
    io:ByteChannel channel = src.openChannel(permission);
    //Then we convert the byte channel to character channel to read content as text.
    io:CharacterChannel characterChannel = channel.toCharacterChannel(encoding);
    return characterChannel;
}
