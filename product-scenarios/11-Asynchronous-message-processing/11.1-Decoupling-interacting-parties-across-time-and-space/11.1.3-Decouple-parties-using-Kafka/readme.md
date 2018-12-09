# 11.1.3 Decouple interacting parties using Kafka


## When to use

Apache Kafka aims to provide a unified, high-throughput, low-latency platform for handling real-time data feeds. If you
stream events from a system, make them flow it via WSO2 EI to do conditional routing or modify events in some way, to 
feed data into Kafka and read from Kafka WSO2 EI provides transport capabilities as depicted below. 

## Sample use case

Apache Kafka in general has below usecases

Messaging : 

Kafka works well as a replacement for a more traditional message broker. Kafka has better throughput and scalability. 
However, if you need proper delivery grantees, better go back to a AMQP based broker like ActiveMQ/RabbitMQ. 

Activity Tracking: 

Kafka can be used to construct a user activity tracking pipeline as a set of real-time publish-subscribe feeds. This includes
real time monitoring and offline data processing and generating reports.  

Metrics

Kafka is often used for operational monitoring data. This involves aggregating statistics from distributed applications 

Log Aggregation

Log aggregation typically collects physical log files off servers and puts them in a central place 
(a file server or HDFS perhaps) for processing. Kafka abstracts away the details of files and gives a cleaner 
abstraction of log or event data as a stream of messages.


Stream Processing

Many users of Kafka process data in processing pipelines consisting of multiple stages, where raw input data is consumed
from Kafka topics and then aggregated, enriched, or otherwise transformed into new topics for further consumption or 
follow-up processing. For the enrichment WSO2 EI can be used. 


Event Sourcing

Event sourcing (https://martinfowler.com/eaaDev/EventSourcing.html) is a style of application design where state changes
are logged as a time-ordered sequence of records

## Prerequisites

Apache Kafka deployment 
SOAP client 
HTTP backend service accepting SOAP messages and reply with  SOAP messages



## Development guidelines

Use EI tooling to develop EI artifacts 
For Kafka? 

## Deployment guidelines

Deploy Kafka in a container 


## Supported versions

This is supported in all the EI and ESB versions

## Test cases

| ID        | Summary                                                                                 |
| ----------|:---------------------------------------------------------------------------------------:|
| 11.1.3.1  |  HTTP <-> Kafka <-> HTTP                                                                |
| 11.1.3.2  |  Asynchronously call many endpoints with same request message (HTTP <-> Kafka <-> HTTP) |
| 11.1.3.3  |  Make Json/SOAP/POX/Text message invocations synchronous using Kafka                    |
