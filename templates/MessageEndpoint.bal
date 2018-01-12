import ballerina.net.http;

@http:configuration {basePath:"/"}@Description {value:"This service represents the Message Endpoint EIP pattern which
is responsible encapsulating the messaging system inside an application. "}
service<http> EndPoint {

    @http:resourceConfig {
        methods:["GET"],
        path:"/"
    }
    resource sendMessage (http:Request request, http:Response response) {
        endpoint<http:HttpClient> httpEndpoint {
            create http:HttpClient("http://localhost:9091", {});
        }

        http:Response backendResponse = {};
        http:Request req = {};
        string responsePayload;
        backendResponse, _ = httpEndpoint.get("/services/SimpleStockQuoteService", req);

        responsePayload = backendResponse.getStringPayload();
        response.setStringPayload(responsePayload);
        _ = response.send();
    }
}
