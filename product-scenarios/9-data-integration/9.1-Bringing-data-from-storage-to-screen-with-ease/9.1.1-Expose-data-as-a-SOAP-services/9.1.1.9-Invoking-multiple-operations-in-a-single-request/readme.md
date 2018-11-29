# 9.1.1.9 Invoking multiple operations in a single request

## Business use case narrative

In this scenario, allows you to invoke multiple operations (consecutively) to a datasource using a single operation.
In one of the operation failed, all the individual operations are rolled back.

## When to use
This approach can be used to execute multiple operations (consecutively) to a datasource using a single operation in a 
transactional manner.

## Sample use-case
In this sample user can execute multiple operations (consecutively) to a datasource using a single operation.

```xml
<data enableBoxcarring="true" name="DTPSampleService" serviceNamespace="http://ws.wso2.org/dataservice/samples/dtp_sample">
   <config id="H2DataSource1">
      <property name="org.wso2.ws.dataservice.xa_datasource_class">org.h2.jdbcx.JdbcDataSource</property>
      <property name="org.wso2.ws.dataservice.xa_datasource_properties">
         <property name="URL">jdbc:h2:file:./samples/data-services/database/DATA_SERV_SAMP</property>
         <property name="User">wso2ds</property>
         <property name="Password">wso2ds</property>
      </property>
   </config>
   <config id="H2DataSource2">
      <property name="org.wso2.ws.dataservice.xa_datasource_class">org.h2.jdbcx.JdbcDataSource</property>
      <property name="org.wso2.ws.dataservice.xa_datasource_properties">
         <property name="URL">jdbc:h2:file:./samples/data-services/database/DATA_SERV_SAMP2</property>
         <property name="User">wso2ds</property>
         <property name="Password">wso2ds</property>
      </property>
   </config>
   <query id="addAccountToBank1Query" returnGeneratedKeys="true" useConfig="H2DataSource1">
      <sql>insert into Accounts (balance) values (:balance)</sql>
      <param defaultValue="0" name="balance" sqlType="DOUBLE"/>
      <result element="GeneratedKeys" rowName="Entry" useColumnNumbers="true">
         <element column="1" name="ID" xsdType="integer"/>
      </result>
   </query>
   <operation name="addAccountToBank1">
      <call-query href="addAccountToBank1Query">
         <with-param name="balance" query-param="balance"/>
      </call-query>
   </operation>
   <query id="addToAccountBalanceInBank1Query" useConfig="H2DataSource1">
      <sql>update Accounts set balance = balance + :value where accountId= :accountId</sql>
      <param name="accountId" sqlType="INTEGER"/>
      <param name="value" sqlType="DOUBLE">
         <validateDoubleRange maximum="2000" minimum="-2000"/>
      </param>
   </query>
   <operation name="addToAccountBalanceInBank1">
      <call-query href="addToAccountBalanceInBank1Query">
         <with-param name="accountId" query-param="accountId"/>
         <with-param name="value" query-param="value"/>
      </call-query>
   </operation>
   <query id="getAccountBalanceFromBank1Query" useConfig="H2DataSource1">
      <sql>select balance from Accounts where accountId=:accountId</sql>
      <param name="accountId" sqlType="INTEGER"/>
      <result element="Balance">
         <element column="balance" name="Value" xsdType="double"/>
      </result>
   </query>
   <operation name="getAccountBalanceFromBank1">
      <call-query href="getAccountBalanceFromBank1Query">
         <with-param name="accountId" query-param="accountId"/>
      </call-query>
   </operation>
   <query id="addAccountToBank2Query" returnGeneratedKeys="true" useConfig="H2DataSource2">
      <sql>insert into Accounts (balance) values (:balance)</sql>
      <param defaultValue="0" name="balance" sqlType="DOUBLE"/>
      <result element="GeneratedKeys" rowName="Entry" useColumnNumbers="true">
         <element column="1" name="ID" xsdType="integer"/>
      </result>
   </query>
   <operation disableStreaming="true" name="addAccountToBank2">
      <call-query href="addAccountToBank2Query">
         <with-param name="balance" query-param="balance"/>
      </call-query>
   </operation>
   <query id="addToAccountBalanceInBank2Query" useConfig="H2DataSource2">
      <sql>update Accounts set balance = balance + :value where accountId= :accountId</sql>
      <param name="accountId" sqlType="INTEGER"/>
      <param name="value" sqlType="DOUBLE">
         <validateDoubleRange maximum="2000" minimum="-2000"/>
      </param>
   </query>
   <operation name="addToAccountBalanceInBank2">
      <call-query href="addToAccountBalanceInBank2Query">
         <with-param name="accountId" query-param="accountId"/>
         <with-param name="value" query-param="value"/>
      </call-query>
   </operation>
   <query id="getAccountBalanceFromBank2Query" useConfig="H2DataSource2">
      <sql>select balance from Accounts where accountId=:accountId</sql>
      <param name="accountId" sqlType="INTEGER"/>
      <result element="Balance">
         <element column="balance" name="Value" xsdType="double"/>
      </result>
   </query>
   <operation name="getAccountBalanceFromBank2">
      <call-query href="getAccountBalanceFromBank2Query">
         <with-param name="accountId" query-param="accountId"/>
      </call-query>
   </operation>
</data>
```

### Prerequisites

### How to try-out sample use-case

## Supported versions
This is supported in all the EI and DSS versions

## Pre-requisites
None

## REST API (if available)
N/A

## Deployment guidelines
Standard way of deploying a data service is by packaging the data service as a Carbon Application. Please refer 
[Managing Data Integration Artifacts via Tooling](https://docs.wso2.com/display/EI640/Managing+Data+Integration+Artifacts+via+Tooling) for instructions.

## Reference
[Invoking Multiple Operations via Request Box](https://docs.wso2.com/display/EI640/Invoking+Multiple+Operations+via+Request+Box)

## Test cases

|      ID       | Summary |
| ------------- | ------------- |
| 9.1.1.9.1     | Invoke create operation and a select operation in a single soap request using request box |