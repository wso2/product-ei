# JMS Synchronous Invocations - Dual Channel

This sample demonstrates how to implement request - response scenarios with JMS. 

Downstream service is a backend which consumes and produces JMS Messages. It will be connected to the pointed message broker and consume messages from one queue, after processing a processing a request it will push its response to another queue.

Frontend service is exposed as to handle http requests and deals with the backend. It should push messages into the request-queue and wait for the backend to put response to the response-queue.

After receiving a successful response from the backend, frontend will respond back to the initial http request.

If the backend fails to respond back within the time interval, the frontend service will notify the client accordingly.

# Entities 

  * reception - This balx contains the http service [CoffeeShopOrderPlacingService](services/samples/reception/CoffeeShopOrderPlacingService.bal) which accepts customer requests.
  After reception of a client request, it will publish the message into request queue (with a new Correlation ID) of the broker.
  Then it will get blocked and wait for the configured time interval until the response arrives at the response queue.  
  Upon a successful reception of the response, it will get forwarded to the client. If timeout occurs it will notify the http client. 
  
  * downstream - This balx contains the jms service [CoffeeShopOrderProcessingService](services/samples/downstream/CoffeeShopOrderProcessingService.bal) 
  This service will listens to a jms queue to consumes requests. After a reception of a request, it will process the request, creates the response and publish it to the provided response destination (queue) with Correlation ID 
  
  * broker - Message broker which will allow to store requests and responses in separate queues.
  
### Building the sample

In order to build the sample we would need the following,

- Service which will act as the downstream service CoffeeShopOrderProcessingService
[CoffeeShopOrderProcessingService.bal](services/samples/downstream/CoffeeShopOrderProcessingService.bal)
- Service which will act as the CoffeeShopOrderPlacingService
[CoffeeShopOrderPlacingService](services/samples/reception/CoffeeShopOrderPlacingService.bal)

# Environment Setup

1. Start message broker by running the following command,

bin$ ./broker.sh

2. Deploy the downstream service by running the following command,

bin$ ./integrator.sh ../samples/jms-synchronous-invocation/downstream.balx

3. Deploy the http client request reception service by running the following command,

bin$ ./integrator.sh ../samples/jms-synchronous-invocation/reception.balx

#### Modifying the source

If it's intended to modify the existing samples following could be done,

1. As elaborated in the section "Building the sample", the relevant source could be found
2. Modify the source as expected 
3. Once modified in order to execute the scenario the following could be done

The services related to jms-synchronous-invocation could be executed in the following manner,

- Navigate to the directory <EI_HOME>/samples/jms-synchronous-invocation/services
- Execute the following,

samples/jms-synchronous-invocation/services$ ../../../bin/integrator.sh run samples/downstream

samples/jms-synchronous-invocation/services$ ../../../bin/integrator.sh run samples/reception

The above operation will be similar to running downstream.balx and reception.balx   

## Invoking the service

Create a payload.json file with the following content.
```
{
    "CustomerInfo": {
        "CustomerName":"Alex"
    },
    "OrderInfo" : {
        "CoffeeType" : "latte"
    }
}
```
Invoke the service using cURL as follows
```
curl -v http://localhost:9090/coffeeshop/order -d@payload.json -H"Content-Type: application/json"

```

## Observations 

After the above command is executed the following log will be visible at the downstream
```
Coffee service backend processing the request: {"CustomerInfo":{"CustomerName":"Alex"},"OrderInfo":{"CoffeeType":"latte"}}
```

cURL client will receive a response as follows,
```
{
    "OrderID": "60ce0c4415b3428a843d3fe108f339e9",
    "Price": "$10.50",
    "OrderReady": true,
    "CustomerInfo": {
        "CustomerName": "Alex"
    },
    "OrderInfo": {
        "CoffeeType": "latte"
    }
}
```

If the backend fails to respond within configured time interval, following response will receive to the client
```
{
    "CutomerInfo": {
        "CustomerName": "Alex"
    },
    "OrderInfo": {
        "CoffeeType": "latte"
    },
    "Error": "Coffee order has been decliend"
}
```
