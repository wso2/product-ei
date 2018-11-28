2.2.1  Listen to a JMS queue, consume messages, and send them to a HTTP back-end service

![EI as JMS consumer](images/JMS-Consumer.png)

When to use
Listen to a JMS queue, consume messages, and send them to a HTTP back-end service.

Sample use-case
--sample use-case

Supported versions

Pre-requisites
    -Configure WSO2 ESB with Apache ActiveMQ, and set up the JMS listener. For instructions, see [Configure with ActiveMQ](https://docs.wso2.com/display/ESB500/Configure+with+ActiveMQ).
    -Start the ESB server.

Development guidelines

REST API (if available)
N/A

Deployment guidelines
--deployment instructions--

Test cases

| ID | Summary |
| ------------- | ------------- |
| 2.2.1.1  | Use inbound endpoint as proxy service ,Config OUT_ONLY property is set to true to indicate that message exchange is one-way    |
| 2.2.1.2  | Proxy service can listen to the queue, pick up a message and do a two-way HTTP call (OUT_ONLY property is not used.)       |
| 2.2.1.3  | Messages consumed from a queue as a SOAP message    |
| 2.2.1.4  | Messages consumed from a queue as a application/xml.       |
| 2.2.1.5  | Failover due to backend is not started and then connection refused.     |
| 2.2.1.6  | Backend is not responding then retry after given timeout period.       |