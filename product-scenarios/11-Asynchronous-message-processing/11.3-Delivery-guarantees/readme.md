# 11.3 Varying delivery guarantees to provide reliable delivery

## Business use case narrative

Systems can be unstable at times. They can go offline temporarily or stop message processing temporarily due to some external/
internal dependency. If synchronous invocation is used, we need to ensure high availability of every layer of the system. 
However, we can make the communication reliable by using a MOM layer (i.e Message Broker). It will store the messages until
they are successfully delivered to the subscribers. 

In this case, publishers do not need to worry if the backend system is offline. It does not even know which are the 
backend systems message would land to. Basically, the idea of re-publishing, re-trying on failures are moved off from 
the publishers. 

There are varying message reliability guarantees a message broker provides. With low guarantees usually the 
performance is better, so there is a trade off when selecting the required level of assurance. 

Following are basic levels of reliability provided by WSO2 EI when dealing with asynchronous messaging (when it is used
as a subscriber). When a message is acknowledged message broker which stores it will remove the message as the subscriber 
is sending a acknowledge that it got the message

 - acknowledge message as soon as WSO2 EI receives it. This does not worry if later processing is success or not. 
 - acknowledge message only if WSO2 EI could process the message without issues 
 - acknowledge message only if all parties in same transaction agrees 
 
 If a failure happens there are different layers to trigger retry. That is WSO2 EI will store the message and keep retrying 
 or it will receive the message from broker layer again and retry. You can control the number of times to retry, interval
 to retry etc as well. There are pattern like Dead Letter Channel come into play when configuring reliable delivery. 
 
 To make end-to-end delivery reliable message publishing to broker from WSO2 EI should also be reliable, making only
 subscriber side reliable it cannot be achieved. TO do that JMS transactions can be used at publisher side when using 
 JMS transport of WSO2 EI. 
    

 


## Persona
Developer 

## Sub-Scenarios

 - [Acknowledge on receive]
 - [Acknowledge if processing is successful]
 - [Transactions]
 - [Retry on  Fail scenarios]

  