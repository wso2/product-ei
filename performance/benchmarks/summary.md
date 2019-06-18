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
| AWS | EC2 | AMI-ID | ami-095192256fe1477ad |
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
| Operating System | Distribution | Kernel | Linux ip-10-0-1-253 4.15.0-1041-aws #43-Ubuntu SMP Thu Jun 6 13:39:11 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux |
System information for WSO2 Enterprise Micro Integrator in 2nd AWS CloudFormation stack.

| Class | Subclass | Description | Value |
| --- | --- | --- | --- |
| AWS | EC2 | AMI-ID | ami-095192256fe1477ad |
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
| Operating System | Distribution | Kernel | Linux ip-10-0-1-96 4.15.0-1041-aws #43-Ubuntu SMP Thu Jun 6 13:39:11 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux |
System information for WSO2 Enterprise Micro Integrator in 3rd AWS CloudFormation stack.

| Class | Subclass | Description | Value |
| --- | --- | --- | --- |
| AWS | EC2 | AMI-ID | ami-095192256fe1477ad |
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
| Operating System | Distribution | Kernel | Linux ip-10-0-1-243 4.15.0-1041-aws #43-Ubuntu SMP Thu Jun 6 13:39:11 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux |


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
|  CBR Proxy | 2G | 100 | 1024 | 0 | 0 | 4193.15 | 23.71 | 26 | 112 |  |  |
|  CBR Proxy | 2G | 100 | 1024 | 1000 | 0 | 99.68 | 1002.79 | 6.98 | 1039 |  |  |
|  CBR Proxy | 2G | 100 | 10240 | 0 | 0 | 771.82 | 129.39 | 53.41 | 279 |  |  |
|  CBR Proxy | 2G | 100 | 10240 | 1000 | 0 | 99.39 | 1003.82 | 5.45 | 1031 |  |  |
|  CBR Proxy | 2G | 200 | 1024 | 0 | 0 | 3996.91 | 49.94 | 38.81 | 186 |  |  |
|  CBR Proxy | 2G | 200 | 1024 | 1000 | 0 | 199.31 | 1002.51 | 5.25 | 1015 |  |  |
|  CBR Proxy | 2G | 200 | 10240 | 0 | 0 | 745.34 | 268.43 | 98.85 | 523 |  |  |
|  CBR Proxy | 2G | 200 | 10240 | 1000 | 0 | 198.48 | 1006.25 | 11.1 | 1063 |  |  |
|  CBR Proxy | 2G | 500 | 1024 | 0 | 0 | 3991.43 | 124.88 | 62.19 | 309 |  |  |
|  CBR Proxy | 2G | 500 | 1024 | 1000 | 0 | 497.61 | 1003 | 5.73 | 1023 |  |  |
|  CBR Proxy | 2G | 500 | 10240 | 0 | 0 | 718.6 | 695.72 | 240.53 | 1327 |  |  |
|  CBR Proxy | 2G | 500 | 10240 | 1000 | 0 | 495.09 | 1008.07 | 14.23 | 1079 |  |  |
|  CBR SOAP Header Proxy | 2G | 100 | 1024 | 0 | 0 | 4998.69 | 19.9 | 23.53 | 102 |  |  |
|  CBR SOAP Header Proxy | 2G | 100 | 1024 | 1000 | 0 | 99.7 | 1002.37 | 3.62 | 1011 |  |  |
|  CBR SOAP Header Proxy | 2G | 100 | 10240 | 0 | 0 | 1150.69 | 86.63 | 149.55 | 188 |  |  |
|  CBR SOAP Header Proxy | 2G | 100 | 10240 | 1000 | 0 | 99.52 | 1002.96 | 7.09 | 1019 |  |  |
|  CBR SOAP Header Proxy | 2G | 200 | 1024 | 0 | 0 | 4648.84 | 42.93 | 35.78 | 173 |  |  |
|  CBR SOAP Header Proxy | 2G | 200 | 1024 | 1000 | 0 | 199.09 | 1003.28 | 7.46 | 1047 |  |  |
|  CBR SOAP Header Proxy | 2G | 200 | 10240 | 0 | 0 | 1108.65 | 180.34 | 66.63 | 363 |  |  |
|  CBR SOAP Header Proxy | 2G | 200 | 10240 | 1000 | 0 | 199.06 | 1003.99 | 8.37 | 1047 |  |  |
|  CBR SOAP Header Proxy | 2G | 500 | 1024 | 0 | 0 | 4581.33 | 109.02 | 55.17 | 281 |  |  |
|  CBR SOAP Header Proxy | 2G | 500 | 1024 | 1000 | 0 | 497.73 | 1003.04 | 6.97 | 1031 |  |  |
|  CBR SOAP Header Proxy | 2G | 500 | 10240 | 0 | 0 | 1099.81 | 454.76 | 150.92 | 831 |  |  |
|  CBR SOAP Header Proxy | 2G | 500 | 10240 | 1000 | 0 | 496.66 | 1004.92 | 9.49 | 1055 |  |  |
|  CBR Transport Header Proxy | 2G | 100 | 1024 | 0 | 0 | 7667.98 | 12.93 | 18.25 | 83 |  |  |
|  CBR Transport Header Proxy | 2G | 100 | 1024 | 1000 | 0 | 99.57 | 1003.09 | 9.44 | 1063 |  |  |
|  CBR Transport Header Proxy | 2G | 100 | 10240 | 0 | 0 | 3936.6 | 25.19 | 21.58 | 74 |  |  |
|  CBR Transport Header Proxy | 2G | 100 | 10240 | 1000 | 0 | 99.55 | 1003.08 | 8.18 | 1055 |  |  |
|  CBR Transport Header Proxy | 2G | 200 | 1024 | 0 | 0 | 7306.16 | 27.29 | 27.25 | 109 |  |  |
|  CBR Transport Header Proxy | 2G | 200 | 1024 | 1000 | 0 | 199.12 | 1004.3 | 12.55 | 1087 |  |  |
|  CBR Transport Header Proxy | 2G | 200 | 10240 | 0 | 0 | 3691.37 | 54.05 | 26.79 | 101 |  |  |
|  CBR Transport Header Proxy | 2G | 200 | 10240 | 1000 | 0 | 199.18 | 1003.36 | 39.14 | 1039 |  |  |
|  CBR Transport Header Proxy | 2G | 500 | 1024 | 0 | 0 | 7307.35 | 68.21 | 40.27 | 192 |  |  |
|  CBR Transport Header Proxy | 2G | 500 | 1024 | 1000 | 0 | 497.78 | 1003.64 | 8.77 | 1055 |  |  |
|  CBR Transport Header Proxy | 2G | 500 | 10240 | 0 | 0 | 3633.57 | 137.45 | 42.04 | 254 |  |  |
|  CBR Transport Header Proxy | 2G | 500 | 10240 | 1000 | 0 | 497.33 | 1003.79 | 7.65 | 1039 |  |  |
|  Direct Proxy | 2G | 100 | 1024 | 0 | 0 | 7837.58 | 12.68 | 17.62 | 79 |  |  |
|  Direct Proxy | 2G | 100 | 1024 | 1000 | 0 | 99.64 | 1003 | 8.71 | 1055 |  |  |
|  Direct Proxy | 2G | 100 | 10240 | 0 | 0 | 3965.49 | 25.11 | 21.09 | 72 |  |  |
|  Direct Proxy | 2G | 100 | 10240 | 1000 | 0 | 99.41 | 1004.16 | 12.28 | 1087 |  |  |
|  Direct Proxy | 2G | 200 | 1024 | 0 | 0 | 7443.85 | 26.78 | 27.17 | 108 |  |  |
|  Direct Proxy | 2G | 200 | 1024 | 1000 | 0 | 199.16 | 1003.04 | 7.64 | 1055 |  |  |
|  Direct Proxy | 2G | 200 | 10240 | 0 | 0 | 3190.83 | 54.78 | 85.69 | 103 |  |  |
|  Direct Proxy | 2G | 200 | 10240 | 1000 | 0 | 199.22 | 1002.65 | 5.92 | 1015 |  |  |
|  Direct Proxy | 2G | 500 | 1024 | 0 | 0 | 7620.6 | 65.42 | 40.57 | 195 |  |  |
|  Direct Proxy | 2G | 500 | 1024 | 1000 | 0 | 497.62 | 1003.87 | 8.92 | 1055 |  |  |
|  Direct Proxy | 2G | 500 | 10240 | 0 | 0 | 3723.93 | 133.9 | 41.81 | 249 |  |  |
|  Direct Proxy | 2G | 500 | 10240 | 1000 | 0 | 497.5 | 1003.97 | 9.2 | 1055 |  |  |
|  Secure Proxy | 2G | 100 | 1024 | 0 | 0 | 252.37 | 396 | 264.07 | 1207 |  |  |
|  Secure Proxy | 2G | 100 | 1024 | 1000 | 0 | 98.89 | 1009.88 | 11.75 | 1071 |  |  |
|  Secure Proxy | 2G | 100 | 10240 | 0 | 0 | 141.6 | 705.74 | 235.36 | 1351 |  |  |
|  Secure Proxy | 2G | 100 | 10240 | 1000 | 0 | 98.13 | 1017.76 | 18.31 | 1103 |  |  |
|  Secure Proxy | 2G | 200 | 1024 | 0 | 0 | 245.05 | 815.38 | 549.46 | 2447 |  |  |
|  Secure Proxy | 2G | 200 | 1024 | 1000 | 0 | 197.95 | 1008.92 | 4.29 | 1031 |  |  |
|  Secure Proxy | 2G | 200 | 10240 | 0 | 0 | 134.56 | 1484.22 | 472.67 | 2767 |  |  |
|  Secure Proxy | 2G | 200 | 10240 | 1000 | 0 | 131.86 | 1513.16 | 205.22 | 2127 |  |  |
|  Secure Proxy | 2G | 500 | 1024 | 0 | 0 | 236.31 | 2109.74 | 1201.37 | 5727 |  |  |
|  Secure Proxy | 2G | 500 | 1024 | 1000 | 0 | 243.52 | 2046.82 | 707.37 | 4223 |  |  |
|  Secure Proxy | 2G | 500 | 10240 | 0 | 0 | 118.87 | 4186.85 | 1163.2 | 7327 |  |  |
|  Secure Proxy | 2G | 500 | 10240 | 1000 | 0 | 116 | 4295.1 | 1138.45 | 7295 |  |  |
|  XSLT Enhanced Proxy | 2G | 100 | 1024 | 0 | 0 | 3650.04 | 27.33 | 29.22 | 123 |  |  |
|  XSLT Enhanced Proxy | 2G | 100 | 1024 | 1000 | 0 | 99.62 | 1003 | 6.83 | 1039 |  |  |
|  XSLT Enhanced Proxy | 2G | 100 | 10240 | 0 | 0 | 594.05 | 168.28 | 65.56 | 349 |  |  |
|  XSLT Enhanced Proxy | 2G | 100 | 10240 | 1000 | 0 | 99.29 | 1005.16 | 11.47 | 1071 |  |  |
|  XSLT Enhanced Proxy | 2G | 200 | 1024 | 0 | 0 | 3530.38 | 56.58 | 42.33 | 213 |  |  |
|  XSLT Enhanced Proxy | 2G | 200 | 1024 | 1000 | 0 | 199.36 | 1002.94 | 6.86 | 1019 |  |  |
|  XSLT Enhanced Proxy | 2G | 200 | 10240 | 0 | 0 | 575.4 | 347.72 | 129.5 | 699 |  |  |
|  XSLT Enhanced Proxy | 2G | 200 | 10240 | 1000 | 0 | 198.85 | 1003.92 | 3.71 | 1019 |  |  |
|  XSLT Enhanced Proxy | 2G | 500 | 1024 | 0 | 0 | 3381.97 | 147.54 | 77.27 | 409 |  |  |
|  XSLT Enhanced Proxy | 2G | 500 | 1024 | 1000 | 0 | 497.75 | 1003.49 | 7.13 | 1031 |  |  |
|  XSLT Enhanced Proxy | 2G | 500 | 10240 | 0 | 0 | 584.28 | 855.08 | 294.21 | 1655 |  |  |
|  XSLT Enhanced Proxy | 2G | 500 | 10240 | 1000 | 0 | 489.56 | 1019.83 | 24.76 | 1111 |  |  |
|  XSLT Proxy | 2G | 100 | 1024 | 0 | 0 | 2055.38 | 48.52 | 38.24 | 199 |  |  |
|  XSLT Proxy | 2G | 100 | 1024 | 1000 | 0 | 99.46 | 1003.64 | 9.5 | 1063 |  |  |
|  XSLT Proxy | 2G | 100 | 10240 | 0 | 0 | 305.72 | 327.15 | 159.02 | 799 |  |  |
|  XSLT Proxy | 2G | 100 | 10240 | 1000 | 0 | 99.25 | 1006.74 | 3.86 | 1031 |  |  |
|  XSLT Proxy | 2G | 200 | 1024 | 0 | 0 | 1977.86 | 101.04 | 53.14 | 289 |  |  |
|  XSLT Proxy | 2G | 200 | 1024 | 1000 | 0 | 198.97 | 1003.8 | 8.66 | 1047 |  |  |
|  XSLT Proxy | 2G | 200 | 10240 | 0 | 0 | 295.63 | 676.43 | 297.06 | 1535 |  |  |
|  XSLT Proxy | 2G | 200 | 10240 | 1000 | 0 | 198.3 | 1007.32 | 4.65 | 1039 |  |  |
|  XSLT Proxy | 2G | 500 | 1024 | 0 | 0 | 1889.4 | 264.24 | 106.53 | 587 |  |  |
|  XSLT Proxy | 2G | 500 | 1024 | 1000 | 0 | 497.38 | 1003.83 | 6.83 | 1039 |  |  |
|  XSLT Proxy | 2G | 500 | 10240 | 0 | 0 | 278.11 | 1794.87 | 712.76 | 3807 |  |  |
|  XSLT Proxy | 2G | 500 | 10240 | 1000 | 0 | 270.69 | 1842.3 | 393.75 | 3007 |  |  |
