# 1.1.3 Converting SOAP message to JSON using Data Mapper Mediator

## When to use
The reconstruction of entire message payload is needed when required format of Client and Service are different. 
In this use case we can use [Data Mapper Mediator](https://docs.wso2.com/display/EI640/Data+Mapper+Mediator) to transform SOAP response to JSON.


## Sample use case

## Prerequisites
A REST client like cURL to invoke the ESB API.

## Development guidelines

## Deployment guidelines

* We can simply deploy by copying the CAR archive into <EI_HOME>/repository/deployment/server/carbonapps directory, and it will be deployed.

<p align="center"><b> OR </b></p>

* We can create the api in Management Console and deploy.

## Supported versions
This is supported in all the EI and ESB versions

## Test cases

| ID        | Summary                                                |
| ----------|:------------------------------------------------------:|
| 1.1.3.1   | Converting a valid SOAP message to JSON ( Happy Path ) |
| 1.1.3.2   | Handling malformed SOAP messages                       |
| 1.1.3.3   | Converting large sized SOAP message to JSON            |
