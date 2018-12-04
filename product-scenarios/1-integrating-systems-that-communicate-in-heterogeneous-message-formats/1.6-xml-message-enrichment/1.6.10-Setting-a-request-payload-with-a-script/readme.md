# 1.6.10 Setting a request payload with a script

## When to use
The WSO2 Script Mediator is used to invoke the functions of a variety of scripting languages such as JavaScript, Groovy, or Ruby.

## Sample use case
This usecase discusses which a payload consists of xml payload with the details of  set of employees. We are going to filter out the employees who are greater than age 30 and reconstruct the payload using a groovy script. 

Original Payload
```
<?xml version="1.0" encoding="UTF-8"?>
<employees>
   <employee>
      <age>25</age>
      <firstName>John</firstName>
      <lastName>Doe</lastName>
   </employee>
   <employee>
      <age>45</age>
      <firstName>Anna</firstName>
      <lastName>Smith</lastName>
   </employee>
   <employee>
      <age>35</age>
      <firstName>Peter</firstName>
      <lastName>Jones</lastName>
   </employee>
</employees>
```

Requested Payload
```
 <?xml version="1.0" encoding="UTF-8"?>
<employees>
   <employee>
      <age>45</age>
      <firstName>Anna</firstName>
      <lastName>Smith</lastName>
   </employee>
   <employee>
      <age>35</age>
      <firstName>Peter</firstName>
      <lastName>Jones</lastName>
   </employee>
</employees>
```
This can be achieved using script mediator. 

## Prerequisites
Download Groovy all dependency jar (groovy-all-2.2.0-beta-1.jar) into $EI_HOME/repository/lib and start WSO2 EI. 

## Development guidelines

Sequence and proxy Configuration

Create the following sequence and add to the proxy.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<proxy xmlns="http://ws.apache.org/ns/synapse" name="scriptProxy" startOnLoad="true" statistics="disable" trace="disable" transports="http,https">
   <target>
      <inSequence>
         <log level="full" />
         <script language="groovy">import groovy.util.XmlSlurper;
import groovy.xml.MarkupBuilder;
import groovy.xml.StreamingMarkupBuilder;

def payload = mc.getPayloadXML();
def rootNode = new XmlSlurper().parseText(payload);
rootNode.children().findAll{it.age.text().toInteger() &lt; 30 }.replaceNode {};
 
mc.setPayloadXML(groovy.xml.XmlUtil.serialize(rootNode));</script>
         <log level="full" />
         <respond />
      </inSequence>
   </target>
   <description />
</proxy>                 
```

Invoke the following proxy with the below payload using a SOAP client like SOAP UI. 
Proxy: http://localhost:8280/services/scriptProxy

```
<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope" xmlns:ser="http://services.samples" xmlns:xsd="http://services.samples/xsd">
   <soap:Header />
   <soap:Body>
      <employees>
         <employee>
            <age>25</age>
            <firstName>John</firstName>
            <lastName>Doe</lastName>
         </employee>
         <employee>
            <age>45</age>
            <firstName>Anna</firstName>
            <lastName>Smith</lastName>
         </employee>
         <employee>
            <age>35</age>
            <firstName>Peter</firstName>
            <lastName>Jones</lastName>
         </employee>
      </employees>
   </soap:Body>
</soap:Envelope>
 
```
The response will be as below after the request is enriched. 

```
<soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope" xmlns:xsd="http://services.samples/xsd" xmlns:ser="http://services.samples">
   <soap:Header/>
   <soap:Body>
      <employees>
         <employee>
            <age>45</age>
            <firstName>Anna</firstName>
            <lastName>Smith</lastName>
         </employee>
         <employee>
            <age>35</age>
            <firstName>Peter</firstName>
            <lastName>Jones</lastName>
         </employee>
      </employees>
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

| ID        | Summary                                                                                                          |
| ----------|:---------------------------------------------------------------------------------------------------------------: |
| 1.6.10.1  | Setup a payload by using a script mediator with inline groovy script                                             |
| 1.6.10.2  | Setup a payload by using a script mediator with the groovy script saved in governance registry                   |
| 1.6.10.3  | Setup a payload by using a script mediator with the groovy script saved in configuration registry                |
| 1.6.10.4  | Setup a payload using script mediator with inline javascript.                                                    |
| 1.6.10.5  | Setup a payload by using a script mediator with the javascript saved in governance registry                      |
| 1.6.10.6  | Setup a payload by using a script mediator with the javascript saved in configuration registry                   |
| 1.6.10.7  | Setup a payload using script mediator with inline ruby script.                                                   |
| 1.6.10.8  | Setup a payload by using a script mediator with the ruby script saved in governance registry                     |
| 1.6.10.9  | Setup a payload by using a script mediator with the ruby script saved in configuration registry                  |
| 1.6.10.10 | Script mediator behaviour when incorporating wrong script language                                               |
                                                          

