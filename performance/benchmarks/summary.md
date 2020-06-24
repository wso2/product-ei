# WSO2 Enterprise Integrator Performance Test Results

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

1. **Throughput**: The number of requests that the WSO2 Enterprise Integrator processes during a specific time interval (e.g. per second).
2. **Response Time**: The end-to-end latency for an operation of invoking a service in WSO2 Enterprise Integrator . The complete distribution of response times was recorded.

In addition to the above metrics, we measure the load average and several memory-related metrics.

The following are the test parameters.

| Test Parameter | Description | Values |
| --- | --- | --- |
| Scenario Name | The name of the test scenario. | Refer to the above table. |
| Heap Size | The amount of memory allocated to the application | 4G |
| Concurrent Users | The number of users accessing the application at the same time. | 100, 200 |
| Message Size (Bytes) | The request payload size in Bytes. | 500, 1024, 10240, 102400 |
| Back-end Delay (ms) | The delay added by the Back-end service. | 0, 30, 100, 500, 1000 |

The duration of each test is **60 seconds**. The warm-up period is **30 seconds**.
The measurement results are collected after the warm-up period.

The performance tests were executed on 1 AWS CloudFormation stack.


System information for WSO2 Enterprise Integrator in 1st AWS CloudFormation stack.

| Class | Subclass | Description | Value |
| --- | --- | --- | --- |
| AWS | EC2 | AMI-ID | ami-0ac80df6eff0e70b5 |
| AWS | EC2 | Instance Type | c5.large |
| System | Processor | CPU(s) | 2 |
| System | Processor | Thread(s) per core | 2 |
| System | Processor | Core(s) per socket | 1 |
| System | Processor | Socket(s) | 1 |
| System | Processor | Model name | Intel(R) Xeon(R) Platinum 8124M CPU @ 3.00GHz |
| System | Memory | BIOS | 64 KiB |
| System | Memory | System memory | 3785420 KiB |
| System | Storage | Block Device: nvme0n1 | 8G |
| Operating System | Distribution | Release | Ubuntu 18.04.4 LTS |
| Operating System | Distribution | Kernel | Linux ip-10-0-1-124 5.3.0-1023-aws #25~18.04.1-Ubuntu SMP Fri Jun 5 15:18:30 UTC 2020 x86_64 x86_64 x86_64 GNU/Linux |


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
|  CBR Proxy | 4G | 100 | 500 | 0 | 0 | 3746.18 | 20.11 | 14.71 | 79 | 94.84 | 49.367 |
|  CBR Proxy | 4G | 100 | 500 | 30 | 0 | 2354.09 | 32.03 | 3.64 | 49 | 95.24 | 49.453 |
|  CBR Proxy | 4G | 100 | 500 | 100 | 0 | 743.3 | 101.38 | 2.92 | 109 | 95.12 | 49.413 |
|  CBR Proxy | 4G | 100 | 500 | 500 | 0 | 148.96 | 501.4 | 0.88 | 503 | 95.95 | 49.764 |
|  CBR Proxy | 4G | 100 | 500 | 1000 | 0 | 73.89 | 1002.02 | 0.36 | 1003 | 91.91 | 49.338 |
|  CBR Proxy | 4G | 100 | 1024 | 0 | 0 | 3224.35 | 23.33 | 16.28 | 84 | 94.88 | 49.329 |
|  CBR Proxy | 4G | 100 | 1024 | 30 | 0 | 2297.72 | 32.83 | 4.57 | 54 | 95.16 | 49.351 |
|  CBR Proxy | 4G | 100 | 1024 | 100 | 0 | 742.82 | 101.53 | 2.15 | 108 | 95.52 | 49.433 |
|  CBR Proxy | 4G | 100 | 1024 | 500 | 0 | 149.06 | 501.97 | 1.58 | 505 | 95.6 | 49.388 |
|  CBR Proxy | 4G | 100 | 1024 | 1000 | 0 | 73.99 | 1002.03 | 0.51 | 1003 | 92.07 | 49.464 |
|  CBR Proxy | 4G | 100 | 10240 | 0 | 0 | 779.97 | 96.77 | 43.76 | 220 | 94.9 | 49.36 |
|  CBR Proxy | 4G | 100 | 10240 | 30 | 0 | 730.03 | 103.41 | 35.61 | 214 | 94.69 | 49.798 |
|  CBR Proxy | 4G | 100 | 10240 | 100 | 0 | 575.16 | 131.1 | 23.04 | 210 | 94.87 | 49.324 |
|  CBR Proxy | 4G | 100 | 10240 | 500 | 0 | 147.92 | 505.67 | 5.95 | 535 | 95.23 | 49.431 |
|  CBR Proxy | 4G | 100 | 10240 | 1000 | 0 | 73.65 | 1003.39 | 3.56 | 1015 | 95.24 | 49.448 |
|  CBR Proxy | 4G | 100 | 102400 | 0 | 0 | 87.05 | 864.51 | 249.13 | 1495 | 93.36 | 52.883 |
|  CBR Proxy | 4G | 100 | 102400 | 30 | 0 | 87.71 | 858.45 | 256.25 | 1455 | 92.75 | 49.399 |
|  CBR Proxy | 4G | 100 | 102400 | 100 | 0 | 86.66 | 865.2 | 251.03 | 1527 | 92.81 | 49.46 |
|  CBR Proxy | 4G | 100 | 102400 | 500 | 0 | 80.38 | 928.73 | 196.9 | 1439 | 93.15 | 49.459 |
|  CBR Proxy | 4G | 100 | 102400 | 1000 | 0 | 61.61 | 1202.87 | 121.32 | 1559 | 93.91 | 47.213 |
|  CBR Proxy | 4G | 200 | 500 | 0 | 0 | 3804.8 | 39.44 | 26.44 | 141 | 94.76 | 49.492 |
|  CBR Proxy | 4G | 200 | 500 | 30 | 0 | 3837.05 | 39.14 | 9.3 | 78 | 95.62 | 52.836 |
|  CBR Proxy | 4G | 200 | 500 | 100 | 0 | 1473.48 | 101.93 | 9.72 | 117 | 95.51 | 49.43 |
|  CBR Proxy | 4G | 200 | 500 | 500 | 0 | 296.68 | 501.38 | 2.48 | 505 | 94.62 | 49.758 |
|  CBR Proxy | 4G | 200 | 500 | 1000 | 0 | 146.97 | 1002.07 | 1.09 | 1003 | 95.68 | 49.445 |
|  CBR Proxy | 4G | 200 | 1024 | 0 | 0 | 3258.81 | 46.05 | 27.11 | 139 | 94.94 | 49.4 |
|  CBR Proxy | 4G | 200 | 1024 | 30 | 0 | 3354.83 | 44.77 | 10.25 | 84 | 95.02 | 49.518 |
|  CBR Proxy | 4G | 200 | 1024 | 100 | 0 | 1468.5 | 102.25 | 3.7 | 120 | 95.23 | 49.986 |
|  CBR Proxy | 4G | 200 | 1024 | 500 | 0 | 296.78 | 501.58 | 2.02 | 507 | 95.38 | 49.438 |
|  CBR Proxy | 4G | 200 | 1024 | 1000 | 0 | 147.16 | 1002.09 | 1.32 | 1007 | 95.3 | 49.393 |
|  CBR Proxy | 4G | 200 | 10240 | 0 | 0 | 810.45 | 184.65 | 75.02 | 377 | 96.71 | 49.479 |
|  CBR Proxy | 4G | 200 | 10240 | 30 | 0 | 795 | 189.1 | 71.6 | 393 | 94.46 | 49.447 |
|  CBR Proxy | 4G | 200 | 10240 | 100 | 0 | 728.26 | 206.45 | 54 | 361 | 94.37 | 49.481 |
|  CBR Proxy | 4G | 200 | 10240 | 500 | 0 | 288.07 | 517.58 | 18.57 | 587 | 94.88 | 49.619 |
|  CBR Proxy | 4G | 200 | 10240 | 1000 | 0 | 146.07 | 1006.44 | 10.79 | 1055 | 94.96 | 49.487 |
|  CBR Proxy | 4G | 200 | 102400 | 0 | 0 | 77.61 | 1925.1 | 604.21 | 3455 | 90.05 | 49.438 |
|  CBR Proxy | 4G | 200 | 102400 | 30 | 0 | 77.69 | 1920.39 | 617.5 | 3583 | 89.6 | 49.427 |
|  CBR Proxy | 4G | 200 | 102400 | 100 | 0 | 77.61 | 1915.84 | 614.47 | 3775 | 89.84 | 49.451 |
|  CBR Proxy | 4G | 200 | 102400 | 500 | 0 | 79.79 | 1855.17 | 552.6 | 3391 | 90.8 | 49.408 |
|  CBR Proxy | 4G | 200 | 102400 | 1000 | 0 | 76.5 | 1915.5 | 428.33 | 3119 | 90.66 | 49.501 |
|  CBR SOAP Header Proxy | 4G | 100 | 500 | 0 | 0 | 3819.01 | 19.72 | 14.39 | 76 | 94.53 | 49.477 |
|  CBR SOAP Header Proxy | 4G | 100 | 500 | 30 | 0 | 2376.2 | 31.72 | 3.06 | 43 | 95.39 | 49.4 |
|  CBR SOAP Header Proxy | 4G | 100 | 500 | 100 | 0 | 744.19 | 101.29 | 2.07 | 107 | 95.35 | 49.474 |
|  CBR SOAP Header Proxy | 4G | 100 | 500 | 500 | 0 | 148.98 | 501.4 | 0.86 | 503 | 93.7 | 49.749 |
|  CBR SOAP Header Proxy | 4G | 100 | 500 | 1000 | 0 | 73.86 | 1002.03 | 0.44 | 1003 | 91.98 | 49.461 |
|  CBR SOAP Header Proxy | 4G | 100 | 1024 | 0 | 0 | 3508.3 | 21.46 | 14.93 | 77 | 95.17 | 49.469 |
|  CBR SOAP Header Proxy | 4G | 100 | 1024 | 30 | 0 | 2354.36 | 32.03 | 3.88 | 46 | 95.49 | 49.413 |
|  CBR SOAP Header Proxy | 4G | 100 | 1024 | 100 | 0 | 743.47 | 101.4 | 2.22 | 109 | 95.39 | 49.442 |
|  CBR SOAP Header Proxy | 4G | 100 | 1024 | 500 | 0 | 149.09 | 501.56 | 1.48 | 503 | 95.36 | 49.373 |
|  CBR SOAP Header Proxy | 4G | 100 | 1024 | 1000 | 0 | 73.87 | 1002.04 | 0.5 | 1003 | 92.03 | 49.454 |
|  CBR SOAP Header Proxy | 4G | 100 | 10240 | 0 | 0 | 1111.38 | 66.98 | 30.67 | 157 | 95.04 | 49.047 |
|  CBR SOAP Header Proxy | 4G | 100 | 10240 | 30 | 0 | 1113.99 | 67.69 | 18.81 | 127 | 94.94 | 49.832 |
|  CBR SOAP Header Proxy | 4G | 100 | 10240 | 100 | 0 | 676.5 | 111.29 | 10.23 | 151 | 95.3 | 49.458 |
|  CBR SOAP Header Proxy | 4G | 100 | 10240 | 500 | 0 | 148.59 | 503.66 | 2.89 | 515 | 95.04 | 49.221 |
|  CBR SOAP Header Proxy | 4G | 100 | 10240 | 1000 | 0 | 73.72 | 1002.57 | 2.43 | 1011 | 95.13 | 49.442 |
|  CBR SOAP Header Proxy | 4G | 100 | 102400 | 0 | 0 | 153.13 | 491.42 | 188.95 | 1003 | 94.06 | 49.457 |
|  CBR SOAP Header Proxy | 4G | 100 | 102400 | 30 | 0 | 152.92 | 492.5 | 176.3 | 999 | 94.16 | 49.358 |
|  CBR SOAP Header Proxy | 4G | 100 | 102400 | 100 | 0 | 150.19 | 501.38 | 152.12 | 903 | 94.09 | 49.39 |
|  CBR SOAP Header Proxy | 4G | 100 | 102400 | 500 | 0 | 118.1 | 631.41 | 81.25 | 875 | 94.02 | 49.758 |
|  CBR SOAP Header Proxy | 4G | 100 | 102400 | 1000 | 0 | 70.09 | 1057.27 | 47.13 | 1231 | 94.89 | 49.785 |
|  CBR SOAP Header Proxy | 4G | 200 | 500 | 0 | 0 | 3685.68 | 40.75 | 24.75 | 123 | 94.65 | 49.48 |
|  CBR SOAP Header Proxy | 4G | 200 | 500 | 30 | 0 | 3873.91 | 38.73 | 8.01 | 74 | 95.05 | 49.615 |
|  CBR SOAP Header Proxy | 4G | 200 | 500 | 100 | 0 | 1469.96 | 102.11 | 3.87 | 119 | 95.05 | 49.439 |
|  CBR SOAP Header Proxy | 4G | 200 | 500 | 500 | 0 | 297.25 | 501.37 | 1.27 | 505 | 95.03 | 49.453 |
|  CBR SOAP Header Proxy | 4G | 200 | 500 | 1000 | 0 | 147.29 | 1002.05 | 0.62 | 1003 | 93.87 | 49.524 |
|  CBR SOAP Header Proxy | 4G | 200 | 1024 | 0 | 0 | 3630.15 | 41.37 | 26.34 | 137 | 95.02 | 49.373 |
|  CBR SOAP Header Proxy | 4G | 200 | 1024 | 30 | 0 | 3728.57 | 40.23 | 8.79 | 79 | 94.97 | 49.479 |
|  CBR SOAP Header Proxy | 4G | 200 | 1024 | 100 | 0 | 1463.57 | 102.46 | 4.38 | 120 | 95.23 | 49.48 |
|  CBR SOAP Header Proxy | 4G | 200 | 1024 | 500 | 0 | 297.38 | 501.41 | 1.27 | 505 | 94.93 | 49.375 |
|  CBR SOAP Header Proxy | 4G | 200 | 1024 | 1000 | 0 | 147.11 | 1002.08 | 1.01 | 1007 | 95.12 | 52.499 |
|  CBR SOAP Header Proxy | 4G | 200 | 10240 | 0 | 0 | 1171.89 | 127.53 | 53.59 | 279 | 94.88 | 49.436 |
|  CBR SOAP Header Proxy | 4G | 200 | 10240 | 30 | 0 | 1147 | 130.99 | 46.41 | 271 | 94.77 | 49.487 |
|  CBR SOAP Header Proxy | 4G | 200 | 10240 | 100 | 0 | 1042.85 | 143.69 | 27.51 | 232 | 94.79 | 49.555 |
|  CBR SOAP Header Proxy | 4G | 200 | 10240 | 500 | 0 | 295.26 | 505.56 | 5.81 | 531 | 95.22 | 49.454 |
|  CBR SOAP Header Proxy | 4G | 200 | 10240 | 1000 | 0 | 146.93 | 1003.74 | 5.25 | 1031 | 95.45 | 49.511 |
|  CBR SOAP Header Proxy | 4G | 200 | 102400 | 0 | 0 | 145.15 | 1028 | 368 | 1943 | 92.45 | 49.484 |
|  CBR SOAP Header Proxy | 4G | 200 | 102400 | 30 | 0 | 145.49 | 1031.48 | 338.06 | 1919 | 92.07 | 49.576 |
|  CBR SOAP Header Proxy | 4G | 200 | 102400 | 100 | 0 | 146.71 | 1018.99 | 339.77 | 1791 | 92.14 | 49.43 |
|  CBR SOAP Header Proxy | 4G | 200 | 102400 | 500 | 0 | 140.85 | 1051.37 | 282.25 | 1815 | 92.65 | 49.47 |
|  CBR SOAP Header Proxy | 4G | 200 | 102400 | 1000 | 0 | 113.57 | 1307.05 | 234.12 | 2239 | 93.05 | 49.346 |
|  CBR Transport Header Proxy | 4G | 100 | 500 | 0 | 0 | 5626.4 | 13.36 | 11.36 | 60 | 94.77 | 49.449 |
|  CBR Transport Header Proxy | 4G | 100 | 500 | 30 | 0 | 2412 | 31.25 | 2.3 | 39 | 95.39 | 49.385 |
|  CBR Transport Header Proxy | 4G | 100 | 500 | 100 | 0 | 746.11 | 100.89 | 1.08 | 103 | 94.94 | 49.423 |
|  CBR Transport Header Proxy | 4G | 100 | 500 | 500 | 0 | 149.26 | 501.17 | 0.68 | 503 | 93.34 | 49.751 |
|  CBR Transport Header Proxy | 4G | 100 | 500 | 1000 | 0 | 74.08 | 1002.03 | 0.4 | 1003 | 85.61 | 48.594 |
|  CBR Transport Header Proxy | 4G | 100 | 1024 | 0 | 0 | 4688.48 | 16.06 | 12.44 | 65 | 95.24 | 49.552 |
|  CBR Transport Header Proxy | 4G | 100 | 1024 | 30 | 0 | 2409.23 | 31.28 | 2.07 | 38 | 95.33 | 49.77 |
|  CBR Transport Header Proxy | 4G | 100 | 1024 | 100 | 0 | 745.2 | 100.97 | 1.53 | 104 | 95.8 | 49.421 |
|  CBR Transport Header Proxy | 4G | 100 | 1024 | 500 | 0 | 149.28 | 501.21 | 0.68 | 503 | 91.61 | 49.39 |
|  CBR Transport Header Proxy | 4G | 100 | 1024 | 1000 | 0 | 74.12 | 1002.05 | 0.96 | 1003 | 94.67 | 49.589 |
|  CBR Transport Header Proxy | 4G | 100 | 10240 | 0 | 0 | 3804.93 | 19.75 | 8.86 | 51 | 95.43 | 49.806 |
|  CBR Transport Header Proxy | 4G | 100 | 10240 | 30 | 0 | 2280.4 | 33.02 | 3.26 | 46 | 95.35 | 49.355 |
|  CBR Transport Header Proxy | 4G | 100 | 10240 | 100 | 0 | 741.52 | 101.65 | 2.28 | 110 | 95.54 | 49.395 |
|  CBR Transport Header Proxy | 4G | 100 | 10240 | 500 | 0 | 149.13 | 501.53 | 1 | 505 | 92.26 | 49.443 |
|  CBR Transport Header Proxy | 4G | 100 | 10240 | 1000 | 0 | 74.06 | 1002.05 | 0.64 | 1003 | 91.22 | 49.784 |
|  CBR Transport Header Proxy | 4G | 100 | 102400 | 0 | 0 | 1033.49 | 72.75 | 34.28 | 173 | 95.12 | 49.431 |
|  CBR Transport Header Proxy | 4G | 100 | 102400 | 30 | 0 | 1056.19 | 71.1 | 22.76 | 145 | 95.16 | 49.519 |
|  CBR Transport Header Proxy | 4G | 100 | 102400 | 100 | 0 | 666.06 | 112.83 | 12.31 | 163 | 95.51 | 49.447 |
|  CBR Transport Header Proxy | 4G | 100 | 102400 | 500 | 0 | 148.35 | 504.15 | 1.95 | 511 | 91.72 | 49.403 |
|  CBR Transport Header Proxy | 4G | 100 | 102400 | 1000 | 0 | 73.67 | 1002.8 | 1.9 | 1011 | 91.66 | 49.415 |
|  CBR Transport Header Proxy | 4G | 200 | 500 | 0 | 0 | 6054.85 | 24.78 | 16.26 | 84 | 95.01 | 49.468 |
|  CBR Transport Header Proxy | 4G | 200 | 500 | 30 | 0 | 4542.04 | 33.05 | 4.7 | 55 | 95.1 | 49.522 |
|  CBR Transport Header Proxy | 4G | 200 | 500 | 100 | 0 | 1480.05 | 101.38 | 2.86 | 114 | 95.24 | 49.485 |
|  CBR Transport Header Proxy | 4G | 200 | 500 | 500 | 0 | 297.26 | 501.18 | 0.72 | 503 | 93.94 | 49.505 |
|  CBR Transport Header Proxy | 4G | 200 | 500 | 1000 | 0 | 147.1 | 1002.05 | 0.74 | 1003 | 92.15 | 49.621 |
|  CBR Transport Header Proxy | 4G | 200 | 1024 | 0 | 0 | 5109.5 | 29.38 | 19.4 | 103 | 94.62 | 45.426 |
|  CBR Transport Header Proxy | 4G | 200 | 1024 | 30 | 0 | 4501.25 | 33.35 | 5.13 | 58 | 95.2 | 49.498 |
|  CBR Transport Header Proxy | 4G | 200 | 1024 | 100 | 0 | 1483.12 | 101.2 | 1.7 | 106 | 95.35 | 49.445 |
|  CBR Transport Header Proxy | 4G | 200 | 1024 | 500 | 0 | 297.09 | 501.22 | 1.24 | 503 | 95.57 | 49.555 |
|  CBR Transport Header Proxy | 4G | 200 | 1024 | 1000 | 0 | 147.19 | 1002.04 | 0.56 | 1003 | 92.48 | 49.283 |
|  CBR Transport Header Proxy | 4G | 200 | 10240 | 0 | 0 | 3598.44 | 41.69 | 16.77 | 92 | 95.07 | 49.589 |
|  CBR Transport Header Proxy | 4G | 200 | 10240 | 30 | 0 | 3552.37 | 42.24 | 9.76 | 79 | 95.35 | 49.474 |
|  CBR Transport Header Proxy | 4G | 200 | 10240 | 100 | 0 | 1462.67 | 102.61 | 4.28 | 121 | 95.54 | 49.464 |
|  CBR Transport Header Proxy | 4G | 200 | 10240 | 500 | 0 | 296.9 | 501.43 | 1.32 | 505 | 94.97 | 49.359 |
|  CBR Transport Header Proxy | 4G | 200 | 10240 | 1000 | 0 | 147.2 | 1002.05 | 0.6 | 1003 | 91.34 | 49.438 |
|  CBR Transport Header Proxy | 4G | 200 | 102400 | 0 | 0 | 918.09 | 163.52 | 72.59 | 365 | 95.14 | 49.515 |
|  CBR Transport Header Proxy | 4G | 200 | 102400 | 30 | 0 | 1074.21 | 139.54 | 56.26 | 311 | 94.87 | 49.413 |
|  CBR Transport Header Proxy | 4G | 200 | 102400 | 100 | 0 | 887.32 | 168.67 | 39.85 | 299 | 95.8 | 49.397 |
|  CBR Transport Header Proxy | 4G | 200 | 102400 | 500 | 0 | 293.97 | 506.49 | 5.01 | 527 | 94.13 | 49.513 |
|  CBR Transport Header Proxy | 4G | 200 | 102400 | 1000 | 0 | 146.75 | 1003.64 | 2.82 | 1015 | 92.41 | 49.519 |
|  Direct Proxy | 4G | 100 | 500 | 0 | 0 | 5818.18 | 12.93 | 10.64 | 57 | 94.85 | 47.012 |
|  Direct Proxy | 4G | 100 | 500 | 30 | 0 | 2410.71 | 31.29 | 2.51 | 38 | 95.47 | 49.372 |
|  Direct Proxy | 4G | 100 | 500 | 100 | 0 | 746.91 | 100.9 | 1.14 | 104 | 94.87 | 49.523 |
|  Direct Proxy | 4G | 100 | 500 | 500 | 0 | 149.28 | 501.21 | 0.84 | 503 | 91.83 | 49.281 |
|  Direct Proxy | 4G | 100 | 500 | 1000 | 0 | 74.1 | 1002.03 | 0.43 | 1003 | 92.24 | 49.382 |
|  Direct Proxy | 4G | 100 | 1024 | 0 | 0 | 5864.91 | 12.82 | 10.84 | 61 | 94.94 | 49.528 |
|  Direct Proxy | 4G | 100 | 1024 | 30 | 0 | 2410.93 | 31.27 | 2.49 | 38 | 95.09 | 49.343 |
|  Direct Proxy | 4G | 100 | 1024 | 100 | 0 | 746.13 | 100.95 | 1.51 | 104 | 95.46 | 49.326 |
|  Direct Proxy | 4G | 100 | 1024 | 500 | 0 | 149.34 | 501.22 | 0.71 | 503 | 91.23 | 49.344 |
|  Direct Proxy | 4G | 100 | 1024 | 1000 | 0 | 74.13 | 1002.04 | 0.86 | 1003 | 94.29 | 49.22 |
|  Direct Proxy | 4G | 100 | 10240 | 0 | 0 | 3627.56 | 20.71 | 10.29 | 54 | 95.22 | 49.533 |
|  Direct Proxy | 4G | 100 | 10240 | 30 | 0 | 2249.4 | 33.49 | 3.91 | 47 | 95.41 | 49.359 |
|  Direct Proxy | 4G | 100 | 10240 | 100 | 0 | 741.79 | 101.55 | 2.03 | 109 | 95.49 | 49.549 |
|  Direct Proxy | 4G | 100 | 10240 | 500 | 0 | 149.05 | 501.62 | 1.07 | 505 | 92.05 | 49.217 |
|  Direct Proxy | 4G | 100 | 10240 | 1000 | 0 | 74.1 | 1002.06 | 0.62 | 1007 | 92.29 | 49.386 |
|  Direct Proxy | 4G | 100 | 102400 | 0 | 0 | 935.73 | 80.38 | 24.42 | 149 | 95.87 | 53.395 |
|  Direct Proxy | 4G | 100 | 102400 | 30 | 0 | 987.57 | 76.08 | 16.77 | 122 | 94.81 | 49.449 |
|  Direct Proxy | 4G | 100 | 102400 | 100 | 0 | 608.46 | 123.47 | 18.06 | 177 | 95.63 | 49.335 |
|  Direct Proxy | 4G | 100 | 102400 | 500 | 0 | 147.72 | 506.2 | 4.42 | 523 | 93.16 | 49.524 |
|  Direct Proxy | 4G | 100 | 102400 | 1000 | 0 | 73.76 | 1002.76 | 1.96 | 1011 | 92.15 | 49.459 |
|  Direct Proxy | 4G | 200 | 500 | 0 | 0 | 5562.63 | 26.96 | 17.77 | 93 | 94.81 | 49.409 |
|  Direct Proxy | 4G | 200 | 500 | 30 | 0 | 4557.16 | 32.91 | 4.4 | 52 | 95.26 | 49.449 |
|  Direct Proxy | 4G | 200 | 500 | 100 | 0 | 1483.85 | 101.2 | 1.79 | 108 | 95.04 | 49.478 |
|  Direct Proxy | 4G | 200 | 500 | 500 | 0 | 297.58 | 501.18 | 0.72 | 503 | 95.99 | 49.407 |
|  Direct Proxy | 4G | 200 | 500 | 1000 | 0 | 147.22 | 1002.04 | 0.52 | 1003 | 91.43 | 50.003 |
|  Direct Proxy | 4G | 200 | 1024 | 0 | 0 | 5591.4 | 26.85 | 17.92 | 94 | 95.12 | 49.179 |
|  Direct Proxy | 4G | 200 | 1024 | 30 | 0 | 4526.96 | 33.18 | 4.55 | 53 | 95.07 | 49.412 |
|  Direct Proxy | 4G | 200 | 1024 | 100 | 0 | 1478.46 | 101.37 | 2.26 | 109 | 95.12 | 49.454 |
|  Direct Proxy | 4G | 200 | 1024 | 500 | 0 | 296.99 | 501.3 | 1.79 | 503 | 95.64 | 49.438 |
|  Direct Proxy | 4G | 200 | 1024 | 1000 | 0 | 147.26 | 1002.03 | 0.54 | 1003 | 91.49 | 49.438 |
|  Direct Proxy | 4G | 200 | 10240 | 0 | 0 | 3505.19 | 42.82 | 19.75 | 101 | 95.26 | 49.476 |
|  Direct Proxy | 4G | 200 | 10240 | 30 | 0 | 3460.06 | 43.37 | 9.72 | 76 | 94.95 | 49.796 |
|  Direct Proxy | 4G | 200 | 10240 | 100 | 0 | 1458.1 | 102.85 | 3.47 | 117 | 95.05 | 49.479 |
|  Direct Proxy | 4G | 200 | 10240 | 500 | 0 | 297.1 | 501.44 | 1.47 | 505 | 95.32 | 49.456 |
|  Direct Proxy | 4G | 200 | 10240 | 1000 | 0 | 147.22 | 1002.07 | 0.66 | 1007 | 91.61 | 49.567 |
|  Direct Proxy | 4G | 200 | 102400 | 0 | 0 | 822.38 | 182.71 | 51.04 | 325 | 94.21 | 49.486 |
|  Direct Proxy | 4G | 200 | 102400 | 30 | 0 | 985.27 | 152.31 | 32.56 | 244 | 94.98 | 49.437 |
|  Direct Proxy | 4G | 200 | 102400 | 100 | 0 | 848.68 | 176.88 | 35.09 | 273 | 95.22 | 49.397 |
|  Direct Proxy | 4G | 200 | 102400 | 500 | 0 | 293.82 | 507.46 | 8.39 | 547 | 95.67 | 49.372 |
|  Direct Proxy | 4G | 200 | 102400 | 1000 | 0 | 146.81 | 1004.47 | 4 | 1019 | 93.28 | 49.479 |
|  XSLT Enhanced Proxy | 4G | 100 | 500 | 0 | 0 | 120.07 | 625.95 | 156.94 | 1151 | 94.51 | 49.446 |
|  XSLT Enhanced Proxy | 4G | 100 | 500 | 30 | 0 | 112.02 | 668.2 | 191.35 | 1215 | 94.7 | 49.413 |
|  XSLT Enhanced Proxy | 4G | 100 | 500 | 100 | 0 | 121.15 | 619.97 | 170.13 | 1223 | 94.65 | 49.361 |
|  XSLT Enhanced Proxy | 4G | 100 | 500 | 500 | 0 | 110.14 | 678.45 | 158.9 | 1119 | 94.92 | 49.461 |
|  XSLT Enhanced Proxy | 4G | 100 | 500 | 1000 | 0 | 71.89 | 1030.12 | 25.13 | 1119 | 94.4 | 49.531 |
|  XSLT Enhanced Proxy | 4G | 100 | 1024 | 0 | 0 | 115.48 | 651.01 | 210.29 | 1263 | 94.58 | 49.505 |
|  XSLT Enhanced Proxy | 4G | 100 | 1024 | 30 | 0 | 125.05 | 600.99 | 160.2 | 1023 | 95.38 | 52.438 |
|  XSLT Enhanced Proxy | 4G | 100 | 1024 | 100 | 0 | 112.66 | 665.22 | 208.66 | 1143 | 94.6 | 49.449 |
|  XSLT Enhanced Proxy | 4G | 100 | 1024 | 500 | 0 | 111.49 | 669.37 | 133.45 | 1007 | 94.57 | 49.407 |
|  XSLT Enhanced Proxy | 4G | 100 | 1024 | 1000 | 0 | 72.17 | 1024.59 | 18.82 | 1095 | 94.78 | 49.403 |
|  XSLT Enhanced Proxy | 4G | 100 | 10240 | 0 | 0 | 109.98 | 683.03 | 233.64 | 1319 | 94.74 | 49.471 |
|  XSLT Enhanced Proxy | 4G | 100 | 10240 | 30 | 0 | 112.2 | 669.23 | 178.86 | 1239 | 94.19 | 49.925 |
|  XSLT Enhanced Proxy | 4G | 100 | 10240 | 100 | 0 | 103.79 | 723.48 | 183.6 | 1215 | 94.69 | 49.36 |
|  XSLT Enhanced Proxy | 4G | 100 | 10240 | 500 | 0 | 98.73 | 755.73 | 190.2 | 1167 | 94.72 | 49.389 |
|  XSLT Enhanced Proxy | 4G | 100 | 10240 | 1000 | 0 | 71.21 | 1036.45 | 28.05 | 1135 | 94.93 | 49.451 |
|  XSLT Enhanced Proxy | 4G | 100 | 102400 | 0 | 0 | 41.92 | 1766.75 | 360.5 | 2623 | 95.08 | 49.434 |
|  XSLT Enhanced Proxy | 4G | 100 | 102400 | 30 | 0 | 42.36 | 1755.04 | 394.42 | 2703 | 94.39 | 49.802 |
|  XSLT Enhanced Proxy | 4G | 100 | 102400 | 100 | 0 | 42.57 | 1746.6 | 406.54 | 2655 | 95.04 | 49.439 |
|  XSLT Enhanced Proxy | 4G | 100 | 102400 | 500 | 0 | 42.6 | 1728.19 | 345.66 | 2543 | 94.61 | 49.332 |
|  XSLT Enhanced Proxy | 4G | 100 | 102400 | 1000 | 0 | 40.09 | 1827.59 | 289.69 | 2511 | 94.96 | 49.46 |
|  XSLT Enhanced Proxy | 4G | 200 | 500 | 0 | 0 | 108.45 | 1371.03 | 376.1 | 2175 | 94.44 | 49.352 |
|  XSLT Enhanced Proxy | 4G | 200 | 500 | 30 | 0 | 118.99 | 1249.25 | 300.14 | 2095 | 94.71 | 49.479 |
|  XSLT Enhanced Proxy | 4G | 200 | 500 | 100 | 0 | 121.28 | 1228.79 | 358.08 | 1991 | 94.54 | 49.59 |
|  XSLT Enhanced Proxy | 4G | 200 | 500 | 500 | 0 | 117.16 | 1265.09 | 297.83 | 2047 | 93.82 | 49.329 |
|  XSLT Enhanced Proxy | 4G | 200 | 500 | 1000 | 0 | 116.91 | 1257.57 | 192.1 | 1815 | 94.82 | 49.366 |
|  XSLT Enhanced Proxy | 4G | 200 | 1024 | 0 | 0 | 122.48 | 1213.88 | 325.22 | 1999 | 94.47 | 49.473 |
|  XSLT Enhanced Proxy | 4G | 200 | 1024 | 30 | 0 | 117.25 | 1269.74 | 326.59 | 2143 | 95.23 | 52.956 |
|  XSLT Enhanced Proxy | 4G | 200 | 1024 | 100 | 0 | 109.08 | 1361.19 | 337.69 | 2127 | 94.72 | 49.723 |
|  XSLT Enhanced Proxy | 4G | 200 | 1024 | 500 | 0 | 124.3 | 1191.68 | 290.62 | 2095 | 94.59 | 49.343 |
|  XSLT Enhanced Proxy | 4G | 200 | 1024 | 1000 | 0 | 109.16 | 1345.22 | 234.09 | 1871 | 94.79 | 49.483 |
|  XSLT Enhanced Proxy | 4G | 200 | 10240 | 0 | 0 | 106.11 | 1401.11 | 367.1 | 2191 | 94.44 | 49.458 |
|  XSLT Enhanced Proxy | 4G | 200 | 10240 | 30 | 0 | 110.19 | 1347.95 | 365.63 | 2335 | 94.63 | 49.382 |
|  XSLT Enhanced Proxy | 4G | 200 | 10240 | 100 | 0 | 105.85 | 1402.43 | 333.97 | 2271 | 94.56 | 49.484 |
|  XSLT Enhanced Proxy | 4G | 200 | 10240 | 500 | 0 | 103.51 | 1429.4 | 279.13 | 2007 | 94.65 | 49.406 |
|  XSLT Enhanced Proxy | 4G | 200 | 10240 | 1000 | 0 | 96.92 | 1509.74 | 378.6 | 2415 | 94.78 | 49.444 |
|  XSLT Enhanced Proxy | 4G | 200 | 102400 | 0 | 0 | 30.81 | 4729.59 | 869.78 | 6943 | 94.76 | 49.779 |
|  XSLT Enhanced Proxy | 4G | 200 | 102400 | 30 | 0 | 37.17 | 3868.69 | 682.3 | 5279 | 94.66 | 49.484 |
|  XSLT Enhanced Proxy | 4G | 200 | 102400 | 100 | 0 | 41.25 | 3520.62 | 792.53 | 5183 | 94.54 | 49.468 |
|  XSLT Enhanced Proxy | 4G | 200 | 102400 | 500 | 0 | 33.83 | 4257.97 | 894.26 | 6079 | 94.73 | 49.385 |
|  XSLT Enhanced Proxy | 4G | 200 | 102400 | 1000 | 0 | 43.07 | 3343.67 | 686.2 | 4895 | 94.5 | 49.403 |
|  XSLT Proxy | 4G | 100 | 500 | 0 | 0 | 106.89 | 700.95 | 233.8 | 1303 | 94.76 | 49.452 |
|  XSLT Proxy | 4G | 100 | 500 | 30 | 0 | 115.04 | 652.74 | 153.78 | 995 | 94.36 | 49.422 |
|  XSLT Proxy | 4G | 100 | 500 | 100 | 0 | 106.21 | 707.13 | 209.73 | 1287 | 94.59 | 49.48 |
|  XSLT Proxy | 4G | 100 | 500 | 500 | 0 | 103.48 | 723.24 | 153.65 | 1119 | 94.54 | 49.471 |
|  XSLT Proxy | 4G | 100 | 500 | 1000 | 0 | 71.2 | 1038.1 | 34.79 | 1175 | 95.02 | 49.317 |
|  XSLT Proxy | 4G | 100 | 1024 | 0 | 0 | 112.82 | 665.45 | 158.75 | 1119 | 94.46 | 49.491 |
|  XSLT Proxy | 4G | 100 | 1024 | 30 | 0 | 105.04 | 715.61 | 222.67 | 1383 | 94.46 | 49.476 |
|  XSLT Proxy | 4G | 100 | 1024 | 100 | 0 | 114.34 | 656.83 | 175.27 | 1095 | 94.63 | 49.322 |
|  XSLT Proxy | 4G | 100 | 1024 | 500 | 0 | 111.68 | 668.22 | 124.43 | 1003 | 94.4 | 49.357 |
|  XSLT Proxy | 4G | 100 | 1024 | 1000 | 0 | 71.66 | 1032.7 | 25.26 | 1135 | 94.93 | 49.523 |
|  XSLT Proxy | 4G | 100 | 10240 | 0 | 0 | 91.75 | 815.97 | 223.45 | 1391 | 94.29 | 49.434 |
|  XSLT Proxy | 4G | 100 | 10240 | 30 | 0 | 86.48 | 865.35 | 246.85 | 1431 | 94.54 | 49.445 |
|  XSLT Proxy | 4G | 100 | 10240 | 100 | 0 | 89.34 | 838.96 | 227.37 | 1359 | 94.4 | 49.407 |
|  XSLT Proxy | 4G | 100 | 10240 | 500 | 0 | 84.36 | 882.15 | 239.3 | 1439 | 94.42 | 49.481 |
|  XSLT Proxy | 4G | 100 | 10240 | 1000 | 0 | 68.95 | 1073.15 | 52.36 | 1231 | 94.84 | 49.519 |
|  XSLT Proxy | 4G | 100 | 102400 | 0 | 0 | 16.39 | 4418.43 | 658.97 | 5855 | 93.57 | 49.449 |
|  XSLT Proxy | 4G | 100 | 102400 | 30 | 0 | 14.8 | 4846.43 | 862.67 | 6623 | 93.67 | 49.546 |
|  XSLT Proxy | 4G | 100 | 102400 | 100 | 0 | 18 | 4009.29 | 846.13 | 5983 | 93.65 | 49.359 |
|  XSLT Proxy | 4G | 100 | 102400 | 500 | 0 | 19.48 | 3686.99 | 700.57 | 5535 | 93.49 | 49.378 |
|  XSLT Proxy | 4G | 100 | 102400 | 1000 | 0 | 19.81 | 3658.3 | 715.55 | 5247 | 93.45 | 49.44 |
|  XSLT Proxy | 4G | 200 | 500 | 0 | 0 | 101.59 | 1462.37 | 407.87 | 2735 | 93.97 | 49.525 |
|  XSLT Proxy | 4G | 200 | 500 | 30 | 0 | 112.41 | 1319.04 | 299.31 | 2159 | 94.37 | 49.564 |
|  XSLT Proxy | 4G | 200 | 500 | 100 | 0 | 113.59 | 1310.37 | 340.61 | 2239 | 94.58 | 49.511 |
|  XSLT Proxy | 4G | 200 | 500 | 500 | 0 | 108.6 | 1369.37 | 301.87 | 2095 | 94.63 | 49.422 |
|  XSLT Proxy | 4G | 200 | 500 | 1000 | 0 | 104.49 | 1405.3 | 290.66 | 2127 | 94.72 | 49.542 |
|  XSLT Proxy | 4G | 200 | 1024 | 0 | 0 | 115.77 | 1286.25 | 369.25 | 2063 | 94.26 | 49.736 |
|  XSLT Proxy | 4G | 200 | 1024 | 30 | 0 | 109 | 1358.58 | 404.92 | 2191 | 94.39 | 49.49 |
|  XSLT Proxy | 4G | 200 | 1024 | 100 | 0 | 103.69 | 1429.39 | 399.01 | 2575 | 94.37 | 49.319 |
|  XSLT Proxy | 4G | 200 | 1024 | 500 | 0 | 109.14 | 1354.12 | 393.26 | 2351 | 94.37 | 49.505 |
|  XSLT Proxy | 4G | 200 | 1024 | 1000 | 0 | 100.15 | 1460.95 | 304.44 | 2207 | 94.44 | 49.839 |
|  XSLT Proxy | 4G | 200 | 10240 | 0 | 0 | 89.46 | 1652.99 | 476.67 | 2799 | 94.07 | 49.771 |
|  XSLT Proxy | 4G | 200 | 10240 | 30 | 0 | 85.73 | 1724.03 | 505.39 | 2799 | 95.09 | 52.867 |
|  XSLT Proxy | 4G | 200 | 10240 | 100 | 0 | 87.2 | 1689.78 | 439.87 | 2527 | 94.03 | 49.462 |
|  XSLT Proxy | 4G | 200 | 10240 | 500 | 0 | 86.71 | 1706.61 | 386.01 | 2543 | 94.1 | 50.494 |
|  XSLT Proxy | 4G | 200 | 10240 | 1000 | 0 | 81.71 | 1788.85 | 464.06 | 2975 | 93.76 | 49.335 |
|  XSLT Proxy | 4G | 200 | 102400 | 0 | 0 | 14.4 | 9948.67 | 2092.73 | 14527 | 91.7 | 49.452 |
|  XSLT Proxy | 4G | 200 | 102400 | 30 | 0 | 15.12 | 9257.59 | 1781.19 | 13695 | 92.06 | 49.575 |
|  XSLT Proxy | 4G | 200 | 102400 | 100 | 0 | 13.8 | 10478.89 | 1861.45 | 14271 | 91.89 | 49.544 |
|  XSLT Proxy | 4G | 200 | 102400 | 500 | 0 | 16.98 | 8233.02 | 1714.38 | 11967 | 91.93 | 49.46 |
|  XSLT Proxy | 4G | 200 | 102400 | 1000 | 0 | 18.92 | 7427.27 | 1487.89 | 10559 | 91.78 | 49.609 |
