# 9.1.1.5 Inserting multiple records in a single operation

## Business use case narrative

In this scenario, send multiple (IN-Only) requests to a datasource using a single operation (batch operation). If one 
request to datasource fails all the requests within the patch opetration has to be rolled back.

## When to use
This approach can be used to insert multiple records in a single operation as a batch. 

## Sample use-case
In this sample multiple records are getting inserted by invoking a single operation .

```xml
<data enableBatchRequests="true" name="BatchRequestSample" serviceNamespace="http://ws.wso2.org/dataservice/samples/batch_request_sample">
   <config id="default">
      <property name="org.wso2.ws.dataservice.driver">org.h2.Driver</property>
      <property name="org.wso2.ws.dataservice.protocol">jdbc:h2:file:./samples/database/DATA_SERV_SAMP</property>
      <property name="org.wso2.ws.dataservice.user">wso2ds</property>
      <property name="org.wso2.ws.dataservice.password">wso2ds</property>
      <property name="org.wso2.ws.dataservice.minpoolsize">1</property>
      <property name="org.wso2.ws.dataservice.maxpoolsize">10</property>
      <property name="org.wso2.ws.dataservice.autocommit">false</property>
      <property name="org.wso2.ws.dataservice.validation_query">SELECT 1</property>
   </config>
   <query id="addEmployeeQuery" useConfig="default">
      <sql>insert into Employees (employeeNumber, lastName, firstName, email, salary) values(:employeeNumber,'test','test',:email,1000)</sql>
      <param name="employeeNumber" ordinal="1" paramType="SCALAR" sqlType="INTEGER" type="IN"/>
      <param name="email" ordinal="2" paramType="SCALAR" sqlType="STRING" type="IN">
         <validatePattern pattern="(?:[a-z0-9!#$%&amp;'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&amp;'*+/=?^_`{|}~-]+)*|&quot;(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])*&quot;)@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)\])"/>
      </param>
   </query>
   <query id="deleteEmployeeQuery" useConfig="default">
      <sql>delete from Employees where employeeNumber=:employeeNumber</sql>
      <properties>
         <property name="org.wso2.ws.dataservice.autocommit">false</property>
      </properties>
      <param name="employeeNumber" ordinal="1" paramType="SCALAR" sqlType="INTEGER" type="IN"/>
   </query>
   <query id="employeeExistsQuery" useConfig="default">
      <sql>select count(*) as c from Employees where employeeNumber=:employeeNumber</sql>
      <param name="employeeNumber" ordinal="1" paramType="SCALAR" sqlType="INTEGER" type="IN"/>
      <result element="Employees" rowName="Employee">
         <element column="c" name="exists"/>
      </result>
   </query>
   <operation name="deleteEmployee">
      <call-query href="deleteEmployeeQuery">
         <with-param name="employeeNumber" query-param="employeeNumber"/>
      </call-query>
   </operation>
   <operation name="addEmployee" returnRequestStatus="true">
      <call-query href="addEmployeeQuery">
         <with-param name="employeeNumber" query-param="employeeNumber"/>
         <with-param name="email" query-param="email"/>
      </call-query>
   </operation>
   <operation name="employeeExists">
      <call-query href="employeeExistsQuery">
         <with-param name="employeeNumber" query-param="employeeNumber"/>
      </call-query>
   </operation>
</data>
```

### Prerequisites
* Create the Employee table inside the database.
```
CREATE TABLE Employees (EmployeeNumber int(11) NOT NULL, FirstName varchar(255) NOT NULL, LastName varchar(255) DEFAULT NULL, Email varchar(255) DEFAULT NULL, Salary varchar(255));
```
* Deploy above dataservice by creating BatchRequestSample.dbs file with above content and copying the file to 
```<EI_HOME>/repository/deployment/server/dataservices``` directory

### How to try-out sample use-case
Service can be invoked using SOAP client.
For WSDL of the SOAP service, go to ```http://localhost:8280/services/RDBMSSample?wsdl```. 
This WSDL is based on the WSDL supplied in the data service configurations.

Sample request for "addEmployeeOp_batch_req" operation:
```text
POST http://localhost:8280/services/BatchRequestSample HTTP/1.1
Content-Type: text/xml;charset=UTF-8
SOAPAction: "urn:addEmployeeOp_batch_req"
Content-Length: 415
Host: localhost:8280


<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
   <soapenv:Header/>
   <soapenv:Body>
        <p:addEmployeeOp_batch_req xmlns:p="http://ws.wso2.org/dataservice">
            <!--1 or more occurrences-->
            <addEmployeeOp xmlns="http://ws.wso2.org/dataservice">
               <!--Exactly 1 occurrence-->
               <xs:EmployeeNumber xmlns:xs="http://ws.wso2.org/dataservice">1002</xs:EmployeeNumber>
               <!--Exactly 1 occurrence-->
               <xs:FirstName xmlns:xs="http://ws.wso2.org/dataservice">John</xs:FirstName>
               <!--Exactly 1 occurrence-->
               <xs:LastName xmlns:xs="http://ws.wso2.org/dataservice">Doe</xs:LastName>
               <!--Exactly 1 occurrence-->
               <xs:Email xmlns:xs="http://ws.wso2.org/dataservice">johnd@wso2.com</xs:Email>
               <!--Exactly 1 occurrence-->
               <xs:JobTitle xmlns:xs="http://ws.wso2.org/dataservice">Consultant</xs:JobTitle>
               <!--Exactly 1 occurrence-->
               <xs:Officecode xmlns:xs="http://ws.wso2.org/dataservice">01</xs:Officecode>
            </addEmployeeOp>
            <addEmployeeOp xmlns="http://ws.wso2.org/dataservice">
               <!--Exactly 1 occurrence-->
               <xs:EmployeeNumber xmlns:xs="http://ws.wso2.org/dataservice">1004</xs:EmployeeNumber>
               <!--Exactly 1 occurrence-->
               <xs:FirstName xmlns:xs="http://ws.wso2.org/dataservice">Peter</xs:FirstName>
               <!--Exactly 1 occurrence-->
               <xs:LastName xmlns:xs="http://ws.wso2.org/dataservice">Parker</xs:LastName>
               <!--Exactly 1 occurrence-->
               <xs:Email xmlns:xs="http://ws.wso2.org/dataservice">peterp@wso2.com</xs:Email>
               <!--Exactly 1 occurrence-->
               <xs:JobTitle xmlns:xs="http://ws.wso2.org/dataservice">Consultant</xs:JobTitle>
               <!--Exactly 1 occurrence-->
               <xs:Officecode xmlns:xs="http://ws.wso2.org/dataservice">01</xs:Officecode>
            </addEmployeeOp>
        </p:addEmployeeOp_batch_req>
   </soapenv:Body>
</soapenv:Envelope>
```

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
[Invoking an Operation with Multiple Records](https://docs.wso2.com/display/EI640/Invoking+an+Operation+with+Multiple+Records)

## Test cases

|      ID       | Summary |
| ------------- | ------------- |
| 9.1.1.5.1     | insert data as batches |