Migration of EI Analytics from DAS to SP [EI 6.4.0 from 6.x.x]
==============================================================
==============================================================

Introduction
============
Analytics profile of WSO2 EI 6.3.0 & below [EI 6.x.x] are based on WSO2 DAS.
Analytics profile of WSO2 EI 6.4.0 is based on WSO2 Stream Processor.
When upgrading from WSO2 EI 6.x.x to 6.4.0, you are moving from an instance of WSO2 DAS to WSO2 Stream Processor. EI Analytics table migration is an indirect process where we need to convert the Analytics tables into RDBMS tables by running spark scripts using the CarbonJDBC provider packed with DAS. The "migEIAnalytics.sh/migEIAnalytics.bat" scripts create the EI Analytics tables & the "migEIAnalyticsSpark" spark script migrate the data.

How to Build?
=============
1. Go to the $project home folder & execute following command from the project home folder to generate the final Jar file. 

$mvn clean install

2. A new single jar named “migEI.one-jar.jar” is created in the $project/target folder together with its dependency jars.

How to Migrate ?
================
1. Copy the built jar file "migEI.one-jar.jar" and "migEIAnalytics.sh/migEIAnalytics.bat" file into a same directory location.

2. Execute the "migEIAnalytics.sh/migEIAnalytics.bat" file. 

$./migEIAnalytics.sh

3. Enter the correct database details in the command prompt.

4. The tables related to EI Analytics are created in the relevant database specified by the user.

5. Next run the Analytic profile of the old version of WSO2 EI which is based on 
WSO2 DAS using the following command. 

$./wso2server.sh

6. Then execute the spark script "migEIAnalyticsSpark" to migrate the data related to the analytics profile. The migrated data would be stored in the RDBMS database specified by the user.

7. Run the Analytic profile of the new version of WSO2 EI which is based on WSO2 SP to generate the rest of the EI Analytics tables using the following command. 

$./worker.sh

8. Run the EI Product & send an event to populate data to aggregation tables.

9. Run the Analytic dashboard of the new version of WSO2 EI to view the migrated statistics using the following command. 

$./dashboard.sh 
