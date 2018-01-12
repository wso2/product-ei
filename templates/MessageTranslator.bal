import ballerina.net.http;

@http:configuration {basePath:"/"} @Description {value:"This service represents the Message Translator EIP pattern which
is responsible for translating messages between applications. "}
service<http> MessageTranslatorService {

    @http:resourceConfig {
        methods:["POST"],
        path:"/"
    }
    resource routeMessage (http:Request request, http:Response response) {
        endpoint<http:HttpClient> httpEndpoint {
            create http:HttpClient("http://localhost:9000", {});
        }
        xmlns "http://services.samples" as m0;
        xmlns "http://schemas.xmlsoap.org/soap/envelope/" as soapenv;
        xml payload = request.getXmlPayload();
        xml symbolElement = payload.selectChildren(soapenv:Body).selectChildren(m0:Code);
        string symbol = symbolElement.getTextValue();
        println(symbol);
        xml reqPayload = xml `<soapenv:Envelope>
                                   <soapenv:Header/>
                                   <soapenv:Body>
                                      <m0:getQuote>
                                         <m0:request>
                                            <m0:symbol>{{symbol}}</m0:symbol>
                                         </m0:request>
                                      </m0:getQuote>
                                   </soapenv:Body>
                                </soapenv:Envelope>`;
        request.setXmlPayload(reqPayload);
        request.setHeader("Content-Type", "text/xml");
        request.setHeader("SOAPAction", "urn:getQuote");
        println(reqPayload);
        http:Response resp = {};
        println(request);
        resp, _ = httpEndpoint.post("/services/SimpleStockQuoteService", request);
        _ = response.forward(resp);
    }
}