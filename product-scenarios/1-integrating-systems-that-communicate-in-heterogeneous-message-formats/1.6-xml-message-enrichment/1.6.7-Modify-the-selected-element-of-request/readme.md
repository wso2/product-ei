# 1.6.7 Modify the selected element of request

## When to use
We can use WSO2 XSLT mediator to modify a selected element in the request which is sent from EI before it sent to the backend. 

## Sample use case
The use case is as follows, WSO2 EI will get a request as shown in “Current Message”. ESB needs to transform this message before sending to the backend service. The backend service require the message to be in “Required Message” format. In this usecase we need to modify ```<FirstName>``` property as ```<Name>```. 

Current Message (message received by EI):
```
<Person>
   <FirstName>John</FirstName>
   <LastName>David</LastName>
   <age>31</age>
   <gender>M</gender>
</Person>
```

Required Message (message format required by the backend service)
```
<Person>
         <Name>John</Name>
         <age>31</age>
         <gender>M</gender>
         <LastName>David</LastName>
</Person>
```

To make the transformation possible we will be using the WSO2 ESB XSLT mediator. Transformation will be done according to the xslt provided to the service.

## Prerequisites
N/A

## Development guidelines

Sequence and proxy Configuration

Create the following sequence and add to the proxy.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<proxy xmlns="http://ws.apache.org/ns/synapse" name="P1" startOnLoad="true" statistics="disable" trace="disable" transports="http,https">
   <target>
      <inSequence>
         <log level="full" />
         <xslt key="gov:/xslt/xslt1.xslt" />
         <log level="full" />
         <respond />
      </inSequence>
   </target>
   <description />
</proxy>
```

transform.xslt
```
<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
   <xsl:output method="xml" omit-xml-declaration="yes" indent="yes" />
   <xsl:template match="/">
      <Person>
         <Name>
            <xsl:value-of select="/Person/FirstName" />
         </Name>
         <age>
            <xsl:value-of select="/Person/age" />
         </age>
         <gender>
            <xsl:value-of select="/Person/gender" />
         </gender>
         <LastName>
            <xsl:value-of select="/Person/LastName" />
         </LastName>
      </Person>
   </xsl:template>
</xsl:stylesheet>
```

Save this transform.xslt in /system/governanace/xslt/ folder. 

Invoke the following proxy with the below payload using a SOAP client like SOAP UI. 
Proxy: http://localhost:8280/services/Proxy1

```
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ser="http://services.samples" xmlns:xsd="http://services.samples/xsd">
   <soapenv:Header />
   <soapenv:Body>
      <Person>
         <FirstName>John</FirstName>
         <LastName>David</LastName>
         <age>31</age>
         <gender>M</gender>
      </Person>
   </soapenv:Body>
</soapenv:Envelope>
```

The response will be as below after the response is enriched. 

```
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ser="http://services.samples" xmlns:xsd="http://services.samples/xsd">
   <soapenv:Header />
   <soapenv:Body>
      <Person>
         <Name>John</Name>
         <age>31</age>
         <gender>M</gender>
         <LastName>David</LastName>
      </Person>
   </soapenv:Body>
</soapenv:Envelope>
```


## Deployment guidelines

* We can simply deploy by copying the CAR archive into <EI_HOME>/repository/deployment/server/carbonapps directory, and it will be deployed.

<p align="center"><b> OR </b></p>

* We can create the proxy in Management Console and deploy.


## Supported versions
This is supported in all the EI and ESB versions

## Test cases

| ID        | Summary                                                                                                  |
| ----------|:-------------------------------------------------------------------------------------------------------: |
| 1.6.7.1   | Selected element of request transformation, XSLT loaded from configuration registry using xslt mediator  |
| 1.6.7.2   | Selected element of request transformation, XSLT loaded from governance registry using xslt mediator     |
| 1.6.7.3   | Selected element of request transformation, XSLT loaded from dynamic key using xslt mediator             |
                                                           

