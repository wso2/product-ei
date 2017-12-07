# Message Routing Gateway Sample

## Scenario

The scenario is about an eCommerce API router.
An eCommerce system consists of several microservices which provides a specific functionality.
Those services are not exposed externally.
To expose those functionality to the outside it is required to create a routing service which acts as a gateway.
So, the external applications can consume them through the Gateway service.

Following are the available microservices.

Browse Service:
browse available items

Order Service:
place an order

Payment Service:
make the payment

Shipment Service:
submit shipment details


Following are some of the key requirement of the gateway.

## Routing based on url
Gateway service provides a single API to the outside which can be consumed by external applications.
 So it need to route the incoming requests into appropriate microservice based on the url.

## Routing based on headers
Gateway service is accessed by internal systems in the organization too. Those systems need special access to those microservices.
‘User-Agent’ header present in the request can be used to identify the internal system.
Here shipment requests needed to be routered to appropriate resource based on the value of the ‘User-Agent’ header.

## Manipulating headers
Since Gateway acts as an intermediate hop, it is required to add the X-Forwarded-For header to the request.


## Content-based routing
There are multiple payment services exists which handles different type of payment methods.
Payment type can be determined using the “PaymentType” element of the payment request.
Payment request need to be routed to the correct service based on the content of payment request message.


## How to run the sample

bin$ ./integrator.sh ../samples/message-router/router.balx


## Executing Sample

### Browse Electronics items

Invoke the service using cURL as follows

```
curl -v http://localhost:9090/ecom/browse/Electronics
```

You should get something similar to following as the output.

```
{
   "Phones":[
      {
         "model":"S7 Edge",
         "brand":"Samsung"
      },
      {
         "model":"S8 Edge",
         "brand":"Samsung"
      },
      {
         "model":"IphoneX",
         "brand":"Apple"
      }
   ],
   "Televisions":[
      {
         "model":"LF530 Smart",
         "brand":"LG"
      },
      {
         "model":"M5000",
         "brand":"Samsung"
      }
   ]
}
```


### Submitting a order


Create a order.json file with the following content.
```
{
   "order":[
      {
         "category":"Electronics",
         "item":{
            "subcategory":"Phones",
            "model":"S7 Edge",
            "brand":"Samsung"
         }
      }
   ]
}
```
Invoke the service using cURL as follows
```
curl -v http://localhost:9090/ecom/order/placeOrder -d@order.json
```

You should get something similar to following as the output.

```
{"Status":"Order placed successfully","OrderId":379}
```


### Make the payment


Create a payment.json file with the following content.
```
{
   "creditCardType":"VISA",
   "card":{
      "no":"1234098618781768",
      "cvv":"123"
   }
}
```

Invoke the service using cURL as follows
```
curl -v http://localhost:9090/ecom/payment/371 -d@payment.json
```

You should get something similar to following as the output.
```
{"Status":"Transaction made through your VISA card is successful for order : 371"}
```

### Submit for shipment

Create a shipment.json file with the following content.

```
{
   "fullName": "John Martin",
   "shipmentType":"Normal",
   "address":{
      "street":"787 Castro st",
      "city":"mountain view",
      "state":"CA",
      "zip":"94041-2013"
   }
}
```


Invoke the service using cURL as follows
```
curl -v http://localhost:9090/ecom/shipment/371 -d@../resources/shipment.json
```


You should get something similar to following as the output.
```
{"Status":"Shipment details submitted","Order ID":"371"}
```

Invoke as the internal system specifying the ‘User-Agent’ header
```
curl -v http://localhost:9090/ecom/shipment/371 -d@../resources/shipment.json -H "User-Agent: Ecom-Agent"
```

You will see following in the log
```
Submitting internal shipment details to order id: 371
```


### Manipulating Headers

For every request you will notice following is getting printed in the log.
Please refer to the setXFwdForHeader() function in the RoutingService.bal for more information about manipulating headers.

```
Setting X-Forwarded-For to : 10.100.1.127
```
