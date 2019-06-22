# WSO2 Enterprise Integrator Performance Test Results

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

1. **Throughput**: The number of requests that the WSO2 Enterprise Integrator processes during a specific time interval (e.g. per second).
2. **Response Time**: The end-to-end latency for an operation of invoking a service in WSO2 Enterprise Integrator . The complete distribution of response times was recorded.

In addition to the above metrics, we measure the load average and several memory-related metrics.

The following are the test parameters.

| Test Parameter | Description | Values |
| --- | --- | --- |
| Scenario Name | The name of the test scenario. | Refer to the above table. |
| Heap Size | The amount of memory allocated to the application | 4G |
| Concurrent Users | The number of users accessing the application at the same time. | 100, 200, 500 |
| Message Size (Bytes) | The request payload size in Bytes. | 1024, 10240, 102400 |
| Back-end Delay (ms) | The delay added by the Back-end service. | 0, 30, 1000 |

The duration of each test is **900 seconds**. The warm-up period is **300 seconds**.
The measurement results are collected after the warm-up period.

The performance tests were executed on 3 AWS CloudFormation stacks.


System information for WSO2 Enterprise Integrator in 1st AWS CloudFormation stack.

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
| Operating System | Distribution | Kernel | Linux ip-10-0-1-192 4.15.0-1041-aws #43-Ubuntu SMP Thu Jun 6 13:39:11 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux |
System information for WSO2 Enterprise Integrator in 2nd AWS CloudFormation stack.

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
| Operating System | Distribution | Kernel | Linux ip-10-0-1-119 4.15.0-1041-aws #43-Ubuntu SMP Thu Jun 6 13:39:11 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux |
System information for WSO2 Enterprise Integrator in 3rd AWS CloudFormation stack.

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
| Operating System | Distribution | Kernel | Linux ip-10-0-1-199 4.15.0-1041-aws #43-Ubuntu SMP Thu Jun 6 13:39:11 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux |


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
|  CBR Proxy | 4G | 100 | 1024 | 0 | 0 | 8497.96 | 11.67 | 14.24 | 93 | 99.25 | 77.54 |
|  CBR Proxy | 4G | 100 | 1024 | 30 | 0 | 3209.7 | 31.09 | 1.24 | 35 | 99.66 | 57.171 |
|  CBR Proxy | 4G | 100 | 1024 | 1000 | 0 | 99.68 | 1002.03 | 0.5 | 1003 | 99.84 | 81.056 |
|  CBR Proxy | 4G | 100 | 10240 | 0 | 0 | 1473.62 | 65.66 | 283.94 | 188 | 98.93 | 57.548 |
|  CBR Proxy | 4G | 100 | 10240 | 30 | 0 | 1416.4 | 70.37 | 21.71 | 138 | 99.03 | 81.468 |
|  CBR Proxy | 4G | 100 | 10240 | 1000 | 0 | 99.6 | 1002.58 | 1.78 | 1007 | 99.8 | 79.323 |
|  CBR Proxy | 4G | 100 | 102400 | 0 | 0 | 160.36 | 623.32 | 176.35 | 1063 | 92.28 | 79.678 |
|  CBR Proxy | 4G | 100 | 102400 | 30 | 0 | 161.81 | 617.85 | 166.7 | 1039 | 92.66 | 74.167 |
|  CBR Proxy | 4G | 100 | 102400 | 1000 | 0 | 84.97 | 1175.45 | 140.39 | 1591 | 96.44 | 81.92 |
|  CBR Proxy | 4G | 200 | 1024 | 0 | 0 | 9191.5 | 21.61 | 18.09 | 113 | 99.04 | 81.015 |
|  CBR Proxy | 4G | 200 | 1024 | 30 | 0 | 5705.47 | 34.91 | 4.12 | 53 | 99.39 | 57.551 |
|  CBR Proxy | 4G | 200 | 1024 | 1000 | 0 | 199.26 | 1002.36 | 15.33 | 1011 | 99.83 | 75.705 |
|  CBR Proxy | 4G | 200 | 10240 | 0 | 0 | 1576.19 | 126.79 | 65.06 | 317 | 98.21 | 77.62 |
|  CBR Proxy | 4G | 200 | 10240 | 30 | 0 | 1308.49 | 134.34 | 136.61 | 283 | 98.3 | 57.42 |
|  CBR Proxy | 4G | 200 | 10240 | 1000 | 0 | 199.01 | 1002.99 | 4.19 | 1019 | 99.67 | 58.436 |
|  CBR Proxy | 4G | 200 | 102400 | 0 | 0 | 134.6 | 1482.44 | 441.52 | 2879 | 80.13 | 462.802 |
|  CBR Proxy | 4G | 200 | 102400 | 30 | 0 | 134.88 | 1481.46 | 420.11 | 2847 | 80.79 | 449.102 |
|  CBR Proxy | 4G | 200 | 102400 | 1000 | 0 | 132.09 | 1511.18 | 264.96 | 2607 | 84.44 | 420.104 |
|  CBR Proxy | 4G | 500 | 1024 | 0 | 0 | 8879.68 | 56.17 | 28.57 | 151 | 98.56 | 79.233 |
|  CBR Proxy | 4G | 500 | 1024 | 30 | 0 | 8209.22 | 60.75 | 20.33 | 138 | 98.68 | 79.509 |
|  CBR Proxy | 4G | 500 | 1024 | 1000 | 0 | 498.4 | 1002.12 | 1.27 | 1007 | 99.77 | 79.014 |
|  CBR Proxy | 4G | 500 | 10240 | 0 | 0 | 1376.51 | 318.52 | 181.75 | 679 | 95.64 | 80.161 |
|  CBR Proxy | 4G | 500 | 10240 | 30 | 0 | 1604.34 | 311.7 | 114.5 | 623 | 95.71 | 76.121 |
|  CBR Proxy | 4G | 500 | 10240 | 1000 | 0 | 495.89 | 1006.66 | 9.11 | 1047 | 98.86 | 78.376 |
|  CBR Proxy | 4G | 500 | 102400 | 0 | 0 | 88.76 | 5616.15 | 1653.52 | 8703 | 54.49 | 1052.06 |
|  CBR Proxy | 4G | 500 | 102400 | 30 | 0 | 81.43 | 6100.92 | 1922.19 | 9343 | 52.66 | 1121.903 |
|  CBR Proxy | 4G | 500 | 102400 | 1000 | 0 | 94.09 | 5286.31 | 1399.15 | 8127 | 59.86 | 1005.887 |
|  CBR SOAP Header Proxy | 4G | 100 | 1024 | 0 | 0 | 9728.96 | 10.21 | 15.44 | 101 | 99.33 | 56.792 |
|  CBR SOAP Header Proxy | 4G | 100 | 1024 | 30 | 0 | 3222.86 | 30.98 | 1.17 | 34 | 99.69 | 57.297 |
|  CBR SOAP Header Proxy | 4G | 100 | 1024 | 1000 | 0 | 99.7 | 1002.04 | 0.69 | 1003 | 99.84 | 42.794 |
|  CBR SOAP Header Proxy | 4G | 100 | 10240 | 0 | 0 | 2348.78 | 42.26 | 268.74 | 125 | 99.25 | 57.748 |
|  CBR SOAP Header Proxy | 4G | 100 | 10240 | 30 | 0 | 2134.14 | 46.78 | 9.3 | 77 | 99.36 | 79.826 |
|  CBR SOAP Header Proxy | 4G | 100 | 10240 | 1000 | 0 | 99.62 | 1002.4 | 1.38 | 1007 | 99.82 | 58.589 |
|  CBR SOAP Header Proxy | 4G | 100 | 102400 | 0 | 0 | 295.3 | 338.7 | 106.66 | 611 | 96.93 | 78.814 |
|  CBR SOAP Header Proxy | 4G | 100 | 102400 | 30 | 0 | 294.2 | 339.95 | 100.51 | 599 | 97.04 | 82.845 |
|  CBR SOAP Header Proxy | 4G | 100 | 102400 | 1000 | 0 | 98.06 | 1018.6 | 19.1 | 1095 | 98.86 | 81.28 |
|  CBR SOAP Header Proxy | 4G | 200 | 1024 | 0 | 0 | 10876.35 | 18.3 | 17.78 | 109 | 99.07 | 80.51 |
|  CBR SOAP Header Proxy | 4G | 200 | 1024 | 30 | 0 | 6047.18 | 33.02 | 2.89 | 46 | 99.46 | 79.342 |
|  CBR SOAP Header Proxy | 4G | 200 | 1024 | 1000 | 0 | 199.4 | 1002.05 | 0.7 | 1003 | 99.83 | 76.441 |
|  CBR SOAP Header Proxy | 4G | 200 | 10240 | 0 | 0 | 2370.17 | 84.26 | 149.13 | 218 | 98.86 | 79.945 |
|  CBR SOAP Header Proxy | 4G | 200 | 10240 | 30 | 0 | 2258.2 | 88.46 | 107.21 | 178 | 98.93 | 80.618 |
|  CBR SOAP Header Proxy | 4G | 200 | 10240 | 1000 | 0 | 198.98 | 1002.57 | 3.12 | 1015 | 99.77 | 57.878 |
|  CBR SOAP Header Proxy | 4G | 200 | 102400 | 0 | 0 | 267.29 | 747.81 | 221.95 | 1239 | 90.86 | 62.891 |
|  CBR SOAP Header Proxy | 4G | 200 | 102400 | 30 | 0 | 266.86 | 749.38 | 209.72 | 1215 | 91.29 | 78.652 |
|  CBR SOAP Header Proxy | 4G | 200 | 102400 | 1000 | 0 | 179.14 | 1114.45 | 87.37 | 1367 | 95.32 | 81.65 |
|  CBR SOAP Header Proxy | 4G | 500 | 1024 | 0 | 0 | 11120.04 | 44.82 | 24.3 | 128 | 98.6 | 80.562 |
|  CBR SOAP Header Proxy | 4G | 500 | 1024 | 30 | 0 | 9870.22 | 50.48 | 15.98 | 116 | 98.75 | 77.505 |
|  CBR SOAP Header Proxy | 4G | 500 | 1024 | 1000 | 0 | 498.54 | 1002.28 | 1.97 | 1011 | 99.79 | 79.774 |
|  CBR SOAP Header Proxy | 4G | 500 | 10240 | 0 | 0 | 2388.43 | 209.16 | 194.41 | 447 | 97.33 | 84.651 |
|  CBR SOAP Header Proxy | 4G | 500 | 10240 | 30 | 0 | 2403.97 | 207.97 | 74.67 | 415 | 97.55 | 81.09 |
|  CBR SOAP Header Proxy | 4G | 500 | 10240 | 1000 | 0 | 497.43 | 1003.04 | 4.21 | 1031 | 99.46 | 60.008 |
|  CBR SOAP Header Proxy | 4G | 500 | 102400 | 0 | 0 | 167.34 | 2982.75 | 1146.22 | 5215 | 60.99 | 939.564 |
|  CBR SOAP Header Proxy | 4G | 500 | 102400 | 30 | 0 | 170.68 | 2925.03 | 1118.38 | 5151 | 60.71 | 940.439 |
|  CBR SOAP Header Proxy | 4G | 500 | 102400 | 1000 | 0 | 170.74 | 2914.86 | 967.14 | 5343 | 63.64 | 897.903 |
|  CBR Transport Header Proxy | 4G | 100 | 1024 | 0 | 0 | 16125.94 | 6.13 | 13.27 | 86 | 99.37 | 42.285 |
|  CBR Transport Header Proxy | 4G | 100 | 1024 | 30 | 0 | 3231.06 | 30.91 | 8.3 | 33 | 99.78 | 55.574 |
|  CBR Transport Header Proxy | 4G | 100 | 1024 | 1000 | 0 | 99.66 | 1002.16 | 1.29 | 1007 | 99.85 | 55.477 |
|  CBR Transport Header Proxy | 4G | 100 | 10240 | 0 | 0 | 9306.5 | 10.62 | 73.1 | 77 | 99.6 | 77.574 |
|  CBR Transport Header Proxy | 4G | 100 | 10240 | 30 | 0 | 3211.75 | 31.07 | 0.91 | 34 | 99.79 | 81.925 |
|  CBR Transport Header Proxy | 4G | 100 | 10240 | 1000 | 0 | 99.66 | 1002.04 | 0.49 | 1003 | 99.84 | 55.23 |
|  CBR Transport Header Proxy | 4G | 100 | 102400 | 0 | 0 | 2294.09 | 43.06 | 10.28 | 73 | 99.82 | 41.86 |
|  CBR Transport Header Proxy | 4G | 100 | 102400 | 30 | 0 | 397.45 | 251.3 | 157.21 | 883 | 99.83 | 82.555 |
|  CBR Transport Header Proxy | 4G | 100 | 102400 | 1000 | 0 | 99.56 | 1002.57 | 1.52 | 1007 | 99.85 | 73.619 |
|  CBR Transport Header Proxy | 4G | 200 | 1024 | 0 | 0 | 18135.32 | 10.94 | 15.44 | 95 | 99.19 | 75.223 |
|  CBR Transport Header Proxy | 4G | 200 | 1024 | 30 | 0 | 6413.84 | 31.07 | 1.9 | 37 | 99.68 | 82.594 |
|  CBR Transport Header Proxy | 4G | 200 | 1024 | 1000 | 0 | 199.43 | 1002.04 | 0.73 | 1003 | 99.84 | 78.274 |
|  CBR Transport Header Proxy | 4G | 200 | 10240 | 0 | 0 | 9596.03 | 20.64 | 16.32 | 87 | 99.54 | 81.603 |
|  CBR Transport Header Proxy | 4G | 200 | 10240 | 30 | 0 | 6161.45 | 32.36 | 2.47 | 45 | 99.67 | 79.978 |
|  CBR Transport Header Proxy | 4G | 200 | 10240 | 1000 | 0 | 199.43 | 1002.11 | 0.95 | 1007 | 99.84 | 56.287 |
|  CBR Transport Header Proxy | 4G | 200 | 102400 | 0 | 0 | 2017.86 | 98.42 | 64.94 | 461 | 99.78 | 56.39 |
|  CBR Transport Header Proxy | 4G | 200 | 102400 | 30 | 0 | 398.22 | 501.64 | 153.28 | 963 | 99.83 | 77.625 |
|  CBR Transport Header Proxy | 4G | 200 | 102400 | 1000 | 0 | 198.93 | 1002.98 | 2.02 | 1011 | 99.82 | 58.585 |
|  CBR Transport Header Proxy | 4G | 500 | 1024 | 0 | 0 | 19343.19 | 25.72 | 23.18 | 116 | 99 | 79.307 |
|  CBR Transport Header Proxy | 4G | 500 | 1024 | 30 | 0 | 13552.22 | 36.8 | 6.59 | 66 | 99.28 | 80.449 |
|  CBR Transport Header Proxy | 4G | 500 | 1024 | 1000 | 0 | 498.64 | 1002.1 | 1.2 | 1007 | 99.83 | 82.851 |
|  CBR Transport Header Proxy | 4G | 500 | 10240 | 0 | 0 | 9736.34 | 51.19 | 27.24 | 140 | 99.44 | 63.979 |
|  CBR Transport Header Proxy | 4G | 500 | 10240 | 30 | 0 | 8977.58 | 55.56 | 16.12 | 109 | 99.47 | 82.062 |
|  CBR Transport Header Proxy | 4G | 500 | 10240 | 1000 | 0 | 498.6 | 1002.16 | 1.42 | 1007 | 99.83 | 76.071 |
|  CBR Transport Header Proxy | 4G | 500 | 102400 | 0 | 0 | 1738.46 | 272.14 | 220.67 | 1439 | 99.76 | 82.972 |
|  CBR Transport Header Proxy | 4G | 500 | 102400 | 30 | 0 | 397.08 | 1256.19 | 412.58 | 2527 | 99.82 | 81.839 |
|  CBR Transport Header Proxy | 4G | 500 | 102400 | 1000 | 0 | 412.39 | 1209.62 | 199.44 | 1863 | 99.83 | 72.67 |
|  Direct Proxy | 4G | 100 | 1024 | 0 | 0 | 16069.51 | 6.16 | 13.96 | 89 | 99.39 | 74.968 |
|  Direct Proxy | 4G | 100 | 1024 | 30 | 0 | 3215.68 | 31.03 | 1.18 | 34 | 99.79 | 79.513 |
|  Direct Proxy | 4G | 100 | 1024 | 1000 | 0 | 99.73 | 1002.02 | 0.58 | 1003 | 99.84 | 42.035 |
|  Direct Proxy | 4G | 100 | 10240 | 0 | 0 | 9179.95 | 10.79 | 13.44 | 75 | 99.57 | 55.991 |
|  Direct Proxy | 4G | 100 | 10240 | 30 | 0 | 3205.76 | 31.13 | 0.98 | 35 | 99.79 | 56.451 |
|  Direct Proxy | 4G | 100 | 10240 | 1000 | 0 | 99.66 | 1002.06 | 0.55 | 1007 | 99.84 | 76.983 |
|  Direct Proxy | 4G | 100 | 102400 | 0 | 0 | 2226.27 | 44.3 | 10.58 | 74 | 99.8 | 81.494 |
|  Direct Proxy | 4G | 100 | 102400 | 30 | 0 | 398.84 | 250.46 | 128.54 | 615 | 99.83 | 80.45 |
|  Direct Proxy | 4G | 100 | 102400 | 1000 | 0 | 99.53 | 1002.39 | 1.21 | 1007 | 99.85 | 75.696 |
|  Direct Proxy | 4G | 200 | 1024 | 0 | 0 | 18149.26 | 10.94 | 16.6 | 103 | 99.23 | 42.39 |
|  Direct Proxy | 4G | 200 | 1024 | 30 | 0 | 6434.69 | 31.01 | 1.85 | 36 | 99.68 | 81.862 |
|  Direct Proxy | 4G | 200 | 1024 | 1000 | 0 | 199.46 | 1002.07 | 0.8 | 1007 | 99.85 | 75.933 |
|  Direct Proxy | 4G | 200 | 10240 | 0 | 0 | 9754.99 | 20.28 | 15.88 | 85 | 99.5 | 59.301 |
|  Direct Proxy | 4G | 200 | 10240 | 30 | 0 | 6196.86 | 32.2 | 2.49 | 44 | 99.68 | 80.394 |
|  Direct Proxy | 4G | 200 | 10240 | 1000 | 0 | 199.42 | 1002.12 | 0.83 | 1007 | 99.83 | 73.546 |
|  Direct Proxy | 4G | 200 | 102400 | 0 | 0 | 2241.39 | 88.48 | 19.93 | 145 | 99.79 | 80.465 |
|  Direct Proxy | 4G | 200 | 102400 | 30 | 0 | 370.5 | 502.07 | 806.57 | 2351 | 99.82 | 55.065 |
|  Direct Proxy | 4G | 200 | 102400 | 1000 | 0 | 199.13 | 1002.37 | 1.27 | 1007 | 99.83 | 78.462 |
|  Direct Proxy | 4G | 500 | 1024 | 0 | 0 | 18519.47 | 26.86 | 21.3 | 110 | 99.02 | 80.511 |
|  Direct Proxy | 4G | 500 | 1024 | 30 | 0 | 13438.82 | 37.11 | 6.79 | 67 | 99.26 | 56.615 |
|  Direct Proxy | 4G | 500 | 1024 | 1000 | 0 | 498.45 | 1002.04 | 0.76 | 1003 | 99.81 | 57.728 |
|  Direct Proxy | 4G | 500 | 10240 | 0 | 0 | 9236.55 | 53.97 | 26.78 | 139 | 99.46 | 80.54 |
|  Direct Proxy | 4G | 500 | 10240 | 30 | 0 | 8702.31 | 57.32 | 15.77 | 109 | 99.48 | 80.603 |
|  Direct Proxy | 4G | 500 | 10240 | 1000 | 0 | 498.02 | 1002.21 | 1.56 | 1007 | 99.81 | 57.49 |
|  Direct Proxy | 4G | 500 | 102400 | 0 | 0 | 1838.73 | 266.84 | 71.14 | 449 | 99.77 | 81.933 |
|  Direct Proxy | 4G | 500 | 102400 | 30 | 0.12 | 346.16 | 1249.58 | 4793.89 | 8447 | 99.81 | 75.534 |
|  Direct Proxy | 4G | 500 | 102400 | 1000 | 0 | 477.25 | 1045.22 | 156.6 | 1807 | 99.82 | 79.583 |
|  Secure Proxy | 4G | 100 | 1024 | 0 | 0 | 506.08 | 197.57 | 141.48 | 635 | 99.16 | 76.019 |
|  Secure Proxy | 4G | 100 | 1024 | 30 | 0 | 503.71 | 198.38 | 108.28 | 535 | 99.17 | 78.489 |
|  Secure Proxy | 4G | 100 | 1024 | 1000 | 0 | 99.05 | 1008.5 | 2.61 | 1019 | 99.75 | 79.468 |
|  Secure Proxy | 4G | 100 | 10240 | 0 | 0 | 279.7 | 357.16 | 141.28 | 751 | 98.07 | 79.792 |
|  Secure Proxy | 4G | 100 | 10240 | 30 | 0 | 278.48 | 358.97 | 131.72 | 723 | 98.16 | 81.653 |
|  Secure Proxy | 4G | 100 | 10240 | 1000 | 0 | 98.56 | 1013.12 | 5.11 | 1039 | 99.34 | 79.556 |
|  Secure Proxy | 4G | 100 | 102400 | 0 | 0 | 34.03 | 2928.33 | 694.47 | 4799 | 74.91 | 536.103 |
|  Secure Proxy | 4G | 100 | 102400 | 30 | 0 | 34.06 | 2928.67 | 700.82 | 4767 | 74.52 | 545.6 |
|  Secure Proxy | 4G | 100 | 102400 | 1000 | 0 | 33.04 | 3013.85 | 807.84 | 5023 | 71.56 | 627.711 |
|  Secure Proxy | 4G | 200 | 1024 | 0 | 0 | 500.79 | 398.96 | 285.27 | 1271 | 98.88 | 56.936 |
|  Secure Proxy | 4G | 200 | 1024 | 30 | 0 | 498.91 | 400.46 | 247.67 | 1159 | 98.65 | 83.859 |
|  Secure Proxy | 4G | 200 | 1024 | 1000 | 0 | 198.06 | 1008.22 | 3.92 | 1023 | 99.56 | 82.029 |
|  Secure Proxy | 4G | 200 | 10240 | 0 | 0 | 279.34 | 715.86 | 225.87 | 1335 | 97.48 | 78.246 |
|  Secure Proxy | 4G | 200 | 10240 | 30 | 0 | 277.11 | 720.88 | 223.24 | 1319 | 97.14 | 79.373 |
|  Secure Proxy | 4G | 200 | 10240 | 1000 | 0 | 179.57 | 1112.54 | 105.33 | 1455 | 97.5 | 56.418 |
|  Secure Proxy | 4G | 200 | 102400 | 0 | 0 | 28.09 | 7067.65 | 1453.92 | 9791 | 64.95 | 911.411 |
|  Secure Proxy | 4G | 200 | 102400 | 30 | 0 | 28.21 | 7052.27 | 1382.05 | 9599 | 65.29 | 900.278 |
|  Secure Proxy | 4G | 200 | 102400 | 1000 | 0 | 27.22 | 7294.2 | 1462.72 | 10111 | 64.21 | 921.841 |
|  Secure Proxy | 4G | 500 | 1024 | 0 | 0 | 498.77 | 1001.38 | 604.54 | 2815 | 98.09 | 80.667 |
|  Secure Proxy | 4G | 500 | 1024 | 30 | 0 | 496.4 | 1006.35 | 606.99 | 2815 | 97.59 | 56.455 |
|  Secure Proxy | 4G | 500 | 1024 | 1000 | 0 | 436.25 | 1143.61 | 136.19 | 1663 | 98.42 | 62.882 |
|  Secure Proxy | 4G | 500 | 10240 | 0 | 0 | 263.45 | 1894.41 | 461.78 | 3103 | 93.37 | 221.821 |
|  Secure Proxy | 4G | 500 | 10240 | 30 | 0 | 262.08 | 1904.44 | 474.5 | 3183 | 93.38 | 248.514 |
|  Secure Proxy | 4G | 500 | 10240 | 1000 | 0 | 245.39 | 2031.79 | 468.05 | 3503 | 87.85 | 413.319 |
|  Secure Proxy | 4G | 500 | 102400 | 0 | 0 | 14.37 | 33768.96 | 6671.82 | 52223 | 38.32 | 1961.967 |
|  Secure Proxy | 4G | 500 | 102400 | 30 | 0 | 14.73 | 32953.89 | 4838.8 | 44287 | 38.69 | 1962.536 |
|  Secure Proxy | 4G | 500 | 102400 | 1000 | 0 | 13.65 | 35647.67 | 6190.92 | 50943 | 36.4 | 2008.049 |
|  XSLT Enhanced Proxy | 4G | 100 | 1024 | 0 | 0 | 7055.57 | 14.11 | 16.21 | 46 | 99.12 | 78.785 |
|  XSLT Enhanced Proxy | 4G | 100 | 1024 | 30 | 0 | 3187.55 | 31.33 | 1.44 | 37 | 99.57 | 55.942 |
|  XSLT Enhanced Proxy | 4G | 100 | 1024 | 1000 | 0 | 99.7 | 1002.42 | 1.44 | 1007 | 99.84 | 55.652 |
|  XSLT Enhanced Proxy | 4G | 100 | 10240 | 0 | 0 | 1061.11 | 87.83 | 151.39 | 212 | 99.29 | 55.107 |
|  XSLT Enhanced Proxy | 4G | 100 | 10240 | 30 | 0 | 1084.5 | 92.12 | 151.35 | 173 | 99.34 | 63.675 |
|  XSLT Enhanced Proxy | 4G | 100 | 10240 | 1000 | 0 | 99.34 | 1005.68 | 51.44 | 1015 | 99.81 | 75.084 |
|  XSLT Enhanced Proxy | 4G | 100 | 102400 | 0 | 0 | 155.02 | 644.45 | 182.58 | 1095 | 98.56 | 56.374 |
|  XSLT Enhanced Proxy | 4G | 100 | 102400 | 30 | 0 | 152.29 | 655.77 | 170.39 | 1079 | 98.74 | 56.184 |
|  XSLT Enhanced Proxy | 4G | 100 | 102400 | 1000 | 0 | 90.14 | 1107.12 | 78.63 | 1375 | 99.39 | 55.887 |
|  XSLT Enhanced Proxy | 4G | 200 | 1024 | 0 | 0 | 7209.23 | 27.6 | 23.8 | 109 | 98.79 | 79.722 |
|  XSLT Enhanced Proxy | 4G | 200 | 1024 | 30 | 0 | 5258.79 | 37.92 | 5.72 | 61 | 99.21 | 56.398 |
|  XSLT Enhanced Proxy | 4G | 200 | 1024 | 1000 | 0 | 199.15 | 1003.18 | 2.68 | 1015 | 99.82 | 56.643 |
|  XSLT Enhanced Proxy | 4G | 200 | 10240 | 0 | 0 | 1171.18 | 170.69 | 76.78 | 391 | 98.96 | 74.778 |
|  XSLT Enhanced Proxy | 4G | 200 | 10240 | 30 | 0 | 1155.4 | 172.55 | 59.3 | 339 | 99.06 | 77.962 |
|  XSLT Enhanced Proxy | 4G | 200 | 10240 | 1000 | 0 | 198.9 | 1003.69 | 4.72 | 1011 | 99.74 | 56.047 |
|  XSLT Enhanced Proxy | 4G | 200 | 102400 | 0 | 0 | 146.68 | 1361.49 | 291.16 | 2079 | 97.54 | 107.957 |
|  XSLT Enhanced Proxy | 4G | 200 | 102400 | 30 | 0 | 150.83 | 1324.2 | 275.69 | 2007 | 97.7 | 110.279 |
|  XSLT Enhanced Proxy | 4G | 200 | 102400 | 1000 | 0 | 137.12 | 1454.67 | 169.95 | 1967 | 98.45 | 76.332 |
|  XSLT Enhanced Proxy | 4G | 500 | 1024 | 0 | 0 | 7232.24 | 69.01 | 61.39 | 283 | 98.14 | 80.457 |
|  XSLT Enhanced Proxy | 4G | 500 | 1024 | 30 | 0 | 7317.76 | 68.23 | 24.8 | 162 | 98.31 | 56.174 |
|  XSLT Enhanced Proxy | 4G | 500 | 1024 | 1000 | 0 | 498.34 | 1002.2 | 1.44 | 1007 | 99.75 | 79.97 |
|  XSLT Enhanced Proxy | 4G | 500 | 10240 | 0 | 0 | 1258.84 | 397.36 | 147.27 | 787 | 98.23 | 77.808 |
|  XSLT Enhanced Proxy | 4G | 500 | 10240 | 30 | 0 | 1238.38 | 403.54 | 135.25 | 771 | 98.16 | 81.958 |
|  XSLT Enhanced Proxy | 4G | 500 | 10240 | 1000 | 0 | 494.22 | 1009.36 | 16.96 | 1055 | 99.42 | 56.074 |
|  XSLT Enhanced Proxy | 4G | 500 | 102400 | 0 | 0 | 142.51 | 3495.89 | 472.09 | 4639 | 96.29 | 308.524 |
|  XSLT Enhanced Proxy | 4G | 500 | 102400 | 30 | 0 | 140.09 | 3552.83 | 521.19 | 4895 | 96.02 | 341.884 |
|  XSLT Enhanced Proxy | 4G | 500 | 102400 | 1000 | 0 | 146.68 | 3392.83 | 417.42 | 4543 | 96.27 | 291.77 |
|  XSLT Proxy | 4G | 100 | 1024 | 0 | 0 | 3991.96 | 24.99 | 22.1 | 129 | 97.14 | 79.171 |
|  XSLT Proxy | 4G | 100 | 1024 | 30 | 0 | 2761.03 | 36.17 | 5.63 | 66 | 98.1 | 56.08 |
|  XSLT Proxy | 4G | 100 | 1024 | 1000 | 0 | 99.61 | 1002.39 | 2.23 | 1007 | 99.79 | 76.535 |
|  XSLT Proxy | 4G | 100 | 10240 | 0 | 0 | 589.09 | 169.75 | 121.54 | 543 | 98.1 | 55.793 |
|  XSLT Proxy | 4G | 100 | 10240 | 30 | 0 | 589.2 | 169.67 | 90.06 | 449 | 98.11 | 63.006 |
|  XSLT Proxy | 4G | 100 | 10240 | 1000 | 0 | 99.26 | 1006.44 | 2.32 | 1015 | 99.6 | 76.603 |
|  XSLT Proxy | 4G | 100 | 102400 | 0 | 0 | 55.29 | 1805.13 | 301.4 | 2575 | 94.98 | 194.143 |
|  XSLT Proxy | 4G | 100 | 102400 | 30 | 0 | 54.24 | 1840.46 | 298.07 | 2607 | 95.1 | 209.807 |
|  XSLT Proxy | 4G | 100 | 102400 | 1000 | 0 | 51.59 | 1932.97 | 256.16 | 2703 | 96.18 | 144.402 |
|  XSLT Proxy | 4G | 200 | 1024 | 0 | 0 | 3747.25 | 53.23 | 38.02 | 193 | 96.94 | 77.93 |
|  XSLT Proxy | 4G | 200 | 1024 | 30 | 0 | 3626.63 | 55.08 | 18.16 | 127 | 97.15 | 80.126 |
|  XSLT Proxy | 4G | 200 | 1024 | 1000 | 0 | 199.23 | 1002.69 | 3.3 | 1015 | 99.71 | 56.059 |
|  XSLT Proxy | 4G | 200 | 10240 | 0 | 0 | 575.5 | 347.57 | 218.68 | 991 | 97.43 | 56.4 |
|  XSLT Proxy | 4G | 200 | 10240 | 30 | 0 | 545.16 | 366.63 | 193.17 | 927 | 97.55 | 81.21 |
|  XSLT Proxy | 4G | 200 | 10240 | 1000 | 0 | 198.27 | 1007.6 | 5.39 | 1039 | 99.22 | 79.254 |
|  XSLT Proxy | 4G | 200 | 102400 | 0 | 0 | 48.31 | 4115.63 | 744.98 | 6111 | 86.43 | 451.604 |
|  XSLT Proxy | 4G | 200 | 102400 | 30 | 0 | 48.38 | 4115.47 | 715.79 | 5951 | 86.33 | 447.967 |
|  XSLT Proxy | 4G | 200 | 102400 | 1000 | 0 | 49.08 | 4056.83 | 674.58 | 5919 | 87.1 | 441.739 |
|  XSLT Proxy | 4G | 500 | 1024 | 0 | 0 | 3904.62 | 127.97 | 69.16 | 349 | 96.11 | 56.096 |
|  XSLT Proxy | 4G | 500 | 1024 | 30 | 0 | 3856.98 | 129.5 | 53.82 | 303 | 96.21 | 81.652 |
|  XSLT Proxy | 4G | 500 | 1024 | 1000 | 0 | 498.09 | 1002.62 | 3.55 | 1015 | 99.44 | 61.242 |
|  XSLT Proxy | 4G | 500 | 10240 | 0 | 0 | 560.07 | 892.18 | 422.26 | 2143 | 95.33 | 80.228 |
|  XSLT Proxy | 4G | 500 | 10240 | 30 | 0 | 530.25 | 942.2 | 417.57 | 2127 | 96 | 56.218 |
|  XSLT Proxy | 4G | 500 | 10240 | 1000 | 0 | 476.86 | 1047.1 | 50.68 | 1255 | 97.44 | 76.359 |
|  XSLT Proxy | 4G | 500 | 102400 | 0 | 0 | 37.19 | 13269.08 | 2095.45 | 17791 | 70.55 | 963.082 |
|  XSLT Proxy | 4G | 500 | 102400 | 30 | 0 | 37.83 | 13070.09 | 1998.05 | 17663 | 69.03 | 954.027 |
|  XSLT Proxy | 4G | 500 | 102400 | 1000 | 0 | 37.79 | 13058.35 | 2038.96 | 17791 | 69.97 | 963.285 |
