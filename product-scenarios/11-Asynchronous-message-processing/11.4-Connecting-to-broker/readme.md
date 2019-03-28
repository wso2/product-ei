# 11.4 Connecting to broker


## Business use case narrative

To provide asynchronous messaging WSO2 EI MUST connect to a Message Oriented Middleware layer (apart from usecases it uses
a shared RDBMS database). This connection should be a reliable one, because if it could not communicate with the broker, 
the whole message communication breaks down. 

This section discusses about how to connect to a broker in a reliable manner and a secured manner. WSO2 EI has recovering
mechanisms in-built if it lost the connection to broker so that you can configure. 

Further, as the common protocol is AMQP, it is not lightweight to create the connections over and over again per message publish or per
message poll. Both JMS transport and AMQP transport of WSO2 EI has caching mechanisms so that you can tune the transport 
for better performance. 

Typically, brokers like ActiveMQ are active/passive. Usual pattern to make them HA is to connect using failover mechanism.
That is, a list of brokers are specified and if first broker is not accessible, it will try the second one and so on. 
 

## Persona
Developer/Infra  

## Sub-Scenarios

 - [Using caching for performance for JMS]
 - [Securing the JMS connections]
 - [Recover JMS connection on a failure]
 - [Recover AMQP connection on a failure]
 - [Securing the AMQP connections]
 - [Handle High Availability]
 - [Connect using JMS 2.0 for extended features]
  