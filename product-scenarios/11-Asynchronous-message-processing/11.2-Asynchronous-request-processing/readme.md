# 11.2 Asynchronous request processing

## Business use case narrative

There are systems that take a longer time to respond or process the given message. Sometimes, that system has its own 
internal asynchronous way to communicate with different sub-systems and process the message. In such cases, a synchronous
message like a HTTP invocation cannot be held until that system responds. In such a situation, message should be delegated 
to a third party (Message Oriented Middleware layer) and request should be treated as in-only. 

This is typically asynchronous request processing.

Even if the request is asynchronous there are scenarios where we want to know if the message is delivered to the intended
backend system by MOM layer. sometimes, we need to trigger another system when message processing is done (like sending a email).
WSO2 EI artifacts can be developed to support for both fire-and-forget scenarios and for the scenarios where we need a feedback. 

Asynchronous messaging enables other business use-cases like message rate controlling, batch processing. Those are also 
important for a middleware layer to ensure fault tolerance by to offload heavy processing to suitable time of the day where
traffic is lower and stopping backend systems get exhausted with sudden incoming message bursts.    

## Persona
Developer 

## Sub-Scenarios

 - [Request and forget]
 - [Request and trigger a operation when job completes (dual channel scenario)]
 - [Request and trigger a operation when job completes in a synchronous manner]
 - [Trigger a request asynchronously at a given time of the day/week/hour]
 - [Hop by hop message processing asynchronously]
 - [Matching message processing rates of 2 or more systems]

  