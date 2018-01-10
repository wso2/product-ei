package samples.holiday;

import ballerina.net.http;

@http:configuration {basePath:"/hotel"}
service<http> HotelService {

    @http:resourceConfig {
        methods:["POST"],
        path:"/miramar"
    }
    resource miramar (http:Request request, http:Response response) {
        json receivedPayload = request.getJsonPayload();
        json from = receivedPayload.from;
        json to = receivedPayload.to;
        json hotelDetails = {
                                "Hotel":"miramar",
                                "From":from,
                                "To":to,
                                "DistanceToLocation":10
                            };
        response.setJsonPayload(hotelDetails);
        _ = response.send();
    }

    @http:resourceConfig {
        methods:["POST"],
        path:"/aqueen"
    }
    resource aqueen (http:Request request, http:Response response) {
        json receivedPayload = request.getJsonPayload();
        json from = receivedPayload.from;
        json to = receivedPayload.to;
        json hotelDetails = {
                                "Hotel":"aqueen",
                                "From":from,
                                "To":to,
                                "DistanceToLocation":8
                            };
        response.setJsonPayload(hotelDetails);
        _ = response.send();
    }

    @http:resourceConfig {
        methods:["POST"],
        path:"/elizabeth"
    }
    resource elizabeth (http:Request request, http:Response response) {
        json receivedPayload = request.getJsonPayload();
        json from = receivedPayload.from;
        json to = receivedPayload.to;
        json hotelDetails = {
                                "Hotel":"elizabeth",
                                "From":from,
                                "To":to,
                                "DistanceToLocation":5
                            };
        response.setJsonPayload(hotelDetails);
        _ = response.send();
    }
}

