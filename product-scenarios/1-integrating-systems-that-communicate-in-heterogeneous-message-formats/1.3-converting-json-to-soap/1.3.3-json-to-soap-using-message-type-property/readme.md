
# 1.3.3 Converting JSON message to SOAP using MessageType property

## When to use
The reconstruction of entire message payload is needed when required format of Client and Service are different. 
In this use case we can expose a SOAP service over JSON by switching between JSON and XML/SOAP message formats 
using the [Messagetype Property Mediator](https://docs.wso2.com/display/EI640/Property+Mediator).


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

| ID        | Summary                                                  |
| ----------|:--------------------------------------------------------:|
| 1.3.3.1   | Converting a valid JSON message to SOAP ( Happy Path )   |
| 1.3.3.2   | Transformation of a JSON message with special characters |
| 1.3.3.3   | Handling malformed JSON messages                         |
