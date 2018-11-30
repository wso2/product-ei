# 9.1.2.6 Nested output read

## Business use case narrative

In this scenario, it allows you to use the result of one query as an input parameter of another, and the queries 
executed in a nested query works in a transactional manner.

## When to use
This approach can be used to retrieve data from different tables from a single request in nested manner. 

## Sample use-case
In this sample user can retrieve data from different tables from a single operation.

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
[Defining Nested Queries](https://docs.wso2.com/display/EI640/Defining+Nested+Queries)

## Test cases

|      ID       | Summary |
| ------------- | ------------- |
| 9.1.2.6.1     | Retrieving data using nested queries in XML |
| 9.1.2.6.2     | Retrieving data using nested queries in JSON by defining call query in the JSON mapping |