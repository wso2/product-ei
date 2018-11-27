# 1.6.2 Modify payload by adding a sibling using enrich mediator

## When to use
The Enrich Mediator can process a message based on a given source configuration and then perform the specified action on the message by using the target configuration. It is often used to do slight modifications to the payload. There are main three target actions as “Replace”, “Child” and “Sibling”. In this scenario we focus on adding a sibling element to the payload. 


## Sample use case
Assume that we have our original payload to be sent as below. 

original payload
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

Before sending this to the endpoind, we need to modify this payload by adding a sibling element to ```<ser:request>``` element. The sibling will be ```<xsd:symbol>SUN</xsd:symbol>```

Required payload
```
<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope" xmlns:ser="http://services.samples" xmlns:xsd="http://services.samples/xsd">
   <soap:Body>
      <ser:getQuote>
         <!--Optional:-->
         <ser:request>
            <!--Optional:-->
            <xsd:symbol>IBM</xsd:symbol>
         </ser:request>
         <xsd:symbol>SUN</xsd:symbol>
      </ser:getQuote>
   </soap:Body>
</soap:Envelope>
```

This slight modification to the payload can be done using enrich mediator. 

mediator
```
<?xml version="1.0" encoding="UTF-8"?>
<enrich xmlns="http://ws.apache.org/ns/synapse">
    <source clone="true" type="inline">
        <xsd:symbol xmlns:xsd="http://services.samples/xsd">SUN</xsd:symbol>
    </source>
    <target action="sibling" type="custom"
        xmlns:ser="http://services.samples"
        xmlns:xsd="http://services.samples/xsd" xpath="//ser:getQuote/ser:request"/>
</enrich>
```

## Prerequisites
N/A

## Development guidelines

Sequence and proxy Configuration

Create the following sequence and add to the proxy service. 

```xml
<?xml version="1.0" encoding="UTF-8"?>
<proxy xmlns="http://ws.apache.org/ns/synapse" name="Enrich3" startOnLoad="true" statistics="disable" trace="disable" transports="http,https">
   <target>
      <inSequence>
         <log level="full" />
         <enrich>
            <source clone="true" type="inline">
               <xsd:symbol xmlns:xsd="http://services.samples/xsd">SUN</xsd:symbol>
            </source>
            <target xmlns:ser="http://services.samples" xmlns:xsd="http://services.samples/xsd" action="sibling" xpath="//ser:getQuote/ser:request" />
         </enrich>
         <log level="full" />
         <respond />
      </inSequence>
   </target>
   <description />
</proxy>
                                
```

Invoke the getQuote service with the following payload. Use an SOAP client like SOAP UI. 
Proxy: http://localhost:8280/services/Enrich3

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

Above proxy will add the sibling element <xsd:symbol>SUN</xsd:symbol> to the <ser:request> element before the payload is sent to the endpoint. You will see the following respond which is the actual request enriched.

```
<soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope" xmlns:xsd="http://services.samples/xsd" xmlns:ser="http://services.samples">
   <soap:Body>
      <ser:getQuote>
         <!--Optional:-->
         <ser:request>
            <!--Optional:-->
            <xsd:symbol>IBM</xsd:symbol>
         </ser:request>
         <xsd:symbol>SUN</xsd:symbol>
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
| 1.6.2.1   | Adding sibling to message body source defined by xpath                                                     |
| 1.6.2.2   | Adding entire message body to target body as sibling                                              		     |
| 1.6.2.3   | Adding source property to the target body as sibling                                                       |
| 1.6.2.4   | Adding source inline content to the target body as sibling                                                 |
| 1.6.2.5   | Adding source inline content from configuration registry to the target body as sibling                     |
| 1.6.2.6   | Adding source inline content from governance registry to the target body as sibling                        |
| 1.6.2.7   | Adding sibling to target message body source defined by xpath   						                               |
| 1.6.2.8   | Adding sibling to target message body using source body     											                         |
| 1.6.2.9   | Adding sibling to target message body using source property                                      				   |   
| 1.6.2.10  | Adding sibling to target message body using source inline content                                       	 |
| 1.6.2.11  | Adding sibling to target message body using source inline content from configuration registry              |
| 1.6.2.12  | Adding sibling to target message body using source inline content from governance registry                 |           
                     

