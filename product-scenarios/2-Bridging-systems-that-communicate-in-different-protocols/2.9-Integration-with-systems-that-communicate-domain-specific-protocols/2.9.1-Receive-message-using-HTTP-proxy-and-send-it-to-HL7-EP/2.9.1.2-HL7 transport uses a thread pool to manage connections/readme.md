# 2.9.1.2 HL7 transport uses a thread pool to manage connections

## When to use
HL7 transport uses a thread pool to manage connections.
The HL7 transport uses a thread pool to manage connections. A larger thread pool provides greater performance, because the transport can process more messages simultaneously, but it also uses more memory. 

## Sample use-case

## Supported versions

## Pre-requisites
The HL7 transport uses a thread pool to manage connections.ou can add the following properties to the proxy service to configure the thread pool to suit your environment:

- transport.hl7.corePoolSize: the core number of threads in the pool. Default is 10.
- transport.hl7.maxPoolSize: the maximum number of threads that can be in the pool. Default is 20.
- transport.hl7.idleThreadKeepAlive: the time in milliseconds to keep idle threads alive before releasing them. Default is 10000 (10 seconds). 

## Development guidelines

## REST API (if available)
N/A

## Deployment guidelines

## Test cases

| ID | Summary |
| ------------- | ------------- |
| 2.9.1.2.1  | CorePoolSize set as default: the core number of threads in the pool. Default is 10     |
| 2.9.1.2.2 | Max pool size set to maxium size.maxPoolSize: the maximum number of threads that can be in the pool. Default is 20          |
| 2.9.1.2.3  | Use idleThreadKeepAlive propety as 10 seconds. idleThreadKeepAlive: the time in milliseconds to keep idle threads alive before releasing them. Default is 10000 (10 seconds)                |
| 2.9.1.2.4| Failover due to backend is not started and then connection refused. |
| 2.9.1.2.5 |  Backend is not responding retry after given timeout period|