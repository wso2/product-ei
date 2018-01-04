# Order Management Scenario

## Scenario

This scenario is about a order management system. It consists of two parties. Those are a customer and an order management system(OMS).
In our sample we are hosting our OMS in  WSO2 Application Server. Customer would interact with OMS to place orders, view orders, make payments and view payments.
As the order management system we will be using a simple java web service which we will be hosting in WSO2 AS.
This is a SOAP service which is used to store all the order details in an in-memory table. To simulate customer operations, we will be using a simple HTTP client tool like curl.
Because our backend(OMS) is a SOAP based backend we will have to use some sort of Rest to SOAP converter. This will be done by Enterprise Integrator.

## Requirements

###Setting up servers
Download the Application server and extract the downloaded zip. Start the Application Server.
Download the OMS .aar file from this link. https://svn.wso2.org/repos/wso2/people/hiranya/rest-sample/bin/StarbucksOutletService.aar
Simply login to the AS management console at https://localhost:9443/carbon. Upload the downloaded .aar file to Services > Add > AAR Service.
It will take a few seconds for the service to get deployed properly.

## Building the scenario

## Run all the services related to sample

The services related to order-management-system could be executed in the following manner,

- Navigate to the directory <EI_HOME>/samples/order-management-system/services
- Execute the following,

samples/order-management-system/services$ ../../../bin/integrator.sh run samples/order/

or execute

bin$ ./integrator.sh ../samples/order-management-system/order.balx

What is done from above 2 commands are quite similar. So select only one and execute it. It will deploy all the services used in our scenario.

## Placing new orders

The back-end OMS provides an addOrder operation to handle this task.
So our ballerina service should ultimately invoke that operation to get the job done.
To build the order placement, we have used a ballerina HTTP service.

### How to run

If you are using Curl, put the following XML snippet into a file named order.xml

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

As you can see, a unique order id is generated for each order. This id can be used to get details about the order if you want to review it later.

## Reviewing Orders

Once an order has been placed in the system, the customer should be able to review it.

### How to run

To try this scenario out, find out the unique ID of the order you submitted earlier (You can get it from the payload you received as the response after placing the order.)
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

If you are using Curl, put the following XML snippet into a file named payment.xml

<?xml version="1.0" encoding="UTF-8"?>
<payment xmlns="https://starbucks.example.org">
  <cardNo>1234-5678-9010</cardNo>
  <expires>12/15</expires>
  <name>Peter Parker</name>
  <amount>10.80</amount>
</payment>

Get the unique-id of the order you placed. And run the following command after replacing order-id from the order you submitted earlier.
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

Get the unique-id of the order you paid for. And run the following command after replacing order-id.
Run the command 'curl -v http://localhost:9093/getPaymentDetails/payment/order/order-id'.

### Observations

Response received by the ballerina service will be as follows:

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
