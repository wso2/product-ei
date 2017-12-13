package samples.orchestration;

import ballerina.net.http;

@http:configuration {basePath:"/insurance"}
service<http> InsuranceService {
    @http:resourceConfig {
        methods:["GET"],
        path:"/validate/{vehicleId}"
    }
    resource validatePolicy (http:Request req, http:Response resp, string vehicleId) {
        println("Validating insurance policy");

        json payload;
        if (vehicleId == "KW-2577" || vehicleId == "CAV-5527" || vehicleId == "KP-6531" || vehicleId == "HI-4257") {
            // This is just a dummy validation
            payload = {"Status":"Valid"};
        } else {
            payload = {"Status":"Invalid"};
        }

        resp.setJsonPayload(payload);
        resp.send();
    }
}