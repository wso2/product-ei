import ballerina.net.http;

@http:configuration {basePath:"/"} @Description {value:"This service represents the Aggregator EIP pattern which
is responsible for collecting and storing individual messages until a complete set of related messages has been received. "}
service<http> Aggregate {

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

        xmlns "http://services.samples" as m0;
        xmlns "http://schemas.xmlsoap.org/soap/envelope/" as soapenv;
        xml xmlResponseA;
        xml xmlResponseB;
        xml xmlResponseC;
        xml fullResponse;

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
                xmlResponseA = responseFromReceiverA.getXmlPayload().selectChildren(soapenv:Body).selectChildren(m0:getQuoteResponse);
                fullResponse = responseFromReceiverA.getXmlPayload().selectChildren(soapenv:Body);
            }

            if (responses["ReceiverB"] != null) {
                var workerResponseB, _ = (any[])responses["ReceiverB"];
                var responseFromReceiverB, _ = (http:Response)(workerResponseB[0]);
                xmlResponseB = responseFromReceiverB.getXmlPayload().selectChildren(soapenv:Body).selectChildren(m0:getQuoteResponse);
            }

            if (responses["ReceiverC"] != null) {
                var workerResponseC, _ = (any[])responses["ReceiverC"];
                var responseFromReceiverC, _ = (http:Response)(workerResponseC[0]);
                xmlResponseC = responseFromReceiverC.getXmlPayload().selectChildren(soapenv:Body).selectChildren(m0:getQuoteResponse);
            }

            fullResponse.setChildren(xmlResponseB + xmlResponseA + xmlResponseC);

            response.setXmlPayload(fullResponse);
            _ = response.send();
        }
    }
}
