# 9.1.1.8 Control viewing data based on the user role

## Business use case narrative

In this scenario,control access to sensitive data for specific user roles. It filters data where specific data sections 
are only accessible to a given type of users. 

## When to use
This approach can be used to avoid exposing sensitive data to anonymous users and control access to only authorized 
users. 

## Sample use-case
In this sample user can restrict the access of certain data to only authorized users

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
[Filtering Responsed by User Role](https://docs.wso2.com/display/EI640/Filtering+Responses+by+User+Role)

## Test cases

|      ID       | Summary |
| ------------- | ------------- |
| 9.1.1.8.1     | Set roles to which data should be exposed in output mapping |