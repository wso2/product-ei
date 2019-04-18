# 9.1.1.1 Basic CRUD operations

## Business use case narrative

In this scenario, data resides in a datasource are exposed as a SOAP with basic CRUD operations (Insert, Select, Update 
and Delete). 

## When to use
This approach can be used to expose data resides in a datasource using CRUD operations. In this usecase the data is exposed
as a SOAP service.

## Sample use-case
In this sample data service insert, read, edit and delete operations on data in an RDBMS datasource are exposed as a 
SOAP operations.

```xml
<data name="RDBMSSample" serviceNamespace="http://ws.wso2.org/dataservice/samples/rdbms_sample">
   <config id="default">
      <property name="org.wso2.ws.dataservice.driver">org.h2.Driver</property>
      <property name="org.wso2.ws.dataservice.protocol">jdbc:h2:file:./samples/data-services/database/DATA_SERV_SAMP</property>
      <property name="org.wso2.ws.dataservice.user">wso2ds</property>
      <property name="org.wso2.ws.dataservice.password">wso2ds</property>
      <property name="org.wso2.ws.dataservice.minpoolsize">1</property>
      <property name="org.wso2.ws.dataservice.maxpoolsize">10</property>
      <property name="org.wso2.ws.dataservice.autocommit">false</property>
      <property name="org.wso2.ws.dataservice.validation_query"/>
   </config>
   <query id="selectEmployeeQuery" useConfig="default">
      <sql>select  * from Employees</sql>
      <result element="employees" rowName="employee">
         <element column="employeeNumber" name="employeeNumber-name" xsdType="string"/>
         <element column="lastName" name="lastName" xsdType="string"/>
         <element column="firstName" name="firstName" xsdType="string"/>
         <element column="email" name="email" xsdType="string"/>
         <element column="salary" name="salary" xsdType="string"/>
      </result>
   </query>
   <query id="setEmployeeSalaryQuery" useConfig="default">
      <sql>update Employees set salary=:salary where employeeNumber=:employeeNumber</sql>
      <param name="salary" ordinal="1" paramType="SCALAR" sqlType="DOUBLE" type="IN"/>
      <param name="employeeNumber" ordinal="2" paramType="SCALAR" sqlType="INTEGER" type="IN"/>
   </query>
   <query id="addEmployeeQuery" useConfig="default">
      <sql>insert into Employees (employeeNumber, lastName, firstName, email, salary) values(:employeeNumber,:lastName,:firstName,:email,:salary)</sql>
      <param name="employeeNumber" ordinal="1" paramType="SCALAR" sqlType="INTEGER" type="IN"/>
      <param name="lastName" ordinal="2" paramType="SCALAR" sqlType="STRING" type="IN"/>
      <param name="firstName" ordinal="3" paramType="SCALAR" sqlType="STRING" type="IN"/>
      <param name="email" ordinal="4" paramType="SCALAR" sqlType="STRING" type="IN"/>
      <param name="salary" ordinal="5" paramType="SCALAR" sqlType="DOUBLE" type="IN"/>
   </query>
   <query id="deleteEmployeeQuery" useConfig="default">
      <sql>delete from Employees where employeeNumber=:employeeNumber</sql>
      <param name="employeeNumber" ordinal="1" paramType="SCALAR" sqlType="INTEGER" type="IN"/>
   </query>
   <operation name="selectEmployee">
      <call-query href="selectEmployeeQuery"/>
   </operation>
   <operation name="setEmployeeSalary">
      <call-query href="setEmployeeSalaryQuery">
         <with-param name="employeeNumber" query-param="employeeNumber"/>
         <with-param name="salary" query-param="salary"/>
      </call-query>
   </operation>
   <operation name="addEmployee" returnRequestStatus="true">
      <call-query href="addEmployeeQuery">
         <with-param name="employeeNumber" query-param="employeeNumber"/>
         <with-param name="lastName" query-param="lastName"/>
         <with-param name="firstName" query-param="firstName"/>
         <with-param name="email" query-param="email"/>
         <with-param name="salary" query-param="salary"/>
      </call-query>
   </operation>
   <operation name="deleteEmployee">
      <call-query href="deleteEmployeeQuery">
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
* Deploy above dataservice by creating RDBMSSample.dbs file with above content and copying the file to 
```<EI_HOME>/repository/deployment/server/dataservices``` directory

### How to try-out sample use-case
Service can be invoked using SOAP client.
For WSDL of the SOAP service, go to ```http://localhost:8280/services/RDBMSSample?wsdl```. 
This WSDL is based on the WSDL supplied in the data service configurations.

Sample request for "addEmployee" operation:
```text
POST http://localhost:8280/services/RDBMSSample HTTP/1.1
Content-Type: text/xml;charset=UTF-8
SOAPAction: "urn:addEmployee"
Content-Length: 415
Host: localhost:8280


<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
   <soapenv:Header/>
   <soapenv:Body>
        <p:addEmployee xmlns:p="http://ws.wso2.org/dataservice">
            <!--Exactly 1 occurrence-->
            <xs:employeeNumber xmlns:xs="http://ws.wso2.org/dataservice">1</xs:employeeNumber>
            <!--Exactly 1 occurrence-->
            <xs:lastName xmlns:xs="http://ws.wso2.org/dataservice">John</xs:lastName>
            <!--Exactly 1 occurrence-->
            <xs:firstName xmlns:xs="http://ws.wso2.org/dataservice">Doe</xs:firstName>
            <!--Exactly 1 occurrence-->
            <xs:email xmlns:xs="http://ws.wso2.org/dataservice">JohnDoe@gmail.com</xs:email>
            <!--Exactly 1 occurrence-->
            <xs:salary xmlns:xs="http://ws.wso2.org/dataservice">10000</xs:salary>
         </p:addEmployee>
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
[Exposing a Datasource as a Data Service](https://docs.wso2.com/display/EI640/Exposing+a+Datasource+as+a+Data+Service)

## Test cases

|      ID       | Summary |
| ------------- | ------------- |
| 9.1.1.1.1     | Insert data to a table |
| 9.1.1.1.2     | Edit data in a table |
| 9.1.1.1.3     | Read data from a table |
| 9.1.1.1.4     | Delete data from a table |