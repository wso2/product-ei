package samples.order;

import ballerina.net.soap;
import ballerina.net.http;

@http:configuration {basePath:"/placeOrder"}
service<http> sampleSoapServiceForPlacingOrder {
    @http:resourceConfig {
        methods:["POST"],
        path:"/order"
    }

    resource PlaceOrder (http:Request request, http:Response response) {
        endpoint<soap:SoapClient> soapClient {
            create soap:SoapClient();
        }

        xmlns "http://ws.starbucks.com" as m0;
        xmlns "http://ws.starbucks.com/xsd" as ax2438;

        xml requestBody = request.getXmlPayload();
        string drink = requestBody.selectChildren("drink").getTextValue();
        string additions = requestBody.selectChildren("additions").getTextValue();

        xml orderPayload = xml `<m0:addOrder>
                                    <m0:drinkName>{{drink}}</m0:drinkName>
                                    <m0:additions>{{additions}}</m0:additions>
                                </m0:addOrder>`;

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

        //Payload coming with the response from backend after creating the order.
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
                                        <orderId>{{orderId}}</orderId>
                                        <drink>{{responseDrinkName}}</drink>
                                        <cost>{{responseCost}}</cost>
                                        <additions>{{responseAdditions}}</additions>
                                   </order>`;

        response.setXmlPayload(responsePayload);
        response.setStatusCode(201);
        _ = response.send();
    }
}

