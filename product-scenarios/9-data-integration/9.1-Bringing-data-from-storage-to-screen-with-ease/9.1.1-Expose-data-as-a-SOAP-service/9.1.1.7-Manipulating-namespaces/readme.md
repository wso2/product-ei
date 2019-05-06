# 9.1.1.7 Manipulating namespaces

## Business use case narrative

In this scenario, to avoid conflicting element names, namespaces can be used to uniquely identify the elements in the 
XML. Using namespaces for a data service is optional. However, in certain data services, namespaces can be used to 
avoid ambiguity. 

## When to use
This approach can be used to uniquely identify the XML elements in the data service. Namespaces can be defined at 
different levels of a data service, which means that you can have multiple namespaces applicable to your data service at
the same time. In such a scenario, the top-level namespaces will be inherited by the lower level. 

## Sample use-case
In this sample user can define same element name in multiple places by having different namespaces to avoid ambiguity.

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
[Using Namespaces](https://docs.wso2.com/display/EI640/Using+Namespaces)

## Test cases

|      ID       | Summary |
| ------------- | ------------- |
| 9.1.1.7.1     | Setting the namespace for a data service and check the namsepace in the response for a select query |
| 9.1.1.7.2     | Setting the namespace for a data service select query (row namespace) and check the namsepace in the response |
| 9.1.1.7.3     | Setting namespaces for specific elements in a select query result and check the namsepace in the response |
| 9.1.1.7.4     | Setting namespaces for complex results in a select query and check the namsepace in the response |