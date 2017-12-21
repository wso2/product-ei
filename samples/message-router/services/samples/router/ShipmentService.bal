package samples.router;

import ballerina.net.http;
import ballerina.log;

@http:configuration {basePath:"/shipment"}
service<http> ShipmentService {
    @http:resourceConfig {
        methods:["POST"],
        path:"/submit/{orderId}"
    }
    resource shipmentResource (http:Request req, http:Response resp, string orderId) {
        println("Submitting shipment details to order id: " + orderId);

        // dummy shipment service
        json payload = {"Status":"Shipment details submitted", "Order ID": orderId};
        resp.setJsonPayload(payload);

        http:HttpConnectorError respondError = resp.send();

        if (respondError != null) {
            log:printError("Error occured at ShipmentService when responding back");
        }
    }

    @http:resourceConfig {
        methods:["POST"],
        path:"/internal/{orderId}"
    }
    resource internalShipmentResource (http:Request req, http:Response resp, string orderId) {
        println("Submitting internal shipment details to order id: " + orderId);

        // dummy shipment service
        json payload = {"Status":"Shipment details submitted", "Order ID": orderId};
        resp.setJsonPayload(payload);
        http:HttpConnectorError respondError = resp.send();

        if (respondError != null) {
            log:printError("Error occured at ShipmentService when responding back");
        }
    }
}