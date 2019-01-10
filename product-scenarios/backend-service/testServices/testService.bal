import ballerina/io;
import ballerina/http;
import ballerinax/docker;
import ballerina/runtime;
import facilitator as facilitate;
import ballerina/cache;
import ballerina/config;

endpoint http:Listener listener {
    port: config:getAsInt("port", default = 9090)
};

// RESTful service.
@http:ServiceConfig { basePath: "/eiTests",
    cors: {
        allowOrigins: ["*"],
        allowCredentials: false,
        allowHeaders: ["*"],
        exposeHeaders: ["messageId"],
        maxAge: 84900
    } }

service<http:Service> eiTests bind listener {


    @http:ResourceConfig {
        methods: ["POST"],
        path: "/JSONEndpoint"
    }
    verifyJSON(endpoint client, http:Request req) {
        // Implementation of test to verify JSON
        string messageId = req.getHeader("messageId");
        map queryParams = req.getQueryParams();
        json|error requestPayload = req.getJsonPayload();
        json responsePayload;
        http:Response response;

        if (messageId != "") {
            match requestPayload {
                json jsonpayload => {
                    boolean jsonValid = facilitate:validateJSON(jsonpayload, untaint messageId);
                    if (jsonValid) {
                        if (queryParams.hasKey("timeoutMillis")) {
                            string timeoutParam = <string>queryParams["timeoutMillis"];
                            int timeoutMillis = check <int>timeoutParam;
                            responsePayload = facilitate:createJSONResponseWithTImeout(untaint timeoutMillis, untaint
                                messageId);
                        } else {
                            responsePayload = facilitate:getResponseJSON(untaint messageId);
                        }
                    } else {
                        responsePayload = facilitate:getJSONContentValidationError();
                    }
                }
                error nonJsonPayload => {
                    responsePayload = facilitate:getJSONBadStringError();
                }
            }
        } else {
            responsePayload = facilitate:getNoMessageIdJSONError();
        }
        response.setJsonPayload(responsePayload);
        response.setHeader("messageId", messageId);
        _ = client->respond(response);
    }

    @http:ResourceConfig {
        methods: ["POST"],
        path: "/XMLEndpoint"
    }
    verifyXML(endpoint client, http:Request req) {
        // Implementation of test to verify XML
        string messageId = req.getHeader("messageId");
        map queryParams = req.getQueryParams();
        xml|error requestPayload = req.getXmlPayload();
        xml responsePayload;
        http:Response response;

        if (messageId != "") {
            match requestPayload {
                xml xmlpayload => {
                    boolean xmlValid = facilitate:validateXML(xmlpayload, untaint messageId);
                    if (xmlValid) {
                        if (queryParams.hasKey("timeoutMillis")) {
                            string timeoutParam = <string>queryParams["timeoutMillis"];
                            int timeoutMillis = check <int>timeoutParam;
                            responsePayload = facilitate:createXMLResponseWithTImeout(untaint timeoutMillis, untaint
                                messageId);
                        } else {
                            responsePayload = facilitate:getResponseXML(untaint messageId);
                        }
                    } else {
                        responsePayload = facilitate:getXMLContentValidationError();
                    }
                }
                error nonXMLPayload => {
                    responsePayload = facilitate:getXMLBadStringError();
                }
            }
        } else {
            responsePayload = facilitate:getNoMessageIdXMLError();
        }
        response.setXmlPayload(responsePayload);
        response.setHeader("messageId", messageId);
        _ = client->respond(response);
    }


    @http:ResourceConfig {
        methods: ["POST"],
        path: "/statusCode/{statusCodeParam}"
    }
    statusCode(endpoint client, http:Request req, string statusCodeParam) {
        // Implementation of test to verify statusCode
        http:Response response;
        json responsePayload;
        int statusCode;
        if (statusCodeParam != "") {
            statusCode = check <int>statusCodeParam;
            boolean hasHeader = req.hasHeader("messageId");
            if (hasHeader) {
                string messageId = req.getHeader("messageId");
                response.setHeader("messageId", messageId);
                json|error requestPayload = req.getJsonPayload();

                match requestPayload {
                    json jsonpayload => {
                        responsePayload = facilitate:getResponseJSON(untaint messageId);
                        io:println(messageId);
                    }
                    error nonJsonPayload => {
                        responsePayload = facilitate:getResponseJSON(untaint messageId);
                    }
                }
            } else {
                responsePayload = facilitate:getResponseJSON("sampleJsonResponse");
            }
        }
        response.setJsonPayload(untaint responsePayload);
        response.statusCode = untaint statusCode;
        _ = client->respond(response);
    }

    @http:ResourceConfig {
        methods: ["POST"],
        path: "/CSVEndpoint"
    }
    verifyCSV(endpoint client, http:Request req) {
        // Implementation of test to verify statusCode
        string messageId = req.getHeader("messageId");
        map queryParams = req.getQueryParams();
        string|error requestPayload = req.getPayloadAsString();
        json responsePayload;
        http:Response response;

        if (messageId != "") {
            match requestPayload
                {
                    string csvpayload => {
                        boolean csvValid = facilitate:validateCSV(csvpayload, untaint messageId);
                        if (csvValid) {
                            if (queryParams.hasKey("timeoutMillis")) {
                                string timeoutParam = <string>queryParams["timeoutMillis"];
                                int timeoutMillis = check <int>timeoutParam;
                                responsePayload = facilitate:createCSVResponseWithTimeout(untaint timeoutMillis, untaint
                                    messageId);
                            } else {
                                responsePayload = facilitate:getCSVResponse(untaint messageId);
                            }
                        } else {
                            responsePayload = facilitate:getCSVContentValidationError();
                        }
                    }
                    error nonJsonPayload => {
                        responsePayload = facilitate:getCSVBadStringError();
                    }
                }
        } else {
            responsePayload = facilitate:getNoMessageIdJSONError();
        }
        response.setPayload(responsePayload);
        response.setHeader("messageId", messageId);
        _ = client->respond(response);
    }


    @http:ResourceConfig {
        methods: ["POST"],
        path: "/textEndpoint"
    }
    textService(endpoint client, http:Request req) {

        http:Response response;
        string responsePayload;
        string|error requestPayload = req.getPayloadAsString();
        boolean hasHeader = req.hasHeader("messageId");
        if (hasHeader) {
            response.setHeader("messageId", req.getHeader("messageId"));
        }
        match requestPayload {
            string textMessage => {
                responsePayload = facilitate:getTextResponse(untaint textMessage);
            }
            error err => {
                responsePayload = facilitate:textError();
            }
        }
        response.setTextPayload(untaint responsePayload);
        _ = client->respond(response);
    }

    @http:ResourceConfig {
        methods: ["POST"],
        path: "/echo"
    }
    echoService(endpoint client, http:Request req) {
        http:Response response;
        string contentType = req.getContentType();

        if (contentType == "application/json") {
            json|error payload = untaint req.getJsonPayload();
            match payload {
                json jsonPayload => {
                    response.setJsonPayload(jsonPayload);
                }
                error jsonError => {
                    response.setPayload(facilitate:getJSONBadStringError());
                }
            }
        } else if (contentType == "application/xml"){
            xml|error payload = untaint req.getXmlPayload();
            match payload {
                xml xmlPayload => {
                    response.setXmlPayload(xmlPayload);
                }
                error err => {
                    response.setPayload(facilitate:getXMLBadStringError());
                }
            }
        } else if (contentType == "text/plain") {
            string|error payload = untaint req.getTextPayload();
            match payload {
                string stringPayload => {
                    response.setTextPayload(stringPayload);
                }
                error err => {
                    response.setTextPayload(facilitate:textError());
                }
            }
        } else {
            response.setPayload("please check the payload content type");
        }
        _ = client->respond(response);
    }
}
