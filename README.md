# WSO2 Enterprise Integrator 7.0.0
WSO2 EI is a unified distribution of Ballerina which works as a single runtime (Integrator) along with optional runtimes for WSO2 Analytics and Message Broker. This product distribution includes a shared component directory, with profile-based management capabilities for each runtime.

WSO2 EI simplifies integration by allowing users to connect apps and services to handle all types of integration scenarios, such as collecting top tweets from a specific location and adding them to a Google spreadsheet, generating emails with real-time quotes pulled from a stock quote service, transforming and routing data based on advanced logic, and much more. These integration capabilities are further powered by the capabilities of the WSO2 Analytics and Message Broker runtimes.

## Installation & Running
## Running the Integrator
1. Extract  wso2ei-7.0.0.zip and go to the extracted directory/bin.
2. Run integrator.sh or integrator.bat.
3. Point your favourite browser to  https://localhost:9443/carbon
4. Use the following username and password to login
   username : admin
   password : admin
   
## Running other runtimes individually (Analytics, Broker)
1. Extract wso2ei-7.0.0.zip and go to the extracted directory.
2. Go to wso2ei-7.0.0/wso2 directory.
3. Go to appropriate runtime directory (analytics/broker) /bin.
4. Execute wso2server.sh or wso2server.bat.
3. Access the url related to the required runtime. (For example, use https://localhost:9445/carbon for the analytics runtime.)

## Known Issues
All the open issues pertaining to WSO2 Enterprise Integrator are reported at the following location:
[known issues](https://github.com/wso2/product-ei/issues)

## Build from the source
- Get a clone or download source from [github](https://github.com/wso2/product-ei)
- Run the Maven command ``mvn clean install`` from the root directory
- Extract the Product EI distribution created at product-ei/distribution/target/wso2ei-7.0.0.zip in to your local directory


|  Branch | Build Status |
| :------------ |:-------------
| master      | [![Build Status](https://wso2.org/jenkins/job/product-ei/badge/icon)](https://wso2.org/jenkins/job/product-ei) |
