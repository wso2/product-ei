import ballerina.net.http;

@http:configuration {basePath:"/"} @Description {value:"This service represents the Message Router EIP pattern which
reads the content of a message and routes it to a specific recipient based on its content."}
service<http> MessageRouterService {

    @http:resourceConfig {
        methods:["POST"],
        path:"/"
    }
    resource routeMessage (http:Request request, http:Response response) {
        endpoint<http:HttpClient> FooHttpEndpoint {
            create http:HttpClient("http://localhost:9000", {});
        }
        endpoint<http:HttpClient> BarHttpEndpoint {
            create http:HttpClient("http://localhost:9001", {});
        }
        endpoint<http:HttpClient> DefaultHttpEndpoint {
            create http:HttpClient("http://localhost:9002", {});
        }
        json payload = request.getJsonPayload();
        string symbol = payload.quote.symbol.toString();
        http:Response resp = {};
        if (symbol.equalsIgnoreCase("foo")) {
            resp, _ = FooHttpEndpoint.post("/services/SimpleStockQuoteService", request);
        } else if (symbol.equalsIgnoreCase("bar")){
            resp, _ = BarHttpEndpoint.post("/services/SimpleStockQuoteService", request);
        } else {
            resp, _ = DefaultHttpEndpoint.post("/services/SimpleStockQuoteService", request);
        }
        _ = response.forward(resp);
    }
}