# 1.6.14 Group Payload By a Distinct Value

## When to use
We can use WSO2 XSLT mediator to modify the paylaod by group it by a distinct value. 

## Sample use case
In this usecase we are going to group the payload by subject ID. 

Original Payload
```
<students>
    <student>
        <studentId>100</studentId>
        <subject>Maths</subject>
    </student>
    <student>
        <studentId>102</studentId>
        <subject>Science</subject>
    </student>
    <student>
        <studentId>101</studentId>
        <subject>English</subject>
    </student>
    <student>
        <studentId>100</studentId>
        <subject>English</subject>
    </student>
    <student>
        <studentId>100</studentId>
        <subject>Science</subject>
    </student>
</students>
```

XSLT mediator
```
<xsl:stylesheet version="1.0" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template match="/">
        <students>
            <xsl:for-each select="//student/studentId[not(.=preceding::*)]">
                <xsl:variable name="studentId" select="."/>
                <student>
                    <studentId>
                        <xsl:value-of select="$studentId"/>
                    </studentId>
                    <xsl:for-each select="//student[studentId=$studentId]/subject">
                        <subject>
                            <xsl:value-of select="."/>
                        </subject>
                    </xsl:for-each>
                </student>
            </xsl:for-each>
        </students>
    </xsl:template>
</xsl:stylesheet>
```

Transformed XML
```
<students xmlns:fn="http://www.w3.org/2005/xpath-functions">
    <student>
        <studentId>100</studentId>
        <subject>Maths</subject>
        <subject>English</subject>
        <subject>Science</subject>
    </student>
    <student>
        <studentId>102</studentId>
        <subject>Science</subject>
    </student>
    <student>
        <studentId>101</studentId>
        <subject>English</subject>
    </student>
</students>
```

## Prerequisites
N/A

## Development guidelines

1. Save the above mentioned XSLT mediator in conf:/xslt folder as mediator.xslt. 

2. Create the following sequence and add to the proxy.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<proxy xmlns="http://ws.apache.org/ns/synapse" name="P1" startOnLoad="true" statistics="disable" trace="disable" transports="http,https">
   <target>
      <inSequence>
         <log level="full" />
         <xslt key="conf:/xslt/mediator.xslt" />
         <log level="full" />
         <respond />
      </inSequence>
   </target>
   <description />
</proxy>
```

3. Send the following payload using SOAP client such as SOAP UI. 
Proxy: http://localhost:8280/services/P1
```
<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope">
   <soap:Header />
   <soap:Body>
      <students>
         <student>
            <studentId>100</studentId>
            <subject>Maths</subject>
         </student>
         <student>
            <studentId>102</studentId>
            <subject>Science</subject>
         </student>
         <student>
            <studentId>101</studentId>
            <subject>English</subject>
         </student>
         <student>
            <studentId>100</studentId>
            <subject>English</subject>
         </student>
         <student>
            <studentId>100</studentId>
            <subject>Science</subject>
         </student>
      </students>
   </soap:Body>
</soap:Envelope>
```

4. The payload will be enriched as below. 
```
<soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope">
   <soap:Header/>
   <soap:Body>
      <students xmlns:fn="http://www.w3.org/2005/xpath-functions">
         <student>
            <studentId>100</studentId>
            <subject>Maths</subject>
            <subject>English</subject>
            <subject>Science</subject>
         </student>
         <student>
            <studentId>102</studentId>
            <subject>Science</subject>
         </student>
         <student>
            <studentId>101</studentId>
            <subject>English</subject>
         </student>
      </students>
   </soap:Body>
</soap:Envelope>
```


## Deployment guidelines

* We can simply deploy by copying the CAR archive into <EI_HOME>/repository/deployment/server/carbonapps directory, and it will be deployed.

<p align="center"><b> OR </b></p>

* We can create the proxy in Management Console and deploy.


## Supported versions
This is supported in all the EI and ESB versions

## Test cases

| ID        | Summary              |
| ----------|:-------------------: |
| 1.6.14.1   | Using XSLT mediator  |
                                                           

