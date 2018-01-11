package samples.stockquote;

import ballerina.net.http;

@http:configuration {basePath:"/SimpleStockQuoteService"}
service<http> SimpleStockQuoteService {

    @http:resourceConfig {
        methods:["POST"],
        path:"/simpleQuote"
    }
    resource simpleQuote (http:Request request, http:Response response) {
        //this is a mock response with soap11 format
        xml soapEnv = request.getXmlPayload();
        xml symbol = soapEnv.selectDescendants("{http://services.samples}symbol");
        println("Generating quote for : " + symbol.getTextValue());
        xml soapResponse = xml `<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
                                   <soapenv:Body>
                                      <ns:getSimpleQuoteResponse xmlns:ns="http://services.samples">
                                         <ns:return xmlns:ax21="http://services.samples/xsd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="ax21:GetQuoteResponse">
                                            <ax21:change>4.3311386045056315</ax21:change>
                                            <ax21:earnings>13.013976582793564</ax21:earnings>
                                            <ax21:high>177.7476403504822</ax21:high>
                                            <ax21:last>170.14182767345125</ax21:last>
                                            <ax21:lastTradeTimestamp>{{currentTime().toString()}}</ax21:lastTradeTimestamp>
                                            <ax21:low>176.09241233172185</ax21:low>
                                            <ax21:marketCap>-241432.41409987584</ax21:marketCap>
                                            <ax21:name>{{symbol.getTextValue()}} Company</ax21:name>
                                            <ax21:open>176.08496815825492</ax21:open>
                                            <ax21:peRatio>23.13885889982435</ax21:peRatio>
                                            <ax21:percentageChange>2.2150838646120135</ax21:percentageChange>
                                            <ax21:prevClose>195.52932842406213</ax21:prevClose>
                                            <ax21:symbol>{{symbol.getTextValue()}}</ax21:symbol>
                                            <ax21:volume>16820</ax21:volume>
                                         </ns:return>
                                      </ns:getSimpleQuoteResponse>
                                   </soapenv:Body>
                                </soapenv:Envelope>`;
        response.setXmlPayload(soapResponse);
        response.setHeader("Content-Type", "text/xml");
        _ = response.send();
    }
}
