# 1.6.5.1 Modify the response partially using enrich mediator

## When to use
We can use set of WSO2 EI mediators to extend the response which is sent from backend. This can be done to the body of the response or to the response header. 

## Sample use case
In this example we will focuss on extend the response message which is sent from the backend by attaching the request. We store the body of the payload to a property called REQUEST_PAYLOAD using a one enrich mediator.  There is another enrich mediator which adds the content of the property REQUEST_PAYLOAD to the body of the response message. So now the response to the client should be the response from the back end plus the request payload. 

Request payload
```
<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope" xmlns:ser="http://services.samples" xmlns:xsd="http://services.samples/xsd">
   <soap:Header />
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

Original Response for this payload without calling the proxy. 
```
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
   <soapenv:Body>
      <ns:getQuoteResponse xmlns:ns="http://services.samples">
         <ns:return xsi:type="ax21:GetQuoteResponse" xmlns:ax21="http://services.samples/xsd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
            <ax21:change>-2.69312395551027</ax21:change>
            <ax21:earnings>13.816048335917998</ax21:earnings>
            <ax21:high>-55.546585167623185</ax21:high>
            <ax21:last>56.33941167148374</ax21:last>
            <ax21:lastTradeTimestamp>Wed Nov 28 14:45:34 IST 2018</ax21:lastTradeTimestamp>
            <ax21:low>-55.85785389893407</ax21:low>
            <ax21:marketCap>1.3960837077288177E7</ax21:marketCap>
            <ax21:name>IBM Company</ax21:name>
            <ax21:open>58.22817776592868</ax21:open>
            <ax21:peRatio>25.262281210284513</ax21:peRatio>
            <ax21:percentageChange>4.958085123183013</ax21:percentageChange>
            <ax21:prevClose>-54.3178240913566</ax21:prevClose>
            <ax21:symbol>IBM</ax21:symbol>
            <ax21:volume>17522</ax21:volume>
         </ns:return>
      </ns:getQuoteResponse>
   </soapenv:Body>
</soapenv:Envelope>
```

Now we are going to attach the request payload to this response. This can be done using an enrich mediator. 


## Prerequisites
SimpleStockQuote service should be up and running. 

## Development guidelines

Sequence and proxy Configuration

Create the following sequence and add to the proxy.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<proxy xmlns="http://ws.apache.org/ns/synapse" name="Enrich" startOnLoad="true" statistics="disable" trace="disable" transports="http,https">
   <target>
      <inSequence>
         <log level="full" />
         <enrich>
            <source clone="true" type="body" />
            <target property="REQUEST_PAYLOAD" type="property" />
         </enrich>
         <log level="full" />
      </inSequence>
      <outSequence>
         <log level="full" />
         <enrich>
            <source clone="true" property="REQUEST_PAYLOAD" type="property" />
            <target action="sibling" type="body" />
         </enrich>
         <send />
      </outSequence>
      <endpoint>
         <address uri="http://localhost:9000/services/SimpleStockQuoteService" />
      </endpoint>
   </target>
   <description />
</proxy>
```

Invoke the following proxy with the below payload using a SOAP client like SOAP UI. 
Proxy: http://localhost:8280/services/Enrich

```
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ser="http://services.samples" xmlns:xsd="http://services.samples/xsd">
   <soapenv:Header/>
   <soapenv:Body>
      <ser:getQuote>
         <!--Optional:-->
         <ser:request>
            <!--Optional:-->
            <xsd:symbol>IBM</xsd:symbol>
         </ser:request>
      </ser:getQuote>
   </soapenv:Body>
</soapenv:Envelope>
```

The response will be as below after the response is enriched. 

```
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://www.w3.org/2003/05/soap-envelope">
   <soapenv:Body>
      <ns:getQuoteResponse xmlns:ns="http://services.samples">
         <ns:return xmlns:ax21="http://services.samples/xsd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="ax21:GetQuoteResponse">
            <ax21:change>-2.9414902067708963</ax21:change>
            <ax21:earnings>-9.088973740464933</ax21:earnings>
            <ax21:high>-66.49514178882588</ax21:high>
            <ax21:last>66.55503850264634</ax21:last>
            <ax21:lastTradeTimestamp>Wed Nov 28 14:49:30 IST 2018</ax21:lastTradeTimestamp>
            <ax21:low>68.72552938917156</ax21:low>
            <ax21:marketCap>5.831977663548674E7</ax21:marketCap>
            <ax21:name>IBM Company</ax21:name>
            <ax21:open>68.27322136608329</ax21:open>
            <ax21:peRatio>24.162572632789754</ax21:peRatio>
            <ax21:percentageChange>4.622211968389575</ax21:percentageChange>
            <ax21:prevClose>-63.63815045452667</ax21:prevClose>
            <ax21:symbol>IBM</ax21:symbol>
            <ax21:volume>7967</ax21:volume>
         </ns:return>
      </ns:getQuoteResponse>
      <ser:getQuote xmlns:ser="http://services.samples">
         <!--Optional:-->
         <ser:request>
            <!--Optional:-->
            <xsd:symbol xmlns:xsd="http://services.samples/xsd">IBM</xsd:symbol>
         </ser:request>
      </ser:getQuote>
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

| ID        | Summary                                              |
| ----------|:---------------------------------------------------: |
| 1.6.5.1.1 | Attach the request message to the response message   |
| 1.6.5.1.2 | Injecting headers to response                        |
                                                           

