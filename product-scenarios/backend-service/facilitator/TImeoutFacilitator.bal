import ballerina/io;
import ballerina/runtime;

public function createJSONResponseWithTImeout(int timeoutMillis, string messageId) returns json {
    runtime:sleep(timeoutMillis);
    json delayedResponse = getResponseJSON(messageId);
    return delayedResponse;
}

public function createXMLResponseWithTImeout(int timeoutMillis, string messageId) returns xml {
    runtime:sleep(timeoutMillis);
    xml delayedResponse = getResponseXML(messageId);
    return delayedResponse;
}

public function createCSVResponseWithTimeout(int timeoutMillis, string messageId) returns string {
    runtime:sleep(timeoutMillis);
    string delayedResponse = getCSVResponse(messageId);
    return delayedResponse;
}
