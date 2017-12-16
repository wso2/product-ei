|  Branch | Build Status |
| :------------ |:-------------
| 7.0.x      | [![Build Status](https://wso2.org/jenkins/view/products/job/products/job/product-ei_7.0.x/badge/icon)](https://wso2.org/jenkins/view/products/job/products/job/product-ei_7.0.x) |
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

# WSO2 Enterprise Integrator 7.0.0
WSO2 Enterprise Integrator 7.0.0 (WSO2 EI 7.0.0) is an intuitive, lightweight, container-native
and high-performance integration platform that enables the agile and rapid development of integrations between services, 
apps, data and systems.

WSO2 Enterprise Integrator 7.0.0 is composed of:

- An integrator runtime based on Ballerina, 
which is an event-driven, parallel programming language that is designed for building networked applications.
- A runtime of the WSO2 Message Broker 4.0, 
which can be used alongside with integrator for enabling reliable persistent messaging between system.
- A workflow engine based on Activiti to execute business processes described in BPMN 2.0.

WSO2 Enterprise Integrator 7.0.0 is an essential platform for building microservices, compositions and
bridging microservices and other monolithic applications, in which you will independently develop,
deploy and administrate container native integration runtimes.

WSO2 Enterprise Integrator 7.0.0 can also be used as the centralized integration middleware for conventional
enterprise integration requirements.

## Installation
Prerequisites
- Java 8 or higher. 


## Running the Integrator
1. Extract  wso2ei-7.0.0.zip and go to the extracted directory/bin.
2. Run integrator.sh <.balx_FILE_LOCATION> or integrator.bat <.balx_FILE_LOCATION>.
   
## Running the Broker
1. Extract wso2ei-7.0.0.zip and go to the extracted directory/bin.
2. Execute broker.sh or broker.bat.

## Quick Start
ToDo

##Documentation 
ToDo 

## Known Issues
All the open issues pertaining to WSO2 Enterprise Integrator are reported at the following location:
[known issues](https://github.com/wso2/product-ei/issues?q=is%3Aopen+is%3Aissue+label%3AEI7)

## Build from the source
- Get a clone or download source from [github](https://github.com/wso2/product-ei)
- Switch to the 7.0.x branch
- Run the Maven command ``mvn clean install`` from the root directory
- Extract the Product EI distribution created at product-ei/distribution/target/wso2ei-7.0.0.zip in to your local directory


