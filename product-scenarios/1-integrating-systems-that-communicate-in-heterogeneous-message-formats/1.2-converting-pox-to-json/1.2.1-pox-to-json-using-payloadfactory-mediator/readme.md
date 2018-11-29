# 1.2.1 Converting POX message to JSON using PayloadFactory mediator


## When to use
The reconstruction of entire message payload is needed when required format of Client and Service are different. 
In this use case we can use [PayloadFactory mediator](https://docs.wso2.com/display/EI640/PayloadFactory+Mediator) 
to transform POX message to JSON.

## Sample use case

## Prerequisites
A REST client like cURL to invoke the ESB API.

## Development guidelines

## Deployment guidelines

* We can simply deploy by copying the CAR archive into <EI_HOME>/repository/deployment/server/carbonapps directory, and 
it will be deployed.

<p align="center"><b> OR </b></p>

* We can create the api in Management Console and deploy.

## Supported versions

This is supported in all the EI and ESB versions

## Test cases

| ID        | Summary                                                                         |
| ----------|:-------------------------------------------------------------------------------:|
| 1.2.1.1   | Converting a valid POX message to JSON ( Happy Path )                          |
| 1.2.1.2   | Handling a malformed POX messages                                              |
| 1.2.1.3   | Converting large sized POX message to JSON                                     |
| 1.2.1.4   | Converting POX message to JSON when arguments with deepCheck="false"           |
| 1.2.1.5   | Converting POX message to JSON when required arguments values are not provided |
| 1.2.1.6   | Converting POX message with special characters                                 |



