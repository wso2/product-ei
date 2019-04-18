import ballerina/io;


public function getTextResponse(string request) returns string {
    return "incoming message is: " + request;
}

public function textError() returns string {
    return "Error while retrieving request";
}
