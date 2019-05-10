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
| Concurrent Users | The number of users accessing the application at the same time. | 10, 20, 50, 100 |
| Message Size (Bytes) | The request payload size in Bytes. | 1024, 10240 |
| Back-end Delay (ms) | The delay added by the Back-end service. | 0, 1000 |

The duration of each test is **900 seconds**. The warm-up period is **300 seconds**.
The measurement results are collected after the warm-up period.

The performance tests were executed on 2 AWS CloudFormation stacks.


System information for WSO2 Enterprise Micro Integrator in 1st AWS CloudFormation stack.

| Class | Subclass | Description | Value |
| --- | --- | --- | --- |
| AWS | EC2 | AMI-ID | ami-0273df992a343e0d6 |
| AWS | EC2 | Instance Type | c5.xlarge |
| System | Processor | CPU(s) | 4 |
| System | Processor | Thread(s) per core | 2 |
| System | Processor | Core(s) per socket | 2 |
| System | Processor | Socket(s) | 1 |
| System | Processor | Model name | Intel(R) Xeon(R) Platinum 8124M CPU @ 3.00GHz |
| System | Memory | BIOS | 64 KiB |
| System | Memory | System memory | 7807992 KiB |
| System | Storage | Block Device: nvme0n1 | 8G |
| Operating System | Distribution | Release | Ubuntu 18.04.2 LTS |
| Operating System | Distribution | Kernel | Linux ip-10-0-1-26 4.15.0-1037-aws #39-Ubuntu SMP Tue Apr 16 08:09:09 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux |
System information for WSO2 Enterprise Micro Integrator in 2nd AWS CloudFormation stack.

