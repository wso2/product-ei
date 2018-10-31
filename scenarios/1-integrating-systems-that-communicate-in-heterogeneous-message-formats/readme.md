# 1. Integrating systems that communicate in heterogeneous message formats

## Business use case narrative

Integration of systems that communicate in various message formats is a common business case in enterprise integration. 
To allow this type of integration, the message formats of the systems need to be converted into the compatible formats.

Let's assume that there is a service that returns data in XML. 
This service needs to be consumed by a mobile client, which accepts messages only in JSON format. 
In this use case, some mediation service in the middle should bridge the communication gap. 
The major business case of this scenario is allowing multiple systems to communicate with each other without depending 
on the message format each system supports.

Alternative:
Integration of systems that communicate in various message formats is a common business case in enterprise integration. In this business case, some mediation service is required in the middle to bridge the communication gap among the systems.

Let's consider a service that returns data in XML format. Assume that this service needs to be consumed by a mobile client, which accepts messages only in JSON format. To allow these two systems to communicate, the mediation service needs to convert message formats during the communication. This allows the systems to communicate with each other without depending on the message formats supported by each system.

## Persona
Developer 

## Sub-Scenarios
- 1.1 Converting SOAP message to JSON 
- 1.2 Converting POX message to JSON
