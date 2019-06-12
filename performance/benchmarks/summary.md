# WSO2 Enterprise Micro Integrator Performance Test Results

During each release, we execute various automated performance test scenarios and publish the results.

| Test Scenarios | Description |
| --- | --- |
| Direct Proxy | Passthrough proxy service |
| CBR Proxy | Routing the message based on the content of the message body |
| XSLT Proxy | Having XSLT transformations in request and response paths |
| CBR SOAP Header Proxy | Routing the message based on a SOAP header in the message payload |
| CBR Transport Header Proxy | Routing the message based on an HTTP header in the message |
| Secure Proxy | Secured proxy service |
| XSLT Enhanced Proxy | Having enhanced, Fast XSLT transformations in request and response paths |

Our test client is [Apache JMeter](https://jmeter.apache.org/index.html). We test each scenario for a fixed duration of
time. We split the test results into warmup and measurement parts and use the measurement part to compute the
performance metrics.

Test scenarios use a [Netty](https://netty.io/) based back-end service which echoes back any request
posted to it after a specified period of time.

We run the performance tests under different numbers of concurrent users, message sizes (payloads) and back-end service
delays.

The main performance metrics:

1. **Throughput**: The number of requests that the WSO2 Enterprise Micro Integrator processes during a specific time interval (e.g. per second).
2. **Response Time**: The end-to-end latency for an operation of invoking a service in WSO2 Enterprise Micro Integrator . The complete distribution of response times was recorded.

In addition to the above metrics, we measure the load average and several memory-related metrics.

The following are the test parameters.

| Test Parameter | Description | Values |
| --- | --- | --- |
| Scenario Name | The name of the test scenario. | Refer to the above table. |
| Heap Size | The amount of memory allocated to the application | 2G |
| Concurrent Users | The number of users accessing the application at the same time. | 100, 200, 500 |
| Message Size (Bytes) | The request payload size in Bytes. | 1024, 10240 |
| Back-end Delay (ms) | The delay added by the Back-end service. | 0, 1000 |

The duration of each test is **900 seconds**. The warm-up period is **300 seconds**.
The measurement results are collected after the warm-up period.

The performance tests were executed on 3 AWS CloudFormation stacks.


System information for WSO2 Enterprise Micro Integrator in 1st AWS CloudFormation stack.

| Class | Subclass | Description | Value |
| --- | --- | --- | --- |
| AWS | EC2 | AMI-ID | ami-079f96ce4a4a7e1c7 |
| AWS | EC2 | Instance Type | c5.xlarge |
| System | Processor | CPU(s) | 4 |
| System | Processor | Thread(s) per core | 2 |
| System | Processor | Core(s) per socket | 2 |
| System | Processor | Socket(s) | 1 |
| System | Processor | Model name | Intel(R) Xeon(R) Platinum 8124M CPU @ 3.00GHz |
| System | Memory | BIOS | 64 KiB |
| System | Memory | System memory | 7807988 KiB |
| System | Storage | Block Device: nvme0n1 | 8G |
| Operating System | Distribution | Release | Ubuntu 18.04.2 LTS |
| Operating System | Distribution | Kernel | Linux ip-10-0-1-52 4.15.0-1039-aws #41-Ubuntu SMP Wed May 8 10:43:54 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux |
System information for WSO2 Enterprise Micro Integrator in 2nd AWS CloudFormation stack.

| Class | Subclass | Description | Value |
| --- | --- | --- | --- |
| AWS | EC2 | AMI-ID | ami-079f96ce4a4a7e1c7 |
| AWS | EC2 | Instance Type | c5.xlarge |
| System | Processor | CPU(s) | 4 |
| System | Processor | Thread(s) per core | 2 |
| System | Processor | Core(s) per socket | 2 |
| System | Processor | Socket(s) | 1 |
| System | Processor | Model name | Intel(R) Xeon(R) Platinum 8124M CPU @ 3.00GHz |
| System | Memory | BIOS | 64 KiB |
| System | Memory | System memory | 7807988 KiB |
| System | Storage | Block Device: nvme0n1 | 8G |
| Operating System | Distribution | Release | Ubuntu 18.04.2 LTS |
| Operating System | Distribution | Kernel | Linux ip-10-0-1-175 4.15.0-1039-aws #41-Ubuntu SMP Wed May 8 10:43:54 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux |
System information for WSO2 Enterprise Micro Integrator in 3rd AWS CloudFormation stack.

| Class | Subclass | Description | Value |
| --- | --- | --- | --- |
| AWS | EC2 | AMI-ID | ami-079f96ce4a4a7e1c7 |
| AWS | EC2 | Instance Type | c5.xlarge |
| System | Processor | CPU(s) | 4 |
| System | Processor | Thread(s) per core | 2 |
| System | Processor | Core(s) per socket | 2 |
| System | Processor | Socket(s) | 1 |
| System | Processor | Model name | Intel(R) Xeon(R) Platinum 8124M CPU @ 3.00GHz |
| System | Memory | BIOS | 64 KiB |
| System | Memory | System memory | 7807988 KiB |
| System | Storage | Block Device: nvme0n1 | 8G |
| Operating System | Distribution | Release | Ubuntu 18.04.2 LTS |
| Operating System | Distribution | Kernel | Linux ip-10-0-1-232 4.15.0-1039-aws #41-Ubuntu SMP Wed May 8 10:43:54 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux |


The following are the measurements collected from each performance test conducted for a given combination of
test parameters.

| Measurement | Description |
| --- | --- |
| Error % | Percentage of requests with errors |
| Average Response Time (ms) | The average response time of a set of results |
| Standard Deviation of Response Time (ms) | The “Standard Deviation” of the response time. |
| 99th Percentile of Response Time (ms) | 99% of the requests took no more than this time. The remaining samples took at least as long as this |
| Throughput (Requests/sec) | The throughput measured in requests per second. |
| Average Memory Footprint After Full GC (M) | The average memory consumed by the application after a full garbage collection event. |

The following is the summary of performance test results collected for the measurement period.

|  Scenario Name | Heap Size | Concurrent Users | Message Size (Bytes) | Back-end Service Delay (ms) | Error % | Throughput (Requests/sec) | Average Response Time (ms) | Standard Deviation of Response Time (ms) | 99th Percentile of Response Time (ms) | WSO2 Enterprise Micro Integrator GC Throughput (%) | Average WSO2 Enterprise Micro Integrator Memory Footprint After Full GC (M) |
|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|
|  CBR Proxy | 2G | 100 | 1024 | 0 | 0 | 996.04 | 100.31 | 43.56 | 199 |  |  |
|  CBR Proxy | 2G | 100 | 1024 | 1000 | 0 | 98.7 | 1011.81 | 26.54 | 1103 |  |  |
|  CBR Proxy | 2G | 100 | 10240 | 0 | 0 | 180.79 | 552.94 | 166.92 | 999 |  |  |
|  CBR Proxy | 2G | 100 | 10240 | 1000 | 0 | 99.32 | 1005.45 | 14.84 | 1103 |  |  |
|  CBR Proxy | 2G | 200 | 1024 | 0 | 0 | 985.47 | 202.62 | 73.41 | 397 |  |  |
|  CBR Proxy | 2G | 200 | 1024 | 1000 | 0 | 196.18 | 1016.98 | 33.93 | 1111 |  |  |
|  CBR Proxy | 2G | 200 | 10240 | 0 | 0 | 184.21 | 1084.71 | 327.06 | 1903 |  |  |
|  CBR Proxy | 2G | 200 | 10240 | 1000 | 0 | 175.15 | 1139.79 | 80.15 | 1391 |  |  |
|  CBR Proxy | 2G | 500 | 1024 | 0 | 0 | 959.93 | 520.85 | 162.93 | 911 |  |  |
|  CBR Proxy | 2G | 500 | 1024 | 1000 | 0 | 487.46 | 1025.01 | 43.35 | 1199 |  |  |
|  CBR Proxy | 2G | 500 | 10240 | 0 | 0 | 165.7 | 3009.02 | 977.65 | 5311 |  |  |
|  CBR Proxy | 2G | 500 | 10240 | 1000 | 0 | 166.46 | 2989.51 | 651.01 | 4415 |  |  |
|  CBR SOAP Header Proxy | 2G | 100 | 1024 | 0 | 0 | 1130.14 | 88.4 | 43.31 | 196 |  |  |
|  CBR SOAP Header Proxy | 2G | 100 | 1024 | 1000 | 0 | 98.05 | 1019.57 | 34.83 | 1103 |  |  |
|  CBR SOAP Header Proxy | 2G | 100 | 10240 | 0 | 0 | 265.74 | 376.51 | 115.46 | 695 |  |  |
|  CBR SOAP Header Proxy | 2G | 100 | 10240 | 1000 | 0 | 98.13 | 1017.29 | 32.97 | 1103 |  |  |
|  CBR SOAP Header Proxy | 2G | 200 | 1024 | 0 | 0 | 1175.31 | 170.09 | 64.71 | 303 |  |  |
|  CBR SOAP Header Proxy | 2G | 200 | 1024 | 1000 | 0 | 195 | 1025.01 | 39.79 | 1111 |  |  |
|  CBR SOAP Header Proxy | 2G | 200 | 10240 | 0 | 0 | 276.47 | 723.19 | 211.23 | 1287 |  |  |
|  CBR SOAP Header Proxy | 2G | 200 | 10240 | 1000 | 0 | 197.24 | 1012.21 | 29.76 | 1111 |  |  |
|  CBR SOAP Header Proxy | 2G | 500 | 1024 | 0 | 0 | 1113.75 | 449.11 | 137.96 | 803 |  |  |
|  CBR SOAP Header Proxy | 2G | 500 | 1024 | 1000 | 0 | 488.06 | 1023.46 | 42.21 | 1191 |  |  |
|  CBR SOAP Header Proxy | 2G | 500 | 10240 | 0 | 0 | 263.41 | 1894.98 | 573.64 | 3311 |  |  |
|  CBR SOAP Header Proxy | 2G | 500 | 10240 | 1000 | 0 | 255.83 | 1950.8 | 341.82 | 2991 |  |  |
|  CBR Transport Header Proxy | 2G | 100 | 1024 | 0 | 0 | 1742.56 | 57.32 | 43.57 | 105 |  |  |
|  CBR Transport Header Proxy | 2G | 100 | 1024 | 1000 | 0 | 98.39 | 1015.25 | 31.94 | 1103 |  |  |
|  CBR Transport Header Proxy | 2G | 100 | 10240 | 0 | 0 | 978.41 | 102.12 | 37.08 | 194 |  |  |
|  CBR Transport Header Proxy | 2G | 100 | 10240 | 1000 | 0 | 98.4 | 1015.49 | 28.36 | 1103 |  |  |
|  CBR Transport Header Proxy | 2G | 200 | 1024 | 0 | 0 | 1797.61 | 111.17 | 47.63 | 201 |  |  |
|  CBR Transport Header Proxy | 2G | 200 | 1024 | 1000 | 0 | 195.44 | 1022.43 | 37.25 | 1103 |  |  |
|  CBR Transport Header Proxy | 2G | 200 | 10240 | 0 | 0 | 970.02 | 206.12 | 54.37 | 305 |  |  |
|  CBR Transport Header Proxy | 2G | 200 | 10240 | 1000 | 0 | 195.68 | 1018.75 | 33.8 | 1103 |  |  |
|  CBR Transport Header Proxy | 2G | 500 | 1024 | 0 | 0 | 1793.23 | 278.96 | 78.09 | 493 |  |  |
|  CBR Transport Header Proxy | 2G | 500 | 1024 | 1000 | 0 | 489.2 | 1021.52 | 36.75 | 1111 |  |  |
|  CBR Transport Header Proxy | 2G | 500 | 10240 | 0 | 0 | 890.11 | 561.71 | 109.18 | 887 |  |  |
|  CBR Transport Header Proxy | 2G | 500 | 10240 | 1000 | 0 | 491.37 | 1016.42 | 32.21 | 1103 |  |  |
|  Direct Proxy | 2G | 100 | 1024 | 0 | 0 | 1771.81 | 56.38 | 43.57 | 105 |  |  |
|  Direct Proxy | 2G | 100 | 1024 | 1000 | 0 | 98.43 | 1015.22 | 31.79 | 1103 |  |  |
|  Direct Proxy | 2G | 100 | 10240 | 0 | 0 | 984.95 | 101.44 | 37.25 | 194 |  |  |
|  Direct Proxy | 2G | 100 | 10240 | 1000 | 0 | 98.24 | 1017.67 | 30.43 | 1103 |  |  |
|  Direct Proxy | 2G | 200 | 1024 | 0 | 0 | 1856.63 | 107.46 | 46.64 | 199 |  |  |
|  Direct Proxy | 2G | 200 | 1024 | 1000 | 0 | 196.04 | 1019.71 | 34.82 | 1103 |  |  |
|  Direct Proxy | 2G | 200 | 10240 | 0 | 0 | 982.63 | 203.48 | 53.76 | 305 |  |  |
|  Direct Proxy | 2G | 200 | 10240 | 1000 | 0 | 195.89 | 1020.28 | 34.85 | 1111 |  |  |
|  Direct Proxy | 2G | 500 | 1024 | 0 | 0 | 1829.67 | 273.39 | 75.8 | 491 |  |  |
|  Direct Proxy | 2G | 500 | 1024 | 1000 | 0 | 488.29 | 1022.22 | 38.08 | 1111 |  |  |
|  Direct Proxy | 2G | 500 | 10240 | 0 | 0 | 889.13 | 562.3 | 110.22 | 891 |  |  |
|  Direct Proxy | 2G | 500 | 10240 | 1000 | 0 | 490.24 | 1018.47 | 33.13 | 1103 |  |  |
|  Secure Proxy | 2G | 100 | 1024 | 0 | 0 | 56.35 | 1769.59 | 878.78 | 4415 |  |  |
|  Secure Proxy | 2G | 100 | 1024 | 1000 | 0 | 59.56 | 1676.31 | 366.59 | 2911 |  |  |
|  Secure Proxy | 2G | 100 | 10240 | 0 | 0 | 30.48 | 3273.37 | 1139.95 | 6527 |  |  |
|  Secure Proxy | 2G | 100 | 10240 | 1000 | 0 | 30.79 | 3237.7 | 815.91 | 5599 |  |  |
|  Secure Proxy | 2G | 200 | 1024 | 0 | 0 | 55.19 | 3609.42 | 1922.94 | 9599 |  |  |
|  Secure Proxy | 2G | 200 | 1024 | 1000 | 0 | 54.43 | 3656.1 | 1304.08 | 7711 |  |  |
|  Secure Proxy | 2G | 200 | 10240 | 0 | 0 | 27.67 | 7158.99 | 2558.18 | 14655 |  |  |
|  Secure Proxy | 2G | 200 | 10240 | 1000 | 0 | 28.58 | 6931.96 | 2145.79 | 13055 |  |  |
|  Secure Proxy | 2G | 500 | 1024 | 0 | 0 | 51.96 | 9477.17 | 4743.37 | 23807 |  |  |
|  Secure Proxy | 2G | 500 | 1024 | 1000 | 0 | 51.11 | 9638.46 | 4578.26 | 23423 |  |  |
|  Secure Proxy | 2G | 500 | 10240 | 0 | 0 | 25.56 | 19238.65 | 5073.3 | 33535 |  |  |
|  Secure Proxy | 2G | 500 | 10240 | 1000 | 0 | 25.75 | 18933.5 | 5802.88 | 36351 |  |  |
|  XSLT Enhanced Proxy | 2G | 100 | 1024 | 0 | 0 | 889.33 | 112.35 | 44.99 | 203 |  |  |
|  XSLT Enhanced Proxy | 2G | 100 | 1024 | 1000 | 0 | 98.75 | 1011.46 | 25.89 | 1103 |  |  |
|  XSLT Enhanced Proxy | 2G | 100 | 10240 | 0 | 0 | 144.12 | 693.7 | 199.04 | 1207 |  |  |
|  XSLT Enhanced Proxy | 2G | 100 | 10240 | 1000 | 0 | 99.31 | 1005.13 | 13.34 | 1095 |  |  |
|  XSLT Enhanced Proxy | 2G | 200 | 1024 | 0 | 0 | 904.22 | 220.81 | 73.06 | 403 |  |  |
|  XSLT Enhanced Proxy | 2G | 200 | 1024 | 1000 | 0 | 196.23 | 1019.04 | 35.48 | 1111 |  |  |
|  XSLT Enhanced Proxy | 2G | 200 | 10240 | 0 | 0 | 144.54 | 1381.34 | 398.37 | 2495 |  |  |
|  XSLT Enhanced Proxy | 2G | 200 | 10240 | 1000 | 0 | 134.23 | 1486.1 | 200.87 | 2207 |  |  |
|  XSLT Enhanced Proxy | 2G | 500 | 1024 | 0 | 0 | 835.85 | 598.37 | 176.11 | 1111 |  |  |
|  XSLT Enhanced Proxy | 2G | 500 | 1024 | 1000 | 0 | 495.43 | 1007.93 | 22.17 | 1103 |  |  |
|  XSLT Enhanced Proxy | 2G | 500 | 10240 | 0 | 0 | 139.31 | 3573.34 | 919.32 | 6143 |  |  |
|  XSLT Enhanced Proxy | 2G | 500 | 10240 | 1000 | 0 | 138.1 | 3598.07 | 768.82 | 5727 |  |  |
|  XSLT Proxy | 2G | 100 | 1024 | 0 | 0 | 483.21 | 206.91 | 69.01 | 399 |  |  |
|  XSLT Proxy | 2G | 100 | 1024 | 1000 | 0 | 98.94 | 1008.79 | 23.22 | 1103 |  |  |
|  XSLT Proxy | 2G | 100 | 10240 | 0 | 0 | 73.36 | 1361.21 | 452.33 | 2607 |  |  |
|  XSLT Proxy | 2G | 100 | 10240 | 1000 | 0 | 75.58 | 1321.29 | 162.91 | 1903 |  |  |
|  XSLT Proxy | 2G | 200 | 1024 | 0 | 0 | 479.65 | 417.07 | 128.67 | 803 |  |  |
|  XSLT Proxy | 2G | 200 | 1024 | 1000 | 0 | 197.24 | 1012.56 | 30.33 | 1111 |  |  |
|  XSLT Proxy | 2G | 200 | 10240 | 0 | 0 | 73.63 | 2706.34 | 933.37 | 5599 |  |  |
|  XSLT Proxy | 2G | 200 | 10240 | 1000 | 0 | 71.09 | 2802.75 | 652.26 | 4607 |  |  |
|  XSLT Proxy | 2G | 500 | 1024 | 0 | 0 | 439.22 | 1137.06 | 353.37 | 2207 |  |  |
|  XSLT Proxy | 2G | 500 | 1024 | 1000 | 0 | 405.95 | 1229.43 | 142.03 | 1703 |  |  |
|  XSLT Proxy | 2G | 500 | 10240 | 0 | 0 | 59.53 | 8308.61 | 3095.59 | 17791 |  |  |
|  XSLT Proxy | 2G | 500 | 10240 | 1000 | 0 | 61.56 | 8047.21 | 2840.58 | 17023 |  |  |
