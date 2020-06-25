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
| Concurrent Users | The number of users accessing the application at the same time. | 500, 1000 |
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
| Operating System | Distribution | Kernel | Linux ip-10-0-1-12 5.3.0-1023-aws #25~18.04.1-Ubuntu SMP Fri Jun 5 15:18:30 UTC 2020 x86_64 x86_64 x86_64 GNU/Linux |


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
|  CBR Proxy | 4G | 500 | 500 | 0 | 0 | 3816.93 | 98.22 | 49.13 | 248 | 95.59 | 49.811 |
|  CBR Proxy | 4G | 500 | 500 | 30 | 0 | 4229.69 | 88.72 | 33.57 | 200 | 95.86 | 49.381 |
|  CBR Proxy | 4G | 500 | 500 | 100 | 0 | 3435.66 | 108.94 | 10.99 | 157 | 96.02 | 49.404 |
|  CBR Proxy | 4G | 500 | 500 | 500 | 0 | 740.38 | 501.56 | 2.94 | 509 | 96.33 | 49.432 |
|  CBR Proxy | 4G | 500 | 500 | 1000 | 0 | 366.91 | 1002.2 | 2.41 | 1007 | 96.28 | 49.552 |
|  CBR Proxy | 4G | 500 | 1024 | 0 | 0 | 3347.74 | 112.31 | 56.14 | 291 | 95.76 | 49.281 |
|  CBR Proxy | 4G | 500 | 1024 | 30 | 0 | 3581.7 | 104.74 | 41.69 | 233 | 95.68 | 49.354 |
|  CBR Proxy | 4G | 500 | 1024 | 100 | 0 | 3156.3 | 118.57 | 16.57 | 179 | 95.99 | 49.292 |
|  CBR Proxy | 4G | 500 | 1024 | 500 | 0 | 739.68 | 501.85 | 3.44 | 515 | 96.31 | 49.449 |
|  CBR Proxy | 4G | 500 | 1024 | 1000 | 0 | 366.87 | 1002.21 | 2.44 | 1007 | 96.04 | 49.478 |
|  CBR Proxy | 4G | 500 | 10240 | 0 | 0 | 816.6 | 459.59 | 169.53 | 899 | 94.91 | 51.119 |
|  CBR Proxy | 4G | 500 | 10240 | 30 | 0 | 821.59 | 457.15 | 163.71 | 931 | 94.9 | 49.355 |
|  CBR Proxy | 4G | 500 | 10240 | 100 | 0 | 802.92 | 466.34 | 156.89 | 883 | 94.95 | 49.465 |
|  CBR Proxy | 4G | 500 | 10240 | 500 | 0 | 621.48 | 597.52 | 64.5 | 787 | 95.24 | 49.422 |
|  CBR Proxy | 4G | 500 | 10240 | 1000 | 0 | 357.29 | 1037.02 | 41.54 | 1191 | 95.69 | 49.438 |
|  CBR Proxy | 4G | 500 | 102400 | 0 | 99.77 | 7296.14 | 46.02 | 359.31 | 277 | 87.08 | 49.4 |
|  CBR Proxy | 4G | 500 | 102400 | 30 | 99.9 | 11356.06 | 28.48 | 152.86 | 205 | 89.94 | 49.469 |
|  CBR Proxy | 4G | 500 | 102400 | 100 | 99.94 | 11686.94 | 26.29 | 136.31 | 203 | 90.19 | 49.458 |
|  CBR Proxy | 4G | 500 | 102400 | 500 | 99.86 | 8857.34 | 36.9 | 276.25 | 223 | 88.53 | 49.423 |
|  CBR Proxy | 4G | 500 | 102400 | 1000 | 99.91 | 10844.63 | 29.73 | 161.87 | 235 | 90.67 | 49.782 |
|  CBR Proxy | 4G | 1000 | 500 | 0 | 0 | 3859.97 | 194.41 | 82.25 | 433 | 95.48 | 49.379 |
|  CBR Proxy | 4G | 1000 | 500 | 30 | 0 | 3799.99 | 197.37 | 77.59 | 423 | 95.43 | 49.431 |
|  CBR Proxy | 4G | 1000 | 500 | 100 | 0 | 3876.16 | 192.98 | 53.97 | 363 | 95.73 | 49.243 |
|  CBR Proxy | 4G | 1000 | 500 | 500 | 0 | 1475.06 | 502.8 | 5.61 | 523 | 96.16 | 49.424 |
|  CBR Proxy | 4G | 1000 | 500 | 1000 | 0 | 732.36 | 1002.54 | 4.18 | 1015 | 96.26 | 49.19 |
|  CBR Proxy | 4G | 1000 | 1024 | 0 | 0 | 3302.46 | 227.44 | 91.99 | 491 | 95.44 | 49.353 |
|  CBR Proxy | 4G | 1000 | 1024 | 30 | 0 | 3420.15 | 219.13 | 87.05 | 449 | 95.62 | 49.372 |
|  CBR Proxy | 4G | 1000 | 1024 | 100 | 0 | 3257.56 | 229.69 | 73.91 | 489 | 95.56 | 49.284 |
|  CBR Proxy | 4G | 1000 | 1024 | 500 | 0 | 1464.88 | 507.11 | 12.68 | 575 | 95.85 | 49.361 |
|  CBR Proxy | 4G | 1000 | 1024 | 1000 | 0 | 732.83 | 1002.63 | 4.09 | 1015 | 95.9 | 49.494 |
|  CBR Proxy | 4G | 1000 | 10240 | 0 | 0 | 775.85 | 958.91 | 342.78 | 1943 | 93.29 | 49.512 |
|  CBR Proxy | 4G | 1000 | 10240 | 30 | 0 | 772.03 | 968.59 | 344.43 | 1887 | 93.93 | 49.332 |
|  CBR Proxy | 4G | 1000 | 10240 | 100 | 0 | 722.86 | 1033.09 | 356.02 | 2063 | 93.19 | 49.531 |
|  CBR Proxy | 4G | 1000 | 10240 | 500 | 0 | 738.12 | 1008.46 | 261.1 | 1783 | 93.87 | 49.559 |
|  CBR Proxy | 4G | 1000 | 10240 | 1000 | 0 | 613.3 | 1194.06 | 133.57 | 1607 | 94.22 | 49.217 |
|  CBR Proxy | 4G | 1000 | 102400 | 0 | 100 | 14938.45 | 41.73 | 85.27 | 393 | 88.94 | 49.571 |
|  CBR Proxy | 4G | 1000 | 102400 | 30 | 100 | 16029.45 | 41.02 | 87.03 | 405 | 88.96 | 49.422 |
|  CBR Proxy | 4G | 1000 | 102400 | 100 | 100 | 15421.74 | 41.28 | 83.94 | 381 | 90.19 | 49.805 |
|  CBR Proxy | 4G | 1000 | 102400 | 500 | 100 | 14750.63 | 42.51 | 89.56 | 413 | 88.86 | 49.427 |
|  CBR Proxy | 4G | 1000 | 102400 | 1000 | 100 | 14657.56 | 44.47 | 92.8 | 423 | 89.28 | 49.812 |
|  CBR SOAP Header Proxy | 4G | 500 | 500 | 0 | 0 | 4208.96 | 89.18 | 45.36 | 236 | 95.72 | 49.825 |
|  CBR SOAP Header Proxy | 4G | 500 | 500 | 30 | 0 | 4219.86 | 88.87 | 33.81 | 203 | 95.74 | 49.491 |
|  CBR SOAP Header Proxy | 4G | 500 | 500 | 100 | 0 | 3459.56 | 108.2 | 10.96 | 157 | 95.95 | 49.378 |
|  CBR SOAP Header Proxy | 4G | 500 | 500 | 500 | 0 | 740.29 | 501.36 | 1.95 | 505 | 95.93 | 49.471 |
|  CBR SOAP Header Proxy | 4G | 500 | 500 | 1000 | 0 | 366.95 | 1002.14 | 1.91 | 1007 | 95.84 | 49.436 |
|  CBR SOAP Header Proxy | 4G | 500 | 1024 | 0 | 0 | 3863.25 | 97.18 | 46.39 | 229 | 96.38 | 52.906 |
|  CBR SOAP Header Proxy | 4G | 500 | 1024 | 30 | 0 | 4055.29 | 92.44 | 36.04 | 212 | 95.62 | 49.255 |
|  CBR SOAP Header Proxy | 4G | 500 | 1024 | 100 | 0 | 3377.32 | 110.81 | 12.1 | 166 | 95.8 | 49.468 |
|  CBR SOAP Header Proxy | 4G | 500 | 1024 | 500 | 0 | 741.42 | 501.48 | 3.12 | 509 | 96.31 | 49.415 |
|  CBR SOAP Header Proxy | 4G | 500 | 1024 | 1000 | 0 | 366.92 | 1002.2 | 2.28 | 1007 | 96.07 | 49.349 |
|  CBR SOAP Header Proxy | 4G | 500 | 10240 | 0 | 0 | 282.52 | 324.14 | 649 | 643 | 95.26 | 49.55 |
|  CBR SOAP Header Proxy | 4G | 500 | 10240 | 30 | 0 | 1182.56 | 317.79 | 114.2 | 647 | 95.39 | 49.716 |
|  CBR SOAP Header Proxy | 4G | 500 | 10240 | 100 | 0 | 1198.78 | 312.86 | 95.96 | 571 | 95.33 | 49.382 |
|  CBR SOAP Header Proxy | 4G | 500 | 10240 | 500 | 0 | 702.31 | 528.64 | 24.76 | 623 | 95.91 | 49.505 |
|  CBR SOAP Header Proxy | 4G | 500 | 10240 | 1000 | 0 | 362.86 | 1009.97 | 14.37 | 1079 | 96 | 49.441 |
|  CBR SOAP Header Proxy | 4G | 500 | 102400 | 0 | 97.63 | 3412.81 | 100.08 | 463.13 | 2911 | 90.6 | 49.452 |
|  CBR SOAP Header Proxy | 4G | 500 | 102400 | 30 | 98.47 | 4868.54 | 71.35 | 350.82 | 2415 | 90.69 | 49.442 |
|  CBR SOAP Header Proxy | 4G | 500 | 102400 | 100 | 98.65 | 5094.24 | 67.12 | 352.56 | 2351 | 90.69 | 49.424 |
|  CBR SOAP Header Proxy | 4G | 500 | 102400 | 500 | 96.63 | 2666.18 | 129.75 | 529.57 | 3087 | 90.36 | 49.921 |
|  CBR SOAP Header Proxy | 4G | 500 | 102400 | 1000 | 95.4 | 2133.07 | 166.77 | 617.1 | 3359 | 90.58 | 49.849 |
|  CBR SOAP Header Proxy | 4G | 1000 | 500 | 0 | 0 | 4167.71 | 179.95 | 82.19 | 443 | 95.7 | 49.421 |
|  CBR SOAP Header Proxy | 4G | 1000 | 500 | 30 | 0 | 4197.22 | 178.5 | 71.63 | 395 | 95.82 | 49.213 |
|  CBR SOAP Header Proxy | 4G | 1000 | 500 | 100 | 0 | 4790.96 | 156.2 | 34.71 | 267 | 95.58 | 49.544 |
|  CBR SOAP Header Proxy | 4G | 1000 | 500 | 500 | 0 | 1469.55 | 504.59 | 8.86 | 547 | 96.18 | 49.437 |
|  CBR SOAP Header Proxy | 4G | 1000 | 500 | 1000 | 0 | 733.33 | 1002.29 | 2.88 | 1011 | 95.82 | 49.351 |
|  CBR SOAP Header Proxy | 4G | 1000 | 1024 | 0 | 0 | 3795.55 | 197.56 | 85.93 | 455 | 95.62 | 49.338 |
|  CBR SOAP Header Proxy | 4G | 1000 | 1024 | 30 | 0 | 3783.4 | 197.95 | 76.97 | 421 | 95.5 | 49.374 |
|  CBR SOAP Header Proxy | 4G | 1000 | 1024 | 100 | 0 | 4134.42 | 181.12 | 47.78 | 335 | 95.58 | 49.501 |
|  CBR SOAP Header Proxy | 4G | 1000 | 1024 | 500 | 0 | 1474.97 | 503.82 | 7.76 | 539 | 96.21 | 49.417 |
|  CBR SOAP Header Proxy | 4G | 1000 | 1024 | 1000 | 0 | 732.76 | 1002.75 | 5.89 | 1023 | 96.1 | 49.463 |
|  CBR SOAP Header Proxy | 4G | 1000 | 10240 | 0 | 0 | 1130.83 | 661.37 | 228.6 | 1231 | 94.43 | 49.461 |
|  CBR SOAP Header Proxy | 4G | 1000 | 10240 | 30 | 0 | 1156.51 | 647.32 | 210.09 | 1231 | 94.52 | 49.247 |
|  CBR SOAP Header Proxy | 4G | 1000 | 10240 | 100 | 0 | 1174.12 | 636.69 | 206.43 | 1167 | 94.38 | 49.256 |
|  CBR SOAP Header Proxy | 4G | 1000 | 10240 | 500 | 0 | 1105.41 | 672.67 | 106.09 | 1003 | 94.77 | 49.457 |
|  CBR SOAP Header Proxy | 4G | 1000 | 10240 | 1000 | 0 | 686.33 | 1070.27 | 81.16 | 1367 | 95.2 | 49.596 |
|  CBR SOAP Header Proxy | 4G | 1000 | 102400 | 0 | 100 | 12717.81 | 49.94 | 175.81 | 441 | 89.71 | 49.421 |
|  CBR SOAP Header Proxy | 4G | 1000 | 102400 | 30 | 100 | 14301.27 | 44.06 | 97.31 | 413 | 90.73 | 49.487 |
|  CBR SOAP Header Proxy | 4G | 1000 | 102400 | 100 | 100 | 13111.14 | 46.89 | 150.22 | 439 | 90.46 | 49.442 |
|  CBR SOAP Header Proxy | 4G | 1000 | 102400 | 500 | 100 | 13170.97 | 47.01 | 144.63 | 431 | 89.85 | 49.504 |
|  CBR SOAP Header Proxy | 4G | 1000 | 102400 | 1000 | 100 | 13624.32 | 46.04 | 122.48 | 453 | 90.4 | 49.769 |
|  CBR Transport Header Proxy | 4G | 500 | 500 | 0 | 0 | 5774.08 | 64.84 | 35.92 | 183 | 95.75 | 49.822 |
|  CBR Transport Header Proxy | 4G | 500 | 500 | 30 | 0 | 7737.32 | 48.38 | 13.13 | 97 | 96 | 49.44 |
|  CBR Transport Header Proxy | 4G | 500 | 500 | 100 | 0 | 3636.24 | 103.07 | 5.45 | 130 | 96.04 | 49.538 |
|  CBR Transport Header Proxy | 4G | 500 | 500 | 500 | 0 | 741.1 | 501.25 | 1.55 | 505 | 96.19 | 49.481 |
|  CBR Transport Header Proxy | 4G | 500 | 500 | 1000 | 0 | 366.98 | 1002.07 | 1.24 | 1003 | 95.81 | 49.472 |
|  CBR Transport Header Proxy | 4G | 500 | 1024 | 0 | 0 | 6109.09 | 61.42 | 33.26 | 169 | 96 | 49.381 |
|  CBR Transport Header Proxy | 4G | 500 | 1024 | 30 | 0 | 7454.05 | 50.24 | 13.24 | 102 | 96.17 | 49.478 |
|  CBR Transport Header Proxy | 4G | 500 | 1024 | 100 | 0 | 3607.04 | 103.69 | 6.84 | 136 | 96.1 | 49.62 |
|  CBR Transport Header Proxy | 4G | 500 | 1024 | 500 | 0 | 740.14 | 501.25 | 1.92 | 505 | 96.22 | 49.418 |
|  CBR Transport Header Proxy | 4G | 500 | 1024 | 1000 | 0 | 366.85 | 1002.07 | 1.23 | 1003 | 96.17 | 49.533 |
|  CBR Transport Header Proxy | 4G | 500 | 10240 | 0 | 0 | 3593.42 | 104.35 | 37.27 | 207 | 95.98 | 49.49 |
|  CBR Transport Header Proxy | 4G | 500 | 10240 | 30 | 0 | 4334.49 | 86.42 | 24.22 | 158 | 96.08 | 49.447 |
|  CBR Transport Header Proxy | 4G | 500 | 10240 | 100 | 0 | 3106.54 | 120.46 | 18.47 | 183 | 96.14 | 49.414 |
|  CBR Transport Header Proxy | 4G | 500 | 10240 | 500 | 0 | 740.64 | 501.68 | 2.03 | 507 | 96.19 | 49.437 |
|  CBR Transport Header Proxy | 4G | 500 | 10240 | 1000 | 0 | 366.81 | 1002.13 | 1.34 | 1007 | 96.31 | 49.496 |
|  CBR Transport Header Proxy | 4G | 500 | 102400 | 0 | 0 | 804.57 | 465.32 | 122.85 | 799 | 96.07 | 49.722 |
|  CBR Transport Header Proxy | 4G | 500 | 102400 | 30 | 0 | 858.94 | 436.66 | 110.15 | 747 | 96 | 49.282 |
|  CBR Transport Header Proxy | 4G | 500 | 102400 | 100 | 0 | 888.63 | 420.69 | 91.43 | 663 | 95.99 | 49.531 |
|  CBR Transport Header Proxy | 4G | 500 | 102400 | 500 | 0 | 668.55 | 552.72 | 52.19 | 751 | 96.07 | 49.239 |
|  CBR Transport Header Proxy | 4G | 500 | 102400 | 1000 | 0 | 362.96 | 1011.05 | 13.51 | 1071 | 96.18 | 49.443 |
|  CBR Transport Header Proxy | 4G | 1000 | 500 | 0 | 0 | 5797.04 | 129.38 | 54.45 | 287 | 95.64 | 49.451 |
|  CBR Transport Header Proxy | 4G | 1000 | 500 | 30 | 0 | 5167.18 | 144.82 | 55.82 | 319 | 95.89 | 49.641 |
|  CBR Transport Header Proxy | 4G | 1000 | 500 | 100 | 0 | 6361.74 | 117.81 | 17.88 | 193 | 96.38 | 52.801 |
|  CBR Transport Header Proxy | 4G | 1000 | 500 | 500 | 0 | 1479.16 | 501.61 | 3.4 | 511 | 96.37 | 49.395 |
|  CBR Transport Header Proxy | 4G | 1000 | 500 | 1000 | 0 | 732.52 | 1002.25 | 2.64 | 1007 | 96.19 | 49.47 |
|  CBR Transport Header Proxy | 4G | 1000 | 1024 | 0 | 0 | 5846.81 | 128.22 | 59.5 | 299 | 95.87 | 49.614 |
|  CBR Transport Header Proxy | 4G | 1000 | 1024 | 30 | 0 | 6320.7 | 118.36 | 46.26 | 261 | 96.35 | 49.783 |
|  CBR Transport Header Proxy | 4G | 1000 | 1024 | 100 | 0 | 6278.28 | 119.21 | 19.42 | 201 | 95.93 | 49.529 |
|  CBR Transport Header Proxy | 4G | 1000 | 1024 | 500 | 0 | 1479.45 | 501.74 | 3.34 | 515 | 96.91 | 49.408 |
|  CBR Transport Header Proxy | 4G | 1000 | 1024 | 1000 | 0 | 732.99 | 1002.18 | 1.95 | 1007 | 95.83 | 49.397 |
|  CBR Transport Header Proxy | 4G | 1000 | 10240 | 0 | 0 | 3533.77 | 211.96 | 80.42 | 445 | 95.87 | 49.388 |
|  CBR Transport Header Proxy | 4G | 1000 | 10240 | 30 | 0 | 3467.55 | 216.01 | 73.54 | 429 | 95.8 | 49.466 |
|  CBR Transport Header Proxy | 4G | 1000 | 10240 | 100 | 0 | 4359.85 | 171.29 | 46.39 | 323 | 95.98 | 49.373 |
|  CBR Transport Header Proxy | 4G | 1000 | 10240 | 500 | 0 | 1471.88 | 504.23 | 8.56 | 543 | 96.26 | 49.298 |
|  CBR Transport Header Proxy | 4G | 1000 | 10240 | 1000 | 0 | 732.49 | 1002.43 | 3.01 | 1015 | 96.34 | 49.391 |
|  CBR Transport Header Proxy | 4G | 1000 | 102400 | 0 | 0 | 878.56 | 846.92 | 308.36 | 1663 | 95.82 | 49.477 |
|  CBR Transport Header Proxy | 4G | 1000 | 102400 | 30 | 0 | 825.1 | 904.25 | 314.16 | 1735 | 96.33 | 49.342 |
|  CBR Transport Header Proxy | 4G | 1000 | 102400 | 100 | 0 | 937.44 | 794.47 | 283 | 1543 | 95.91 | 49.381 |
|  CBR Transport Header Proxy | 4G | 1000 | 102400 | 500 | 0 | 890.01 | 830.27 | 169.68 | 1319 | 95.91 | 49.419 |
|  CBR Transport Header Proxy | 4G | 1000 | 102400 | 1000 | 0 | 697.81 | 1048.31 | 49.74 | 1247 | 96.14 | 49.449 |
|  Direct Proxy | 4G | 500 | 500 | 0 | 0 | 5879.07 | 63.72 | 34 | 170 | 96.36 | 49.774 |
|  Direct Proxy | 4G | 500 | 500 | 30 | 0 | 7633.72 | 49.03 | 13.4 | 101 | 96.04 | 49.405 |
|  Direct Proxy | 4G | 500 | 500 | 100 | 0 | 3634.62 | 103.07 | 5.68 | 131 | 96.31 | 49.355 |
|  Direct Proxy | 4G | 500 | 500 | 500 | 0 | 741.37 | 501.3 | 2.12 | 507 | 96.29 | 49.267 |
|  Direct Proxy | 4G | 500 | 500 | 1000 | 0 | 367.4 | 1002.09 | 1.47 | 1003 | 96.57 | 49.114 |
|  Direct Proxy | 4G | 500 | 1024 | 0 | 0 | 6147.96 | 60.9 | 32.41 | 161 | 95.93 | 49.462 |
|  Direct Proxy | 4G | 500 | 1024 | 30 | 0 | 7423.19 | 50.48 | 14.08 | 107 | 95.76 | 49.36 |
|  Direct Proxy | 4G | 500 | 1024 | 100 | 0 | 3622.49 | 103.34 | 5.31 | 126 | 96.11 | 49.356 |
|  Direct Proxy | 4G | 500 | 1024 | 500 | 0 | 740.77 | 501.26 | 2.23 | 505 | 96.15 | 49.347 |
|  Direct Proxy | 4G | 500 | 1024 | 1000 | 0 | 366.78 | 1002.07 | 1.18 | 1003 | 96.32 | 49.358 |
|  Direct Proxy | 4G | 500 | 10240 | 0 | 0 | 3680.67 | 101.89 | 42.06 | 232 | 95.59 | 49.341 |
|  Direct Proxy | 4G | 500 | 10240 | 30 | 0 | 4491.57 | 83.44 | 27.45 | 174 | 95.94 | 49.326 |
|  Direct Proxy | 4G | 500 | 10240 | 100 | 0 | 3210.48 | 116.65 | 17.37 | 176 | 96.18 | 49.265 |
|  Direct Proxy | 4G | 500 | 10240 | 500 | 0 | 739.77 | 501.54 | 1.6 | 507 | 96.93 | 52.524 |
|  Direct Proxy | 4G | 500 | 10240 | 1000 | 0 | 367.09 | 1002.12 | 1.31 | 1007 | 96.08 | 49.282 |
|  Direct Proxy | 4G | 500 | 102400 | 0 | 0 | 911.52 | 410.04 | 161.33 | 835 | 95.98 | 49.268 |
|  Direct Proxy | 4G | 500 | 102400 | 30 | 0 | 927.97 | 404.03 | 150.66 | 787 | 96.09 | 49.695 |
|  Direct Proxy | 4G | 500 | 102400 | 100 | 0 | 1006.74 | 372.2 | 118.55 | 699 | 96.13 | 49.461 |
|  Direct Proxy | 4G | 500 | 102400 | 500 | 0 | 673.47 | 551.45 | 56.35 | 755 | 96.27 | 49.396 |
|  Direct Proxy | 4G | 500 | 102400 | 1000 | 0 | 364.77 | 1007.49 | 6.76 | 1031 | 96.25 | 49.476 |
|  Direct Proxy | 4G | 1000 | 500 | 0 | 0 | 6129.65 | 122.26 | 59.82 | 309 | 95.56 | 49.369 |
|  Direct Proxy | 4G | 1000 | 500 | 30 | 0 | 6143.98 | 121.86 | 45.26 | 263 | 95.84 | 49.247 |
|  Direct Proxy | 4G | 1000 | 500 | 100 | 0 | 6427.96 | 116.39 | 17.67 | 190 | 95.85 | 49.447 |
|  Direct Proxy | 4G | 1000 | 500 | 500 | 0 | 1478.32 | 501.87 | 3.5 | 515 | 96.03 | 49.592 |
|  Direct Proxy | 4G | 1000 | 500 | 1000 | 0 | 732.81 | 1002.25 | 2.78 | 1011 | 96.35 | 49.478 |
|  Direct Proxy | 4G | 1000 | 1024 | 0 | 0 | 5946.43 | 125.89 | 55.01 | 289 | 95.8 | 49.45 |
|  Direct Proxy | 4G | 1000 | 1024 | 30 | 0 | 6299.69 | 118.89 | 44.66 | 252 | 95.7 | 49.376 |
|  Direct Proxy | 4G | 1000 | 1024 | 100 | 0 | 6370.43 | 117.37 | 17.98 | 189 | 95.8 | 49.3 |
|  Direct Proxy | 4G | 1000 | 1024 | 500 | 0 | 1478.03 | 501.57 | 3.09 | 511 | 96.15 | 49.683 |
|  Direct Proxy | 4G | 1000 | 1024 | 1000 | 0 | 732.64 | 1002.14 | 1.86 | 1007 | 95.85 | 49.567 |
|  Direct Proxy | 4G | 1000 | 10240 | 0 | 0 | 3526.28 | 212.81 | 137.53 | 519 | 95.91 | 48.963 |
|  Direct Proxy | 4G | 1000 | 10240 | 30 | 0 | 3961.72 | 189.1 | 66.91 | 401 | 95.93 | 49.498 |
|  Direct Proxy | 4G | 1000 | 10240 | 100 | 0 | 4143.22 | 180.34 | 46.14 | 329 | 96 | 49.451 |
|  Direct Proxy | 4G | 1000 | 10240 | 500 | 0 | 1466.87 | 505.51 | 9.04 | 547 | 96.13 | 49.383 |
|  Direct Proxy | 4G | 1000 | 10240 | 1000 | 0 | 732.7 | 1002.3 | 2.67 | 1011 | 96.25 | 49.522 |
|  Direct Proxy | 4G | 1000 | 102400 | 0 | 0 | 799.55 | 929.38 | 331.61 | 1759 | 96.2 | 49.377 |
|  Direct Proxy | 4G | 1000 | 102400 | 30 | 0 | 856.58 | 869.96 | 303.07 | 1591 | 96.08 | 49.27 |
|  Direct Proxy | 4G | 1000 | 102400 | 100 | 0 | 802.34 | 925.6 | 286.36 | 1743 | 96.13 | 49.387 |
|  Direct Proxy | 4G | 1000 | 102400 | 500 | 0 | 855.04 | 859.63 | 168.48 | 1319 | 96.4 | 49.5 |
|  Direct Proxy | 4G | 1000 | 102400 | 1000 | 0 | 656.58 | 1105.06 | 104.09 | 1431 | 96.22 | 49.303 |
|  XSLT Enhanced Proxy | 4G | 500 | 500 | 0 | 0 | 113.32 | 3207.12 | 774.08 | 4639 | 95.22 | 49.475 |
|  XSLT Enhanced Proxy | 4G | 500 | 500 | 30 | 0 | 120.24 | 3009.22 | 716.57 | 4223 | 95.1 | 49.519 |
|  XSLT Enhanced Proxy | 4G | 500 | 500 | 100 | 0 | 110.79 | 3268.49 | 715.84 | 4735 | 95.21 | 49.465 |
|  XSLT Enhanced Proxy | 4G | 500 | 500 | 500 | 0 | 111.41 | 3269.29 | 750.68 | 4799 | 95.43 | 49.446 |
|  XSLT Enhanced Proxy | 4G | 500 | 500 | 1000 | 0 | 116.56 | 3104.31 | 653.46 | 4415 | 94.8 | 49.603 |
|  XSLT Enhanced Proxy | 4G | 500 | 1024 | 0 | 0 | 109.33 | 3332.95 | 811.14 | 4767 | 95.33 | 49.579 |
|  XSLT Enhanced Proxy | 4G | 500 | 1024 | 30 | 0 | 116.65 | 3131.1 | 828.4 | 4575 | 95.29 | 49.254 |
|  XSLT Enhanced Proxy | 4G | 500 | 1024 | 100 | 0 | 119.5 | 3040.89 | 655.54 | 4351 | 95.03 | 49.463 |
|  XSLT Enhanced Proxy | 4G | 500 | 1024 | 500 | 0 | 113.25 | 3186.51 | 678.63 | 4639 | 95.36 | 49.514 |
|  XSLT Enhanced Proxy | 4G | 500 | 1024 | 1000 | 0 | 118.63 | 3047.76 | 687.44 | 4575 | 95.15 | 49.362 |
|  XSLT Enhanced Proxy | 4G | 500 | 10240 | 0 | 0 | 105.9 | 3428.49 | 871.16 | 5055 | 95.06 | 49.423 |
|  XSLT Enhanced Proxy | 4G | 500 | 10240 | 30 | 0 | 105.67 | 3432.94 | 852.33 | 5343 | 95.14 | 49.554 |
|  XSLT Enhanced Proxy | 4G | 500 | 10240 | 100 | 0 | 104.92 | 3468.67 | 894.25 | 5375 | 95.09 | 49.544 |
|  XSLT Enhanced Proxy | 4G | 500 | 10240 | 500 | 0 | 103.33 | 3485.09 | 701.49 | 4671 | 95.06 | 49.408 |
|  XSLT Enhanced Proxy | 4G | 500 | 10240 | 1000 | 0 | 100.02 | 3623.26 | 730.41 | 5343 | 95.44 | 49.386 |
|  XSLT Enhanced Proxy | 4G | 500 | 102400 | 0 | 0 | 20.09 | 16415.34 | 2855.1 | 23039 | 94.97 | 49.367 |
|  XSLT Enhanced Proxy | 4G | 500 | 102400 | 30 | 0 | 34.81 | 9809.12 | 1721.15 | 12735 | 94.95 | 49.444 |
|  XSLT Enhanced Proxy | 4G | 500 | 102400 | 100 | 0 | 23.69 | 13914.89 | 1918.73 | 18047 | 95.4 | 49.387 |
|  XSLT Enhanced Proxy | 4G | 500 | 102400 | 500 | 0 | 28.65 | 12156.01 | 2071.25 | 15743 | 95.13 | 49.388 |
|  XSLT Enhanced Proxy | 4G | 500 | 102400 | 1000 | 0 | 28.78 | 11738.9 | 2063.79 | 15167 | 94.93 | 49.42 |
|  XSLT Enhanced Proxy | 4G | 1000 | 500 | 0 | 0 | 106.12 | 6593.23 | 1159.58 | 8447 | 94.82 | 49.391 |
|  XSLT Enhanced Proxy | 4G | 1000 | 500 | 30 | 0 | 110.47 | 6409.82 | 1296.16 | 8383 | 95.36 | 52.613 |
|  XSLT Enhanced Proxy | 4G | 1000 | 500 | 100 | 0 | 111.9 | 6290.12 | 1357.46 | 8703 | 95.02 | 49.437 |
|  XSLT Enhanced Proxy | 4G | 1000 | 500 | 500 | 0 | 105.97 | 6653.12 | 1363.32 | 9471 | 94.62 | 49.515 |
|  XSLT Enhanced Proxy | 4G | 1000 | 500 | 1000 | 0 | 109.07 | 6403.55 | 1097.23 | 8159 | 94.94 | 49.505 |
|  XSLT Enhanced Proxy | 4G | 1000 | 1024 | 0 | 0 | 115.97 | 6107.4 | 1170.85 | 8383 | 94.94 | 49.522 |
|  XSLT Enhanced Proxy | 4G | 1000 | 1024 | 30 | 0 | 109.89 | 6413.08 | 1491.69 | 9407 | 94.88 | 49.422 |
|  XSLT Enhanced Proxy | 4G | 1000 | 1024 | 100 | 0 | 101.95 | 6879.79 | 1214.96 | 9215 | 94.91 | 49.532 |
|  XSLT Enhanced Proxy | 4G | 1000 | 1024 | 500 | 0 | 108.92 | 6422.61 | 1046.2 | 8255 | 95.06 | 49.41 |
|  XSLT Enhanced Proxy | 4G | 1000 | 1024 | 1000 | 0 | 110.56 | 6329.2 | 1095.19 | 8095 | 94.93 | 49.475 |
|  XSLT Enhanced Proxy | 4G | 1000 | 10240 | 0 | 0 | 100.08 | 6978.56 | 1137.45 | 8895 | 94.61 | 49.427 |
|  XSLT Enhanced Proxy | 4G | 1000 | 10240 | 30 | 0 | 98.73 | 7127.98 | 1469.08 | 9535 | 94.96 | 49.482 |
|  XSLT Enhanced Proxy | 4G | 1000 | 10240 | 100 | 0 | 103.56 | 6806.55 | 1403.86 | 9087 | 94.88 | 49.405 |
|  XSLT Enhanced Proxy | 4G | 1000 | 10240 | 500 | 0 | 101.35 | 6872.03 | 1003.6 | 8383 | 95 | 49.541 |
|  XSLT Enhanced Proxy | 4G | 1000 | 10240 | 1000 | 0 | 98.26 | 7144.41 | 1295.14 | 9791 | 94.86 | 49.29 |
|  XSLT Enhanced Proxy | 4G | 1000 | 102400 | 0 | 70.29 | 34.51 | 18041.69 | 5867.99 | 26751 | 94.25 | 49.516 |
|  XSLT Enhanced Proxy | 4G | 1000 | 102400 | 30 | 72.36 | 21.12 | 29295.07 | 6038.52 | 42495 | 94.59 | 49.816 |
|  XSLT Enhanced Proxy | 4G | 1000 | 102400 | 100 | 98.8 | 852.89 | 677.85 | 3064.66 | 19327 | 94.42 | 49.733 |
|  XSLT Enhanced Proxy | 4G | 1000 | 102400 | 500 | 73.18 | 41.29 | 14981.15 | 6947.26 | 24447 | 94.96 | 52.797 |
|  XSLT Enhanced Proxy | 4G | 1000 | 102400 | 1000 | 61.21 | 28.04 | 23323.08 | 4667.98 | 31615 | 94.49 | 49.392 |
|  XSLT Proxy | 4G | 500 | 500 | 0 | 0 | 107.73 | 3373.21 | 861.69 | 4799 | 94.97 | 49.949 |
|  XSLT Proxy | 4G | 500 | 500 | 30 | 0 | 111.76 | 3255.9 | 890.39 | 4991 | 95.28 | 49.474 |
|  XSLT Proxy | 4G | 500 | 500 | 100 | 0 | 108.62 | 3354.2 | 710 | 4703 | 95.14 | 49.432 |
|  XSLT Proxy | 4G | 500 | 500 | 500 | 0 | 115.15 | 3143.69 | 623.42 | 4447 | 94.83 | 49.471 |
|  XSLT Proxy | 4G | 500 | 500 | 1000 | 0 | 108.44 | 3318.86 | 671.33 | 4671 | 94.95 | 49.449 |
|  XSLT Proxy | 4G | 500 | 1024 | 0 | 0 | 109.04 | 3341.42 | 992.33 | 5183 | 95.1 | 49.25 |
|  XSLT Proxy | 4G | 500 | 1024 | 30 | 0 | 97.47 | 3709.3 | 815.38 | 5503 | 94.83 | 49.571 |
|  XSLT Proxy | 4G | 500 | 1024 | 100 | 0 | 107.44 | 3391.37 | 994.18 | 5343 | 94.99 | 49.357 |
|  XSLT Proxy | 4G | 500 | 1024 | 500 | 0 | 102.75 | 3519.17 | 843.81 | 5407 | 95.04 | 49.307 |
|  XSLT Proxy | 4G | 500 | 1024 | 1000 | 0 | 111.42 | 3232.55 | 574.64 | 4287 | 95.04 | 49.371 |
|  XSLT Proxy | 4G | 500 | 10240 | 0 | 0 | 81.65 | 4393.04 | 1263.15 | 7199 | 94.35 | 49.485 |
|  XSLT Proxy | 4G | 500 | 10240 | 30 | 0 | 85.53 | 4178 | 1036.2 | 6207 | 94.08 | 49.469 |
|  XSLT Proxy | 4G | 500 | 10240 | 100 | 0 | 82.12 | 4341 | 1121.21 | 6463 | 93.73 | 49.552 |
|  XSLT Proxy | 4G | 500 | 10240 | 500 | 0 | 86.16 | 4171.18 | 1013.82 | 5791 | 94.24 | 49.41 |
|  XSLT Proxy | 4G | 500 | 10240 | 1000 | 0 | 84.73 | 4233.62 | 959.46 | 6079 | 94.21 | 49.474 |
|  XSLT Proxy | 4G | 500 | 102400 | 0 | 100 | 4333.05 | 60.34 | 524.89 | 355 | 91.57 | 49.589 |
|  XSLT Proxy | 4G | 500 | 102400 | 30 | 100 | 3757.98 | 71.09 | 688.66 | 349 | 92.04 | 49.788 |
|  XSLT Proxy | 4G | 500 | 102400 | 100 | 100 | 3870.11 | 71.3 | 649.94 | 371 | 91.7 | 49.45 |
|  XSLT Proxy | 4G | 500 | 102400 | 500 | 99.99 | 2543.06 | 95.94 | 923.94 | 1003 | 91.23 | 49.616 |
|  XSLT Proxy | 4G | 500 | 102400 | 1000 | 100 | 5108.15 | 48.95 | 414.37 | 337 | 92.1 | 49.364 |
|  XSLT Proxy | 4G | 1000 | 500 | 0 | 0 | 102.64 | 6804 | 1544.26 | 9855 | 94.67 | 49.795 |
|  XSLT Proxy | 4G | 1000 | 500 | 30 | 0 | 106.67 | 6537.24 | 996.73 | 8319 | 94.83 | 49.454 |
|  XSLT Proxy | 4G | 1000 | 500 | 100 | 0 | 93.21 | 7421.16 | 1312.56 | 9855 | 94.56 | 49.391 |
|  XSLT Proxy | 4G | 1000 | 500 | 500 | 0 | 100.2 | 7033.56 | 1194.55 | 8831 | 94.73 | 49.387 |
|  XSLT Proxy | 4G | 1000 | 500 | 1000 | 0 | 106.15 | 6621.3 | 1376.19 | 8895 | 94.62 | 49.539 |
|  XSLT Proxy | 4G | 1000 | 1024 | 0 | 0 | 100.44 | 7021.95 | 1638.68 | 9343 | 94.44 | 49.506 |
|  XSLT Proxy | 4G | 1000 | 1024 | 30 | 0 | 103.16 | 6830.02 | 1387.2 | 9215 | 94.76 | 49.042 |
|  XSLT Proxy | 4G | 1000 | 1024 | 100 | 0 | 104.77 | 6732.28 | 1375.22 | 8767 | 94.79 | 49.38 |
|  XSLT Proxy | 4G | 1000 | 1024 | 500 | 0 | 95.86 | 7306.43 | 1182.66 | 9343 | 94.7 | 49.529 |
|  XSLT Proxy | 4G | 1000 | 1024 | 1000 | 0 | 104.6 | 6699.94 | 1428.05 | 9151 | 94.68 | 49.481 |
|  XSLT Proxy | 4G | 1000 | 10240 | 0 | 42.72 | 37.4 | 14819.08 | 8816.42 | 31487 | 93.08 | 49.455 |
|  XSLT Proxy | 4G | 1000 | 10240 | 30 | 99.32 | 5111.54 | 124.03 | 694.03 | 3615 | 93.06 | 49.787 |
|  XSLT Proxy | 4G | 1000 | 10240 | 100 | 99.14 | 4386.79 | 145.06 | 816.21 | 6015 | 93 | 49.783 |
|  XSLT Proxy | 4G | 1000 | 10240 | 500 | 99 | 3369.39 | 187 | 945.01 | 7135 | 93.01 | 49.369 |
|  XSLT Proxy | 4G | 1000 | 10240 | 1000 | 99.19 | 4217.31 | 149.79 | 802.19 | 5951 | 92.98 | 49.384 |
|  XSLT Proxy | 4G | 1000 | 102400 | 0 | 100 | 9745.32 | 55.1 | 127.66 | 551 | 91.68 | 49.46 |
|  XSLT Proxy | 4G | 1000 | 102400 | 30 | 100 | 7940.58 | 64.04 | 166.46 | 603 | 91.71 | 49.465 |
|  XSLT Proxy | 4G | 1000 | 102400 | 100 | 100 | 9721.93 | 53.57 | 154.52 | 505 | 91.83 | 49.444 |
|  XSLT Proxy | 4G | 1000 | 102400 | 500 | 100 | 6984.64 | 66.58 | 287.35 | 579 | 91.9 | 49.744 |
|  XSLT Proxy | 4G | 1000 | 102400 | 1000 | 100 | 10969.91 | 49.9 | 107.7 | 483 | 89.22 | 49.929 |
