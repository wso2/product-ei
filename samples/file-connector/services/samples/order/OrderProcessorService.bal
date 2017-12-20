package samples.order;

import ballerina.net.http;

@http:configuration {basePath:"/orders"}
service<http> OrderProcessor {

    @http:resourceConfig {
        methods:["POST"],
        path:"/"
    }
    resource processOrder (http:Request request, http:Response response) {
        json payload = request.getJsonPayload();
        var orderId,_ = (string) payload.OrderId;
        println("Processing order : " + orderId);
        var paymentStatus,_ = (string) payload.PaymentStatus;
        string status = "Failed";
        if (paymentStatus.equalsIgnoreCase("Paid")) {
            status = "Success";
        }
        json responsePayload = {"OrderId": orderId, "Status": status};

        response.setJsonPayload(responsePayload);
         _ = response.send();
    }
}
