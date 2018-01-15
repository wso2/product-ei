import ballerina.net.http;

@http:configuration {basePath:"/"} @Description {value:"This service represents the Message Filter EIP pattern which
checks an incoming message against a certain criteria that the message should adhere to. "}
service<http> MessageRouterService {

    @http:resourceConfig {
        methods:["POST"],
        path:"/"
    }
    resource routeMessage (http:Request request, http:Response response) {
        endpoint<http:HttpClient> FooHttpEndpoint {
            create http:HttpClient("http://localhost:9000", {});
        }
        json payload = request.getJsonPayload();
        string symbol = payload.quote.symbol.toString();
        http:Response resp = {};
        if (symbol.equalsIgnoreCase("foo")) {
            resp, _ = FooHttpEndpoint.post("/services/SimpleStockQuoteService", request);
            _ = response.forward(resp);
        }
    }
}