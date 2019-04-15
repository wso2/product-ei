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
| Operating System | Distribution | Kernel | Linux ip-10-0-1-245 4.15.0-1035-aws #37-Ubuntu SMP Mon Mar 18 16:15:14 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux |
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
| Operating System | Distribution | Kernel | Linux ip-10-0-1-138 4.15.0-1035-aws #37-Ubuntu SMP Mon Mar 18 16:15:14 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux |
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
| Operating System | Distribution | Kernel | Linux ip-10-0-1-222 4.15.0-1035-aws #37-Ubuntu SMP Mon Mar 18 16:15:14 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux |
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
| Operating System | Distribution | Kernel | Linux ip-10-0-1-64 4.15.0-1035-aws #37-Ubuntu SMP Mon Mar 18 16:15:14 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux |


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
|  CBRProxy | 512M | 100 | 500 | 0 | 0 | 1236.86 | 61.06 | 39.99 | 120 | 97.72 | 28.141 |
|  CBRProxy | 512M | 100 | 500 | 30 | 0 | 1768.46 | 42.64 | 14.31 | 98 | 98.36 | 28.08 |
|  CBRProxy | 512M | 100 | 500 | 100 | 0 | 740.97 | 101.67 | 3.5 | 116 | 99.1 | 28.055 |
|  CBRProxy | 512M | 100 | 500 | 500 | 0 | 149.02 | 501.57 | 1.5 | 507 | 99.37 | 28.095 |
|  CBRProxy | 512M | 100 | 500 | 1000 | 0 | 73.9 | 1002.06 | 0.84 | 1003 | 99.57 | 27.924 |
|  CBRProxy | 512M | 100 | 1024 | 0 | 0 | 1188.14 | 63.45 | 39.61 | 164 | 97.62 | 28.15 |
|  CBRProxy | 512M | 100 | 1024 | 30 | 0 | 1395.59 | 54.03 | 20.01 | 102 | 98.31 | 28.164 |
|  CBRProxy | 512M | 100 | 1024 | 100 | 0 | 738.88 | 101.93 | 4.63 | 119 | 98.98 | 28.106 |
|  CBRProxy | 512M | 100 | 1024 | 500 | 0 | 148.96 | 501.87 | 1.87 | 511 | 99.47 | 28.143 |
|  CBRProxy | 512M | 100 | 1024 | 1000 | 0 | 74.01 | 1002.34 | 4.61 | 1007 | 99.44 | 28.122 |
|  CBRProxy | 512M | 100 | 10240 | 0 | 0 | 350 | 215.4 | 89.29 | 495 | 96.75 | 28.08 |
|  CBRProxy | 512M | 100 | 10240 | 30 | 0 | 343.76 | 219.86 | 79.27 | 489 | 96.99 | 24.005 |
|  CBRProxy | 512M | 100 | 10240 | 100 | 0 | 338.35 | 222.92 | 67.62 | 403 | 97.24 | 28.133 |
|  CBRProxy | 512M | 100 | 10240 | 500 | 0 | 148.07 | 505.35 | 17.65 | 555 | 98.98 | 24.049 |
|  CBRProxy | 512M | 100 | 10240 | 1000 | 0 | 73.72 | 1002.64 | 3.14 | 1023 | 99.37 | 28.098 |
|  CBRProxy | 512M | 100 | 102400 | 0 | 0 | 37.19 | 2019.73 | 647.89 | 3791 | 86.11 | 148.821 |
|  CBRProxy | 512M | 100 | 102400 | 30 | 0 | 35.85 | 2065.76 | 638.01 | 3599 | 85.9 | 149.697 |
|  CBRProxy | 512M | 100 | 102400 | 100 | 0 | 36.27 | 2061.78 | 664.35 | 3647 | 86.22 | 149.301 |
|  CBRProxy | 512M | 100 | 102400 | 500 | 0 | 35.92 | 2100.52 | 549.87 | 3407 | 86.33 | 149.65 |
|  CBRProxy | 512M | 100 | 102400 | 1000 | 0 | 36.1 | 2044.42 | 489.22 | 3119 | 88.72 | 141.873 |
|  CBRProxy | 512M | 200 | 500 | 0 | 0 | 1170.38 | 128.53 | 55.41 | 289 | 97.26 | 28.143 |
|  CBRProxy | 512M | 200 | 500 | 30 | 0 | 1283.2 | 117.22 | 45.49 | 215 | 97.17 | 28.215 |
|  CBRProxy | 512M | 200 | 500 | 100 | 0 | 1361.35 | 110.38 | 18.69 | 178 | 98.33 | 28.055 |
|  CBRProxy | 512M | 200 | 500 | 500 | 0 | 296.86 | 502.76 | 4.83 | 531 | 99.26 | 28.086 |
|  CBRProxy | 512M | 200 | 500 | 1000 | 0 | 147.05 | 1002.38 | 2.48 | 1019 | 99.39 | 28.095 |
|  CBRProxy | 512M | 200 | 1024 | 0 | 0 | 1041.35 | 144.42 | 60.35 | 299 | 97.78 | 28.103 |
|  CBRProxy | 512M | 200 | 1024 | 30 | 0 | 1186.35 | 126.79 | 43.03 | 219 | 97.22 | 23.963 |
|  CBRProxy | 512M | 200 | 1024 | 100 | 0 | 1082.03 | 139.02 | 37.82 | 211 | 97.99 | 28.102 |
|  CBRProxy | 512M | 200 | 1024 | 500 | 0 | 296.2 | 503.18 | 5.71 | 539 | 99.2 | 28.015 |
|  CBRProxy | 512M | 200 | 1024 | 1000 | 0 | 147.21 | 1002.51 | 3.18 | 1019 | 99.34 | 28.09 |
|  CBRProxy | 512M | 200 | 10240 | 0 | 0 | 316.12 | 477.77 | 184.55 | 975 | 94.04 | 63.046 |
|  CBRProxy | 512M | 200 | 10240 | 30 | 0 | 308.79 | 484.71 | 189.45 | 887 | 93.96 | 65.822 |
|  CBRProxy | 512M | 200 | 10240 | 100 | 0 | 304.17 | 496.64 | 193.21 | 1011 | 93.81 | 65.025 |
|  CBRProxy | 512M | 200 | 10240 | 500 | 0 | 254.92 | 584.25 | 90.65 | 907 | 96.45 | 53.47 |
|  CBRProxy | 512M | 200 | 10240 | 1000 | 0 | 146.06 | 1007.03 | 83.63 | 1111 | 97.91 | 28.02 |
|  CBRProxy | 512M | 200 | 102400 | 0 | 0 | 28.12 | 5263.98 | 1787.05 | 9599 | 80.5 | 242.358 |
|  CBRProxy | 512M | 200 | 102400 | 30 | 0 | 29.68 | 4873.65 | 1642.97 | 10943 | 80.53 | 232.211 |
|  CBRProxy | 512M | 200 | 102400 | 100 | 0 | 27.26 | 5293.82 | 2242.1 | 10559 | 82.73 | 256.653 |
|  CBRProxy | 512M | 200 | 102400 | 500 | 0 | 26.62 | 5647.9 | 1769.15 | 8767 | 79.24 | 258.457 |
|  CBRProxy | 512M | 200 | 102400 | 1000 | 0 | 28.31 | 5088.52 | 1480.61 | 8959 | 81.27 | 232.183 |
|  CBRProxy | 512M | 500 | 500 | 0 | 0 | 1237.92 | 303.42 | 129.63 | 691 | 95.39 | 69.259 |
|  CBRProxy | 512M | 500 | 500 | 30 | 0 | 1348.13 | 277.92 | 110.06 | 603 | 94.93 | 71.743 |
|  CBRProxy | 512M | 500 | 500 | 100 | 0 | 1173.94 | 320.36 | 116.71 | 683 | 94.83 | 72.272 |
|  CBRProxy | 512M | 500 | 500 | 500 | 0 | 727.42 | 510.97 | 23.29 | 595 | 97.66 | 59.082 |
|  CBRProxy | 512M | 500 | 500 | 1000 | 0 | 366.9 | 1002.91 | 5.71 | 1039 | 98.65 | 28.097 |
|  CBRProxy | 512M | 500 | 1024 | 0 | 0 | 1104.56 | 341.14 | 142.52 | 783 | 94.99 | 70.245 |
|  CBRProxy | 512M | 500 | 1024 | 30 | 0 | 1194.1 | 313.87 | 119.86 | 647 | 94.26 | 71.654 |
|  CBRProxy | 512M | 500 | 1024 | 100 | 0 | 1083.75 | 346.46 | 139.95 | 803 | 94.21 | 71.839 |
|  CBRProxy | 512M | 500 | 1024 | 500 | 0 | 705.06 | 527.63 | 46.68 | 699 | 97.33 | 52.721 |
|  CBRProxy | 512M | 500 | 1024 | 1000 | 0 | 365.95 | 1005.38 | 14.59 | 1079 | 98.53 | 28.119 |
|  CBRProxy | 512M | 500 | 10240 | 0 | 0 | 308.3 | 1214.58 | 429.45 | 2111 | 89.33 | 121.592 |
|  CBRProxy | 512M | 500 | 10240 | 30 | 0 | 300.33 | 1248.16 | 479.43 | 2415 | 88.8 | 120.31 |
|  CBRProxy | 512M | 500 | 10240 | 100 | 0 | 302.31 | 1233.33 | 414.28 | 2191 | 89.52 | 126.159 |
|  CBRProxy | 512M | 500 | 10240 | 500 | 0 | 295.94 | 1239.7 | 320.44 | 2111 | 90.49 | 119.736 |
|  CBRProxy | 512M | 500 | 10240 | 1000 | 0 | 275.15 | 1335.42 | 228.08 | 1903 | 92.54 | 108.707 |
|  CBRProxy | 512M | 500 | 102400 | 0 | 100 | 3.7 | 91227.99 | 13294.94 | 104959 | 33.52 | 461.476 |
|  CBRProxy | 512M | 500 | 102400 | 30 | 100 | 2.74 | 108014.27 | 23850.17 | 120319 | 28.87 | 472.803 |
|  CBRProxy | 512M | 500 | 102400 | 100 | 100 | 1.92 | 120064 | 0 | 120319 | 21.12 | 480.719 |
|  CBRProxy | 512M | 500 | 102400 | 500 | 98.92 | 2.93 | 126382.58 | 15458.21 | 153599 | 28.9 | 471.997 |
|  CBRProxy | 512M | 500 | 102400 | 1000 | 100 | 2.25 | 120064 | 0 | 120319 | 24.84 | 475.644 |
|  CBRProxy | 512M | 1000 | 500 | 0 | 0 | 1154.57 | 649.25 | 238.14 | 1303 | 93.11 | 114.023 |
|  CBRProxy | 512M | 1000 | 500 | 30 | 0 | 1243.65 | 600.14 | 225.28 | 1207 | 92.35 | 115.857 |
|  CBRProxy | 512M | 1000 | 500 | 100 | 0 | 1176.48 | 639.82 | 221.66 | 1319 | 93.56 | 117.69 |
|  CBRProxy | 512M | 1000 | 500 | 500 | 0 | 1106.78 | 672.79 | 125.3 | 1087 | 93.49 | 114.576 |
|  CBRProxy | 512M | 1000 | 500 | 1000 | 0 | 670.47 | 1081.58 | 104.58 | 1415 | 96.17 | 108.693 |
|  CBRProxy | 512M | 1000 | 1024 | 0 | 0 | 1008.9 | 743.51 | 276.2 | 1519 | 92.92 | 116.24 |
|  CBRProxy | 512M | 1000 | 1024 | 30 | 0 | 1018.59 | 734.06 | 275.26 | 1503 | 92.36 | 116.464 |
|  CBRProxy | 512M | 1000 | 1024 | 100 | 0 | 1041.88 | 718.99 | 266.56 | 1591 | 92.94 | 116.281 |
|  CBRProxy | 512M | 1000 | 1024 | 500 | 0 | 1017.64 | 727.52 | 144.29 | 1191 | 93.67 | 120.844 |
|  CBRProxy | 512M | 1000 | 1024 | 1000 | 0 | 699.1 | 1053.59 | 77.3 | 1327 | 96.53 | 116.63 |
|  CBRProxy | 512M | 1000 | 10240 | 0 | 0 | 271.94 | 2727.81 | 983.35 | 5023 | 87.96 | 196.496 |
|  CBRProxy | 512M | 1000 | 10240 | 30 | 0 | 275.68 | 2690.32 | 946.37 | 4895 | 88.6 | 198.561 |
|  CBRProxy | 512M | 1000 | 10240 | 100 | 0 | 274.53 | 2669.57 | 1039.8 | 5823 | 87.88 | 198.007 |
|  CBRProxy | 512M | 1000 | 10240 | 500 | 0 | 279.34 | 2642.11 | 896.9 | 4927 | 87.65 | 182.001 |
|  CBRProxy | 512M | 1000 | 10240 | 1000 | 0 | 273.23 | 2659.58 | 718.12 | 4351 | 91.5 | 193.23 |
|  CBRProxy | 512M | 1000 | 102400 | 0 | 100 | 3.36 | 120098.61 | 185.04 | 121343 | 26.57 | 485.736 |
|  CBRProxy | 512M | 1000 | 102400 | 30 | 100 | 3.32 | 119968.39 | 414.02 | 120831 | 28.83 | 485.137 |
|  CBRProxy | 512M | 1000 | 102400 | 100 | 100 | 3.34 | 120102.83 | 195.6 | 121343 | 29.06 | 483.791 |
|  CBRProxy | 512M | 1000 | 102400 | 500 | 100 | 2.31 | 121390.26 | 6328.76 | 152575 | 26.56 | 484.527 |
|  CBRProxy | 512M | 1000 | 102400 | 1000 | 100 | 2.62 | 128762 | 22000.28 | 180223 | 26.53 | 484.479 |
|  CBRSOAPHeaderProxy | 512M | 100 | 500 | 0 | 0 | 1398.43 | 53.99 | 39.26 | 110 | 98.01 | 28.143 |
|  CBRSOAPHeaderProxy | 512M | 100 | 500 | 30 | 0 | 1626.72 | 46.4 | 17.06 | 99 | 98.5 | 28.083 |
|  CBRSOAPHeaderProxy | 512M | 100 | 500 | 100 | 0 | 736.89 | 102.22 | 6.35 | 144 | 99.1 | 28.152 |
|  CBRSOAPHeaderProxy | 512M | 100 | 500 | 500 | 0 | 149.1 | 501.68 | 1.85 | 511 | 99.39 | 28.067 |
|  CBRSOAPHeaderProxy | 512M | 100 | 500 | 1000 | 0 | 74.04 | 1002.32 | 3.76 | 1007 | 99.53 | 28.136 |
|  CBRSOAPHeaderProxy | 512M | 100 | 1024 | 0 | 0 | 1249.36 | 60.39 | 40.77 | 175 | 98.07 | 28.105 |
|  CBRSOAPHeaderProxy | 512M | 100 | 1024 | 30 | 0 | 1283.78 | 58.73 | 23.48 | 106 | 98.13 | 28.116 |
|  CBRSOAPHeaderProxy | 512M | 100 | 1024 | 100 | 0 | 737.8 | 102.07 | 5.37 | 132 | 99.15 | 28.152 |
|  CBRSOAPHeaderProxy | 512M | 100 | 1024 | 500 | 0 | 148.93 | 501.67 | 2.16 | 509 | 99.49 | 28.106 |
|  CBRSOAPHeaderProxy | 512M | 100 | 1024 | 1000 | 0 | 73.73 | 1002.63 | 6.06 | 1019 | 99.36 | 28.064 |
|  CBRSOAPHeaderProxy | 512M | 100 | 10240 | 0 | 0 | 481.76 | 156.93 | 62.22 | 303 | 97.77 | 28.062 |
|  CBRSOAPHeaderProxy | 512M | 100 | 10240 | 30 | 0 | 484.8 | 155.46 | 51.23 | 297 | 97.94 | 28.091 |
|  CBRSOAPHeaderProxy | 512M | 100 | 10240 | 100 | 0 | 469.7 | 160.12 | 46.55 | 287 | 98.09 | 28.021 |
|  CBRSOAPHeaderProxy | 512M | 100 | 10240 | 500 | 0 | 148.86 | 503.48 | 8.71 | 531 | 99.38 | 28.066 |
|  CBRSOAPHeaderProxy | 512M | 100 | 10240 | 1000 | 0 | 73.82 | 1002.17 | 1.41 | 1011 | 99.43 | 28.115 |
|  CBRSOAPHeaderProxy | 512M | 100 | 102400 | 0 | 0 | 62.82 | 1195.59 | 361.65 | 1991 | 92.51 | 129.586 |
|  CBRSOAPHeaderProxy | 512M | 100 | 102400 | 30 | 0 | 64.79 | 1164.71 | 363.82 | 1999 | 92.78 | 131.479 |
|  CBRSOAPHeaderProxy | 512M | 100 | 102400 | 100 | 0 | 62.01 | 1211.88 | 423.62 | 2399 | 92.59 | 133.568 |
|  CBRSOAPHeaderProxy | 512M | 100 | 102400 | 500 | 0 | 60.29 | 1237.37 | 353.95 | 2111 | 93.02 | 129.733 |
|  CBRSOAPHeaderProxy | 512M | 100 | 102400 | 1000 | 0 | 59.02 | 1250.95 | 377.8 | 2007 | 94.29 | 128.807 |
|  CBRSOAPHeaderProxy | 512M | 200 | 500 | 0 | 0 | 1151.23 | 130.76 | 55.4 | 289 | 97.66 | 28.07 |
|  CBRSOAPHeaderProxy | 512M | 200 | 500 | 30 | 0 | 1427.98 | 105.39 | 29.14 | 204 | 98.03 | 28.092 |
|  CBRSOAPHeaderProxy | 512M | 200 | 500 | 100 | 0 | 1151.49 | 130.33 | 34.92 | 205 | 98.13 | 28.122 |
|  CBRSOAPHeaderProxy | 512M | 200 | 500 | 500 | 0 | 296.8 | 502.65 | 3.86 | 527 | 99.17 | 28.107 |
|  CBRSOAPHeaderProxy | 512M | 200 | 500 | 1000 | 0 | 147 | 1002.24 | 2 | 1015 | 99.42 | 28.064 |
|  CBRSOAPHeaderProxy | 512M | 200 | 1024 | 0 | 0 | 1145.79 | 131.61 | 58.45 | 291 | 97.61 | 28.089 |
|  CBRSOAPHeaderProxy | 512M | 200 | 1024 | 30 | 0 | 1314.22 | 114.31 | 35.17 | 206 | 97.97 | 28.155 |
|  CBRSOAPHeaderProxy | 512M | 200 | 1024 | 100 | 0 | 1063.28 | 141.5 | 41 | 273 | 98.02 | 28.084 |
|  CBRSOAPHeaderProxy | 512M | 200 | 1024 | 500 | 0 | 296.52 | 502.59 | 4.11 | 527 | 99.16 | 28.19 |
|  CBRSOAPHeaderProxy | 512M | 200 | 1024 | 1000 | 0 | 147.1 | 1002.39 | 2.69 | 1023 | 99.35 | 28.075 |
|  CBRSOAPHeaderProxy | 512M | 200 | 10240 | 0 | 0 | 456.27 | 331.08 | 128.45 | 695 | 95.82 | 55.895 |
|  CBRSOAPHeaderProxy | 512M | 200 | 10240 | 30 | 0 | 454.37 | 331.6 | 121.18 | 703 | 96.05 | 55.82 |
|  CBRSOAPHeaderProxy | 512M | 200 | 10240 | 100 | 0 | 442.75 | 339.45 | 113.24 | 683 | 96.29 | 57.929 |
|  CBRSOAPHeaderProxy | 512M | 200 | 10240 | 500 | 0 | 285.71 | 522.27 | 56.33 | 691 | 97.84 | 28.211 |
|  CBRSOAPHeaderProxy | 512M | 200 | 10240 | 1000 | 0 | 146.65 | 1004.59 | 10 | 1063 | 98.97 | 28.076 |
|  CBRSOAPHeaderProxy | 512M | 200 | 102400 | 0 | 0 | 55.87 | 2647.54 | 844.69 | 5023 | 88.73 | 219.751 |
|  CBRSOAPHeaderProxy | 512M | 200 | 102400 | 30 | 0 | 59.8 | 2497.87 | 733.56 | 4079 | 88.76 | 216.677 |
|  CBRSOAPHeaderProxy | 512M | 200 | 102400 | 100 | 0 | 54.34 | 2766.12 | 822.38 | 4735 | 88.84 | 212.84 |
|  CBRSOAPHeaderProxy | 512M | 200 | 102400 | 500 | 0 | 55.75 | 2663.94 | 709.66 | 4223 | 89.19 | 199.621 |
|  CBRSOAPHeaderProxy | 512M | 200 | 102400 | 1000 | 0 | 55.41 | 2635.07 | 707.21 | 4127 | 88.88 | 222.373 |
|  CBRSOAPHeaderProxy | 512M | 500 | 500 | 0 | 0 | 1215.22 | 308.53 | 118.68 | 683 | 94.98 | 70.3 |
|  CBRSOAPHeaderProxy | 512M | 500 | 500 | 30 | 0 | 1241.66 | 302.47 | 121.6 | 699 | 95.13 | 71.77 |
|  CBRSOAPHeaderProxy | 512M | 500 | 500 | 100 | 0 | 1230.39 | 304.79 | 102.96 | 603 | 94.87 | 71.5 |
|  CBRSOAPHeaderProxy | 512M | 500 | 500 | 500 | 0 | 721.65 | 515.26 | 29.68 | 615 | 97.56 | 60.578 |
|  CBRSOAPHeaderProxy | 512M | 500 | 500 | 1000 | 0 | 365.61 | 1004.48 | 11.59 | 1071 | 98.57 | 28.06 |
|  CBRSOAPHeaderProxy | 512M | 500 | 1024 | 0 | 0 | 1183.55 | 316.73 | 130.39 | 703 | 94.14 | 71.407 |
|  CBRSOAPHeaderProxy | 512M | 500 | 1024 | 30 | 0 | 1268.46 | 295.84 | 117.69 | 615 | 94.12 | 70.769 |
|  CBRSOAPHeaderProxy | 512M | 500 | 1024 | 100 | 0 | 1234.98 | 304.16 | 106.91 | 615 | 95.31 | 71.192 |
|  CBRSOAPHeaderProxy | 512M | 500 | 1024 | 500 | 0 | 723.7 | 513.63 | 29.35 | 631 | 97.7 | 61.481 |
|  CBRSOAPHeaderProxy | 512M | 500 | 1024 | 1000 | 0 | 366.5 | 1002.95 | 5.8 | 1031 | 98.87 | 28.112 |
|  CBRSOAPHeaderProxy | 512M | 500 | 10240 | 0 | 0 | 451.68 | 829.46 | 316.81 | 1623 | 91.58 | 115.098 |
|  CBRSOAPHeaderProxy | 512M | 500 | 10240 | 30 | 0 | 446.41 | 837.29 | 327.88 | 1703 | 91.74 | 115.987 |
|  CBRSOAPHeaderProxy | 512M | 500 | 10240 | 100 | 0 | 442.39 | 848.57 | 290 | 1383 | 90.95 | 117.536 |
|  CBRSOAPHeaderProxy | 512M | 500 | 10240 | 500 | 0 | 427.04 | 870.34 | 235.42 | 1591 | 92.62 | 114.267 |
|  CBRSOAPHeaderProxy | 512M | 500 | 10240 | 1000 | 0 | 319.05 | 1161.95 | 190.91 | 1895 | 95.24 | 104.599 |
|  CBRSOAPHeaderProxy | 512M | 500 | 102400 | 0 | 48.69 | 8.2 | 54654.06 | 41956.27 | 104447 | 40.78 | 468.427 |
|  CBRSOAPHeaderProxy | 512M | 500 | 102400 | 30 | 57.72 | 7.84 | 56072.65 | 42548.05 | 102399 | 38.09 | 468.589 |
|  CBRSOAPHeaderProxy | 512M | 500 | 102400 | 100 | 56.31 | 4.47 | 78367.95 | 60746.09 | 178175 | 27.35 | 481.14 |
|  CBRSOAPHeaderProxy | 512M | 500 | 102400 | 500 | 55.09 | 7.61 | 58429.57 | 44114.95 | 108031 | 39.49 | 468.585 |
|  CBRSOAPHeaderProxy | 512M | 500 | 102400 | 1000 | 0 | 25.18 | 14801.51 | 7733.28 | 30847 | 68.02 | 401.486 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 500 | 0 | 0 | 1196.07 | 627.08 | 238.42 | 1279 | 93.97 | 109.971 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 500 | 30 | 0 | 1286.14 | 583.61 | 210.97 | 1111 | 92.68 | 117.065 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 500 | 100 | 0 | 1213.84 | 617.27 | 207.68 | 1199 | 93.58 | 111.585 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 500 | 500 | 0 | 1071.06 | 696.56 | 183.17 | 1303 | 93.51 | 116.132 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 500 | 1000 | 0 | 715.44 | 1028.5 | 54.19 | 1255 | 97.33 | 98.385 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 1024 | 0 | 0 | 1154.12 | 647.84 | 239.59 | 1303 | 92.35 | 119.416 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 1024 | 30 | 0 | 1194.47 | 628.77 | 237.09 | 1303 | 92.17 | 118.729 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 1024 | 100 | 0 | 1164.61 | 641.87 | 203.87 | 1135 | 93.5 | 118.661 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 1024 | 500 | 0 | 1025.74 | 726.76 | 188.86 | 1287 | 93.46 | 116.79 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 1024 | 1000 | 0 | 689.71 | 1062.22 | 89.84 | 1391 | 96.26 | 108.711 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 10240 | 0 | 0 | 426.75 | 1777.17 | 622.25 | 3007 | 90.55 | 194.746 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 10240 | 30 | 0 | 425.95 | 1768.64 | 619.34 | 3311 | 89.37 | 194.694 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 10240 | 100 | 0 | 423.57 | 1767.53 | 680.87 | 3503 | 89.91 | 187.293 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 10240 | 500 | 0 | 416.71 | 1797 | 584.76 | 3183 | 90.83 | 193.911 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 10240 | 1000 | 0 | 392.59 | 1837.86 | 500.06 | 3215 | 92.27 | 187.574 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 102400 | 0 | 100 | 2.93 | 120749.07 | 8590.34 | 151551 | 24.2 | 488.108 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 102400 | 30 | 100 | 3.35 | 60534 | 10035.64 | 85503 | 42.89 | 478.99 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 102400 | 100 | 100 | 3.79 | 79630.33 | 18338.5 | 120319 | 28.22 | 485.036 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 102400 | 500 | 100 | 3.34 | 120064 | 0 | 120319 | 25.93 | 487.862 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 102400 | 1000 | 100 | 3.81 | 107328.45 | 19831.3 | 120319 | 27.4 | 485.406 |
|  CBRTransportHeaderProxy | 512M | 100 | 500 | 0 | 0 | 1889.54 | 39.89 | 36.93 | 100 | 98.08 | 28.118 |
|  CBRTransportHeaderProxy | 512M | 100 | 500 | 30 | 0 | 2247.53 | 33.55 | 6.51 | 65 | 98.77 | 28.12 |
|  CBRTransportHeaderProxy | 512M | 100 | 500 | 100 | 0 | 742.24 | 101.48 | 3.48 | 118 | 99.14 | 28.091 |
|  CBRTransportHeaderProxy | 512M | 100 | 500 | 500 | 0 | 148.98 | 501.66 | 3.28 | 509 | 99.51 | 28.085 |
|  CBRTransportHeaderProxy | 512M | 100 | 500 | 1000 | 0 | 73.99 | 1002.29 | 3.63 | 1007 | 99.33 | 28.074 |
|  CBRTransportHeaderProxy | 512M | 100 | 1024 | 0 | 0 | 1796.08 | 42.01 | 36.92 | 103 | 97.98 | 28.041 |
|  CBRTransportHeaderProxy | 512M | 100 | 1024 | 30 | 0 | 2266.32 | 33.25 | 5.19 | 59 | 98.65 | 28.071 |
|  CBRTransportHeaderProxy | 512M | 100 | 1024 | 100 | 0 | 748.93 | 100.54 | 8.24 | 117 | 99.24 | 28.176 |
|  CBRTransportHeaderProxy | 512M | 100 | 1024 | 500 | 0 | 150.53 | 495.95 | 49.96 | 503 | 99.35 | 28.102 |
|  CBRTransportHeaderProxy | 512M | 100 | 1024 | 1000 | 0 | 73.9 | 1002.15 | 2.01 | 1003 | 99.46 | 28.128 |
|  CBRTransportHeaderProxy | 512M | 100 | 10240 | 0 | 0 | 1284.75 | 58.72 | 38.11 | 109 | 98.43 | 28.178 |
|  CBRTransportHeaderProxy | 512M | 100 | 10240 | 30 | 0 | 1694.92 | 44.45 | 16.4 | 95 | 98.44 | 28.14 |
|  CBRTransportHeaderProxy | 512M | 100 | 10240 | 100 | 0 | 811.78 | 92.73 | 27.22 | 148 | 99.1 | 28.128 |
|  CBRTransportHeaderProxy | 512M | 100 | 10240 | 500 | 0 | 149.59 | 499.9 | 26.98 | 509 | 99.55 | 28.129 |
|  CBRTransportHeaderProxy | 512M | 100 | 10240 | 1000 | 0 | 74.02 | 1001.85 | 21.2 | 1007 | 99.3 | 28.077 |
|  CBRTransportHeaderProxy | 512M | 100 | 102400 | 0 | 0 | 415.44 | 181.8 | 57.48 | 307 | 98.99 | 28.096 |
|  CBRTransportHeaderProxy | 512M | 100 | 102400 | 30 | 0 | 474.52 | 158.67 | 51.12 | 281 | 99.08 | 28.103 |
|  CBRTransportHeaderProxy | 512M | 100 | 102400 | 100 | 0 | 432.14 | 174.28 | 51.35 | 299 | 99.22 | 28.092 |
|  CBRTransportHeaderProxy | 512M | 100 | 102400 | 500 | 0 | 176.62 | 423.33 | 174.72 | 543 | 99.48 | 28.119 |
|  CBRTransportHeaderProxy | 512M | 100 | 102400 | 1000 | 0 | 85.86 | 863.49 | 328.45 | 1039 | 99.42 | 28.124 |
|  CBRTransportHeaderProxy | 512M | 200 | 500 | 0 | 0 | 1686.3 | 89.16 | 41.17 | 195 | 97.68 | 28.09 |
|  CBRTransportHeaderProxy | 512M | 200 | 500 | 30 | 0 | 2172.68 | 69.12 | 24.81 | 118 | 98.04 | 28.087 |
|  CBRTransportHeaderProxy | 512M | 200 | 500 | 100 | 0 | 1444.57 | 104.05 | 10.69 | 166 | 98.61 | 28.062 |
|  CBRTransportHeaderProxy | 512M | 200 | 500 | 500 | 0 | 299.22 | 498.05 | 43.92 | 527 | 99.34 | 28.087 |
|  CBRTransportHeaderProxy | 512M | 200 | 500 | 1000 | 0 | 147.24 | 1002.34 | 2.11 | 1015 | 99.38 | 27.999 |
|  CBRTransportHeaderProxy | 512M | 200 | 1024 | 0 | 0 | 1657.3 | 90.84 | 44.3 | 203 | 97.98 | 28.134 |
|  CBRTransportHeaderProxy | 512M | 200 | 1024 | 30 | 0 | 1958.38 | 76.72 | 25.39 | 121 | 97.96 | 28.122 |
|  CBRTransportHeaderProxy | 512M | 200 | 1024 | 100 | 0 | 1420.61 | 105.73 | 14.07 | 172 | 98.5 | 28.037 |
|  CBRTransportHeaderProxy | 512M | 200 | 1024 | 500 | 0 | 296.94 | 502.04 | 3.33 | 523 | 99.33 | 28.152 |
|  CBRTransportHeaderProxy | 512M | 200 | 1024 | 1000 | 0 | 146.97 | 1002.21 | 1.91 | 1011 | 99.51 | 28.157 |
|  CBRTransportHeaderProxy | 512M | 200 | 10240 | 0 | 0 | 1167.58 | 128.55 | 48.01 | 263 | 98.19 | 24.027 |
|  CBRTransportHeaderProxy | 512M | 200 | 10240 | 30 | 0 | 1364.64 | 110.21 | 30.46 | 201 | 97.95 | 28.037 |
|  CBRTransportHeaderProxy | 512M | 200 | 10240 | 100 | 0 | 1111.59 | 135.08 | 45.05 | 215 | 98.58 | 28.056 |
|  CBRTransportHeaderProxy | 512M | 200 | 10240 | 500 | 0 | 297.68 | 499.95 | 44.1 | 583 | 99.37 | 28.137 |
|  CBRTransportHeaderProxy | 512M | 200 | 10240 | 1000 | 0 | 147.95 | 996.95 | 72.29 | 1019 | 99.37 | 28.123 |
|  CBRTransportHeaderProxy | 512M | 200 | 102400 | 0 | 0 | 343.04 | 437.93 | 114.28 | 779 | 98.94 | 28.083 |
|  CBRTransportHeaderProxy | 512M | 200 | 102400 | 30 | 0 | 396.18 | 379.42 | 94.54 | 611 | 98.97 | 28.126 |
|  CBRTransportHeaderProxy | 512M | 200 | 102400 | 100 | 0 | 355.44 | 422.97 | 112.69 | 715 | 99.13 | 28.154 |
|  CBRTransportHeaderProxy | 512M | 200 | 102400 | 500 | 0 | 334.79 | 444.01 | 195.83 | 695 | 99.24 | 28.161 |
|  CBRTransportHeaderProxy | 512M | 200 | 102400 | 1000 | 0 | 161.51 | 910.59 | 279.99 | 1047 | 99.42 | 28.173 |
|  CBRTransportHeaderProxy | 512M | 500 | 500 | 0 | 0 | 1711.43 | 219.79 | 78.92 | 409 | 96.76 | 28.152 |
|  CBRTransportHeaderProxy | 512M | 500 | 500 | 30 | 0 | 2415.21 | 155.15 | 54.24 | 295 | 96.93 | 28.143 |
|  CBRTransportHeaderProxy | 512M | 500 | 500 | 100 | 0 | 1735.14 | 215.96 | 64.39 | 399 | 97.01 | 28.12 |
|  CBRTransportHeaderProxy | 512M | 500 | 500 | 500 | 0 | 739.95 | 501.79 | 3.82 | 527 | 99.03 | 28.085 |
|  CBRTransportHeaderProxy | 512M | 500 | 500 | 1000 | 0 | 366.99 | 999.84 | 49.6 | 1023 | 99.23 | 28.063 |
|  CBRTransportHeaderProxy | 512M | 500 | 1024 | 0 | 0 | 1802.91 | 208.63 | 76.19 | 405 | 96.73 | 28.013 |
|  CBRTransportHeaderProxy | 512M | 500 | 1024 | 30 | 0 | 1911.17 | 196.09 | 67.05 | 401 | 96.22 | 28.039 |
|  CBRTransportHeaderProxy | 512M | 500 | 1024 | 100 | 0 | 1746.33 | 214.96 | 67.12 | 401 | 96.89 | 28.109 |
|  CBRTransportHeaderProxy | 512M | 500 | 1024 | 500 | 0 | 741.32 | 501.47 | 8.78 | 523 | 98.97 | 24.074 |
|  CBRTransportHeaderProxy | 512M | 500 | 1024 | 1000 | 0 | 367.1 | 1002.54 | 3.94 | 1031 | 99.16 | 28.116 |
|  CBRTransportHeaderProxy | 512M | 500 | 10240 | 0 | 0 | 1244.77 | 302.07 | 92.82 | 523 | 96.93 | 28.103 |
|  CBRTransportHeaderProxy | 512M | 500 | 10240 | 30 | 0 | 1354.23 | 277.33 | 90.29 | 507 | 97.45 | 28.051 |
|  CBRTransportHeaderProxy | 512M | 500 | 10240 | 100 | 0 | 1211.24 | 309.87 | 94.45 | 579 | 97.59 | 28.164 |
|  CBRTransportHeaderProxy | 512M | 500 | 10240 | 500 | 0 | 797.32 | 466.34 | 125.37 | 575 | 98.59 | 28.153 |
|  CBRTransportHeaderProxy | 512M | 500 | 10240 | 1000 | 0 | 368.6 | 997.58 | 69.86 | 1031 | 99.14 | 28.144 |
|  CBRTransportHeaderProxy | 512M | 500 | 102400 | 0 | 0 | 376.7 | 989.02 | 250.32 | 1687 | 98.75 | 28.095 |
|  CBRTransportHeaderProxy | 512M | 500 | 102400 | 30 | 0 | 426.8 | 874.67 | 192.62 | 1399 | 98.79 | 28.074 |
|  CBRTransportHeaderProxy | 512M | 500 | 102400 | 100 | 0 | 355.46 | 1050.32 | 252.17 | 1719 | 98.68 | 28.106 |
|  CBRTransportHeaderProxy | 512M | 500 | 102400 | 500 | 0 | 372.44 | 1000.92 | 230.31 | 1687 | 98.85 | 28.143 |
|  CBRTransportHeaderProxy | 512M | 500 | 102400 | 1000 | 0 | 353.01 | 1038.02 | 323.25 | 1511 | 98.9 | 28.092 |
|  CBRTransportHeaderProxy | 512M | 1000 | 500 | 0 | 0 | 1801.88 | 416.65 | 137.48 | 779 | 96.54 | 28.176 |
|  CBRTransportHeaderProxy | 512M | 1000 | 500 | 30 | 0 | 1699.91 | 442.05 | 165.47 | 815 | 96.23 | 28.135 |
|  CBRTransportHeaderProxy | 512M | 1000 | 500 | 100 | 0 | 1618.15 | 463 | 150.82 | 907 | 96.39 | 28.168 |
|  CBRTransportHeaderProxy | 512M | 1000 | 500 | 500 | 0 | 1454.08 | 511.03 | 24.64 | 595 | 98.06 | 28.044 |
|  CBRTransportHeaderProxy | 512M | 1000 | 500 | 1000 | 0 | 732.52 | 1000.66 | 51.41 | 1047 | 98.96 | 28.155 |
|  CBRTransportHeaderProxy | 512M | 1000 | 1024 | 0 | 0 | 1825.48 | 410.3 | 152.85 | 815 | 95.73 | 24.938 |
|  CBRTransportHeaderProxy | 512M | 1000 | 1024 | 30 | 0 | 1731.92 | 432.74 | 161.36 | 875 | 96.07 | 28.068 |
|  CBRTransportHeaderProxy | 512M | 1000 | 1024 | 100 | 0 | 1619.79 | 462.38 | 141.15 | 887 | 96.32 | 28.101 |
|  CBRTransportHeaderProxy | 512M | 1000 | 1024 | 500 | 0 | 1427.75 | 520.08 | 36.52 | 679 | 97.97 | 28.148 |
|  CBRTransportHeaderProxy | 512M | 1000 | 1024 | 1000 | 0 | 734.56 | 1002.14 | 38 | 1047 | 98.75 | 28.076 |
|  CBRTransportHeaderProxy | 512M | 1000 | 10240 | 0 | 0 | 1235.94 | 606.58 | 189.38 | 1119 | 96.28 | 69.946 |
|  CBRTransportHeaderProxy | 512M | 1000 | 10240 | 30 | 0 | 1230.59 | 608.49 | 175.37 | 1095 | 97.15 | 28.197 |
|  CBRTransportHeaderProxy | 512M | 1000 | 10240 | 100 | 0 | 1163.1 | 643.82 | 184.71 | 1183 | 97.3 | 28.022 |
|  CBRTransportHeaderProxy | 512M | 1000 | 10240 | 500 | 0 | 1206.59 | 614.36 | 189.04 | 1119 | 97.1 | 28.052 |
|  CBRTransportHeaderProxy | 512M | 1000 | 10240 | 1000 | 0 | 750.91 | 977.32 | 159.78 | 1055 | 98.77 | 28.147 |
|  CBRTransportHeaderProxy | 512M | 1000 | 102400 | 0 | 0 | 409 | 1806.96 | 334.05 | 2783 | 98.68 | 28.18 |
|  CBRTransportHeaderProxy | 512M | 1000 | 102400 | 30 | 0 | 395.32 | 1865.3 | 408.17 | 2975 | 98.62 | 28.09 |
|  CBRTransportHeaderProxy | 512M | 1000 | 102400 | 100 | 0 | 377.46 | 1956.08 | 406 | 3103 | 98.69 | 28.152 |
|  CBRTransportHeaderProxy | 512M | 1000 | 102400 | 500 | 0 | 367.89 | 2014.56 | 483.07 | 3279 | 98.56 | 28.103 |
|  CBRTransportHeaderProxy | 512M | 1000 | 102400 | 1000 | 0 | 359.66 | 2057.77 | 433.31 | 3215 | 98.59 | 28.083 |
|  DirectProxy | 512M | 100 | 500 | 0 | 0 | 1911.33 | 38.79 | 36.27 | 99 | 97.88 | 28.123 |
|  DirectProxy | 512M | 100 | 500 | 30 | 0 | 2249.55 | 33.51 | 6.45 | 64 | 98.54 | 28.054 |
|  DirectProxy | 512M | 100 | 500 | 100 | 0 | 741.62 | 101.48 | 3.22 | 117 | 99.19 | 28.076 |
|  DirectProxy | 512M | 100 | 500 | 500 | 0 | 149.11 | 501.41 | 1.43 | 507 | 99.43 | 28.108 |
|  DirectProxy | 512M | 100 | 500 | 1000 | 0 | 73.89 | 1002.04 | 0.61 | 1003 | 99.48 | 28.142 |
|  DirectProxy | 512M | 100 | 1024 | 0 | 0 | 1839.91 | 40.95 | 37.1 | 101 | 97.86 | 28.041 |
|  DirectProxy | 512M | 100 | 1024 | 30 | 0 | 2069.06 | 36.43 | 10.31 | 72 | 98.61 | 28.091 |
|  DirectProxy | 512M | 100 | 1024 | 100 | 0 | 760.06 | 99.1 | 12.11 | 114 | 99.07 | 28.07 |
|  DirectProxy | 512M | 100 | 1024 | 500 | 0 | 149.11 | 501.53 | 1.64 | 511 | 99.36 | 28.081 |
|  DirectProxy | 512M | 100 | 1024 | 1000 | 0 | 74.26 | 999.86 | 46.16 | 1003 | 99.45 | 28.092 |
|  DirectProxy | 512M | 100 | 10240 | 0 | 0 | 1281.82 | 58.81 | 38.01 | 108 | 98.16 | 24.03 |
|  DirectProxy | 512M | 100 | 10240 | 30 | 0 | 1729.47 | 43.56 | 15.88 | 92 | 98.79 | 28.116 |
|  DirectProxy | 512M | 100 | 10240 | 100 | 0 | 767.53 | 98.18 | 19.52 | 147 | 99.04 | 28.103 |
|  DirectProxy | 512M | 100 | 10240 | 500 | 0 | 149.26 | 501.02 | 14.47 | 505 | 99.38 | 28.108 |
|  DirectProxy | 512M | 100 | 10240 | 1000 | 0 | 74.08 | 1002.12 | 1.34 | 1007 | 99.43 | 28.125 |
|  DirectProxy | 512M | 100 | 102400 | 0 | 0 | 452.7 | 166.77 | 50.66 | 295 | 98.96 | 28.153 |
|  DirectProxy | 512M | 100 | 102400 | 30 | 0 | 425.42 | 177.21 | 55.5 | 303 | 98.96 | 28.052 |
|  DirectProxy | 512M | 100 | 102400 | 100 | 0 | 405.34 | 185.97 | 56.89 | 309 | 99.1 | 28.136 |
|  DirectProxy | 512M | 100 | 102400 | 500 | 0 | 158.06 | 474.13 | 109.89 | 539 | 99.45 | 28.125 |
|  DirectProxy | 512M | 100 | 102400 | 1000 | 0 | 79.71 | 929.84 | 256.45 | 1019 | 99.52 | 28.122 |
|  DirectProxy | 512M | 200 | 500 | 0 | 0 | 1662.49 | 90.54 | 43.59 | 199 | 97.43 | 28.167 |
|  DirectProxy | 512M | 200 | 500 | 30 | 0 | 2075.82 | 72.43 | 26.25 | 131 | 97.56 | 28.095 |
|  DirectProxy | 512M | 200 | 500 | 100 | 0 | 1437.32 | 104.38 | 10.71 | 165 | 98.54 | 28.1 |
|  DirectProxy | 512M | 200 | 500 | 500 | 0 | 296.99 | 501.72 | 21.56 | 531 | 99.29 | 28.086 |
|  DirectProxy | 512M | 200 | 500 | 1000 | 0 | 147.22 | 1002.2 | 1.71 | 1015 | 99.6 | 28.113 |
|  DirectProxy | 512M | 200 | 1024 | 0 | 0 | 1603.09 | 93.87 | 43.41 | 198 | 97.41 | 28.021 |
|  DirectProxy | 512M | 200 | 1024 | 30 | 0 | 1834.78 | 81.82 | 26.87 | 160 | 97.88 | 28.091 |
|  DirectProxy | 512M | 200 | 1024 | 100 | 0 | 1443.91 | 103.96 | 10.08 | 160 | 98.47 | 28.127 |
|  DirectProxy | 512M | 200 | 1024 | 500 | 0 | 298.51 | 499.46 | 35.95 | 527 | 99.32 | 28.121 |
|  DirectProxy | 512M | 200 | 1024 | 1000 | 0 | 147.17 | 1002.4 | 2.48 | 1019 | 99.44 | 28.083 |
|  DirectProxy | 512M | 200 | 10240 | 0 | 0 | 1203.19 | 125.26 | 48.96 | 223 | 98.22 | 28.098 |
|  DirectProxy | 512M | 200 | 10240 | 30 | 0 | 1268.38 | 118.27 | 35.97 | 210 | 98.37 | 28.111 |
|  DirectProxy | 512M | 200 | 10240 | 100 | 0 | 1227.99 | 122.31 | 40.09 | 202 | 98.52 | 28.08 |
|  DirectProxy | 512M | 200 | 10240 | 500 | 0 | 312.84 | 472.14 | 120.43 | 559 | 99.09 | 28.088 |
|  DirectProxy | 512M | 200 | 10240 | 1000 | 0 | 70.09 | 1002.18 | 1.36 | 1011 | 99.5 | 28.106 |
|  DirectProxy | 512M | 200 | 102400 | 0 | 0 | 369.7 | 406.8 | 104.39 | 683 | 99.02 | 28.077 |
|  DirectProxy | 512M | 200 | 102400 | 30 | 0 | 381.44 | 395.32 | 103.59 | 683 | 98.99 | 28.123 |
|  DirectProxy | 512M | 200 | 102400 | 100 | 0 | 342.9 | 438.08 | 118.77 | 787 | 98.62 | 28.128 |
|  DirectProxy | 512M | 200 | 102400 | 500 | 0 | 324.69 | 458.11 | 184.68 | 691 | 98.88 | 28.247 |
|  DirectProxy | 512M | 200 | 102400 | 1000 | 0 | 157.42 | 934.57 | 250.28 | 1047 | 99.42 | 28.136 |
|  DirectProxy | 512M | 500 | 500 | 0 | 0 | 1799.44 | 204.41 | 78.05 | 403 | 97.16 | 28.101 |
|  DirectProxy | 512M | 500 | 500 | 30 | 0 | 2184.23 | 171.64 | 59.59 | 315 | 96.85 | 28.132 |
|  DirectProxy | 512M | 500 | 500 | 100 | 0 | 1967.77 | 190.48 | 54.61 | 327 | 96.59 | 28.108 |
|  DirectProxy | 512M | 500 | 500 | 500 | 0 | 742.54 | 500.77 | 21.58 | 527 | 98.96 | 28.09 |
|  DirectProxy | 512M | 500 | 500 | 1000 | 0 | 367.23 | 1001.99 | 21.15 | 1019 | 99.17 | 28.133 |
|  DirectProxy | 512M | 500 | 1024 | 0 | 0 | 1855.43 | 201.92 | 79.12 | 407 | 96.73 | 28.131 |
|  DirectProxy | 512M | 500 | 1024 | 30 | 0 | 2325.43 | 161.25 | 56.17 | 301 | 96.42 | 28.048 |
|  DirectProxy | 512M | 500 | 1024 | 100 | 0 | 2014.18 | 186.01 | 51.18 | 305 | 96.62 | 28.106 |
|  DirectProxy | 512M | 500 | 1024 | 500 | 0 | 734.97 | 503.98 | 12.72 | 567 | 98.8 | 28.052 |
|  DirectProxy | 512M | 500 | 1024 | 1000 | 0 | 367.25 | 1002.43 | 3.24 | 1023 | 99.16 | 28.175 |
|  DirectProxy | 512M | 500 | 10240 | 0 | 0 | 1247.83 | 300.59 | 89.16 | 523 | 97.28 | 28.194 |
|  DirectProxy | 512M | 500 | 10240 | 30 | 0 | 1490.09 | 251.81 | 73.48 | 407 | 97.2 | 28.093 |
|  DirectProxy | 512M | 500 | 10240 | 100 | 0 | 1263.75 | 297.01 | 90.73 | 515 | 97.01 | 28.077 |
|  DirectProxy | 512M | 500 | 10240 | 500 | 0 | 758.33 | 489.38 | 102.78 | 587 | 98.67 | 28.033 |
|  DirectProxy | 512M | 500 | 10240 | 1000 | 0 | 369.36 | 996.95 | 73.02 | 1031 | 99.19 | 28.12 |
|  DirectProxy | 512M | 500 | 102400 | 0 | 0 | 384.25 | 972.17 | 235.97 | 1623 | 99.07 | 24.106 |
|  DirectProxy | 512M | 500 | 102400 | 30 | 0 | 402.69 | 926.94 | 222.49 | 1527 | 98.61 | 28.065 |
|  DirectProxy | 512M | 500 | 102400 | 100 | 0 | 366.18 | 1018.29 | 250.06 | 1695 | 98.48 | 28.092 |
|  DirectProxy | 512M | 500 | 102400 | 500 | 0 | 368.98 | 1007.2 | 212.02 | 1695 | 98.54 | 28.107 |
|  DirectProxy | 512M | 500 | 102400 | 1000 | 0.01 | 345.03 | 1061.8 | 305.14 | 1503 | 99.03 | 28.115 |
|  DirectProxy | 512M | 1000 | 500 | 0 | 0 | 1822.71 | 408.47 | 143.12 | 795 | 95.65 | 28.079 |
|  DirectProxy | 512M | 1000 | 500 | 30 | 0 | 1715.82 | 435.94 | 144.34 | 803 | 96 | 28.19 |
|  DirectProxy | 512M | 1000 | 500 | 100 | 0 | 1576.86 | 473.22 | 139.46 | 815 | 96.16 | 28.005 |
|  DirectProxy | 512M | 1000 | 500 | 500 | 0 | 1423.02 | 521.71 | 36.62 | 679 | 97.7 | 28.089 |
|  DirectProxy | 512M | 1000 | 500 | 1000 | 0 | 732.01 | 1002.61 | 31.28 | 1047 | 98.85 | 28.08 |
|  DirectProxy | 512M | 1000 | 1024 | 0 | 0 | 1597.28 | 468.67 | 172.68 | 923 | 96.31 | 28.102 |
|  DirectProxy | 512M | 1000 | 1024 | 30 | 0 | 1692.05 | 441.61 | 149.06 | 879 | 96.14 | 28.113 |
|  DirectProxy | 512M | 1000 | 1024 | 100 | 0 | 1700.92 | 440.72 | 137.98 | 815 | 96.02 | 28.049 |
|  DirectProxy | 512M | 1000 | 1024 | 500 | 0 | 1453.67 | 510.42 | 21.65 | 587 | 97.95 | 28.054 |
|  DirectProxy | 512M | 1000 | 1024 | 1000 | 0 | 731.88 | 1003.47 | 10.13 | 1055 | 98.75 | 28.153 |
|  DirectProxy | 512M | 1000 | 10240 | 0 | 0 | 1157.31 | 647.62 | 180.61 | 1103 | 96.95 | 28.116 |
|  DirectProxy | 512M | 1000 | 10240 | 30 | 0 | 1175.84 | 634.93 | 186.46 | 1175 | 97.37 | 28.088 |
|  DirectProxy | 512M | 1000 | 10240 | 100 | 0 | 1165.55 | 640.43 | 179.6 | 1095 | 97.18 | 28.192 |
|  DirectProxy | 512M | 1000 | 10240 | 500 | 0 | 1201.3 | 617.65 | 185.81 | 1003 | 97.06 | 28.143 |
|  DirectProxy | 512M | 1000 | 10240 | 1000 | 0 | 761.27 | 965.41 | 205.27 | 1095 | 98.53 | 28.043 |
|  DirectProxy | 512M | 1000 | 102400 | 0 | 0 | 368.94 | 1994.96 | 466.05 | 3231 | 98.61 | 24.06 |
|  DirectProxy | 512M | 1000 | 102400 | 30 | 0 | 418.06 | 1770.11 | 389.84 | 2799 | 98.89 | 28.144 |
|  DirectProxy | 512M | 1000 | 102400 | 100 | 0 | 352.06 | 2071.29 | 517.42 | 3407 | 98.69 | 28.082 |
|  DirectProxy | 512M | 1000 | 102400 | 500 | 0 | 368.09 | 2005.41 | 493.45 | 3311 | 98.34 | 28.081 |
|  DirectProxy | 512M | 1000 | 102400 | 1000 | 0 | 352.73 | 2089.96 | 448.79 | 3503 | 98.71 | 27.993 |
|  XSLTEnhancedProxy | 512M | 100 | 500 | 0 | 0 | 1034.33 | 73.05 | 40.61 | 187 | 98.2 | 28.2 |
|  XSLTEnhancedProxy | 512M | 100 | 500 | 30 | 0 | 1355.03 | 55.64 | 20.58 | 105 | 98.23 | 28.103 |
|  XSLTEnhancedProxy | 512M | 100 | 500 | 100 | 0 | 732.45 | 102.89 | 9.78 | 150 | 98.5 | 39.502 |
|  XSLTEnhancedProxy | 512M | 100 | 500 | 500 | 0 | 148.77 | 501.87 | 2.18 | 515 | 99.35 | 28.145 |
|  XSLTEnhancedProxy | 512M | 100 | 500 | 1000 | 0 | 74.08 | 1002.7 | 6.39 | 1015 | 99.35 | 24.862 |
|  XSLTEnhancedProxy | 512M | 100 | 1024 | 0 | 0 | 893.12 | 84.58 | 44.67 | 198 | 97.19 | 41.323 |
|  XSLTEnhancedProxy | 512M | 100 | 1024 | 30 | 0 | 931.7 | 81.08 | 27.08 | 162 | 98.24 | 39.667 |
|  XSLTEnhancedProxy | 512M | 100 | 1024 | 100 | 0 | 718.42 | 104.91 | 12.28 | 166 | 98.54 | 39.396 |
|  XSLTEnhancedProxy | 512M | 100 | 1024 | 500 | 0 | 148.91 | 501.97 | 2.11 | 515 | 99.33 | 28.088 |
|  XSLTEnhancedProxy | 512M | 100 | 1024 | 1000 | 0 | 73.91 | 1002.07 | 0.78 | 1007 | 99.41 | 28.103 |
|  XSLTEnhancedProxy | 512M | 100 | 10240 | 0 | 0 | 211.63 | 356.49 | 120.76 | 703 | 97.36 | 39.817 |
|  XSLTEnhancedProxy | 512M | 100 | 10240 | 30 | 0 | 213.85 | 352.63 | 124.87 | 707 | 97.72 | 39.473 |
|  XSLTEnhancedProxy | 512M | 100 | 10240 | 100 | 0 | 201.86 | 372.38 | 118.99 | 707 | 97.1 | 39.621 |
|  XSLTEnhancedProxy | 512M | 100 | 10240 | 500 | 0 | 167.47 | 446.6 | 164.83 | 591 | 98.85 | 41.362 |
|  XSLTEnhancedProxy | 512M | 100 | 10240 | 1000 | 0 | 78.88 | 930.06 | 246.6 | 1055 | 99.02 | 40.214 |
|  XSLTEnhancedProxy | 512M | 100 | 102400 | 0 | 0 | 31.59 | 2353.11 | 736.48 | 3903 | 96.77 | 78.986 |
|  XSLTEnhancedProxy | 512M | 100 | 102400 | 30 | 0 | 32.26 | 2278.78 | 702.64 | 3903 | 95.34 | 69.068 |
|  XSLTEnhancedProxy | 512M | 100 | 102400 | 100 | 0 | 32.19 | 2302.7 | 691.41 | 4159 | 95.9 | 78.073 |
|  XSLTEnhancedProxy | 512M | 100 | 102400 | 500 | 0 | 30.78 | 2367.67 | 758.31 | 4479 | 96.6 | 70.919 |
|  XSLTEnhancedProxy | 512M | 100 | 102400 | 1000 | 0 | 31.7 | 2314.93 | 580.51 | 4015 | 96.41 | 60.409 |
|  XSLTEnhancedProxy | 512M | 200 | 500 | 0 | 0 | 916.45 | 164.59 | 75.82 | 409 | 97.44 | 28.048 |
|  XSLTEnhancedProxy | 512M | 200 | 500 | 30 | 0 | 938.32 | 160.35 | 57.01 | 305 | 97 | 46.258 |
|  XSLTEnhancedProxy | 512M | 200 | 500 | 100 | 0 | 1018.37 | 147.28 | 42.06 | 279 | 97.98 | 28.049 |
|  XSLTEnhancedProxy | 512M | 200 | 500 | 500 | 0 | 296.66 | 503.25 | 6.38 | 543 | 99.12 | 28.175 |
|  XSLTEnhancedProxy | 512M | 200 | 500 | 1000 | 0 | 147.11 | 1003.77 | 10.03 | 1079 | 99.16 | 43.674 |
|  XSLTEnhancedProxy | 512M | 200 | 1024 | 0 | 0 | 807.89 | 186.29 | 78.83 | 407 | 97.05 | 28.031 |
|  XSLTEnhancedProxy | 512M | 200 | 1024 | 30 | 0 | 879.96 | 171.07 | 59.38 | 319 | 97.18 | 38.965 |
|  XSLTEnhancedProxy | 512M | 200 | 1024 | 100 | 0 | 804.01 | 186.79 | 51.41 | 303 | 97.73 | 28.076 |
|  XSLTEnhancedProxy | 512M | 200 | 1024 | 500 | 0 | 296.2 | 503.88 | 6.87 | 543 | 98.97 | 28.063 |
|  XSLTEnhancedProxy | 512M | 200 | 1024 | 1000 | 0 | 146.99 | 1002.64 | 3.16 | 1023 | 99.26 | 40.285 |
|  XSLTEnhancedProxy | 512M | 200 | 10240 | 0 | 0 | 197.63 | 756.45 | 237.97 | 1391 | 96.85 | 40.544 |
|  XSLTEnhancedProxy | 512M | 200 | 10240 | 30 | 0 | 202.59 | 739.97 | 253.72 | 1399 | 97.02 | 40.421 |
|  XSLTEnhancedProxy | 512M | 200 | 10240 | 100 | 0 | 194.31 | 768.93 | 227.51 | 1391 | 96.69 | 40.447 |
|  XSLTEnhancedProxy | 512M | 200 | 10240 | 500 | 0 | 191.88 | 776.5 | 188.15 | 1215 | 97.57 | 40.162 |
|  XSLTEnhancedProxy | 512M | 200 | 10240 | 1000 | 0 | 154.53 | 950.48 | 315.94 | 1311 | 98.26 | 40.534 |
|  XSLTEnhancedProxy | 512M | 200 | 102400 | 0 | 0 | 28.54 | 5012.04 | 1255.01 | 7903 | 95.19 | 114.236 |
|  XSLTEnhancedProxy | 512M | 200 | 102400 | 30 | 0 | 29.89 | 4840.3 | 1166.86 | 7327 | 94.35 | 110.543 |
|  XSLTEnhancedProxy | 512M | 200 | 102400 | 100 | 0 | 28.45 | 4984.73 | 1279.34 | 7903 | 95.41 | 113.524 |
|  XSLTEnhancedProxy | 512M | 200 | 102400 | 500 | 0 | 27.99 | 5195.58 | 1438.54 | 8031 | 94.71 | 110.048 |
|  XSLTEnhancedProxy | 512M | 200 | 102400 | 1000 | 0 | 28.51 | 4997.31 | 1334.6 | 7903 | 94.94 | 109.188 |
|  XSLTEnhancedProxy | 512M | 500 | 500 | 0 | 0 | 944.49 | 396.2 | 166.81 | 899 | 94.66 | 83.759 |
|  XSLTEnhancedProxy | 512M | 500 | 500 | 30 | 0 | 1081.86 | 347.56 | 139.93 | 799 | 93.97 | 83.17 |
|  XSLTEnhancedProxy | 512M | 500 | 500 | 100 | 0 | 947.2 | 395.96 | 141.63 | 899 | 94.78 | 78.325 |
|  XSLTEnhancedProxy | 512M | 500 | 500 | 500 | 0 | 712.64 | 522.99 | 39.98 | 703 | 96.76 | 73.381 |
|  XSLTEnhancedProxy | 512M | 500 | 500 | 1000 | 0 | 360.81 | 1012.78 | 26.67 | 1111 | 97.9 | 53.866 |
|  XSLTEnhancedProxy | 512M | 500 | 1024 | 0 | 0 | 835.74 | 448.96 | 167.31 | 927 | 93.77 | 79.402 |
|  XSLTEnhancedProxy | 512M | 500 | 1024 | 30 | 0 | 892.56 | 417.55 | 151.1 | 815 | 94.03 | 79.018 |
|  XSLTEnhancedProxy | 512M | 500 | 1024 | 100 | 0 | 884.08 | 424.26 | 142.66 | 811 | 92.97 | 80.64 |
|  XSLTEnhancedProxy | 512M | 500 | 1024 | 500 | 0 | 636.28 | 584.3 | 105.33 | 1031 | 95.31 | 65.941 |
|  XSLTEnhancedProxy | 512M | 500 | 1024 | 1000 | 0 | 359.48 | 1025.16 | 50.93 | 1287 | 96.8 | 65.602 |
|  XSLTEnhancedProxy | 512M | 500 | 10240 | 0 | 0 | 213.64 | 1749.78 | 516.58 | 3199 | 94.15 | 81.194 |
|  XSLTEnhancedProxy | 512M | 500 | 10240 | 30 | 0 | 209.19 | 1791.01 | 529.03 | 3311 | 94.59 | 84.559 |
|  XSLTEnhancedProxy | 512M | 500 | 10240 | 100 | 0 | 212.15 | 1750.64 | 524.92 | 3295 | 95.16 | 85.351 |
|  XSLTEnhancedProxy | 512M | 500 | 10240 | 500 | 0 | 205.72 | 1792.46 | 491.3 | 3183 | 94.28 | 77.043 |
|  XSLTEnhancedProxy | 512M | 500 | 10240 | 1000 | 0 | 201.9 | 1812.13 | 394.27 | 2879 | 95.45 | 78.002 |
|  XSLTEnhancedProxy | 512M | 500 | 102400 | 0 | 0 | 25.32 | 13642.13 | 3591.5 | 21631 | 90.55 | 234.876 |
|  XSLTEnhancedProxy | 512M | 500 | 102400 | 30 | 0 | 24.43 | 13088.45 | 1869.03 | 18175 | 91.32 | 210.659 |
|  XSLTEnhancedProxy | 512M | 500 | 102400 | 100 | 0 | 24.95 | 12930.08 | 3196.17 | 20991 | 91.81 | 217.282 |
|  XSLTEnhancedProxy | 512M | 500 | 102400 | 500 | 0 | 24.24 | 13256.74 | 2984.63 | 19455 | 92.32 | 219.401 |
|  XSLTEnhancedProxy | 512M | 500 | 102400 | 1000 | 0 | 27.41 | 12289.02 | 2510.93 | 17407 | 91.61 | 202.581 |
|  XSLTEnhancedProxy | 512M | 1000 | 500 | 0 | 0 | 946.06 | 792.84 | 289.82 | 1687 | 92.62 | 114.323 |
|  XSLTEnhancedProxy | 512M | 1000 | 500 | 30 | 0 | 998.11 | 746.08 | 261.7 | 1607 | 92.77 | 114.981 |
|  XSLTEnhancedProxy | 512M | 1000 | 500 | 100 | 0 | 942.95 | 795.08 | 271 | 1511 | 92.03 | 114.884 |
|  XSLTEnhancedProxy | 512M | 1000 | 500 | 500 | 0 | 931.86 | 799.5 | 181.74 | 1319 | 92.6 | 110.175 |
|  XSLTEnhancedProxy | 512M | 1000 | 500 | 1000 | 0 | 658.83 | 1129.54 | 136.58 | 1599 | 94.38 | 107.116 |
|  XSLTEnhancedProxy | 512M | 1000 | 1024 | 0 | 0 | 854.23 | 872.5 | 294.19 | 1711 | 92.97 | 118.135 |
|  XSLTEnhancedProxy | 512M | 1000 | 1024 | 30 | 0 | 874.67 | 854.45 | 290.8 | 1655 | 92.02 | 110.648 |
|  XSLTEnhancedProxy | 512M | 1000 | 1024 | 100 | 0 | 813.9 | 918.19 | 329.28 | 1815 | 92.5 | 113.071 |
|  XSLTEnhancedProxy | 512M | 1000 | 1024 | 500 | 0 | 807.87 | 919.6 | 243.55 | 1583 | 92.49 | 108.245 |
|  XSLTEnhancedProxy | 512M | 1000 | 1024 | 1000 | 0 | 675.71 | 1088.65 | 78.99 | 1311 | 94.74 | 104.398 |
|  XSLTEnhancedProxy | 512M | 1000 | 10240 | 0 | 0 | 207.25 | 3501.25 | 952.17 | 5823 | 94.38 | 116.267 |
|  XSLTEnhancedProxy | 512M | 1000 | 10240 | 30 | 0 | 206.71 | 3493.33 | 1147.69 | 6911 | 95.28 | 125.906 |
|  XSLTEnhancedProxy | 512M | 1000 | 10240 | 100 | 0 | 207.51 | 3506.09 | 967.38 | 6111 | 94.16 | 126.844 |
|  XSLTEnhancedProxy | 512M | 1000 | 10240 | 500 | 0 | 213.28 | 3445.95 | 1078.89 | 6815 | 93.71 | 125.784 |
|  XSLTEnhancedProxy | 512M | 1000 | 10240 | 1000 | 0 | 213.29 | 3395.25 | 903.1 | 6015 | 93.97 | 112.209 |
|  XSLTEnhancedProxy | 512M | 1000 | 102400 | 0 | 0 | 18.89 | 31986.06 | 5573.89 | 40191 | 87.8 | 314.108 |
|  XSLTEnhancedProxy | 512M | 1000 | 102400 | 30 | 0 | 18.37 | 30427 | 4803.5 | 41727 | 87.65 | 315.45 |
|  XSLTEnhancedProxy | 512M | 1000 | 102400 | 100 | 0 | 19.09 | 32905.49 | 8720.03 | 49151 | 86.59 | 338.076 |
|  XSLTEnhancedProxy | 512M | 1000 | 102400 | 500 | 0 | 14.74 | 35530.23 | 3503.9 | 44287 | 88.38 | 310.899 |
|  XSLTEnhancedProxy | 512M | 1000 | 102400 | 1000 | 0 | 19.53 | 31015.21 | 5348.65 | 42239 | 87.82 | 307.699 |
|  XSLTProxy | 512M | 100 | 500 | 0 | 0 | 703.45 | 107.41 | 50.96 | 275 | 97.49 | 40.411 |
|  XSLTProxy | 512M | 100 | 500 | 30 | 0 | 857.55 | 87.9 | 25.13 | 161 | 97.19 | 40.552 |
|  XSLTProxy | 512M | 100 | 500 | 100 | 0 | 628.57 | 119.67 | 28.45 | 194 | 97.71 | 40.582 |
|  XSLTProxy | 512M | 100 | 500 | 500 | 0 | 149.02 | 502.79 | 3.25 | 523 | 99.16 | 40.878 |
|  XSLTProxy | 512M | 100 | 500 | 1000 | 0 | 73.71 | 1007.88 | 21.81 | 1103 | 99.27 | 41.253 |
|  XSLTProxy | 512M | 100 | 1024 | 0 | 0 | 563.03 | 133.95 | 57.42 | 299 | 97.19 | 41.368 |
|  XSLTProxy | 512M | 100 | 1024 | 30 | 0 | 556.68 | 135.78 | 46.01 | 289 | 97.57 | 40.979 |
|  XSLTProxy | 512M | 100 | 1024 | 100 | 0 | 556.94 | 135.38 | 38.64 | 210 | 97.48 | 40.563 |
|  XSLTProxy | 512M | 100 | 1024 | 500 | 0 | 148.85 | 503.61 | 4.76 | 531 | 99.07 | 41.666 |
|  XSLTProxy | 512M | 100 | 1024 | 1000 | 0 | 73.79 | 1002.48 | 2.14 | 1015 | 99.28 | 41.013 |
|  XSLTProxy | 512M | 100 | 10240 | 0 | 0 | 123.07 | 613.21 | 311.26 | 1703 | 96.56 | 86.367 |
|  XSLTProxy | 512M | 100 | 10240 | 30 | 0 | 115.43 | 651.86 | 280.29 | 1511 | 96.33 | 88.889 |
|  XSLTProxy | 512M | 100 | 10240 | 100 | 0 | 118.89 | 630.4 | 245.03 | 1399 | 97.01 | 72.011 |
|  XSLTProxy | 512M | 100 | 10240 | 500 | 0 | 114.81 | 655.3 | 153.65 | 1191 | 97.76 | 44.651 |
|  XSLTProxy | 512M | 100 | 10240 | 1000 | 0 | 73.34 | 1010.75 | 68.84 | 1095 | 98.69 | 43.02 |
|  XSLTProxy | 512M | 100 | 102400 | 0 | 0 | 11.53 | 6201.81 | 1912.08 | 10303 | 91.7 | 158.989 |
|  XSLTProxy | 512M | 100 | 102400 | 30 | 0 | 10.83 | 6415 | 1529.2 | 10111 | 92.42 | 149.584 |
|  XSLTProxy | 512M | 100 | 102400 | 100 | 0 | 11.2 | 6373.77 | 1586.37 | 10111 | 92.17 | 145.643 |
|  XSLTProxy | 512M | 100 | 102400 | 500 | 0 | 11.71 | 6364.63 | 1693.28 | 10623 | 92.04 | 158.6 |
|  XSLTProxy | 512M | 100 | 102400 | 1000 | 0 | 11.42 | 6272.83 | 1654.73 | 11327 | 91.94 | 153.689 |
|  XSLTProxy | 512M | 200 | 500 | 0 | 0 | 637.47 | 235.83 | 89.44 | 499 | 96.85 | 40.836 |
|  XSLTProxy | 512M | 200 | 500 | 30 | 0 | 685.1 | 219.5 | 77.86 | 477 | 96.38 | 41.477 |
|  XSLTProxy | 512M | 200 | 500 | 100 | 0 | 644.47 | 233 | 62.12 | 401 | 96.8 | 41.378 |
|  XSLTProxy | 512M | 200 | 500 | 500 | 0 | 294.13 | 505.75 | 10.1 | 559 | 98.73 | 42.861 |
|  XSLTProxy | 512M | 200 | 500 | 1000 | 0 | 146.84 | 1003.1 | 4.99 | 1031 | 98.88 | 41.107 |
|  XSLTProxy | 512M | 200 | 1024 | 0 | 0 | 516.6 | 292 | 122.77 | 711 | 96.01 | 65.178 |
|  XSLTProxy | 512M | 200 | 1024 | 30 | 0 | 519.57 | 288.49 | 104.92 | 599 | 96.91 | 70.447 |
|  XSLTProxy | 512M | 200 | 1024 | 100 | 0 | 494.34 | 302.9 | 87.78 | 583 | 97.14 | 40.824 |
|  XSLTProxy | 512M | 200 | 1024 | 500 | 0 | 291.68 | 510.95 | 19.41 | 591 | 98.46 | 42.084 |
|  XSLTProxy | 512M | 200 | 1024 | 1000 | 0 | 146.5 | 1003.78 | 6.62 | 1039 | 98.96 | 41.91 |
|  XSLTProxy | 512M | 200 | 10240 | 0 | 0 | 112.18 | 1340 | 561.83 | 3007 | 94.42 | 109.106 |
|  XSLTProxy | 512M | 200 | 10240 | 30 | 0 | 108.85 | 1371.03 | 649.46 | 3519 | 94.66 | 109.565 |
|  XSLTProxy | 512M | 200 | 10240 | 100 | 0 | 107.51 | 1392.56 | 531.62 | 2863 | 94.77 | 104.737 |
|  XSLTProxy | 512M | 200 | 10240 | 500 | 0 | 109.45 | 1340.94 | 422.32 | 2815 | 95 | 104.594 |
|  XSLTProxy | 512M | 200 | 10240 | 1000 | 0 | 105.17 | 1407.98 | 281.01 | 2303 | 95.74 | 97.301 |
|  XSLTProxy | 512M | 200 | 102400 | 0 | 0 | 9.78 | 13635.35 | 2547.65 | 19199 | 88.39 | 247.242 |
|  XSLTProxy | 512M | 200 | 102400 | 30 | 0 | 8.44 | 15210.07 | 4658.86 | 24959 | 86.25 | 284.156 |
|  XSLTProxy | 512M | 200 | 102400 | 100 | 0 | 7.98 | 15666.28 | 3415.71 | 23295 | 87.64 | 279.292 |
|  XSLTProxy | 512M | 200 | 102400 | 500 | 0 | 7.85 | 17132.24 | 4235.84 | 25087 | 88 | 278.625 |
|  XSLTProxy | 512M | 200 | 102400 | 1000 | 0 | 7.8 | 17919.57 | 6625.69 | 30847 | 83.48 | 319.329 |
|  XSLTProxy | 512M | 500 | 500 | 0 | 0 | 634.49 | 587.16 | 222.25 | 1287 | 92.1 | 121.347 |
|  XSLTProxy | 512M | 500 | 500 | 30 | 0 | 687.31 | 546.76 | 196.97 | 1011 | 91.91 | 119.808 |
|  XSLTProxy | 512M | 500 | 500 | 100 | 0 | 655.26 | 572.3 | 203.06 | 1175 | 92.15 | 119.337 |
|  XSLTProxy | 512M | 500 | 500 | 500 | 0 | 585.18 | 630.61 | 107.81 | 987 | 94.04 | 113.119 |
|  XSLTProxy | 512M | 500 | 500 | 1000 | 0 | 353.12 | 1050.84 | 73.79 | 1319 | 96.36 | 101.271 |
|  XSLTProxy | 512M | 500 | 1024 | 0 | 0 | 514.64 | 726.8 | 263.41 | 1527 | 92.33 | 119.909 |
|  XSLTProxy | 512M | 500 | 1024 | 30 | 0 | 541.5 | 692.38 | 264.02 | 1511 | 92.39 | 119.685 |
|  XSLTProxy | 512M | 500 | 1024 | 100 | 0 | 539.4 | 690.38 | 229.3 | 1479 | 92.81 | 116.763 |
|  XSLTProxy | 512M | 500 | 1024 | 500 | 0 | 499.98 | 739.32 | 157.6 | 1207 | 93.93 | 112.146 |
|  XSLTProxy | 512M | 500 | 1024 | 1000 | 0 | 350.06 | 1057.88 | 79.3 | 1311 | 95.76 | 105.002 |
|  XSLTProxy | 512M | 500 | 10240 | 0 | 0 | 103.34 | 3536.62 | 1060.37 | 6527 | 91.84 | 145.619 |
|  XSLTProxy | 512M | 500 | 10240 | 30 | 0 | 112.1 | 3286.99 | 1413.81 | 7711 | 91.74 | 162.834 |
|  XSLTProxy | 512M | 500 | 10240 | 100 | 0 | 105.59 | 3387.53 | 1293.27 | 7391 | 92.78 | 151.246 |
|  XSLTProxy | 512M | 500 | 10240 | 500 | 0 | 107.99 | 3312.28 | 1363.57 | 8319 | 91.47 | 155.759 |
|  XSLTProxy | 512M | 500 | 10240 | 1000 | 0 | 107.16 | 3336.38 | 1086.79 | 6303 | 92.34 | 143.503 |
|  XSLTProxy | 512M | 500 | 102400 | 0 | 100 | 2.26 | 87724.29 | 20327.76 | 120319 | 36.07 | 479.462 |
|  XSLTProxy | 512M | 500 | 102400 | 30 | 100 | 1.95 | 118964.6 | 3383.87 | 120319 | 34.5 | 479.195 |
|  XSLTProxy | 512M | 500 | 102400 | 100 | 100 | 1.96 | 120067.48 | 59.62 | 120319 | 25.3 | 485.321 |
|  XSLTProxy | 512M | 500 | 102400 | 500 | 100 | 2.37 | 120064 | 0 | 120319 | 26.56 | 484.263 |
|  XSLTProxy | 512M | 500 | 102400 | 1000 | 100 | 2.62 | 120064 | 0 | 120319 | 34.47 | 480.157 |
|  XSLTProxy | 512M | 1000 | 500 | 0 | 0 | 588.79 | 1257.37 | 418.75 | 2319 | 91.36 | 150.299 |
|  XSLTProxy | 512M | 1000 | 500 | 30 | 0 | 669.71 | 1114.24 | 369.28 | 2023 | 91.41 | 151.753 |
|  XSLTProxy | 512M | 1000 | 500 | 100 | 0 | 606.39 | 1228.6 | 443.85 | 2607 | 91.2 | 153.438 |
|  XSLTProxy | 512M | 1000 | 500 | 500 | 0 | 626.65 | 1193.59 | 312.49 | 2111 | 91.5 | 152.607 |
|  XSLTProxy | 512M | 1000 | 500 | 1000 | 0 | 559.58 | 1320.86 | 212.59 | 1895 | 92.62 | 146.853 |
|  XSLTProxy | 512M | 1000 | 1024 | 0 | 0 | 492 | 1516.03 | 557.96 | 3295 | 91.44 | 152.485 |
|  XSLTProxy | 512M | 1000 | 1024 | 30 | 0 | 522.31 | 1414.87 | 501.23 | 2815 | 91.51 | 153.863 |
|  XSLTProxy | 512M | 1000 | 1024 | 100 | 0 | 497.29 | 1497.13 | 518.52 | 2767 | 91.81 | 148.043 |
|  XSLTProxy | 512M | 1000 | 1024 | 500 | 0 | 543.95 | 1363.36 | 408.95 | 2719 | 91.97 | 149.156 |
|  XSLTProxy | 512M | 1000 | 1024 | 1000 | 0 | 499.46 | 1451.14 | 286.66 | 2319 | 92.63 | 147.231 |
|  XSLTProxy | 512M | 1000 | 10240 | 0 | 0 | 90.97 | 7722.17 | 2361.72 | 14143 | 90.46 | 240.993 |
|  XSLTProxy | 512M | 1000 | 10240 | 30 | 0 | 98.14 | 7125.69 | 2297.36 | 12351 | 91.31 | 220.999 |
|  XSLTProxy | 512M | 1000 | 10240 | 100 | 0 | 100.74 | 7160.2 | 2591.68 | 15039 | 91.57 | 218.576 |
|  XSLTProxy | 512M | 1000 | 10240 | 500 | 0 | 90.8 | 7383.3 | 3141.02 | 16063 | 90.09 | 237.533 |
|  XSLTProxy | 512M | 1000 | 10240 | 1000 | 0 | 95.45 | 7433.26 | 2954.18 | 15615 | 89.03 | 239.05 |
|  XSLTProxy | 512M | 1000 | 102400 | 0 | 100 | 2.67 | 120064 | 0 | 120319 | 27.93 | 489.501 |
|  XSLTProxy | 512M | 1000 | 102400 | 30 | 100 | 1.85 | 110211.57 | 19336.34 | 123391 | 28.75 | 489.547 |
|  XSLTProxy | 512M | 1000 | 102400 | 100 | 100 | 3.32 | 120055.76 | 64.43 | 120319 | 29.38 | 487.615 |
|  XSLTProxy | 512M | 1000 | 102400 | 500 | 100 | 2.52 | 117950.98 | 7936.94 | 120319 | 30.88 | 488.992 |
|  XSLTProxy | 512M | 1000 | 102400 | 1000 | 100 | 2.13 | 127585.58 | 20707.33 | 186367 | 25.03 | 490.302 |
