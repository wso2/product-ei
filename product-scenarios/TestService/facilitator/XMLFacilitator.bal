import ballerina/io;
import ballerina/log;
import ballerina/streams;


public function validateXML(xml requestPayload, string messageId) returns boolean{
    string presetFilePath = "./Resource/xml/request/"+messageId+".xml";
    //internal:Path path = new ("./facilitator/Resource/json/request/"+messageId+".json");
    //string absPath = path.toAbsolutePath().getPathValue();
    xml definedXml = getXML(presetFilePath);
    boolean result = compareXML(definedXml, requestPayload);

    return result;
}

function  getXML(string path) returns xml {
    io:ByteChannel byteChannel = io:openFile(path, io:READ);
    io:CharacterChannel characterChannel = new io:CharacterChannel(byteChannel, "UTF8");
    json file;

    match characterChannel.readXml(){
        xml message => {
            close(characterChannel);
            return message;
        }
        error xmlError => {
            close(characterChannel);
            throw xmlError;
        }
    }
}

function compareXML(xml original, xml request) returns boolean {

    boolean result;
    string originalMessage = <string>original;
    string requestMessage = <string>request;
    originalMessage = originalMessage.trim().unescape().replaceAll("\n","");
    requestMessage = requestMessage.trim().unescape().replaceAll("\n","");

    if (originalMessage == requestMessage) {
        result = true;
    }

    return result;
}

public function getResponseXML(string messageId) returns xml{
    string responseFilePath = "./Resource/xml/response/"+messageId+".xml";
    xml responseXML = getXML(responseFilePath);
    
    return responseXML;
}

public function getXMLError() returns xml{
    return xml `<status>invalid</status>`;
}

public function getNoMessageIdXMLError() returns xml{
    return xml `<status>no message header defined</status>`;
}

