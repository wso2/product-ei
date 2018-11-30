# 9.1.1.6 Nested output read

## Business use case narrative

In this scenario, it allows you to use the result of one query as an input parameter of another, and the queries 
executed in a nested query works in a transactional manner.

## When to use
This approach can be used to retrieve data from different tables from a single request in nested manner. 

## Sample use-case
In this sample user can retrieve data from different tables from a single operation.

```xml
<data name="NestedQuerySample" serviceNamespace="http://ws.wso2.org/dataservice/samples/nested_query_sample">
   <config id="default">
      <property name="org.wso2.ws.dataservice.driver">org.h2.Driver</property>
      <property name="org.wso2.ws.dataservice.protocol">jdbc:h2:file:./samples/database/DATA_SERV_SAMP</property>
      <property name="org.wso2.ws.dataservice.user">wso2ds</property>
      <property name="org.wso2.ws.dataservice.password">wso2ds</property>
      <property name="org.wso2.ws.dataservice.minpoolsize">1</property>
      <property name="org.wso2.ws.dataservice.maxpoolsize">10</property>
      <property name="org.wso2.ws.dataservice.validation_query"/>
   </config>
   <operation name="customerOrders">
      <call-query href="customerOrdersSQL"/>
   </operation>
   <query id="customerOrdersSQL" useConfig="default">
      <sql>select o.ORDERNUMBER,o.ORDERDATE, o.STATUS,o.CUSTOMERNUMBER from ORDERS o</sql>
      <result element="Orders" rowName="Order">
         <element column="ORDERNUMBER" name="Order-number"/>
         <element column="ORDERDATE" name="Last-date"/>
         <element column="STATUS" name="Status"/>
         <call-query href="customerNameSQL">
            <with-param name="customerNumber" query-param="customerNumber"/>
         </call-query>
      </result>
   </query>
   <operation name="customerName">
      <call-query href="customerNameSQL">
         <with-param name="customerNumber" query-param="customerNumber"/>
      </call-query>
   </operation>
   <query id="customerNameSQL" useConfig="default">
      <sql>select c.CUSTOMERNAME from CUSTOMERS c where c.CUSTOMERNUMBER = :customerNumber</sql>
      <result element="Customer">
         <element column="CUSTOMERNAME" name="Name"/>
      </result>
      <param name="customerNumber" sqlType="INTEGER"/>
   </query>
   <operation name="employeesInOffice">
      <call-query href="employeesInOfficeSQL">
         <with-param name="officeCode" query-param="officeCode"/>
      </call-query>
   </operation>
   <query id="employeesInOfficeSQL" useConfig="default">
      <sql>select employeeNumber, firstName, lastName, email, jobTitle from Employees where officeCode = :officeCode</sql>
      <result element="Employees" rowName="Employee">
         <element column="employeeNumber" name="employeeNumber"/>
         <element column="firstName" name="firstName"/>
         <element column="lastName" name="lastName"/>
         <element column="email" name="email"/>
         <element column="jobTitle" name="jobTitle"/>
      </result>
      <param name="officeCode" sqlType="STRING"/>
   </query>
   <operation name="listOffices">
      <call-query href="listOfficesSQL"/>
   </operation>
   <query id="listOfficesSQL" useConfig="default">
      <sql>select officeCode, addressLine1, addressLine2, city, state, country, phone from Offices</sql>
      <result element="Offices" rowName="Office">
         <element column="officeCode" name="officeCode"/>
         <element column="addressLine1" name="addressLine1"/>
         <element column="addressLine2" name="addressLine2"/>
         <element column="city" name="city"/>
         <element column="state" name="state"/>
         <element column="country" name="country"/>
         <element column="phone" name="phone"/>
         <call-query href="employeesInOfficeSQL">
            <with-param name="officeCode" query-param="officeCode"/>
         </call-query>
      </result>
   </query>
</data>
```

