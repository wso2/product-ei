package samples.router;

import ballerina.net.http;
import ballerina.log;

@http:configuration {basePath:"/payment"}
service<http> PaymentGatewayService {
    @http:resourceConfig {
        methods:["POST"],
        path:"/visa/{orderId}"
    }
    resource visaCardPayment (http:Request req, http:Response resp, string orderId) {
        println("Processing Payment");

        // dummy payment service
        json payload = req.getJsonPayload();
        var cardNo,_ = (string) payload.card.no;
        if (cardNo != "") {
            payload = {"Status":"Transaction made through your VISA card is successful for order : " + orderId};
        } else {
            payload = {"Status":"Failed"};
        }
        
        resp.setJsonPayload(payload);
        http:HttpConnectorError respondError = resp.send();

        if (respondError != null) {
            log:printError("Error occured at PaymentGatewayService visaCard while responding back");
        }
    }

    @http:resourceConfig {
        methods:["POST"],
        path:"/master/{orderId}"
    }
    resource masterCardPayment (http:Request req, http:Response resp, string orderId) {
        println("Processing Payment");

        // dummy payment service
        json payload = req.getJsonPayload();
        var cardNo,_ = (string) payload.card.no;
        if (cardNo != "") {
            payload = {"Status":"Transaction made through your Master card is successful for order : " + orderId};
        } else {
            payload = {"Status":"Failed"};
        }

        resp.setJsonPayload(payload);
        http:HttpConnectorError respondError = resp.send();

        if (respondError != null) {
            log:printError("Error occured at PaymentGatewayService masterCard while responding back");
        }
    }

}