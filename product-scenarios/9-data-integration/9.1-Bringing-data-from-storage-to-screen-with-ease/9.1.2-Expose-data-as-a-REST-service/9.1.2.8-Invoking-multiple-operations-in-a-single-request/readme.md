# 9.1.2.8 Invoking multiple operations in a single request

## Business use case narrative

In this scenario, allows you to invoke multiple operations (consecutively) to a datasource using a single operation.
In one of the operation failed, all the individual operations are rolled back.

## When to use
This approach can be used to execute multiple operations (consecutively) to a datasource using a single operation in a 
transactional manner.

## Sample use-case
In this sample user can execute multiple operations (consecutively) to a datasource using a single operation.

## Supported versions
This is supported in all the EI and DSS versions

## Pre-requisites
None

## REST API (if available)
N/A

## Deployment guidelines
Standard way of deploying a data service is by packaging the data service as a Carbon Application. Please refer 
[Managing Data Integration Artifacts via Tooling](https://docs.wso2.com/display/EI640/Managing+Data+Integration+Artifacts+via+Tooling) for instructions.

## Reference
[Invoking Multiple Operations via Request Box](https://docs.wso2.com/display/EI640/Invoking+Multiple+Operations+via+Request+Box)

## Test cases

|      ID       | Summary |
| ------------- | ------------- |
| 9.1.2.8.1     | Invoke create operation and a select operation in a single XML request using request box |
| 9.1.2.8.2     | Invoke create operation and a select operation in a single JSON request using request box |