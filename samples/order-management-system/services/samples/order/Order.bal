package samples.order;

import ballerina.net.http;
import ballerina.net.soap;

xmlns "http://ws.starbucks.com" as m0;
xmlns "http://ws.starbucks.com/xsd" as ax2438;

@http:configuration {basePath:"/order", port:9095}

service<http> Order {

    @http:resourceConfig {
        methods:["GET"],
        path:"/orderId/{orderIdPathParam}"
    }
    resource getOrderInfo (http:Request request, http:Response response, string orderIdPathParam) {

        xml orderPayload = xml `<m0:getOrder><m0:orderId>{{orderIdPathParam}}</m0:orderId></m0:getOrder>`;
        xml responsePayload = sendSOAPRequest(orderPayload);

        response.setXmlPayload(responsePayload);
        _ = response.send();
    }

    @http:resourceConfig {
        methods:["POST"],
        path:"/placeOrder"
    }
    resource placeOrder (http:Request request, http:Response response) {

        xml requestBody = request.getXmlPayload();
        string drink = requestBody.selectChildren("drink").getTextValue();
        string additions = requestBody.selectChildren("additions").getTextValue();

        xml orderPayload = xml `<m0:addOrder>
                                    <m0:drinkName>{{drink}}</m0:drinkName>
                                    <m0:additions>{{additions}}</m0:additions>
                                </m0:addOrder>`;

        xml responsePayload = sendSOAPRequest(orderPayload);

        response.setXmlPayload(responsePayload);
        response.setStatusCode(201);
        _ = response.send();
    }
}

function sendSOAPRequest (xml orderPayload) (xml) {

    endpoint<soap:SoapClient> ep {
        create soap:SoapClient();
    }

    soap:SoapVersion version11 = soap:SoapVersion.SOAP11;
    soap:Request soapRequest = {
                                   soapAction:"urn:getQuote",
                                   soapVersion:version11,
                                   payload:orderPayload
                               };

    soap:Response soapResponse;
    soap:SoapError soapError;
    soapResponse, soapError = ep
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
                                    <orderId>{{orderId}}</orderId>
                                    <drink>{{responseDrinkName}}</drink>
                                    <cost>{{responseCost}}</cost>
                                    <additions>{{responseAdditions}}</additions>
                                   </order>`;
    return responsePayload;
}

