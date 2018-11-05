# WSO2 Enterprise Integrator 6.4.0

WSO2 EI is a unified distribution of WSO2 Enterprise Service Bus and Data Services Server, which works as a single runtime (Integrator) along with optional runtimes for WSO2 Analytics, Business Processor and Message Broker. This product distribution includes a shared component directory, with profile-based management capabilities for each runtime.

WSO2 EI simplifies integration by allowing users to easily configure message routing, inter-mediation,transformation, logging, task scheduling, load balancing, failover routing, event brokering etc. Data services and various applications can also be hosted and exposed using WSO2 EI. These integration capabilities are further powered by the capabilities of the WSO2 Analytics, Business Processor and Message Broker runtimes.

## Installation & Running

## Running the Integrator

1. Extract  wso2ei-6.4.0.zip and go to the extracted directory/bin.
2. Run integrator.sh or integrator.bat.
3. Point your favourite browser to  <https://localhost:9443/carbon>
4. Use the following username and password to login
   username : admin
   password : admin

## Running other runtimes individually (Analytics, Broker, Business-Process)

1. Extract wso2ei-6.4.0 and go to the extracted directory.
2. Go to wso2ei-6.4.0/wso2 directory.
3. Go to appropriate runtime directory (analytics/broker/business-process) /bin.
4. Execute wso2server.sh or wso2server.bat.
5. Access the url related to the required runtime. (For example, use <https://localhost:9445/carbon> for the business-process runtime.)

## Known Issues

All the open issues pertaining to WSO2 Enterprise Integrator are reported at the following location:
[known issues](https://github.com/wso2/product-ei/issues)

### Certificate validation errors

WSO2 EI includes the MSF4J profile. That profile make use of an old version of the Eclipse Equinox framework that is signed with a certificate that is not trusted by recent versions of Java.

If the Maven build fails with the error ***One or more certificates rejected. Cannot proceed with installation***, that means you have to manually import the certificate to the Java certificate store.

In that case, you can check what certificate is missing from the certificate store with the following command started from the root of the Product EI source directory:

```jarsigner -verify ./p2-profile/msf4j-profile/target/p2-repo/plugins/org.eclipse.equinox.ds_(version).jar -verbose -certs```

For example, you need to download the certificate with thumbprint ***97817950D81C9670CC34D809CF794431367EF474*** from the DigiCert website (<https://www.digicert.com/digicert-root-certificates.htm>). To import it, you need to run the following command as root from your Java security directory (ex: /usr/lib/jvm/java-8-oracle/jre/lib/security/):

```keytool -import -file GTECyberTrustGlobalRoot.crt -keystore cacerts```

***Note:*** The default password for the certificate store is *changeit*

## Build from the source

- Get a clone or download source from [github](https://github.com/wso2/product-ei)
- Run the Maven command ``mvn clean install`` from the root directory
- Extract the Product EI distribution created at product-ei/distribution/target/wso2ei-6.4.0.zip in to your local directory

## Building Product EI with dependency repositories

WSO2 Enterprise Integrator product respository has number of other repository dependencies. Hence if want to make a change to one of these repositories and get that changes included in the product ei, you will have to first make your changes to the dependency repository, build the dependency repository and then update that repository version within product ei or corresponding parent repositories. 

### Dependency repositories

Following are some of the important dependency repositories used within product-ei. You can use ``maven clean install`` command to build any of the below listed dependency repositories.

### Integrator/ESB profile

#### wso2-axis2-transports

Repository URL <https://github.com/wso2/wso2-axis2-transports>  
This repository contains additional transport implementations.  
Dependent repository:wso2-syanspe

#### wso2-commons-vfs

Repository URL <https://github.com/wso2/wso2-commons-vfs>  
This contains the forked apache commons vfs library. Provides file processing capabilities to ESB.  
Dependent repository:wso2-syanspe

#### wso2 synapse

Repository URL <https://github.com/wso2/wso2-synapse>  
Provides most of the ESB functionality to WSO2 Enterprise Service Bus.  
Dependent repository:carbon-mediation

#### carbon-mediation

Repository URL <https://github.com/wso2/carbon-mediation>  
Implements more functionality on top of wso2-synapse.  
Dependent repository:product-ei

#### carbon-data

Repository URL <https://github.com/wso2/carbon-data>    
Provides data services functionality.  
Dependent repository:product-ei

### Business Process profile

#### wso2-ode

Repository URL <https://github.com/wso2/wso2-ode>  
Fork of apache ode project. Provides BPEL execution capability.  
Dependent repository:carbon-business-process

#### carbon-business-process

Repository URL <https://github.com/wso2/carbon-business-process>  
Contains the modules implementing BPEL, BPMN and WS-Human Tasks capabilities for business-process profile.  
Dependent repository:product-ei

### Message Broker profile

#### andes

Repository URL <https://github.com/wso2/andes>  
Provides message brokering functionality.  
Dependent repository:carbon-business-messaging

#### carbon-messaging

Repository URL <https://github.com/wso2/carbon-business-messaging/>  
Builds more capabilities on top of andes.  
Dependent repository:product-ei

### Analytics profile

Repository URL <https://github.com/wso2/carbon-analytics>  
Includes the functionalities provided in wso2-analytics platform.  
Dependent repositories:product-ei

### Microservices/MSF4J profile

Repository URL <https://github.com/wso2/msf4j>  
Microservices implementation framework for java


## Build status of product EI

|  Branch | Build Status |
| :------------ |:-------------
| master      | [![Build Status](https://wso2.org/jenkins/job/products/job/product-ei/badge/icon)](https://wso2.org/jenkins/job/products/job/product-ei) |
