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
| System | Memory | System memory | 7807992 KiB |
| System | Storage | Block Device: nvme0n1 | 8G |
| Operating System | Distribution | Release | Ubuntu 18.04.2 LTS |
| Operating System | Distribution | Kernel | Linux ip-10-0-1-236 4.15.0-1035-aws #37-Ubuntu SMP Mon Mar 18 16:15:14 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux |
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
| System | Memory | System memory | 7807992 KiB |
| System | Storage | Block Device: nvme0n1 | 8G |
| Operating System | Distribution | Release | Ubuntu 18.04.2 LTS |
| Operating System | Distribution | Kernel | Linux ip-10-0-1-107 4.15.0-1035-aws #37-Ubuntu SMP Mon Mar 18 16:15:14 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux |


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
|  CBR Proxy | 512M | 100 | 1024 | 0 | 0 | 760.32 | 131.32 | 53.39 | 291 | 97.45 | 28.192 |
|  CBR Proxy | 512M | 100 | 1024 | 1000 | 0 | 99.06 | 1007.94 | 22.08 | 1103 | 99.71 | 28.154 |
|  CBR Proxy | 512M | 100 | 10240 | 0 | 0 | 153.13 | 653.14 | 218.86 | 1207 | 91.58 | 88.724 |
|  CBR Proxy | 512M | 100 | 10240 | 1000 | 0 | 116.84 | 854.75 | 356.1 | 1207 | 95.06 | 83.892 |
|  CBR Proxy | 512M | 200 | 1024 | 0 | 0 | 741.26 | 269.82 | 91.48 | 501 | 96.11 | 53.879 |
|  CBR Proxy | 512M | 200 | 1024 | 1000 | 0 | 197.21 | 1013.22 | 29.17 | 1111 | 99.26 | 28.254 |
|  CBR Proxy | 512M | 200 | 10240 | 0 | 0 | 143.28 | 1393.26 | 430.1 | 2511 | 84.99 | 117.955 |
|  CBR Proxy | 512M | 200 | 10240 | 1000 | 0 | 139.49 | 1431.64 | 269.72 | 2007 | 87.15 | 112.467 |
|  CBR Proxy | 512M | 500 | 1024 | 0 | 0 | 678.4 | 736.05 | 258.53 | 1399 | 87.06 | 116.276 |
|  CBR Proxy | 512M | 500 | 1024 | 1000 | 0 | 467.13 | 1068.56 | 84.53 | 1399 | 91.93 | 115.083 |
|  CBR Proxy | 512M | 500 | 10240 | 0 | 0 | 129.34 | 3856.36 | 1218.19 | 6815 | 79.14 | 202.291 |
|  CBR Proxy | 512M | 500 | 10240 | 1000 | 0 | 134.78 | 3698.71 | 903.64 | 5631 | 79.09 | 198.742 |
|  CBR SOAP Header Proxy | 512M | 100 | 1024 | 0 | 0 | 857.82 | 116.39 | 48.87 | 210 | 97.58 | 28.22 |
|  CBR SOAP Header Proxy | 512M | 100 | 1024 | 1000 | 0 | 98.87 | 1011.27 | 25.36 | 1103 | 99.74 | 28.127 |
|  CBR SOAP Header Proxy | 512M | 100 | 10240 | 0 | 0 | 229.12 | 436.24 | 144.3 | 827 | 94.29 | 89.077 |
|  CBR SOAP Header Proxy | 512M | 100 | 10240 | 1000 | 0 | 125.49 | 795.52 | 366.1 | 1111 | 97.75 | 83.583 |
|  CBR SOAP Header Proxy | 512M | 200 | 1024 | 0 | 0 | 848.33 | 235.77 | 81.71 | 421 | 96.53 | 52.131 |
|  CBR SOAP Header Proxy | 512M | 200 | 1024 | 1000 | 0 | 197.31 | 1011.06 | 25 | 1103 | 99.49 | 28.161 |
|  CBR SOAP Header Proxy | 512M | 200 | 10240 | 0 | 0 | 222.42 | 898.55 | 292.16 | 1607 | 89.73 | 114.071 |
|  CBR SOAP Header Proxy | 512M | 200 | 10240 | 1000 | 0 | 198.45 | 1006.11 | 301.73 | 1423 | 92.75 | 110.184 |
|  CBR SOAP Header Proxy | 512M | 500 | 1024 | 0 | 0 | 772.12 | 647.45 | 228.7 | 1215 | 87.91 | 116.16 |
|  CBR SOAP Header Proxy | 512M | 500 | 1024 | 1000 | 0 | 452.87 | 1102.84 | 98.86 | 1487 | 93.6 | 115.776 |
|  CBR SOAP Header Proxy | 512M | 500 | 10240 | 0 | 0 | 208.25 | 2397.1 | 713.62 | 3999 | 82.17 | 188.644 |
|  CBR SOAP Header Proxy | 512M | 500 | 10240 | 1000 | 0 | 209.53 | 2376.28 | 459.01 | 3503 | 82.25 | 185.429 |
|  CBR Transport Header Proxy | 512M | 100 | 1024 | 0 | 0 | 1238.23 | 80.62 | 41.87 | 192 | 97.65 | 28.137 |
|  CBR Transport Header Proxy | 512M | 100 | 1024 | 1000 | 0 | 98.58 | 1012.61 | 30.29 | 1103 | 99.84 | 28.08 |
|  CBR Transport Header Proxy | 512M | 100 | 10240 | 0 | 0 | 758.13 | 131.77 | 47.44 | 207 | 98.58 | 28.194 |
|  CBR Transport Header Proxy | 512M | 100 | 10240 | 1000 | 0 | 117.47 | 850.53 | 347.9 | 1103 | 99.76 | 28.196 |
|  CBR Transport Header Proxy | 512M | 200 | 1024 | 0 | 0 | 1239.75 | 161.18 | 59.86 | 301 | 97.23 | 24.071 |
|  CBR Transport Header Proxy | 512M | 200 | 1024 | 1000 | 0 | 197.58 | 1011.47 | 28.21 | 1103 | 99.64 | 28.165 |
|  CBR Transport Header Proxy | 512M | 200 | 10240 | 0 | 0 | 744.69 | 268.65 | 70.95 | 415 | 98.25 | 28.179 |
|  CBR Transport Header Proxy | 512M | 200 | 10240 | 1000 | 0 | 271.48 | 735.59 | 405.02 | 1103 | 99.55 | 28.199 |
|  CBR Transport Header Proxy | 512M | 500 | 1024 | 0 | 0 | 1226.7 | 407.77 | 120.79 | 703 | 95.26 | 28.205 |
|  CBR Transport Header Proxy | 512M | 500 | 1024 | 1000 | 0 | 491.6 | 1015.75 | 41.24 | 1135 | 98.41 | 28.176 |
|  CBR Transport Header Proxy | 512M | 500 | 10240 | 0 | 0 | 697.32 | 716.76 | 143.21 | 1103 | 97.22 | 28.154 |
|  CBR Transport Header Proxy | 512M | 500 | 10240 | 1000 | 0 | 637.68 | 782.72 | 403.6 | 1223 | 97.52 | 28.162 |
|  Direct Proxy | 512M | 100 | 1024 | 0 | 0 | 1263.13 | 78.96 | 42.76 | 192 | 97.77 | 28.117 |
|  Direct Proxy | 512M | 100 | 1024 | 1000 | 0 | 98.8 | 1011.41 | 26.38 | 1103 | 99.8 | 28.184 |
|  Direct Proxy | 512M | 100 | 10240 | 0 | 0 | 770.34 | 129.61 | 46.98 | 206 | 98.51 | 28.237 |
|  Direct Proxy | 512M | 100 | 10240 | 1000 | 0.17 | 99.13 | 1007.01 | 4871.8 | 1103 | 99.83 | 28.207 |
|  Direct Proxy | 512M | 200 | 1024 | 0 | 0 | 1268.86 | 157.52 | 58.62 | 299 | 97.17 | 24.058 |
|  Direct Proxy | 512M | 200 | 1024 | 1000 | 0 | 197.22 | 1012.66 | 47.82 | 1111 | 99.7 | 28.304 |
|  Direct Proxy | 512M | 200 | 10240 | 0 | 0 | 745.25 | 268.31 | 69.38 | 409 | 98.4 | 28.263 |
|  Direct Proxy | 512M | 200 | 10240 | 1000 | 0 | 242.88 | 822.7 | 368.59 | 1103 | 99.55 | 28.112 |
|  Direct Proxy | 512M | 500 | 1024 | 0 | 0 | 1241.38 | 402.92 | 118.66 | 699 | 95.08 | 28.167 |
|  Direct Proxy | 512M | 500 | 1024 | 1000 | 0 | 492.17 | 1014.85 | 45.28 | 1127 | 98.45 | 28.215 |
|  Direct Proxy | 512M | 500 | 10240 | 0 | 0 | 702.41 | 711.63 | 140.97 | 1103 | 97.13 | 28.144 |
|  Direct Proxy | 512M | 500 | 10240 | 1000 | 0 | 641.25 | 778.16 | 410.06 | 1295 | 97.43 | 28.183 |
|  XSLT Enhanced Proxy | 512M | 100 | 1024 | 0 | 0 | 690.68 | 144.64 | 55.66 | 297 | 97.16 | 41.65 |
|  XSLT Enhanced Proxy | 512M | 100 | 1024 | 1000 | 0 | 98.96 | 1010.44 | 24.62 | 1103 | 99.68 | 24.949 |
|  XSLT Enhanced Proxy | 512M | 100 | 10240 | 0 | 0 | 136.48 | 732.49 | 211.15 | 1303 | 97.34 | 53.429 |
|  XSLT Enhanced Proxy | 512M | 100 | 10240 | 1000 | 0 | 124.63 | 800.63 | 366.56 | 1199 | 98.55 | 41.682 |
|  XSLT Enhanced Proxy | 512M | 200 | 1024 | 0 | 0 | 676.23 | 295.94 | 95.43 | 599 | 95.83 | 57.692 |
|  XSLT Enhanced Proxy | 512M | 200 | 1024 | 1000 | 0 | 197.94 | 1008.56 | 21.89 | 1103 | 99.29 | 39.787 |
|  XSLT Enhanced Proxy | 512M | 200 | 10240 | 0 | 0 | 129.42 | 1543.58 | 448.35 | 2799 | 94.78 | 84.971 |
|  XSLT Enhanced Proxy | 512M | 200 | 10240 | 1000 | 0 | 126.13 | 1582.69 | 285.15 | 2303 | 95.61 | 80.811 |
|  XSLT Enhanced Proxy | 512M | 500 | 1024 | 0 | 0 | 595.79 | 837.87 | 266.89 | 1607 | 86.05 | 111.942 |
|  XSLT Enhanced Proxy | 512M | 500 | 1024 | 1000 | 0 | 398.46 | 1252.86 | 184.5 | 1799 | 90.34 | 104.321 |
|  XSLT Enhanced Proxy | 512M | 500 | 10240 | 0 | 0 | 126.53 | 3941.47 | 1050.31 | 6815 | 91.68 | 138.711 |
|  XSLT Enhanced Proxy | 512M | 500 | 10240 | 1000 | 0 | 124.79 | 3984.46 | 941.91 | 6719 | 92 | 134.048 |
|  XSLT Proxy | 512M | 100 | 1024 | 0 | 0 | 394.12 | 253.84 | 86.3 | 501 | 96.41 | 106.636 |
|  XSLT Proxy | 512M | 100 | 1024 | 1000 | 0 | 99.35 | 1004.95 | 12.79 | 1079 | 99.53 | 41.385 |
|  XSLT Proxy | 512M | 100 | 10240 | 0 | 0 | 62.34 | 1600.38 | 548.92 | 3199 | 91.44 | 137.643 |
|  XSLT Proxy | 512M | 100 | 10240 | 1000 | 0 | 64.03 | 1559.7 | 273.49 | 2415 | 93.76 | 144.593 |
|  XSLT Proxy | 512M | 200 | 1024 | 0 | 0 | 375.27 | 533.04 | 181.52 | 1103 | 91.74 | 140.347 |
|  XSLT Proxy | 512M | 200 | 1024 | 1000 | 0 | 195 | 1024.21 | 51.86 | 1207 | 97.98 | 103.687 |
|  XSLT Proxy | 512M | 200 | 10240 | 0 | 0 | 58.46 | 3406.5 | 1203.1 | 6815 | 88.9 | 158.678 |
|  XSLT Proxy | 512M | 200 | 10240 | 1000 | 0 | 64.02 | 3112.92 | 760.23 | 5119 | 89.63 | 152.703 |
|  XSLT Proxy | 512M | 500 | 1024 | 0 | 0 | 333.74 | 1496.48 | 501.74 | 2911 | 85.01 | 151.424 |
|  XSLT Proxy | 512M | 500 | 1024 | 1000 | 0 | 314.85 | 1585.38 | 289.96 | 2511 | 85.61 | 144.22 |
|  XSLT Proxy | 512M | 500 | 10240 | 0 | 0 | 50.2 | 9826.2 | 3758.39 | 21375 | 80.07 | 249.092 |
|  XSLT Proxy | 512M | 500 | 10240 | 1000 | 0 | 53.25 | 9270.6 | 3397.69 | 19839 | 81.38 | 243.595 |
