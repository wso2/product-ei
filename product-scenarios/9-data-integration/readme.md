# 1. Data Integration

## Business use case narrative

Data integration is an important part of an integration process. For example, consider the following scenario, where you
have a typical integration process that is managed using the ESB profile of WSO2 EI. In this scenario, data stored in 
various, disparate datasources is required in order to complete the integration use case. The data services 
functionality that is embedded in the ESB profile allows you to manage this integration scenario by decoupling the data
from the datasource layer and exposing them as data services. The main integration flow defined in the ESB profile will 
then have the capability of managing the data through the data service. Once the data service is defined, you can 
manipulate the data stored in the datasources by invoking the relevant operation defined in the data service. For 
example, you can perform the basic CRUD operations as well as other advanced operations.

## Persona
Developer 

## Sub-Scenarios
- [Bringing data from storage to screen with ease](9.1-Bringing-data-from-storage-to-screen-with-ease) 