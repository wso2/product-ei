# 4.1.1.2 Expose a SOAP service, with different SOAP interface (as different operations, actions) using proxy service

## Business use case narrative

In this scenario, back-end SOAP service is exposed as a different SOAP service with different interfaces using proxy 
service in WSO2 ESB.

In this scenario depending on the scale of difference between original service and required service, it is required to 
process incoming message from the SOAP client and build the relevant payload and action within the ESB proxy.

## When to use

This approach can be used when required to expose a SOAP service with different interfaces, payloads compared to the 
 actual back-end SOAP service.

## Sample use-case
In this sample ESB fronts SimpleStockQuote axis2 service and expose it as CustomStockQuoteService SOAP service using a 
Proxy service. In this sample the SOAPAction, operation, namespaces, request/response payloads are different compared with
SimpleStockQuote service.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<proxy name="CustomStockQuoteService" startOnLoad="true" transports="http https" xmlns="http://ws.apache.org/ns/synapse">
    <target>
        <endpoint>
            <wsdl port="SimpleStockQuoteServiceHttpSoap11Endpoint" service="SimpleStockQuoteService" 
                    uri="http://localhost:9000/services/SimpleStockQuoteService?wsdl"/>
        </endpoint>
        <inSequence>
            <payloadFactory media-type="xml">
                <format>
                    <m0:getQuote xmlns:m0="http://services.samples">
                        <m0:request>
                            <m0:symbol>$1</m0:symbol>
                        </m0:request>
                    </m0:getQuote>
                </format>
                <args>
                    <arg evaluator="xml" expression="//cus:getQuotation/Symbol/text()" 
                            xmlns:cus="http://www.example.org/CustomStockService/"/>
                </args>
            </payloadFactory>
            <header name="Action" scope="default" value="urn:getQuote"/>
        </inSequence>
        <outSequence>
            <payloadFactory media-type="xml">
                <format>
                    <soapenv:Envelope xmlns:cus="http://www.example.org/CustomStockService/" 
                                xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
                        <soapenv:Header/>
                        <soapenv:Body>
                            <cus:getQuotationResponse>
                                <openPrice xmlns="">$1</openPrice>
                            </cus:getQuotationResponse>
                        </soapenv:Body>
                    </soapenv:Envelope>
                </format>
                <args>
                    <arg evaluator="xml" expression="//ns:getQuoteResponse/ns:return/ax21:open/text()" 
                                xmlns:ax21="http://services.samples/xsd" xmlns:ns="http://services.samples"/>
                </args>
            </payloadFactory>
            <send/>
        </outSequence>
        <faultSequence/>
    </target>
    <publishWSDL preservePolicy="true" uri="file:CustomStockService.wsdl"/>
</proxy>
```

CustomStockService.wsdl

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<wsdl:definitions xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/" xmlns:tns="http://www.example.org/CustomStockService/" xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/" xmlns:xsd="http://www.w3.org/2001/XMLSchema" name="CustomStockService" targetNamespace="http://www.example.org/CustomStockService/">
  <wsdl:types>
    <xsd:schema targetNamespace="http://www.example.org/CustomStockService/">
      <xsd:element name="getQuotation">
        <xsd:complexType>
          <xsd:sequence>
            <xsd:element name="Symbol" type="xsd:string"/>
          </xsd:sequence>
        </xsd:complexType>
      </xsd:element>
      <xsd:element name="getQuotationResponse">
        <xsd:complexType>
          <xsd:sequence>
            <xsd:element name="openPrice" type="xsd:float"/>
          </xsd:sequence>
        </xsd:complexType>
      </xsd:element>
    </xsd:schema>
  </wsdl:types>
  <wsdl:message name="getQuotationRequest">
    <wsdl:part element="tns:getQuotation" name="parameters"/>
  </wsdl:message>
  <wsdl:message name="getQuotationResponse">
    <wsdl:part element="tns:getQuotationResponse" name="parameters"/>
  </wsdl:message>
  <wsdl:portType name="CustomStockService">
    <wsdl:operation name="getQuotation">
      <wsdl:input message="tns:getQuotationRequest"/>
      <wsdl:output message="tns:getQuotationResponse"/>
    </wsdl:operation>
  </wsdl:portType>
  <wsdl:binding name="CustomStockServiceSOAP" type="tns:CustomStockService">
    <soap:binding style="document" transport="http://schemas.xmlsoap.org/soap/http"/>
    <wsdl:operation name="getQuotation">
      <soap:operation soapAction="http://www.example.org/CustomStockService/getQuotation"/>
      <wsdl:input>
        <soap:body use="literal"/>
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal"/>
      </wsdl:output>
    </wsdl:operation>
  </wsdl:binding>
  <wsdl:service name="CustomStockService">
    <wsdl:port binding="tns:CustomStockServiceSOAP" name="CustomStockServiceSOAP">
      <soap:address location="http://www.example.org/"/>
    </wsdl:port>
  </wsdl:service>
</wsdl:definitions>
```

### Prerequisites
* Deploy above proxy service by creating CustomStockQuoteService.xml file with above content and copying the file to 
```<EI_HOME>/repository/deployment/server/synapse-configs/default/proxy-services/``` directory.
* Create CustomStockService.wsdl file and copy to <EI_HOME>
* Start the Axis2 server and deploy the SimpleStockQuoteService if not already done. 
(Refer [Deploying sample back-end services](https://docs.wso2.com/display/EI640/Setting+Up+the+ESB+Samples#SettingUptheESBSamples-Deployingsampleback-endservices) 
for instructions to deploy back-end services for this sample)


Service can be invoked using SOAP client or HTTP client like Curl.
For WSDL of the SOAP service, go to ```http://localhost:8280/services/CustomStockQuoteService?wsdl```. 

Sample request for "getQuotation" operation:
```text
POST http://localhost:8280/services/CustomStockQuoteService HTTP/1.1
Content-Type: text/xml;charset=UTF-8
SOAPAction: "http://www.example.org/CustomStockService/getQuotation"

<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                                    xmlns:cus="http://www.example.org/CustomStockService/">
   <soapenv:Header/>
   <soapenv:Body>
      <cus:getQuotation>
         <Symbol>WSO2</Symbol>
      </cus:getQuotation>
   </soapenv:Body>
</soapenv:Envelope>
```

## Supported versions
This is supported in all the EI and ESB versions

## Pre-requisites


## Development guidelines


## REST API (if available)


## Deployment guidelines
Standard way of deploying a proxy service is by packaging the proxy service as a Carbon Application. Please refer 
[Creating a Proxy Service](https://docs.wso2.com/display/EI640/Creating+a+Proxy+Service) for instructions.



## Reference
[Creating a Proxy Service](https://docs.wso2.com/display/EI640/Creating+a+Proxy+Service)


## Test cases
|      ID       | Summary |
| ------------- | ------------- |
| 4.1.1.2.1	| Expose back-end soap service with different operation name, SOAP Action, namespaces and request/response payloads|
| 4.1.1.2.2	| Expose back-end soap service with different operation name, SOAP Action, namespaces and request/response payloads disable operation validation by setting "disableOperationValidation" property to true|