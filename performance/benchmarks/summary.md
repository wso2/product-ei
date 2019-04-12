# WSO2 Enterprise Micro Integrator Performance Test Results

During each release, we execute various automated performance test scenarios and publish the results.

| Test Scenarios | Description |
| --- | --- |
| DirectProxy |  |
| CBRProxy |  |
| XSLTProxy |  |
| CBRSOAPHeaderProxy |  |
| CBRTransportHeaderProxy |  |
| XSLTEnhancedProxy |  |

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
| Concurrent Users | The number of users accessing the application at the same time. | 100, 200, 500, 1000 |
| Message Size (Bytes) | The request payload size in Bytes. | 500, 1024, 10240, 102400 |
| Back-end Delay (ms) | The delay added by the Back-end service. | 0, 30, 100, 500, 1000 |

The duration of each test is **60 seconds**. The warm-up period is **30 seconds**.
The measurement results are collected after the warm-up period.

The performance tests were executed on 4 AWS CloudFormation stacks.


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
| Operating System | Distribution | Kernel | Linux ip-10-0-1-114 4.15.0-1035-aws #37-Ubuntu SMP Mon Mar 18 16:15:14 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux |
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
| Operating System | Distribution | Kernel | Linux ip-10-0-1-86 4.15.0-1035-aws #37-Ubuntu SMP Mon Mar 18 16:15:14 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux |
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
| Operating System | Distribution | Kernel | Linux ip-10-0-1-50 4.15.0-1035-aws #37-Ubuntu SMP Mon Mar 18 16:15:14 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux |
System information for WSO2 Enterprise Micro Integrator in 4th AWS CloudFormation stack.

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
| Operating System | Distribution | Kernel | Linux ip-10-0-1-247 4.15.0-1035-aws #37-Ubuntu SMP Mon Mar 18 16:15:14 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux |


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
|  CBRProxy | 512M | 100 | 500 | 0 | 0 | 3533.63 | 21.32 | 24.37 | 105 | 96.8 | 28.097 |
|  CBRProxy | 512M | 100 | 500 | 30 | 0 | 2338.17 | 32.23 | 2.85 | 46 | 98.23 | 28.081 |
|  CBRProxy | 512M | 100 | 500 | 100 | 0 | 742.89 | 101.38 | 1.71 | 113 | 99.14 | 28.061 |
|  CBRProxy | 512M | 100 | 500 | 500 | 0 | 149.04 | 501.74 | 1.13 | 503 | 99.52 | 28.148 |
|  CBRProxy | 512M | 100 | 500 | 1000 | 0 | 73.92 | 1002.23 | 3.2 | 1007 | 99.58 | 28.092 |
|  CBRProxy | 512M | 100 | 1024 | 0 | 0 | 2880.23 | 26.16 | 27.66 | 118 | 96.81 | 28.061 |
|  CBRProxy | 512M | 100 | 1024 | 30 | 0 | 2317.84 | 32.52 | 2.89 | 47 | 98.03 | 28.078 |
|  CBRProxy | 512M | 100 | 1024 | 100 | 0 | 742.03 | 101.47 | 1.83 | 113 | 99.08 | 28.082 |
|  CBRProxy | 512M | 100 | 1024 | 500 | 0 | 149.04 | 501.87 | 1.18 | 503 | 99.52 | 28.024 |
|  CBRProxy | 512M | 100 | 1024 | 1000 | 0 | 73.81 | 1002.05 | 0.72 | 1003 | 99.55 | 28.131 |
|  CBRProxy | 512M | 100 | 10240 | 0 | 0 | 759.09 | 99.52 | 66.95 | 311 | 95.14 | 28.118 |
|  CBRProxy | 512M | 100 | 10240 | 30 | 0 | 838.39 | 89.97 | 34.61 | 202 | 95.95 | 28.146 |
|  CBRProxy | 512M | 100 | 10240 | 100 | 0 | 690.1 | 109.28 | 10.53 | 146 | 97.18 | 28.062 |
|  CBRProxy | 512M | 100 | 10240 | 500 | 0 | 148.49 | 503.73 | 3.17 | 527 | 99.11 | 28.062 |
|  CBRProxy | 512M | 100 | 10240 | 1000 | 0 | 73.79 | 1002.8 | 4.25 | 1031 | 99.36 | 28.155 |
|  CBRProxy | 512M | 100 | 102400 | 0 | 0 | 63.73 | 1189.42 | 478.21 | 2575 | 76.22 | 149.096 |
|  CBRProxy | 512M | 100 | 102400 | 30 | 0 | 65.04 | 1161.34 | 458.13 | 2287 | 77.7 | 141.8 |
|  CBRProxy | 512M | 100 | 102400 | 100 | 0 | 63.85 | 1177.02 | 450.37 | 2447 | 77.86 | 152.004 |
|  CBRProxy | 512M | 100 | 102400 | 500 | 0 | 65.95 | 1130.54 | 403.63 | 2399 | 80.91 | 141.864 |
|  CBRProxy | 512M | 100 | 102400 | 1000 | 0 | 64.5 | 1147.86 | 475.37 | 2023 | 85.4 | 141.314 |
|  CBRProxy | 512M | 200 | 500 | 0 | 0 | 3087.69 | 48.69 | 35.13 | 162 | 96.07 | 28.068 |
|  CBRProxy | 512M | 200 | 500 | 30 | 0 | 3623.46 | 41.43 | 11.26 | 79 | 96.73 | 28.057 |
|  CBRProxy | 512M | 200 | 500 | 100 | 0 | 1472.16 | 102.08 | 2.92 | 118 | 98.45 | 28.144 |
|  CBRProxy | 512M | 200 | 500 | 500 | 0 | 295.38 | 501.82 | 2.22 | 515 | 99.39 | 28.133 |
|  CBRProxy | 512M | 200 | 500 | 1000 | 0 | 147.31 | 1002.13 | 1.48 | 1007 | 99.49 | 28.148 |
|  CBRProxy | 512M | 200 | 1024 | 0 | 0 | 2644.3 | 56.92 | 38.72 | 183 | 95.71 | 28.164 |
|  CBRProxy | 512M | 200 | 1024 | 30 | 0 | 3296.89 | 45.58 | 14.07 | 93 | 96.53 | 28.093 |
|  CBRProxy | 512M | 200 | 1024 | 100 | 0 | 1465.19 | 102.47 | 3.47 | 121 | 98.38 | 28.198 |
|  CBRProxy | 512M | 200 | 1024 | 500 | 0 | 297.29 | 501.49 | 1.41 | 503 | 99.34 | 28.051 |
|  CBRProxy | 512M | 200 | 1024 | 1000 | 0 | 147.09 | 1002.12 | 1.41 | 1003 | 99.47 | 28.099 |
|  CBRProxy | 512M | 200 | 10240 | 0 | 0 | 698.42 | 216.26 | 134.15 | 595 | 90.47 | 83.795 |
|  CBRProxy | 512M | 200 | 10240 | 30 | 0 | 703.41 | 213.74 | 105.94 | 543 | 90.94 | 73.38 |
|  CBRProxy | 512M | 200 | 10240 | 100 | 0 | 783.78 | 191.91 | 74.48 | 451 | 90.98 | 82.414 |
|  CBRProxy | 512M | 200 | 10240 | 500 | 0 | 290.42 | 512.5 | 31.9 | 599 | 96.86 | 54.625 |
|  CBRProxy | 512M | 200 | 10240 | 1000 | 0 | 147.44 | 997.71 | 76.26 | 1063 | 98.33 | 28.123 |
|  CBRProxy | 512M | 200 | 102400 | 0 | 0 | 48.45 | 3062.44 | 1448.28 | 6655 | 69 | 236.715 |
|  CBRProxy | 512M | 200 | 102400 | 30 | 0 | 47.41 | 3146.73 | 1342.26 | 6495 | 69.31 | 240.151 |
|  CBRProxy | 512M | 200 | 102400 | 100 | 0 | 46.12 | 3241.54 | 1454.75 | 7263 | 68.34 | 248.255 |
|  CBRProxy | 512M | 200 | 102400 | 500 | 0 | 50.04 | 2958.04 | 1168.62 | 5919 | 70.35 | 233.093 |
|  CBRProxy | 512M | 200 | 102400 | 1000 | 0 | 48.92 | 3002.55 | 1162.67 | 6527 | 73.02 | 241.33 |
|  CBRProxy | 512M | 500 | 500 | 0 | 0 | 2846.8 | 131.85 | 80 | 393 | 91.29 | 95.398 |
|  CBRProxy | 512M | 500 | 500 | 30 | 0 | 3442.97 | 108.97 | 49.18 | 305 | 91.17 | 92.835 |
|  CBRProxy | 512M | 500 | 500 | 100 | 0 | 3039.3 | 123.47 | 34.97 | 313 | 93.33 | 92.674 |
|  CBRProxy | 512M | 500 | 500 | 500 | 0 | 735.63 | 505.27 | 16.89 | 571 | 98.08 | 60.975 |
|  CBRProxy | 512M | 500 | 500 | 1000 | 0 | 366.65 | 1002.74 | 5.02 | 1039 | 98.87 | 28.097 |
|  CBRProxy | 512M | 500 | 1024 | 0 | 0 | 2576.27 | 145.59 | 85.42 | 415 | 90.88 | 95.456 |
|  CBRProxy | 512M | 500 | 1024 | 30 | 0 | 2994.21 | 125.38 | 59.95 | 353 | 90.13 | 97.615 |
|  CBRProxy | 512M | 500 | 1024 | 100 | 0 | 2809.81 | 132.9 | 43.31 | 339 | 92.2 | 95.465 |
|  CBRProxy | 512M | 500 | 1024 | 500 | 0 | 729.15 | 510.3 | 25.26 | 603 | 97.74 | 61.601 |
|  CBRProxy | 512M | 500 | 1024 | 1000 | 0 | 365.48 | 1002.9 | 6.34 | 1039 | 98.76 | 28.16 |
|  CBRProxy | 512M | 500 | 10240 | 0 | 0 | 606.44 | 615.39 | 310.23 | 1423 | 82.46 | 128.217 |
|  CBRProxy | 512M | 500 | 10240 | 30 | 0 | 620.68 | 602.99 | 279.29 | 1375 | 82.49 | 121.994 |
|  CBRProxy | 512M | 500 | 10240 | 100 | 0 | 631.85 | 592.44 | 279.54 | 1479 | 82.79 | 124.775 |
|  CBRProxy | 512M | 500 | 10240 | 500 | 0 | 576.63 | 644.82 | 130 | 1095 | 86.78 | 130.2 |
|  CBRProxy | 512M | 500 | 10240 | 1000 | 0 | 352.08 | 1048.19 | 85.57 | 1327 | 92.3 | 117.509 |
|  CBRProxy | 512M | 500 | 102400 | 0 | 74.72 | 2.92 | 101722.42 | 50284.13 | 174079 | 19.07 | 462.539 |
|  CBRProxy | 512M | 500 | 102400 | 30 | 93.91 | 2.5 | 115282.03 | 28778.27 | 149503 | 17.13 | 471.049 |
|  CBRProxy | 512M | 500 | 102400 | 100 | 100 | 2.39 | 120121.92 | 1094.4 | 120319 | 14.33 | 473.853 |
|  CBRProxy | 512M | 500 | 102400 | 500 | 99.75 | 1.83 | 126164.76 | 17219.68 | 216063 | 16.92 | 471.232 |
|  CBRProxy | 512M | 500 | 102400 | 1000 | 93.8 | 2 | 124926.48 | 33117.61 | 225279 | 14.07 | 476.418 |
|  CBRProxy | 512M | 1000 | 500 | 0 | 0 | 2521.72 | 296.89 | 150.11 | 719 | 89.44 | 127.732 |
|  CBRProxy | 512M | 1000 | 500 | 30 | 0 | 2847.73 | 263.49 | 118.55 | 607 | 88.23 | 131.664 |
|  CBRProxy | 512M | 1000 | 500 | 100 | 0 | 3085.49 | 242.71 | 101.78 | 559 | 88.05 | 127.409 |
|  CBRProxy | 512M | 1000 | 500 | 500 | 0 | 1363.86 | 548.23 | 68.09 | 791 | 95.53 | 115.748 |
|  CBRProxy | 512M | 1000 | 500 | 1000 | 0 | 717.32 | 1027.62 | 58.56 | 1287 | 97.21 | 109.542 |
|  CBRProxy | 512M | 1000 | 1024 | 0 | 0 | 2253.21 | 333.41 | 166.61 | 787 | 88.62 | 125.996 |
|  CBRProxy | 512M | 1000 | 1024 | 30 | 0 | 2370.98 | 315.92 | 146.6 | 779 | 87.15 | 130.678 |
|  CBRProxy | 512M | 1000 | 1024 | 100 | 0 | 2398.85 | 312.46 | 117.02 | 647 | 87.66 | 129.952 |
|  CBRProxy | 512M | 1000 | 1024 | 500 | 0 | 1334.06 | 555.44 | 77.41 | 843 | 94.1 | 127.806 |
|  CBRProxy | 512M | 1000 | 1024 | 1000 | 0 | 710.92 | 1034.56 | 64.29 | 1263 | 96.64 | 118.426 |
|  CBRProxy | 512M | 1000 | 10240 | 0 | 0 | 518.76 | 1421.39 | 670.68 | 3375 | 82.03 | 187.959 |
|  CBRProxy | 512M | 1000 | 10240 | 30 | 0 | 533.53 | 1402.67 | 643.26 | 3215 | 84.68 | 185.469 |
|  CBRProxy | 512M | 1000 | 10240 | 100 | 0 | 538.32 | 1396.05 | 614.39 | 3599 | 85.12 | 178.643 |
|  CBRProxy | 512M | 1000 | 10240 | 500 | 0 | 493.73 | 1511.15 | 673.5 | 4127 | 84.43 | 202.964 |
|  CBRProxy | 512M | 1000 | 10240 | 1000 | 0 | 494.83 | 1485.76 | 370.26 | 2607 | 85.76 | 204.193 |
|  CBRProxy | 512M | 1000 | 102400 | 0 | 100 | 2.74 | 120111.45 | 303.93 | 121343 | 24.08 | 478.012 |
|  CBRProxy | 512M | 1000 | 102400 | 30 | 100 | 2.73 | 104628.89 | 20988.34 | 123391 | 22.32 | 478.379 |
|  CBRProxy | 512M | 1000 | 102400 | 100 | 100 | 2.62 | 121834.67 | 4244.85 | 136191 | 23.31 | 478.217 |
|  CBRProxy | 512M | 1000 | 102400 | 500 | 100 | 2.66 | 120291.49 | 1367.29 | 123391 | 23.41 | 480.259 |
|  CBRProxy | 512M | 1000 | 102400 | 1000 | 100 | 2.67 | 120064 | 72.41 | 120319 | 23.79 | 479.195 |
|  CBRSOAPHeaderProxy | 512M | 100 | 500 | 0 | 0 | 3289.13 | 22.92 | 25.82 | 108 | 96.89 | 28.043 |
|  CBRSOAPHeaderProxy | 512M | 100 | 500 | 30 | 0 | 2346.27 | 32.14 | 2.88 | 45 | 98.29 | 24.948 |
|  CBRSOAPHeaderProxy | 512M | 100 | 500 | 100 | 0 | 743.45 | 101.3 | 1.5 | 109 | 99.19 | 28.107 |
|  CBRSOAPHeaderProxy | 512M | 100 | 500 | 500 | 0 | 148.81 | 501.81 | 1.16 | 503 | 99.53 | 28.063 |
|  CBRSOAPHeaderProxy | 512M | 100 | 500 | 1000 | 0 | 73.97 | 1002.06 | 0.78 | 1003 | 99.65 | 28.146 |
|  CBRSOAPHeaderProxy | 512M | 100 | 1024 | 0 | 0 | 3102.5 | 24.27 | 25.63 | 104 | 96.72 | 28.108 |
|  CBRSOAPHeaderProxy | 512M | 100 | 1024 | 30 | 0 | 2336.99 | 32.27 | 2.82 | 46 | 98.23 | 28.145 |
|  CBRSOAPHeaderProxy | 512M | 100 | 1024 | 100 | 0 | 740.63 | 101.8 | 3.91 | 116 | 99.05 | 28.112 |
|  CBRSOAPHeaderProxy | 512M | 100 | 1024 | 500 | 0 | 148.82 | 501.78 | 1.19 | 503 | 99.54 | 28.163 |
|  CBRSOAPHeaderProxy | 512M | 100 | 1024 | 1000 | 0 | 73.97 | 1002.07 | 0.87 | 1003 | 99.5 | 28.067 |
|  CBRSOAPHeaderProxy | 512M | 100 | 10240 | 0 | 0 | 1043.73 | 72.27 | 48.91 | 216 | 96.91 | 28.125 |
|  CBRSOAPHeaderProxy | 512M | 100 | 10240 | 30 | 0 | 1240.47 | 60.78 | 21 | 116 | 97.32 | 28.134 |
|  CBRSOAPHeaderProxy | 512M | 100 | 10240 | 100 | 0 | 732.46 | 102.9 | 7.14 | 122 | 98.53 | 28.082 |
|  CBRSOAPHeaderProxy | 512M | 100 | 10240 | 500 | 0 | 150.89 | 496.08 | 56.59 | 507 | 99.34 | 28.104 |
|  CBRSOAPHeaderProxy | 512M | 100 | 10240 | 1000 | 0 | 73.84 | 1002.2 | 1.63 | 1007 | 99.47 | 28.182 |
|  CBRSOAPHeaderProxy | 512M | 100 | 102400 | 0 | 0 | 120.42 | 626.93 | 241.04 | 1255 | 87.56 | 135.723 |
|  CBRSOAPHeaderProxy | 512M | 100 | 102400 | 30 | 0 | 113.76 | 659.92 | 236.51 | 1295 | 88.11 | 135.826 |
|  CBRSOAPHeaderProxy | 512M | 100 | 102400 | 100 | 0 | 115.72 | 650.64 | 210.26 | 1167 | 88.01 | 139.202 |
|  CBRSOAPHeaderProxy | 512M | 100 | 102400 | 500 | 0 | 111.14 | 669.79 | 213.79 | 1079 | 90.03 | 135.797 |
|  CBRSOAPHeaderProxy | 512M | 100 | 102400 | 1000 | 0 | 78.76 | 935.17 | 414 | 1399 | 93.41 | 129.366 |
|  CBRSOAPHeaderProxy | 512M | 200 | 500 | 0 | 0 | 3369.42 | 44.55 | 33.22 | 139 | 96.23 | 28.125 |
|  CBRSOAPHeaderProxy | 512M | 200 | 500 | 30 | 0 | 3809.66 | 39.43 | 9.71 | 71 | 97.18 | 28.094 |
|  CBRSOAPHeaderProxy | 512M | 200 | 500 | 100 | 0 | 1471.29 | 102.1 | 3.07 | 119 | 98.78 | 28.057 |
|  CBRSOAPHeaderProxy | 512M | 200 | 500 | 500 | 0 | 297.22 | 501.38 | 1.43 | 503 | 99.42 | 28.093 |
|  CBRSOAPHeaderProxy | 512M | 200 | 500 | 1000 | 0 | 146.93 | 1002.06 | 0.89 | 1003 | 99.52 | 28.071 |
|  CBRSOAPHeaderProxy | 512M | 200 | 1024 | 0 | 0 | 3429.66 | 43.8 | 33.69 | 162 | 95.96 | 28.072 |
|  CBRSOAPHeaderProxy | 512M | 200 | 1024 | 30 | 0 | 3549.46 | 42.35 | 11.61 | 79 | 96.67 | 28.115 |
|  CBRSOAPHeaderProxy | 512M | 200 | 1024 | 100 | 0 | 1472.12 | 102.07 | 3.14 | 120 | 98.54 | 28.167 |
|  CBRSOAPHeaderProxy | 512M | 200 | 1024 | 500 | 0 | 297.33 | 501.37 | 2.56 | 503 | 99.35 | 28.108 |
|  CBRSOAPHeaderProxy | 512M | 200 | 1024 | 1000 | 0 | 147.3 | 1002.14 | 1.51 | 1007 | 99.5 | 28.083 |
|  CBRSOAPHeaderProxy | 512M | 200 | 10240 | 0 | 0 | 1035.8 | 144.93 | 87.71 | 455 | 93.74 | 76.013 |
|  CBRSOAPHeaderProxy | 512M | 200 | 10240 | 30 | 0 | 1175.26 | 127.83 | 54.32 | 317 | 93.52 | 72.651 |
|  CBRSOAPHeaderProxy | 512M | 200 | 10240 | 100 | 0 | 1105.81 | 135.75 | 37.86 | 323 | 94.64 | 73.94 |
|  CBRSOAPHeaderProxy | 512M | 200 | 10240 | 500 | 0 | 297.95 | 500.72 | 36.69 | 551 | 98.62 | 24.945 |
|  CBRSOAPHeaderProxy | 512M | 200 | 10240 | 1000 | 0 | 146.99 | 1002.54 | 3.93 | 1023 | 99.1 | 28.084 |
|  CBRSOAPHeaderProxy | 512M | 200 | 102400 | 0 | 0 | 100.09 | 1491.71 | 579 | 2991 | 81.17 | 216.976 |
|  CBRSOAPHeaderProxy | 512M | 200 | 102400 | 30 | 0 | 101.47 | 1471.18 | 520.66 | 2911 | 81.37 | 211.79 |
|  CBRSOAPHeaderProxy | 512M | 200 | 102400 | 100 | 0 | 100.5 | 1489.82 | 578.92 | 3007 | 80.73 | 220.741 |
|  CBRSOAPHeaderProxy | 512M | 200 | 102400 | 500 | 0 | 100 | 1489.87 | 489.35 | 2751 | 81.98 | 218.427 |
|  CBRSOAPHeaderProxy | 512M | 200 | 102400 | 1000 | 0 | 97.27 | 1508.46 | 453.65 | 2399 | 84.45 | 216.288 |
|  CBRSOAPHeaderProxy | 512M | 500 | 500 | 0 | 0 | 3009.22 | 124.67 | 73.28 | 377 | 90.99 | 93.189 |
|  CBRSOAPHeaderProxy | 512M | 500 | 500 | 30 | 0 | 3801.65 | 98.72 | 50.2 | 309 | 92.18 | 96.884 |
|  CBRSOAPHeaderProxy | 512M | 500 | 500 | 100 | 0 | 3072.3 | 121.78 | 33.91 | 303 | 93.5 | 88.087 |
|  CBRSOAPHeaderProxy | 512M | 500 | 500 | 500 | 0 | 734.87 | 504.7 | 15.06 | 567 | 98.19 | 61.411 |
|  CBRSOAPHeaderProxy | 512M | 500 | 500 | 1000 | 0 | 367 | 1002.54 | 4.56 | 1023 | 99 | 28.161 |
|  CBRSOAPHeaderProxy | 512M | 500 | 1024 | 0 | 0 | 2937.65 | 127.99 | 73.75 | 369 | 90.7 | 97.679 |
|  CBRSOAPHeaderProxy | 512M | 500 | 1024 | 30 | 0 | 3445.42 | 108.88 | 54.5 | 311 | 90.31 | 96.505 |
|  CBRSOAPHeaderProxy | 512M | 500 | 1024 | 100 | 0 | 2956.88 | 126.74 | 37.52 | 317 | 93 | 95.448 |
|  CBRSOAPHeaderProxy | 512M | 500 | 1024 | 500 | 0 | 733.17 | 507.49 | 21.25 | 619 | 98.03 | 61.89 |
|  CBRSOAPHeaderProxy | 512M | 500 | 1024 | 1000 | 0 | 367.2 | 1002.63 | 4.84 | 1031 | 98.92 | 28.065 |
|  CBRSOAPHeaderProxy | 512M | 500 | 10240 | 0 | 0 | 943.74 | 398.35 | 212.23 | 1023 | 86.51 | 129.605 |
|  CBRSOAPHeaderProxy | 512M | 500 | 10240 | 30 | 0 | 962.4 | 390.85 | 197.86 | 1023 | 87.1 | 125.572 |
|  CBRSOAPHeaderProxy | 512M | 500 | 10240 | 100 | 0 | 957.23 | 391.26 | 170.35 | 967 | 87.41 | 127.1 |
|  CBRSOAPHeaderProxy | 512M | 500 | 10240 | 500 | 0 | 652.31 | 572.61 | 86.43 | 867 | 92.7 | 123.688 |
|  CBRSOAPHeaderProxy | 512M | 500 | 10240 | 1000 | 0 | 359.43 | 1016.36 | 71.35 | 1239 | 95.79 | 112.268 |
|  CBRSOAPHeaderProxy | 512M | 500 | 102400 | 0 | 28.46 | 5.87 | 40059.39 | 55301.38 | 153599 | 16.87 | 475.894 |
|  CBRSOAPHeaderProxy | 512M | 500 | 102400 | 30 | 50.2 | 4.22 | 63272.83 | 59546.09 | 152575 | 17.11 | 477.787 |
|  CBRSOAPHeaderProxy | 512M | 500 | 102400 | 100 | 29.69 | 5.49 | 43662.19 | 60955.68 | 248831 | 17.48 | 476.526 |
|  CBRSOAPHeaderProxy | 512M | 500 | 102400 | 500 | 39.95 | 4.87 | 56023.17 | 63042.72 | 232447 | 17.56 | 477.931 |
|  CBRSOAPHeaderProxy | 512M | 500 | 102400 | 1000 | 43.41 | 4.26 | 50052.98 | 56042.4 | 189439 | 35.5 | 473.679 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 500 | 0 | 0 | 2712.98 | 276.55 | 137.61 | 659 | 91.79 | 128.358 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 500 | 30 | 0 | 2770.46 | 271.11 | 127.23 | 631 | 88.59 | 129.173 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 500 | 100 | 0 | 3078.15 | 243.92 | 97.09 | 523 | 88.1 | 131.457 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 500 | 500 | 0 | 1370.9 | 542.78 | 60.37 | 771 | 95.36 | 119.444 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 500 | 1000 | 0 | 720.86 | 1015.15 | 39.2 | 1239 | 97.27 | 110.754 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 1024 | 0 | 0 | 2464 | 304.18 | 158.05 | 771 | 88.78 | 128.82 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 1024 | 30 | 0 | 2434.42 | 308.33 | 133.36 | 695 | 89.22 | 132.732 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 1024 | 100 | 0 | 2922.8 | 255.67 | 107.3 | 547 | 87.96 | 130.027 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 1024 | 500 | 0 | 1356.95 | 552.46 | 70.91 | 787 | 94.85 | 125.144 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 1024 | 1000 | 0 | 714.22 | 1031.1 | 57.92 | 1263 | 97.08 | 111.121 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 10240 | 0 | 0 | 790.54 | 933.07 | 449.61 | 2399 | 84.48 | 196.5 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 10240 | 30 | 0 | 814.93 | 921.63 | 448.52 | 2335 | 85.15 | 191.279 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 10240 | 100 | 0 | 816.47 | 910.82 | 412.77 | 2191 | 84.99 | 187.214 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 10240 | 500 | 0 | 785.19 | 944.68 | 309.11 | 1695 | 86.75 | 199.034 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 10240 | 1000 | 0 | 642.49 | 1142.36 | 145.8 | 1543 | 89.88 | 201.629 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 102400 | 0 | 100 | 2.27 | 114680.84 | 21941.32 | 185343 | 20.07 | 484.827 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 102400 | 30 | 100 | 2.29 | 111497.76 | 26094.87 | 185343 | 38.74 | 478.496 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 102400 | 100 | 100 | 2.72 | 120064 | 0 | 120319 | 25.54 | 481.207 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 102400 | 500 | 100 | 3.53 | 112760.75 | 18934.76 | 123391 | 24.09 | 482.024 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 102400 | 1000 | 100 | 4.93 | 69508.01 | 36098.49 | 118783 | 32.8 | 473.284 |
|  CBRTransportHeaderProxy | 512M | 100 | 500 | 0 | 0 | 5290.37 | 14.21 | 19.28 | 88 | 96.54 | 28.14 |
|  CBRTransportHeaderProxy | 512M | 100 | 500 | 30 | 0 | 2393.05 | 31.51 | 2.93 | 45 | 98.61 | 28.059 |
|  CBRTransportHeaderProxy | 512M | 100 | 500 | 100 | 0 | 749.54 | 100.43 | 8.2 | 109 | 99.25 | 28.131 |
|  CBRTransportHeaderProxy | 512M | 100 | 500 | 500 | 0 | 148.9 | 501.37 | 1 | 503 | 99.52 | 28.013 |
|  CBRTransportHeaderProxy | 512M | 100 | 500 | 1000 | 0 | 73.99 | 1002.47 | 4.6 | 1015 | 99.54 | 28.088 |
|  CBRTransportHeaderProxy | 512M | 100 | 1024 | 0 | 0 | 4928.67 | 15.25 | 20.48 | 99 | 96.67 | 28.109 |
|  CBRTransportHeaderProxy | 512M | 100 | 1024 | 30 | 0 | 2391.02 | 31.53 | 2.81 | 45 | 98.61 | 28.139 |
|  CBRTransportHeaderProxy | 512M | 100 | 1024 | 100 | 0 | 744.96 | 101.01 | 3.84 | 105 | 99.31 | 28.153 |
|  CBRTransportHeaderProxy | 512M | 100 | 1024 | 500 | 0 | 149.18 | 501.43 | 1.04 | 503 | 99.53 | 28.056 |
|  CBRTransportHeaderProxy | 512M | 100 | 1024 | 1000 | 0 | 74.09 | 1002.05 | 0.84 | 1003 | 99.57 | 28.094 |
|  CBRTransportHeaderProxy | 512M | 100 | 10240 | 0 | 0 | 3503.43 | 21.47 | 22.22 | 88 | 97.52 | 28.199 |
|  CBRTransportHeaderProxy | 512M | 100 | 10240 | 30 | 0 | 2399.98 | 31.41 | 4.28 | 45 | 98.55 | 28.109 |
|  CBRTransportHeaderProxy | 512M | 100 | 10240 | 100 | 0 | 751.21 | 100.26 | 9 | 105 | 99.25 | 28.184 |
|  CBRTransportHeaderProxy | 512M | 100 | 10240 | 500 | 0 | 150.78 | 495.11 | 52.66 | 503 | 99.48 | 28.084 |
|  CBRTransportHeaderProxy | 512M | 100 | 10240 | 1000 | 0 | 74.44 | 996.08 | 80.52 | 1031 | 99.49 | 28.029 |
|  CBRTransportHeaderProxy | 512M | 100 | 102400 | 0 | 0 | 924.84 | 81.48 | 33.93 | 175 | 99.03 | 28.057 |
|  CBRTransportHeaderProxy | 512M | 100 | 102400 | 30 | 0 | 1085.97 | 69.24 | 24.34 | 112 | 99 | 28.072 |
|  CBRTransportHeaderProxy | 512M | 100 | 102400 | 100 | 0 | 909.91 | 82.61 | 42.8 | 164 | 99.07 | 28.17 |
|  CBRTransportHeaderProxy | 512M | 100 | 102400 | 500 | 0 | 154.9 | 482.88 | 95.33 | 505 | 99.52 | 28.095 |
|  CBRTransportHeaderProxy | 512M | 100 | 102400 | 1000 | 0 | 74.2 | 995.99 | 77.01 | 1007 | 99.59 | 28.037 |
|  CBRTransportHeaderProxy | 512M | 200 | 500 | 0 | 0 | 4570.82 | 32.87 | 29.58 | 117 | 96.22 | 28.148 |
|  CBRTransportHeaderProxy | 512M | 200 | 500 | 30 | 0 | 4349.79 | 34.54 | 5.6 | 57 | 97.78 | 28.02 |
|  CBRTransportHeaderProxy | 512M | 200 | 500 | 100 | 0 | 1493.1 | 100.52 | 7.76 | 117 | 98.84 | 28.036 |
|  CBRTransportHeaderProxy | 512M | 200 | 500 | 500 | 0 | 297.33 | 501.18 | 1.12 | 503 | 99.49 | 28.165 |
|  CBRTransportHeaderProxy | 512M | 200 | 500 | 1000 | 0 | 147.41 | 1002.05 | 0.77 | 1003 | 99.6 | 28.083 |
|  CBRTransportHeaderProxy | 512M | 200 | 1024 | 0 | 0 | 4893.65 | 30.71 | 28.8 | 116 | 96.29 | 28.025 |
|  CBRTransportHeaderProxy | 512M | 200 | 1024 | 30 | 0 | 4376.12 | 34.34 | 5.38 | 56 | 97.45 | 28.215 |
|  CBRTransportHeaderProxy | 512M | 200 | 1024 | 100 | 0 | 1480.57 | 101.42 | 3.18 | 117 | 98.85 | 28.132 |
|  CBRTransportHeaderProxy | 512M | 200 | 1024 | 500 | 0 | 297.54 | 501.22 | 1.21 | 503 | 99.35 | 28.065 |
|  CBRTransportHeaderProxy | 512M | 200 | 1024 | 1000 | 0 | 147.29 | 1002.06 | 0.92 | 1003 | 99.61 | 28.085 |
|  CBRTransportHeaderProxy | 512M | 200 | 10240 | 0 | 0 | 3150.62 | 47.64 | 31.75 | 127 | 97.28 | 28.146 |
|  CBRTransportHeaderProxy | 512M | 200 | 10240 | 30 | 0 | 3577.49 | 41.94 | 12.51 | 79 | 97.7 | 28.098 |
|  CBRTransportHeaderProxy | 512M | 200 | 10240 | 100 | 0 | 1513.8 | 99.22 | 14.58 | 117 | 98.84 | 28.148 |
|  CBRTransportHeaderProxy | 512M | 200 | 10240 | 500 | 0 | 297.4 | 501.33 | 5.46 | 503 | 99.44 | 28.083 |
|  CBRTransportHeaderProxy | 512M | 200 | 10240 | 1000 | 0 | 147.47 | 1000.54 | 38.5 | 1003 | 99.54 | 28.114 |
|  CBRTransportHeaderProxy | 512M | 200 | 102400 | 0 | 0 | 908.67 | 165.27 | 54.51 | 303 | 98.73 | 28.155 |
|  CBRTransportHeaderProxy | 512M | 200 | 102400 | 30 | 0 | 956.19 | 157.11 | 50.5 | 293 | 98.7 | 28.085 |
|  CBRTransportHeaderProxy | 512M | 200 | 102400 | 100 | 0 | 908.01 | 165.35 | 52.43 | 293 | 98.86 | 28.109 |
|  CBRTransportHeaderProxy | 512M | 200 | 102400 | 500 | 0 | 314.78 | 474.01 | 112.16 | 507 | 99.46 | 28.1 |
|  CBRTransportHeaderProxy | 512M | 200 | 102400 | 1000 | 0 | 148.31 | 994.24 | 86.22 | 1003 | 99.56 | 28.086 |
|  CBRTransportHeaderProxy | 512M | 500 | 500 | 0 | 0 | 4819.93 | 77.83 | 46.61 | 220 | 94.22 | 28.105 |
|  CBRTransportHeaderProxy | 512M | 500 | 500 | 30 | 0 | 5350.61 | 69.93 | 25.49 | 146 | 95 | 28.046 |
|  CBRTransportHeaderProxy | 512M | 500 | 500 | 100 | 0 | 3511.64 | 106.66 | 10.37 | 142 | 97.2 | 28.098 |
|  CBRTransportHeaderProxy | 512M | 500 | 500 | 500 | 0 | 741.68 | 501.43 | 7.13 | 519 | 99.02 | 28.154 |
|  CBRTransportHeaderProxy | 512M | 500 | 500 | 1000 | 0 | 367.54 | 1002.16 | 1.8 | 1007 | 99.33 | 28.125 |
|  CBRTransportHeaderProxy | 512M | 500 | 1024 | 0 | 0 | 4547.27 | 82.53 | 48.54 | 244 | 94.82 | 28.148 |
|  CBRTransportHeaderProxy | 512M | 500 | 1024 | 30 | 0 | 5267.79 | 71.16 | 26.15 | 145 | 94.94 | 28.144 |
|  CBRTransportHeaderProxy | 512M | 500 | 1024 | 100 | 0 | 3519.48 | 106.48 | 8.78 | 139 | 97.17 | 28.119 |
|  CBRTransportHeaderProxy | 512M | 500 | 1024 | 500 | 0 | 738.84 | 501.39 | 2.69 | 515 | 99.11 | 28.098 |
|  CBRTransportHeaderProxy | 512M | 500 | 1024 | 1000 | 0 | 367.54 | 1001.28 | 29.61 | 1007 | 99.33 | 28.043 |
|  CBRTransportHeaderProxy | 512M | 500 | 10240 | 0 | 0 | 2825.63 | 132.71 | 60.94 | 301 | 96.3 | 28.144 |
|  CBRTransportHeaderProxy | 512M | 500 | 10240 | 30 | 0 | 3590.9 | 104.34 | 34.83 | 206 | 96.05 | 28.139 |
|  CBRTransportHeaderProxy | 512M | 500 | 10240 | 100 | 0 | 3315.09 | 112.57 | 27.94 | 191 | 97.14 | 28.173 |
|  CBRTransportHeaderProxy | 512M | 500 | 10240 | 500 | 0 | 748.8 | 496.15 | 51.2 | 519 | 99.1 | 28.031 |
|  CBRTransportHeaderProxy | 512M | 500 | 10240 | 1000 | 0 | 369.49 | 995.29 | 81.4 | 1003 | 99.34 | 28.13 |
|  CBRTransportHeaderProxy | 512M | 500 | 102400 | 0 | 0 | 839.76 | 446.84 | 129.13 | 799 | 98.7 | 28.084 |
|  CBRTransportHeaderProxy | 512M | 500 | 102400 | 30 | 0 | 907.28 | 413.21 | 105.1 | 691 | 98.53 | 28.163 |
|  CBRTransportHeaderProxy | 512M | 500 | 102400 | 100 | 0 | 822.68 | 456.22 | 133.46 | 803 | 98.65 | 28.15 |
|  CBRTransportHeaderProxy | 512M | 500 | 102400 | 500 | 0 | 991.2 | 374.67 | 216.5 | 663 | 98.87 | 28.09 |
|  CBRTransportHeaderProxy | 512M | 500 | 102400 | 1000 | 0 | 387.75 | 947.68 | 225.47 | 1011 | 99.35 | 28.05 |
|  CBRTransportHeaderProxy | 512M | 1000 | 500 | 0 | 0 | 4759.74 | 157.4 | 87.81 | 501 | 93.76 | 72.234 |
|  CBRTransportHeaderProxy | 512M | 1000 | 500 | 30 | 0 | 4984.92 | 150.21 | 60.68 | 321 | 93.75 | 28.145 |
|  CBRTransportHeaderProxy | 512M | 1000 | 500 | 100 | 0 | 4862.11 | 154.09 | 42.76 | 293 | 95.13 | 28.142 |
|  CBRTransportHeaderProxy | 512M | 1000 | 500 | 500 | 0 | 1474.8 | 502.99 | 17.36 | 547 | 98.42 | 28.068 |
|  CBRTransportHeaderProxy | 512M | 1000 | 500 | 1000 | 0 | 733.62 | 1002.62 | 12.4 | 1039 | 98.99 | 28.164 |
|  CBRTransportHeaderProxy | 512M | 1000 | 1024 | 0 | 0 | 4155.71 | 180.7 | 86.83 | 475 | 94.44 | 28.083 |
|  CBRTransportHeaderProxy | 512M | 1000 | 1024 | 30 | 0 | 5212.44 | 143.39 | 58.2 | 309 | 94.02 | 28.092 |
|  CBRTransportHeaderProxy | 512M | 1000 | 1024 | 100 | 0 | 4861.59 | 154.21 | 46.18 | 293 | 94.68 | 28.07 |
|  CBRTransportHeaderProxy | 512M | 1000 | 1024 | 500 | 0 | 1478.89 | 502.32 | 28.27 | 547 | 98.39 | 28.068 |
|  CBRTransportHeaderProxy | 512M | 1000 | 1024 | 1000 | 0 | 734.72 | 1001.39 | 38.21 | 1039 | 99 | 28.104 |
|  CBRTransportHeaderProxy | 512M | 1000 | 10240 | 0 | 0 | 2790.63 | 268.55 | 109.09 | 579 | 96.15 | 28.085 |
|  CBRTransportHeaderProxy | 512M | 1000 | 10240 | 30 | 0 | 3253.33 | 230.25 | 80.91 | 461 | 96.41 | 28.07 |
|  CBRTransportHeaderProxy | 512M | 1000 | 10240 | 100 | 0 | 3500.2 | 213.89 | 70.45 | 407 | 95.5 | 28.069 |
|  CBRTransportHeaderProxy | 512M | 1000 | 10240 | 500 | 0 | 1522.47 | 486.03 | 93.83 | 555 | 98.24 | 28.082 |
|  CBRTransportHeaderProxy | 512M | 1000 | 10240 | 1000 | 0 | 739.59 | 993.68 | 93.48 | 1039 | 99.01 | 28.068 |
|  CBRTransportHeaderProxy | 512M | 1000 | 102400 | 0 | 0 | 798.87 | 932.91 | 255.98 | 1631 | 98.91 | 28.086 |
|  CBRTransportHeaderProxy | 512M | 1000 | 102400 | 30 | 0 | 827.79 | 900.19 | 254.13 | 1591 | 98.57 | 28.027 |
|  CBRTransportHeaderProxy | 512M | 1000 | 102400 | 100 | 0 | 804.24 | 925.83 | 244.65 | 1607 | 98.37 | 28.215 |
|  CBRTransportHeaderProxy | 512M | 1000 | 102400 | 500 | 0 | 836.43 | 891.23 | 187.57 | 1383 | 98.45 | 28.134 |
|  CBRTransportHeaderProxy | 512M | 1000 | 102400 | 1000 | 0 | 808.45 | 905.95 | 352.39 | 1231 | 98.74 | 28.095 |
|  DirectProxy | 512M | 100 | 500 | 0 | 0 | 4518.83 | 16.66 | 21.66 | 95 | 96.96 | 28.069 |
|  DirectProxy | 512M | 100 | 500 | 30 | 0 | 2391.02 | 31.53 | 2.48 | 45 | 98.64 | 28.166 |
|  DirectProxy | 512M | 100 | 500 | 100 | 0 | 751.7 | 100.17 | 9.7 | 107 | 99.3 | 28.077 |
|  DirectProxy | 512M | 100 | 500 | 500 | 0 | 149.18 | 501.41 | 1.05 | 503 | 99.53 | 28.085 |
|  DirectProxy | 512M | 100 | 500 | 1000 | 0 | 74.03 | 1002.33 | 4.14 | 1007 | 99.49 | 28.142 |
|  DirectProxy | 512M | 100 | 1024 | 0 | 0 | 4294.15 | 17.53 | 22.03 | 91 | 96.63 | 28.098 |
|  DirectProxy | 512M | 100 | 1024 | 30 | 0 | 2392.59 | 31.49 | 2.38 | 44 | 98.55 | 28.079 |
|  DirectProxy | 512M | 100 | 1024 | 100 | 0 | 747.05 | 100.82 | 6.5 | 114 | 99.27 | 28.149 |
|  DirectProxy | 512M | 100 | 1024 | 500 | 0 | 149.18 | 501.35 | 0.89 | 503 | 99.55 | 28.071 |
|  DirectProxy | 512M | 100 | 1024 | 1000 | 0 | 74.04 | 1002.51 | 5.51 | 1007 | 99.56 | 28.089 |
|  DirectProxy | 512M | 100 | 10240 | 0 | 0 | 3165.45 | 23.78 | 24.1 | 98 | 97.53 | 28.107 |
|  DirectProxy | 512M | 100 | 10240 | 30 | 0 | 2384.43 | 31.59 | 3.83 | 45 | 98.6 | 28.093 |
|  DirectProxy | 512M | 100 | 10240 | 100 | 0 | 759.75 | 99.11 | 12.86 | 104 | 99.29 | 28.123 |
|  DirectProxy | 512M | 100 | 10240 | 500 | 0 | 151.06 | 494.63 | 54.39 | 503 | 99.61 | 28.146 |
|  DirectProxy | 512M | 100 | 10240 | 1000 | 0 | 73.9 | 1002.06 | 0.87 | 1003 | 99.54 | 28.167 |
|  DirectProxy | 512M | 100 | 102400 | 0 | 0 | 877.8 | 85.85 | 31.8 | 173 | 98.81 | 28.097 |
|  DirectProxy | 512M | 100 | 102400 | 30 | 0 | 1108.12 | 67.88 | 24.06 | 112 | 99.03 | 28.145 |
|  DirectProxy | 512M | 100 | 102400 | 100 | 0 | 873.52 | 86.19 | 41.79 | 166 | 99.18 | 28.143 |
|  DirectProxy | 512M | 100 | 102400 | 500 | 0 | 155.74 | 480.54 | 98.42 | 505 | 99.57 | 28.099 |
|  DirectProxy | 512M | 100 | 102400 | 1000 | 0 | 79.19 | 935.36 | 238.61 | 1007 | 99.59 | 28.151 |
|  DirectProxy | 512M | 200 | 500 | 0 | 0 | 4974.19 | 30.18 | 29.13 | 126 | 96.22 | 28.227 |
|  DirectProxy | 512M | 200 | 500 | 30 | 0 | 4366.71 | 34.39 | 5.59 | 57 | 97.44 | 28.149 |
|  DirectProxy | 512M | 200 | 500 | 100 | 0 | 1457 | 101.76 | 5.82 | 119 | 98.87 | 28.116 |
|  DirectProxy | 512M | 200 | 500 | 500 | 0 | 297.41 | 501.2 | 1.21 | 503 | 99.5 | 28.134 |
|  DirectProxy | 512M | 200 | 500 | 1000 | 0 | 147.23 | 1002.05 | 0.79 | 1003 | 99.58 | 28.112 |
|  DirectProxy | 512M | 200 | 1024 | 0 | 0 | 5045.62 | 29.78 | 29.11 | 116 | 96.05 | 28.096 |
|  DirectProxy | 512M | 200 | 1024 | 30 | 0 | 4343.21 | 34.54 | 5.78 | 58 | 97.46 | 28.12 |
|  DirectProxy | 512M | 200 | 1024 | 100 | 0 | 1485.45 | 101.09 | 7.13 | 118 | 98.9 | 28.086 |
|  DirectProxy | 512M | 200 | 1024 | 500 | 0 | 297.55 | 501.17 | 5.4 | 503 | 99.46 | 28.131 |
|  DirectProxy | 512M | 200 | 1024 | 1000 | 0 | 147.26 | 1002.06 | 0.94 | 1003 | 99.55 | 28.082 |
|  DirectProxy | 512M | 200 | 10240 | 0 | 0 | 3487 | 43.05 | 30.06 | 115 | 96.87 | 28.148 |
|  DirectProxy | 512M | 200 | 10240 | 30 | 0 | 3554.6 | 42.25 | 12.47 | 80 | 97.74 | 28.088 |
|  DirectProxy | 512M | 200 | 10240 | 100 | 0 | 1509.72 | 99.44 | 14.61 | 118 | 98.89 | 28.161 |
|  DirectProxy | 512M | 200 | 10240 | 500 | 0 | 311.05 | 476.51 | 111.08 | 571 | 99.45 | 28.073 |
|  DirectProxy | 512M | 200 | 10240 | 1000 | 0 | 147.56 | 1000.74 | 35.64 | 1003 | 99.61 | 28.037 |
|  DirectProxy | 512M | 200 | 102400 | 0 | 0 | 946.09 | 158.77 | 49.4 | 289 | 98.88 | 28.045 |
|  DirectProxy | 512M | 200 | 102400 | 30 | 0 | 938.06 | 159.97 | 46.81 | 281 | 98.84 | 28.147 |
|  DirectProxy | 512M | 200 | 102400 | 100 | 0 | 981.62 | 152.8 | 38.4 | 222 | 98.76 | 28.065 |
|  DirectProxy | 512M | 200 | 102400 | 500 | 0 | 313.14 | 475.23 | 112.05 | 509 | 99.49 | 28.143 |
|  DirectProxy | 512M | 200 | 102400 | 1000 | 0 | 150.38 | 980.01 | 143.18 | 1007 | 99.52 | 28.058 |
|  DirectProxy | 512M | 500 | 500 | 0 | 0 | 4016.8 | 93.5 | 49.7 | 227 | 95.16 | 28.072 |
|  DirectProxy | 512M | 500 | 500 | 30 | 0 | 5430.49 | 69.05 | 25.4 | 143 | 94.93 | 28.07 |
|  DirectProxy | 512M | 500 | 500 | 100 | 0 | 3524.26 | 106.29 | 9.82 | 140 | 97.27 | 28.188 |
|  DirectProxy | 512M | 500 | 500 | 500 | 0 | 741.74 | 501.16 | 11.73 | 515 | 99.15 | 28.142 |
|  DirectProxy | 512M | 500 | 500 | 1000 | 0 | 367.25 | 1002.17 | 1.84 | 1007 | 99.33 | 28.123 |
|  DirectProxy | 512M | 500 | 1024 | 0 | 0 | 4657.52 | 80.43 | 45.85 | 215 | 95.4 | 28.1 |
|  DirectProxy | 512M | 500 | 1024 | 30 | 0 | 5387.67 | 69.59 | 25.78 | 144 | 95.16 | 28.218 |
|  DirectProxy | 512M | 500 | 1024 | 100 | 0 | 3514.02 | 106.26 | 9.2 | 140 | 97.21 | 28.167 |
|  DirectProxy | 512M | 500 | 1024 | 500 | 0 | 741.78 | 500.92 | 14.96 | 511 | 99.12 | 28.063 |
|  DirectProxy | 512M | 500 | 1024 | 1000 | 0 | 367.32 | 1002.15 | 1.95 | 1003 | 99.34 | 28.072 |
|  DirectProxy | 512M | 500 | 10240 | 0 | 0 | 2958.49 | 126.92 | 58.54 | 287 | 96.24 | 28.079 |
|  DirectProxy | 512M | 500 | 10240 | 30 | 0 | 3641.01 | 102.82 | 31.86 | 203 | 96.45 | 28.203 |
|  DirectProxy | 512M | 500 | 10240 | 100 | 0 | 3365.35 | 111.18 | 29.54 | 186 | 97.01 | 28.096 |
|  DirectProxy | 512M | 500 | 10240 | 500 | 0 | 746.11 | 497.92 | 41.78 | 519 | 99.13 | 28.121 |
|  DirectProxy | 512M | 500 | 10240 | 1000 | 0 | 367.05 | 1002.15 | 1.72 | 1007 | 99.37 | 28.096 |
|  DirectProxy | 512M | 500 | 102400 | 0 | 0 | 867.58 | 432.1 | 105.69 | 707 | 98.52 | 28.118 |
|  DirectProxy | 512M | 500 | 102400 | 30 | 0 | 865.49 | 433.66 | 122.98 | 767 | 98.42 | 28.058 |
|  DirectProxy | 512M | 500 | 102400 | 100 | 0 | 873.26 | 429.65 | 110.74 | 723 | 98.66 | 28.102 |
|  DirectProxy | 512M | 500 | 102400 | 500 | 0 | 931.79 | 397.7 | 210.77 | 691 | 98.93 | 28.099 |
|  DirectProxy | 512M | 500 | 102400 | 1000 | 0 | 386.19 | 952.03 | 216.9 | 1011 | 99.34 | 28.061 |
|  DirectProxy | 512M | 1000 | 500 | 0 | 0 | 4212.01 | 177.87 | 92.17 | 455 | 94.04 | 28.081 |
|  DirectProxy | 512M | 1000 | 500 | 30 | 0 | 4972.23 | 150.86 | 59.72 | 317 | 93.64 | 28.078 |
|  DirectProxy | 512M | 1000 | 500 | 100 | 0 | 5041.18 | 148.51 | 43.66 | 293 | 94.97 | 28.129 |
|  DirectProxy | 512M | 1000 | 500 | 500 | 0 | 1471.26 | 504.02 | 16.6 | 551 | 98.44 | 28.109 |
|  DirectProxy | 512M | 1000 | 500 | 1000 | 0 | 734.52 | 1000.71 | 45.4 | 1039 | 99.17 | 28.124 |
|  DirectProxy | 512M | 1000 | 1024 | 0 | 0 | 4053.54 | 185.24 | 95.47 | 461 | 93.75 | 28.097 |
|  DirectProxy | 512M | 1000 | 1024 | 30 | 0 | 4553.58 | 164.48 | 61.48 | 323 | 94.12 | 28.103 |
|  DirectProxy | 512M | 1000 | 1024 | 100 | 0 | 4964.12 | 150.79 | 42.88 | 283 | 94.67 | 28.087 |
|  DirectProxy | 512M | 1000 | 1024 | 500 | 0 | 1477.01 | 503.42 | 22.85 | 551 | 98.66 | 28.088 |
|  DirectProxy | 512M | 1000 | 1024 | 1000 | 0 | 735.5 | 1001.67 | 29.79 | 1031 | 99.04 | 28.132 |
|  DirectProxy | 512M | 1000 | 10240 | 0 | 0 | 3004.57 | 249.98 | 94.51 | 491 | 95.71 | 28.113 |
|  DirectProxy | 512M | 1000 | 10240 | 30 | 0 | 2937.61 | 255.6 | 98.22 | 507 | 95.55 | 28.146 |
|  DirectProxy | 512M | 1000 | 10240 | 100 | 0 | 3579.25 | 209.3 | 69.72 | 405 | 95.79 | 28.112 |
|  DirectProxy | 512M | 1000 | 10240 | 500 | 0 | 1496.88 | 496.3 | 65.29 | 551 | 98.37 | 28.132 |
|  DirectProxy | 512M | 1000 | 10240 | 1000 | 0 | 738.87 | 995.33 | 86.13 | 1039 | 98.98 | 28.097 |
|  DirectProxy | 512M | 1000 | 102400 | 0 | 0 | 814.82 | 915.09 | 242.25 | 1623 | 98.41 | 28.082 |
|  DirectProxy | 512M | 1000 | 102400 | 30 | 0 | 847.82 | 880.64 | 233.23 | 1559 | 98.35 | 28.042 |
|  DirectProxy | 512M | 1000 | 102400 | 100 | 0 | 830.33 | 898.3 | 244.07 | 1599 | 98.4 | 28.049 |
|  DirectProxy | 512M | 1000 | 102400 | 500 | 0 | 793.74 | 937.2 | 222.37 | 1599 | 98.52 | 28.091 |
|  DirectProxy | 512M | 1000 | 102400 | 1000 | 0 | 907.58 | 805.18 | 417.74 | 1271 | 98.64 | 28.154 |
|  XSLTEnhancedProxy | 512M | 100 | 500 | 0 | 0 | 2717.03 | 27.73 | 28.96 | 127 | 96.39 | 41.57 |
|  XSLTEnhancedProxy | 512M | 100 | 500 | 30 | 0 | 2276.96 | 33.12 | 3.05 | 46 | 97.65 | 38.673 |
|  XSLTEnhancedProxy | 512M | 100 | 500 | 100 | 0 | 741.14 | 101.57 | 1.81 | 113 | 98.89 | 28.044 |
|  XSLTEnhancedProxy | 512M | 100 | 500 | 500 | 0 | 149.04 | 502.22 | 1.54 | 507 | 99.45 | 28.161 |
|  XSLTEnhancedProxy | 512M | 100 | 500 | 1000 | 0 | 73.82 | 1002.07 | 0.76 | 1007 | 99.53 | 28.118 |
|  XSLTEnhancedProxy | 512M | 100 | 1024 | 0 | 0 | 2119.81 | 35.59 | 35.34 | 178 | 96.04 | 28.096 |
|  XSLTEnhancedProxy | 512M | 100 | 1024 | 30 | 0 | 2143.11 | 35.16 | 7.14 | 57 | 97.33 | 41.191 |
|  XSLTEnhancedProxy | 512M | 100 | 1024 | 100 | 0 | 737.89 | 102.05 | 2.63 | 114 | 98.83 | 28.098 |
|  XSLTEnhancedProxy | 512M | 100 | 1024 | 500 | 0 | 148.96 | 502.24 | 1.24 | 503 | 99.48 | 28.146 |
|  XSLTEnhancedProxy | 512M | 100 | 1024 | 1000 | 0 | 74.04 | 1002.2 | 1.81 | 1007 | 99.53 | 28.155 |
|  XSLTEnhancedProxy | 512M | 100 | 10240 | 0 | 0 | 449.63 | 167.8 | 74.82 | 381 | 97.1 | 41.067 |
|  XSLTEnhancedProxy | 512M | 100 | 10240 | 30 | 0 | 462.84 | 163.04 | 59.08 | 317 | 96.94 | 39.471 |
|  XSLTEnhancedProxy | 512M | 100 | 10240 | 100 | 0 | 452.15 | 166.96 | 54.38 | 305 | 97.38 | 39.534 |
|  XSLTEnhancedProxy | 512M | 100 | 10240 | 500 | 0 | 160.58 | 466.5 | 125.42 | 519 | 98.95 | 23.999 |
|  XSLTEnhancedProxy | 512M | 100 | 10240 | 1000 | 0 | 73.73 | 1003.34 | 29.63 | 1015 | 99.34 | 23.951 |
|  XSLTEnhancedProxy | 512M | 100 | 102400 | 0 | 0 | 63.85 | 1170.04 | 399.63 | 2095 | 94.28 | 79.223 |
|  XSLTEnhancedProxy | 512M | 100 | 102400 | 30 | 0 | 64.67 | 1160.68 | 409.71 | 2399 | 93.99 | 81.896 |
|  XSLTEnhancedProxy | 512M | 100 | 102400 | 100 | 0 | 63.39 | 1174.87 | 389.18 | 2191 | 94.75 | 79.989 |
|  XSLTEnhancedProxy | 512M | 100 | 102400 | 500 | 0 | 62.16 | 1208.53 | 324.15 | 2079 | 95.48 | 73.07 |
|  XSLTEnhancedProxy | 512M | 100 | 102400 | 1000 | 0 | 59.73 | 1232.61 | 345.11 | 1871 | 96.35 | 64.132 |
|  XSLTEnhancedProxy | 512M | 200 | 500 | 0 | 0 | 2797.94 | 53.75 | 46.43 | 215 | 95.12 | 46.374 |
|  XSLTEnhancedProxy | 512M | 200 | 500 | 30 | 0 | 3028.87 | 49.65 | 16.27 | 97 | 95.9 | 45.184 |
|  XSLTEnhancedProxy | 512M | 200 | 500 | 100 | 0 | 1459.8 | 102.83 | 3.66 | 119 | 98.06 | 28.092 |
|  XSLTEnhancedProxy | 512M | 200 | 500 | 500 | 0 | 297.29 | 505.27 | 17.02 | 603 | 99.11 | 43.9 |
|  XSLTEnhancedProxy | 512M | 200 | 500 | 1000 | 0 | 147.2 | 1002.07 | 0.95 | 1003 | 99.47 | 28.097 |
|  XSLTEnhancedProxy | 512M | 200 | 1024 | 0 | 0 | 1970 | 76.35 | 57.49 | 299 | 95.42 | 46.061 |
|  XSLTEnhancedProxy | 512M | 200 | 1024 | 30 | 0 | 2585.78 | 58.02 | 19.97 | 111 | 95.8 | 44.886 |
|  XSLTEnhancedProxy | 512M | 200 | 1024 | 100 | 0 | 1437.21 | 104.45 | 8.92 | 126 | 97.73 | 44.859 |
|  XSLTEnhancedProxy | 512M | 200 | 1024 | 500 | 0 | 297.18 | 501.75 | 1.59 | 505 | 99.26 | 28.063 |
|  XSLTEnhancedProxy | 512M | 200 | 1024 | 1000 | 0 | 147.03 | 1002.14 | 1.34 | 1007 | 99.44 | 28.098 |
|  XSLTEnhancedProxy | 512M | 200 | 10240 | 0 | 0 | 454.15 | 330.95 | 146.5 | 787 | 95.76 | 50.986 |
|  XSLTEnhancedProxy | 512M | 200 | 10240 | 30 | 0 | 460.72 | 323.53 | 132.94 | 719 | 95.41 | 49.983 |
|  XSLTEnhancedProxy | 512M | 200 | 10240 | 100 | 0 | 450.77 | 333.34 | 119.88 | 707 | 95.39 | 48.943 |
|  XSLTEnhancedProxy | 512M | 200 | 10240 | 500 | 0 | 321.1 | 462.46 | 161.99 | 659 | 97.06 | 48.615 |
|  XSLTEnhancedProxy | 512M | 200 | 10240 | 1000 | 0 | 158.04 | 932.92 | 248.5 | 1031 | 98.62 | 40.684 |
|  XSLTEnhancedProxy | 512M | 200 | 102400 | 0 | 0 | 60.94 | 2394.99 | 761.45 | 4287 | 92 | 116.898 |
|  XSLTEnhancedProxy | 512M | 200 | 102400 | 30 | 0 | 62.86 | 2337.73 | 686.79 | 3935 | 91.33 | 114.109 |
|  XSLTEnhancedProxy | 512M | 200 | 102400 | 100 | 0 | 62.14 | 2361.72 | 709.75 | 3919 | 91.38 | 113.013 |
|  XSLTEnhancedProxy | 512M | 200 | 102400 | 500 | 0 | 61.31 | 2381.08 | 673.37 | 4127 | 91.26 | 116.699 |
|  XSLTEnhancedProxy | 512M | 200 | 102400 | 1000 | 0 | 63.78 | 2316.8 | 622.63 | 3999 | 93.47 | 109.175 |
|  XSLTEnhancedProxy | 512M | 500 | 500 | 0 | 0 | 2096.92 | 178.66 | 110.13 | 555 | 90.33 | 95.78 |
|  XSLTEnhancedProxy | 512M | 500 | 500 | 30 | 0 | 2739.64 | 136.73 | 66.34 | 355 | 88.09 | 93.383 |
|  XSLTEnhancedProxy | 512M | 500 | 500 | 100 | 0 | 2629.38 | 142.24 | 49.79 | 337 | 90.93 | 89.835 |
|  XSLTEnhancedProxy | 512M | 500 | 500 | 500 | 0 | 735.98 | 508.33 | 18.85 | 595 | 96.98 | 72.262 |
|  XSLTEnhancedProxy | 512M | 500 | 500 | 1000 | 0 | 366.92 | 1004.15 | 13.54 | 1055 | 98.21 | 53.875 |
|  XSLTEnhancedProxy | 512M | 500 | 1024 | 0 | 0 | 1920.5 | 195.4 | 125.27 | 635 | 89.82 | 93.817 |
|  XSLTEnhancedProxy | 512M | 500 | 1024 | 30 | 0 | 2222.08 | 168.92 | 77.33 | 411 | 89.04 | 95.281 |
|  XSLTEnhancedProxy | 512M | 500 | 1024 | 100 | 0 | 2307.33 | 161.97 | 57.66 | 373 | 90.36 | 87.518 |
|  XSLTEnhancedProxy | 512M | 500 | 1024 | 500 | 0 | 715.04 | 519.22 | 36.7 | 687 | 96.42 | 76.366 |
|  XSLTEnhancedProxy | 512M | 500 | 1024 | 1000 | 0 | 364.28 | 1005.65 | 15.2 | 1087 | 97.81 | 65.287 |
|  XSLTEnhancedProxy | 512M | 500 | 10240 | 0 | 0 | 458.58 | 810.98 | 321.71 | 1735 | 91.8 | 89.722 |
|  XSLTEnhancedProxy | 512M | 500 | 10240 | 30 | 0 | 450.12 | 831.01 | 360.91 | 1887 | 92.01 | 92.904 |
|  XSLTEnhancedProxy | 512M | 500 | 10240 | 100 | 0 | 452.84 | 823.95 | 312.48 | 1711 | 92.43 | 91.528 |
|  XSLTEnhancedProxy | 512M | 500 | 10240 | 500 | 0 | 444.55 | 837.28 | 235.49 | 1527 | 93.07 | 83.588 |
|  XSLTEnhancedProxy | 512M | 500 | 10240 | 1000 | 0 | 382.82 | 958.46 | 374.76 | 1495 | 95.43 | 78.841 |
|  XSLTEnhancedProxy | 512M | 500 | 102400 | 0 | 0 | 53.99 | 6483.31 | 1568.37 | 10111 | 85.31 | 210.546 |
|  XSLTEnhancedProxy | 512M | 500 | 102400 | 30 | 0 | 51.92 | 6900.31 | 2120.54 | 11839 | 84.52 | 228.685 |
|  XSLTEnhancedProxy | 512M | 500 | 102400 | 100 | 0 | 49.45 | 6951.27 | 1538.24 | 10495 | 88.12 | 211.315 |
|  XSLTEnhancedProxy | 512M | 500 | 102400 | 500 | 0 | 56.15 | 6322.74 | 1491.17 | 9983 | 85.97 | 203.709 |
|  XSLTEnhancedProxy | 512M | 500 | 102400 | 1000 | 0 | 55.41 | 6439.78 | 1706.07 | 10751 | 85.89 | 203.082 |
|  XSLTEnhancedProxy | 512M | 1000 | 500 | 0 | 0 | 1993.69 | 376.2 | 201.02 | 975 | 87.75 | 125.162 |
|  XSLTEnhancedProxy | 512M | 1000 | 500 | 30 | 0 | 2252.7 | 332.56 | 171.78 | 887 | 86.18 | 126.601 |
|  XSLTEnhancedProxy | 512M | 1000 | 500 | 100 | 0 | 2374.34 | 315.75 | 129.7 | 755 | 86.32 | 127.289 |
|  XSLTEnhancedProxy | 512M | 1000 | 500 | 500 | 0 | 1353.73 | 550.8 | 60.98 | 751 | 93.87 | 113.495 |
|  XSLTEnhancedProxy | 512M | 1000 | 500 | 1000 | 0 | 720.28 | 1022.91 | 47.25 | 1255 | 96.16 | 107.113 |
|  XSLTEnhancedProxy | 512M | 1000 | 1024 | 0 | 0 | 1722.21 | 435.73 | 232.4 | 1191 | 88.36 | 129.442 |
|  XSLTEnhancedProxy | 512M | 1000 | 1024 | 30 | 0 | 1910.53 | 392.91 | 190.4 | 1031 | 86.82 | 123.344 |
|  XSLTEnhancedProxy | 512M | 1000 | 1024 | 100 | 0 | 1980.79 | 376.49 | 148.51 | 831 | 89.67 | 124.278 |
|  XSLTEnhancedProxy | 512M | 1000 | 1024 | 500 | 0 | 1335.71 | 559.69 | 64.39 | 771 | 92.65 | 116.358 |
|  XSLTEnhancedProxy | 512M | 1000 | 1024 | 1000 | 0 | 720.95 | 1024.69 | 42.19 | 1215 | 95.71 | 108.921 |
|  XSLTEnhancedProxy | 512M | 1000 | 10240 | 0 | 0 | 430.77 | 1725.77 | 581.89 | 3135 | 91.6 | 135.989 |
|  XSLTEnhancedProxy | 512M | 1000 | 10240 | 30 | 0 | 435.23 | 1698.2 | 634.39 | 3279 | 91.47 | 142.086 |
|  XSLTEnhancedProxy | 512M | 1000 | 10240 | 100 | 0 | 440.79 | 1685.52 | 572.41 | 3167 | 91.24 | 137.257 |
|  XSLTEnhancedProxy | 512M | 1000 | 10240 | 500 | 0 | 446.74 | 1638.01 | 498.54 | 3087 | 91.45 | 138.184 |
|  XSLTEnhancedProxy | 512M | 1000 | 10240 | 1000 | 0 | 432.8 | 1697.2 | 464.28 | 3087 | 92.93 | 130.755 |
|  XSLTEnhancedProxy | 512M | 1000 | 102400 | 0 | 0 | 36.77 | 18014.96 | 5137.19 | 27775 | 80.9 | 314.582 |
|  XSLTEnhancedProxy | 512M | 1000 | 102400 | 30 | 0 | 37.06 | 18420.42 | 6225.66 | 31487 | 82.3 | 319.637 |
|  XSLTEnhancedProxy | 512M | 1000 | 102400 | 100 | 0 | 33.14 | 18741.11 | 5847.64 | 30975 | 79.31 | 321.288 |
|  XSLTEnhancedProxy | 512M | 1000 | 102400 | 500 | 0 | 33.68 | 19842.13 | 6878.8 | 33279 | 79.35 | 330.7 |
|  XSLTEnhancedProxy | 512M | 1000 | 102400 | 1000 | 0 | 38.79 | 16694.78 | 4259.94 | 25855 | 80.37 | 298.947 |
|  XSLTProxy | 512M | 100 | 500 | 0 | 0 | 1614.23 | 46.74 | 38.89 | 185 | 95.81 | 39.876 |
|  XSLTProxy | 512M | 100 | 500 | 30 | 0 | 1912.98 | 39.42 | 8.96 | 70 | 96.63 | 40.771 |
|  XSLTProxy | 512M | 100 | 500 | 100 | 0 | 734.56 | 102.6 | 3.46 | 119 | 98.38 | 41.046 |
|  XSLTProxy | 512M | 100 | 500 | 500 | 0 | 149.09 | 502.62 | 1.59 | 505 | 99.21 | 42.594 |
|  XSLTProxy | 512M | 100 | 500 | 1000 | 0 | 73.81 | 1002.21 | 1.22 | 1007 | 99.32 | 41.7 |
|  XSLTProxy | 512M | 100 | 1024 | 0 | 0 | 1220.78 | 61.82 | 52.34 | 287 | 96.17 | 40.186 |
|  XSLTProxy | 512M | 100 | 1024 | 30 | 0 | 1593.82 | 47.34 | 14.23 | 93 | 96.58 | 40.964 |
|  XSLTProxy | 512M | 100 | 1024 | 100 | 0 | 729.65 | 103.29 | 3.63 | 120 | 98.28 | 40.368 |
|  XSLTProxy | 512M | 100 | 1024 | 500 | 0 | 149.11 | 503.02 | 1.65 | 511 | 99.21 | 40.959 |
|  XSLTProxy | 512M | 100 | 1024 | 1000 | 0 | 73.94 | 1003.05 | 6.64 | 1039 | 99.34 | 41.049 |
|  XSLTProxy | 512M | 100 | 10240 | 0 | 0 | 244.91 | 308.34 | 187.59 | 883 | 94.68 | 104.37 |
|  XSLTProxy | 512M | 100 | 10240 | 30 | 0 | 249.48 | 301.97 | 149.51 | 799 | 94.59 | 104.807 |
|  XSLTProxy | 512M | 100 | 10240 | 100 | 0 | 250.2 | 300.86 | 122.32 | 699 | 94.62 | 97.509 |
|  XSLTProxy | 512M | 100 | 10240 | 500 | 0 | 145.98 | 512.66 | 12.79 | 567 | 98.11 | 43.467 |
|  XSLTProxy | 512M | 100 | 10240 | 1000 | 0 | 73.54 | 1008.96 | 9.5 | 1055 | 98.83 | 43.874 |
|  XSLTProxy | 512M | 100 | 102400 | 0 | 0 | 22.07 | 3342.41 | 938.7 | 5727 | 85.07 | 161.883 |
|  XSLTProxy | 512M | 100 | 102400 | 30 | 0 | 21.67 | 3384.06 | 927.96 | 5695 | 86.22 | 156.457 |
|  XSLTProxy | 512M | 100 | 102400 | 100 | 0 | 21.03 | 3474.44 | 948.75 | 5727 | 86.01 | 160.851 |
|  XSLTProxy | 512M | 100 | 102400 | 500 | 0 | 21.74 | 3367.9 | 901.66 | 5343 | 86.86 | 157.475 |
|  XSLTProxy | 512M | 100 | 102400 | 1000 | 0 | 21.33 | 3410.5 | 886.42 | 5567 | 87.93 | 148.616 |
|  XSLTProxy | 512M | 200 | 500 | 0 | 0 | 1613.01 | 93.18 | 60.05 | 307 | 93.96 | 86.286 |
|  XSLTProxy | 512M | 200 | 500 | 30 | 0 | 1980.87 | 75.89 | 30.04 | 158 | 94.49 | 83.155 |
|  XSLTProxy | 512M | 200 | 500 | 100 | 0 | 1417.52 | 105.95 | 6.86 | 131 | 96.9 | 40.919 |
|  XSLTProxy | 512M | 200 | 500 | 500 | 0 | 296.46 | 502.43 | 2.94 | 523 | 98.94 | 41.223 |
|  XSLTProxy | 512M | 200 | 500 | 1000 | 0 | 146.99 | 1002.18 | 1.74 | 1007 | 99.19 | 41.711 |
|  XSLTProxy | 512M | 200 | 1024 | 0 | 0 | 1228.19 | 122.44 | 81.63 | 403 | 93.85 | 91.425 |
|  XSLTProxy | 512M | 200 | 1024 | 30 | 0 | 1512.24 | 99.46 | 39.31 | 232 | 94.39 | 88.184 |
|  XSLTProxy | 512M | 200 | 1024 | 100 | 0 | 1343.99 | 111.74 | 13.17 | 160 | 96.7 | 24.013 |
|  XSLTProxy | 512M | 200 | 1024 | 500 | 0 | 296.95 | 503.12 | 3.32 | 523 | 98.83 | 41.515 |
|  XSLTProxy | 512M | 200 | 1024 | 1000 | 0 | 146.81 | 1002.22 | 1.92 | 1011 | 99.14 | 42.064 |
|  XSLTProxy | 512M | 200 | 10240 | 0 | 0 | 230.47 | 649.98 | 386.27 | 1823 | 91.27 | 127.624 |
|  XSLTProxy | 512M | 200 | 10240 | 30 | 0 | 233.65 | 640.76 | 308.77 | 1511 | 90.91 | 126.267 |
|  XSLTProxy | 512M | 200 | 10240 | 100 | 0 | 228.35 | 654.04 | 301.22 | 1519 | 91.4 | 121.16 |
|  XSLTProxy | 512M | 200 | 10240 | 500 | 0 | 208.84 | 712.18 | 173.34 | 1303 | 93.73 | 115.82 |
|  XSLTProxy | 512M | 200 | 10240 | 1000 | 0 | 140.16 | 1049.52 | 63.86 | 1295 | 96.3 | 107.768 |
|  XSLTProxy | 512M | 200 | 102400 | 0 | 0 | 17.77 | 8051.46 | 2270.77 | 12991 | 79.29 | 248.621 |
|  XSLTProxy | 512M | 200 | 102400 | 30 | 0 | 14.75 | 9631.88 | 3658.4 | 19455 | 74.77 | 289.768 |
|  XSLTProxy | 512M | 200 | 102400 | 100 | 0 | 17.04 | 8401.03 | 2243.05 | 13823 | 78.1 | 253.252 |
|  XSLTProxy | 512M | 200 | 102400 | 500 | 0 | 17.48 | 7986.58 | 2491.72 | 14015 | 78.61 | 243.172 |
|  XSLTProxy | 512M | 200 | 102400 | 1000 | 0 | 17.2 | 8286.61 | 2182.88 | 12863 | 77.5 | 253.56 |
|  XSLTProxy | 512M | 500 | 500 | 0 | 0 | 1387.63 | 270.98 | 145.84 | 703 | 87.02 | 138.105 |
|  XSLTProxy | 512M | 500 | 500 | 30 | 0 | 1592.58 | 236.19 | 110.93 | 571 | 85.61 | 137.45 |
|  XSLTProxy | 512M | 500 | 500 | 100 | 0 | 1558.19 | 240.67 | 91.41 | 501 | 86.6 | 131.404 |
|  XSLTProxy | 512M | 500 | 500 | 500 | 0 | 682.06 | 545.68 | 63.7 | 755 | 94.68 | 116.737 |
|  XSLTProxy | 512M | 500 | 500 | 1000 | 0 | 362.96 | 1019.81 | 46.41 | 1239 | 96.86 | 103.36 |
|  XSLTProxy | 512M | 500 | 1024 | 0 | 0 | 1121.12 | 334.71 | 180.41 | 899 | 87.1 | 134.181 |
|  XSLTProxy | 512M | 500 | 1024 | 30 | 0 | 1247.65 | 301.1 | 140.58 | 703 | 86.31 | 131.733 |
|  XSLTProxy | 512M | 500 | 1024 | 100 | 0 | 1180.54 | 317.42 | 124.33 | 703 | 87.56 | 131.625 |
|  XSLTProxy | 512M | 500 | 1024 | 500 | 0 | 672.04 | 552.96 | 62.33 | 767 | 93.77 | 114.751 |
|  XSLTProxy | 512M | 500 | 1024 | 1000 | 0 | 357.09 | 1030.01 | 55.87 | 1295 | 96.31 | 104.281 |
|  XSLTProxy | 512M | 500 | 10240 | 0 | 0 | 219.52 | 1683.78 | 995.22 | 5119 | 86.23 | 169.697 |
|  XSLTProxy | 512M | 500 | 10240 | 30 | 0 | 228.42 | 1623.57 | 766.38 | 3807 | 86.11 | 161.439 |
|  XSLTProxy | 512M | 500 | 10240 | 100 | 0 | 230.87 | 1597.13 | 766.9 | 3919 | 85.92 | 165.804 |
|  XSLTProxy | 512M | 500 | 10240 | 500 | 0 | 217.33 | 1669.05 | 652.48 | 3615 | 86.75 | 165.247 |
|  XSLTProxy | 512M | 500 | 10240 | 1000 | 0 | 223.29 | 1651.75 | 426.52 | 2895 | 88.71 | 153.555 |
|  XSLTProxy | 512M | 500 | 102400 | 0 | 100 | 2.51 | 106210.3 | 23779.01 | 120319 | 25.19 | 472.741 |
|  XSLTProxy | 512M | 500 | 102400 | 30 | 100 | 2.18 | 111757.31 | 23856.81 | 123391 | 23.05 | 474.834 |
|  XSLTProxy | 512M | 500 | 102400 | 100 | 100 | 1.52 | 100224.78 | 29706.2 | 185343 | 18.31 | 482.479 |
|  XSLTProxy | 512M | 500 | 102400 | 500 | 100 | 2 | 117405.87 | 10902.25 | 123391 | 23.62 | 478.015 |
|  XSLTProxy | 512M | 500 | 102400 | 1000 | 100 | 2.18 | 117177.33 | 6204.64 | 121343 | 15.6 | 482.113 |
|  XSLTProxy | 512M | 1000 | 500 | 0 | 0 | 1247.68 | 601.39 | 258.36 | 1239 | 86.02 | 165.107 |
|  XSLTProxy | 512M | 1000 | 500 | 30 | 0 | 1333.3 | 560.61 | 236.41 | 1199 | 85.45 | 162.817 |
|  XSLTProxy | 512M | 1000 | 500 | 100 | 0 | 1440.06 | 520.63 | 215.44 | 1223 | 87.02 | 160.863 |
|  XSLTProxy | 512M | 1000 | 500 | 500 | 0 | 1224 | 607.39 | 103.48 | 943 | 90.64 | 158.227 |
|  XSLTProxy | 512M | 1000 | 500 | 1000 | 0 | 708.77 | 1042.5 | 64.24 | 1287 | 93.7 | 147.367 |
|  XSLTProxy | 512M | 1000 | 1024 | 0 | 0 | 1053.34 | 709.76 | 319.96 | 1575 | 87.43 | 160.341 |
|  XSLTProxy | 512M | 1000 | 1024 | 30 | 0 | 1090.05 | 688.27 | 307.54 | 1503 | 85.89 | 157.743 |
|  XSLTProxy | 512M | 1000 | 1024 | 100 | 0 | 1105.74 | 677.58 | 275.23 | 1543 | 86.08 | 161.607 |
|  XSLTProxy | 512M | 1000 | 1024 | 500 | 0 | 1028.01 | 717.92 | 155.19 | 1159 | 90.61 | 156.653 |
|  XSLTProxy | 512M | 1000 | 1024 | 1000 | 0 | 681.35 | 1083.88 | 91.83 | 1375 | 92.95 | 149.823 |
|  XSLTProxy | 512M | 1000 | 10240 | 0 | 0 | 190.96 | 3862.23 | 1794.87 | 9151 | 84.97 | 226.831 |
|  XSLTProxy | 512M | 1000 | 10240 | 30 | 0 | 159.56 | 4581.74 | 1954.43 | 9023 | 87.72 | 219.619 |
|  XSLTProxy | 512M | 1000 | 10240 | 100 | 0 | 161 | 4408.46 | 2127.76 | 12095 | 82.1 | 258.033 |
|  XSLTProxy | 512M | 1000 | 10240 | 500 | 0 | 171.41 | 4208.99 | 2282.83 | 10431 | 83.48 | 251.247 |
|  XSLTProxy | 512M | 1000 | 10240 | 1000 | 0 | 177.09 | 4026.67 | 2030.62 | 10239 | 86.18 | 242.598 |
|  XSLTProxy | 512M | 1000 | 102400 | 0 | 100 | 2.25 | 127541.52 | 24829.65 | 186367 | 21.05 | 488.058 |
|  XSLTProxy | 512M | 1000 | 102400 | 30 | 100 | 3.03 | 122005.08 | 6730.27 | 152575 | 23.93 | 486.409 |
|  XSLTProxy | 512M | 1000 | 102400 | 100 | 100 | 2.72 | 118876.46 | 5000.9 | 123391 | 24.06 | 486.475 |
|  XSLTProxy | 512M | 1000 | 102400 | 500 | 100 | 2.66 | 120064 | 0 | 120319 | 23.57 | 485.42 |
|  XSLTProxy | 512M | 1000 | 102400 | 1000 | 100 | 2.66 | 120064 | 0 | 120319 | 22.99 | 485.982 |
