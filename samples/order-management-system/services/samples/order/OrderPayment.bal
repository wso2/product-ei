package samples.order;

import ballerina.net.http;
import ballerina.net.soap;

@http:configuration {basePath:"/payment"}

service<http> Payment {
    @http:resourceConfig {
        methods:["PUT"],
        path:"/payOrder/{orderIdPathParam}"
    }
    resource payOrder (http:Request request, http:Response response, string orderIdPathParam) {

        xml body = request.getXmlPayload();

        string cardNo = body.selectChildren("cardNo").getTextValue();
        string expires = body.selectChildren("expires").getTextValue();
        string amount = body.selectChildren("amount").getTextValue();
        string name = body.selectChildren("name").getTextValue();
        boolean paidOrder = false;

        xml orderPayload = xml `<m0:doPayment>
                                    <m0:orderId>{{orderIdPathParam}}</m0:orderId>
                                    <m0:name>{{name}}</m0:name>
                                    <m0:cardNumber>{{cardNo}}</m0:cardNumber>
                                    <m0:expiryDate>{{expires}}</m0:expiryDate>
                                    <m0:amount>{{amount}}</m0:amount>
                                </m0:doPayment>`;

        sendPaymentSOAPRequest(orderPayload, paidOrder, response);
    }

    @http:resourceConfig {
        methods:["GET"],
        path:"/getPaymentInfo/{orderIdPathParam}"
    }
    resource orderPaymentInfo (http:Request request, http:Response response, string orderIdPathParam) {

        xml orderPayload = xml `<m0:getPayment>
                                    <m0:orderId>{{orderIdPathParam}}</m0:orderId>
                                </m0:getPayment>`;
        boolean paidOrder = true;
        sendPaymentSOAPRequest(orderPayload, paidOrder, response);
    }                                                                          }

function sendPaymentSOAPRequest (xml orderPayload, boolean paidOrder, http:Response response) {

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

    string acceptedPayment = "Payment Accepted";
    string responseCardNo;
    string responseExpires;
    string responseName;
    string responseAmount;
    string orderId;
    xml responsePayload;
    xml backendPayload = soapResponse.payload;
    string paymentStatus = backendPayload.selectChildren("{http://ws.starbucks.com}return")
                           .selectChildren(ax2438:status).getTextValue();

    if (paymentStatus == acceptedPayment) {

        responseCardNo = backendPayload.selectChildren("{http://ws.starbucks.com}return")
                         .selectChildren(ax2438:payment).selectChildren(ax2438:cardNumber).getTextValue();
        responseExpires = backendPayload.selectChildren("{http://ws.starbucks.com}return")
                          .selectChildren(ax2438:payment).selectChildren(ax2438:expiryDate).getTextValue();
        responseName = backendPayload.selectChildren("{http://ws.starbucks.com}return")
                       .selectChildren(ax2438:payment).selectChildren(ax2438:name).getTextValue();
        responseAmount = backendPayload.selectChildren("{http://ws.starbucks.com}return")
                         .selectChildren(ax2438:payment).selectChildren(ax2438:amount).getTextValue();
        orderId = backendPayload.selectChildren("{http://ws.starbucks.com}return")
                  .selectChildren(ax2438:payment).selectChildren(ax2438:orderId).getTextValue();

        responsePayload = xml `<payment xmlns="https://starbucks.example.org/">
                                        <cardNo>{{responseCardNo}}</cardNo>
                                        <expires>{{responseExpires}}</expires>
                                        <name>{{responseName}}</name>
                                        <amount>{{responseAmount}}</amount>
                                        <orderId>{{orderId}}</orderId>
                                       </payment>`;
        response.setStatusCode(201);

    } else if (paidOrder) {
        responseCardNo = backendPayload.selectChildren("{http://ws.starbucks.com}return")
                         .selectChildren(ax2438:cardNumber).getTextValue();
        responseExpires = backendPayload.selectChildren("{http://ws.starbucks.com}return")
                          .selectChildren(ax2438:expiryDate).getTextValue();
        responseName = backendPayload.selectChildren("{http://ws.starbucks.com}return")
                       .selectChildren(ax2438:name).getTextValue();
        responseAmount = backendPayload.selectChildren("{http://ws.starbucks.com}return")
                         .selectChildren(ax2438:amount).getTextValue();
        orderId = backendPayload.selectChildren("{http://ws.starbucks.com}return")
                  .selectChildren(ax2438:orderId).getTextValue();

        responsePayload = xml `<payment xmlns="https://starbucks.example.org/">
                                        <cardNo>{{responseCardNo}}</cardNo>
                                        <expires>{{responseExpires}}</expires>
                                        <name>{{responseName}}</name>
                                        <amount>{{responseAmount}}</amount>
                                        <orderId>{{orderId}}</orderId>
                                       </payment>`;
    } else {
        responsePayload = backendPayload;
    }

    response.setXmlPayload(responsePayload);
    _ = response.send();
}

