package samples.orchestration;

import ballerina.net.http;

@http:configuration {basePath:"/licenseIssuer"}
service<http> LicenseIssuerService {
    @http:resourceConfig {
        methods:["GET"],
        path:"/{vehicleId}"
    }
    resource licenseIssuerResource (http:Request req, http:Response resp, string vehicleId) {
        println("Generating License");

        json payload = {"License Certificate":"XLO1029302020", "Vehicle ID": vehicleId};
        resp.setJsonPayload(payload);
        resp.send();
    }
}