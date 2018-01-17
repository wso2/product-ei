# Order management with data composition
This is an extended version of the order management system of a restaurant where instead of making external calls for order management the database calls are used for the purpose.

## Scenario

The following scenarios are covered:
* **placeOrder**: Using this method you can place an order in the restaurant.
* **getOrderDetails**: Using this method it is possible to get the details of a single order.
* **makePayment**: Using this method an order can be paid for.
* **checkPaymentStatus**: Using this method the payment status of an order can be checked.
* **cancelOrder**: An order can be cancelled before making payments using this method.
* **completeOrder**:  An order can be completed after making payments.
* **listCompletedOrders**: The completed orders can be listed using this method.
* **listPendingOrders**: The pending orders can be listed using this method.
* **deleteCompletedOrder**: This method facilitates deleting the completed orders.

## Prerequisites

This sample uses MySQL DB. Before running the sample copy the MySQL JDBC driver to following folder
 
    <EI_HOME>/wso2/ballerina/bre/lib
 
Change the DB connection parameters as required.

Use the order-management.sql file located at resources directory to create required database schema, tables and data.

## How to run the sample
   
bin$ ./integrator.sh ../samples/order-management-system-builtin/order-management-system-builtin.balx

## Invoking the service
1. Using the `order.json` file in the resources folder invoke place order method:
        
        curl -v -d@order.json -H"Content-Type: application/json" http://localhost:9090/order/place

A response such as follows will be returned:
        
        {"orderid":1,"totalprice":290,"orderstatus":"Pending","paymentStatus":"Not paid"}
        
Invoke the placeOrder method again to create another order.

2. To get the details of an order use the following

        curl -v http://localhost:9090/order/get/1
        
A response similar to the following shall be returned:
    
    {"orderid":1,"totalprice":290,"orderstatus":"Pending","paymentStatus":"Not paid","orderItems":[{"drinkName":"orange","additions":"extra sugar","cost":150},{"drinkName":"lime","additions":"no sugar","cost":140}]}
    
3. Use the `payment.json` file in the resources folder to make a payment:

        curl -v -d@payment.json -H"Content-Type: application/json" http://localhost:9090/order/pay

Make sure to change the `orderid` as necessary in the payment request.

The following shall be returned:
    
    "Payment succesful for Order 1"
    
4. Check payment status as follows:

        curl -v http://localhost:9090/order/payStatus/1

Get a result as follows:
    
    {"paymentStatus":"Paid"}

5. Cancel the unpaid second order:

        curl -v http://localhost:9090/order/cancel/2
        
The result is:

    "The order 2 does not exist"
    
6. Complete the first order:

         curl -v http://localhost:9090/order/complete/1
         
 Returned result is:
    
    {"orderid":1,"totalprice":290,"orderstatus":"Completed","paymentStatus":"Paid"}
    
7. List all pending orders:

        curl -v http://localhost:9090/order/listPending

8. List completed orders
        curl -v http://localhost:9090/order/listCompleted    
        
which returns
       
     [{"orderid":1,"totalprice":290,"orderstatus":"Completed","paymentStatus":"Paid"}]

9. Delete completed orders:

        curl -X DELETE http://localhost:9090/order/deleteCompleted

which returns     

     {"Deleted":[{"orderid":1,"totalprice":290,"orderstatus":"Completed","paymentStatus":"Paid"}]}
     
     
## Modifying the source

If it's intended to modify the existing samples following could be done,

The `OrderService` service has all the methods and it could be modified as required.

Once modified navigate to the directory `<EI_HOME>/samples/order-management-system-builtin/services`

Execute the following,

    samples/order-management-system-builtin/services$ ../../../bin/integrator.sh samples/order                          