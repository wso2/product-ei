package samples.order;

import ballerina.net.soap;
import ballerina.net.http;

@http:configuration {basePath:"/getPaymentDetails", port: 9093}
service<http> sampleSoapServiceForGettingPaymentInfo {

    @http:resourceConfig {
        methods:["GET"],
        path:"/payment/order/{orderIdPathParam}"
    }
    resource GetPaymentDetails (http:Request request, http:Response response, string orderIdPathParam) {
        endpoint<soap:SoapClient> soapClient {
            create soap:SoapClient();
        }
	   
        xmlns "http://ws.starbucks.com" as m0;
        xmlns "http://ws.starbucks.com/xsd" as ax2438;

        xml orderPayload = xml `<m0:getPayment>
                                    <m0:orderId>{{orderIdPathParam}}</m0:orderId>
                                </m0:getPayment>`;

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

        string responseCardNo = backendPayload.selectChildren("{http://ws.starbucks.com}return")
                           .selectChildren(ax2438:cardNumber).getTextValue();
        string responseExpires = backendPayload.selectChildren("{http://ws.starbucks.com}return")
                            .selectChildren(ax2438:expiryDate).getTextValue();
        string responseName = backendPayload.selectChildren("{http://ws.starbucks.com}return")
                         .selectChildren(ax2438:name).getTextValue();
        string responseAmount = backendPayload.selectChildren("{http://ws.starbucks.com}return")
                           .selectChildren(ax2438:amount).getTextValue();
        string orderId = backendPayload.selectChildren("{http://ws.starbucks.com}return")
                         .selectChildren(ax2438:orderId).getTextValue();

        xml responsePayload = xml `<payment xmlns="https://starbucks.example.org/">
                                        <cardNo>{{responseCardNo}}</cardNo>
                                        <expires>{{responseExpires}}</expires>
                                        <name>{{responseName}}</name>
                                        <amount>{{responseAmount}}</amount>
                                        <orderId>{{orderId}}</orderId>
                                   </payment>`;

        response.setXmlPayload(responsePayload);
        _=response.send();
    }
}