| Class | Subclass | Description | Value |
| --- | --- | --- | --- |
| AWS | EC2 | AMI-ID | ami-0273df992a343e0d6 |
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
| Operating System | Distribution | Kernel | Linux ip-10-0-1-115 4.15.0-1037-aws #39-Ubuntu SMP Tue Apr 16 08:09:09 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux |


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
|  CBR Proxy | 512M | 10 | 1024 | 0 | 0 | 113.19 | 88.26 | 58.88 | 202 | 99.04 | 28.169 |
|  CBR Proxy | 512M | 10 | 1024 | 1000 | 0 | 9.56 | 1045.46 | 59.77 | 1207 | 99.5 | 28.229 |
|  CBR Proxy | 512M | 10 | 10240 | 0 | 0 | 16.72 | 598.46 | 415.88 | 2399 | 98.93 | 28.3 |
|  CBR Proxy | 512M | 10 | 10240 | 1000 | 0 | 10.86 | 920.07 | 533.39 | 2303 | 99.28 | 28.218 |
|  CBR Proxy | 512M | 20 | 1024 | 0 | 0 | 108.81 | 183.76 | 129.45 | 703 | 98.81 | 28.18 |
|  CBR Proxy | 512M | 20 | 1024 | 1000 | 0 | 18.95 | 1054.24 | 85.46 | 1407 | 99.49 | 28.129 |
|  CBR Proxy | 512M | 20 | 10240 | 0 | 0 | 16.5 | 1211.69 | 675.96 | 3807 | 98.72 | 28.216 |
|  CBR Proxy | 512M | 20 | 10240 | 1000 | 0 | 14.96 | 1334.23 | 849.06 | 4927 | 98.77 | 28.15 |
|  CBR Proxy | 512M | 50 | 1024 | 0 | 0 | 116.38 | 429.68 | 204.47 | 1199 | 98.49 | 28.183 |
|  CBR Proxy | 512M | 50 | 1024 | 1000 | 0 | 46.94 | 1063.95 | 88.02 | 1407 | 99.19 | 28.139 |
|  CBR Proxy | 512M | 50 | 10240 | 0 | 0 | 18.95 | 2631.96 | 1216.23 | 6527 | 97.45 | 28.178 |
|  CBR Proxy | 512M | 50 | 10240 | 1000 | 0 | 17.47 | 2857.84 | 1060.7 | 6303 | 97.71 | 28.177 |
|  CBR Proxy | 512M | 100 | 1024 | 0 | 0 | 119.53 | 836.26 | 377.62 | 2207 | 98.19 | 28.155 |
|  CBR Proxy | 512M | 100 | 1024 | 1000 | 0 | 80.4 | 1240.79 | 290.95 | 2415 | 98.64 | 28.2 |
|  CBR Proxy | 512M | 100 | 10240 | 0 | 0 | 17.65 | 5639.93 | 2353.55 | 12863 | 93.98 | 71.511 |
|  CBR Proxy | 512M | 100 | 10240 | 1000 | 0 | 19.68 | 5069.56 | 1895.07 | 11007 | 93.68 | 71.453 |
|  CBR SOAP Header Proxy | 512M | 10 | 1024 | 0 | 0 | 108.21 | 92.35 | 71.23 | 301 | 99.18 | 28.097 |
|  CBR SOAP Header Proxy | 512M | 10 | 1024 | 1000 | 0 | 9.69 | 1031.33 | 63.64 | 1207 | 99.43 | 28.189 |
|  CBR SOAP Header Proxy | 512M | 10 | 10240 | 0 | 0 | 21.15 | 473.02 | 398.73 | 2111 | 99.13 | 28.197 |
|  CBR SOAP Header Proxy | 512M | 10 | 10240 | 1000 | 0 | 10.3 | 970.61 | 341.37 | 1799 | 99.32 | 28.147 |
|  CBR SOAP Header Proxy | 512M | 20 | 1024 | 0 | 0 | 119.84 | 166.83 | 94.38 | 501 | 98.95 | 28.096 |
|  CBR SOAP Header Proxy | 512M | 20 | 1024 | 1000 | 0 | 18.94 | 1054.67 | 86.14 | 1399 | 99.47 | 25.044 |
|  CBR SOAP Header Proxy | 512M | 20 | 10240 | 0 | 0 | 22.12 | 904.18 | 487.11 | 2511 | 98.97 | 28.171 |
|  CBR SOAP Header Proxy | 512M | 20 | 10240 | 1000 | 0 | 20.83 | 959.12 | 540.28 | 2511 | 99.28 | 28.158 |
|  CBR SOAP Header Proxy | 512M | 50 | 1024 | 0 | 0 | 124.02 | 402.89 | 193.78 | 1103 | 98.69 | 28.207 |
|  CBR SOAP Header Proxy | 512M | 50 | 1024 | 1000 | 0 | 47.04 | 1062.15 | 80.79 | 1303 | 99.18 | 28.058 |
|  CBR SOAP Header Proxy | 512M | 50 | 10240 | 0 | 0 | 23.55 | 2123.44 | 1064.55 | 5631 | 98.49 | 28.208 |
|  CBR SOAP Header Proxy | 512M | 50 | 10240 | 1000 | 0.01 | 24.66 | 2024.04 | 845.65 | 4927 | 98.6 | 28.211 |
|  CBR SOAP Header Proxy | 512M | 100 | 1024 | 0 | 0 | 134.45 | 743.96 | 372.28 | 2111 | 98.16 | 28.168 |
|  CBR SOAP Header Proxy | 512M | 100 | 1024 | 1000 | 0 | 84.43 | 1182.44 | 251.27 | 2207 | 98.68 | 28.127 |
|  CBR SOAP Header Proxy | 512M | 100 | 10240 | 0 | 0 | 27.06 | 3684.99 | 1605.86 | 8511 | 95.72 | 67.15 |
|  CBR SOAP Header Proxy | 512M | 100 | 10240 | 1000 | 0 | 24.32 | 4095.86 | 1704.34 | 9215 | 95.84 | 67.073 |
|  CBR Transport Header Proxy | 512M | 10 | 1024 | 0 | 0 | 164.27 | 60.75 | 59.4 | 200 | 98.99 | 28.268 |
|  CBR Transport Header Proxy | 512M | 10 | 1024 | 1000 | 0 | 9.71 | 1028.52 | 126.35 | 1207 | 99.59 | 28.146 |
|  CBR Transport Header Proxy | 512M | 10 | 10240 | 0 | 0 | 101.79 | 98.17 | 80.83 | 401 | 99.14 | 28.277 |
|  CBR Transport Header Proxy | 512M | 10 | 10240 | 1000 | 0 | 14.12 | 708.13 | 411.79 | 1207 | 99.57 | 28.161 |
|  CBR Transport Header Proxy | 512M | 20 | 1024 | 0 | 0 | 151.12 | 132.23 | 99.64 | 501 | 98.96 | 28.198 |
|  CBR Transport Header Proxy | 512M | 20 | 1024 | 1000 | 0 | 19.14 | 1044.29 | 120.87 | 1303 | 99.41 | 28.125 |
|  CBR Transport Header Proxy | 512M | 20 | 10240 | 0 | 0 | 101.29 | 197.4 | 129.04 | 703 | 99.19 | 28.186 |
|  CBR Transport Header Proxy | 512M | 20 | 10240 | 1000 | 0 | 23.83 | 838.63 | 369.16 | 1207 | 99.47 | 28.214 |
|  CBR Transport Header Proxy | 512M | 50 | 1024 | 0 | 0 | 182.86 | 273.53 | 145.14 | 803 | 98.61 | 28.169 |
|  CBR Transport Header Proxy | 512M | 50 | 1024 | 1000 | 0 | 46.88 | 1065.55 | 91.16 | 1407 | 99.37 | 28.201 |
|  CBR Transport Header Proxy | 512M | 50 | 10240 | 0 | 0 | 111.96 | 446.88 | 189.62 | 1199 | 99.02 | 28.172 |
|  CBR Transport Header Proxy | 512M | 50 | 10240 | 1000 | 0 | 57.1 | 875.11 | 364.84 | 1503 | 99.28 | 28.147 |
|  CBR Transport Header Proxy | 512M | 100 | 1024 | 0 | 0 | 215.58 | 463.86 | 200.42 | 1199 | 98.3 | 28.24 |
|  CBR Transport Header Proxy | 512M | 100 | 1024 | 1000 | 0 | 89.14 | 1120.04 | 143.27 | 1695 | 99 | 28.16 |
|  CBR Transport Header Proxy | 512M | 100 | 10240 | 0 | 0 | 113.37 | 880.65 | 344.31 | 2007 | 98.61 | 28.146 |
|  CBR Transport Header Proxy | 512M | 100 | 10240 | 1000 | 0 | 95.71 | 1042.64 | 432.56 | 2207 | 98.87 | 28.365 |
|  Direct Proxy | 512M | 10 | 1024 | 0 | 0 | 169.94 | 58.76 | 58.63 | 200 | 99.03 | 28.152 |
|  Direct Proxy | 512M | 10 | 1024 | 1000 | 0 | 9.57 | 1045.15 | 89.68 | 1207 | 99.55 | 28.194 |
|  Direct Proxy | 512M | 10 | 10240 | 0 | 0 | 119.44 | 83.6 | 65.82 | 299 | 99.19 | 28.102 |
|  Direct Proxy | 512M | 10 | 10240 | 1000 | 0 | 13.29 | 752.25 | 413.94 | 1199 | 99.47 | 28.145 |
|  Direct Proxy | 512M | 20 | 1024 | 0 | 0 | 179.12 | 111.56 | 84.54 | 401 | 98.88 | 28.214 |
|  Direct Proxy | 512M | 20 | 1024 | 1000 | 0 | 19.23 | 1040.11 | 96.48 | 1207 | 99.58 | 28.255 |
|  Direct Proxy | 512M | 20 | 10240 | 0 | 0 | 113.82 | 175.37 | 102.43 | 599 | 99.23 | 24.083 |
|  Direct Proxy | 512M | 20 | 10240 | 1000 | 0 | 24.97 | 800.54 | 395.92 | 1303 | 99.5 | 28.109 |
|  Direct Proxy | 512M | 50 | 1024 | 0 | 0 | 191.91 | 260.65 | 128.38 | 703 | 98.72 | 28.284 |
|  Direct Proxy | 512M | 50 | 1024 | 1000 | 0 | 46.62 | 1070.5 | 129.85 | 1607 | 99.23 | 24.084 |
|  Direct Proxy | 512M | 50 | 10240 | 0 | 0 | 117.75 | 424.62 | 175.68 | 1103 | 99 | 28.163 |
|  Direct Proxy | 512M | 50 | 10240 | 1000 | 0 | 62.18 | 802.43 | 416.1 | 1703 | 99.21 | 28.186 |
|  Direct Proxy | 512M | 100 | 1024 | 0 | 0 | 187.12 | 534.47 | 259.16 | 1407 | 98.17 | 28.125 |
|  Direct Proxy | 512M | 100 | 1024 | 1000 | 0 | 91.76 | 1088.11 | 121.91 | 1599 | 99.01 | 28.192 |
|  Direct Proxy | 512M | 100 | 10240 | 0 | 0 | 113.2 | 883.29 | 338.11 | 1903 | 98.81 | 28.192 |
|  Direct Proxy | 512M | 100 | 10240 | 1000 | 0 | 94.37 | 1057.58 | 468.36 | 2303 | 99.02 | 28.145 |
|  XSLT Enhanced Proxy | 512M | 10 | 1024 | 0 | 0 | 82.55 | 121.07 | 90.66 | 499 | 98.89 | 24.121 |
|  XSLT Enhanced Proxy | 512M | 10 | 1024 | 1000 | 0 | 9.39 | 1063.99 | 74.4 | 1303 | 99.46 | 39.319 |
|  XSLT Enhanced Proxy | 512M | 10 | 10240 | 0 | 0 | 8.16 | 1224.51 | 673.89 | 3407 | 99.31 | 39.642 |
|  XSLT Enhanced Proxy | 512M | 10 | 10240 | 1000 | 0 | 8.95 | 1115.3 | 587.48 | 3103 | 99.28 | 39.669 |
|  XSLT Enhanced Proxy | 512M | 20 | 1024 | 0 | 0 | 85.83 | 232.97 | 137.44 | 703 | 98.63 | 24.164 |
|  XSLT Enhanced Proxy | 512M | 20 | 1024 | 1000 | 0 | 18.49 | 1079.98 | 105.95 | 1503 | 99.25 | 39.627 |
|  XSLT Enhanced Proxy | 512M | 20 | 10240 | 0 | 0 | 8.63 | 2314.63 | 1097.99 | 5727 | 99.04 | 41.603 |
|  XSLT Enhanced Proxy | 512M | 20 | 10240 | 1000 | 0 | 7.72 | 2579.53 | 1115.82 | 6207 | 99.1 | 41.544 |
|  XSLT Enhanced Proxy | 512M | 50 | 1024 | 0 | 0 | 99.91 | 500.67 | 234.73 | 1303 | 98.23 | 28.145 |
|  XSLT Enhanced Proxy | 512M | 50 | 1024 | 1000 | 0 | 43.85 | 1139.58 | 184.23 | 1903 | 98.97 | 39.256 |
|  XSLT Enhanced Proxy | 512M | 50 | 10240 | 0 | 0 | 9.87 | 5023.77 | 2130.51 | 11519 | 98.86 | 42.126 |
|  XSLT Enhanced Proxy | 512M | 50 | 10240 | 1000 | 0 | 8.69 | 5723.05 | 2315.13 | 12799 | 98.71 | 41.819 |
|  XSLT Enhanced Proxy | 512M | 100 | 1024 | 0 | 0 | 117.43 | 850.48 | 361.08 | 2111 | 97.64 | 41.819 |
|  XSLT Enhanced Proxy | 512M | 100 | 1024 | 1000 | 0 | 73.31 | 1362.03 | 313.81 | 2607 | 98.47 | 40.999 |
|  XSLT Enhanced Proxy | 512M | 100 | 10240 | 0 | 0 | 11.81 | 8415.11 | 2982.31 | 16511 | 98.31 | 43.982 |
|  XSLT Enhanced Proxy | 512M | 100 | 10240 | 1000 | 0 | 11.79 | 8379.73 | 2935.3 | 17663 | 98.53 | 46.442 |
|  XSLT Proxy | 512M | 10 | 1024 | 0 | 0 | 52.68 | 189.76 | 128.86 | 703 | 98.8 | 41.126 |
|  XSLT Proxy | 512M | 10 | 1024 | 1000 | 0 | 9.34 | 1069.09 | 82.38 | 1311 | 99.38 | 41.125 |
|  XSLT Proxy | 512M | 10 | 10240 | 0 | 0 | 10.25 | 975.55 | 403.33 | 2207 | 98.75 | 42.675 |
|  XSLT Proxy | 512M | 10 | 10240 | 1000 | 0 | 7.86 | 1271.31 | 280.82 | 2207 | 98.99 | 42.165 |
|  XSLT Proxy | 512M | 20 | 1024 | 0 | 0 | 54.31 | 368.37 | 200.24 | 1103 | 98.64 | 42.413 |
|  XSLT Proxy | 512M | 20 | 1024 | 1000 | 0 | 18.27 | 1094 | 110.14 | 1503 | 99.35 | 24.198 |
|  XSLT Proxy | 512M | 20 | 10240 | 0 | 0 | 11.72 | 1702.81 | 671.48 | 3807 | 98.4 | 44.549 |
|  XSLT Proxy | 512M | 20 | 10240 | 1000 | 0 | 10.37 | 1923.88 | 491.41 | 3407 | 98.67 | 24.016 |
|  XSLT Proxy | 512M | 50 | 1024 | 0 | 0 | 62.92 | 794.99 | 334.01 | 1903 | 97.97 | 43.593 |
|  XSLT Proxy | 512M | 50 | 1024 | 1000 | 0 | 40.86 | 1221.65 | 218.47 | 2007 | 98.61 | 42.007 |
|  XSLT Proxy | 512M | 50 | 10240 | 0 | 0 | 11.73 | 4250.43 | 1589.41 | 8959 | 96.01 | 108.09 |
|  XSLT Proxy | 512M | 50 | 10240 | 1000 | 0 | 11.65 | 4283.85 | 1330.37 | 8511 | 97.08 | 84.748 |
|  XSLT Proxy | 512M | 100 | 1024 | 0 | 0 | 60.61 | 1645.08 | 714.22 | 4319 | 97.61 | 47.762 |
|  XSLT Proxy | 512M | 100 | 1024 | 1000 | 0 | 57.3 | 1741.86 | 453.22 | 3215 | 97.83 | 44.431 |
|  XSLT Proxy | 512M | 100 | 10240 | 0 | 0 | 10.54 | 9365.92 | 3860.51 | 21759 | 93.33 | 125.17 |
|  XSLT Proxy | 512M | 100 | 10240 | 1000 | 0 | 11.08 | 8938.21 | 2734.28 | 17023 | 93.18 | 123.159 |
