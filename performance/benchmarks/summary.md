# WSO2 Enterprise Integrator Performance Test Results

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

1. **Throughput**: The number of requests that the WSO2 Enterprise Integrator processes during a specific time interval (e.g. per second).
2. **Response Time**: The end-to-end latency for an operation of invoking a service in WSO2 Enterprise Integrator . The complete distribution of response times was recorded.

In addition to the above metrics, we measure the load average and several memory-related metrics.

The following are the test parameters.

| Test Parameter | Description | Values |
| --- | --- | --- |
| Scenario Name | The name of the test scenario. | Refer to the above table. |
| Heap Size | The amount of memory allocated to the application | 2G |
| Concurrent Users | The number of users accessing the application at the same time. | 100, 200, 500 |
| Message Size (Bytes) | The request payload size in Bytes. | 1024, 10240, 102400 |
| Back-end Delay (ms) | The delay added by the Back-end service. | 0, 30, 1000 |

The duration of each test is **900 seconds**. The warm-up period is **300 seconds**.
The measurement results are collected after the warm-up period.

The performance tests were executed on 3 AWS CloudFormation stacks.


System information for WSO2 Enterprise Integrator in 1st AWS CloudFormation stack.

| Class | Subclass | Description | Value |
| --- | --- | --- | --- |
| AWS | EC2 | AMI-ID | ami-095192256fe1477ad |
| AWS | EC2 | Instance Type | c5.large |
| System | Processor | CPU(s) | 2 |
| System | Processor | Thread(s) per core | 2 |
| System | Processor | Core(s) per socket | 1 |
| System | Processor | Socket(s) | 1 |
| System | Processor | Model name | Intel(R) Xeon(R) Platinum 8124M CPU @ 3.00GHz |
| System | Memory | BIOS | 64 KiB |
| System | Memory | System memory | 3794280 KiB |
| System | Storage | Block Device: nvme0n1 | 8G |
| Operating System | Distribution | Release | Ubuntu 18.04.2 LTS |
| Operating System | Distribution | Kernel | Linux ip-10-0-1-160 4.15.0-1041-aws #43-Ubuntu SMP Thu Jun 6 13:39:11 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux |
System information for WSO2 Enterprise Integrator in 2nd AWS CloudFormation stack.

| Class | Subclass | Description | Value |
| --- | --- | --- | --- |
| AWS | EC2 | AMI-ID | ami-095192256fe1477ad |
| AWS | EC2 | Instance Type | c5.large |
| System | Processor | CPU(s) | 2 |
| System | Processor | Thread(s) per core | 2 |
| System | Processor | Core(s) per socket | 1 |
| System | Processor | Socket(s) | 1 |
| System | Processor | Model name | Intel(R) Xeon(R) Platinum 8124M CPU @ 3.00GHz |
| System | Memory | BIOS | 64 KiB |
| System | Memory | System memory | 3794280 KiB |
| System | Storage | Block Device: nvme0n1 | 8G |
| Operating System | Distribution | Release | Ubuntu 18.04.2 LTS |
| Operating System | Distribution | Kernel | Linux ip-10-0-1-111 4.15.0-1041-aws #43-Ubuntu SMP Thu Jun 6 13:39:11 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux |
System information for WSO2 Enterprise Integrator in 3rd AWS CloudFormation stack.

| Class | Subclass | Description | Value |
| --- | --- | --- | --- |
| AWS | EC2 | AMI-ID | ami-095192256fe1477ad |
| AWS | EC2 | Instance Type | c5.large |
| System | Processor | CPU(s) | 2 |
| System | Processor | Thread(s) per core | 2 |
| System | Processor | Core(s) per socket | 1 |
| System | Processor | Socket(s) | 1 |
| System | Processor | Model name | Intel(R) Xeon(R) Platinum 8124M CPU @ 3.00GHz |
| System | Memory | BIOS | 64 KiB |
| System | Memory | System memory | 3794288 KiB |
| System | Storage | Block Device: nvme0n1 | 8G |
| Operating System | Distribution | Release | Ubuntu 18.04.2 LTS |
| Operating System | Distribution | Kernel | Linux ip-10-0-1-254 4.15.0-1041-aws #43-Ubuntu SMP Thu Jun 6 13:39:11 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux |


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

