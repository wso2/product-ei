 WSO2 Enterprise Integrator @product.version@
===============================================

Welcome to the WSO2 EI @product.version@ release

WSO2 Enterprise Integrator 7.0.0 (WSO2 EI 7.0.0) is an intuitive, lightweight, container-native and high-performance
integration platform that enables the agile and rapid development of integrations between services, apps, data and systems.

WSO2 Enterprise Integrator 7.0.0 is an essential platform for building microservices compositions
and bridging microservices and other monolithic applications, in which you will independently develop,
deploy and administer container native integration runtimes.

WSO2 Enterprise Integrator 7.0.0 also can be used as the centralized integration middleware for
conventional enterprise integration requirements.

WSO2 Enterprise Integrator 7.0.0 is composed of:

- An integrator runtime based on Ballerina, 
which is an event-driven, parallel programming language that is designed for building networked applications.
- WSO2 Message Broker,
which can be used alongside with integrator for enabling reliable persistent messaging between systems.
- A workflow engine based on Activiti to execute business processes described in BPMN 2.0.

Installation & Running
=======================

Running the Integrator
=======================
1. Extract  wso2ei-7.0.0.zip and go to the extracted directory/bin.
2. Run integrator.sh <.balx_FILE_LOCATION> or integrator.bat <.balx_FILE_LOCATION>.


Running the Broker
==================
1. Extract wso2ei-7.0.0.zip and go to the extracted directory/bin.
2. Execute broker.sh or broker.bat.


WSO2 EI distribution directory
===============================

-bin <br>
Contains various scripts .sh & .bat scripts.

-conf <br>
Contains configuration files specific to EI.

-samples <br>
 Contains some sample services and client applications that demonstrate the functionality and capabilities of WSO2 EI.

-LICENSE.txt <br>
 Apache License 2.0 and the relevant other licenses under which WSO2 EI is distributed.

-README.txt <br> 
 Product information for WSO2 EI 7.0.0.

-wso2/broker <br>
 Contains broker runtime related files/folders.

-wso2/broker/conf <br>
 Broker runtime specific configuration files.

-wso2/ballerina <br>
 Contains ballerina runtime related files/folders.


Documentation
=============

For Enterprise Integrator 7 documentation, please refer to:
https://github.com/wso2/product-ei/blob/7.0.x/README.md

For more information about Message Broker, please visit:
https://github.com/wso2/message-broker/blob/master/README.md

More information on Ballerina can be found from:
https://ballerinalang.org/docs/


Known issues of WSO2 EI @product.version@
==================================

     - https://github.com/wso2/product-ei/labels/EI7

Support
==================================

WSO2 Inc. offers a variety of development and production support
programs, ranging from Web-based support up through normal business
hours, to premium 24x7 phone support.

For additional support information please refer to http://wso2.com/support/

For more information on WSO2 EI, visit the WSO2 Oxygen Tank (http://wso2.org)


(c) Copyright 2017 WSO2 Inc.




