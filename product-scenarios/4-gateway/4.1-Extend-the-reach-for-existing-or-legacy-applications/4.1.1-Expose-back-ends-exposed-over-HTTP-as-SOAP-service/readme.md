# 4.1.1 Expose back-end(s) exposed over HTTP as SOAP service

## Business use case narrative

A proxy service is a virtual service that receives SOAP messages and optionally processes them before forwarding them to a 
service at a given endpoint. The proxy service allows you to perform necessary transformations to build/transform message 
to format which back-end support, before routing the message to the actual back-end.

Using Proxy Services, users can front/expose any HTTP back-end service or multiple HTTP back-end services as a SOAP service.

References:  
[Working with Proxy Services](https://docs.wso2.com/display/EI640/Working+with+Proxy+Services)


## Sub-Scenarios

4.1.1.1. [Expose a SOAP service as SOAP service using proxy service](4.1.1.1-Expose-a-SOAP-service-as-SOAP-service-using-proxy-service)  
4.1.1.2. [Expose a SOAP service, with different SOAP interface (as different operations, actions) using proxy service](4.1.1.2-Expose-a-SOAP-service-with-different-SOAP-interface-using-proxy-service)  
4.1.1.3. [Expose multiple SOAP services as a single SOAP service using proxy service](4.1.1.3-Expose-multiple-SOAP-services-as-a-single-SOAP-service-using-proxy-service)  
4.1.1.4. [Expose REST back-end as a SOAP service using proxy service](4.1.1.4-Expose-REST-back-end-as-a-SOAP-service-using-proxy-service)  
4.1.1.5. [Expose multiple REST back-ends as a single SOAP service using proxy service](4.1.1.5-Expose-multiple-REST-back-ends-as-a-single-SOAP-service-using-proxy-service)  
4.1.1.6. [Expose secured SOAP back-end as open SOAP service using proxy service](4.1.1.6-Expose-secured-SOAP-back-end-as-open-SOAP-service-using-proxy-service)  
4.1.1.7. [Expose secured REST back-end as open SOAP service using proxy service](4.1.1.7-Expose-secured-REST-back-end-as-open-SOAP-service-using-proxy-service)  
