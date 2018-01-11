package samples.order;

import ballerina.net.soap;
import ballerina.net.http;

@http:configuration {basePath:"/payOrder", port:9092}
service<http> sampleSoapServiceForPayment {
    @http:resourceConfig {
        methods:["PUT"],
        path:"/payment/{orderIdPathParam}"
    }

    resource PayOrder (http:Request request, http:Response response, string orderIdPathParam) {
        endpoint<soap:SoapClient> soapClient {
            create soap:SoapClient("http://localhost:9763", {});
        }

        xmlns "http://ws.starbucks.com" as m0;
        xmlns "http://ws.starbucks.com/xsd" as ax2438;

        xml body = request.getXmlPayload();

        string cardNo = body.selectChildren("cardNo").getTextValue();
        string expires = body.selectChildren("expires").getTextValue();
        string amount = body.selectChildren("amount").getTextValue();
        string name = body.selectChildren("name").getTextValue();

        xml orderPayload = xml `<m0:doPayment>
                                    <m0:orderId>{{orderIdPathParam}}</m0:orderId>
                                    <m0:name>{{name}}</m0:name>
                                    <m0:cardNumber>{{cardNo}}</m0:cardNumber>
                                    <m0:expiryDate>{{expires}}</m0:expiryDate>
                                    <m0:amount>{{amount}}</m0:amount>
                                </m0:doPayment>`;

        soap:SoapVersion version11 = soap:SoapVersion.SOAP11;
        soap:Request soapRequest = {
                                       soapAction:"urn:getQuote",
                                       soapVersion:version11,
                                       payload:orderPayload
                                   };

        soap:Response soapResponse;
        soap:SoapError soapError;
        soapResponse, soapError = soapClient
                                  .sendReceive("/services/StarbucksOutletService/", soapRequest);

        xml backendPayload = soapResponse.payload;

        string paymentStatus = backendPayload.selectChildren("{http://ws.starbucks.com}return")
                               .selectChildren(ax2438:status).getTextValue();

        if (paymentStatus == "Payment Accepted") {

            string responseCardNo = backendPayload.selectChildren("{http://ws.starbucks.com}return")
                                    .selectChildren(ax2438:cardNumber).getTextValue();
            string responseExpires = backendPayload.selectChildren("{http://ws.starbucks.com}return")
                                     .selectChildren(ax2438:payment).selectChildren(ax2438:expiryDate).getTextValue();
            string responseName = backendPayload.selectChildren("{http://ws.starbucks.com}return")
                                  .selectChildren(ax2438:payment).selectChildren(ax2438:name).getTextValue();
            string responseAmount = backendPayload.selectChildren("{http://ws.starbucks.com}return")
                                    .selectChildren(ax2438:payment).selectChildren(ax2438:amount).getTextValue();
            string orderId = backendPayload.selectChildren("{http://ws.starbucks.com}return")
                             .selectChildren(ax2438:payment).selectChildren(ax2438:orderId).getTextValue();

            xml responsePayload = xml `<payment xmlns="https://starbucks.example.org/">
                                        <cardNo>{{responseCardNo}}</cardNo>
                                        <expires>{{responseExpires}}</expires>
                                        <name>{{responseName}}</name>
                                        <amount>{{responseAmount}}</amount>
                                        <orderId>{{orderId}}</orderId>
                                       </payment>`;

            response.setXmlPayload(responsePayload);
            response.setStatusCode(201);
        } else {
            response.setXmlPayload(backendPayload);
        }
        _ = response.send();
    }
}

