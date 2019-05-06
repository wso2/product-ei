# 11.1.1.1 Make a HTTP call asynchronous using JMS broker 

## When to use

Sometime we find HTTP services are not reliable and not always active. Also we might want to ensure the message
is delivered to the service and it is processed. In such situations we can use JMS broker (eg: ActiveMQ/WSO2 MB)as
messaging provider and achieve the requirement. 


## Sample use case

EI will receive a HTTP message (a SOAP message), and place it in a Active MQ queue. Then EI will poll messages of 
that queue and get the message and invoke the backend service. The reliability is handled by different Acknowledgement 
methods. 

## Prerequisites

Active MQ broker deployment with JMS support
SOAP client 
HTTP backend service accepting SOAP messages and reply with  SOAP messages


## Development guidelines

Use EI tooling to develop EI artifacts 
Use Management Console of ActiveMQ to setup queues/topics 



## Deployment guidelines

Deploy ActiveMQ broker in a docker container 



## Supported versions

This is supported in all the EI and ESB versions

## Test cases

| ID        | Summary                                                                                                                       |
| ----------|:----------------------------------------------------------------------------------------------------------------------------- :                                                                       |
| 11.1.1.1  |  asynchromous point to point invocation (HTTP <-> ActiveMQ Queue <-> HTTP)                                                    |
| 11.1.1.2  |  asynchrously call many endpoints with same request message (HTTP <-> ActiveMQ Topic(Durable) <-> HTTP)                       |
| 11.1.1.3  |  asynchrously call many endpoints with same request message with different QOS (HTTP <-> ActiveMQ Topic(non-durable) <-> HTTP)|
