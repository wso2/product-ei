# 13. Hosting micro-services 

## Business use case narrative

Microservices is an architectural style that structures an application as a collection of loosely coupled services,
which implement business capabilities. The benefit of decomposing an application into different smaller services is that it improves modularity.

Usually micro services are deployed on Container frameworks like Docker and Kubernetes. They are designed to start in a
very short period of time and also lightweight in size.   

WSO2 EI is packaged with [`MSF4J framework`](https://wso2.com/products/microservices-framework-for-java/) which is a rich platform 
to develop services and host. It is a lightweight tool which provides an easy way to build a wide variety of services focused on high performance.
With its Spring-native programming model, you can now write your microservices, interceptors, ExceptionMappers, 
and configuration as Spring beans and wire them up at runtime.

This means MSF4J profile of WSO2 EI having individual micro services can be deployed in containers. These containers  
can be grouped into cells to construct coarse gained services catering for meaningful business use-cases.    


This section describes what features are available with MSF4J platform and what use cases can be achieved. 

## Persona
Developer 

## Sub-Scenarios

- [13.1-Hosting-a-web-service](13.1-Hosting-a-web-service)
- [13.2-Handle-HTTP-sessions-in-micro-services](13.2-Handle-HTTP-sessions-in-micro-services)
- [13.3-Package-micro-services-with-dependencies](13.3-Package-micro-services-with-dependencies)
- [13.4-Monitor-micro-services-deployed](13.4-Monitor-micro-services-deployed)
- [13.5-Handling-request-data](13.5-Handling-request-data)
- [13.6-Request-and-response-interceptors](13.6-Request-and-response-interceptors)
- [13.7-Handling-connection-failures](13.7-Handling-connection-failures)
- [13.8-Data-handling](13.8-Data-handling)
- [13.9-Securing-services](13.9-Securing-services)
- [13.10-Templating](13.10-Templating)
