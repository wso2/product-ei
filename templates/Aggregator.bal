import ballerina.net.http;

@http:configuration {basePath:"/"} @Description {value:"This service represents the Aggregator EIP pattern which
is responsible for collecting and storing individual messages until a complete set of related messages has been received. "}
service<http> AggregateService {

    @http:resourceConfig {
        methods:["GET"],
        path:"/"
    }
    resource aggregate (http:Request request, http:Response response) {
        endpoint<http:HttpClient> httpEndpointA {
            create http:HttpClient("http://localhost:9091", {});
        }
        endpoint<http:HttpClient> httpEndpointB {
            create http:HttpClient("http://localhost:9092", {});
        }
        endpoint<http:HttpClient> httpEndpointC {
            create http:HttpClient("http://localhost:9093", {});
        }

        json jsonResponseA;
        json jsonResponseB;
        json jsonResponseC;
        json fullResponse;

        fork {

            worker ReceiverA {
                http:Request req = {};
                http:Response responseA = {};
                responseA, _ = httpEndpointA.get("/services/SimpleStockQuoteService", req);
                responseA -> fork;
            }

            worker ReceiverB {
                http:Request req = {};
                http:Response responseB = {};
                responseB, _ = httpEndpointB.get("/services/SimpleStockQuoteService", req);
                responseB -> fork;
            }

            worker ReceiverC {
                http:Request req = {};
                http:Response responseC = {};
                responseC, _ = httpEndpointC.get("/services/SimpleStockQuoteService", req);
                responseC -> fork;
            }
        }
            // Wait until all the responses are received from the parallely running workers.
        join (all) (map responses) {

            if (responses["ReceiverA"] != null) {
                var workerResponseA, _ = (any[])responses["ReceiverA"];
                var responseFromReceiverA, _ = (http:Response)(workerResponseA[0]);
                jsonResponseA = responseFromReceiverA.getJsonPayload();
            }

            if (responses["ReceiverB"] != null) {
                var workerResponseB, _ = (any[])responses["ReceiverB"];
                var responseFromReceiverB, _ = (http:Response)(workerResponseB[0]);
                jsonResponseB = responseFromReceiverB.getJsonPayload();
            }

            if (responses["ReceiverC"] != null) {
                var workerResponseC, _ = (any[])responses["ReceiverC"];
                var responseFromReceiverC, _ = (http:Response)(workerResponseC[0]);
                jsonResponseC = responseFromReceiverC.getJsonPayload();
            }

            fullResponse = {"A":jsonResponseA, "B":jsonResponseB, "C":jsonResponseC};

            response.setJsonPayload(fullResponse);
            _ = response.send();
        }
    }
}

