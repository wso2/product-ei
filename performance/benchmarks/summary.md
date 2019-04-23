# WSO2 Enterprise Micro Integrator Performance Test Results

During each release, we execute various automated performance test scenarios and publish the results.

| Test Scenarios | Description |
| --- | --- |
| Direct Proxy | Passthrough proxy service |
| CBR Proxy | Routing the message based on the content of the message body |
| XSLT Proxy | Having XSLT transformations in request and response paths |
| CBR SOAP Header Proxy | Routing the message based on a SOAP header in the message payload |
| CBR Transport Header Proxy | Routing the message based on an HTTP header in the message |
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
| Heap Size | The amount of memory allocated to the application | 512M |
| Concurrent Users | The number of users accessing the application at the same time. | 100, 200, 500 |
| Message Size (Bytes) | The request payload size in Bytes. | 1024, 10240 |
| Back-end Delay (ms) | The delay added by the Back-end service. | 0, 1000 |

The duration of each test is **900 seconds**. The warm-up period is **300 seconds**.
The measurement results are collected after the warm-up period.

The performance tests were executed on 3 AWS CloudFormation stacks.


System information for WSO2 Enterprise Micro Integrator in 1st AWS CloudFormation stack.

| Class | Subclass | Description | Value |
| --- | --- | --- | --- |
| AWS | EC2 | AMI-ID | ami-0fba9b33b5304d8b4 |
| AWS | EC2 | Instance Type | c5.xlarge |
| System | Processor | CPU(s) | 4 |
| System | Processor | Thread(s) per core | 2 |
| System | Processor | Core(s) per socket | 2 |
| System | Processor | Socket(s) | 1 |
| System | Processor | Model name | Intel(R) Xeon(R) Platinum 8124M CPU @ 3.00GHz |
| System | Memory | BIOS | 64 KiB |
| System | Memory | System memory | 7625 MiB |
| System | Storage | Block Device: nvme0n1 | 8G |
| Operating System | Distribution | Release | Ubuntu 18.04.2 LTS |
| Operating System | Distribution | Kernel | Linux ip-10-0-1-104 4.15.0-1035-aws #37-Ubuntu SMP Mon Mar 18 16:15:14 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux |
System information for WSO2 Enterprise Micro Integrator in 2nd AWS CloudFormation stack.

| Class | Subclass | Description | Value |
| --- | --- | --- | --- |
| AWS | EC2 | AMI-ID | ami-0fba9b33b5304d8b4 |
| AWS | EC2 | Instance Type | c5.xlarge |
| System | Processor | CPU(s) | 4 |
| System | Processor | Thread(s) per core | 2 |
| System | Processor | Core(s) per socket | 2 |
| System | Processor | Socket(s) | 1 |
| System | Processor | Model name | Intel(R) Xeon(R) Platinum 8124M CPU @ 3.00GHz |
| System | Memory | BIOS | 64 KiB |
| System | Memory | System memory | 7625 MiB |
| System | Storage | Block Device: nvme0n1 | 8G |
| Operating System | Distribution | Release | Ubuntu 18.04.2 LTS |
| Operating System | Distribution | Kernel | Linux ip-10-0-1-16 4.15.0-1035-aws #37-Ubuntu SMP Mon Mar 18 16:15:14 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux |
System information for WSO2 Enterprise Micro Integrator in 3rd AWS CloudFormation stack.

