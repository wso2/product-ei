# 1.6.9 Extracting elements from a payload

## When to use
We can use WSO2 XSLT mediator to extract elements from a payload and configure a new request before it is sent to the backend. 

## Sample use case
The use case is as follows, WSO2 EI will get a payload and it will modify the payload by extracting its elements and configure a new payload before it is sent to the backend. 

Original Payload
```
<soapenv:Envelope
    xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
    <soapenv:Body>
        <Orders>
            <order number="1">
                <orderid>CF201</orderid>
                <ordername>Samsung Tab</ordername>
                <unitprice>USD 234.00</unitprice>
                <quantity>1</quantity>
            </order>
            <order number="2">
                <orderid>CB203</orderid>
                <ordername>Philips Shaver</ordername>
                <unitprice>USD 35.00</unitprice>
                <quantity>2</quantity>
            </order>
            <order number="3">
                <orderid>SD506</orderid>
                <ordername>Samsung S6</ordername>
                <unitprice>USD 350.00</unitprice>
                <quantity>1</quantity>
            </order>
            <order number="4">
                <orderid>NW304</orderid>
                <ordername>Motorola G6</ordername>
                <unitprice>USD 250.00</unitprice>
                <quantity>1</quantity>
            </order>
            <order number="5">
                <orderid>CB210</orderid>
                <ordername>Philips LED TV</ordername>
                <unitprice>USD 245.00</unitprice>
                <quantity>1</quantity>
            </order>
            <order number="6">
                <orderid>SE670</orderid>
                <ordername>Samsung DVD Player</ordername>
                <unitprice>USD 200.00</unitprice>
                <quantity>2</quantity>
            </order>
            <order number="7">
                <orderid>DF506</orderid>
                <ordername>Philips DVD Player</ordername>
                <unitprice>USD 210.00</unitprice>
                <quantity>1</quantity>
            </order>
        </Orders>
    </soapenv:Body>
</soapenv:Envelope>
 
```

Requested Payload
```
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
   <soapenv:Body>
      <Order xmlns:fn="http://www.w3.org/2005/02/xpath-functions">
         <ordername>Samsung Tab</ordername>
         <ordername>Philips Shaver</ordername>
         <ordername>Samsung S6</ordername>
         <ordername>Motorola G6</ordername>
         <ordername>Philips LED TV</ordername>
         <ordername>Samsung DVD Player</ordername>
         <ordername>Philips DVD Player</ordername>
      </Order>
   </soapenv:Body>
</soapenv:Envelope>
```
This can be achieved using xslt mediator. 

## Prerequisites
N/A

## Development guidelines

Sequence and proxy Configuration

Create the following sequence and add to the proxy.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<proxy xmlns="http://ws.apache.org/ns/synapse"
       name="Proxy1"
       startOnLoad="true"
       statistics="disable"
       trace="disable"
       transports="http,https">
   <target>
      <inSequence>
         <log level="full"/>
         <xslt key="gov:/xslt/req1.xslt"/>
         <log level="full"/>
         <respond/>
      </inSequence>
   </target>
   <description/>
</proxy>
                                
```

transform.xslt
```
<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ax21="http://services.samples/xsd" xmlns:fn="http://www.w3.org/2005/02/xpath-functions" xmlns:ns="http://services.samples" version="2.0">
   <xsl:output method="xml" omit-xml-declaration="yes" indent="yes" />
   <xsl:template match="node()|@*">
      <xsl:copy>
         <xsl:apply-templates select="node()|@*" />
      </xsl:copy>
   </xsl:template>
   <xsl:template match="ax21:name" />
   <xsl:template match="ax21:symbol" />
</xsl:stylesheet>
```

Save this transform.xslt in /system/governanace/xslt/ folder. 

Invoke the following proxy with the below payload using a SOAP client like SOAP UI. 
Proxy: http://localhost:8280/services/Proxy1

```
<soapenv:Envelope
    xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
    <soapenv:Body>
        <Orders>
            <order number="1">
                <orderid>CF201</orderid>
                <ordername>Samsung Tab</ordername>
                <unitprice>USD 234.00</unitprice>
                <quantity>1</quantity>
            </order>
            <order number="2">
                <orderid>CB203</orderid>
                <ordername>Philips Shaver</ordername>
                <unitprice>USD 35.00</unitprice>
                <quantity>2</quantity>
            </order>
            <order number="3">
                <orderid>SD506</orderid>
                <ordername>Samsung S6</ordername>
                <unitprice>USD 350.00</unitprice>
                <quantity>1</quantity>
            </order>
            <order number="4">
                <orderid>NW304</orderid>
                <ordername>Motorola G6</ordername>
                <unitprice>USD 250.00</unitprice>
                <quantity>1</quantity>
            </order>
            <order number="5">
                <orderid>CB210</orderid>
                <ordername>Philips LED TV</ordername>
                <unitprice>USD 245.00</unitprice>
                <quantity>1</quantity>
            </order>
            <order number="6">
                <orderid>SE670</orderid>
                <ordername>Samsung DVD Player</ordername>
                <unitprice>USD 200.00</unitprice>
                <quantity>2</quantity>
            </order>
            <order number="7">
                <orderid>DF506</orderid>
                <ordername>Philips DVD Player</ordername>
                <unitprice>USD 210.00</unitprice>
                <quantity>1</quantity>
            </order>
        </Orders>
    </soapenv:Body>
</soapenv:Envelope>
 
```

The response will be as below after the request is enriched. 

```
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
   <soapenv:Body>
      <Order xmlns:fn="http://www.w3.org/2005/02/xpath-functions">
         <ordername>Samsung Tab</ordername>
         <ordername>Philips Shaver</ordername>
         <ordername>Samsung S6</ordername>
         <ordername>Motorola G6</ordername>
         <ordername>Philips LED TV</ordername>
         <ordername>Samsung DVD Player</ordername>
         <ordername>Philips DVD Player</ordername>
      </Order>
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

| ID        | Summary                                                                                                          |
| ----------|:---------------------------------------------------------------------------------------------------------------: |
| 1.6.9.1   | Extracting elements based on the property defined in xslt loaded from config registry using xslt mediator        |
| 1.6.9.2   | Extracting elements based on the property defined in xslt loaded from governance registry using xslt mediator    |
| 1.6.9.3   | Extracting elements based on the property defined in xslt loaded as dynamic key using xslt mediator              |
                                                           

