import ballerina/io;
import ballerina/math;
import ballerina/log;
import ballerina/internal;


function close(io:CharacterChannel characterChannel) {
    characterChannel.close() but {
        error e =>
        log:printError("Error occurred while closing character stream", err = e)
    };
}

public function validateJSON(json requestPayload, string messageId) returns boolean {
    string presetFilePath = "./resources/json/request/" + messageId + ".json";
    json definedJson = getJSON(presetFilePath);
    boolean result = compareJSON(definedJson, requestPayload);

    return result;
}


function compareJSON(json original, json request) returns boolean {

    boolean result;
    if (original.toString() == request.toString()) {
        return true;
    }
    int originalSize = lengthof original;
    if (originalSize == (lengthof request)) {
        foreach key, value in check <map>original  {
            string element = request[key].toString();
            if (element != null) {
                if (element == <string>value) {
                    result = true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    } else {
        return false;
    }
    return result;
}

function getJSON(string path) returns json {
    io:ByteChannel byteChannel = io:openFile(path, io:READ);
    io:CharacterChannel characterChannel = new io:CharacterChannel(byteChannel, "UTF8");
    json file;
    match characterChannel.readJson() {
        json message => {
            close(characterChannel);
            return message;
        } error jsonError => {
            close(characterChannel);
            throw jsonError;
        }
    }
}

public function getResponseJSON(string messageId) returns json {
    string responseFilePath = "./resources/json/response/" + messageId + ".json";
    json responseJson = getJSON(responseFilePath);
    return responseJson;
}

public function getJSONContentValidationError() returns json{
    return {status: "content mismatch in json"};
}

public function getJSONBadStringError() returns json{
    return {status: "bad json string"};
}

public function getNoMessageIdJSONError() returns json {
    return {status: "no message id defined"};
}
