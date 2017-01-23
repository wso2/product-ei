# product-ei
Product EI is an unified distribution of WSO2 Enterprise Service Bus, Application Server, Data Services Server alongside Analytics, Business Process and Message Broker runtimes. With the capability of shared components directory with profile based management of each runtime.

## Startup
Starting up Enterprise Integrator can be done by invoking bin/integrator.sh or bin/integrator.bat and respective wso2server.sh or wso2server.bat files within the EI-HOME/wso2/< runtime >. 

Executing bin/start-all.sh or bin/start-all.bat performs startup of all the runtimes with respective offsets. 

## Known Issues
All the open issues pertaining to WSO2 Enterprise Integrator are reported at the following location:
[known issues](https://github.com/wso2/product-ei/issues)

## Build from the source
- Get a clone or download source from [github](https://github.com/wso2/product-ei)
- Run the Maven command ``mvn clean install`` from the root directory
- Extract the ballerina distribution created at product-ei/distribution/target/wso2ei-6.0.0-SNAPSHOT.zip in to your local directory
