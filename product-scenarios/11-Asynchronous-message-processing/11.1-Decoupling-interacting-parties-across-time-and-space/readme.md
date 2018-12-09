# 11. Decoupling interacting parties across time and space

## Business use case narrative

 One of the main use-cases of asynchronous messaging is the ability to deliver message to the consumer when it becomes 
 available. If we take HTTP protocol both client and server should be online for successful message transfer. However, 
 there are situations where subscriber can go offline at times. Publisher should be able to publish the message without
 any knowledge whether the subscriber is live or not. 
 
 Also in a typical synchronous communication like HTTP, client should know about the server, the url or the place service
 can be invoked/accessed. Advantage of asynchronous messaging is, publisher does not need to know about the subscribers, 
 who they are and where they can be accessed. It just need to know about the Messaging Provider like a JMS broker. 
 
 There is a few ways in EI to make an asynchronous call using message brokers, eventing mechanisms and databases. In this section 
 the approaches for asynchronous communication provided by EI server and how to manage listening for messages/retrying to receive
 same message after message processing has failed are discussed.     



## Persona
Developer 

## Sub-Scenarios

 - [Decouple interacting parties using JMS queues/topics]
 - [Decouple interacting parties using AMQP]
 - [Decouple interacting parties using Kafka]
 - [Receive one message and deliver to multiple parties]
 - [Receive messages conditionally and process]
 - [Scaling up and down number of message consumers according to consume rate (performance)]
 - [Receive and process messages one after the other]
 - [Speed up message receival from a given queue/topic]
 - [Retry polling for JMS messages on failure]
 - [Stop polling messages in runtime]
  