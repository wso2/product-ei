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
        xmlns "http://services.samples" as m0;
        xmlns "http://schemas.xmlsoap.org/soap/envelope/" as soapenv;
        xml payload = request.getXmlPayload();
        string responsePayload;
        http:Request req = {};

        if ((payload.selectChildren(soapenv:Body).selectChildren(m0:credentials).selectChildren(m0:name)
             .getTextValue()) == "UserName") {
            if ((payload.selectChildren(soapenv:Body).selectChildren(m0:credentials).selectChildren(m0:id)
                 .getTextValue()) == "001") {
                backendResponse, _ = httpEndpoint.get("/services/SimpleStockQuoteService", req);
                responsePayload = backendResponse.getStringPayload();
            }
        }
        response.setStringPayload(responsePayload);
        _ = response.send();
    }
}
