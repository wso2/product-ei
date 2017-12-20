# Reliable Messaging

This sample demonstrates how a message could be delivered to a downstream service reliably.A given message
will be sent to a http endpoint which will accept the message and place it into a message queue in the 
broker. The placed message will be consumed from the queue and will be routed to the relevant message to downstream 
service.

At an event where the downstream service is unavailable, the message will be persisted in the queue until the 
service becomes available.

# Entities 

  * messaging - This balx contains two services. CoffeeShopService and CoffeeOrderProcessingService, 
  CoffeeShopService accepts an incoming http message and it will store the message in a queue in the broker.
  CoffeeOrderProcessingService would consume the stored message from the queue in the broker and will attempt to 
  deliver the message to a downstream service CoffeeOrderDispatchService.If the CoffeeOrderProcessingService 
  successfully delivers the message to CoffeeOrderDispatchService, the message will be polled from the queue. If the 
  delivery fails the service will notify the broker to resend the message. The CoffeeOrderProcessingService would ensure 
  that the message is successfully delivered to the specified downstream service. 
  
  * downstream - A service which will accept an incoming http message. This service will simply log the message.
  
  * broker - Message broker which will allow to store the message in a queue or a topic. The broker will ensure to 
  preserve the order and provide delivery guarantees. 
  
### Building the sample

In order to build the sample we would need the following,

- Service which will act as the downstream service CoffeeOrderDispatchService
[CoffeeOrderDispatchService](services/samples/downstream/CoffeeOrderDispatchService.bal)
- Service which will act as the CoffeeOrderProcessingService
[CoffeeOrderProcessingService](services/samples/messaging/CoffeeOrderProcessingService.bal)
- Service which will act as the CoffeeShopService
[CoffeeShopService](services/samples/messaging/CoffeeShopService.bal)

# Environment Setup

1. Start message broker by running the following command,

bin$ ./broker.sh

2. Deploy the downstream service by running the following command,

bin$ ./integrator.sh ../samples/reliable-messaging/downstream.balx

3. Deploy the reliable messaging service by running the following command,

bin$ ./integrator.sh ../samples/reliable-messaging/messaging.balx

#### Modifying the source

If it's intended to modify the existing samples following could be done,

1. As elaborated in the section "Building the sample", the relevant source could be found
2. Modify the source as expected 
3. Once modified in order to execute the scenario the following could be done

The services related to reliable-messaging could be executed in the following manner,

- Navigate to the directory <EI_HOME>/samples/reliable-messaging/services
- Execute the following,

samples/reliable-messaging/services$ ../../../bin/integrator.sh run samples/downstream
samples/reliable-messaging/services$ ../../../bin/integrator.sh run samples/messaging

The above operation will be similar to running downstream.balx and messaging.balx   

## Invoking the service

Create a payload.json file with the following content.
```
{
   "Order":{
      "ID":"797976"
   },
   "details":{
      "type":"Cappuccino",
      "addons":"Heavy Milk"
   }
}
```
Invoke the service using cURL as follows
```
curl -v http://localhost:9090/coffee/order -d@payload.json -H"Content-Type: application/json"

```

## Observations 

Once the above command is executed immediately the following response would be visible to the caller

```
{"Status":"Coffee Order Processed"}
```

Also a log will be indicated in both downstream and messaging services. 

## Alternative routes 

1. Shutdown the downstream service 
2. Invoke the messaging service by executing the command described above in "Invoking the service section"
3. Notice a log shown as the following being logged in messaging service 

```
Payload: {"Order":{"ID":"797976"},"details":{"type":"Cappuccino","addons":"Heavy Milk"}} received by processing service
Error occurred while dispatching the message, hence message will be retried
```

4. Now restart the downstream service 
5. Once the service is restarted the message will be delivered to the service. This would be observable through the 
log indicated in the downstream service.
 