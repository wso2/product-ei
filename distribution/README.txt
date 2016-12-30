================================================================================
                        WSO2 Enterprise Service Bus 5.0.0
================================================================================

Welcome to the WSO2 ESB 5.0.0 release

WSO2 ESB is a lightweight and easy-to-use Open Source Enterprise Service Bus
(ESB) available under the Apache Software License v2.0. WSO2 ESB allows
administrators to simply and easily configure message routing, intermediation,
transformation, logging, task scheduling, load balancing, failover routing,
event brokering, etc.. The runtime has been designed to be completely
asynchronous, non-blocking and streaming based on the Apache Synapse core.

This is based on the revolutionary WSO2 Carbon [Middleware a' la carte]
framework. All the major features have been developed as pluggable Carbon
components.

Key Features of WSO2 ESB
==================================

1. Proxy services - facilitating synchronous/asynchronous transport, interface
   (WSDL/Schema/Policy), message format (SOAP 1.1/1.2, POX/REST, Text, Binary),
   QoS (WS-Addressing/WS-Security) and optimization switching (MTOM/SwA).
2. API facilitating building REST services.
3. Non-blocking HTTP/S transports based on Apache HttpCore for ultrafast
   execution and support for thousands of connections at high concurreny with
   constant memory usage.
4. Built in Registry/Repository, facilitating dynamic updating and reloading
   of the configuration and associated resources (e.g. XSLTs, XSD, WSDL,
   Policies, JS, Configurations ..)
5. Easily extendable via custom Java class (mediator and command)/Spring
   mediators, or BSF Scripting languages (Javascript, Ruby, Groovy, etc.)
6. Built in support for scheduling tasks using the Quartz scheduler.
7. Load-balancing (with or without sticky sessions)/Fail-over, and clustered
   Throttling and Caching support
8. WS-Security, Caching & Throttling configurable via
   (message/operation/service level) WS-Policies
9. Lightweight, XML and Web services centric messaging model
10. Support for industrial standards (Hessian binary web service protocol/
   Financial Information eXchange protocol and optional Helth Level-7 protocol)
11. Enhanced support for the VFS(File/FTP/SFTP)/JMS/Mail transports with
    optional TCP/UDP transports and transport switching for any of the above
    transports
12. Support for message splitting & aggregation using the EIP and service
    callouts
13. Database lookup & store support with DBMediators with reusable database
    connection pools
14. Transactions support via the JMS transport and Transaction mediator for
    database mediators
15. Internationalized GUI management console with user/permission management for
    configuration development and monitoring support with statistics,
    configurable logging and tracing
16. JMX monitoring support and JMX management capabilities like,
    Gracefull/Forcefull shutdown/restart
17. Inbound endpoint is  a message source that can be configured dynamically.
    Inbound endpoints support all transports to work in a multi-tenant mode. 
    The behaviour of an inbound endpoint can be Polling, Busy wait or Listening 
    based on the implementation
18. Guaranteed message delivery pattern is supported with cluster coordination 
    support added to the new Message store and Message processor implementation
19. Message queueing protocol support has been extended with MQTT, RabbitMQ, Kafka
    and HL7 Inbound endpoint/transport implementations.
20. Dynamic SSL profiles support added to configure keystores dynamically without 
    even restarting the server


Installation & Running
==================================

1. Extract the wso2esb-5.0.0.zip and go to the extracted directory
2. Run the wso2server.sh or wso2server.bat as appropriate
3. Point your favourite browser to

    https://localhost:9443/carbon

4. Use the following username and password to login

    username : admin
    password : admin

5. Sample configurations can be started with wso2esb-samples.sh or
   wso2esb-samples.bat as appropriate specifying the sample number with the -sn
   option, for example to run sample 0 the command is

    ./wso2esb-samples.sh -sn 0
     wso2esb-samples.bat -sn 0

WSO2 ESB 5.0.0 distribution directory structure
=============================================

	CARBON_HOME
		|- bin <folder>
		|- dbscripts <folder>
		|- lib <folder>
		|- repository <folder>
			|-- logs <folder>
			|--- database <folder>
			|--- conf <folder>
		|- resources <folder>
		|- samples <folder>
		|- tmp <folder>
		|- LICENSE.txt <file>
		|- README.txt <file>
		|- release-notes.html <file>

    - bin
	  Contains various scripts .sh & .bat scripts

    - database
	  Contains the database

    - dbscripts
	  Contains all the database scripts

    - lib
	  Contains the basic set of libraries required to startup ESB
	  in standalone mode

    - repository
	  The repository where services and modules deployed in WSO2 ESB
	  are stored. In addition to this the components directory inside the
	  repository directory contains the carbon runtime and the user added
	  jar files including mediators third party libraries and so on..
	
	- conf
	  Contains configuration files

	- logs
	  Contains all log files created during execution

    - resources
	  Contains additional resources that may be required, including sample
	  configuration and sample resources

    - samples
	  Contains some sample services and client applications that demonstrate
	  the functionality and capabilities of WSO2 ESB

    - tmp
	  Used for storing temporary files, and is pointed to by the
	  java.io.tmpdir System property

    - LICENSE.txt
	  Apache License 2.0 and the relevant other licenses under which
	  WSO2 ESB is distributed.

    - README.txt
	  This document.

    - release-notes.html
	  Release information for WSO2 ESB 5.0.0

Support
==================================

WSO2 Inc. offers a variety of development and production support
programs, ranging from Web-based support up through normal business
hours, to premium 24x7 phone support.

For additional support information please refer to http://wso2.com/support/

For more information on WSO2 ESB, visit the WSO2 Oxygen Tank (http://wso2.org)

Known issues of WSO2 ESB 5.0.0
==================================

 * Dependency management within the configuration is not handled properly

Issue Tracker
==================================

  https://wso2.org/jira/browse/CARBON
  https://wso2.org/jira/browse/ESBJAVA

Crypto Notice
==================================

   This distribution includes cryptographic software.  The country in
   which you currently reside may have restrictions on the import,
   possession, use, and/or re-export to another country, of
   encryption software.  BEFORE using any encryption software, please
   check your country's laws, regulations and policies concerning the
   import, possession, or use, and re-export of encryption software, to
   see if this is permitted.  See <http://www.wassenaar.org/> for more
   information.

   The U.S. Government Department of Commerce, Bureau of Industry and
   Security (BIS), has classified this software as Export Commodity
   Control Number (ECCN) 5D002.C.1, which includes information security
   software using or performing cryptographic functions with asymmetric
   algorithms.  The form and manner of this Apache Software Foundation
   distribution makes it eligible for export under the License Exception
   ENC Technology Software Unrestricted (TSU) exception (see the BIS
   Export Administration Regulations, Section 740.13) for both object
   code and source code.

   The following provides more details on the included cryptographic
   software:

   Apache Rampart   : http://ws.apache.org/rampart/
   Apache WSS4J     : http://ws.apache.org/wss4j/
   Apache Santuario : http://santuario.apache.org/
   Bouncycastle     : http://www.bouncycastle.org/

--------------------------------------------------------------------------------
(c) Copyright 2016 WSO2 Inc.



