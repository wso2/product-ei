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
| AWS | EC2 | AMI-ID | ami-024a64a6685d05041 |
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
| Operating System | Distribution | Kernel | Linux ip-10-0-1-179 4.15.0-1039-aws #41-Ubuntu SMP Wed May 8 10:43:54 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux |
System information for WSO2 Enterprise Micro Integrator in 2nd AWS CloudFormation stack.

| Class | Subclass | Description | Value |
| --- | --- | --- | --- |
| AWS | EC2 | AMI-ID | ami-024a64a6685d05041 |
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
| Operating System | Distribution | Kernel | Linux ip-10-0-1-198 4.15.0-1039-aws #41-Ubuntu SMP Wed May 8 10:43:54 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux |
System information for WSO2 Enterprise Micro Integrator in 3rd AWS CloudFormation stack.

| Class | Subclass | Description | Value |
| --- | --- | --- | --- |
| AWS | EC2 | AMI-ID | ami-024a64a6685d05041 |
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
| Operating System | Distribution | Kernel | Linux ip-10-0-1-9 4.15.0-1039-aws #41-Ubuntu SMP Wed May 8 10:43:54 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux |


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
|  CBR Proxy | 2G | 100 | 1024 | 0 | 0 | 1956.85 | 51.03 | 37.11 | 103 |  |  |
|  CBR Proxy | 2G | 100 | 1024 | 1000 | 0 | 99.39 | 1004.89 | 14.19 | 1095 |  |  |
|  CBR Proxy | 2G | 100 | 10240 | 0 | 0 | 371.84 | 269.07 | 84.4 | 491 |  |  |
|  CBR Proxy | 2G | 100 | 10240 | 1000 | 0 | 114.34 | 873.31 | 310.7 | 1047 |  |  |
|  CBR Proxy | 2G | 200 | 1024 | 0 | 0 | 2010.81 | 99.37 | 41.39 | 196 |  |  |
|  CBR Proxy | 2G | 200 | 1024 | 1000 | 0 | 198.34 | 1007.17 | 17.51 | 1103 |  |  |
|  CBR Proxy | 2G | 200 | 10240 | 0 | 0 | 383.24 | 522.09 | 150.76 | 903 |  |  |
|  CBR Proxy | 2G | 200 | 10240 | 1000 | 0 | 256.14 | 779.47 | 365.69 | 1079 |  |  |
|  CBR Proxy | 2G | 500 | 1024 | 0 | 0 | 1891.47 | 264.45 | 82.7 | 489 |  |  |
|  CBR Proxy | 2G | 500 | 1024 | 1000 | 0 | 492.39 | 1014.04 | 29.49 | 1111 |  |  |
|  CBR Proxy | 2G | 500 | 10240 | 0 | 0 | 337.74 | 1479.07 | 451.01 | 2591 |  |  |
|  CBR Proxy | 2G | 500 | 10240 | 1000 | 0 | 339.96 | 1468.27 | 295.44 | 2095 |  |  |
|  CBR SOAP Header Proxy | 2G | 100 | 1024 | 0 | 0 | 2257.64 | 44.22 | 36.22 | 98 |  |  |
|  CBR SOAP Header Proxy | 2G | 100 | 1024 | 1000 | 0 | 99.26 | 1005.65 | 15.67 | 1095 |  |  |
|  CBR SOAP Header Proxy | 2G | 100 | 10240 | 0 | 0 | 554.42 | 180.16 | 60.33 | 303 |  |  |
|  CBR SOAP Header Proxy | 2G | 100 | 10240 | 1000 | 0 | 105.49 | 946.54 | 218.34 | 1039 |  |  |
|  CBR SOAP Header Proxy | 2G | 200 | 1024 | 0 | 0 | 2365.86 | 84.44 | 38.62 | 187 |  |  |
|  CBR SOAP Header Proxy | 2G | 200 | 1024 | 1000 | 0 | 198.42 | 1007.34 | 18.14 | 1103 |  |  |
|  CBR SOAP Header Proxy | 2G | 200 | 10240 | 0 | 0 | 580.18 | 344.9 | 100.6 | 591 |  |  |
|  CBR SOAP Header Proxy | 2G | 200 | 10240 | 1000 | 0 | 240.97 | 828.88 | 351.07 | 1063 |  |  |
|  CBR SOAP Header Proxy | 2G | 500 | 1024 | 0 | 0 | 2228.08 | 224.32 | 69.59 | 399 |  |  |
|  CBR SOAP Header Proxy | 2G | 500 | 1024 | 1000 | 0 | 495.2 | 1008.77 | 20.46 | 1103 |  |  |
|  CBR SOAP Header Proxy | 2G | 500 | 10240 | 0 | 0 | 509.57 | 980.71 | 279.67 | 1687 |  |  |
|  CBR SOAP Header Proxy | 2G | 500 | 10240 | 1000 | 0 | 489.05 | 1020.83 | 324.14 | 1471 |  |  |
|  CBR Transport Header Proxy | 2G | 100 | 1024 | 0 | 0 | 3443.42 | 28.98 | 31.97 | 88 |  |  |
|  CBR Transport Header Proxy | 2G | 100 | 1024 | 1000 | 0 | 100.24 | 996.84 | 81.68 | 1103 |  |  |
|  CBR Transport Header Proxy | 2G | 100 | 10240 | 0 | 0 | 1906.03 | 52.38 | 34.08 | 96 |  |  |
|  CBR Transport Header Proxy | 2G | 100 | 10240 | 1000 | 0 | 113.62 | 879.83 | 311.74 | 1095 |  |  |
|  CBR Transport Header Proxy | 2G | 200 | 1024 | 0 | 0 | 3701.28 | 53.96 | 36.5 | 103 |  |  |
|  CBR Transport Header Proxy | 2G | 200 | 1024 | 1000 | 0 | 198.56 | 1006.84 | 24.49 | 1103 |  |  |
|  CBR Transport Header Proxy | 2G | 200 | 10240 | 0 | 0 | 2010.62 | 99.36 | 29.59 | 181 |  |  |
|  CBR Transport Header Proxy | 2G | 200 | 10240 | 1000 | 0 | 242.33 | 824.08 | 362.57 | 1055 |  |  |
|  CBR Transport Header Proxy | 2G | 500 | 1024 | 0 | 0 | 3548.52 | 140.77 | 43.79 | 232 |  |  |
|  CBR Transport Header Proxy | 2G | 500 | 1024 | 1000 | 0 | 496.34 | 1006.16 | 33.42 | 1095 |  |  |
|  CBR Transport Header Proxy | 2G | 500 | 10240 | 0 | 0 | 1781.86 | 280.74 | 56.31 | 411 |  |  |
|  CBR Transport Header Proxy | 2G | 500 | 10240 | 1000 | 0 | 608.15 | 820.88 | 374.73 | 1063 |  |  |
|  Direct Proxy | 2G | 100 | 1024 | 0 | 0 | 3504.53 | 28.49 | 32.48 | 89 |  |  |
|  Direct Proxy | 2G | 100 | 1024 | 1000 | 0 | 100.05 | 997.73 | 86.74 | 1103 |  |  |
|  Direct Proxy | 2G | 100 | 10240 | 0 | 0 | 1923.08 | 51.92 | 33.84 | 95 |  |  |
|  Direct Proxy | 2G | 100 | 10240 | 1000 | 0 | 121.23 | 824.16 | 354.45 | 1063 |  |  |
|  Direct Proxy | 2G | 200 | 1024 | 0 | 0 | 3774.88 | 52.91 | 36.59 | 103 |  |  |
|  Direct Proxy | 2G | 200 | 1024 | 1000 | 0 | 199.54 | 1002.45 | 69.5 | 1103 |  |  |
|  Direct Proxy | 2G | 200 | 10240 | 0 | 0 | 2102.23 | 94.97 | 29.07 | 178 |  |  |
|  Direct Proxy | 2G | 200 | 10240 | 1000 | 0 | 241.56 | 826.76 | 366.97 | 1071 |  |  |
|  Direct Proxy | 2G | 500 | 1024 | 0 | 0 | 3598.6 | 138.85 | 43.57 | 221 |  |  |
|  Direct Proxy | 2G | 500 | 1024 | 1000 | 0 | 495.13 | 1008.43 | 24.37 | 1103 |  |  |
|  Direct Proxy | 2G | 500 | 10240 | 0 | 0 | 1833.28 | 272.86 | 56.96 | 405 |  |  |
|  Direct Proxy | 2G | 500 | 10240 | 1000 | 0 | 675.01 | 739.17 | 416.91 | 1087 |  |  |
|  Secure Proxy | 2G | 100 | 1024 | 0 | 0 | 119.75 | 834.55 | 438.88 | 2207 |  |  |
|  Secure Proxy | 2G | 100 | 1024 | 1000 | 0 | 98.43 | 1014.51 | 24.22 | 1111 |  |  |
|  Secure Proxy | 2G | 100 | 10240 | 0 | 0 | 64.72 | 1543.92 | 469.98 | 2911 |  |  |
|  Secure Proxy | 2G | 100 | 10240 | 1000 | 0 | 67.48 | 1479.99 | 180.72 | 2095 |  |  |
|  Secure Proxy | 2G | 200 | 1024 | 0 | 0 | 119.36 | 1671.2 | 993.98 | 4703 |  |  |
|  Secure Proxy | 2G | 200 | 1024 | 1000 | 0 | 124.29 | 1606.03 | 331.05 | 2703 |  |  |
|  Secure Proxy | 2G | 200 | 10240 | 0 | 0 | 62.38 | 3195.22 | 905.32 | 5727 |  |  |
|  Secure Proxy | 2G | 200 | 10240 | 1000 | 0 | 64.03 | 3109.08 | 669.16 | 4895 |  |  |
|  Secure Proxy | 2G | 500 | 1024 | 0 | 0 | 107.56 | 4624.98 | 2267.14 | 11519 |  |  |
|  Secure Proxy | 2G | 500 | 1024 | 1000 | 0 | 111.69 | 4449.93 | 1837.59 | 10111 |  |  |
|  Secure Proxy | 2G | 500 | 10240 | 0 | 0 | 58.55 | 8443.83 | 1490.46 | 12543 |  |  |
|  Secure Proxy | 2G | 500 | 10240 | 1000 | 0 | 57 | 8692.63 | 1887.55 | 13887 |  |  |
|  XSLT Enhanced Proxy | 2G | 100 | 1024 | 0 | 0 | 1737 | 57.5 | 37.78 | 107 |  |  |
|  XSLT Enhanced Proxy | 2G | 100 | 1024 | 1000 | 0 | 99.25 | 1006.47 | 16.88 | 1103 |  |  |
|  XSLT Enhanced Proxy | 2G | 100 | 10240 | 0 | 0 | 299.09 | 334.57 | 90.1 | 587 |  |  |
|  XSLT Enhanced Proxy | 2G | 100 | 10240 | 1000 | 0 | 118.54 | 842.52 | 346.89 | 1079 |  |  |
|  XSLT Enhanced Proxy | 2G | 200 | 1024 | 0 | 0 | 1741.85 | 114.74 | 42.73 | 210 |  |  |
|  XSLT Enhanced Proxy | 2G | 200 | 1024 | 1000 | 0 | 198.41 | 1005.7 | 14.73 | 1103 |  |  |
|  XSLT Enhanced Proxy | 2G | 200 | 10240 | 0 | 0 | 296.04 | 675.55 | 185.83 | 1191 |  |  |
|  XSLT Enhanced Proxy | 2G | 200 | 10240 | 1000 | 0 | 229.59 | 869.58 | 307.35 | 1071 |  |  |
|  XSLT Enhanced Proxy | 2G | 500 | 1024 | 0 | 0 | 1636.14 | 305.79 | 93.96 | 599 |  |  |
|  XSLT Enhanced Proxy | 2G | 500 | 1024 | 1000 | 0 | 496.11 | 1006.01 | 12.81 | 1079 |  |  |
|  XSLT Enhanced Proxy | 2G | 500 | 10240 | 0 | 0 | 296.19 | 1684.41 | 416.98 | 2815 |  |  |
|  XSLT Enhanced Proxy | 2G | 500 | 10240 | 1000 | 0 | 286.87 | 1738.48 | 285.89 | 2607 |  |  |
|  XSLT Proxy | 2G | 100 | 1024 | 0 | 0 | 943.02 | 105.96 | 41.12 | 204 |  |  |
|  XSLT Proxy | 2G | 100 | 1024 | 1000 | 0 | 99.25 | 1006.41 | 15.49 | 1103 |  |  |
|  XSLT Proxy | 2G | 100 | 10240 | 0 | 0 | 139.52 | 716.24 | 236.81 | 1391 |  |  |
|  XSLT Proxy | 2G | 100 | 10240 | 1000 | 0 | 99.07 | 1008.34 | 39.23 | 1103 |  |  |
|  XSLT Proxy | 2G | 200 | 1024 | 0 | 0 | 997.15 | 200.56 | 66.06 | 395 |  |  |
|  XSLT Proxy | 2G | 200 | 1024 | 1000 | 0 | 198.13 | 1007.65 | 18.24 | 1103 |  |  |
|  XSLT Proxy | 2G | 200 | 10240 | 0 | 0 | 147.62 | 1354.31 | 434.91 | 2527 |  |  |
|  XSLT Proxy | 2G | 200 | 10240 | 1000 | 0 | 146.57 | 1362.58 | 168.98 | 1991 |  |  |
|  XSLT Proxy | 2G | 500 | 1024 | 0 | 0 | 916.76 | 545.04 | 163.26 | 1007 |  |  |
|  XSLT Proxy | 2G | 500 | 1024 | 1000 | 0 | 489.44 | 1020.11 | 37.63 | 1191 |  |  |
|  XSLT Proxy | 2G | 500 | 10240 | 0 | 0 | 130.65 | 3815.54 | 1185.62 | 7007 |  |  |
|  XSLT Proxy | 2G | 500 | 10240 | 1000 | 0 | 135.01 | 3689.54 | 874.89 | 6015 |  |  |
