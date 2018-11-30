# 9.1.1.3 Selectively map result data columns to output response

## Business use case narrative

In this scenario, the output fields can be mapped selectively for the data columns retrieved from database. The XML 
mapping can be defined. The root element, row element, and each data column name can be configured for the result set.

## When to use
This approach can be used to execute database queries and selectively map the data column to the response.

## Sample use-case
In this sample data service SQL queries on data in an RDBMS datasource can be executed as
a SOAP operations and retrieve the response according to the configured output mapping.

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
    <operation name="selectEmployee">
         <call-query href="selectEmployeeQuery"/>
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

Sample request for "selectEmployee" operation:
```text
POST http://localhost:8280/services/RDBMSSample HTTP/1.1
Content-Type: text/xml;charset=UTF-8
SOAPAction: "urn:selectEmployee"
Content-Length: 415
Host: localhost:8280


<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
   <soapenv:Header/>
   <soapenv:Body>
      <p:selectEmployee xmlns:p="http://ws.wso2.org/dataservice">
      </p:selectEmployee>
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
| 9.1.1.3.1     | Add XML output mappings |