package samples.stockquote;

import ballerina.net.soap;
import ballerina.net.http;

@http:configuration {basePath:"/soapSample"}
service<http> sampleSoapService {

    @http:resourceConfig {
        methods:["POST"],
        path:"/"
    }
    resource stockSample (http:Request request, http:Response response) {
        endpoint<soap:SoapClient> soapClient {
            create soap:SoapClient("http://localhost:9090", {});
        }

        xml body = request.getXmlPayload();
        soap:SoapVersion version11 = soap:SoapVersion.SOAP11;

        soap:Request soapRequest = {
                                       soapAction:"urn:getQuote",
                                       soapVersion:version11,
                                       payload:body};

        soap:Response soapResponse;
        soap:SoapError soapError;
        soapResponse, soapError = soapClient.sendReceive("/SimpleStockQuoteService/simpleQuote", soapRequest);

        println(soapError);
        xml payload = soapResponse.payload;
        response.setXmlPayload(payload);
        _ = response.send();
    }
}
