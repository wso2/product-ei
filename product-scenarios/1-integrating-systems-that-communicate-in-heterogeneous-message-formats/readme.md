# 1. Integrating systems that communicate in heterogeneous message formats

## Business use case narrative

Integration of systems that communicate in various message formats is a common business case in enterprise integration. 
In this business case, some mediation service is required in the middle to bridge the communication gap among the systems.

Let's consider a service that returns data in XML format. 
Assume that this service needs to be consumed by a mobile client, which accepts messages only in JSON format. 
To allow these two systems to communicate, the mediation service needs to convert message formats during the communication. 
This allows the systems to communicate with each other without depending on the message formats supported by each system.

## Persona
Developer 

## Sub-Scenarios
- [1.1 Converting SOAP message to JSON](https://github.com/wso2/product-ei/tree/scenarios-docs/scenarios/1-integrating-systems-that-communicate-in-heterogeneous-message-formats/1.1-converting-soap-to-json)
- 1.2 Converting POX message to JSON
