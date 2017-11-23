# Soap Connector Sample

This sample consumes an XML request and using the soap connector construct the soap request and send that to the mock backend service.
The mock backend will then respond with a soap response.
And the client will get the soap body as an XML response.

## How to run the sample

bin$ ./integrator.sh ../samples/soap-connector/soap-connector-sample.balx

## Invoking the service
Create a payload.xml file with the following content.
```
<ser:getSimpleQuote xmlns:ser="http://services.samples">
   <ser:symbol>ABC</ser:symbol>
</ser:getSimpleQuote>
```
Invoke the service using cURL as follows
```
curl -v -d@payload.xml -H"Content-Type: application/xml" http://localhost:9090/soapSample
```