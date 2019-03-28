# 1.5.1 Converting Other message formats to CSV using Data Mapper Mediator


## When to use
The reconstruction of entire message payload is needed when required format of Client and Service are different. 
In this use case we can switch between CSV and other message formats such as XML, JSON and other CSV formats 
using the [Data Mapper Mediator](https://docs.wso2.com/display/EI640/Data+Mapper+Mediator)


## Sample use case

## Prerequisites
A REST client like cURL to invoke the ESB API.

## Development guidelines

## Deployment guidelines

* We can simply deploy by copying the CAR archive into <EI_HOME>/repository/deployment/server/carbonapps directory, and it will be deployed.

<p align="center"><b> OR </b></p>

* We can create the api in Management Console and deploy.

## REST API


## Supported versions

This is supported in all the EI and ESB versions

## Test cases

| ID        | Summary                             |
| ----------|:-----------------------------------:|
| 1.5.1.1   | Converting XML to CSV               |
| 1.5.1.2   | Converting JSON to CSV              |
