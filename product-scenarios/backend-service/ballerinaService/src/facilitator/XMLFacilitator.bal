import ballerina/io;
import ballerina/log;
import ballerina/streams;


public function validateXML(xml requestPayload, string messageId) returns boolean{
    string presetFilePath = "./resources/xml/request/"+messageId+".xml";
    xml definedXml = getXML(presetFilePath);
    boolean result = compareXML(definedXml, requestPayload);
    return result;
}

function getXML(string path) returns xml {
    io:ByteChannel byteChannel = io:openFile(path, io:READ);
    io:CharacterChannel characterChannel = new io:CharacterChannel(byteChannel, "UTF8");
    json file;

    match characterChannel.readXml() {
        xml message => {
            close(characterChannel);
            return message;
        } error xmlError => {
        close(characterChannel);
        throw xmlError;
    }
    }
}

function compareXML(xml original, xml request) returns boolean {
    boolean result;
    string originalMessage = io:sprintf("%s", original);
    string requestMessage = io:sprintf("%s", request);
    originalMessage = originalMessage.trim().unescape().replaceAll("\n", "").replaceAll(" ", "");
    requestMessage = requestMessage.trim().unescape().replaceAll("\n", "").replaceAll(" ", "");
    if (originalMessage == requestMessage) {
        result = true;
    }
    return result;
}

public function getResponseXML(string messageId) returns xml {
    string responseFilePath = "./resources/xml/response/" + messageId + ".xml";
    xml responseXML = getXML(responseFilePath);
    return responseXML;
}

public function getXMLBadStringError() returns xml{
    return xml `<status>bad xml string</status>`;
}

public function getXMLContentValidationError() returns xml{
    return xml `<status>content mismatch in xml</status>`;
}

public function getNoMessageIdXMLError() returns xml{
    return xml `<status>no message header defined</status>`;
}
