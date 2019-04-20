 WSO2 Enterprise Integrator @product.version@
======================================================================

Welcome to the WSO2 EI @product.version@ release

WSO2 EI is a unified distribution of WSO2 Enterprise Service Bus and Data Services Server, which works as a single runtime (Integrator) along with optional runtimes for WSO2 Analytics, Business Processor and Message Broker.
This product distribution includes a shared component directory, with profile-based management capabilities for each runtime.

WSO2 EI simplifies integration by allowing users to easily configure message routing, mediation, transformation, logging, task scheduling, load balancing, failover routing, event brokering etc.
Data services and various applications can also be hosted and exposed using WSO2 EI.
These integration capabilities are further powered by the capabilities of the WSO2 Analytics, Business Processor and Message Broker runtimes.

Key features of WSO2 EI @product.version@
==================================

See the online WSO2 EI documentation for more information on product features: https://docs.wso2
.com/display/EI650/WSO2+Enterprise+Integrator+Documentation


Installation & Running
==================================

Running the Integrator
==================================
1. Extract  wso2ei-@product.version@.zip and go to the extracted directory/bin.
2. Run integrator.sh or integrator.bat.
3. Point your favourite browser to  https://localhost:9443/carbon
4. Use the following username and password to login
   username : admin
   password : admin

Running other runtimes individually (Analytics, Broker, Business Process )
========================================================
1. Extract wso2ei-@product.version@.zip and go to the extracted directory.
2. Go to wso2ei-@product.version@/wso2 directory.
3. Go to appropriate runtime directory (analytics/broker/business-process ) /bin.
4. Execute wso2server.sh or wso2server.bat.
5. Optionally, you can start the runtimes using scripts located at wso2ei-@product.version@/bin directory.
Scripts available are analytics.sh/analytics.bat for analytics profile, broker.sh/broker.bat for broker profile and
business-process.sh/business-process.bat for business process profile.

WSO2 EI distribution directory
=============================================

 - bin
	  Contains various scripts (.sh & .bat scripts).

 - dbscripts
	  Contains all the database scripts.

 - lib
	  Used to add external jars(dependencies) to all runtimes.

 - repository
	  The repository where services and modules deployed in WSO2 EI integrator runtime
	  are stored.

 - conf
	  Contains configuration files specific to integrator runtime.

 - logs
	  Contains all log files created during execution of EI.

 - resources
	  Contains additional resources that may be required, including sample
	  configurations and sample resources.

 - samples
	  Contains some sample services and client applications that demonstrate
	  the functionality and capabilities of WSO2 EI.

 - tmp
	  Used for storing temporary files, and is pointed to by the
	  java.io.tmpdir System property.

 - LICENSE.txt
	  Apache License 2.0 and the relevant other licenses under which
	  WSO2 EI is distributed.

 - README.txt
	  This document.

 - release-notes.html
	  Release information for WSO2 EI @product.version@

 - patches
	  Used to add patches related for all runtimes.

 -dropins
	  Used to add external osgi bundles(dependencies) to all runtimes.

 -extensions
	  Used to add carbon extensions.

 -servicepacks
	  Used to add service packs related to all runtimes.

 -webapp-mode

 -wso2/components
	  Contains profiles for all runtimes and the plugins folder.

 -wso2/lib
	  Contains jars that are required/shared by all runtimes.

 -wso2/analytics
	  Contains analytics runtime related files/folders.

      -wso2/analytics/conf
	    Analytics runtime specific configuration files.

      -wso2/analytics/repository
	    Directory where deployments of Analytics runtime is stored.

 -wso2/business-process
      Contains business-process runtime related files/folders.

      -wso2/business-process/conf
        Business-process runtime specific configuration files.

      -wso2/busines-process/repository
        Directory where deployments of business-process runtime is stored.

 -wso2/broker
      Contains broker runtime related files/folders.

      -wso2/broker/conf
        Broker runtime specific configuration files.

      -wso2/broker/repository
        Directory where deployments of broker runtime is stored.

 -wso2/tools
      Forget-me tool for user anonymization tasks.

Known issues of WSO2 EI @product.version@
==================================

     - https://github.com/wso2/product-ei/issues

Support
==================================

WSO2 Inc. offers a variety of development and production support
programs, ranging from Web-based support up through normal business
hours, to premium 24x7 phone support.

For additional support information please refer to http://wso2.com/support/

For more information on WSO2 EI, visit the WSO2 Oxygen Tank (http://wso2.org)

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
(c) Copyright 2019 WSO2 Inc.