|  Scenario Name | Heap Size | Concurrent Users | Message Size (Bytes) | Back-end Service Delay (ms) | Error % | Throughput (Requests/sec) | Average Response Time (ms) | Standard Deviation of Response Time (ms) | 99th Percentile of Response Time (ms) | WSO2 Enterprise Integrator GC Throughput (%) | Average WSO2 Enterprise Integrator Memory Footprint After Full GC (M) |
|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|
|  CBR Proxy | 2G | 100 | 1024 | 0 | 0 | 4989.43 | 19.96 | 8.85 | 51 | 98.91 | 56.113 |
|  CBR Proxy | 2G | 100 | 1024 | 30 | 0 | 3130.26 | 31.91 | 2.11 | 42 | 99.31 | 56.086 |
|  CBR Proxy | 2G | 100 | 1024 | 1000 | 0 | 99.69 | 1002.09 | 0.91 | 1007 | 99.74 | 56.528 |
|  CBR Proxy | 2G | 100 | 10240 | 0 | 0 | 952.42 | 104.88 | 164.96 | 229 | 97.69 | 73.052 |
|  CBR Proxy | 2G | 100 | 10240 | 30 | 0 | 915.31 | 109.13 | 32.9 | 201 | 97.98 | 57.332 |
|  CBR Proxy | 2G | 100 | 10240 | 1000 | 0 | 99.56 | 1002.69 | 2.21 | 1011 | 99.61 | 56.509 |
|  CBR Proxy | 2G | 100 | 102400 | 0 | 0 | 80.86 | 1235.84 | 393.2 | 2623 | 77.6 | 281.612 |
|  CBR Proxy | 2G | 100 | 102400 | 30 | 0 | 78.79 | 1267.99 | 380.36 | 2591 | 77.9 | 279.159 |
|  CBR Proxy | 2G | 100 | 102400 | 1000 | 0 | 72.93 | 1368.7 | 265.58 | 2463 | 83.39 | 260.317 |
|  CBR Proxy | 2G | 200 | 1024 | 0 | 0 | 5143.68 | 38.78 | 15.98 | 88 | 98.53 | 57.947 |
|  CBR Proxy | 2G | 200 | 1024 | 30 | 0 | 4556.46 | 43.81 | 7.32 | 70 | 98.71 | 70.319 |
|  CBR Proxy | 2G | 200 | 1024 | 1000 | 0 | 199.5 | 1002.15 | 1.14 | 1007 | 99.75 | 73.459 |
|  CBR Proxy | 2G | 200 | 10240 | 0 | 0 | 958.73 | 208.57 | 237.21 | 417 | 95.91 | 73.225 |
|  CBR Proxy | 2G | 200 | 10240 | 30 | 0 | 969.7 | 206.19 | 170.44 | 385 | 96.18 | 57.201 |
|  CBR Proxy | 2G | 200 | 10240 | 1000 | 0 | 198.89 | 1003.73 | 4.64 | 1031 | 99.18 | 60.585 |
|  CBR Proxy | 2G | 200 | 102400 | 0 | 0 | 58.2 | 3429.1 | 1262.9 | 6047 | 59.62 | 478.112 |
|  CBR Proxy | 2G | 200 | 102400 | 30 | 0 | 58.74 | 3390.66 | 1211.96 | 5855 | 60.03 | 476.439 |
|  CBR Proxy | 2G | 200 | 102400 | 1000 | 0 | 64.38 | 3097.07 | 861.98 | 5055 | 63.87 | 443.573 |
|  CBR Proxy | 2G | 500 | 1024 | 0 | 0 | 5035.68 | 99.18 | 43.52 | 225 | 97.31 | 74.484 |
|  CBR Proxy | 2G | 500 | 1024 | 30 | 0 | 4849.19 | 103 | 29.94 | 189 | 97.38 | 56.727 |
|  CBR Proxy | 2G | 500 | 1024 | 1000 | 0 | 498.34 | 1002.21 | 1.77 | 1007 | 99.56 | 57.761 |
|  CBR Proxy | 2G | 500 | 10240 | 0 | 0 | 890.05 | 561.88 | 192.2 | 1031 | 89.54 | 157.72 |
|  CBR Proxy | 2G | 500 | 10240 | 30 | 0 | 894.6 | 558.8 | 180.59 | 1019 | 89.64 | 159.287 |
|  CBR Proxy | 2G | 500 | 10240 | 1000 | 0 | 481.52 | 1036.97 | 36.81 | 1175 | 95.14 | 62.281 |
|  CBR Proxy | 2G | 500 | 102400 | 0 | 0 | 14.51 | 33215.54 | 8702.15 | 52735 | 22.24 | 1111.488 |
|  CBR Proxy | 2G | 500 | 102400 | 30 | 0 | 15.77 | 30920.17 | 7102.84 | 47103 | 23.76 | 1081.712 |
|  CBR Proxy | 2G | 500 | 102400 | 1000 | 0 | 22.63 | 21704.81 | 4054.45 | 31487 | 28 | 982.294 |
|  CBR SOAP Header Proxy | 2G | 100 | 1024 | 0 | 0 | 5944.02 | 16.74 | 7.76 | 45 | 98.92 | 56.235 |
|  CBR SOAP Header Proxy | 2G | 100 | 1024 | 30 | 0 | 3175.32 | 31.46 | 1.77 | 39 | 99.37 | 57.755 |
|  CBR SOAP Header Proxy | 2G | 100 | 1024 | 1000 | 0 | 99.72 | 1002.05 | 0.78 | 1003 | 99.77 | 57.079 |
|  CBR SOAP Header Proxy | 2G | 100 | 10240 | 0 | 0 | 1335.34 | 67.55 | 182.46 | 148 | 98.54 | 56.576 |
|  CBR SOAP Header Proxy | 2G | 100 | 10240 | 30 | 0 | 1392.24 | 71.72 | 18.09 | 125 | 98.71 | 55.697 |
|  CBR SOAP Header Proxy | 2G | 100 | 10240 | 1000 | 0 | 99.62 | 1002.28 | 1.47 | 1007 | 99.69 | 60.475 |
|  CBR SOAP Header Proxy | 2G | 100 | 102400 | 0 | 0 | 169.27 | 590.53 | 178.73 | 1003 | 89.25 | 100.096 |
|  CBR SOAP Header Proxy | 2G | 100 | 102400 | 30 | 0 | 166.61 | 600.13 | 169.42 | 991 | 89.91 | 41.534 |
|  CBR SOAP Header Proxy | 2G | 100 | 102400 | 1000 | 0 | 91.26 | 1093.83 | 72.55 | 1295 | 95.26 | 65.026 |
|  CBR SOAP Header Proxy | 2G | 200 | 1024 | 0 | 0 | 6165.6 | 32.34 | 13.6 | 76 | 98.48 | 70.924 |
|  CBR SOAP Header Proxy | 2G | 200 | 1024 | 30 | 0 | 5075.17 | 39.35 | 5.85 | 62 | 98.82 | 56.153 |
|  CBR SOAP Header Proxy | 2G | 200 | 1024 | 1000 | 0 | 199.46 | 1002.06 | 0.81 | 1003 | 99.75 | 55.502 |
|  CBR SOAP Header Proxy | 2G | 200 | 10240 | 0 | 0 | 1451.81 | 137.64 | 51.98 | 273 | 97.4 | 61.93 |
|  CBR SOAP Header Proxy | 2G | 200 | 10240 | 30 | 0 | 1470.15 | 135.92 | 39.94 | 245 | 97.62 | 59.334 |
|  CBR SOAP Header Proxy | 2G | 200 | 10240 | 1000 | 0 | 199.21 | 1002.47 | 2.31 | 1011 | 99.54 | 61.398 |
|  CBR SOAP Header Proxy | 2G | 200 | 102400 | 0 | 0 | 112.52 | 1775.44 | 844.32 | 3663 | 63.88 | 442.234 |
|  CBR SOAP Header Proxy | 2G | 200 | 102400 | 30 | 0 | 116.17 | 1719.06 | 803.33 | 3583 | 63.9 | 439.522 |
|  CBR SOAP Header Proxy | 2G | 200 | 102400 | 1000 | 0 | 115.55 | 1728.01 | 520.88 | 3279 | 72.71 | 393.438 |
|  CBR SOAP Header Proxy | 2G | 500 | 1024 | 0 | 0 | 5971.73 | 83.6 | 36.24 | 191 | 97.46 | 73.136 |
|  CBR SOAP Header Proxy | 2G | 500 | 1024 | 30 | 0 | 5819.09 | 85.64 | 23.52 | 156 | 97.61 | 74.102 |
|  CBR SOAP Header Proxy | 2G | 500 | 1024 | 1000 | 0 | 498.22 | 1002.3 | 2.15 | 1011 | 99.6 | 57.126 |
|  CBR SOAP Header Proxy | 2G | 500 | 10240 | 0 | 0 | 1401.06 | 356.86 | 119.41 | 659 | 93.9 | 120.106 |
|  CBR SOAP Header Proxy | 2G | 500 | 10240 | 30 | 0 | 1421.57 | 351.72 | 110.58 | 635 | 93.86 | 119.692 |
|  CBR SOAP Header Proxy | 2G | 500 | 10240 | 1000 | 0 | 491.99 | 1014.48 | 18.58 | 1095 | 97.97 | 57.957 |
|  CBR SOAP Header Proxy | 2G | 500 | 102400 | 0 | 0 | 45.85 | 10818.03 | 3518.06 | 17791 | 32.1 | 959.243 |
|  CBR SOAP Header Proxy | 2G | 500 | 102400 | 30 | 0 | 46.17 | 10750.33 | 3646.59 | 17279 | 32.17 | 944.789 |
|  CBR SOAP Header Proxy | 2G | 500 | 102400 | 1000 | 0 | 51.9 | 9518.02 | 3018.48 | 16511 | 34.09 | 919.612 |
|  CBR Transport Header Proxy | 2G | 100 | 1024 | 0 | 0 | 10174.39 | 9.76 | 9.9 | 55 | 99.09 | 55.146 |
|  CBR Transport Header Proxy | 2G | 100 | 1024 | 30 | 0 | 3218.56 | 31.03 | 1.37 | 35 | 99.62 | 57.384 |
|  CBR Transport Header Proxy | 2G | 100 | 1024 | 1000 | 0 | 99.71 | 1002.07 | 0.85 | 1007 | 99.79 | 55.927 |
|  CBR Transport Header Proxy | 2G | 100 | 10240 | 0 | 0 | 5430.57 | 18.3 | 7.52 | 39 | 99.42 | 55.579 |
|  CBR Transport Header Proxy | 2G | 100 | 10240 | 30 | 0 | 3014.07 | 33.1 | 2.63 | 43 | 99.58 | 60.671 |
|  CBR Transport Header Proxy | 2G | 100 | 10240 | 1000 | 0 | 99.7 | 1002.11 | 0.76 | 1007 | 99.78 | 55.661 |
|  CBR Transport Header Proxy | 2G | 100 | 102400 | 0 | 0 | 1271.81 | 78.2 | 24.28 | 115 | 99.72 | 73.409 |
|  CBR Transport Header Proxy | 2G | 100 | 102400 | 30 | 0 | 238.43 | 419.45 | 50.26 | 663 | 99.78 | 55.112 |
|  CBR Transport Header Proxy | 2G | 100 | 102400 | 1000 | 0 | 99.52 | 1002.89 | 1.74 | 1007 | 99.76 | 55.731 |
|  CBR Transport Header Proxy | 2G | 200 | 1024 | 0 | 0 | 10173.16 | 19.49 | 12.51 | 66 | 98.88 | 58.826 |
|  CBR Transport Header Proxy | 2G | 200 | 1024 | 30 | 0 | 6151.64 | 32.45 | 6.48 | 45 | 99.33 | 56.083 |
|  CBR Transport Header Proxy | 2G | 200 | 1024 | 1000 | 0 | 199.23 | 1002.25 | 1.49 | 1011 | 99.78 | 56.655 |
|  CBR Transport Header Proxy | 2G | 200 | 10240 | 0 | 0 | 5332.16 | 37.37 | 30.35 | 146 | 99.4 | 55.113 |
|  CBR Transport Header Proxy | 2G | 200 | 10240 | 30 | 0 | 4592.22 | 43.45 | 7.59 | 68 | 99.44 | 75.899 |
|  CBR Transport Header Proxy | 2G | 200 | 10240 | 1000 | 0 | 199.42 | 1002.11 | 0.98 | 1007 | 99.77 | 59.584 |
|  CBR Transport Header Proxy | 2G | 200 | 102400 | 0 | 0 | 1074.56 | 185.63 | 156.47 | 959 | 99.65 | 55.226 |
|  CBR Transport Header Proxy | 2G | 200 | 102400 | 30 | 0 | 238.26 | 838.62 | 197.43 | 1431 | 99.77 | 55.28 |
|  CBR Transport Header Proxy | 2G | 200 | 102400 | 1000 | 0 | 198.93 | 1003.51 | 4.26 | 1015 | 99.76 | 55.935 |
|  CBR Transport Header Proxy | 2G | 500 | 1024 | 0 | 0 | 10298.1 | 48.39 | 25.49 | 129 | 98.48 | 70.178 |
|  CBR Transport Header Proxy | 2G | 500 | 1024 | 30 | 0 | 9364.8 | 53.28 | 13.02 | 97 | 98.61 | 55.69 |
|  CBR Transport Header Proxy | 2G | 500 | 1024 | 1000 | 0 | 496.88 | 1002.85 | 20.52 | 1019 | 99.72 | 59.083 |
|  CBR Transport Header Proxy | 2G | 500 | 10240 | 0 | 0 | 5527.91 | 90.33 | 48.38 | 235 | 99.1 | 56.535 |
|  CBR Transport Header Proxy | 2G | 500 | 10240 | 30 | 0 | 5242.69 | 95.05 | 33.19 | 193 | 99.14 | 57.822 |
|  CBR Transport Header Proxy | 2G | 500 | 10240 | 1000 | 0 | 498.17 | 1002.26 | 1.55 | 1007 | 99.73 | 56.7 |
|  CBR Transport Header Proxy | 2G | 500 | 102400 | 0 | 0.05 | 794.78 | 555.08 | 2780.39 | 2479 | 99.66 | 55.665 |
|  CBR Transport Header Proxy | 2G | 500 | 102400 | 30 | 0.3 | 207.64 | 2059.03 | 7729.16 | 17407 | 99.7 | 61.859 |
|  CBR Transport Header Proxy | 2G | 500 | 102400 | 1000 | 0.06 | 211.41 | 2089.07 | 3481.75 | 8511 | 99.73 | 64.358 |
|  Direct Proxy | 2G | 100 | 1024 | 0 | 0 | 10737.52 | 9.24 | 9.16 | 51 | 99.05 | 56.387 |
|  Direct Proxy | 2G | 100 | 1024 | 30 | 0 | 3225.3 | 30.97 | 1.35 | 35 | 99.61 | 73.729 |
|  Direct Proxy | 2G | 100 | 1024 | 1000 | 0 | 99.72 | 1002.07 | 0.89 | 1003 | 99.79 | 73.401 |
|  Direct Proxy | 2G | 100 | 10240 | 0 | 0 | 5542.31 | 17.93 | 7.18 | 38 | 99.45 | 55.672 |
|  Direct Proxy | 2G | 100 | 10240 | 30 | 0 | 3020.33 | 33.01 | 2.51 | 42 | 99.63 | 58.143 |
|  Direct Proxy | 2G | 100 | 10240 | 1000 | 0 | 99.7 | 1002.14 | 0.82 | 1007 | 99.78 | 58.622 |
|  Direct Proxy | 2G | 100 | 102400 | 0 | 0 | 1156.74 | 86.03 | 75.04 | 463 | 99.72 | 54.985 |
|  Direct Proxy | 2G | 100 | 102400 | 30 | 0 | 238.51 | 419.35 | 44.65 | 655 | 99.77 | 58.001 |
|  Direct Proxy | 2G | 100 | 102400 | 1000 | 0 | 99.53 | 1002.66 | 1.58 | 1007 | 99.77 | 56.894 |
|  Direct Proxy | 2G | 200 | 1024 | 0 | 0 | 10394.22 | 19.16 | 12.49 | 66 | 98.96 | 61.883 |
|  Direct Proxy | 2G | 200 | 1024 | 30 | 0 | 6206.71 | 32.18 | 2.81 | 45 | 99.34 | 54.933 |
|  Direct Proxy | 2G | 200 | 1024 | 1000 | 0 | 199.45 | 1002.05 | 0.91 | 1003 | 99.78 | 59.987 |
|  Direct Proxy | 2G | 200 | 10240 | 0 | 0 | 5496.67 | 36.26 | 30.51 | 147 | 99.38 | 62.424 |
|  Direct Proxy | 2G | 200 | 10240 | 30 | 0 | 4646.95 | 42.94 | 7.29 | 67 | 99.47 | 57.779 |
|  Direct Proxy | 2G | 200 | 10240 | 1000 | 0 | 199.4 | 1002.12 | 1 | 1007 | 99.78 | 54.952 |
|  Direct Proxy | 2G | 200 | 102400 | 0 | 0 | 941.93 | 211.97 | 215.84 | 1127 | 99.7 | 74.519 |
|  Direct Proxy | 2G | 200 | 102400 | 30 | 0 | 238.26 | 838.77 | 192.96 | 1407 | 99.77 | 55.497 |
|  Direct Proxy | 2G | 200 | 102400 | 1000 | 0 | 199.09 | 1002.62 | 1.6 | 1007 | 99.78 | 71.329 |
|  Direct Proxy | 2G | 500 | 1024 | 0 | 0 | 10725.72 | 46.49 | 25.6 | 130 | 98.51 | 55.205 |
|  Direct Proxy | 2G | 500 | 1024 | 30 | 0 | 9841.46 | 50.69 | 11.91 | 91 | 98.64 | 55.677 |
|  Direct Proxy | 2G | 500 | 1024 | 1000 | 0 | 498.57 | 1002.16 | 1.67 | 1007 | 99.73 | 73.617 |
|  Direct Proxy | 2G | 500 | 10240 | 0 | 0 | 5518.43 | 90.49 | 50.83 | 247 | 99.13 | 73.356 |
|  Direct Proxy | 2G | 500 | 10240 | 30 | 0 | 5279.78 | 94.59 | 34.03 | 196 | 99.13 | 75.171 |
|  Direct Proxy | 2G | 500 | 10240 | 1000 | 0 | 498.36 | 1002.08 | 2.09 | 1007 | 99.72 | 57.68 |
|  Direct Proxy | 2G | 500 | 102400 | 0 | 0 | 787.62 | 633.42 | 618.2 | 2831 | 99.66 | 58.566 |
|  Direct Proxy | 2G | 500 | 102400 | 30 | 0 | 237.91 | 2095.88 | 508.77 | 3711 | 99.68 | 44.196 |
|  Direct Proxy | 2G | 500 | 102400 | 1000 | 0 | 237.82 | 2096.18 | 413.29 | 3279 | 99.74 | 73.97 |
|  Secure Proxy | 2G | 100 | 1024 | 0 | 0 | 242.64 | 412.23 | 201.92 | 987 | 98.64 | 55.194 |
|  Secure Proxy | 2G | 100 | 1024 | 30 | 0 | 243.59 | 410.64 | 179.09 | 915 | 98.71 | 56.573 |
|  Secure Proxy | 2G | 100 | 1024 | 1000 | 0 | 98.76 | 1011.23 | 10.05 | 1055 | 99.51 | 57.707 |
|  Secure Proxy | 2G | 100 | 10240 | 0 | 0 | 143.59 | 696.19 | 196.12 | 1223 | 96.07 | 55.233 |
|  Secure Proxy | 2G | 100 | 10240 | 30 | 0 | 145.17 | 688.81 | 190.48 | 1199 | 96.44 | 57.834 |
|  Secure Proxy | 2G | 100 | 10240 | 1000 | 0 | 88.85 | 1123.46 | 102.55 | 1447 | 97.5 | 56.766 |
|  Secure Proxy | 2G | 100 | 102400 | 0 | 0 | 15.7 | 6323.16 | 1073.64 | 7967 | 62.26 | 510.986 |
|  Secure Proxy | 2G | 100 | 102400 | 30 | 0 | 15.59 | 6386.97 | 1097.66 | 8095 | 63.04 | 508.249 |
|  Secure Proxy | 2G | 100 | 102400 | 1000 | 0 | 15.2 | 6537.74 | 1352.95 | 8575 | 59.57 | 524.616 |
|  Secure Proxy | 2G | 200 | 1024 | 0 | 0 | 237.22 | 842.78 | 354.57 | 1847 | 97.93 | 63.743 |
|  Secure Proxy | 2G | 200 | 1024 | 30 | 0 | 239.97 | 833.23 | 335.99 | 1775 | 98.01 | 60.431 |
|  Secure Proxy | 2G | 200 | 1024 | 1000 | 0 | 179.47 | 1112.47 | 114.4 | 1535 | 98.83 | 59.542 |
|  Secure Proxy | 2G | 200 | 10240 | 0 | 0 | 139.01 | 1437.33 | 330.78 | 2383 | 94.35 | 118.259 |
|  Secure Proxy | 2G | 200 | 10240 | 30 | 0 | 140.97 | 1417.24 | 327.02 | 2319 | 93.82 | 131.166 |
|  Secure Proxy | 2G | 200 | 10240 | 1000 | 0 | 135.54 | 1472.62 | 218.65 | 2239 | 92.11 | 192.338 |
|  Secure Proxy | 2G | 200 | 102400 | 0 | 0 | 10.46 | 18855.61 | 1956.95 | 22911 | 45.36 | 885.955 |
|  Secure Proxy | 2G | 200 | 102400 | 30 | 0 | 10.64 | 18556.47 | 2012.42 | 22783 | 45.4 | 878.784 |
|  Secure Proxy | 2G | 200 | 102400 | 1000 | 0 | 10.36 | 19006.16 | 2179.52 | 24575 | 44.52 | 879.727 |
|  Secure Proxy | 2G | 500 | 1024 | 0 | 0 | 231.8 | 2151.42 | 682.7 | 4015 | 96.34 | 74.197 |
|  Secure Proxy | 2G | 500 | 1024 | 30 | 0 | 232.06 | 2149.23 | 690.77 | 4095 | 96.24 | 72.231 |
|  Secure Proxy | 2G | 500 | 1024 | 1000 | 0 | 238.13 | 2093.31 | 477.24 | 3455 | 95.17 | 144.813 |
|  Secure Proxy | 2G | 500 | 10240 | 0 | 0 | 121.64 | 4099.27 | 884.37 | 6687 | 83.95 | 335.514 |
|  Secure Proxy | 2G | 500 | 10240 | 30 | 0 | 122.05 | 4078.93 | 824.9 | 6239 | 84.31 | 329.272 |
|  Secure Proxy | 2G | 500 | 10240 | 1000 | 0 | 114.21 | 4358.71 | 1192.17 | 7327 | 78.61 | 385.644 |
|  Secure Proxy | 2G | 500 | 102400 | 0 | 100 | 11963.57 | 32.4 | 75.73 | 109 | 14.79 | 1543.368 |
|  Secure Proxy | 2G | 500 | 102400 | 30 | 100 | 3.29 | 136057.81 | 34734.03 | 274431 | N/A | N/A |
|  XSLT Enhanced Proxy | 2G | 100 | 1024 | 0 | 0 | 3917.87 | 25.45 | 13.95 | 77 | 98.7 | 54.692 |
|  XSLT Enhanced Proxy | 2G | 100 | 1024 | 30 | 0 | 2835.63 | 35.22 | 3.69 | 50 | 99.11 | 56.478 |
|  XSLT Enhanced Proxy | 2G | 100 | 1024 | 1000 | 0 | 99.65 | 1002.16 | 0.99 | 1007 | 99.76 | 66.979 |
|  XSLT Enhanced Proxy | 2G | 100 | 10240 | 0 | 0 | 631.05 | 158.37 | 56.92 | 311 | 98.9 | 73.355 |
|  XSLT Enhanced Proxy | 2G | 100 | 10240 | 30 | 0 | 614.22 | 162.7 | 47.39 | 291 | 98.96 | 57.529 |
|  XSLT Enhanced Proxy | 2G | 100 | 10240 | 1000 | 0 | 99.48 | 1003.7 | 2.3 | 1011 | 99.68 | 56.416 |
|  XSLT Enhanced Proxy | 2G | 100 | 102400 | 0 | 0 | 76.25 | 1310.25 | 314.31 | 1999 | 97.47 | 100.63 |
|  XSLT Enhanced Proxy | 2G | 100 | 102400 | 30 | 0 | 75.8 | 1318.29 | 305.29 | 2007 | 97.5 | 102.279 |
|  XSLT Enhanced Proxy | 2G | 100 | 102400 | 1000 | 0 | 66.73 | 1495.92 | 211.02 | 2223 | 98.03 | 59.753 |
|  XSLT Enhanced Proxy | 2G | 200 | 1024 | 0 | 0 | 4056.1 | 49.23 | 27.89 | 147 | 97.98 | 64.036 |
|  XSLT Enhanced Proxy | 2G | 200 | 1024 | 30 | 0 | 3786.98 | 52.69 | 11.06 | 89 | 98.36 | 61.673 |
|  XSLT Enhanced Proxy | 2G | 200 | 1024 | 1000 | 0 | 199.39 | 1002.21 | 1.39 | 1007 | 99.72 | 57.223 |
|  XSLT Enhanced Proxy | 2G | 200 | 10240 | 0 | 0 | 653.01 | 305.67 | 95.41 | 543 | 98.34 | 55.877 |
|  XSLT Enhanced Proxy | 2G | 200 | 10240 | 30 | 0 | 649.95 | 307.86 | 86.61 | 539 | 98.37 | 56.882 |
|  XSLT Enhanced Proxy | 2G | 200 | 10240 | 1000 | 0 | 198.73 | 1004.76 | 4.38 | 1023 | 99.47 | 55.517 |
|  XSLT Enhanced Proxy | 2G | 200 | 102400 | 0 | 0 | 72.95 | 2733.22 | 622.03 | 4159 | 96.06 | 195.519 |
|  XSLT Enhanced Proxy | 2G | 200 | 102400 | 30 | 0 | 75.95 | 2622.83 | 576.58 | 4015 | 95.94 | 194.977 |
|  XSLT Enhanced Proxy | 2G | 200 | 102400 | 1000 | 0 | 73.39 | 2716.45 | 474.93 | 4031 | 96.45 | 172.451 |
|  XSLT Proxy | 2G | 100 | 1024 | 0 | 0 | 2151.38 | 46.4 | 25.26 | 132 | 97.58 | 73.369 |
|  XSLT Proxy | 2G | 100 | 1024 | 30 | 0 | 1979.33 | 50.46 | 11.22 | 91 | 97.85 | 59.183 |
|  XSLT Proxy | 2G | 100 | 1024 | 1000 | 0 | 99.65 | 1002.38 | 2.06 | 1007 | 99.68 | 57.522 |
|  XSLT Proxy | 2G | 100 | 10240 | 0 | 0 | 370.41 | 269.98 | 137.76 | 675 | 97.16 | 58.967 |
|  XSLT Proxy | 2G | 100 | 10240 | 30 | 0 | 376.92 | 265.34 | 119.51 | 615 | 97.32 | 62.05 |
|  XSLT Proxy | 2G | 100 | 10240 | 1000 | 0 | 99.24 | 1006.85 | 3.56 | 1023 | 99.32 | 58.704 |
|  XSLT Proxy | 2G | 100 | 102400 | 0 | 0 | 31.37 | 3174.4 | 683.11 | 4991 | 82.09 | 327.716 |
|  XSLT Proxy | 2G | 100 | 102400 | 30 | 0 | 30.6 | 3258 | 647.34 | 4927 | 82.33 | 323.961 |
|  XSLT Proxy | 2G | 100 | 102400 | 1000 | 0 | 28.17 | 3541.37 | 817.36 | 5407 | 83.84 | 314.441 |
|  XSLT Proxy | 2G | 200 | 1024 | 0 | 0 | 2222.77 | 89.84 | 42.05 | 220 | 96.98 | 60.659 |
|  XSLT Proxy | 2G | 200 | 1024 | 30 | 0 | 2192.05 | 91.16 | 27.36 | 176 | 97.07 | 55.915 |
|  XSLT Proxy | 2G | 200 | 1024 | 1000 | 0 | 199.25 | 1002.33 | 2.2 | 1011 | 99.58 | 57.236 |
|  XSLT Proxy | 2G | 200 | 10240 | 0 | 0 | 353.81 | 565.25 | 217.9 | 1175 | 95.81 | 57.577 |
|  XSLT Proxy | 2G | 200 | 10240 | 30 | 0 | 366.45 | 545.87 | 197.75 | 1095 | 95.5 | 55.503 |
|  XSLT Proxy | 2G | 200 | 10240 | 1000 | 0 | 197.66 | 1010.71 | 15 | 1071 | 98.43 | 73.738 |
|  XSLT Proxy | 2G | 200 | 102400 | 0 | 0 | 23.31 | 8486.61 | 1620.75 | 12287 | 66 | 508.623 |
|  XSLT Proxy | 2G | 200 | 102400 | 30 | 0 | 23.98 | 8304.4 | 1600.24 | 12287 | 67.16 | 486.316 |
|  XSLT Proxy | 2G | 200 | 102400 | 1000 | 0 | 25.24 | 7872.07 | 1318.05 | 11263 | 69.65 | 457.107 |
|  XSLT Proxy | 2G | 500 | 1024 | 0 | 0 | 2063.65 | 242.31 | 100.36 | 527 | 95.02 | 328.86 |
|  XSLT Proxy | 2G | 500 | 1024 | 30 | 0 | 2080.61 | 240.35 | 85.96 | 485 | 95.02 | 328.132 |
|  XSLT Proxy | 2G | 500 | 1024 | 1000 | 0 | 497.49 | 1003.33 | 5.82 | 1039 | 98.94 | 59.517 |
|  XSLT Proxy | 2G | 500 | 10240 | 0 | 0 | 304.9 | 1636.09 | 454.25 | 2927 | 84.97 | 403.197 |
|  XSLT Proxy | 2G | 500 | 10240 | 30 | 0 | 304.2 | 1641.2 | 468.75 | 2943 | 84.77 | 398.801 |
|  XSLT Proxy | 2G | 500 | 10240 | 1000 | 0 | 327.99 | 1521.49 | 303.32 | 2543 | 88.41 | 344.402 |
|  XSLT Proxy | 2G | 500 | 102400 | 0 | 0 | 8.8 | 54703.58 | 12580.94 | 76799 | 34.52 | 1121.083 |
|  XSLT Proxy | 2G | 500 | 102400 | 30 | 0 | 9.51 | 50283.1 | 10772.81 | 75775 | 33.2 | 1119.023 |
|  XSLT Proxy | 2G | 500 | 102400 | 1000 | 0 | 9.37 | 50397.16 | 9663.03 | 68607 | 36.04 | 1097.002 |
