# 1.6.8 Modify the selected element of response

## When to use
We can use WSO2 XSLT mediator to modify a selected element in the response which is sent from EI before it sent to the backend. 

## Sample use case
The use case is as follows, WSO2 EI will get a request and it will be sent to the backend. Once it gets the response, a selected element of the response will be modified and displayed to the user.

Request Payload
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

Original Response
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
Now we need to remove ```<ax21:name>IBM Company</ax21:name>``` element from the response. To make the transformation possible we will be using the WSO2 ESB XSLT mediator. Transformation will be done according to the xslt provided to the service.

## Prerequisites
SimpleStockQuote service needs to be up and running. 

## Development guidelines

Sequence and proxy Configuration

Create the following sequence and add to the proxy.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<proxy xmlns="http://ws.apache.org/ns/synapse" name="Proxy1" startOnLoad="true" statistics="disable" trace="disable" transports="http,https">
   <target>
      <inSequence>
         <log level="full" />
      </inSequence>
      <outSequence>
         <log level="full" />
         <xslt key="gov:/xslt/transform.xslt" />
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

transform.xslt
```
<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ax21="http://services.samples/xsd" xmlns:fn="http://www.w3.org/2005/02/xpath-functions" xmlns:ns="http://services.samples" version="2.0">
   <xsl:output method="xml" omit-xml-declaration="yes" indent="yes" />
   <xsl:template match="node()|@*">
      <xsl:copy>
         <xsl:apply-templates select="node()|@*" />
      </xsl:copy>
   </xsl:template>
   <xsl:template match="ax21:name" />
</xsl:stylesheet>
```

Save this transform.xslt in /system/governanace/xslt/ folder. 

Invoke the following proxy with the below payload using a SOAP client like SOAP UI. 
Proxy: http://localhost:8280/services/Proxy1

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
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
   <soapenv:Body>
      <ns:getQuoteResponse xmlns:ns="http://services.samples">
         <ns:return xsi:type="ax21:GetQuoteResponse" xmlns:ax21="http://services.samples/xsd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
            <ax21:change>-2.826090632692984</ax21:change>
            <ax21:earnings>13.01645430986072</ax21:earnings>
            <ax21:high>-177.98915312043724</ax21:high>
            <ax21:last>180.9396500900612</ax21:last>
            <ax21:lastTradeTimestamp>Fri Nov 30 18:27:33 IST 2018</ax21:lastTradeTimestamp>
            <ax21:low>-178.5336071884377</ax21:low>
            <ax21:marketCap>1.2358118718062818E7</ax21:marketCap>
            <ax21:open>-178.56663696439568</ax21:open>
            <ax21:peRatio>23.87385055591428</ax21:peRatio>
            <ax21:percentageChange>1.677763991454756</ax21:percentageChange>
            <ax21:prevClose>-168.44387214691244</ax21:prevClose>
            <ax21:volume>8090</ax21:volume>
            <ax21:symbol>IBM</ax21:symbol>
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

| ID        | Summary                                                                                                  |
| ----------|:-------------------------------------------------------------------------------------------------------: |
| 1.6.8.1   | Selected element of response transformation, XSLT loaded from configuration registry using xslt mediator  |
| 1.6.8.2   | Selected element of response transformation, XSLT loaded from governance registry using xslt mediator    |
| 1.6.8.3   | Selected element of response transformation, XSLT loaded from dynamic key using xslt mediator            |
                                                           

