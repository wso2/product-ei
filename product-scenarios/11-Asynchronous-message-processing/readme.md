# 11. Asynchronous message processing

## Business use case narrative

Asynchronous messaging is a communication method wherein the system puts a message in a message queue and does not
require an immediate response to continue processing. Below is a few places to use aynchorouns messaging. 

  - delegate the request to some external system for processing
  - to ensure delivery of a message to an external system 
  - throttle message rates between two systems 
  - batch processing of messages 

There is a few aspects for asynchronous message processing. 

 - Asynchronous messaging solves the problem of intermittent connectivity. The message receiving party does not need to be online 
as the message is stored in a middle layer and it can receive it when it comes online. 

 - Message consumers does not need to know about the message publishers and they can operate independently to each other. 
  
 - Disadvantages of asynchronous messaging include the additional component of a message broker or transfer agent to ensure
the message is received. This may affect both performance and reliability.

 - There are various levels of message delivery reliability grantees from publisher to broker and from broker to subscriber. 
   Wire level protocols like AMQP and MQTT can provide those.  



## Persona
Developer 

## Sub-Scenarios

 - [11.1 Decoupling interacting parties across time and space]
 - [11.2 Asynchronous request processing in case of long running operations]
 - [11.3 Varying delivery guarantees to provide reliable delivery]
 - [11.4 Connecting to broker]
 
 