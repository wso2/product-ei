# 1.6.15 Adding namespace and prefix to tags inside payload.

## When to use
We can use WSO2 XSLT mediator to add namespace and prefix to tags inside payload.

## Sample use case
In this usecase it shows how to add namespace and prefix to tags inside payload in wso2 ESB using XSLT Mediator. Our original payload as below, and we need to add we ex0 prefix to catalog tag and namespace must be http://sample.com/test//blog/Sample.

Original Payload
```
<soapenv:Envelope xmlns:soapenv="http://www.w3.org/2003/05/soap-envelope">  
   <soapenv:Body>  
    <catalog>       
       <product>    
          <number>100</number>    
          <name>BaseBall</name>    
          <colourChoices>Black</colourChoices>    
          <price>$50</price>  
       </product> 
    </catalog>  
</soapenv:Body>
</soapenv:Envelope>
```

XSLT mediator
```
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">  
    <xsl:output indent="yes"/>  
    <xsl:strip-space elements="*"/>  
    <!--match all the nodes and attributes-->  
    <xsl:template match="node()|@*">  
        <xsl:copy>  
            <xsl:apply-templates select="node()|@*">  
        </xsl:apply-templates></xsl:copy>  
    </xsl:template>  
    <!--Select the element need to be apply the namespace and prefix -->  
    <xsl:template match="catalog">  
        <!--Define the namespace with prefix ns0 -->  
        <xsl:element name="ex0:{name()}" namespace="http://sample.com/test/blog/Sample">  
            <!--apply to above selected node-->  
            <xsl:apply-templates select="node()|@*">  
        </xsl:apply-templates></xsl:element>  
    </xsl:template>  
</xsl:stylesheet>  
```

Transformed XML
```
<soapenv:Envelope xmlns:soapenv="http://www.w3.org/2003/05/soap-envelope">
   <soapenv:Body>
      <ex0:catalog xmlns:ex0="http://sample.com/test/blog/Sample">
         <product>
            <number>100</number>
            <name>BaseBall</name>
            <colourChoices>Black</colourChoices>
            <price>$50</price>
         </product>
      </ex0:catalog>
   </soapenv:Body>
</soapenv:Envelope>
```

## Prerequisites
N/A

## Development guidelines

1. Save the above mentioned XSLT mediator in conf:/xslt folder as mediator.xslt. 

2. Create the following sequence and add to the proxy.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<proxy xmlns="http://ws.apache.org/ns/synapse"
       name="m3"
       startOnLoad="true"
       statistics="disable"
       trace="disable"
       transports="http,https">
   <target>
      <inSequence>
         <log level="full"/>
         <xslt key="conf:/xslt/mediator3.xslt"/>
         <log level="full"/>
         <respond/>
      </inSequence>
   </target>
   <description/>
</proxy>
                                
```

3. Send the following payload using SOAP client such as SOAP UI. 
Proxy: http://localhost:8280/services/m3
```
<soapenv:Envelope xmlns:soapenv="http://www.w3.org/2003/05/soap-envelope">  
   <soapenv:Body>  
    <catalog>       
       <product>    
          <number>100</number>    
          <name>BaseBall</name>    
          <colourChoices>Black</colourChoices>    
          <price>$50</price>  
       </product> 
    </catalog>  
</soapenv:Body>
</soapenv:Envelope>
```

4. The enriched payload will be as below. 
```
<soapenv:Envelope xmlns:soapenv="http://www.w3.org/2003/05/soap-envelope">
   <soapenv:Body>
      <ex0:catalog xmlns:ex0="http://sample.com/test/blog/Sample">
         <product>
            <number>100</number>
            <name>BaseBall</name>
            <colourChoices>Black</colourChoices>
            <price>$50</price>
         </product>
      </ex0:catalog>
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

| ID        | Summary              |
| ----------|:-------------------: |
| 1.6.15.1   | Using XSLT mediator  |
                                                           

