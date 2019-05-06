# 9.1.2.9 Passing default values for parameters where values are not defined in the request

## Business use case narrative

In this scenario, automatically assign a value to a parameter when a user has not entered a specific parameter value in
a request. It also should possible to assign Internal Property Values as the default value.

## When to use
This approach can be used to automatically assign a default value in a case that the user has not assigned a value for 
that parameter in the request.

## Sample use-case
In this sample user can execute an operation without passing a value for an input parameter, but the defined default 
value is passed to the datasource in the query.

### Prerequisites

### How to try-out sample use-case

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
[Writing Queries](https://docs.wso2.com/display/EI640/Writing+Queries)

## Test cases

|      ID       | Summary |
| ------------- | ------------- |
| 9.1.2.9.1     | Set username as the default value |
| 9.1.2.9.2     | Set tenant ID as the default value |
| 9.1.2.9.3     | Set user role as the default value |
| 9.1.2.9.4     | Set null as the default value in XML |
| 9.1.2.9.5     | Set null as the default value in JSON |
| 9.1.2.9.6     | Set a value as the default value |