# 1.6.1 Modify payload by adding a child element using enrich mediator

## When to use
The Enrich Mediator can process a message based on a given source configuration and then perform the specified action on the message by using the target configuration. It is often used to do slight modifications to the payload. There are main three target actions as “Replace”, “Child” and “Sibling”. In this scenario we focus on adding a child element to the payload. 


## Sample use case
Assume that we have our original payload to be sent as below. 

original payload
```
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ser="http://services.samples" xmlns:xsd="http://services.samples/xsd">
   <soapenv:Header />
   <soapenv:Body>
      <ser:getQuote>
         <!--Optional:-->
         <ser:request>
            <!--Optional:-->
            <xsd:symbol>WSO2</xsd:symbol>
         </ser:request>
      </ser:getQuote>
   </soapenv:Body>
</soapenv:Envelope>
```

Before sending this to the endpoind, we need to modify this payload by adding a child element to ```<ser:request>``` element. The child element will be ```<xsd:symbol>SUN</xsd:symbol>```

Required payload
```
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ser="http://services.samples" xmlns:xsd="http://services.samples/xsd">
   <soapenv:Header />
   <soapenv:Body>
      <ser:getQuote>
         <!--Optional:-->
         <ser:request>
            <!--Optional:-->
            <xsd:symbol>WSO2</xsd:symbol>
            <xsd:symbol>SUN</xsd:symbol>
         </ser:request>
      </ser:getQuote>
   </soapenv:Body>
</soapenv:Envelope>
```

This slight modification to the payload can be done using enrich mediator. 

mediator
```
<?xml version="1.0" encoding="UTF-8"?>
<inSequence xmlns="http://ws.apache.org/ns/synapse">
   <log level="full" />
   <enrich>
      <source clone="true" type="inline">
         <xsd:symbol xmlns:xsd="http://services.samples/xsd">SUN</xsd:symbol>
      </source>
      <target xmlns:ser="http://services.samples" xmlns:xsd="http://services.samples/xsd" action="child" type="custom" xpath="//ser:getQuote/ser:request" />
   </enrich>
   <log level="full" />
   <respond />
</inSequence>
```

## Prerequisites
N/A

## Development guidelines

Sequence and proxy Configuration

Create the following sequence and add to the proxy service. 

```xml
<?xml version="1.0" encoding="UTF-8"?>
<proxy xmlns="http://ws.apache.org/ns/synapse" name="Enrich2" startOnLoad="true" statistics="disable" trace="disable" transports="http,https">
   <target>
      <inSequence>
         <log level="full" />
         <enrich>
            <source clone="true" type="inline">
               <xsd:symbol xmlns:xsd="http://services.samples/xsd">SUN</xsd:symbol>
            </source>
            <target xmlns:ser="http://services.samples" xmlns:xsd="http://services.samples/xsd" action="child" xpath="//ser:getQuote/ser:request" />
         </enrich>
         <log level="full" />
         <respond />
      </inSequence>
   </target>
   <description />
</proxy>    
```

Invoke the getQuote service with the following payload. Use an SOAP client like SOAP UI. 
Proxy: http://localhost:8280/services/Enrich2

```
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ser="http://services.samples" xmlns:xsd="http://services.samples/xsd">
   <soapenv:Header />
   <soapenv:Body>
      <ser:getQuote>
         <!--Optional:-->
         <ser:request>
            <!--Optional:-->
            <xsd:symbol>WSO2</xsd:symbol>
         </ser:request>
      </ser:getQuote>
   </soapenv:Body>
</soapenv:Envelope>
```

Above proxy will add the child element ```<xsd:symbol>SUN</xsd:symbol>``` to the ```<ser:request>``` element before the payload is sent to the endpoint. 

The request before enrich mediator is invoked. 

```
<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope" xmlns:ser="http://services.samples" xmlns:xsd="http://services.samples/xsd">
   <soap:Body>
      <ser:getQuote>
         <!--Optional:-->
         <ser:request>
            <!--Optional:-->
            <xsd:symbol>WSO2</xsd:symbol>
         </ser:request>
      </ser:getQuote>
   </soap:Body>
</soap:Envelope>
```

You will see the following respond which is the actual request enriched. 
```
<soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope" xmlns:xsd="http://services.samples/xsd" xmlns:ser="http://services.samples">
   <soap:Body>
      <ser:getQuote>
         <!--Optional:-->
         <ser:request>
            <!--Optional:-->
            <xsd:symbol>IBM</xsd:symbol>
            <xsd:symbol>SUN</xsd:symbol>
         </ser:request>
      </ser:getQuote>
   </soap:Body>
</soap:Envelope>
```

## Deployment guidelines

* We can simply deploy by copying the CAR archive into <EI_HOME>/repository/deployment/server/carbonapps directory, and it will be deployed.

<p align="center"><b> OR </b></p>

* We can create the proxy in Management Console and deploy.


## Supported versions
This is supported in all the EI and ESB versions

## Test cases

| ID        | Summary                                                                                                    |
| ----------|:---------------------------------------------------------------------------------------------------------: |
| 1.6.1.1   | Adding child to target message body using source inline content                                            |
| 1.6.1.2   | Adding child to message body source defined by xpath                                              		    |
| 1.6.1.3   | Adding source message body to the target body as child                                                     |
| 1.6.1.4   | Adding source property to the target body as child                                                		    |
| 1.6.1.5   | Adding source defined as inline content to the target body as child                                        |
| 1.6.1.6   | Adding source defined as inline configuration registry to the target body as child                         |
| 1.6.1.7   | Adding source defined as inline governance registry to the target body as child   						       |
| 1.6.1.8   | Adding child to target message body source defined by xpath      											          |
| 1.6.1.9   | Adding child to target message body using source body                                        				    |   
| 1.6.1.10  | Adding child to target message body using source property                                       			    |
| 1.6.1.11  | Adding child to target message body using source inline content from configuration registry                |
| 1.6.1.12  | Adding child to target message body using source inline content from governance registry                   |
| 1.6.1.13  | Adding source property with type OM to the target body as child                        				 	       |
                                                           

