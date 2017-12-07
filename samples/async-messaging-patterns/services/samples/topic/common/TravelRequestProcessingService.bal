package samples.topic.common;

import ballerina.net.http;

@Description {value:"Mimics order dispatching service"}
@http:configuration {basePath:"/dispatch",
                     port:9091}
service<http> travelRequestProcessingService {
    @http:resourceConfig {
        methods:["POST"],
        path:"/travelOrder"
    }
    resource dispatchResource (http:Request req, http:Response res) {
        //Retrieve incoming json payload
        json orderPayload = req.getJsonPayload();
        // Print the retrieved payload.
        println("Tavel order received : " + orderPayload.toString());
        //Send a successful response back to the caller
        json responsePayload = {"Status":"Success"};
        res.setJsonPayload(responsePayload);
        res.send();
    }
}
