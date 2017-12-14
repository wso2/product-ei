package samples.billing;

import ballerina.net.http;

@Description {value:"Mimics bill analytics service"}
@http:configuration {basePath:"/dispatch",
                     port:9092}
service<http> travelAnalyticsService {
    @http:resourceConfig {
        methods:["POST"],
        path:"/analytics"
    }
    resource dispatchResource (http:Request req, http:Response res) {
        //Retrieve incoming json payload
        json orderPayload = req.getJsonPayload();
        // Print the retrieved payload.
        println("Analytics service received the request: " + orderPayload.toString());
        //Send a successful response back to the caller
        json responsePayload = {"Status":"Success"};
        res.setJsonPayload(responsePayload);
        res.send();
    }
}
