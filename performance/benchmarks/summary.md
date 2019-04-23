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
| Operating System | Distribution | Kernel | Linux ip-10-0-1-124 4.15.0-1035-aws #37-Ubuntu SMP Mon Mar 18 16:15:14 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux |
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
| Operating System | Distribution | Kernel | Linux ip-10-0-1-53 4.15.0-1035-aws #37-Ubuntu SMP Mon Mar 18 16:15:14 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux |
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
| Operating System | Distribution | Kernel | Linux ip-10-0-1-99 4.15.0-1035-aws #37-Ubuntu SMP Mon Mar 18 16:15:14 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux |


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
|  CBR Proxy | 512M | 100 | 1024 | 0 | 0 | 3031.64 | 32.91 | 32.26 | 161 | 94.19 | 28.037 |
|  CBR Proxy | 512M | 100 | 1024 | 1000 | 0 | 99.59 | 1002.58 | 4.96 | 1019 | 99.82 | 28.145 |
|  CBR Proxy | 512M | 100 | 10240 | 0 | 0 | 622.05 | 160.69 | 75.73 | 391 | 84.32 | 89.705 |
|  CBR Proxy | 512M | 100 | 10240 | 1000 | 0 | 132.77 | 752.09 | 371.98 | 1055 | 97.3 | 83.723 |
|  CBR Proxy | 512M | 200 | 1024 | 0 | 0 | 2975.19 | 67.13 | 44.94 | 218 | 92.54 | 71.531 |
|  CBR Proxy | 512M | 200 | 1024 | 1000 | 0 | 199.28 | 1002.74 | 5.53 | 1023 | 99.54 | 28.084 |
|  CBR Proxy | 512M | 200 | 10240 | 0 | 0 | 527.74 | 379.19 | 159.05 | 811 | 71.25 | 119.831 |
|  CBR Proxy | 512M | 200 | 10240 | 1000 | 0 | 227.09 | 879.53 | 357.92 | 1287 | 90.75 | 113.095 |
|  CBR Proxy | 512M | 500 | 1024 | 0 | 0 | 2590.05 | 193.04 | 101.42 | 485 | 75.73 | 117.854 |
|  CBR Proxy | 512M | 500 | 1024 | 1000 | 0 | 495.17 | 1008.84 | 24.25 | 1135 | 95.91 | 116.322 |
|  CBR Proxy | 512M | 500 | 10240 | 0 | 0 | 459.73 | 1086.23 | 398.23 | 2143 | 61.44 | 205.223 |
|  CBR Proxy | 512M | 500 | 10240 | 1000 | 0 | 423.26 | 1178.94 | 454.35 | 1975 | 68.17 | 192.862 |
|  CBR SOAP Header Proxy | 512M | 100 | 1024 | 0 | 0 | 3303.92 | 30.18 | 29.87 | 132 | 94.67 | 28.082 |
|  CBR SOAP Header Proxy | 512M | 100 | 1024 | 1000 | 0 | 99.66 | 1002.5 | 4.01 | 1019 | 99.84 | 28.1 |
|  CBR SOAP Header Proxy | 512M | 100 | 10240 | 0 | 0 | 937.12 | 106.61 | 52.46 | 287 | 88.96 | 90.722 |
|  CBR SOAP Header Proxy | 512M | 100 | 10240 | 1000 | 0 | 111.91 | 892.46 | 295.47 | 1047 | 98.95 | 80.98 |
|  CBR SOAP Header Proxy | 512M | 200 | 1024 | 0 | 0 | 3351.23 | 59.59 | 40.07 | 191 | 92.51 | 70.682 |
|  CBR SOAP Header Proxy | 512M | 200 | 1024 | 1000 | 0 | 199.18 | 1002.79 | 5.85 | 1023 | 99.61 | 28.119 |
|  CBR SOAP Header Proxy | 512M | 200 | 10240 | 0 | 0 | 868.8 | 230.21 | 99.74 | 497 | 79.5 | 114.48 |
|  CBR SOAP Header Proxy | 512M | 200 | 10240 | 1000 | 0 | 262 | 762.39 | 369.06 | 1095 | 95.6 | 109.988 |
|  CBR SOAP Header Proxy | 512M | 500 | 1024 | 0 | 0 | 2946.31 | 169.62 | 90.82 | 429 | 77.13 | 117.214 |
|  CBR SOAP Header Proxy | 512M | 500 | 1024 | 1000 | 0 | 493.81 | 1011.32 | 27.84 | 1167 | 96.6 | 116.563 |
|  CBR SOAP Header Proxy | 512M | 500 | 10240 | 0 | 0 | 729.67 | 684.54 | 247.24 | 1271 | 67.89 | 190.443 |
|  CBR SOAP Header Proxy | 512M | 500 | 10240 | 1000 | 0 | 546.11 | 914.5 | 368.8 | 1431 | 78.18 | 181.562 |
|  CBR Transport Header Proxy | 512M | 100 | 1024 | 0 | 0 | 4830.06 | 20.63 | 23.54 | 102 | 95.35 | 28.116 |
|  CBR Transport Header Proxy | 512M | 100 | 1024 | 1000 | 0 | 99.92 | 1000.21 | 48.07 | 1031 | 99.89 | 28.078 |
|  CBR Transport Header Proxy | 512M | 100 | 10240 | 0 | 0 | 2952.69 | 33.75 | 26.37 | 100 | 97.22 | 28.116 |
|  CBR Transport Header Proxy | 512M | 100 | 10240 | 1000 | 0 | 116.03 | 860.88 | 322.96 | 1011 | 99.88 | 28.07 |
|  CBR Transport Header Proxy | 512M | 200 | 1024 | 0 | 0 | 4817.82 | 41.43 | 32.75 | 156 | 94.06 | 28.071 |
|  CBR Transport Header Proxy | 512M | 200 | 1024 | 1000 | 0 | 199.89 | 999.7 | 48.76 | 1031 | 99.78 | 28.07 |
|  CBR Transport Header Proxy | 512M | 200 | 10240 | 0 | 0 | 2965.19 | 67.3 | 32.38 | 160 | 96.37 | 28.085 |
|  CBR Transport Header Proxy | 512M | 200 | 10240 | 1000 | 0 | 225.65 | 884.87 | 297.73 | 1019 | 99.75 | 28.219 |
|  CBR Transport Header Proxy | 512M | 500 | 1024 | 0 | 0 | 4785.29 | 104.38 | 57.82 | 299 | 91.06 | 59.214 |
|  CBR Transport Header Proxy | 512M | 500 | 1024 | 1000 | 0 | 501.3 | 997.35 | 67.79 | 1031 | 99.19 | 28.081 |
|  CBR Transport Header Proxy | 512M | 500 | 10240 | 0 | 0 | 2868.51 | 174.11 | 56 | 317 | 94.45 | 28.156 |
|  CBR Transport Header Proxy | 512M | 500 | 10240 | 1000 | 0 | 570.09 | 876.44 | 310.59 | 1023 | 99.1 | 28.067 |
|  Direct Proxy | 512M | 100 | 1024 | 0 | 0 | 4959.81 | 20.08 | 23.01 | 101 | 95.3 | 28.044 |
|  Direct Proxy | 512M | 100 | 1024 | 1000 | 0 | 99.68 | 1001.81 | 31.8 | 1019 | 99.89 | 27.974 |
|  Direct Proxy | 512M | 100 | 10240 | 0 | 0 | 2974.54 | 33.5 | 26.61 | 103 | 97.25 | 28.117 |
|  Direct Proxy | 512M | 100 | 10240 | 1000 | 0 | 102.47 | 974.99 | 156.94 | 1015 | 99.89 | 28.031 |
|  Direct Proxy | 512M | 200 | 1024 | 0 | 0 | 5145.45 | 38.78 | 31.66 | 144 | 94.18 | 24.028 |
|  Direct Proxy | 512M | 200 | 1024 | 1000 | 0 | 199.44 | 1002.02 | 7.2 | 1003 | 99.78 | 28.083 |
|  Direct Proxy | 512M | 200 | 10240 | 0 | 0 | 3118.62 | 63.99 | 32.99 | 161 | 96.49 | 28.097 |
|  Direct Proxy | 512M | 200 | 10240 | 1000 | 0 | 207.53 | 962.78 | 191.47 | 1003 | 99.78 | 28.147 |
|  Direct Proxy | 512M | 500 | 1024 | 0 | 0 | 4833.09 | 103.26 | 57.52 | 299 | 91.07 | 59.034 |
|  Direct Proxy | 512M | 500 | 1024 | 1000 | 0 | 500.24 | 998.25 | 63.35 | 1031 | 99.21 | 28.104 |
|  Direct Proxy | 512M | 500 | 10240 | 0 | 0 | 2963.35 | 168.49 | 55.6 | 309 | 94.49 | 28.115 |
|  Direct Proxy | 512M | 500 | 10240 | 1000 | 0 | 599.94 | 832.72 | 331.94 | 1019 | 99.06 | 28.074 |
|  XSLT Enhanced Proxy | 512M | 100 | 1024 | 0 | 0 | 2666.07 | 37.45 | 36.79 | 195 | 94.11 | 49.063 |
|  XSLT Enhanced Proxy | 512M | 100 | 1024 | 1000 | 0 | 99.59 | 1002.95 | 6.29 | 1019 | 99.79 | 40.354 |
|  XSLT Enhanced Proxy | 512M | 100 | 10240 | 0 | 0 | 469.85 | 186.98 | 222.78 | 385 | 94.98 | 66.233 |
|  XSLT Enhanced Proxy | 512M | 100 | 10240 | 1000 | 0 | 115.19 | 867.05 | 316.37 | 1063 | 99.17 | 40.358 |
|  XSLT Enhanced Proxy | 512M | 200 | 1024 | 0 | 0 | 2666.87 | 74.93 | 55.53 | 291 | 90.66 | 76.285 |
|  XSLT Enhanced Proxy | 512M | 200 | 1024 | 1000 | 0 | 199.05 | 1003.59 | 6.98 | 1031 | 99.45 | 43.372 |
|  XSLT Enhanced Proxy | 512M | 200 | 10240 | 0 | 0 | 522.04 | 383.26 | 141.91 | 775 | 90.25 | 87.965 |
|  XSLT Enhanced Proxy | 512M | 200 | 10240 | 1000 | 0 | 272.77 | 732.06 | 387.91 | 1127 | 95.97 | 80.213 |
|  XSLT Enhanced Proxy | 512M | 500 | 1024 | 0 | 0 | 2281.69 | 218.82 | 177.63 | 831 | 72.53 | 121.82 |
|  XSLT Enhanced Proxy | 512M | 500 | 1024 | 1000 | 0 | 490.32 | 1018.7 | 30.47 | 1127 | 94.61 | 104.044 |
|  XSLT Enhanced Proxy | 512M | 500 | 10240 | 0 | 0 | 514.46 | 971.05 | 335.31 | 1847 | 83.89 | 139.732 |
|  XSLT Enhanced Proxy | 512M | 500 | 10240 | 1000 | 0 | 488.66 | 1021.57 | 346.91 | 1503 | 88.17 | 115.227 |
|  XSLT Proxy | 512M | 100 | 1024 | 0 | 0 | 1577.36 | 63.34 | 52 | 287 | 92.43 | 136.15 |
|  XSLT Proxy | 512M | 100 | 1024 | 1000 | 0 | 99.51 | 1003.58 | 8.12 | 1039 | 99.63 | 41.049 |
|  XSLT Proxy | 512M | 100 | 10240 | 0 | 0 | 245.95 | 406.71 | 210.99 | 1011 | 83.64 | 143.163 |
|  XSLT Proxy | 512M | 100 | 10240 | 1000 | 0 | 98.53 | 1013.6 | 59.96 | 1151 | 96.01 | 146.113 |
|  XSLT Proxy | 512M | 200 | 1024 | 0 | 0 | 1470.15 | 136 | 83.93 | 417 | 82.31 | 143.996 |
|  XSLT Proxy | 512M | 200 | 1024 | 1000 | 0 | 198.39 | 1006.48 | 17.3 | 1095 | 98.75 | 107.926 |
|  XSLT Proxy | 512M | 200 | 10240 | 0 | 0 | 234.87 | 851.02 | 395.44 | 1991 | 77.78 | 162.467 |
|  XSLT Proxy | 512M | 200 | 10240 | 1000 | 0 | 179.95 | 1109.76 | 107.81 | 1455 | 87.69 | 145.672 |
|  XSLT Proxy | 512M | 500 | 1024 | 0 | 0 | 1300.07 | 384.64 | 180.04 | 915 | 68.97 | 157.868 |
|  XSLT Proxy | 512M | 500 | 1024 | 1000 | 0 | 487.84 | 1023.51 | 39.05 | 1215 | 89.84 | 141.926 |
|  XSLT Proxy | 512M | 500 | 10240 | 0 | 0 | 174.76 | 2851.07 | 1294.11 | 6751 | 61.02 | 248.9 |
|  XSLT Proxy | 512M | 500 | 10240 | 1000 | 0 | 205.63 | 2425.34 | 670.5 | 4447 | 71.67 | 228.602 |
