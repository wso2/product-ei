# Order Management Scenario

## Scenario

This scenario is about a order management system. It consists of two parties. Those are a customer and an order management system(OMS).
In this sample an OMS will be hosted in  WSO2 Application Server. Customer would interact with OMS to place orders, view orders, make payments and view payments.
As the order management system a simple java web service hosted in WSO2 AS is used.
This is a SOAP service which is used to store all the order details in an in-memory table. To simulate customer operations, a simple HTTP client tool like curl is used.
Because our backend(OMS) is a SOAP based backend, some sort of Rest to SOAP converter should be used. This is done by Enterprise Integrator.

## Requirements

###Setting up servers
Download the Application server and extract the downloaded zip. Start the Application Server.
Download the OMS .aar file from this link. https://svn.wso2.org/repos/wso2/people/hiranya/rest-sample/bin/StarbucksOutletService.aar
Login to the AS management console at https://localhost:9443/carbon. Upload the downloaded .aar file to Services > Add > AAR Service.
After a few seconds the service will get deployed properly.

## Building the scenario

## Run all the services related to sample

The services related to order-management-system could be executed in the following manner,

- Navigate to the directory <EI_HOME>/samples/order-management-system/services
- Execute the following,

samples/order-management-system/services$ ../../../bin/integrator.sh run samples/order/

or execute

bin$ ./integrator.sh ../samples/order-management-system/order.balx

What is done from above 2 commands are quite similar. Select only one and execute it. It will deploy all the services used in our scenario.

## Placing new orders

The back-end OMS provides an addOrder operation to handle this task.
Ballerina service should ultimately invoke that operation to get the job done.
To build the order placement, a ballerina HTTP service is used.

### How to run

If Curl is used as the client tool, put the following XML snippet into a file named order.xml

<?xml version="1.0" encoding="UTF-8"?>
<order xmlns="https://starbucks.example.org">
  <drink>Caffe Misto</drink>
  <additions>Milk</additions>
</order>

Run the command 'curl -v -d @order.xml -H "Content-type: application/xml" http://localhost:9090/placeOrder/order' from the place where order.xml is saved.

### Observations

Response received:

< HTTP/1.1 201 Created
< Content-Type: application/xml
< Content-Length: 455
<
<order xmlns="http://starbucks.example.org" xmlns:m0="http://ws.starbucks.com" xmlns:ax2438="http://ws.starbucks.com/xsd">
                  <orderId>8ce7aa39-edad-4962-b1be-c72cbd4fcd28</orderId>
                  <drink>Caffe Misto</drink>
                  <cost>10.8</cost>
                  <additions>Milk</additions>
                </order>

A unique order id is generated for each order. This id can be used to get details about the order if it is required to review the order later.

## Reviewing Orders

Once an order has been placed in the system, the customer should be able to review it.

### How to run

To try this scenario out, find out the unique ID of the order submitted earlier (It can be taken from the payload received as the response, after placing the order.)
Run Curl as follows (replace my-order-id with the actual order ID)

'curl -v http://localhost:9091/getOrderInfo/order/my-order-id'.

#### Observations

Response received:

< HTTP/1.1 200 OK
< Content-Type: application/xml
< Content-Length: 347
<
<order xmlns="http://starbucks.example.org" xmlns:m0="http://ws.starbucks.com" xmlns:ax2438="http://ws.starbucks.com/xsd">
                <drink>Caffe Misto</drink>
                <cost>10.8</cost>
                <additions>Milk</additions>
               </order>

## Making Payments

The customer can create a payment resource by sending a PUT request.
The payload of the request should contain all the required payment information such as amount and credit card details.

### How to run

Put the following XML snippet into a file named payment.xml

<?xml version="1.0" encoding="UTF-8"?>
<payment xmlns="https://starbucks.example.org">
  <cardNo>1234-5678-9010</cardNo>
  <expires>12/15</expires>
  <name>Peter Parker</name>
  <amount>10.80</amount>
</payment>

Get the unique-id of the placed order. And run the following command after replacing order-id from the order submitted earlier.
And make sure to add an amount which is no less than the cost of the order.
Run the command 'curl -v -X PUT -d @payment.xml -H "Content-type: application/xml" http://localhost:9092/payOrder/payment/order-id' from the place where order.xml is saved.

### Observations

Response received:

< HTTP/1.1 201 Created
< Content-Type: application/xml
< Content-Length: 523
<
<payment xmlns="https://starbucks.example.org/" xmlns:m0="http://ws.starbucks.com" xmlns:ax2438="http://ws.starbucks.com/xsd">
                  <cardNo></cardNo>
                  <expires>12/15</expires>
                  <name>Peter Parker</name>
                  <amount>10.8</amount>
                  <orderId>8ce7aa39-edad-4962-b1be-c72cbd4fcd28</orderId>
                 </payment>

## Reviewing Payments

Once a payment has been made, the customer can review the payment details by making a GET request.

### How to run

Get the unique-id of the paid order. And run the following command after replacing order-id.
Run the command 'curl -v http://localhost:9093/getPaymentDetails/payment/order/order-id'.

### Observations

Response received:

< HTTP/1.1 200 OK
< Content-Type: application/xml
< Content-Length: 533
<
<payment xmlns="https://starbucks.example.org/" xmlns:m0="http://ws.starbucks.com" xmlns:ax2438="http://ws.starbucks.com/xsd">
                  <cardNo>1234-5678-9010</cardNo>
                  <expires>12/15</expires>
                  <name>Peter Parker</name>
                  <amount>10.8</amount>
                  <orderId>8ce7aa39-edad-4962-b1be-c72cbd4fcd28</orderId>
                </payment>
