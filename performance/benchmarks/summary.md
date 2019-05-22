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
| AWS | EC2 | AMI-ID | ami-024a64a6685d05041 |
| AWS | EC2 | Instance Type | c5.xlarge |
| System | Processor | CPU(s) | 4 |
| System | Processor | Thread(s) per core | 2 |
| System | Processor | Core(s) per socket | 2 |
| System | Processor | Socket(s) | 1 |
| System | Processor | Model name | Intel(R) Xeon(R) Platinum 8124M CPU @ 3.00GHz |
| System | Memory | BIOS | 64 KiB |
| System | Memory | System memory | 7807996 KiB |
| System | Storage | Block Device: nvme0n1 | 8G |
| Operating System | Distribution | Release | Ubuntu 18.04.2 LTS |
| Operating System | Distribution | Kernel | Linux ip-10-0-1-191 4.15.0-1039-aws #41-Ubuntu SMP Wed May 8 10:43:54 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux |
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
| System | Memory | System memory | 7807996 KiB |
| System | Storage | Block Device: nvme0n1 | 8G |
| Operating System | Distribution | Release | Ubuntu 18.04.2 LTS |
| Operating System | Distribution | Kernel | Linux ip-10-0-1-23 4.15.0-1039-aws #41-Ubuntu SMP Wed May 8 10:43:54 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux |


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
|  CBR Proxy | 512M | 100 | 1024 | 0 | 0 | 930.78 | 107.35 | 47.1 | 203 |  |  |
|  CBR Proxy | 512M | 100 | 1024 | 1000 | 0 | 98.23 | 1017.7 | 32.65 | 1103 |  |  |
|  CBR Proxy | 512M | 100 | 10240 | 0 | 0 | 167.23 | 598.13 | 205.81 | 1207 |  |  |
|  CBR Proxy | 512M | 100 | 10240 | 1000 | 0 | 118.46 | 843.21 | 367.87 | 1391 |  |  |
|  CBR Proxy | 512M | 200 | 1024 | 0 | 0 | 922.67 | 216.58 | 79.18 | 403 |  |  |
|  CBR Proxy | 512M | 200 | 1024 | 1000 | 0 | 197.24 | 1013.32 | 30.38 | 1111 |  |  |
|  CBR Proxy | 512M | 200 | 10240 | 0 | 0 | 148.44 | 1345.27 | 456.69 | 2511 |  |  |
|  CBR Proxy | 512M | 200 | 10240 | 1000 | 0 | 150.01 | 1331.51 | 372.35 | 2111 |  |  |
|  CBR Proxy | 512M | 500 | 1024 | 0 | 0 | 809.26 | 617.55 | 238.65 | 1311 |  |  |
|  CBR Proxy | 512M | 500 | 1024 | 1000 | 0 | 460.7 | 1083.92 | 108.56 | 1599 |  |  |
|  CBR Proxy | 512M | 500 | 10240 | 0 | 0 | 115.19 | 4329.75 | 1353.74 | 7519 |  |  |
|  CBR Proxy | 512M | 500 | 10240 | 1000 | 0 | 120.38 | 4135.03 | 1027.98 | 6527 |  |  |
|  CBR SOAP Header Proxy | 512M | 100 | 1024 | 0 | 0 | 1131.07 | 88.32 | 44.18 | 196 |  |  |
|  CBR SOAP Header Proxy | 512M | 100 | 1024 | 1000 | 0 | 98.35 | 1016.97 | 32.64 | 1103 |  |  |
|  CBR SOAP Header Proxy | 512M | 100 | 10240 | 0 | 0 | 252.92 | 395.57 | 141.09 | 899 |  |  |
|  CBR SOAP Header Proxy | 512M | 100 | 10240 | 1000 | 0 | 119.18 | 837.83 | 338 | 1103 |  |  |
|  CBR SOAP Header Proxy | 512M | 200 | 1024 | 0 | 0 | 1080.98 | 184.77 | 69.56 | 389 |  |  |
|  CBR SOAP Header Proxy | 512M | 200 | 1024 | 1000 | 0 | 195.85 | 1020.58 | 43.82 | 1111 |  |  |
|  CBR SOAP Header Proxy | 512M | 200 | 10240 | 0 | 0 | 243.01 | 822.74 | 294.41 | 1599 |  |  |
|  CBR SOAP Header Proxy | 512M | 200 | 10240 | 1000 | 0 | 209.04 | 954.59 | 366.21 | 1607 |  |  |
|  CBR SOAP Header Proxy | 512M | 500 | 1024 | 0 | 0 | 956.57 | 522.82 | 204.56 | 1199 |  |  |
|  CBR SOAP Header Proxy | 512M | 500 | 1024 | 1000 | 0 | 464.66 | 1074.61 | 107.91 | 1607 |  |  |
|  CBR SOAP Header Proxy | 512M | 500 | 10240 | 0 | 0 | 200.36 | 2487.71 | 785.35 | 4319 |  |  |
|  CBR SOAP Header Proxy | 512M | 500 | 10240 | 1000 | 0 | 200.84 | 2483.77 | 470.22 | 3695 |  |  |
|  CBR Transport Header Proxy | 512M | 100 | 1024 | 0 | 0 | 1679.99 | 59.45 | 43.71 | 109 |  |  |
|  CBR Transport Header Proxy | 512M | 100 | 1024 | 1000 | 0 | 99.42 | 1004.75 | 111.57 | 1103 |  |  |
|  CBR Transport Header Proxy | 512M | 100 | 10240 | 0 | 0 | 939.24 | 106.37 | 38.5 | 195 |  |  |
|  CBR Transport Header Proxy | 512M | 100 | 10240 | 1000 | 0 | 117.98 | 846.17 | 350.26 | 1103 |  |  |
|  CBR Transport Header Proxy | 512M | 200 | 1024 | 0 | 0 | 1750.9 | 114.13 | 48.96 | 204 |  |  |
|  CBR Transport Header Proxy | 512M | 200 | 1024 | 1000 | 0 | 196.97 | 1014.89 | 49.26 | 1111 |  |  |
|  CBR Transport Header Proxy | 512M | 200 | 10240 | 0 | 0 | 941.97 | 212.22 | 56.05 | 315 |  |  |
|  CBR Transport Header Proxy | 512M | 200 | 10240 | 1000 | 0 | 243.69 | 819.73 | 373.95 | 1103 |  |  |
|  CBR Transport Header Proxy | 512M | 500 | 1024 | 0 | 0 | 1682.3 | 297.24 | 83.27 | 501 |  |  |
|  CBR Transport Header Proxy | 512M | 500 | 1024 | 1000 | 0 | 488.67 | 1021.65 | 39.94 | 1127 |  |  |
|  CBR Transport Header Proxy | 512M | 500 | 10240 | 0 | 0 | 862.6 | 579.64 | 114.94 | 899 |  |  |
|  CBR Transport Header Proxy | 512M | 500 | 10240 | 1000 | 0 | 683.93 | 730.17 | 385.91 | 1103 |  |  |
|  Direct Proxy | 512M | 100 | 1024 | 0 | 0 | 1723.13 | 57.97 | 43.1 | 107 |  |  |
|  Direct Proxy | 512M | 100 | 1024 | 1000 | 0 | 98.13 | 1018.2 | 37.73 | 1103 |  |  |
|  Direct Proxy | 512M | 100 | 10240 | 0 | 0 | 955.61 | 104.54 | 37.84 | 195 |  |  |
|  Direct Proxy | 512M | 100 | 10240 | 1000 | 0 | 113.29 | 882.3 | 320.56 | 1103 |  |  |
|  Direct Proxy | 512M | 200 | 1024 | 0 | 0 | 1734.22 | 115.23 | 49 | 204 |  |  |
|  Direct Proxy | 512M | 200 | 1024 | 1000 | 0 | 195.87 | 1019.18 | 40.8 | 1111 |  |  |
|  Direct Proxy | 512M | 200 | 10240 | 0 | 0 | 940.46 | 212.61 | 56.03 | 319 |  |  |
|  Direct Proxy | 512M | 200 | 10240 | 1000 | 0 | 241.32 | 828.62 | 369.62 | 1103 |  |  |
|  Direct Proxy | 512M | 500 | 1024 | 0 | 0 | 1734.98 | 288.3 | 81.29 | 499 |  |  |
|  Direct Proxy | 512M | 500 | 1024 | 1000 | 0 | 486.66 | 1025.87 | 45.1 | 1183 |  |  |
|  Direct Proxy | 512M | 500 | 10240 | 0 | 0 | 865.28 | 577.88 | 114.48 | 895 |  |  |
|  Direct Proxy | 512M | 500 | 10240 | 1000 | 0 | 693.99 | 719.26 | 398.86 | 1111 |  |  |
|  Secure Proxy | 512M | 100 | 1024 | 0 | 0 | 52.49 | 1900.23 | 1019.61 | 5119 |  |  |
|  Secure Proxy | 512M | 100 | 1024 | 1000 | 0 | 55.36 | 1800.88 | 431.16 | 3215 |  |  |
|  Secure Proxy | 512M | 100 | 10240 | 0 | 0 | 26.4 | 3774.46 | 1447.03 | 7903 |  |  |
|  Secure Proxy | 512M | 100 | 10240 | 1000 | 0 | 26.41 | 3772.46 | 1022.35 | 6623 |  |  |
|  Secure Proxy | 512M | 200 | 1024 | 0 | 0 | 50.85 | 3906.99 | 2113.55 | 10431 |  |  |
|  Secure Proxy | 512M | 200 | 1024 | 1000 | 0 | 51.52 | 3861.55 | 1468.68 | 8319 |  |  |
|  Secure Proxy | 512M | 200 | 10240 | 0 | 0 | 24.4 | 8127.14 | 3160.33 | 17535 |  |  |
|  Secure Proxy | 512M | 200 | 10240 | 1000 | 0 | 23.48 | 8401.98 | 2733.89 | 16255 |  |  |
|  Secure Proxy | 512M | 500 | 1024 | 0 | 0 | 46.57 | 10530.78 | 6149.82 | 29311 |  |  |
|  Secure Proxy | 512M | 500 | 1024 | 1000 | 0 | 46.5 | 10550.91 | 5889.81 | 28927 |  |  |
|  Secure Proxy | 512M | 500 | 10240 | 0 | 0 | 15.6 | 31043.92 | 14461.09 | 72191 |  |  |
|  Secure Proxy | 512M | 500 | 10240 | 1000 | 0 | 14.39 | 33626.63 | 15240.31 | 82943 |  |  |
|  XSLT Enhanced Proxy | 512M | 100 | 1024 | 0 | 0 | 831.51 | 120.18 | 48.27 | 209 |  |  |
|  XSLT Enhanced Proxy | 512M | 100 | 1024 | 1000 | 0 | 98.79 | 1012.01 | 26.22 | 1103 |  |  |
|  XSLT Enhanced Proxy | 512M | 100 | 10240 | 0 | 0 | 137.97 | 724.62 | 207.42 | 1295 |  |  |
|  XSLT Enhanced Proxy | 512M | 100 | 10240 | 1000 | 0 | 124.6 | 801.31 | 351.71 | 1079 |  |  |
|  XSLT Enhanced Proxy | 512M | 200 | 1024 | 0 | 0 | 846.16 | 236.37 | 78.99 | 417 |  |  |
|  XSLT Enhanced Proxy | 512M | 200 | 1024 | 1000 | 0 | 198.57 | 1006.14 | 35.79 | 1095 |  |  |
|  XSLT Enhanced Proxy | 512M | 200 | 10240 | 0 | 0 | 139.28 | 1432.52 | 406.8 | 2527 |  |  |
|  XSLT Enhanced Proxy | 512M | 200 | 10240 | 1000 | 0 | 129.1 | 1546.11 | 332.67 | 2415 |  |  |
|  XSLT Enhanced Proxy | 512M | 500 | 1024 | 0 | 0 | 696.31 | 717.94 | 257.71 | 1407 |  |  |
|  XSLT Enhanced Proxy | 512M | 500 | 1024 | 1000 | 0 | 432.16 | 1154.04 | 151.22 | 1703 |  |  |
|  XSLT Enhanced Proxy | 512M | 500 | 10240 | 0 | 0 | 125.42 | 3966.6 | 1035.85 | 6719 |  |  |
|  XSLT Enhanced Proxy | 512M | 500 | 10240 | 1000 | 0 | 125.17 | 3968.31 | 952.28 | 6719 |  |  |
|  XSLT Proxy | 512M | 100 | 1024 | 0 | 0 | 452.3 | 221.1 | 78.43 | 407 |  |  |
|  XSLT Proxy | 512M | 100 | 1024 | 1000 | 0 | 98.87 | 1010.99 | 26.81 | 1111 |  |  |
|  XSLT Proxy | 512M | 100 | 10240 | 0 | 0 | 64.44 | 1550.05 | 545.4 | 3199 |  |  |
|  XSLT Proxy | 512M | 100 | 10240 | 1000 | 0 | 62.38 | 1596.32 | 334.68 | 2607 |  |  |
|  XSLT Proxy | 512M | 200 | 1024 | 0 | 0 | 431.72 | 463.41 | 185.44 | 1103 |  |  |
|  XSLT Proxy | 512M | 200 | 1024 | 1000 | 0 | 194.67 | 1026.49 | 60.15 | 1287 |  |  |
|  XSLT Proxy | 512M | 200 | 10240 | 0 | 0 | 62.66 | 3179.77 | 1102.59 | 6399 |  |  |
|  XSLT Proxy | 512M | 200 | 10240 | 1000 | 0 | 63.38 | 3142.37 | 823.07 | 5407 |  |  |
|  XSLT Proxy | 512M | 500 | 1024 | 0 | 0 | 357.39 | 1394.3 | 499.61 | 2895 |  |  |
|  XSLT Proxy | 512M | 500 | 1024 | 1000 | 0 | 321.28 | 1553.28 | 275.77 | 2319 |  |  |
|  XSLT Proxy | 512M | 500 | 10240 | 0 | 0 | 47.21 | 10438.89 | 4226.78 | 22783 |  |  |
|  XSLT Proxy | 512M | 500 | 10240 | 1000 | 0 | 47.03 | 10495.66 | 3779.77 | 23423 |  |  |
