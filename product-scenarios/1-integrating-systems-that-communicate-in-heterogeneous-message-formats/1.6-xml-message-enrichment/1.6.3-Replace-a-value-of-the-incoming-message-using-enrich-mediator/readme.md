# 1.6.3 Replace a value of the incoming message using enrich mediator

## When to use
The Enrich Mediator can process a message based on a given source configuration and then perform the specified action on the message by using the target configuration. It is often used to do slight modifications to the payload. There are main three target actions as “Replace”, “Child” and “Sibling”. In this scenario we focus on replacing a value of the incoming payload. 


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
            <xsd:symbol>IBM</xsd:symbol>
         </ser:request>
      </ser:getQuote>
   </soapenv:Body>
</soapenv:Envelope>
```

Before sending this to the endpoind, we need to modify this payload by replacing the value of symbol 'IBM' to 'WSO2'. 

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
         </ser:request>
      </ser:getQuote>
   </soapenv:Body>
</soapenv:Envelope>
```

This slight modification to the payload can be done using enrich mediator. 

mediator
```
<?xml version="1.0" encoding="UTF-8"?>
<enrich xmlns="http://ws.apache.org/ns/synapse">
    <source clone="true" type="inline">
        <xsd:symbol xmlns:xsd="http://services.samples/xsd">WSO2</xsd:symbol>
    </source>
    <target action="replace" type="custom"
        xmlns:ser="http://services.samples"
        xmlns:xsd="http://services.samples/xsd" xpath="//ser:getQuote/ser:request/xsd:symbol"/>
</enrich>
```

## Prerequisites
N/A 

## Development guidelines

Sequence and proxy Configuration

Create the following sequence and add to the proxy service. 

```xml
<?xml version="1.0" encoding="UTF-8"?>
<proxy xmlns="http://ws.apache.org/ns/synapse" name="Enrich" startOnLoad="true" statistics="disable" trace="disable" transports="http,https">
   <target>
      <inSequence>
         <log level="full" />
         <enrich>
            <source clone="true" type="inline">
               <xsd:symbol xmlns:xsd="http://services.samples/xsd">WSO2</xsd:symbol>
            </source>
            <target xmlns:ser="http://services.samples" xmlns:xsd="http://services.samples/xsd" xpath="//ser:getQuote/ser:request/xsd:symbol" />
         </enrich>
         <log level="full" />
         <respond />
      </inSequence>
   </target>
   <description />
</proxy>
                                
```

Invoke the getQuote service with the following payload. Use an SOAP client like SOAP UI. 
Proxy: http://localhost:8280/services/Enrich

```
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ser="http://services.samples" xmlns:xsd="http://services.samples/xsd">
   <soapenv:Header />
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

Above proxy will Replace the symbol value with the 'WSO2' beofore sending the message to the endpoint. You will see the following response which is the actual request enriched.


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

## Deployment guidelines

* We can simply deploy by copying the CAR archive into <EI_HOME>/repository/deployment/server/carbonapps directory, and it will be deployed.

<p align="center"><b> OR </b></p>

* We can create the proxy in Management Console and deploy.


## Supported versions
This is supported in all the EI and ESB versions

## Test cases

| ID        | Summary                                                                                                    |
| ----------|:---------------------------------------------------------------------------------------------------------: |
| 1.6.3.1   | Replacing body of payload using source defined by property                                                   |
| 1.6.3.2   | Replacing target message by source defined through custom xpath                                              |
| 1.6.3.3   | Replacing body of payload using source body                                                                  |
| 1.6.3.4   | Replacing target message defined through xpath by source body                                                |
| 1.6.3.5   | Replacing target message defined through xpath by source property                                            |
| 1.6.3.6   | Replacing target message defined through xpath by source inline content                                      |
| 1.6.3.7   | Replacing target message defined through xpath by source inline content loaded from configuration registry   |
| 1.6.3.8   | Replacing target message defined through xpath by source inline content loaded from governance registry      |
| 1.6.3.9   | Replacing a payload using content defined as local entry using inline                                        |   
| 1.6.3.10  | Replacing target message by source defined through custom get property                                       |
| 1.6.3.11  | Replacing body of payload using source defined inline through local entry                                    |
| 1.6.3.12  | Replacing body of payload using source defined inline through governance registry                            |
| 1.6.3.13  | Replacing body of payload using source defined inline through configuration registry                         |
| 1.6.3.14  | Source envelope replacing target message property                                                            |

