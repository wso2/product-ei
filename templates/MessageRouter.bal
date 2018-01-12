import ballerina.net.http;

@http:configuration {basePath:"/"} @Description {value:"This service represents the Message Router EIP pattern which
reads the content of a message and routes it to a specific recipient based on its content. "}
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
        xmlns "http://services.samples" as m0;
        xmlns "http://schemas.xmlsoap.org/soap/envelope/" as soapenv;
        xml payload = request.getXmlPayload();
        xml symbolElement = payload.selectChildren(soapenv:Body).selectChildren(m0:getQuote).selectChildren(m0:request)
                            .selectChildren(m0:symbol);
        string symbol = symbolElement.getTextValue();
        http:Response resp = {};
        if (symbol == "foo") {
            resp, _ = FooHttpEndpoint.post("/services/SimpleStockQuoteService", request);
        } else if (symbol == "bar") {
            resp, _ = BarHttpEndpoint.post("/services/SimpleStockQuoteService", request);
        } else {
            resp, _ = DefaultHttpEndpoint.post("/services/SimpleStockQuoteService", request);
        }
        _ = response.forward(resp);
    }
}