# 1.6.6 Modify the entire request

## When to use
We can use WSO2 XSLT mediator to transform the entire request which is sent from EI before it sent to the backend. 

## Sample use case
The use case is as follows, WSO2 EI will get a request as shown in “Current Message”. ESB needs to transform this message before sending to the backend service. The backend service require the message to be in “Required Message” format.

Current Message (message received by EI):
```
<TRANSACTION>
   <TRANS_TIMESTAMP>1437038356</TRANS_TIMESTAMP>
   <TRANS_ID>TR10035918373588</TRANS_ID>
   <TRANS_TYPE>ONLINE</TRANS_TYPE>
   <BANK_CODE>BNK001</BANK_CODE>
</TRANSACTION>
```

Required Message (message format required by the backend service)
```
<BANK_TR>
     <TIMESTAMP>1437038356</TIMESTAMP>
     <TRANSACTION_ID>TR10035918373588</TRANSACTION_ID>
     <TRANSACTION_TYPE>ONLINE</TRANSACTION_TYPE>
     <BANKCODE>BNK001</BANKCODE>
     <PROCESSED>TRUE</PROCESSED>
</BANK_TR>
```

To make the transformation possible we will be using the WSO2 ESB XSLT mediator. Transformation will be done according to the xslt provided to the service.

## Prerequisites
N/A

## Development guidelines

Sequence and proxy Configuration

Create the following sequence and add to the proxy.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<proxy xmlns="http://ws.apache.org/ns/synapse" name="Proxy1" startOnLoad="true" statistics="disable" trace="disable" transports="http,https">
   <target>
      <inSequence>
         <log level="full" />
         <xslt key="conf:/xslt/transform.xslt" />
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
      <BANK_TR>
         <TIMESTAMP>
            <xsl:value-of select="/TRANSACTION/TRANS_TIMESTAMP" />
         </TIMESTAMP>
         <TRANSACTION_ID>
            <xsl:value-of select="/TRANSACTION/TRANS_ID" />
         </TRANSACTION_ID>
         <TRANSACTION_TYPE>
            <xsl:value-of select="/TRANSACTION/TRANS_TYPE" />
         </TRANSACTION_TYPE>
         <BANKCODE>
            <xsl:value-of select="/TRANSACTION/BANK_CODE" />
         </BANKCODE>
         <PROCESSED>TRUE</PROCESSED>
      </BANK_TR>
   </xsl:template>
</xsl:stylesheet>
```

Save this transform.xslt in /system/config/xslt/ folder. 

Invoke the following proxy with the below payload using a SOAP client like SOAP UI. 
Proxy: http://localhost:8280/services/Proxy1

```
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ser="http://services.samples" xmlns:xsd="http://services.samples/xsd">
   <soapenv:Header />
   <soapenv:Body>
      <TRANSACTION>
         <TRANS_TIMESTAMP>1437038356</TRANS_TIMESTAMP>
         <TRANS_ID>TR10035918373588</TRANS_ID>
         <TRANS_TYPE>ONLINE</TRANS_TYPE>
         <BANK_CODE>BNK001</BANK_CODE>
      </TRANSACTION>
   </soapenv:Body>
</soapenv:Envelope>
```

The response will be as below after the response is enriched. 

```
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ser="http://services.samples" xmlns:xsd="http://services.samples/xsd">
   <soapenv:Header />
   <soapenv:Body>
      <BANK_TR>
         <TIMESTAMP>1437038356</TIMESTAMP>
         <TRANSACTION_ID>TR10035918373588</TRANSACTION_ID>
         <TRANSACTION_TYPE>ONLINE</TRANSACTION_TYPE>
         <BANKCODE>BNK001</BANKCODE>
         <PROCESSED>TRUE</PROCESSED>
      </BANK_TR>
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

| ID        | Summary                                                                  |
| ----------|:-----------------------------------------------------------------------: |
| 1.6.6.1   | Entire request transformation, XSLT loaded from configuration registry   |
| 1.6.6.2   | Entire request transformation, XSLT loaded from governance registry      |
| 1.6.6.3   | Entire request transformation, XSLT loaded as a dynamic key              |
                                                           

