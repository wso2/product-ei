# 4. Gateway

## Business use case narrative

Gateway pattern is used to expose business APIs, exposing business functionalities, securely accessible by external and internal consumers.
In technical terms, APIs provide an abstract layer for the internal business services to cater consumer demand.  

Gateways functions as single point entry for expose backend services (microservices) as proxy services / APIs aggregating the backend services into a unified services layer and 
simplify the backend service contracts by introducing security policies for authentication and authorization appropriately, 
only allowing authorized consumers to access services. 

Above can be achieved by deploying a WSO2 ESB in a “DMZ” (demilitarized zone) and exposing the services to external 
service consumers. The DMZ ESB pre-processes service requests coming from the public and routes only valid and authorized 
messages to the actual service platforms.

Pre-processing steps typically consist of message validation, filtering, and transformation, orchestration, etc.


## Sub-Scenarios
- [4.1	Extend the reach for existing applications / legacy applications](4.1-Extend-the-reach-for-existing-or-legacy-applications)
- [4.2	ESB as the security gateway](4.2-ESB-as-the-security-gateway)
- [4.3	Using ESB as caching layer](4.3-Using-ESB-as-caching-layer)
- [4.4	Protecting back-end by request throttling](4.4-Protecting-back-end-by-request-throttling)
- [4.5	Request validation](4.5-Request-validation)
- [4.6	Request load balancing](4.6-Request-load-balancing)


