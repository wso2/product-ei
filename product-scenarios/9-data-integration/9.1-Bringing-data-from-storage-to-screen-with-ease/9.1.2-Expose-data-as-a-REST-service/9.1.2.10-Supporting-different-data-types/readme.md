# 9.1.2.10 Supporting different data types

## Business use case narrative

In this scenario, supports different data types which can be defined for column data types as input and output 
parameters in the request.

## When to use
This approach can be used to assign different data types for input and output parameters.

## Sample use-case
In this sample user can assign different data types for input and output parameters.

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
[Mapping Data Types](https://docs.wso2.com/display/EI640/Mapping+Data+Types)

## Test cases

|      ID       | Summary |
| ------------- | ------------- |
| 9.1.2.10.1     | Array data type as an input parameter in XML |
| 9.1.2.10.2     | Array data type as an input parameter in JSON |
| 9.1.2.10.3     | Array data type as an output parameter in XML |
| 9.1.2.10.4     | Array data type as an output parameter in JSON |
| 9.1.2.10.5     | Binary data type as an input parameter in XML |
| 9.1.2.10.6     | Binary data type as an input parameter in JSON |
| 9.1.2.10.7     | Binary data type as an output parameter in XML |
| 9.1.2.10.8     | Binary data type as an output parameter in JSON |
| 9.1.2.10.9     | User defined data type as an input parameter in XML |
| 9.1.2.10.10    | User defined data type as an input parameter in JSON |
| 9.1.2.10.11    | User defined data type as an output parameter in XML |
| 9.1.2.10.12    | User defined data type as an output parameter in JSON |
| 9.1.2.10.13    | Timestamp data type as input parameter in XML |
| 9.1.2.10.14    | Timestamp data type as input parameter in JSON |
| 9.1.2.10.15    | JSON mapping with data types (ex: $int) |