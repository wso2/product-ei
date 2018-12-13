# 2.5.1-Receiving SOAP messages over UDP and forwarding them over HTTP to the backend.

## When to use
Receive SOAP messages over UDP and forwarding them over HTTP to the backend.

 
## Sample use-case

## Supported versions

## Pre-requisites

- [Configure Synapse to use the UDP transport](https://docs.wso2.com/display/EI640/Setting+Up+the+ESB+Samples#SettingUptheESBSamples-ConfigureWSO2ESBforUDPTransport).The sample 
Axis2 client should also be setup to send UDP requests.
- Start Synpase.
- Start Axis2 server with SimpleStockService deployed.

## Development guidelines

## REST API (if available)
N/A

## Deployment guidelines
- Demonstrate receiving SOAP messages over UDP and forwarding them over HTTP.

```xml
<definitions xmlns="http://ws.apache.org/ns/synapse"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://ws.apache.org/ns/synapse http://synapse.apache.org/ns/2010/04/configuration/synapse_config.xsd">
 
    <proxy name="StockQuoteProxy" transports="udp">
        <target>
            <endpoint>
                <address uri="http://localhost:9000/services/SimpleStockQuoteService"/>
            </endpoint>
            <inSequence>
                <log level="full"/>
                <property name="OUT_ONLY" value="true"/>
            </inSequence>
        </target>
        <parameter name="transport.udp.port">9999</parameter>
        <parameter name="transport.udp.contentType">text/xml</parameter>
        <publishWSDL uri="file:samples/service-bus/resources/proxy/sample_proxy_1.wsdl"/>
    </proxy>
</definitions>
```

## Test cases

| ID | Summary |
| ------------- | ------------- |
| 2.5.1.1  | UDP transport configurations for send messages in user datagram protocol (UDP) format.     |
| 2.5.1.2  | Failover due to backend is not started and then connection refused.   |
| 2.5.1.3  | Backend is not responding retry after given timeout period.  |
