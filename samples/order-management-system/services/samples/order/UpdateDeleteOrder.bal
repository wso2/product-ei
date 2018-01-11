package samples.order;

import ballerina.net.http;
import ballerina.net.soap;

@http:configuration {basePath:"/modifyOrder"}

service<http> ModifyOrder {

    @http:resourceConfig {
        methods:["DELETE"],
        path:"/orderId/{orderIdPathParam}"
    }
    resource deleteOrder (http:Request request, http:Response response, string orderIdPathParam) {

        xml deleteOrderPayload = xml `<m0:removeOrder><m0:orderId>{{orderIdPathParam}}</m0:orderId></m0:removeOrder>`;

        xml backendPayload = sendRequest(deleteOrderPayload);
        xml responsePayload = xml `<message xmlns="https://starbucks.example.org">Order deleted</message>`;

        response.setXmlPayload(responsePayload);
        _ = response.send();
    }

    @http:resourceConfig {
        methods:["OPTIONS"],
        path:"/orderId/{orderIdPathParam}"
    }
    resource checkAllowUpdates (http:Request request, http:Response response, string orderIdPathParam) {

        xml orderInfoPayload = xml `<m0:getOrder><m0:orderId>{{orderIdPathParam}}</m0:orderId></m0:getOrder>`;

        xml backendPayload = sendRequest(orderInfoPayload);
        string locked = backendPayload.selectChildren("{http://ws.starbucks.com}return")
                              .selectChildren(ax2438:locked).getTextValue();

        if (locked == "false") {
            response.setHeader("Allow", "GET,PUT");
        }
        else {
            response.setHeader("Allow", "GET");
        }
        _ = response.send();
    }

    @http:resourceConfig {
        methods:["PUT"],
        path:"/orderId/{orderIdPathParam}"
    }
    resource updateOrder (http:Request request, http:Response response, string orderIdPathParam) {

        xml requestBody = request.getXmlPayload();
        string drink = requestBody.selectChildren("drink").getTextValue();
        string additions = requestBody.selectChildren("additions").getTextValue();

        xml updateOrderPayload = xml `<m0:updateOrder>
                                        <m0:orderId>{{orderIdPathParam}}</m0:orderId>
                                        <m0:drinkName>{{drink}}</m0:drinkName>
                                        <m0:additions>{{additions}}</m0:additions>
                                    </m0:updateOrder>`;

        xml backendPayload = sendRequest(updateOrderPayload);
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
        _ = response.send();
    }
}

function sendRequest (xml orderPayload) (xml) {

    endpoint<soap:SoapClient> ep {
        create soap:SoapClient("http://localhost:9763", {});
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
                              .sendReceive("/services/StarbucksOutletService/", soapRequest);

    xml backendPayload = soapResponse.payload;
    return backendPayload;
}

