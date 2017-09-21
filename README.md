# WSO2 Enterprise Integrator 6.1.1-update5
WSO2 EI is a unified distribution of WSO2 Enterprise Service Bus, Application Server and Data Services Server, which works as a single runtime (Integrator) along with optional runtimes for WSO2 Analytics, Business Processor and Message Broker. This product distribution includes a shared component directory, with profile-based management capabilities for each runtime.

WSO2 EI simplifies integration by allowing users to easily configure message routing, inter-mediation,transformation, logging, task scheduling, load balancing, failover routing, event brokering etc. Data services and various applications can also be hosted and exposed using WSO2 EI. These integration capabilities are further powered by the capabilities of the WSO2 Analytics, Business Processor and Message Broker runtimes.

## Installation & Running
## Running the Integrator
1. Extract  wso2ei-6.1.1-update5.zip and go to the extracted directory/bin.
2. Run integrator.sh or integrator.bat.
3. Point your favourite browser to  https://localhost:9443/carbon
4. Use the following username and password to login
   username : admin
   password : admin
   
## Running other runtimes individually (Analytics, Broker, Business-Process)
1. Extract wso2ei-6.1.1-update5 and go to the extracted directory.
2. Go to wso2ei-6.1.1-update5/wso2 directory.
3. Go to appropriate runtime directory (analytics/broker/business-process) /bin.
4. Execute wso2server.sh or wso2server.bat.
3. Access the url related to the required runtime. (For example, use https://localhost:9445/carbon for the business-process runtime.)

## Known Issues
All the open issues pertaining to WSO2 Enterprise Integrator are reported at the following location:
[known issues](https://github.com/wso2/product-ei/issues)

## Build from the source
- Get a clone or download source from [github](https://github.com/wso2/product-ei)
- Run the Maven command ``mvn clean install`` from the root directory
- Extract the Product EI distribution created at product-ei/distribution/target/wso2ei-6.1.1-update5.zip in to your 
local directory


|  Branch | Build Status |
| :------------ |:-------------
| master      | [![Build Status](https://wso2.org/jenkins/job/products/job/product-ei/badge/icon)](https://wso2.org/jenkins/products/job/product-ei) |
