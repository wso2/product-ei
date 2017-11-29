# File System Connector Sample

This sample demontrates the File System Server Connnector's capability to listen to a directory in the local file system.

The scenario is about an Order Processing system.

The Order Processing Server accpets orders over HTTP protocol and the essages should be in json format.
A legacy system which submits orders produces a batch of orders in a csv file.
Whenever a new csv file available, there should be a way (a service/system) to read it and submit orders over
HTTP protocol to the Order Processing service.

Following are the two services in this scenario

OrderProcessorService: a mock service written in ballerina to act as the Order Processing Service.

FileProcessorService: a service written in ballerina which listen to the file system, pick csv files, transform 
and submit orders to the OrderProcessorService.

## How to run the sample

bin$ ./integrator.sh ../samples/file-connector/file-connector-sample.balx

## Invoking the service

Copy orders.csv file resides in samples/file-connector/resiurces into samples/file-connector/resiurces/in directory

After some time you will notice following logs get printed in the console.
```
Processing order : 210001
{"OrderId":"210001","Status":"Success"}
Processing order : 210002
{"OrderId":"210002","Status":"Success"}
Processing order : 210003
{"OrderId":"210003","Status":"Failed"}
```
