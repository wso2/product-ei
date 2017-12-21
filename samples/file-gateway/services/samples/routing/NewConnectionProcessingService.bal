package samples.routing;

import ballerina.net.http;

@Description {value:"Mimics new connection prcoessing service"}
@http:configuration {basePath:"/connection",
                     port:9090}
service<http> billAuditingService {
    @http:resourceConfig {
        methods:["POST"],
        path:"/register"
    }
    resource dispatchResource (http:Request req, http:Response res) {
        //Retrieve incoming json payload
        json orderPayload = req.getJsonPayload();
        // Print the retrieved payload.
        println("New connection request received " + orderPayload.toString());
        //Send a successful response back to the caller
        json responsePayload = {"Status":"Success"};
        res.setJsonPayload(responsePayload);
        _ = res.send();
    }
}
