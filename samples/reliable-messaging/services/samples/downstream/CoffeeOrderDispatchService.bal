package samples.downstream;

import ballerina.net.http;

@http:configuration {basePath:"/dispatch",
                     port:9091}
service<http> coffeeOrderDispatchService {
    @http:resourceConfig {
        methods:["POST"],
        path:"/order"
    }
    resource dispatchResource (http:Request req, http:Response res) {
        json orderPayload = req.getJsonPayload();
        // Print the retrieved payload.
        println("Payload: " + orderPayload.toString());
        json responsePayload = {"Status":"Success"};
        res.setJsonPayload(responsePayload);
        res.send();
    }
}