### Prerequisites
* Create the OFFICES and EMPLOYEES tables inside the database.
```
CREATE TABLE `OFFICES` (`OfficeCode` int(11) NOT NULL, `AddressLine1` varchar(255) NOT NULL, `AddressLine2` varchar(255) DEFAULT NULL, `City` varchar(255) DEFAULT NULL, `State` varchar(255) DEFAULT NULL, `Country` varchar(255) DEFAULT NULL, `Phone` varchar(255) DEFAULT NULL, PRIMARY KEY (`OfficeCode`));
CREATE TABLE `EMPLOYEES` (`EmployeeNumber` int(11) NOT NULL, `FirstName` varchar(255) NOT NULL, `LastName` varchar(255) DEFAULT NULL, `Email` varchar(255) DEFAULT NULL, `JobTitle` varchar(255) DEFAULT NULL, `OfficeCode` int(11) NOT NULL, PRIMARY KEY (`EmployeeNumber`,`OfficeCode`), CONSTRAINT `employees_ibfk_1` FOREIGN KEY (`OfficeCode`) REFERENCES `OFFICES` (`OfficeCode`));
```
* Insert data as below.
```
INSERT INTO OFFICES VALUES (1,"51","Glen Street","Norwich","London","United Kingdom","+441523624");
INSERT INTO OFFICES VALUES (2,"72","Rose Street","Pasadena","California","United States","+152346343");

INSERT INTO EMPLOYEES VALUES (1,"John","Gardiner","john@office1.com","Manager",1);
INSERT INTO EMPLOYEES VALUES (2,"Jane","Stewart","jane@office2.com","Head of Sales",2);
INSERT INTO EMPLOYEES VALUES (3,"David","Green","david@office1.com","Manager",1);
```
* Deploy above dataservice by creating NestedQuerySample.dbs file with above content and copying the file to 
```<EI_HOME>/repository/deployment/server/dataservices``` directory

### How to try-out sample use-case
Service can be invoked using SOAP client.
For WSDL of the SOAP service, go to ```http://localhost:8280/services/NestedQuerySample?wsdl```. 
This WSDL is based on the WSDL supplied in the data service configurations.

Sample request for "listOfficeSQLOP" operation:
```text
POST http://localhost:8280/services/NestedQuerySample HTTP/1.1
Content-Type: text/xml;charset=UTF-8
SOAPAction: "urn:listOfficeSQLOP"
Content-Length: 415
Host: localhost:8280


<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
   <soapenv:Header/>
   <soapenv:Body>
        <p:listOfficeSQLOP xmlns:p="http://ws.wso2.org/dataservice">
            <!--Exactly 1 occurrence-->
            <xs:OfficeCode xmlns:xs="http://ws.wso2.org/dataservice">1</xs:OfficeCode>
        </p:listOfficeSQLOP>
   </soapenv:Body>
</soapenv:Envelope>
```
Sample Response

``` text
<Entries xmlns="http://ws.wso2.org/dataservice">
   <Entry>
      <OfficeCode>1</OfficeCode>
      <AddressLine1>51</AddressLine1>
      <AddressLine2>Glen Street</AddressLine2>
      <City>Norwich</City>
      <State>London</State>
      <Country>United Kingdom</Country>
      <Phone>+441523624</Phone>
      <Entries>
         <Entry>
            <EmployeeNumber>1</EmployeeNumber>
            <FirstName>John</FirstName>
            <LastName>Gardiner</LastName>
            <Email>john@office1.com</Email>
            <JobTitle>Manager</JobTitle>
            <OfficeCode>1</OfficeCode>
         </Entry>
         <Entry>
            <EmployeeNumber>3</EmployeeNumber>
            <FirstName>David</FirstName>
            <LastName>Green</LastName>
            <Email>david@office1.com</Email>
            <JobTitle>Manager</JobTitle>
            <OfficeCode>1</OfficeCode>
         </Entry>
      </Entries>
   </Entry>
</Entries>
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
[Defining Nested Queries](https://docs.wso2.com/display/EI640/Defining+Nested+Queries)

## Test cases

|      ID       | Summary |
| ------------- | ------------- |
| 9.1.1.6.1     | Retrieving data using nested queries which maps two different tables |