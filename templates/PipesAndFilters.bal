import ballerina.net.http;

@http:configuration {basePath:"/"}@Description {value:"This service represents the Pipes and Filters EIP pattern."}
service<http> PipesAndFiltersService {

    @http:resourceConfig {
        methods:["POST"],
        path:"/"
    }
    resource pipesAndFilters (http:Request request, http:Response response) {
        endpoint<http:HttpClient> httpEndpoint {
            create http:HttpClient("http://localhost:9091", {});
        }

        http:Response backendResponse = {};
        json payload = request.getJsonPayload();
        string responsePayload;
        http:Request req = {};
        var name, _ = (string) payload.credentials.name;
        var id, _ = (string) payload.credentials.id;

        if (name.equalsIgnoreCase("UserName")) {
            if (id.equalsIgnoreCase("001")) {
                backendResponse, _ = httpEndpoint.get("/services/SimpleStockQuoteService", req);
                responsePayload = backendResponse.getStringPayload();
            }
        }
        response.setStringPayload(responsePayload);
        _ = response.send();
    }
}

