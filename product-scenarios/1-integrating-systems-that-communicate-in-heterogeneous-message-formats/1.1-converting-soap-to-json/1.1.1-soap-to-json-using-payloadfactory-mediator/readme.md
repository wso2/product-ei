# 1.1.1 Converting SOAP message to JSON using PayloadFactory mediator


## When to use
The most common message transformation use case is reconstructing the entire message payload according to the required 
format of the service/client. We can use the PayloadFactory Mediator to do the transformation in this use case, 
if we know that the structure of the new payload is simple, which means the message format is fixed, and only a 
few parameters are extracted from the original message. If the original message has repetitive segments and if you want 
to transform each of those segment into a new format with repetitive segments, you can use the For-Each Mediator 
together with the [PayloadFactory mediator](https://docs.wso2.com/display/EI640/PayloadFactory+Mediator).


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

| ID        | Summary                                                                         |
| ----------|:-------------------------------------------------------------------------------:|
| 1.1.1.1   | Converting a valid SOAP message to JSON ( Happy Path )                          |
| 1.1.1.2   | Handling a malformed SOAP messages                                              |
| 1.1.1.3   | Converting large sized SOAP message to JSON                                     |
| 1.1.1.4   | Converting SOAP message to JSON when arguments with deepCheck="false"           |
| 1.1.1.5   | Converting SOAP message to JSON when required arguments values are not provided |
| 1.1.1.6   | Converting SOAP message with special characters                                 |



