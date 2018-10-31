#1. Integrating systems that communicate in heterogeneous message formats

##Business use-case narrative
Integration of systems that communicate in various message formats, is a common scenario in enterprise integration. To do this kind of integrations, message formats of the systems need to be converted into compatible formats.
Let's assume that there is a service which returns data in a specific format (e.g. XML). This service need to be consumed by a mobile client which accepts only messages in JSON format. In this use case, some mediation service in middle should bridge the communication gap. So here the major business case would be, allowing multiple systems to communicate with each other without depending on message format each system supports.

##Persona
Developer 

##Sub-Scenarios
- 1.1 Converting SOAP message to JSON 
- 1.2 Converting POX message to JSON