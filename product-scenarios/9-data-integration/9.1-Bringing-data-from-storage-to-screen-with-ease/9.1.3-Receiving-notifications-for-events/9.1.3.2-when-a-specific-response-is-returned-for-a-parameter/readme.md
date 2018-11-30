# 9.1.3.2 When a specific response is returned for a parameter

## Business use case narrative

The event trigger is evaluated when a specific query is returning its result. There isn't a specific creation of an XML 
element that is used with the XPath expression like in the input event trigger. But the full result XML is used to 
evaluate it. The result will be namespace qualified.

## When to use
This approach can be used to send notifications if a value received for an out parameter in the response is matched with
the given condition. When a certain event-trigger is activated, emails will be sent to all the respective subscribers. 

## Sample use-case
In this sample data service sending email notification for an out parameter is configured.

```xml
<data name="EventingSample" serviceNamespace="http://ws.wso2.org/dataservice/samples/eventing_sample">
   <config id="default">
      <property name="org.wso2.ws.dataservice.driver">org.h2.Driver</property>
      <property name="org.wso2.ws.dataservice.protocol">jdbc:h2:file:./samples/data-services/database/DATA_SERV_SAMP</property>
      <property name="org.wso2.ws.dataservice.user">wso2ds</property>
      <property name="org.wso2.ws.dataservice.password">wso2ds</property>
      <property name="org.wso2.ws.dataservice.minpoolsize">1</property>
      <property name="org.wso2.ws.dataservice.maxpoolsize">10</property>
      <property name="org.wso2.ws.dataservice.validation_query"/>
   </config>
   <query id="addProductQuery" useConfig="default">
      <sql>insert into Products (productCode, productLine, productName, quantityInStock, buyPrice) values (:productCode,:productLine,:productName,:quantityInStock,:buyPrice)</sql>
      <param name="productCode" ordinal="1" paramType="SCALAR" sqlType="STRING" type="IN"/>
      <param name="productLine" ordinal="2" paramType="SCALAR" sqlType="STRING" type="IN"/>
      <param name="productName" ordinal="3" paramType="SCALAR" sqlType="STRING" type="IN"/>
      <param name="quantityInStock" ordinal="4" paramType="SCALAR" sqlType="INTEGER" type="IN"/>
      <param name="buyPrice" ordinal="5" paramType="SCALAR" sqlType="DOUBLE" type="IN"/>
   </query>
   <query id="updateProductQuantityQuery" input-event-trigger="product_stock_low_trigger" useConfig="default">
      <sql>update Products set quantityInStock=:quantityInStock where productCode=:productCode</sql>
      <param name="productCode" ordinal="2" paramType="SCALAR" sqlType="STRING" type="IN"/>
      <param name="quantityInStock" ordinal="1" paramType="SCALAR" sqlType="DOUBLE" type="IN"/>
   </query>
   <query id="getProductByCodeQuery" output-event-trigger="product_line_motorcycles_trigger" useConfig="default">
      <sql>select productCode, productLine, productName, quantityInStock, buyPrice from Products where productCode=:productCode</sql>
      <param name="productCode" ordinal="1" paramType="SCALAR" sqlType="STRING" type="IN"/>
      <result element="Products" rowName="Product">
         <element column="productCode" name="productCode" xsdType="string"/>
         <element column="productLine" name="productLine" xsdType="string"/>
         <element column="productName" name="productName" xsdType="string"/>
         <element column="quantityInStock" name="quantityInStock" xsdType="integer"/>
         <element column="buyPrice" name="buyPrice" requiredRoles="admin" xsdType="double"/>
      </result>
   </query>
   <event-trigger id="product_stock_low_trigger" language="XPath">
      <expression>/updateProductQuantityQuery/quantityInStock&lt;10</expression>
      <target-topic>product_stock_low_topic</target-topic>
      <subscriptions>
         <subscription>mailto:test@test.com</subscription>
      </subscriptions>
   </event-trigger>
   <event-trigger id="product_line_motorcycles_trigger" language="XPath">
      <expression>//*[local-name()='productLine' and namespace-uri()='http://ws.wso2.org/dataservice/samples/eventing_sample']='Motorcycles'</expression>
      <target-topic>product_line_motorcycles_topic</target-topic>
      <subscriptions>
         <subscription>mailto:test@test.com</subscription>
      </subscriptions>
   </event-trigger>
   <operation name="addProduct">
      <call-query href="addProductQuery">
         <with-param name="productCode" query-param="productCode"/>
         <with-param name="productLine" query-param="productLine"/>
         <with-param name="productName" query-param="productName"/>
         <with-param name="quantityInStock" query-param="quantityInStock"/>
         <with-param name="buyPrice" query-param="buyPrice"/>
      </call-query>
   </operation>
   <operation name="updateProductQuantity">
      <call-query href="updateProductQuantityQuery">
         <with-param name="productCode" query-param="productCode"/>
         <with-param name="quantityInStock" query-param="quantityInStock"/>
      </call-query>
   </operation>
   <operation name="getProductByCode">
      <call-query href="getProductByCodeQuery">
         <with-param name="productCode" query-param="productCode"/>
      </call-query>
   </operation>
</data>
```

### Prerequisites
* Create the Employee table inside the database.
```text
CREATE TABLE ACCOUNT(AccountID int NOT NULL,Branch varchar(255) NOT NULL, AccountNumber varchar(255),AccountType ENUM('CURRENT', 'SAVINGS') NOT NULL,Balance FLOAT,ModifiedDate DATE,PRIMARY KEY (AccountID));
```
* Insert value to the table
```text
INSERT INTO ACCOUNT VALUES (1,"AOB","A00012","CURRENT",231221,'2014-12-02');
```
* Enable mailto transport in ```<EI_HOME>/conf/axis2/axis2_client.xml```
```xml
<transportSender name="mailto" class="org.apache.axis2.transport.mail.MailTransportSender">
   <parameter name="mail.smtp.host">smtp.gmail.com</parameter>
   <parameter name="mail.smtp.port">587</parameter>
   <parameter name="mail.smtp.starttls.enable">true</parameter>
   <parameter name="mail.smtp.auth">true</parameter>
   <parameter name="mail.smtp.user">{EMIAL_USERNAME}</parameter>
   <parameter name="mail.smtp.password">{EMAIL_PASSWORD}</parameter>
   <parameter name="mail.smtp.from">{EMAIL_ADDRESS}</parameter>
</transportSender>
```
* Deploy above dataservice by creating EventingSample.dbs file with above content and copying the file to 
```<EI_HOME>/repository/deployment/server/dataservices``` directory

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
[Receiving Notifications from Data Services](https://docs.wso2.com/display/EI640/Receiving+Notifications+from+Data+Services)

## Test cases

|      ID       | Summary |
| ------------- | ------------- |
| 9.1.3.2.1     | Using output parameter event triggers send an email |