| Class | Subclass | Description | Value |
| --- | --- | --- | --- |
| AWS | EC2 | AMI-ID | ami-0fba9b33b5304d8b4 |
| AWS | EC2 | Instance Type | c5.xlarge |
| System | Processor | CPU(s) | 4 |
| System | Processor | Thread(s) per core | 2 |
| System | Processor | Core(s) per socket | 2 |
| System | Processor | Socket(s) | 1 |
| System | Processor | Model name | Intel(R) Xeon(R) Platinum 8124M CPU @ 3.00GHz |
| System | Memory | BIOS | 64 KiB |
| System | Memory | System memory | 7625 MiB |
| System | Storage | Block Device: nvme0n1 | 8G |
| Operating System | Distribution | Release | Ubuntu 18.04.2 LTS |
| Operating System | Distribution | Kernel | Linux ip-10-0-1-242 4.15.0-1035-aws #37-Ubuntu SMP Mon Mar 18 16:15:14 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux |


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
|  CBR Proxy | 512M | 100 | 1024 | 0 | 0 | 2341.2 | 42.63 | 41.38 | 196 | 95.48 | 28.121 |
|  CBR Proxy | 512M | 100 | 1024 | 1000 | 0 | 99.67 | 1002.72 | 6.19 | 1019 | 99.83 | 28.172 |
|  CBR Proxy | 512M | 100 | 10240 | 0 | 0 | 477.16 | 209.54 | 91.53 | 479 | 86.78 | 89.654 |
|  CBR Proxy | 512M | 100 | 10240 | 1000 | 0 | 114.61 | 870.85 | 289.14 | 1071 | 97.69 | 84.385 |
|  CBR Proxy | 512M | 200 | 1024 | 0 | 0 | 2283.47 | 87.49 | 55.61 | 287 | 92.66 | 67.907 |
|  CBR Proxy | 512M | 200 | 1024 | 1000 | 0 | 199.24 | 1002.78 | 5.38 | 1023 | 99.55 | 28.118 |
|  CBR Proxy | 512M | 200 | 10240 | 0 | 0 | 409.24 | 488.78 | 195.02 | 1015 | 78.39 | 119.87 |
|  CBR Proxy | 512M | 200 | 10240 | 1000 | 0 | 277.71 | 719.47 | 397.27 | 1239 | 89.69 | 113.398 |
|  CBR Proxy | 512M | 500 | 1024 | 0 | 0 | 2017.23 | 247.9 | 117.24 | 587 | 80.09 | 116.868 |
|  CBR Proxy | 512M | 500 | 1024 | 1000 | 0 | 493.46 | 1011.98 | 28.66 | 1151 | 96.04 | 115.89 |
|  CBR Proxy | 512M | 500 | 10240 | 0 | 0 | 385.81 | 1294.49 | 463.09 | 2511 | 67.89 | 205.192 |
|  CBR Proxy | 512M | 500 | 10240 | 1000 | 0 | 379.33 | 1315.39 | 441.26 | 2191 | 71.31 | 193.47 |
|  CBR SOAP Header Proxy | 512M | 100 | 1024 | 0 | 0 | 2630.83 | 37.93 | 38.29 | 184 | 95.48 | 28.122 |
|  CBR SOAP Header Proxy | 512M | 100 | 1024 | 1000 | 0 | 99.73 | 1002.82 | 5.89 | 1019 | 99.85 | 28.028 |
|  CBR SOAP Header Proxy | 512M | 100 | 10240 | 0 | 0 | 717.45 | 139.28 | 63.26 | 315 | 91.16 | 91.497 |
|  CBR SOAP Header Proxy | 512M | 100 | 10240 | 1000 | 0 | 105.05 | 950.47 | 196.1 | 1039 | 99.03 | 81.614 |
|  CBR SOAP Header Proxy | 512M | 200 | 1024 | 0 | 0 | 2580.81 | 77.37 | 53.75 | 279 | 94.12 | 69.454 |
|  CBR SOAP Header Proxy | 512M | 200 | 1024 | 1000 | 0 | 199.35 | 1002.54 | 4.38 | 1023 | 99.61 | 28.101 |
|  CBR SOAP Header Proxy | 512M | 200 | 10240 | 0 | 0 | 656.34 | 304.85 | 126.78 | 623 | 83.98 | 115.222 |
|  CBR SOAP Header Proxy | 512M | 200 | 10240 | 1000 | 0 | 254.87 | 783.28 | 359.61 | 1111 | 95.63 | 110.919 |
|  CBR SOAP Header Proxy | 512M | 500 | 1024 | 0 | 0 | 2324.86 | 214.97 | 106.16 | 507 | 80.93 | 117.065 |
|  CBR SOAP Header Proxy | 512M | 500 | 1024 | 1000 | 0 | 492.83 | 1012.92 | 29.71 | 1183 | 96.67 | 115.274 |
|  CBR SOAP Header Proxy | 512M | 500 | 10240 | 0 | 0 | 599.31 | 833.69 | 293.37 | 1527 | 73.31 | 191.367 |
|  CBR SOAP Header Proxy | 512M | 500 | 10240 | 1000 | 0 | 525.76 | 949.27 | 368.67 | 1431 | 78.52 | 180.852 |
|  CBR Transport Header Proxy | 512M | 100 | 1024 | 0 | 0 | 3796.39 | 26.23 | 31.49 | 119 | 96.4 | 28.062 |
|  CBR Transport Header Proxy | 512M | 100 | 1024 | 1000 | 0 | 99.94 | 1000.06 | 48.95 | 1079 | 99.9 | 28.094 |
|  CBR Transport Header Proxy | 512M | 100 | 10240 | 0 | 0 | 2344.05 | 42.53 | 34.36 | 115 | 97.72 | 28.056 |
|  CBR Transport Header Proxy | 512M | 100 | 10240 | 1000 | 0 | 110.9 | 901.06 | 283.22 | 1039 | 99.88 | 28.167 |
|  CBR Transport Header Proxy | 512M | 200 | 1024 | 0 | 0 | 3749.35 | 53.21 | 44.26 | 205 | 95.29 | 28.19 |
|  CBR Transport Header Proxy | 512M | 200 | 1024 | 1000 | 0 | 199.45 | 1001.94 | 18.39 | 1007 | 99.78 | 28.099 |
|  CBR Transport Header Proxy | 512M | 200 | 10240 | 0 | 0 | 2290.75 | 87.13 | 42.32 | 200 | 97.04 | 28.16 |
|  CBR Transport Header Proxy | 512M | 200 | 10240 | 1000 | 0 | 209.96 | 951.8 | 207.35 | 1011 | 99.77 | 28.107 |
|  CBR Transport Header Proxy | 512M | 500 | 1024 | 0 | 0 | 3615.37 | 137.94 | 72.24 | 389 | 92.79 | 50.306 |
|  CBR Transport Header Proxy | 512M | 500 | 1024 | 1000 | 0 | 497.43 | 1003.87 | 41.44 | 1055 | 99.21 | 24.018 |
|  CBR Transport Header Proxy | 512M | 500 | 10240 | 0 | 0 | 2158.31 | 231.22 | 72.41 | 411 | 95.46 | 28.086 |
|  CBR Transport Header Proxy | 512M | 500 | 10240 | 1000 | 0 | 710.95 | 702.15 | 404.7 | 1039 | 98.95 | 28.139 |
|  Direct Proxy | 512M | 100 | 1024 | 0 | 0 | 3900.3 | 25.53 | 30.78 | 115 | 96.36 | 28.108 |
|  Direct Proxy | 512M | 100 | 1024 | 1000 | 0 | 99.54 | 1003.8 | 17.5 | 1087 | 99.89 | 28.073 |
|  Direct Proxy | 512M | 100 | 10240 | 0 | 0 | 2365.78 | 42.14 | 34.44 | 115 | 97.68 | 28.098 |
|  Direct Proxy | 512M | 100 | 10240 | 1000 | 0 | 105.69 | 945.12 | 222.73 | 1047 | 99.89 | 28.081 |
|  Direct Proxy | 512M | 200 | 1024 | 0 | 0 | 3875.61 | 51.47 | 41.4 | 193 | 95.33 | 28.061 |
|  Direct Proxy | 512M | 200 | 1024 | 1000 | 0 | 200.15 | 998.69 | 57.2 | 1003 | 99.78 | 28.088 |
|  Direct Proxy | 512M | 200 | 10240 | 0 | 0 | 2358.33 | 84.59 | 39.7 | 192 | 97.02 | 28.092 |
|  Direct Proxy | 512M | 200 | 10240 | 1000 | 0 | 212.18 | 941.91 | 231.62 | 1003 | 99.78 | 28.135 |
|  Direct Proxy | 512M | 500 | 1024 | 0 | 0 | 3742.4 | 133.47 | 71.42 | 381 | 92.49 | 51.444 |
|  Direct Proxy | 512M | 500 | 1024 | 1000 | 0 | 498.05 | 1003.69 | 32.56 | 1047 | 99.21 | 28.049 |
|  Direct Proxy | 512M | 500 | 10240 | 0 | 0 | 2195.76 | 227.67 | 71.78 | 407 | 95.45 | 28.11 |
|  Direct Proxy | 512M | 500 | 10240 | 1000 | 0 | 618.85 | 806.97 | 366.73 | 1047 | 99.07 | 28.058 |
|  XSLT Enhanced Proxy | 512M | 100 | 1024 | 0 | 0 | 2130.21 | 46.88 | 47.52 | 228 | 95.19 | 44.905 |
|  XSLT Enhanced Proxy | 512M | 100 | 1024 | 1000 | 0 | 99.51 | 1003.23 | 10.13 | 1039 | 99.79 | 40.812 |
|  XSLT Enhanced Proxy | 512M | 100 | 10240 | 0 | 0 | 400.82 | 249.55 | 93.15 | 499 | 96.13 | 64.166 |
|  XSLT Enhanced Proxy | 512M | 100 | 10240 | 1000 | 0 | 102.99 | 969.02 | 172.09 | 1015 | 99.31 | 40.41 |
|  XSLT Enhanced Proxy | 512M | 200 | 1024 | 0 | 0 | 2032.98 | 98.3 | 73.19 | 389 | 92.25 | 74.551 |
|  XSLT Enhanced Proxy | 512M | 200 | 1024 | 1000 | 0 | 199.26 | 1002.95 | 6.19 | 1023 | 99.47 | 45.611 |
|  XSLT Enhanced Proxy | 512M | 200 | 10240 | 0 | 0 | 393.53 | 508.27 | 192.92 | 1019 | 92.14 | 89.947 |
|  XSLT Enhanced Proxy | 512M | 200 | 10240 | 1000 | 0 | 268.12 | 745.01 | 388.14 | 1127 | 96.08 | 80.072 |
|  XSLT Enhanced Proxy | 512M | 500 | 1024 | 0 | 0 | 1832.71 | 272.83 | 159.43 | 843 | 75.28 | 118.835 |
|  XSLT Enhanced Proxy | 512M | 500 | 1024 | 1000 | 0 | 482.3 | 1035.8 | 45.21 | 1191 | 94.7 | 104.974 |
|  XSLT Enhanced Proxy | 512M | 500 | 10240 | 0 | 0 | 395.72 | 1262.12 | 430.8 | 2399 | 86.9 | 141.136 |
|  XSLT Enhanced Proxy | 512M | 500 | 10240 | 1000 | 0 | 381.96 | 1306.41 | 329.99 | 1975 | 89.13 | 120.639 |
|  XSLT Proxy | 512M | 100 | 1024 | 0 | 0 | 1239.76 | 80.52 | 64.55 | 373 | 94.18 | 131.549 |
|  XSLT Proxy | 512M | 100 | 1024 | 1000 | 0 | 99.61 | 1002.87 | 5.07 | 1019 | 99.65 | 41.383 |
|  XSLT Proxy | 512M | 100 | 10240 | 0 | 0 | 199.93 | 500.28 | 258.5 | 1279 | 85.96 | 142.071 |
|  XSLT Proxy | 512M | 100 | 10240 | 1000 | 0 | 97.3 | 1026.58 | 56.55 | 1215 | 95.92 | 147.223 |
|  XSLT Proxy | 512M | 200 | 1024 | 0 | 0 | 1138.07 | 175.68 | 102.92 | 511 | 86.03 | 144.542 |
|  XSLT Proxy | 512M | 200 | 1024 | 1000 | 0 | 198.39 | 1007.03 | 16.87 | 1095 | 98.75 | 109.459 |
|  XSLT Proxy | 512M | 200 | 10240 | 0 | 0 | 184.27 | 1083.4 | 512.21 | 2607 | 82.14 | 163.925 |
|  XSLT Proxy | 512M | 200 | 10240 | 1000 | 0 | 169.35 | 1179.43 | 137.55 | 1631 | 86.92 | 146.969 |
|  XSLT Proxy | 512M | 500 | 1024 | 0 | 0 | 1051.39 | 475.46 | 212.38 | 1111 | 76.19 | 157.192 |
|  XSLT Proxy | 512M | 500 | 1024 | 1000 | 0 | 482.63 | 1034.73 | 47.9 | 1223 | 90.08 | 141.212 |
|  XSLT Proxy | 512M | 500 | 10240 | 0 | 0 | 140.71 | 3543.94 | 1840.32 | 9343 | 66.26 | 260.282 |
|  XSLT Proxy | 512M | 500 | 10240 | 1000 | 0 | 168.07 | 2961.05 | 932.14 | 5727 | 74.66 | 232.586 |
