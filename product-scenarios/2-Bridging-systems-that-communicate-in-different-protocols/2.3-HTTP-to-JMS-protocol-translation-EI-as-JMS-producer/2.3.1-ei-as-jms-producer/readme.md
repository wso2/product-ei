# 2.3.1-Receive messages via HTTP and send them to JMS queue by using  ActiveMQ and JMS sender.

![EI as JMS consumer](images/JMS-producer   .png)

## When to use
In this sections walk you through the steps to configure EI to listen to a HTTP and send them to JMS queue by 
using  ActiveMQ and JMS sender.

## Sample use-case
--sample use-case

## Supported versions

## Pre-requisites

- Configure WSO2 EI with Apache ActiveMQ, and set up the JMS listener. For instructions, see [Configure with ActiveMQ]
(https://docs.wso2.com/display/EI640/Configure+with+ActiveMQ).
- Start the EI server.

## Development guidelines

- Create a proxy service with the following configuration. To create a proxy service using Tooling, see Working with Proxy Services via Tooling.see [Working with Proxy Services via Tooling](https://docs.wso2.com/display/EI600/Working+with+Proxy+Services+via+Tooling).

```xml
<proxy xmlns="http://ws.apache.org/ns/synapse" name="StockQuoteProxy" transports="http">
    <target>
        <inSequence>
            <property action="set" name="OUT_ONLY" value="true"/>
            <send>
                <endpoint>
                    <address uri=""/> <!-- Specify the JMS connection URL here -->
                </endpoint>
            </send>
        </inSequence>
        <outSequence/>
    </target>
    <publishWSDL uri="file:samples/service-bus/resources/proxy/sample_proxy_1.wsdl"/>
</proxy>
```
- Within the inSequence, the OUT_ONLY property is set to true to indicate that message exchange is one-way.

## REST API (if available)
N/A

## Deployment guidelines
--deployment instructions--

## Test cases

| ID | Summary |
| ------------- | ------------- |
| 2.3.1.1  | Use inbound endpoint as proxy service    |
| 2.3.1.2  | Config JMS connection URL with four connection factory parameters (depend on the type of the JMS broker)   |
| 2.3.1.3  | Failover due to backend is not started and then connection refused.    |
| 2.3.1.4  | Backend is not responding retry after given timeout period.       |
