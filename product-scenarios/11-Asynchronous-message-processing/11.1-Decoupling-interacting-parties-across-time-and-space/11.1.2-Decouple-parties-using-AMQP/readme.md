# 11.1.2 Decouple interacting parties using AMQP


## When to use

AMQP (Advance Message Queing Protocol) is the widely used protocol for message-oriented middleware. It can be used for 
both point-to-point communication and publish-subscribe communication. 

JMS (Java Message Service) is the Java provided interface by Message Broker vendors to deal with asynchronous messaging.
However, message brokers like RabbitMQ expose APIs related to AMQP layer. Hence, WSO2 EI provides separate transport to 
send messages and receive messages from Rabbit MQ. 

If you use RabbitMQ as the broker and using AMQP related APIs to communicate, you can use these features.  

## Sample use case

Integrator will receive a HTTP message (a SOAP message), and place it in a RabbitMQ MQ queue. Then the integrator will 
poll messages of that queue and get the message and invoke the backend service. The reliability is handled by different 
Acknowledgement methods. 

## Prerequisites

Rabbit MQ broker deployment with JMS support
SOAP client 
HTTP backend service accepting SOAP messages and reply with  SOAP messages


## Development guidelines

Use EI tooling to develop EI artifacts 
Use Management Console of RabbitMQ to setup queues/topics 


## Deployment guidelines

Deploy RabbitMQ broker in a docker container

## Supported versions

This is supported in all the EI and ESB versions

## Test cases

| ID        | Summary                                                                                                                       |
| ----------|:------------------------------------------------------------------------------------------------------------------------------:                                                                       |
| 11.1.2.1  |  asynchromous point to point invocation (HTTP <-> RabbitMQ Queue <-> HTTP)                                                    |
| 11.1.2.2  |  asynchrously call many endpoints with same request message (HTTP <-> RabbitMQ Topic(Durable) <-> HTTP)                       |
| 11.1.2.3  |  asynchrously call many endpoints with same request message with different QOS (HTTP <-> RabbitMQ Topic(non-durable) <-> HTTP)|
