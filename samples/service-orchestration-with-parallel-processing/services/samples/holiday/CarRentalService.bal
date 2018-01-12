package samples.holiday;

import ballerina.net.http;

@http:configuration {basePath:"/carRental"}
service<http> CarRentalService {

    @http:resourceConfig {
        methods:["POST"],
        path:"/driveSg"
    }
    resource driveSg (http:Request request, http:Response response) {
        json receivedPayload = request.getJsonPayload();
        json from = receivedPayload.from;
        json to = receivedPayload.to;
        json vehicleType = receivedPayload.vehicleType;
        json vehicleDetails = {
                                  "Company":"DriveSG",
                                  "VehicleType":vehicleType,
                                  "From":from,
                                  "To":to,
                                  "price":50
                              };
        response.setJsonPayload(vehicleDetails);
        _ = response.send();
    }

    @http:resourceConfig {
        methods:["POST"],
        path:"/dreamCar"
    }
    resource dreamCar (http:Request request, http:Response response) {
        json receivedPayload = request.getJsonPayload();
        json from = receivedPayload.from;
        json to = receivedPayload.to;
        json vehicleType = receivedPayload.vehicleType;
        json vehicleDetails = {
                                  "Company":"DreamCar",
                                  "VehicleType":vehicleType,
                                  "From":from,
                                  "To":to,
                                  "price":60
                              };
        response.setJsonPayload(vehicleDetails);
        _ = response.send();
    }

    @http:resourceConfig {
        methods:["POST"],
        path:"/sixt"
    }
    resource sixT (http:Request request, http:Response response) {
        json receivedPayload = request.getJsonPayload();
        json from = receivedPayload.from;
        json to = receivedPayload.to;
        json vehicleType = receivedPayload.vehicleType;
        json vehicleDetails = {
                                  "Company":"SixT",
                                  "VehicleType":vehicleType,
                                  "From":from,
                                  "To":to,
                                  "price":65
                              };
        response.setJsonPayload(vehicleDetails);
        _ = response.send();
    }
}

