# Soap Connector Sample

This sample consumes an XML request and using the soap connector construct the soap request and send that to the mock backend service.
The mock backend will then respond with a soap response.
And the client will get the soap body as an XML response.

### Building the Scenario

For this scenario we would need the following,

- Service which will act as the SOAP back-end 
[SimpleStockQuoteService](services/samples/stockquote/SimpleStockQuoteService.bal)  
- Service which will act as a proxy to invoke the SOAP bak-end 
[SoapConnectorSample](services/samples/queue/TravelRequestGateway.bal)  

Composing the above services soap-connector-sample.balx is created.

### Generating a balx

Currently balx is generated through ballerina-tools distribution which could be located in 
[ballerina-tools](https://ballerinalang.org/downloads/)
.Please follow the instructions in the site to setup ballerina-tools in the environment. 

Currently EI includes only ballerina runtime, hence EI will not have Out of Box (OOB) support for composing a balx. 
Ballerina tools pertaining to EI would be included in the future releases. 

If it's intended to modify the source please refer section "modifying the source".

### How to run the sample

bin$ ./integrator.sh ../samples/soap-connector/soap-connector-sample.balx

### Invoking the service
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

### Modifying the source

If it's intended to modify the existing samples following could be done,

1. As elaborated in the section "Building the Scenario", the relevant source could be found
2. Modify the source as expected 
3. Once modified in order to execute the scenario the following could be done

The services related to SOAP sample could be executed in the following manner,

- Navigate to the directory <EI_HOME>/samples/soap-connector/services
- Execute the following,

samples/soap-connector/services$ ../../../bin/integrator.sh samples/stockquote
