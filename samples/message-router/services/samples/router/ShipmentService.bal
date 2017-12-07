package samples.router;

import ballerina.net.http;

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
        resp.send();
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
        resp.send();
    }
}