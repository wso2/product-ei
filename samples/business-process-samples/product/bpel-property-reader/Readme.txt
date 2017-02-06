===========================
### Configuration Guide ###
===========================

Configuration file is $PropertyReaderExt.properties

A sample config:

clientTrustStorePath=/home/wso2bps-3.1.0/repository/resources/security/wso2carbon.jks
clientTrustStorePassword=wso2carbon
clientTrustStoreType=JKS
bps.mgt.hostname=localhost
bps.mgt.port=9443
wso2.bps.username=admin
wso2.bps.password=admin

How to run
===========
1. 'mvn clean install' from bpel-property-reader directory location.
2. Copy the <bpel-property-reader>/target/bpel-property-reader-4.2.0.jar to <BPS_HOME>/repository/components/lib/
3. Copy PropertyReaderExt.properties file into <BPS_HOME>/repository/conf/
4. Start the server.

BPEL Configurations
=====================

Sample BPEL project is $PropertyReader_1.0.0.zip

NOTE:
-------------------------------------
<bpel:extensionActivity>
    <propr:readProperties location="conf:sample.properties">
        <property name="property1">
            <to variable="test"></to>
        </property>
        <property name="property2">
            <to variable="test2"></to>
        </property>
    </propr:readProperties>
</bpel:extensionActivity>
------------------------------------

If location starts with 'conf:'

** location="conf:file path inside config registry"

Similarly

** location="gov:file path inside governance registry"
** location="file:file path inside local machine"

1. Add the properties resource file into the BPS server registry or local machine according to BPEL Extension location attribute.
   i.e: /_system/config/sample.properties

2. Deploy BPEL and call the service.

