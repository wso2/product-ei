# 1.6.11 Modify the response with a script

## When to use
The WSO2 Script Mediator is used to invoke the functions of a variety of scripting languages such as JavaScript, Groovy, or Ruby.

## Sample use case
In this usecase we are going to modify the response coming from backend using a script mediator using inline javascript. 

Original Response
```
<soapenv:Envelope xmlns:soapenv="http://www.w3.org/2003/05/soap-envelope">
   <soapenv:Body>
      <ns:getQuoteResponse xmlns:ns="http://services.samples">
         <ns:return xsi:type="ax21:GetQuoteResponse" xmlns:ax21="http://services.samples/xsd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
            <ax21:change>4.36949216699184</ax21:change>
            <ax21:earnings>-8.598683611011193</ax21:earnings>
            <ax21:high>-77.50711615860914</ax21:high>
            <ax21:last>78.31032589384014</ax21:last>
            <ax21:lastTradeTimestamp>Mon Dec 03 16:01:41 IST 2018</ax21:lastTradeTimestamp>
            <ax21:low>80.38124880059465</ax21:low>
            <ax21:marketCap>965681.4269076362</ax21:marketCap>
            <ax21:name>IBM Company</ax21:name>
            <ax21:open>-77.94243329444193</ax21:open>
            <ax21:peRatio>-17.61515654348164</ax21:peRatio>
            <ax21:percentageChange>-5.905550865850721</ax21:percentageChange>
            <ax21:prevClose>-73.98957804696506</ax21:prevClose>
            <ax21:symbol>IBM</ax21:symbol>
            <ax21:volume>19594</ax21:volume>
         </ns:return>
      </ns:getQuoteResponse>
   </soapenv:Body>
</soapenv:Envelope>
```

Requested Response
```
<soapenv:Envelope xmlns:soapenv="http://www.w3.org/2003/05/soap-envelope">
   <soapenv:Body>
      <ns:getQuoteResponse xmlns:ns="http://services.samples">
         <ns:return xsi:type="ax21:GetQuoteResponse" xmlns:ax21="http://services.samples/xsd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
            <ax21:name>IBM Company</ax21:name>
            <ax21:Open>69.40005762027275</ax21:Open>
         </ns:return>
      </ns:getQuoteResponse>
   </soapenv:Body>
</soapenv:Envelope>
```
This can be achieved using script mediator with a inline javascript. 

## Prerequisites
N/A 

## Development guidelines

Sequence and proxy Configuration

Create the following sequence and add to the proxy.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<proxy xmlns="http://ws.apache.org/ns/synapse" name="Script1" startOnLoad="true" statistics="disable" trace="disable" transports="http,https">
   <target>
      <inSequence>
         <log level="full" />
      </inSequence>
      <outSequence>
         <log level="full" />
         <script language="js">
            var symbol = mc.getPayloadXML()..*::name.toString();
    var price = mc.getPayloadXML()..*::last.toString();
    mc.setPayloadXML(
            <ns:getQuoteResponse xmlns:ns="http://services.samples">
               <ns:return xmlns:ax21="http://services.samples/xsd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="ax21:GetQuoteResponse">
                  <ax21:name>{symbol}</ax21:name>
                  <ax21:Open>{price}</ax21:Open>
               </ns:return>
            </ns:getQuoteResponse>
            );
         </script>
         <log level="full" />
         <respond />
      </outSequence>
      <endpoint>
         <address uri="http://localhost:9000/services/SimpleStockQuoteService" />
      </endpoint>
   </target>
   <description />
</proxy>                
```

Invoke the following proxy with the below payload using a SOAP client like SOAP UI. 
Proxy: http://localhost:8280/services/Script1

```
<soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope" xmlns:ser="http://services.samples" xmlns:xsd="http://services.samples/xsd">
   <soap:Header/>
   <soap:Body>
      <ser:getQuote>
         <!--Optional:-->
         <ser:request>
            <!--Optional:-->
            <xsd:symbol>IBM</xsd:symbol>
         </ser:request>
      </ser:getQuote>
   </soap:Body>
</soap:Envelope>
```

The response will be as below after the response is enriched. 

```
<soapenv:Envelope xmlns:soapenv="http://www.w3.org/2003/05/soap-envelope">
   <soapenv:Body>
      <ns:getQuoteResponse xmlns:ns="http://services.samples">
         <ns:return xsi:type="ax21:GetQuoteResponse" xmlns:ax21="http://services.samples/xsd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
            <ax21:name>IBM Company</ax21:name>
            <ax21:Open>69.40005762027275</ax21:Open>
         </ns:return>
      </ns:getQuoteResponse>
   </soapenv:Body>
</soapenv:Envelope>
```


## Deployment guidelines

* We can simply deploy by copying the CAR archive into <EI_HOME>/repository/deployment/server/carbonapps directory, and it will be deployed.

<p align="center"><b> OR </b></p>

* We can create the proxy in Management Console and deploy.


## Supported versions
This is supported in all the EI and ESB versions

## Test cases

| ID        | Summary                                                                                                          |
| ----------|:---------------------------------------------------------------------------------------------------------------: |
| 1.6.11.1  | Modify the response by using a script mediator with inline groovy script                                         |
| 1.6.11.2  | Modify the response by using a script mediator with the groovy script saved in governance registry               |
| 1.6.11.3  | Modify the response by using a script mediator with the groovy script saved in configuration registry            |
| 1.6.11.4  | Modify the response using script mediator with inline javascript.                                                |
| 1.6.11.5  | Modify the response by using a script mediator with the javascript saved in governance registry                  |
| 1.6.11.6  | Modify the response by using a script mediator with the javascript saved in configuration registry               |
| 1.6.11.7  | Modify the response using script mediator with inline ruby.                                                      |
| 1.6.11.8  | Modify the response by using a script mediator with the ruby script saved in governance registry                 |
| 1.6.11.9  | Modify the response by using a script mediator with the ruby script saved in configuration registry              |
                                                          

