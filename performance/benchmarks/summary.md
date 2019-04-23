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

The performance tests were executed on 2 AWS CloudFormation stacks.


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
| Operating System | Distribution | Kernel | Linux ip-10-0-1-98 4.15.0-1035-aws #37-Ubuntu SMP Mon Mar 18 16:15:14 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux |


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
|  CBR Proxy | 512M | 100 | 1024 | 0 | 0 | 1517.07 | 65.67 | 38.15 | 122 | 96.29 | 23.976 |
|  CBR Proxy | 512M | 100 | 1024 | 1000 | 0 | 99.47 | 1005.28 | 25.07 | 1087 | 99.83 | 28.093 |
|  CBR Proxy | 512M | 100 | 10240 | 0 | 0 | 323.25 | 309.56 | 105.85 | 591 | 89.47 | 89.885 |
|  CBR Proxy | 512M | 100 | 10240 | 1000 | 0 | 112.05 | 891.4 | 278.52 | 1103 | 97.56 | 83.255 |
|  CBR Proxy | 512M | 200 | 1024 | 0 | 0 | 1514.07 | 131.9 | 51.19 | 281 | 94.95 | 64.15 |
|  CBR Proxy | 512M | 200 | 1024 | 1000 | 0 | 198.53 | 1006.61 | 16.47 | 1103 | 99.52 | 28.103 |
|  CBR Proxy | 512M | 200 | 10240 | 0 | 0 | 300.48 | 665.37 | 206.48 | 1199 | 81.21 | 118.519 |
|  CBR Proxy | 512M | 200 | 10240 | 1000 | 0 | 222.84 | 896.25 | 351.15 | 1311 | 89.96 | 113.099 |
|  CBR Proxy | 512M | 500 | 1024 | 0 | 0 | 1343.13 | 372.29 | 132.18 | 699 | 83.7 | 117.435 |
|  CBR Proxy | 512M | 500 | 1024 | 1000 | 0 | 490.56 | 1018.52 | 36.55 | 1183 | 95.44 | 114.159 |
|  CBR Proxy | 512M | 500 | 10240 | 0 | 0 | 268.06 | 1863.04 | 553.56 | 3119 | 75.25 | 202.514 |
|  CBR Proxy | 512M | 500 | 10240 | 1000 | 0 | 274.68 | 1816.55 | 315.82 | 2591 | 76.45 | 195.396 |
|  CBR SOAP Header Proxy | 512M | 100 | 1024 | 0 | 0 | 1747.65 | 57.13 | 37.72 | 112 | 96.55 | 28.068 |
|  CBR SOAP Header Proxy | 512M | 100 | 1024 | 1000 | 0 | 99.27 | 1006.52 | 17.36 | 1103 | 99.83 | 28.077 |
|  CBR SOAP Header Proxy | 512M | 100 | 10240 | 0 | 0 | 481.43 | 207.61 | 74.9 | 401 | 93.18 | 91.678 |
|  CBR SOAP Header Proxy | 512M | 100 | 10240 | 1000 | 0 | 116.9 | 854.41 | 333.29 | 1103 | 98.93 | 81.021 |
|  CBR SOAP Header Proxy | 512M | 200 | 1024 | 0 | 0 | 1697.87 | 117.59 | 46.89 | 214 | 95.49 | 64.817 |
|  CBR SOAP Header Proxy | 512M | 200 | 1024 | 1000 | 0 | 198.71 | 1005.68 | 13.45 | 1095 | 99.6 | 28.091 |
|  CBR SOAP Header Proxy | 512M | 200 | 10240 | 0 | 0 | 457.85 | 436.54 | 140.49 | 791 | 87.2 | 115.086 |
|  CBR SOAP Header Proxy | 512M | 200 | 10240 | 1000 | 0 | 247.95 | 805.46 | 343.27 | 1103 | 95.69 | 109.54 |
|  CBR SOAP Header Proxy | 512M | 500 | 1024 | 0 | 0 | 1556.18 | 321.46 | 114.58 | 607 | 84.78 | 116.966 |
|  CBR SOAP Header Proxy | 512M | 500 | 1024 | 1000 | 0 | 487.51 | 1024.69 | 40.13 | 1207 | 96.55 | 115.223 |
|  CBR SOAP Header Proxy | 512M | 500 | 10240 | 0 | 0 | 419.2 | 1191.78 | 342.45 | 1991 | 79.86 | 189.051 |
|  CBR SOAP Header Proxy | 512M | 500 | 10240 | 1000 | 0 | 406 | 1229.25 | 303.15 | 1735 | 80.95 | 181.618 |
|  CBR Transport Header Proxy | 512M | 100 | 1024 | 0 | 0 | 2476.05 | 40.33 | 35.28 | 99 | 97.19 | 28.07 |
|  CBR Transport Header Proxy | 512M | 100 | 1024 | 1000 | 0 | 99.53 | 1003.87 | 42.31 | 1095 | 99.88 | 28.139 |
|  CBR Transport Header Proxy | 512M | 100 | 10240 | 0 | 0 | 1586.79 | 62.89 | 35.49 | 106 | 98.16 | 28.154 |
|  CBR Transport Header Proxy | 512M | 100 | 10240 | 1000 | 0 | 117.39 | 850.72 | 341.76 | 1071 | 99.87 | 28.153 |
|  CBR Transport Header Proxy | 512M | 200 | 1024 | 0 | 0 | 2548.75 | 78.36 | 37.06 | 181 | 96.37 | 28.128 |
|  CBR Transport Header Proxy | 512M | 200 | 1024 | 1000 | 0 | 199.43 | 1002.04 | 0.72 | 1003 | 99.78 | 28.162 |
|  CBR Transport Header Proxy | 512M | 200 | 10240 | 0 | 0 | 1555.85 | 128.39 | 39.98 | 204 | 97.64 | 28.092 |
|  CBR Transport Header Proxy | 512M | 200 | 10240 | 1000 | 0 | 202.51 | 986.55 | 121.84 | 1003 | 99.78 | 28.09 |
|  CBR Transport Header Proxy | 512M | 500 | 1024 | 0 | 0 | 2419.68 | 206.67 | 65.18 | 387 | 94 | 28.164 |
|  CBR Transport Header Proxy | 512M | 500 | 1024 | 1000 | 0 | 497.96 | 1003.21 | 34.19 | 1071 | 99.17 | 28.114 |
|  CBR Transport Header Proxy | 512M | 500 | 10240 | 0 | 0 | 1393.33 | 359 | 72.46 | 567 | 96.41 | 28.08 |
|  CBR Transport Header Proxy | 512M | 500 | 10240 | 1000 | 0 | 550.62 | 907.02 | 274.51 | 1023 | 99.09 | 28.138 |
|  Direct Proxy | 512M | 100 | 1024 | 0 | 0 | 2554.39 | 39.08 | 34.89 | 97 | 96.94 | 28.121 |
|  Direct Proxy | 512M | 100 | 1024 | 1000 | 0 | 100.04 | 998.11 | 79.35 | 1095 | 99.89 | 28.084 |
|  Direct Proxy | 512M | 100 | 10240 | 0 | 0 | 1543.49 | 64.69 | 35.85 | 108 | 98.16 | 28.098 |
|  Direct Proxy | 512M | 100 | 10240 | 1000 | 0 | 105.37 | 948.14 | 223.79 | 1095 | 99.87 | 28.114 |
|  Direct Proxy | 512M | 200 | 1024 | 0 | 0 | 2543.73 | 78.54 | 37.16 | 180 | 96.34 | 28.125 |
|  Direct Proxy | 512M | 200 | 1024 | 1000 | 0 | 199 | 1003.59 | 67.4 | 1103 | 99.78 | 28.131 |
|  Direct Proxy | 512M | 200 | 10240 | 0 | 0 | 1523.98 | 131.09 | 39.86 | 205 | 97.81 | 28.044 |
|  Direct Proxy | 512M | 200 | 10240 | 1000 | 0 | 233.42 | 855.53 | 339.78 | 1079 | 99.75 | 28.153 |
|  Direct Proxy | 512M | 500 | 1024 | 0 | 0 | 2490.47 | 200.55 | 64.36 | 381 | 94.03 | 28.11 |
|  Direct Proxy | 512M | 500 | 1024 | 1000 | 0 | 495.91 | 1007.28 | 26.66 | 1095 | 99.17 | 28.075 |
|  Direct Proxy | 512M | 500 | 10240 | 0 | 0 | 1424.32 | 351.29 | 71.09 | 519 | 96.46 | 28.088 |
|  Direct Proxy | 512M | 500 | 10240 | 1000 | 0 | 726.77 | 686.97 | 418.81 | 1047 | 98.86 | 28.121 |
|  XSLT Enhanced Proxy | 512M | 100 | 1024 | 0 | 0 | 1407.57 | 70.97 | 37.19 | 174 | 96.47 | 42.026 |
|  XSLT Enhanced Proxy | 512M | 100 | 1024 | 1000 | 0 | 99.42 | 1004.31 | 12.01 | 1087 | 99.79 | 40.742 |
|  XSLT Enhanced Proxy | 512M | 100 | 10240 | 0 | 0 | 282.76 | 353.85 | 96.09 | 599 | 96.73 | 58.385 |
|  XSLT Enhanced Proxy | 512M | 100 | 10240 | 1000 | 0 | 122.93 | 812.37 | 369.88 | 1079 | 99.18 | 40.33 |
|  XSLT Enhanced Proxy | 512M | 200 | 1024 | 0 | 0 | 1396.37 | 143.17 | 53.15 | 299 | 94.76 | 65.562 |
|  XSLT Enhanced Proxy | 512M | 200 | 1024 | 1000 | 0 | 199.1 | 1003.95 | 9.63 | 1063 | 99.48 | 44.809 |
|  XSLT Enhanced Proxy | 512M | 200 | 10240 | 0 | 0 | 280.7 | 712.2 | 199.4 | 1231 | 93.82 | 87.462 |
|  XSLT Enhanced Proxy | 512M | 200 | 10240 | 1000 | 0 | 237.67 | 840.45 | 372.64 | 1207 | 95.51 | 80.666 |
|  XSLT Enhanced Proxy | 512M | 500 | 1024 | 0 | 0 | 1211.15 | 413.07 | 143.68 | 895 | 82.09 | 113.494 |
|  XSLT Enhanced Proxy | 512M | 500 | 1024 | 1000 | 0 | 477.04 | 1046.47 | 62.09 | 1271 | 93.42 | 103.725 |
|  XSLT Enhanced Proxy | 512M | 500 | 10240 | 0 | 0 | 261.83 | 1903.6 | 477.78 | 3151 | 89.74 | 139.865 |
|  XSLT Enhanced Proxy | 512M | 500 | 10240 | 1000 | 0 | 267.84 | 1863.85 | 307.31 | 2719 | 90.07 | 129.588 |
|  XSLT Proxy | 512M | 100 | 1024 | 0 | 0 | 818.85 | 121.96 | 46.88 | 275 | 95.35 | 123.294 |
|  XSLT Proxy | 512M | 100 | 1024 | 1000 | 0 | 99.58 | 1003.76 | 8.1 | 1031 | 99.62 | 41.129 |
|  XSLT Proxy | 512M | 100 | 10240 | 0 | 0 | 129.69 | 771.15 | 268.03 | 1511 | 90.49 | 141.197 |
|  XSLT Proxy | 512M | 100 | 10240 | 1000 | 0 | 93.5 | 1068.42 | 94.92 | 1415 | 95.37 | 144.618 |
|  XSLT Proxy | 512M | 200 | 1024 | 0 | 0 | 751.56 | 266.22 | 95.18 | 583 | 88.73 | 141.476 |
|  XSLT Proxy | 512M | 200 | 1024 | 1000 | 0 | 197.6 | 1010.58 | 24.24 | 1111 | 98.7 | 108.417 |
|  XSLT Proxy | 512M | 200 | 10240 | 0 | 0 | 129.22 | 1547.09 | 524.46 | 3007 | 86.44 | 159.934 |
|  XSLT Proxy | 512M | 200 | 10240 | 1000 | 0 | 124.12 | 1607.84 | 282.24 | 2495 | 88.45 | 150.974 |
|  XSLT Proxy | 512M | 500 | 1024 | 0 | 0 | 700.51 | 713.67 | 223.04 | 1319 | 80.67 | 153.584 |
|  XSLT Proxy | 512M | 500 | 1024 | 1000 | 0 | 465.11 | 1073.85 | 79.94 | 1383 | 88.33 | 140.415 |
|  XSLT Proxy | 512M | 500 | 10240 | 0 | 0 | 102.96 | 4831.47 | 1512.59 | 9407 | 75.57 | 247.857 |
|  XSLT Proxy | 512M | 500 | 10240 | 1000 | 0 | 113.59 | 4363.26 | 1179.18 | 7519 | 80.97 | 231.675 |
