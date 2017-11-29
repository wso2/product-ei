package samples.downstream;

import ballerina.net.http;

@Description{value:"Mimics order dispatching service"}
@http:configuration {basePath:"/dispatch",
                     port:9091}
service<http> coffeeOrderDispatchService {
    @http:resourceConfig {
        methods:["POST"],
        path:"/coffeeOrder"
    }
    resource dispatchResource (http:Request req, http:Response res) {
        //Retrieve incoming json payload
        json orderPayload = req.getJsonPayload();
        // Print the retrieved payload.
        println("Payload: " + orderPayload.toString());
        //Send a successful response back to the caller
        json responsePayload = {"Status":"Success"};
        res.setJsonPayload(responsePayload);
        res.send();
    }
}
