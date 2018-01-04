package samples.order;

import ballerina.net.soap;
import ballerina.net.http;

@http:configuration {basePath:"/getOrderInfo", port: 9091}
service<http> sampleSoapServiceForGettingOrderInfo {

    @http:resourceConfig {
        methods:["GET"],
        path:"/order/{orderIdPathParam}"
    }
    resource GetOrderInfo (http:Request request, http:Response response, string orderIdPathParam) {
        endpoint<soap:SoapClient> soapClient {
            create soap:SoapClient();
        }
	   
        xmlns "http://ws.starbucks.com" as m0;
        xmlns "http://ws.starbucks.com/xsd" as ax2438;

        xml orderPayload = xml `<m0:getOrder><m0:orderId>{{orderIdPathParam}}</m0:orderId></m0:getOrder>`;

        soap:SoapVersion version11 = soap:SoapVersion.SOAP11;
        soap:Request soapRequest = {
                                       soapAction:"urn:getQuote",
                                       soapVersion:version11,
                                       payload:orderPayload
                                   };

        soap:Response soapResponse;
        soap:SoapError soapError;
        soapResponse, soapError = soapClient
                                  .sendReceive(soapRequest, "http://localhost:9763/services/StarbucksOutletService/");

        xml backendPayload = soapResponse.payload;
        string responseCost = backendPayload.selectChildren("{http://ws.starbucks.com}return")
                              .selectChildren(ax2438:cost).getTextValue();
        string responseDrinkName = backendPayload.selectChildren("{http://ws.starbucks.com}return")
                                   .selectChildren(ax2438:drinkName).getTextValue();
        string responseAdditions = backendPayload.selectChildren("{http://ws.starbucks.com}return")
                                   .selectChildren(ax2438:additions).getTextValue();
        string orderId = backendPayload.selectChildren("{http://ws.starbucks.com}return")
                         .selectChildren(ax2438:orderId).getTextValue();

        xml responsePayload = xml `<order xmlns="http://starbucks.example.org">
                                    <drink>{{responseDrinkName}}</drink>
                                    <cost>{{responseCost}}</cost>
                                    <additions>{{responseAdditions}}</additions>
                                   </order>`;

        response.setXmlPayload(responsePayload);
        _=response.send();
    }
}

