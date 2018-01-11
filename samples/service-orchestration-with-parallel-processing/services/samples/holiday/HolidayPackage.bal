package samples.holiday;

import ballerina.net.http;

@http:configuration {basePath:"/web"}
service<http> HolidayPackage {

    @http:resourceConfig {
        methods:["GET"],
        path:"/holiday"
    }
    resource holidayPackage (http:Request request, http:Response response) {

        endpoint<http:HttpClient> httpEndpoint {
            create http:HttpClient("http://localhost:9090/", {});
        }

        map params = request.getQueryParams();
        var departureDate, _ = (string)params.depart;
        var returnDate, _ = (string)params.returnDate;
        var from, _ = (string)params.from;
        var to, _ = (string)params.to;
        var vehicleType, _ = (string)params.vehicleType;
        var location, _ = (string)params.location;

        string flightPayload = string `{"departure":"{{departureDate}}", "returnDate":"{{returnDate}}",
                                "from":"{{from}}", "to":"{{to}}"}`;
        string vehiclePayload = string `{"from":"{{departureDate}}", "to":"{{returnDate}}", "vehicleType":"{{vehicleType}}"}`;
        string hotelPayload = string `{"from":"{{departureDate}}", "to":"{{returnDate}}", "location":"{{location}}"}`;

        json jsonFlightResponse;
        json jsonVehicleResponse;
        json jsonHotelResponse;
        json miramarJsonResponse;
        json aqueenJsonResponse;
        json elizabethJsonResponse;
        json jsonFlightResponseEmirates;
        json jsonFlightResponseAsiana;
        json jsonFlightResponseQatar;

        fork {

            worker qatarWorker {
                var payload, _ = <json>flightPayload;
                http:Request req = {};
                http:Response respWorkerQater = {};
                req.setJsonPayload(payload);
                respWorkerQater, _ = httpEndpoint.post("/airline/qatarAirways", req);
                respWorkerQater -> fork;
            }

            worker asianaWorker {
                var payload, _ = <json>flightPayload;
                http:Request req = {};
                http:Response respWorkerAsiana = {};
                req.setJsonPayload(payload);
                respWorkerAsiana, _ = httpEndpoint.post("/airline/asiana", req);
                respWorkerAsiana -> fork;
            }

            worker emiratesWorker {
                var payload, _ = <json>flightPayload;
                http:Request req = {};
                http:Response respWorkerEmirates = {};
                req.setJsonPayload(payload);
                respWorkerEmirates, _ = httpEndpoint.post("/airline/emirates", req);
                respWorkerEmirates -> fork;
            }
        }
        // Wait until all the responses are received from the parallely running workers.
        join (all) (map airlineResponses) {
            var qatarPrice = 0;
            var asianaPrice = 0;
            var emiratesPrice = 0;

            if (airlineResponses["qatarWorker"] != null) {
                var resQatarWorker, _ = (any[])airlineResponses["qatarWorker"];
                var responseQatar, _ = (http:Response)(resQatarWorker[0]);
                jsonFlightResponseQatar = responseQatar.getJsonPayload();
                qatarPrice, _ = (int)responseQatar.getJsonPayload().price;
            }

            if (airlineResponses["asianaWorker"] != null) {
                var resAsianaWorker, _ = (any[])airlineResponses["asianaWorker"];
                var responseAsiana, _ = (http:Response)(resAsianaWorker[0]);
                jsonFlightResponseAsiana = responseAsiana.getJsonPayload();
                asianaPrice, _ = (int)responseAsiana.getJsonPayload().price;
            }

            if (airlineResponses["emiratesWorker"] != null) {
                var resEmiratesWorker, _ = (any[])airlineResponses["emiratesWorker"];
                var responseEmirates, _ = ((http:Response)(resEmiratesWorker[0]));
                jsonFlightResponseEmirates = responseEmirates.getJsonPayload();
                emiratesPrice, _ = (int)responseEmirates.getJsonPayload().price;

            }

            if (qatarPrice < asianaPrice) {
                if (qatarPrice < emiratesPrice) {
                    jsonFlightResponse = jsonFlightResponseQatar;
                }
            } else {
                if (qatarPrice < emiratesPrice) {
                    jsonFlightResponse = jsonFlightResponseAsiana;
                }
                else {
                    jsonFlightResponse = jsonFlightResponseEmirates;
                }
            }
        }

        fork {

            worker driveSg {
                var payload, _ = <json>vehiclePayload;
                http:Request req = {};
                http:Response respWorkerDriveSg = {};
                req.setJsonPayload(payload);
                respWorkerDriveSg, _ = httpEndpoint.post("/carRental/driveSg", req);
                respWorkerDriveSg -> fork;
            }

            worker dreamCar {
                var payload, _ = <json>vehiclePayload;
                http:Request req = {};
                http:Response respWorkerDreamCar = {};
                req.setJsonPayload(payload);
                respWorkerDreamCar, _ = httpEndpoint.post("/carRental/dreamCar", req);
                respWorkerDreamCar -> fork;
            }

            worker sixt {
                var payload, _ = <json>vehiclePayload;
                http:Request req = {};
                http:Response respWorkerSixt = {};
                req.setJsonPayload(payload);
                respWorkerSixt, _ = httpEndpoint.post("/carRental/sixt", req);
                respWorkerSixt -> fork;
            }
        }
        // Get the first responding worker.
        join (some 1) (map carResponses) {

            if (carResponses["driveSg"] != null) {
                var resDriveSgWorker, _ = (any[])carResponses["driveSg"];
                var responseDriveSg, _ = (http:Response)(resDriveSgWorker[0]);
                jsonVehicleResponse = responseDriveSg.getJsonPayload();
            }

            else if (carResponses["dreamCar"] != null) {
                var resDreamCarWorker, _ = (any[])carResponses["dreamCar"];
                var responseDreamCar, _ = (http:Response)(resDreamCarWorker[0]);
                jsonVehicleResponse = responseDreamCar.getJsonPayload();
            }

            else if (carResponses["sixt"] != null) {
                var resSixtWorker, _ = (any[])carResponses["sixt"];
                var responseSixt, _ = ((http:Response)(resSixtWorker[0]));
                jsonVehicleResponse = responseSixt.getJsonPayload();
            }
        }

        fork {

            worker miramar {
                var payload, _ = <json>hotelPayload;
                http:Request req = {};
                http:Response respWorkerMiramar = {};
                req.setJsonPayload(payload);
                respWorkerMiramar, _ = httpEndpoint.post("/hotel/miramar", req);
                respWorkerMiramar -> fork;
            }

            worker aqueen {
                var payload, _ = <json>hotelPayload;
                http:Request req = {};
                http:Response respWorkerAqueen = {};
                req.setJsonPayload(payload);
                respWorkerAqueen, _ = httpEndpoint.post("/hotel/aqueen", req);
                respWorkerAqueen -> fork;
            }

            worker elizabeth {
                var payload, _ = <json>hotelPayload;
                http:Request req = {};
                http:Response respWorkerElizabeth = {};
                req.setJsonPayload(payload);
                respWorkerElizabeth, _ = httpEndpoint.post("/hotel/elizabeth", req);
                respWorkerElizabeth -> fork;
            }
        }
        // Wait until all the responses are received from the parallely running workers.
        join (all) (map hotelResponses) {

            var miramarDistance = 0;
            var aqueenDistance = 0;
            var elizabethDistance = 0;

            if (hotelResponses["miramar"] != null) {
                var resMiramarWorker, _ = (any[])hotelResponses["miramar"];
                var responseMiramar, _ = (http:Response)(resMiramarWorker[0]);
                miramarJsonResponse = responseMiramar.getJsonPayload();
                miramarDistance, _ = (int)responseMiramar.getJsonPayload().DistanceToLocation;
            }

           if (hotelResponses["aqueen"] != null) {
                var resAqueenWorker, _ = (any[])hotelResponses["aqueen"];
                var responseAqueen, _ = (http:Response)(resAqueenWorker[0]);
                aqueenJsonResponse = responseAqueen.getJsonPayload();
                aqueenDistance, _ = (int)responseAqueen.getJsonPayload().DistanceToLocation;
            }

           if (hotelResponses["elizabeth"] != null) {
                var resElizabethWorker, _ = (any[])hotelResponses["elizabeth"];
                var responseElizabeth, _ = ((http:Response)(resElizabethWorker[0]));
                elizabethJsonResponse = responseElizabeth.getJsonPayload();
                elizabethDistance, _ = (int)responseElizabeth.getJsonPayload().DistanceToLocation;
            }

           if (miramarDistance < aqueenDistance) {
                if (miramarDistance < elizabethDistance) {
                    jsonHotelResponse = miramarJsonResponse;
                }
            } else {
                if (aqueenDistance < elizabethDistance) {
                    jsonHotelResponse = aqueenJsonResponse;
                }
                else {
                    jsonHotelResponse = elizabethJsonResponse;
                }
            }
        }

        json clientResponse = {
                                  "Flight":
                                  {
                                      "Company":jsonFlightResponse.Flight,
                                      "Departure Date":jsonFlightResponse.DepartureDate,
                                      "From":jsonFlightResponse.From,
                                      "To":jsonFlightResponse.To,
                                      "Price":jsonFlightResponse.price
                                  },
                                  "Vehicle":
                                  {
                                      "Company":jsonVehicleResponse.Company,
                                      "VehicleType":jsonVehicleResponse.VehicleType,
                                      "Price per Day($)":jsonVehicleResponse.price
                                  },
                                  "Hotel":
                                  {
                                      "Hotel":jsonHotelResponse.Hotel,
                                      "From Date":jsonHotelResponse.From,
                                      "To Date":jsonHotelResponse.To,
                                      "Distance to Location(Miles)":jsonHotelResponse.DistanceToLocation
                                  }
                              };

        response.setJsonPayload(clientResponse);
        _ = response.send();
    }
}

