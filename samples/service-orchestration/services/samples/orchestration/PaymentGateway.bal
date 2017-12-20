package samples.orchestration;

import ballerina.net.http;
import ballerina.log;

@http:configuration {basePath:"/payment"}
service<http> PaymentGatewayService {
    @http:resourceConfig {
        methods:["POST"],
        path:"/pay"
    }
    resource makePayment (http:Request req, http:Response resp) {
        println("Processing Payment");

        // dummy payment service
        json payload = req.getJsonPayload();
        println(payload);
        var cardNo,_ = (int) payload.creditCardNo;
        if (cardNo != 0) { 
            payload = {"Status":"Successful"};
        } else {
            payload = {"Status":"Failed"};
        }
        
        resp.setJsonPayload(payload);

        http:HttpConnectorError respondError = null;
        respondError = resp.send();

        if (respondError != null) {
            log:printError("Error occured at PaymentGatewayService while responding back");
        }
    }
}