package samples.orchestration;

import ballerina.net.http;
import ballerina.log;

@http:configuration {basePath:"/emission"}
service<http> EmissionService {
    @http:resourceConfig {
        methods:["GET"],
        path:"/validate/{vehicleId}"
    }
    resource validateCert (http:Request req, http:Response resp, string vehicleId) {
        println("Validating emission test certificate");

        json payload;
        if (vehicleId == "11111") { 
            // This is just a dummy validation
            payload = {"Status":"Valid"};
        } else {
            payload = {"Status":"Invalid"};
        }

        resp.setJsonPayload(payload);

        http:HttpConnectorError respondError = null;
        respondError = resp.send();

        if (respondError != null) {
            log:printError("Error occured at EmissionService while responding back");
        }
    }
}