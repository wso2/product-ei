package samples.billing;

import ballerina.net.http;

@Description {value:"Mimics bill audit service"}
@http:configuration {basePath:"/dispatch",
                     port:9091}
service<http> billAuditingService {
    @http:resourceConfig {
        methods:["POST"],
        path:"/audit"
    }
    resource dispatchResource (http:Request req, http:Response res) {
        //Retrieve incoming json payload
        json orderPayload = req.getJsonPayload();
        // Print the retrieved payload.
        println("Audit service received the request: " + orderPayload.toString());
        //Send a successful response back to the caller
        json responsePayload = {"Status":"Success"};
        res.setJsonPayload(responsePayload);
        res.send();
    }
}
