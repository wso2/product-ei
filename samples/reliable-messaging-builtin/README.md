# Reliable Messaging with Reliable Delivery Connector

This sample demonstrates how a message could be delivered to a downstream service reliably using Ballerina/EI Reliable Delivery connector. We have to provide a persistence message storage provider (eg: a message broker) and use reliable http client connectors when invoking the backend service.  

For the moment this sample will use ActiveMQ Broker as the message persistence service. It will be usable with EI embedded broker when the required features become available in the broker.


# Entities 

  * messaging - A service which will accept the incoming message and handover to the reliable http client. This service will also start a message processor instance which will be needed to handle the reliable delivery task.
  
  * downstream - A service which will accept an incoming http message. This service will simply log the message.
  
  * broker - Message broker which will be used to make messages persistent until forwards to the endpoint.
  
### Building the sample

In order to build the sample we would need the following,

- Service which will act as the downstream service CoffeeOrderDispatchService
[CoffeeOrderDispatchService](services/samples/downstream/CoffeeOrderDispatchService.bal)
- Service which will act as the CoffeeShopService
[CoffeeShopService](services/samples/messaging/CoffeeShopService.bal)

# Environment Setup

1. Start ActiveMQ broker locally with default port (61616).

2. Copy ActiveMQ [libs](http://activemq.apache.org/what-jars-do-i-need.html) into _<EI_HOME>/wso2/ballerina/bre/lib_ folder.

3. Deploy the downstream service by running the following command,

bin$ ./integrator.sh ../samples/reliable-messaging-builtin/downstream.balx

4. Deploy the reliable messaging service by running the following command,

bin$ ./integrator.sh ../samples/reliable-messaging-builtin/messaging.balx

#### Modifying the source

If it's intended to modify the existing samples following could be done,

1. As elaborated in the section "Building the sample", the relevant source could be found
2. Modify the source as expected 
3. Once modified in order to execute the scenario the following could be done

The services related to reliable-messaging could be executed in the following manner,

- Navigate to the directory <EI_HOME>/samples/reliable-messaging-builtin/services
- Execute the following,

samples/reliable-messaging-builtin/services$ ../../../bin/integrator.sh run samples/downstream

samples/reliable-messaging-builtin/services$ ../../../bin/integrator.sh run samples/messaging

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

Also a log will be indicated in downstream service. 

## Alternative routes 

1. Shutdown the downstream service 
2. Invoke the messaging service by executing the command described above in "Invoking the service section"
3. Now restart the downstream service 
4. Once the downstream service is restarted the message will be delivered to the service. This would be observable through the 
log indicated in the downstream service.
 