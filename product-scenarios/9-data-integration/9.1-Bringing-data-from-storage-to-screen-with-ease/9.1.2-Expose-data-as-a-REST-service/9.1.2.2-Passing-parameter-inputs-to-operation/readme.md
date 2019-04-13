# 9.1.2.2 Passing parameter inputs to operation

## Business use case narrative

In this scenario, the input parameters for a query is passed to execute the query in the request rather than define that
in the service deployment time.

## When to use
This approach can be used to execute database queries by passing parameters in the request it self in the runtime, 
rather than configuring in the deployment time.

## Sample use-case
In this sample data service SQL queries on data in an RDBMS datasource can be executed as
a SOAP operations by passing parameters in the request.

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
   <resource method="PUT" path="employee">
      <call-query href="setEmployeeSalaryQuery">
          <with-param name="employeeNumber" query-param="employeeNumber"/>
          <with-param name="salary" query-param="salary"/>
      </call-query>
   </resource>
   <resource method="POST" path="employee">
      <call-query href="addEmployeeQuery">
         <with-param name="employeeNumber" query-param="employeeNumber"/>
         <with-param name="lastName" query-param="lastName"/>
         <with-param name="firstName" query-param="firstName"/>
         <with-param name="email" query-param="email"/>
         <with-param name="salary" query-param="salary"/>
      </call-query>
   </resource>
   <resource method="DELETE" path="employee">
      <call-query href="deleteEmployeeQuery">
         <with-param name="employeeNumber" query-param="employeeNumber"/>
      </call-query>
   </resource>
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
Service can be invoked using REST client.

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
| 9.1.2.2.1     | Pass parameter as query params |
| 9.1.2.2.2     | Pass parameters in the request body in XML |
| 9.1.2.2.3     | Pass parameters in the request body in JSON |
| 9.1.2.2.4     | Pass parameters in the url mapping |
