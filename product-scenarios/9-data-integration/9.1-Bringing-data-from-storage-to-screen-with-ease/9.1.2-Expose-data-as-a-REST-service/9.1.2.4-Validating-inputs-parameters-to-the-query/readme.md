# 9.1.2.4 Validating inputs parameters to the query

## Business use case narrative

Validators are added to individual input mappings in a query. Input validation allows data services to validate the 
input parameters in a request and stop the execution of the request if the input doesn’t meet the required criteria. 


## When to use
This approach can be used to validate value passed for an input parameter against the validator defined. 

## Sample use-case
In this sample values passed for input parameters for an operation exposed by a data service is validated against the
defined validator.

The ESB profile of WSO2 Enterprise Integrator (WSO2 EI) provides a set of built-in validators for some of the most 
common use cases. It also provides an extension mechanism to write custom validators.

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
    <query id="addEmployeeQuery" useConfig="default">
        <sql>insert into Employees (employeeNumber, lastName, firstName, email, salary) values(:employeeNumber,:lastName,:firstName,:email,:salary)</sql>
        <param name="employeeNumber" ordinal="1" paramType="SCALAR" sqlType="INTEGER" type="IN"/>
        <param name="lastName" ordinal="2" paramType="SCALAR" sqlType="STRING" type="IN">
            <validateLength maximum="20" minimum="3"/>
        </param>
        <param name="firstName" ordinal="3" paramType="SCALAR" sqlType="STRING" type="IN"/>
        <param name="email" ordinal="4" paramType="SCALAR" sqlType="STRING" type="IN">
            <validatePattern pattern="(?:[a-z0-9!#$%&amp;'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&amp;'*+/=?^_`{|}~-]+)*|&quot;(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])*&quot;)@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)\])"/>
        </param>
        <param defaultValue="1500" name="salary" ordinal="5" paramType="SCALAR" sqlType="DOUBLE" type="IN"/>
    </query>
    <resource method="POST" path="employee">
        <call-query href="addEmployeeQuery">
            <with-param name="employeeNumber" query-param="employeeNumber"/>
            <with-param name="lastName" query-param="lastName"/>
            <with-param name="firstName" query-param="firstName"/>
            <with-param name="email" query-param="email"/>
            <with-param name="salary" query-param="salary"/>
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
[Validating Input Values in a Data Request](https://docs.wso2.com/display/EI640/Validating+Input+Values+in+a+Data+Request)

## Test cases

|      ID       | Summary |
| ------------- | ------------- |
| 9.1.2.4.1     | Length validators |
| 9.1.2.4.2     | Long Range validator |
| 9.1.2.4.3     | Double Range validator |
| 9.1.2.4.4     | Validators using regular expressions |
| 9.1.2.4.5     | Custom validator |