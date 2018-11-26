# 1.1.2 Converting SOAP message to JSON using MessageType property


![SOAP to JSON conversion](images/SOAP-to-JSON.png)


## When to use
The reconstruction of entire message payload is needed when required format of Client and Service are different. 
In this use case we can use Property mediator to transform SOAP response to JSON.


## Sample use case
Here we have a SOAP service which gives out the information of a city taking the zip code as the input.  
We are going to expose this SOAP web service as a REST API by doing a SOAP to JSON conversion.

## Prerequisites
A REST client like cURL to invoke the ESB API.

## Development guidelines

REST API Configuration

The API resource is configured with an URI-Template and parameterized to get the ZIP code. 
A request payload is constructed with a given zip code. Also, ‘SOAPAction’ which is a mandatory header for SOAP 1.1 is 
set before invoking the endpoint. Once the response is received, it is sent back to the client by converting the XML 
message to a JSON message using the messageType property.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<api xmlns="http://ws.apache.org/ns/synapse" name="CityInformationAPI" context="/city">
   <resource methods="GET" uri-template="/lookup/{zipCode}">
      <inSequence>
         <payloadFactory media-type="xml" description="Build SOAP request payload">
            <format>
               <tem:LookupCity xmlns:tem="http://tempuri.org">
                  <tem:zip>$1</tem:zip>
               </tem:LookupCity>
            </format>
            <args>
               <arg evaluator="xml" expression="get-property('uri.var.zipCode')"/>
            </args>
         </payloadFactory>
         <header name="Action" scope="default" value="http://tempuri.org/SOAP.Demo.LookupCity" description="Set SOAPAction header"/>
         <property name="REST_URL_POSTFIX" scope="axis2" action="remove" description="Avoid appending resource to endpoint URL"/>
         <send description="Send Request Payload to SOAP endpoint">
            <endpoint key="CityLookupEP"/>
         </send>
      </inSequence>
      <outSequence>
         <property name="messageType" value="application/json" scope="axis2"/>
         <respond description="Respond to client"/>
      </outSequence>
      <faultSequence>
         <payloadFactory media-type="json" description="">
            <format>
            {  "Error": {    "message": "Error while processing the request",    "code": "$1",    "description": "$2"  }}
            </format>
            <args>
               <arg evaluator="xml" expression="$ctx:ERROR_CODE"/>
               <arg evaluator="xml" expression="$ctx:ERROR_MESSAGE"/>
            </args>
         </payloadFactory>
         <respond/>
      </faultSequence>
   </resource>
</api>
```

Endpoint Configuration

```xml
<endpoint name="CityLookupEP" xmlns="http://ws.apache.org/ns/synapse">
    <address format="soap11" uri="http://www.crcind.com/csp/samples/SOAP.Demo.cls"/>
</endpoint>
```

Invoke the service with the following request. Use an HTTP client like cURL.

```xml
curl http://localhost:8280/city/lookup/60601
```

## Deployment guidelines

* We can simply deploy by copying the CAR archive into <EI_HOME>/repository/deployment/server/carbonapps directory, and it will be deployed.

<p align="center"><b> OR </b></p>

* We can create the api in Management Console and deploy.

## REST API

Use "RestApiAdmin" admin service to deploy REST API

## Supported versions
This is supported in all the EI and ESB versions

## Test cases

| ID        | Summary                                       |
| ----------|:---------------------------------------------:|
| 1.1.2.1   | Processing expected SOAP message (Happy path) |
| 1.1.2.2   | Handling malformed SOAP message               |
| 1.1.2.3   | Handling large sized SOAP message             |
