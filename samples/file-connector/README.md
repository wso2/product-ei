# File System Connector Sample

This sample demonstrates the File System Server Connector's capability to listen to a directory in the local file system.

## Scenario

The scenario is about an Order Processing system.

The Order Processing Server accepts orders over HTTP protocol and the messages should be in json format.
A legacy system that submits orders produces a batch of orders in a csv file.
Whenever a new csv file available, there should be a way (a service/system) to read it and submit orders over
HTTP protocol to the Order Processing service.


## Building the scenario

Following are the two services in this scenario

OrderProcessorService: a mock service written in ballerina to act as the Order Processing Service.

FileProcessorService: a service written in ballerina which listen to the file system, pick csv files, transform
and submit orders to the OrderProcessorService.

## Test the scenario

### How to run the sample

bin$ ./integrator.sh ../samples/file-connector/file-connector-sample.balx

### Invoking the service

Copy orders.csv file resides in samples/file-connector/resources into samples/file-connector/resources/in directory

After some time you will notice following logs get printed in the console.
```
Processing order : 210001
{"OrderId":"210001","Status":"Success"}
Processing order : 210002
{"OrderId":"210002","Status":"Success"}
Processing order : 210003
{"OrderId":"210003","Status":"Failed"}
```


### Modifying the code

All the services related to this sample is located at <EI_HOME>/samples/file-connector/services directory.

Following are the available ballerina program files.

[OrderProcessorService.bal](services/samples/order/OrderProcessorService.bal)

OrderProcessorService is just a dummy service which mock the functionality of a backend service.

[FileProcessorService.bal](services/samples/order/FileProcessorService.bal) is the main service which we focus on this sample.

Please note that these files are located at samples/order sub-directory inside the service directory
as all these services belongs to the samples.order package.

We can simply edit the configurations as we prefer and execute the sample again with modified
content with the following command.

```
samples/file-connector/services$ ../../../bin/integrator.sh samples/order
```

We can test the sample by executing the same set of steps given above.