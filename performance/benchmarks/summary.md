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
| Operating System | Distribution | Kernel | Linux ip-10-0-1-229 4.15.0-1039-aws #41-Ubuntu SMP Wed May 8 10:43:54 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux |
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
| Operating System | Distribution | Kernel | Linux ip-10-0-1-174 4.15.0-1039-aws #41-Ubuntu SMP Wed May 8 10:43:54 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux |
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
| Operating System | Distribution | Kernel | Linux ip-10-0-1-149 4.15.0-1039-aws #41-Ubuntu SMP Wed May 8 10:43:54 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux |


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
|  CBR Proxy | 2G | 100 | 1024 | 0 | 0 | 1845.08 | 54.14 | 37.62 | 105 |  |  |
|  CBR Proxy | 2G | 100 | 1024 | 1000 | 0 | 99.42 | 1005.32 | 15.74 | 1095 |  |  |
|  CBR Proxy | 2G | 100 | 10240 | 0 | 0 | 371.67 | 269.15 | 84.57 | 491 |  |  |
|  CBR Proxy | 2G | 100 | 10240 | 1000 | 0 | 99.35 | 1005.82 | 11.36 | 1087 |  |  |
|  CBR Proxy | 2G | 200 | 1024 | 0 | 0 | 1969.82 | 101.33 | 41.86 | 198 |  |  |
|  CBR Proxy | 2G | 200 | 1024 | 1000 | 0 | 198.61 | 1006.4 | 15.51 | 1095 |  |  |
|  CBR Proxy | 2G | 200 | 10240 | 0 | 0 | 372.17 | 537.44 | 156.09 | 915 |  |  |
|  CBR Proxy | 2G | 200 | 10240 | 1000 | 0 | 198.14 | 1007.27 | 17.89 | 1103 |  |  |
|  CBR Proxy | 2G | 500 | 1024 | 0 | 0 | 1990.39 | 250.96 | 81.77 | 477 |  |  |
|  CBR Proxy | 2G | 500 | 1024 | 1000 | 0 | 495.75 | 1007.2 | 19.13 | 1103 |  |  |
|  CBR Proxy | 2G | 500 | 10240 | 0 | 0 | 343.5 | 1451.34 | 447.06 | 2511 |  |  |
|  CBR Proxy | 2G | 500 | 10240 | 1000 | 0 | 338.46 | 1474.6 | 189.43 | 2007 |  |  |
|  CBR SOAP Header Proxy | 2G | 100 | 1024 | 0 | 0 | 2219.88 | 44.95 | 36.59 | 99 |  |  |
|  CBR SOAP Header Proxy | 2G | 100 | 1024 | 1000 | 0 | 99.44 | 1004.67 | 13.95 | 1095 |  |  |
|  CBR SOAP Header Proxy | 2G | 100 | 10240 | 0 | 0 | 558.77 | 178.9 | 60.17 | 303 |  |  |
|  CBR SOAP Header Proxy | 2G | 100 | 10240 | 1000 | 0 | 99.24 | 1005.33 | 13.26 | 1095 |  |  |
|  CBR SOAP Header Proxy | 2G | 200 | 1024 | 0 | 0 | 2318.19 | 86.18 | 39.1 | 188 |  |  |
|  CBR SOAP Header Proxy | 2G | 200 | 1024 | 1000 | 0 | 198.46 | 1006.74 | 16.91 | 1103 |  |  |
|  CBR SOAP Header Proxy | 2G | 200 | 10240 | 0 | 0 | 545.08 | 366.96 | 106.98 | 607 |  |  |
|  CBR SOAP Header Proxy | 2G | 200 | 10240 | 1000 | 0 | 199.22 | 1002.89 | 4.97 | 1031 |  |  |
|  CBR SOAP Header Proxy | 2G | 500 | 1024 | 0 | 0 | 2270.88 | 219.8 | 69.42 | 395 |  |  |
|  CBR SOAP Header Proxy | 2G | 500 | 1024 | 1000 | 0 | 495.03 | 1008.56 | 19.86 | 1103 |  |  |
|  CBR SOAP Header Proxy | 2G | 500 | 10240 | 0 | 0 | 523.45 | 954.42 | 273.46 | 1623 |  |  |
|  CBR SOAP Header Proxy | 2G | 500 | 10240 | 1000 | 0 | 463.46 | 1077.34 | 79.47 | 1399 |  |  |
|  CBR Transport Header Proxy | 2G | 100 | 1024 | 0 | 0 | 3260.89 | 30.62 | 33.36 | 91 |  |  |
|  CBR Transport Header Proxy | 2G | 100 | 1024 | 1000 | 0 | 99.44 | 1004.52 | 13.62 | 1095 |  |  |
|  CBR Transport Header Proxy | 2G | 100 | 10240 | 0 | 0 | 1838.23 | 54.32 | 34.24 | 97 |  |  |
|  CBR Transport Header Proxy | 2G | 100 | 10240 | 1000 | 0 | 99.1 | 1007.16 | 16.83 | 1095 |  |  |
|  CBR Transport Header Proxy | 2G | 200 | 1024 | 0 | 0 | 3614.97 | 55.25 | 36.02 | 104 |  |  |
|  CBR Transport Header Proxy | 2G | 200 | 1024 | 1000 | 0 | 198.59 | 1006.73 | 17.72 | 1103 |  |  |
|  CBR Transport Header Proxy | 2G | 200 | 10240 | 0 | 0 | 1935.42 | 103.19 | 30.26 | 184 |  |  |
|  CBR Transport Header Proxy | 2G | 200 | 10240 | 1000 | 0 | 198.24 | 1007.67 | 16.23 | 1095 |  |  |
|  CBR Transport Header Proxy | 2G | 500 | 1024 | 0 | 0 | 3712.53 | 134.53 | 43.49 | 217 |  |  |
|  CBR Transport Header Proxy | 2G | 500 | 1024 | 1000 | 0 | 496.24 | 1006.58 | 15.59 | 1087 |  |  |
|  CBR Transport Header Proxy | 2G | 500 | 10240 | 0 | 0 | 1891.96 | 264 | 54.97 | 399 |  |  |
|  CBR Transport Header Proxy | 2G | 500 | 10240 | 1000 | 0 | 495.6 | 1007.81 | 18.19 | 1103 |  |  |
|  Direct Proxy | 2G | 100 | 1024 | 0 | 0 | 3399.38 | 29.37 | 32.19 | 88 |  |  |
|  Direct Proxy | 2G | 100 | 1024 | 1000 | 0 | 99.21 | 1006.6 | 18.83 | 1103 |  |  |
|  Direct Proxy | 2G | 100 | 10240 | 0 | 0 | 1879.44 | 53.12 | 34.18 | 96 |  |  |
|  Direct Proxy | 2G | 100 | 10240 | 1000 | 0 | 99.28 | 1005.56 | 13.53 | 1079 |  |  |
|  Direct Proxy | 2G | 200 | 1024 | 0 | 0 | 3640.91 | 54.82 | 36.58 | 104 |  |  |
|  Direct Proxy | 2G | 200 | 1024 | 1000 | 0 | 198.51 | 1006.5 | 17.15 | 1103 |  |  |
|  Direct Proxy | 2G | 200 | 10240 | 0 | 0 | 1950.4 | 102.44 | 30.08 | 184 |  |  |
|  Direct Proxy | 2G | 200 | 10240 | 1000 | 0 | 198.6 | 1005.04 | 12.38 | 1079 |  |  |
|  Direct Proxy | 2G | 500 | 1024 | 0 | 0 | 3813.15 | 130.96 | 42.11 | 209 |  |  |
|  Direct Proxy | 2G | 500 | 1024 | 1000 | 0 | 496.01 | 1006.14 | 15.88 | 1095 |  |  |
|  Direct Proxy | 2G | 500 | 10240 | 0 | 0 | 1908.42 | 262.05 | 53.67 | 397 |  |  |
|  Direct Proxy | 2G | 500 | 10240 | 1000 | 0 | 496.29 | 1006.1 | 12.6 | 1079 |  |  |
|  Secure Proxy | 2G | 100 | 1024 | 0 | 0 | 119.36 | 837.17 | 435 | 2207 |  |  |
|  Secure Proxy | 2G | 100 | 1024 | 1000 | 0 | 98.4 | 1014.85 | 25.56 | 1119 |  |  |
|  Secure Proxy | 2G | 100 | 10240 | 0 | 0 | 64 | 1559.01 | 458.95 | 2895 |  |  |
|  Secure Proxy | 2G | 100 | 10240 | 1000 | 0 | 65.71 | 1517.87 | 195.7 | 2111 |  |  |
|  Secure Proxy | 2G | 200 | 1024 | 0 | 0 | 118.57 | 1684.09 | 914.38 | 4479 |  |  |
|  Secure Proxy | 2G | 200 | 1024 | 1000 | 0 | 123.07 | 1620.04 | 341.47 | 2799 |  |  |
|  Secure Proxy | 2G | 200 | 10240 | 0 | 0 | 61.55 | 3240.55 | 920.48 | 5919 |  |  |
|  Secure Proxy | 2G | 200 | 10240 | 1000 | 0 | 63.4 | 3140.81 | 674.91 | 4991 |  |  |
|  Secure Proxy | 2G | 500 | 1024 | 0 | 0 | 111.44 | 4460.37 | 2458.74 | 12223 |  |  |
|  Secure Proxy | 2G | 500 | 1024 | 1000 | 0 | 113.97 | 4366.46 | 2007.51 | 10623 |  |  |
|  Secure Proxy | 2G | 500 | 10240 | 0 | 0 | 59.95 | 8260.39 | 1610.72 | 12991 |  |  |
|  Secure Proxy | 2G | 500 | 10240 | 1000 | 0 | 59.1 | 8366.53 | 1748.33 | 13439 |  |  |
|  XSLT Enhanced Proxy | 2G | 100 | 1024 | 0 | 0 | 1631.05 | 61.24 | 37.93 | 110 |  |  |
|  XSLT Enhanced Proxy | 2G | 100 | 1024 | 1000 | 0 | 98.99 | 1008.06 | 19.9 | 1103 |  |  |
|  XSLT Enhanced Proxy | 2G | 100 | 10240 | 0 | 0 | 290.7 | 344.12 | 91.6 | 595 |  |  |
|  XSLT Enhanced Proxy | 2G | 100 | 10240 | 1000 | 0 | 99.13 | 1007.82 | 15.8 | 1103 |  |  |
|  XSLT Enhanced Proxy | 2G | 200 | 1024 | 0 | 0 | 1719.83 | 116.19 | 43.01 | 209 |  |  |
|  XSLT Enhanced Proxy | 2G | 200 | 1024 | 1000 | 0 | 198.78 | 1005.52 | 13.46 | 1087 |  |  |
|  XSLT Enhanced Proxy | 2G | 200 | 10240 | 0 | 0 | 301.48 | 662.72 | 180.04 | 1119 |  |  |
|  XSLT Enhanced Proxy | 2G | 200 | 10240 | 1000 | 0 | 197.71 | 1009.76 | 21.59 | 1103 |  |  |
|  XSLT Enhanced Proxy | 2G | 500 | 1024 | 0 | 0 | 1718.38 | 291.04 | 93.25 | 599 |  |  |
|  XSLT Enhanced Proxy | 2G | 500 | 1024 | 1000 | 0 | 496.24 | 1006.25 | 16.5 | 1095 |  |  |
|  XSLT Enhanced Proxy | 2G | 500 | 10240 | 0 | 0 | 290.6 | 1718.93 | 426.52 | 2815 |  |  |
|  XSLT Enhanced Proxy | 2G | 500 | 10240 | 1000 | 0 | 291.68 | 1710.76 | 249.59 | 2511 |  |  |
|  XSLT Proxy | 2G | 100 | 1024 | 0 | 0 | 931.51 | 107.26 | 40.18 | 202 |  |  |
|  XSLT Proxy | 2G | 100 | 1024 | 1000 | 0 | 99.07 | 1008.84 | 19.84 | 1103 |  |  |
|  XSLT Proxy | 2G | 100 | 10240 | 0 | 0 | 142.73 | 700.79 | 231.9 | 1327 |  |  |
|  XSLT Proxy | 2G | 100 | 10240 | 1000 | 0 | 99.06 | 1008.15 | 13.94 | 1079 |  |  |
|  XSLT Proxy | 2G | 200 | 1024 | 0 | 0 | 950.95 | 210.29 | 69.09 | 401 |  |  |
|  XSLT Proxy | 2G | 200 | 1024 | 1000 | 0 | 198.73 | 1005.94 | 15.71 | 1103 |  |  |
|  XSLT Proxy | 2G | 200 | 10240 | 0 | 0 | 141.83 | 1408.75 | 448.09 | 2607 |  |  |
|  XSLT Proxy | 2G | 200 | 10240 | 1000 | 0 | 137 | 1457.12 | 193.52 | 2191 |  |  |
|  XSLT Proxy | 2G | 500 | 1024 | 0 | 0 | 934.69 | 534.73 | 161.03 | 1003 |  |  |
|  XSLT Proxy | 2G | 500 | 1024 | 1000 | 0 | 495.52 | 1007.04 | 19.9 | 1103 |  |  |
|  XSLT Proxy | 2G | 500 | 10240 | 0 | 0 | 129.45 | 3851.27 | 1149.51 | 7007 |  |  |
|  XSLT Proxy | 2G | 500 | 10240 | 1000 | 0 | 132.94 | 3747.36 | 913.69 | 6207 |  |  |
