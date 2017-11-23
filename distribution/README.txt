 WSO2 Enterprise Integrator @product.version@
===============================================

Welcome to the WSO2 EI @product.version@ release

WSO2 EI is a unified distribution of Ballerina which works as a single runtime (Integrator) along with the optional runtime
for Message Broker.

This product distribution includes a shared component directory, with profile-based management capabilities for each runtime.

WSO2 EI simplifies integration by allowing users to connect apps and services to handle all types of integration scenarios,
such as collecting top tweets from a specific location and adding them to a Google spreadsheet,
generating emails with real-time quotes pulled from a stock quote service, transforming and routing data based on advanced logic, and much more.
These integration capabilities are further powered by the capabilities of the WSO2 Message Broker runtime.

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

-dbscripts <br>
    Contains all the database scripts.

-lib <br>
    Place to add external library files

-conf <br>
    Contains configuration files specific to EI.

-logs <br>
    Contains all log files created during execution of EI.

-resources <br>
    Contains additional resources that may be required, including sample configuration and sample resources.

-samples <br>
    Contains some sample services and client applications that demonstrate the functionality and capabilities of WSO2 EI.

-tmp <br>
    Used for storing temporary files, and is pointed to by the java.io.tmpdir System property.

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
(c) Copyright 2017 WSO2 Inc.




