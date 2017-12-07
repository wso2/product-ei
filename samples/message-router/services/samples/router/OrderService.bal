package samples.router;

import ballerina.net.http;
import ballerina.math;

@http:configuration {basePath:"/order"}
service<http> OrderService {
    @http:resourceConfig {
        methods:["POST"],
        path:"/placeOrder"
    }
    resource placeOrder (http:Request req, http:Response resp) {
        println("Processing Order");

        // dummy order submitting service
        json payload = req.getJsonPayload();
        println(payload);
        int orderId = math:randomInRange(100,1000);
        payload = {"Status":"Order placed successfully", "OrderId" : + orderId  };

        resp.setJsonPayload(payload);
        resp.send();
    }

}