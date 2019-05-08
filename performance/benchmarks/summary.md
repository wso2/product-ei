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
| Operating System | Distribution | Kernel | Linux ip-10-0-1-11 4.15.0-1037-aws #39-Ubuntu SMP Tue Apr 16 08:09:09 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux |
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
| Operating System | Distribution | Kernel | Linux ip-10-0-1-63 4.15.0-1037-aws #39-Ubuntu SMP Tue Apr 16 08:09:09 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux |


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
|  CBR Proxy | 512M | 10 | 1024 | 0 | 0 | 253.15 | 39.46 | 46.27 | 100 | 98.89 | 28.162 |
|  CBR Proxy | 512M | 10 | 1024 | 1000 | 0 | 9.54 | 1046.74 | 54.55 | 1199 | 99.8 | 28.164 |
|  CBR Proxy | 512M | 10 | 10240 | 0 | 0 | 54.85 | 179.25 | 75.61 | 399 | 99 | 28.119 |
|  CBR Proxy | 512M | 10 | 10240 | 1000 | 0 | 12.61 | 792.71 | 389.49 | 1103 | 99.65 | 28.188 |
|  CBR Proxy | 512M | 20 | 1024 | 0 | 0 | 265.58 | 75.17 | 47.02 | 198 | 98.91 | 28.135 |
|  CBR Proxy | 512M | 20 | 1024 | 1000 | 0 | 19.44 | 1027.46 | 44.93 | 1199 | 99.76 | 28.198 |
|  CBR Proxy | 512M | 20 | 10240 | 0 | 0 | 52.43 | 381.57 | 147.9 | 803 | 98.66 | 28.233 |
|  CBR Proxy | 512M | 20 | 10240 | 1000 | 0 | 27.1 | 736.97 | 400.31 | 1199 | 99.34 | 28.25 |
|  CBR Proxy | 512M | 50 | 1024 | 0 | 0 | 297.84 | 167.77 | 67.08 | 303 | 98.52 | 28.127 |
|  CBR Proxy | 512M | 50 | 1024 | 1000 | 0 | 48.63 | 1026.78 | 54.85 | 1199 | 99.61 | 28.159 |
|  CBR Proxy | 512M | 50 | 10240 | 0 | 0 | 56.09 | 891.24 | 309.19 | 1799 | 97.1 | 28.147 |
|  CBR Proxy | 512M | 50 | 10240 | 1000 | 0 | 55.21 | 904.56 | 366.78 | 1407 | 97.63 | 28.197 |
|  CBR Proxy | 512M | 100 | 1024 | 0 | 0 | 297.42 | 335.98 | 108.96 | 607 | 97.9 | 28.193 |
|  CBR Proxy | 512M | 100 | 1024 | 1000 | 0 | 96.84 | 1031.68 | 47.93 | 1199 | 99.12 | 28.148 |
|  CBR Proxy | 512M | 100 | 10240 | 0 | 0 | 51.93 | 1922.12 | 679.7 | 3999 | 92.68 | 84.576 |
|  CBR Proxy | 512M | 100 | 10240 | 1000 | 0 | 51.01 | 1954.81 | 484.88 | 3615 | 93.11 | 81.904 |
|  CBR SOAP Header Proxy | 512M | 10 | 1024 | 0 | 0 | 284.99 | 35.05 | 45.19 | 101 | 98.97 | 28.251 |
|  CBR SOAP Header Proxy | 512M | 10 | 1024 | 1000 | 0 | 9.59 | 1042.65 | 54.83 | 1199 | 99.8 | 28.107 |
|  CBR SOAP Header Proxy | 512M | 10 | 10240 | 0 | 0 | 84.2 | 118.7 | 55.69 | 297 | 99.25 | 28.153 |
|  CBR SOAP Header Proxy | 512M | 10 | 10240 | 1000 | 0 | 14.88 | 671.22 | 431.52 | 1103 | 99.78 | 28.195 |
|  CBR SOAP Header Proxy | 512M | 20 | 1024 | 0 | 0 | 300.95 | 66.39 | 47.83 | 196 | 98.89 | 28.353 |
|  CBR SOAP Header Proxy | 512M | 20 | 1024 | 1000 | 0 | 19.46 | 1026.5 | 43.24 | 1199 | 99.77 | 28.154 |
|  CBR SOAP Header Proxy | 512M | 20 | 10240 | 0 | 0 | 75.31 | 265.63 | 111.4 | 603 | 99 | 28.151 |
|  CBR SOAP Header Proxy | 512M | 20 | 10240 | 1000 | 0 | 24.4 | 819.07 | 370.65 | 1111 | 99.69 | 28.19 |
|  CBR SOAP Header Proxy | 512M | 50 | 1024 | 0 | 0 | 319.6 | 156.3 | 64.85 | 301 | 98.58 | 28.115 |
|  CBR SOAP Header Proxy | 512M | 50 | 1024 | 1000 | 0 | 48.7 | 1025.9 | 42.5 | 1111 | 99.63 | 28.105 |
|  CBR SOAP Header Proxy | 512M | 50 | 10240 | 0 | 0 | 74.06 | 674.85 | 258.51 | 1503 | 98.27 | 28.332 |
|  CBR SOAP Header Proxy | 512M | 50 | 10240 | 1000 | 0 | 65.92 | 757.22 | 396.64 | 1207 | 98.76 | 28.16 |
|  CBR SOAP Header Proxy | 512M | 100 | 1024 | 0 | 0 | 338.24 | 295.31 | 100.29 | 599 | 98.1 | 28.198 |
|  CBR SOAP Header Proxy | 512M | 100 | 1024 | 1000 | 0 | 96.38 | 1036.6 | 51.81 | 1207 | 99.38 | 28.194 |
|  CBR SOAP Header Proxy | 512M | 100 | 10240 | 0 | 0 | 83.28 | 1198.94 | 415.22 | 2511 | 95.1 | 81.96 |
|  CBR SOAP Header Proxy | 512M | 100 | 10240 | 1000 | 0 | 71.74 | 1391.78 | 385.01 | 2511 | 95.41 | 78.106 |
|  CBR Transport Header Proxy | 512M | 10 | 1024 | 0 | 0 | 393.85 | 25.35 | 40.94 | 99 | 98.79 | 28.347 |
|  CBR Transport Header Proxy | 512M | 10 | 1024 | 1000 | 0 | 9.68 | 1032.93 | 125.64 | 1199 | 99.79 | 28.223 |
|  CBR Transport Header Proxy | 512M | 10 | 10240 | 0 | 0 | 259.3 | 38.51 | 45.99 | 100 | 99.13 | 28.104 |
|  CBR Transport Header Proxy | 512M | 10 | 10240 | 1000 | 0 | 15.95 | 626.34 | 434.09 | 1103 | 99.79 | 28.332 |
|  CBR Transport Header Proxy | 512M | 20 | 1024 | 0 | 0 | 410.19 | 48.54 | 47.86 | 103 | 98.7 | 28.12 |
|  CBR Transport Header Proxy | 512M | 20 | 1024 | 1000 | 0 | 19.53 | 1022.51 | 126.05 | 1199 | 99.78 | 28.186 |
|  CBR Transport Header Proxy | 512M | 20 | 10240 | 0 | 0 | 267.91 | 74.57 | 47.56 | 197 | 99.13 | 28.177 |
|  CBR Transport Header Proxy | 512M | 20 | 10240 | 1000 | 0 | 28.86 | 692.27 | 425.33 | 1103 | 99.75 | 28.216 |
|  CBR Transport Header Proxy | 512M | 50 | 1024 | 0 | 0 | 453.61 | 109.87 | 50.74 | 203 | 98.53 | 28.147 |
|  CBR Transport Header Proxy | 512M | 50 | 1024 | 1000 | 0 | 48.38 | 1033.06 | 75.99 | 1199 | 99.65 | 28.133 |
|  CBR Transport Header Proxy | 512M | 50 | 10240 | 0 | 0 | 272 | 183.61 | 67.99 | 397 | 98.95 | 28.158 |
|  CBR Transport Header Proxy | 512M | 50 | 10240 | 1000 | 0 | 55.8 | 895.34 | 319.51 | 1199 | 99.62 | 28.157 |
|  CBR Transport Header Proxy | 512M | 100 | 1024 | 0 | 0 | 468.86 | 213.12 | 81.16 | 407 | 98.31 | 28.192 |
|  CBR Transport Header Proxy | 512M | 100 | 1024 | 1000 | 0 | 97.16 | 1027.12 | 63.21 | 1191 | 99.55 | 28.124 |
|  CBR Transport Header Proxy | 512M | 100 | 10240 | 0 | 0 | 271.81 | 368.03 | 103.92 | 611 | 98.86 | 28.32 |
|  CBR Transport Header Proxy | 512M | 100 | 10240 | 1000 | 0 | 123.07 | 811.29 | 351.06 | 1103 | 99.32 | 28.159 |
|  Direct Proxy | 512M | 10 | 1024 | 0 | 0 | 404.5 | 24.68 | 40.52 | 99 | 98.89 | 28.089 |
|  Direct Proxy | 512M | 10 | 1024 | 1000 | 0 | 9.72 | 1028.57 | 72.76 | 1199 | 99.81 | 28.159 |
|  Direct Proxy | 512M | 10 | 10240 | 0 | 0 | 264.7 | 37.72 | 45.86 | 100 | 99.24 | 28.213 |
|  Direct Proxy | 512M | 10 | 10240 | 1000 | 0 | 16.29 | 613.66 | 439.67 | 1103 | 99.81 | 28.199 |
|  Direct Proxy | 512M | 20 | 1024 | 0 | 0 | 414.53 | 48.19 | 47.96 | 102 | 98.78 | 28.283 |
|  Direct Proxy | 512M | 20 | 1024 | 1000 | 0 | 19.68 | 1015.19 | 88.54 | 1199 | 99.76 | 28.134 |
|  Direct Proxy | 512M | 20 | 10240 | 0 | 0 | 269.82 | 74.05 | 47.31 | 196 | 99.08 | 28.124 |
|  Direct Proxy | 512M | 20 | 10240 | 1000 | 0 | 24.17 | 826.51 | 371.11 | 1103 | 99.71 | 28.15 |
|  Direct Proxy | 512M | 50 | 1024 | 0 | 0 | 454.01 | 109.94 | 50.52 | 205 | 98.61 | 28.167 |
|  Direct Proxy | 512M | 50 | 1024 | 1000 | 0 | 48.82 | 1023.05 | 63.62 | 1111 | 99.68 | 28.209 |
|  Direct Proxy | 512M | 50 | 10240 | 0 | 0 | 277.73 | 179.95 | 67.02 | 397 | 98.87 | 28.132 |
|  Direct Proxy | 512M | 50 | 10240 | 1000 | 0 | 56.21 | 889.12 | 318.54 | 1111 | 99.63 | 28.204 |
|  Direct Proxy | 512M | 100 | 1024 | 0 | 0 | 498.75 | 200.4 | 72.76 | 399 | 98.42 | 28.126 |
|  Direct Proxy | 512M | 100 | 1024 | 1000 | 0 | 96.79 | 1031.35 | 49.78 | 1199 | 99.43 | 28.176 |
|  Direct Proxy | 512M | 100 | 10240 | 0 | 0 | 282.38 | 354.16 | 98.88 | 603 | 98.82 | 28.143 |
|  Direct Proxy | 512M | 100 | 10240 | 1000 | 0 | 122.99 | 812.45 | 353.86 | 1103 | 99.34 | 28.138 |
|  XSLT Enhanced Proxy | 512M | 10 | 1024 | 0 | 0 | 219.91 | 45.37 | 47.27 | 102 | 99.02 | 38.595 |
|  XSLT Enhanced Proxy | 512M | 10 | 1024 | 1000 | 0 | 9.38 | 1065.89 | 61.8 | 1199 | 99.7 | 38.061 |
|  XSLT Enhanced Proxy | 512M | 10 | 10240 | 0 | 0 | 33.71 | 296.73 | 134.99 | 703 | 99.26 | 39.379 |
|  XSLT Enhanced Proxy | 512M | 10 | 10240 | 1000 | 0 | 12.15 | 822.71 | 390.23 | 1207 | 99.65 | 40.369 |
|  XSLT Enhanced Proxy | 512M | 20 | 1024 | 0 | 0 | 223.78 | 89.3 | 47.69 | 199 | 99.01 | 38.391 |
|  XSLT Enhanced Proxy | 512M | 20 | 1024 | 1000 | 0 | 19.5 | 1024.83 | 43.35 | 1199 | 99.67 | 39.12 |
|  XSLT Enhanced Proxy | 512M | 20 | 10240 | 0 | 0 | 33.73 | 592.5 | 245.37 | 1399 | 99.2 | 39.998 |
|  XSLT Enhanced Proxy | 512M | 20 | 10240 | 1000 | 0 | 21.25 | 940.41 | 286.96 | 1303 | 99.55 | 39.699 |
|  XSLT Enhanced Proxy | 512M | 50 | 1024 | 0 | 0 | 252.82 | 197.47 | 72.75 | 399 | 98.39 | 39.758 |
|  XSLT Enhanced Proxy | 512M | 50 | 1024 | 1000 | 0 | 48.67 | 1026.39 | 45.27 | 1199 | 99.55 | 28.192 |
|  XSLT Enhanced Proxy | 512M | 50 | 10240 | 0 | 0 | 33.93 | 1470.01 | 586.94 | 3311 | 98.68 | 24.016 |
|  XSLT Enhanced Proxy | 512M | 50 | 10240 | 1000 | 0 | 31.68 | 1574.92 | 520.22 | 3103 | 98.89 | 41.604 |
|  XSLT Enhanced Proxy | 512M | 100 | 1024 | 0 | 0 | 256.85 | 389.37 | 139.54 | 803 | 97.67 | 43.281 |
|  XSLT Enhanced Proxy | 512M | 100 | 1024 | 1000 | 0 | 96.27 | 1037.92 | 55.45 | 1207 | 99.01 | 41.284 |
|  XSLT Enhanced Proxy | 512M | 100 | 10240 | 0 | 0 | 36.12 | 2763.87 | 1049.76 | 5919 | 97.83 | 43.171 |
|  XSLT Enhanced Proxy | 512M | 100 | 10240 | 1000 | 0 | 35.71 | 2791.31 | 818.28 | 5119 | 98.41 | 43.295 |
|  XSLT Proxy | 512M | 10 | 1024 | 0 | 0 | 137.28 | 72.76 | 47.92 | 197 | 98.49 | 41.865 |
|  XSLT Proxy | 512M | 10 | 1024 | 1000 | 0 | 9.62 | 1038.34 | 52.63 | 1199 | 99.68 | 41.36 |
|  XSLT Proxy | 512M | 10 | 10240 | 0 | 0 | 26.39 | 379 | 134.41 | 799 | 98.84 | 43.888 |
|  XSLT Proxy | 512M | 10 | 10240 | 1000 | 0 | 9.34 | 1069.45 | 80.05 | 1303 | 99.52 | 43.136 |
|  XSLT Proxy | 512M | 20 | 1024 | 0 | 0 | 143.05 | 139.57 | 63.09 | 301 | 98.37 | 41.861 |
|  XSLT Proxy | 512M | 20 | 1024 | 1000 | 0 | 19.47 | 1025.49 | 43.99 | 1199 | 99.64 | 41.648 |
|  XSLT Proxy | 512M | 20 | 10240 | 0 | 0 | 24.54 | 814.62 | 283.83 | 1607 | 98.43 | 43.746 |
|  XSLT Proxy | 512M | 20 | 10240 | 1000 | 0 | 17.56 | 1137.59 | 125.76 | 1503 | 99.11 | 43.139 |
|  XSLT Proxy | 512M | 50 | 1024 | 0 | 0 | 141.68 | 353 | 130.93 | 799 | 97.97 | 42.489 |
|  XSLT Proxy | 512M | 50 | 1024 | 1000 | 0 | 48.4 | 1031.16 | 50.49 | 1199 | 99.27 | 41.912 |
|  XSLT Proxy | 512M | 50 | 10240 | 0 | 0 | 25.54 | 1952.43 | 724.36 | 4127 | 95.44 | 127.633 |
|  XSLT Proxy | 512M | 50 | 10240 | 1000 | 0 | 23.48 | 2121.98 | 508.14 | 3711 | 97.31 | 93.954 |
|  XSLT Proxy | 512M | 100 | 1024 | 0 | 0 | 141.61 | 706.19 | 268.46 | 1599 | 97.01 | 74.957 |
|  XSLT Proxy | 512M | 100 | 1024 | 1000 | 0 | 92.13 | 1083.09 | 106.5 | 1503 | 98.1 | 42.673 |
|  XSLT Proxy | 512M | 100 | 10240 | 0 | 0 | 22.71 | 4385.57 | 1693.36 | 9535 | 92.1 | 134.95 |
|  XSLT Proxy | 512M | 100 | 10240 | 1000 | 0 | 23.11 | 4302.35 | 1241.03 | 7615 | 92.61 | 133.375 |
