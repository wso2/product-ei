# 2.4.1-Receive SOAP messages over TCP and forwarding them over HTTP to the backend.

## When to use
Receive SOAP messages over TCP and forwarding them over HTTP to the backend.

 
## Sample use-case

TCP is not an application layer protocol. Hence there are no application level headers available in the requests. EI 
has to simply read the XML content coming through the socket and dispatch it to the right proxy service based on the 
information available in the message payload itself. The TCP transport is capable of dispatching requests based on  
addressing headers or the first element in the SOAP body. In this sample, we will get the sample client to send  
WS-Addressing headers in the request. Therefore the dispatching will take place based on the addressing header values.


## Supported versions

## Pre-requisites

- Configure EI to use the TCP transport and configure sample Axis2 client to send TCP requests. See here for details 
on how to do this.
- Start Synpase using [sample](https://docs.wso2.com/pages/viewpage.action?pageId=103322226).
- Start Axis2 server with SimpleStockService deployed.

## Development guidelines

## REST API (if available)
N/A

## Deployment guidelines
- Demonstrate receiving SOAP messages over TCP and forwarding them over HTTP

```xml
<definitions xmlns="http://ws.apache.org/ns/synapse"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://ws.apache.org/ns/synapse http://synapse.apache.org/ns/2010/04/configuration/synapse_config.xsd">
 
    <proxy name="StockQuoteProxy" transports="tcp">
        <target>
            <endpoint>
                <address uri="http://localhost:9000/services/SimpleStockQuoteService"/>
            </endpoint>
            <inSequence>
                <log level="full"/>
                <property name="OUT_ONLY" value="true"/>
            </inSequence>
        </target>
    </proxy>
 
</definitions>
```

## Test cases

| ID | Summary |
| ------------- | ------------- |
| 2.4.1.1  | Use TCP transport sender configurations with application/xml.    |
| 2.4.1.2  | Use TCP transport sender configurations with application/json.   |
| 2.4.1.3  | Use TCP transport sender configurations with text/html.  |
| 2.4.1.4  | Use TCP transport sender configurations with valid Port.       |
| 2.4.1.5  | Use TCP transport sender configurations with valid Host name.    |
| 2.4.1.6  | Use TCP transport sender configurations with valid responseClient.   |
| 2.4.1.7  | Failover due to backend is not started and then connection refused.   |
| 2.4.1.8  | Backend is not responding retry after given timeout period.       |
