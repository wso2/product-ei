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
| System | Memory | System memory | 7625 MiB |
| System | Storage | Block Device: nvme0n1 | 8G |
| Operating System | Distribution | Release | Ubuntu 18.04.2 LTS |
| Operating System | Distribution | Kernel | Linux ip-10-0-1-108 4.15.0-1037-aws #39-Ubuntu SMP Tue Apr 16 08:09:09 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux |
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
| Operating System | Distribution | Kernel | Linux ip-10-0-1-123 4.15.0-1037-aws #39-Ubuntu SMP Tue Apr 16 08:09:09 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux |


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
|  CBR Proxy | 512M | 10 | 1024 | 0 | 0 | 340.01 | 29.37 | 42.13 | 98 | 99.02 | 28.145 |
|  CBR Proxy | 512M | 10 | 1024 | 1000 | 0 | 9.55 | 1047.05 | 54.16 | 1199 | 99.82 | 23.999 |
|  CBR Proxy | 512M | 10 | 10240 | 0 | 0 | 73.63 | 135.72 | 57.21 | 297 | 98.93 | 28.101 |
|  CBR Proxy | 512M | 10 | 10240 | 1000 | 0 | 12.14 | 823.3 | 362.59 | 1103 | 99.76 | 28.177 |
|  CBR Proxy | 512M | 20 | 1024 | 0 | 0 | 338 | 59.09 | 47.09 | 108 | 98.85 | 24.093 |
|  CBR Proxy | 512M | 20 | 1024 | 1000 | 0 | 19.61 | 1018.91 | 37.23 | 1103 | 99.84 | 28.138 |
|  CBR Proxy | 512M | 20 | 10240 | 0 | 0 | 71.21 | 280.99 | 103.3 | 599 | 98.38 | 28.151 |
|  CBR Proxy | 512M | 20 | 10240 | 1000 | 0 | 24.31 | 822.37 | 364.95 | 1103 | 99.56 | 28.184 |
|  CBR Proxy | 512M | 50 | 1024 | 0 | 0 | 378.01 | 132.16 | 56.42 | 297 | 98.49 | 28.203 |
|  CBR Proxy | 512M | 50 | 1024 | 1000 | 0 | 48.92 | 1020.9 | 37.89 | 1111 | 99.69 | 28.145 |
|  CBR Proxy | 512M | 50 | 10240 | 0 | 0 | 74.28 | 670.87 | 224.67 | 1303 | 96.94 | 28.103 |
|  CBR Proxy | 512M | 50 | 10240 | 1000 | 0 | 63.76 | 783.21 | 383.18 | 1199 | 98.34 | 28.167 |
|  CBR Proxy | 512M | 100 | 1024 | 0 | 0 | 385.47 | 259.51 | 88.04 | 499 | 97.96 | 28.153 |
|  CBR Proxy | 512M | 100 | 1024 | 1000 | 0 | 97.23 | 1027.52 | 45.27 | 1199 | 99.33 | 28.164 |
|  CBR Proxy | 512M | 100 | 10240 | 0 | 0 | 73.99 | 1350.79 | 467.57 | 2703 | 92.22 | 84.442 |
|  CBR Proxy | 512M | 100 | 10240 | 1000 | 0 | 69.77 | 1430.86 | 320.81 | 2303 | 93.08 | 83.889 |
|  CBR SOAP Header Proxy | 512M | 10 | 1024 | 0 | 0 | 374.35 | 26.67 | 40.84 | 99 | 98.81 | 28.146 |
|  CBR SOAP Header Proxy | 512M | 10 | 1024 | 1000 | 0 | 9.76 | 1023.67 | 43.75 | 1199 | 99.84 | 28.149 |
|  CBR SOAP Header Proxy | 512M | 10 | 10240 | 0 | 0 | 108.73 | 91.87 | 44.24 | 199 | 99.1 | 28.167 |
|  CBR SOAP Header Proxy | 512M | 10 | 10240 | 1000 | 0 | 12.03 | 829.7 | 349.83 | 1103 | 99.78 | 28.063 |
|  CBR SOAP Header Proxy | 512M | 20 | 1024 | 0 | 0 | 389.07 | 51.34 | 47 | 103 | 98.83 | 28.071 |
|  CBR SOAP Header Proxy | 512M | 20 | 1024 | 1000 | 0 | 19.47 | 1026.97 | 44.31 | 1199 | 99.82 | 28.271 |
|  CBR SOAP Header Proxy | 512M | 20 | 10240 | 0 | 0 | 106.54 | 187.7 | 479.94 | 397 | 99.01 | 28.125 |
|  CBR SOAP Header Proxy | 512M | 20 | 10240 | 1000 | 0 | 27.46 | 727.61 | 406.22 | 1103 | 99.7 | 28.172 |
|  CBR SOAP Header Proxy | 512M | 50 | 1024 | 0 | 0 | 426.87 | 116.93 | 51.65 | 208 | 98.56 | 28.254 |
|  CBR SOAP Header Proxy | 512M | 50 | 1024 | 1000 | 0 | 48.86 | 1022.36 | 38.62 | 1103 | 99.79 | 28.108 |
|  CBR SOAP Header Proxy | 512M | 50 | 10240 | 0 | 0 | 109.41 | 457.04 | 151.37 | 895 | 98.18 | 28.198 |
|  CBR SOAP Header Proxy | 512M | 50 | 10240 | 1000 | 0 | 66.3 | 753.21 | 388.81 | 1111 | 99.14 | 28.229 |
|  CBR SOAP Header Proxy | 512M | 100 | 1024 | 0 | 0 | 428.17 | 233.52 | 81.45 | 495 | 98.03 | 28.15 |
|  CBR SOAP Header Proxy | 512M | 100 | 1024 | 1000 | 0 | 97.99 | 1018.51 | 36.76 | 1111 | 99.58 | 28.201 |
|  CBR SOAP Header Proxy | 512M | 100 | 10240 | 0 | 0 | 108.6 | 920.37 | 325.41 | 1903 | 94.71 | 84.618 |
|  CBR SOAP Header Proxy | 512M | 100 | 10240 | 1000 | 0 | 98.66 | 1011.78 | 310.56 | 1695 | 95.84 | 82.083 |
|  CBR Transport Header Proxy | 512M | 10 | 1024 | 0 | 0 | 515.51 | 19.36 | 36.4 | 97 | 98.8 | 28.204 |
|  CBR Transport Header Proxy | 512M | 10 | 1024 | 1000 | 0 | 9.97 | 1002.35 | 103.23 | 1103 | 99.82 | 28.119 |
|  CBR Transport Header Proxy | 512M | 10 | 10240 | 0 | 0 | 332.54 | 30.01 | 42.36 | 98 | 99.15 | 28.105 |
|  CBR Transport Header Proxy | 512M | 10 | 10240 | 1000 | 0 | 10.16 | 983.09 | 164.69 | 1103 | 99.82 | 28.182 |
|  CBR Transport Header Proxy | 512M | 20 | 1024 | 0 | 0 | 532.58 | 37.49 | 45.01 | 100 | 98.85 | 28.23 |
|  CBR Transport Header Proxy | 512M | 20 | 1024 | 1000 | 0 | 19.58 | 1020.49 | 82.4 | 1199 | 99.85 | 28.183 |
|  CBR Transport Header Proxy | 512M | 20 | 10240 | 0 | 0 | 339.12 | 58.9 | 46.93 | 105 | 99.08 | 28.177 |
|  CBR Transport Header Proxy | 512M | 20 | 10240 | 1000 | 0 | 25.5 | 782.98 | 380.79 | 1103 | 99.8 | 28.229 |
|  CBR Transport Header Proxy | 512M | 50 | 1024 | 0 | 0 | 578.31 | 86.3 | 45.52 | 198 | 98.5 | 28.156 |
|  CBR Transport Header Proxy | 512M | 50 | 1024 | 1000 | 0 | 49.17 | 1015.79 | 94.6 | 1103 | 99.75 | 28.189 |
|  CBR Transport Header Proxy | 512M | 50 | 10240 | 0 | 0 | 358.03 | 139.54 | 54.78 | 297 | 98.99 | 24.113 |
|  CBR Transport Header Proxy | 512M | 50 | 10240 | 1000 | 0 | 59.62 | 837.13 | 362.52 | 1103 | 99.69 | 28.17 |
|  CBR Transport Header Proxy | 512M | 100 | 1024 | 0 | 0 | 619.05 | 161.4 | 63.11 | 303 | 98.22 | 28.107 |
|  CBR Transport Header Proxy | 512M | 100 | 1024 | 1000 | 0 | 96.94 | 1030.31 | 47.05 | 1199 | 99.54 | 28.218 |
|  CBR Transport Header Proxy | 512M | 100 | 10240 | 0 | 0 | 355.62 | 281.28 | 80.89 | 499 | 98.89 | 24.055 |
|  CBR Transport Header Proxy | 512M | 100 | 10240 | 1000 | 0 | 120.99 | 825.27 | 374.54 | 1111 | 99.56 | 28.039 |
|  Direct Proxy | 512M | 10 | 1024 | 0 | 0 | 524.21 | 19.03 | 36.11 | 96 | 98.86 | 28.164 |
|  Direct Proxy | 512M | 10 | 1024 | 1000 | 0 | 9.92 | 1006.64 | 79.06 | 1103 | 99.8 | 28.197 |
|  Direct Proxy | 512M | 10 | 10240 | 0 | 0 | 342.19 | 29.16 | 41.99 | 98 | 99.17 | 28.094 |
|  Direct Proxy | 512M | 10 | 10240 | 1000 | 0 | 10.32 | 967.91 | 178.54 | 1103 | 99.83 | 28.153 |
|  Direct Proxy | 512M | 20 | 1024 | 0 | 0 | 546.85 | 36.51 | 44.8 | 101 | 98.78 | 28.221 |
|  Direct Proxy | 512M | 20 | 1024 | 1000 | 0 | 19.69 | 1015.72 | 34.08 | 1103 | 99.85 | 28.186 |
|  Direct Proxy | 512M | 20 | 10240 | 0 | 0 | 343.97 | 58.03 | 47.14 | 105 | 99.07 | 24.062 |
|  Direct Proxy | 512M | 20 | 10240 | 1000 | 0 | 19.92 | 1002.97 | 128.08 | 1103 | 99.85 | 28.211 |
|  Direct Proxy | 512M | 50 | 1024 | 0 | 0 | 597.98 | 83.54 | 45.78 | 198 | 98.43 | 24.092 |
|  Direct Proxy | 512M | 50 | 1024 | 1000 | 0 | 49.12 | 1016.76 | 34.04 | 1103 | 99.8 | 28.108 |
|  Direct Proxy | 512M | 50 | 10240 | 0 | 0 | 365.36 | 136.7 | 53.11 | 295 | 99.05 | 28.193 |
|  Direct Proxy | 512M | 50 | 10240 | 1000 | 0 | 61.02 | 819.23 | 373.82 | 1103 | 99.74 | 28.122 |
|  Direct Proxy | 512M | 100 | 1024 | 0 | 0 | 621.41 | 160.62 | 63.9 | 303 | 98.19 | 24.125 |
|  Direct Proxy | 512M | 100 | 1024 | 1000 | 0 | 97.11 | 1029.03 | 50.47 | 1191 | 99.59 | 24.083 |
|  Direct Proxy | 512M | 100 | 10240 | 0 | 0 | 352.1 | 284.1 | 86.82 | 503 | 98.85 | 28.127 |
|  Direct Proxy | 512M | 100 | 10240 | 1000 | 0 | 116.43 | 858.15 | 345.87 | 1111 | 99.54 | 28.087 |
|  XSLT Enhanced Proxy | 512M | 10 | 1024 | 0 | 0 | 289.98 | 34.41 | 44.12 | 100 | 98.86 | 37.717 |
|  XSLT Enhanced Proxy | 512M | 10 | 1024 | 1000 | 0 | 9.5 | 1052.22 | 62.34 | 1199 | 99.81 | 28.146 |
|  XSLT Enhanced Proxy | 512M | 10 | 10240 | 0 | 0 | 51.96 | 192.42 | 77.28 | 401 | 99.41 | 40.913 |
|  XSLT Enhanced Proxy | 512M | 10 | 10240 | 1000 | 0 | 12.06 | 829.06 | 379.12 | 1199 | 99.73 | 24.091 |
|  XSLT Enhanced Proxy | 512M | 20 | 1024 | 0 | 0 | 306.18 | 65.25 | 46.85 | 192 | 98.52 | 24.135 |
|  XSLT Enhanced Proxy | 512M | 20 | 1024 | 1000 | 0 | 19.6 | 1020.26 | 37.66 | 1111 | 99.77 | 39.711 |
|  XSLT Enhanced Proxy | 512M | 20 | 10240 | 0 | 0 | 52.61 | 380.11 | 140.85 | 799 | 99.09 | 39.881 |
|  XSLT Enhanced Proxy | 512M | 20 | 10240 | 1000 | 0 | 21.2 | 942.17 | 247.14 | 1199 | 99.6 | 39.483 |
|  XSLT Enhanced Proxy | 512M | 50 | 1024 | 0 | 0 | 336.58 | 148.48 | 59.85 | 299 | 98.15 | 39.792 |
|  XSLT Enhanced Proxy | 512M | 50 | 1024 | 1000 | 0 | 48.94 | 1021.3 | 38.94 | 1111 | 99.62 | 40.797 |
|  XSLT Enhanced Proxy | 512M | 50 | 10240 | 0 | 0 | 53.64 | 930.54 | 327.76 | 1903 | 98.43 | 40.376 |
|  XSLT Enhanced Proxy | 512M | 50 | 10240 | 1000 | 0 | 59.32 | 841.87 | 395.07 | 1407 | 98.87 | 40.039 |
|  XSLT Enhanced Proxy | 512M | 100 | 1024 | 0 | 0 | 313.43 | 319 | 114.89 | 699 | 97.54 | 43.331 |
|  XSLT Enhanced Proxy | 512M | 100 | 1024 | 1000 | 0 | 97.3 | 1027.19 | 49.88 | 1199 | 99.35 | 40.323 |
|  XSLT Enhanced Proxy | 512M | 100 | 10240 | 0 | 0 | 56.79 | 1758.43 | 580.3 | 3407 | 97.44 | 43.313 |
|  XSLT Enhanced Proxy | 512M | 100 | 10240 | 1000 | 0 | 54.8 | 1820.08 | 428.81 | 3103 | 97.85 | 41.488 |
|  XSLT Proxy | 512M | 10 | 1024 | 0 | 0 | 182.51 | 54.74 | 47.39 | 106 | 98.43 | 40.815 |
|  XSLT Proxy | 512M | 10 | 1024 | 1000 | 0 | 9.76 | 1024.3 | 40.72 | 1199 | 99.77 | 40.598 |
|  XSLT Proxy | 512M | 10 | 10240 | 0 | 0 | 32.54 | 307.55 | 103.73 | 603 | 98.75 | 43.49 |
|  XSLT Proxy | 512M | 10 | 10240 | 1000 | 0 | 9.55 | 1046.16 | 59.13 | 1207 | 99.62 | 43.519 |
|  XSLT Proxy | 512M | 20 | 1024 | 0 | 0 | 181.72 | 109.96 | 51.01 | 204 | 98.47 | 42.025 |
|  XSLT Proxy | 512M | 20 | 1024 | 1000 | 0 | 19.72 | 1012.37 | 28.45 | 1103 | 99.68 | 40.794 |
|  XSLT Proxy | 512M | 20 | 10240 | 0 | 0 | 32.9 | 608.06 | 208.96 | 1207 | 98.42 | 43.104 |
|  XSLT Proxy | 512M | 20 | 10240 | 1000 | 0 | 18.58 | 1075.1 | 91.05 | 1311 | 99.27 | 42.414 |
|  XSLT Proxy | 512M | 50 | 1024 | 0 | 0 | 197.32 | 253.4 | 90.04 | 501 | 97.83 | 41.307 |
|  XSLT Proxy | 512M | 50 | 1024 | 1000 | 0 | 48.86 | 1022.08 | 40.51 | 1199 | 99.52 | 40.752 |
|  XSLT Proxy | 512M | 50 | 10240 | 0 | 0 | 31.73 | 1573.52 | 556.31 | 3215 | 95.7 | 132.115 |
|  XSLT Proxy | 512M | 50 | 10240 | 1000 | 0 | 32.64 | 1528.85 | 269.9 | 2511 | 97.7 | 96.555 |
|  XSLT Proxy | 512M | 100 | 1024 | 0 | 0 | 186.97 | 534.99 | 188.29 | 1103 | 97.05 | 73.198 |
|  XSLT Proxy | 512M | 100 | 1024 | 1000 | 0 | 96.8 | 1030.93 | 52.65 | 1207 | 98.77 | 42.152 |
|  XSLT Proxy | 512M | 100 | 10240 | 0 | 0 | 30.29 | 3290.12 | 1160.6 | 6719 | 92.24 | 134.483 |
|  XSLT Proxy | 512M | 100 | 10240 | 1000 | 0 | 29.4 | 3395.04 | 912.52 | 5919 | 92.59 | 135.255 |
