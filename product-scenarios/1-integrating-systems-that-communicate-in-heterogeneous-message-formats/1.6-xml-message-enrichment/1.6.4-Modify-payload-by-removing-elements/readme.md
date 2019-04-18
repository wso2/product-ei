# 1.6.4 Modify payload by removing elements

## When to use
We can use set of WSO2 EI mediators to modify the payload by removing elements of it before sending it to the endpoint. It can remove soap header, body and elements in the body. This can be acheived using enrich mediaotr, script mediator and XSLT mediator. 


## Sample use case
Assume that we have our original payload to be sent as below. 

original payload
```
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ser="http://services.samples" xmlns:xsd="http://services.samples/xsd">
   <soapenv:Header />
   <soapenv:Body>
      <company>WSO2</company>
      <first_name>Jay</first_name>
      <last_name>Cleark</last_name>
   </soapenv:Body>
</soapenv:Envelope>
```

Before sending this to the endpoind, we need to remove the first element of the payload body, which is ```<company>WSO2</company>```. 

Required payload
```
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ser="http://services.samples" xmlns:xsd="http://services.samples/xsd">
   <soapenv:Header />
   <soapenv:Body>
      <first_name>Jay</first_name>
      <last_name>Cleark</last_name>
   </soapenv:Body>
</soapenv:Envelope>
```

This can be acheived using a mediator. In this example we will look in to get this done using a script mediator. 

mediator
```
<?xml version="1.0" encoding="UTF-8"?>
<script xmlns="http://ws.apache.org/ns/synapse" language="js"><![CDATA[mc.getEnvelope().getBody().getFirstElement().detach();]]></script>
```

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
         <script language="js">mc.getEnvelope().getBody().getFirstElement().detach();</script>
         <log level="full" />
         <respond />
      </inSequence>
   </target>
   <description />
</proxy>
```

Invoke the following proxy with the below payload using a SOAP client like SOAP UI. 
Proxy: http://localhost:8280/services/Proxy1

```
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ser="http://services.samples" xmlns:xsd="http://services.samples/xsd">
   <soapenv:Header />
   <soapenv:Body>
      <company>WSO2</company>
      <first_name>Jay</first_name>
      <last_name>Cleark</last_name>
   </soapenv:Body>
</soapenv:Envelope>
```

Above proxy will remove the first element of the body which is ```<company>``` before sending this to the endpoint. 

You will see the following respond which is the actual request enriched. 

```
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ser="http://services.samples" xmlns:xsd="http://services.samples/xsd">
   <soapenv:Header />
   <soapenv:Body>
      <first_name>Jay</first_name>
      <last_name>Cleark</last_name>
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

| ID        | Summary                                     |
| ----------|:------------------------------------------: |
| 1.6.4.1   | Using enrich mediator                       |
| 1.6.4.2   | Using XSLT mediator                         |
| 1.6.4.3   | Using script mediator                       |
                                                           

