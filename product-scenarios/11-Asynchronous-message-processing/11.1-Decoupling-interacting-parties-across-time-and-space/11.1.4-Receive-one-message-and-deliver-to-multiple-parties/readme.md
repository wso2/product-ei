# 11.1.4 Receive one message and deliver to multiple parties


## When to use

Publish and subscribe pattern is very common in asynchronous messaging. It basically enables user to publish one message to \
middleware layer and same copy of the message will be distributed to multiple subscribers. 

There are two basic patterns to be considered here. 

 - Subscribers are non durable : subscribers will receive messages during the time they are active on messaging provider (a message broker)
 - Subscribers are durable : subscribers will register to messaging provider first and then receive messages. Even if subscriber
                             goes offline, broker will keep that subscribers copy. In this way subscribers can receive messages independently
                             with different delivery grantees.    

## Sample use case

Invoke multiple endpoint in asynchronous way with the same message. System should be able to register a new subscriber
without any changes to message publish side. 

## Prerequisites

 - A Message Broker 
 - A SOAP backend
 - A HTTP Client 


## Development guidelines

 - Use EI tooling to develop EI artifacts 
 - Use broker's console to define topics


## Deployment guidelines

Deploy broker in a container

## Supported versions

This is supported in all the EI and ESB versions

## Test cases

| ID        | Summary                                                |
| ----------|:------------------------------------------------------:|
| 11.1.1.1  |  |
| 11.1.1    |  |
| 11.1.1    |  |
