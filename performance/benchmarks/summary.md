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
| System | Memory | System memory | 7625 MiB |
| System | Storage | Block Device: nvme0n1 | 8G |
| Operating System | Distribution | Release | Ubuntu 18.04.2 LTS |
| Operating System | Distribution | Kernel | Linux ip-10-0-1-187 4.15.0-1035-aws #37-Ubuntu SMP Mon Mar 18 16:15:14 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux |
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
| Operating System | Distribution | Kernel | Linux ip-10-0-1-8 4.15.0-1035-aws #37-Ubuntu SMP Mon Mar 18 16:15:14 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux |
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
| Operating System | Distribution | Kernel | Linux ip-10-0-1-13 4.15.0-1035-aws #37-Ubuntu SMP Mon Mar 18 16:15:14 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux |
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
| Operating System | Distribution | Kernel | Linux ip-10-0-1-78 4.15.0-1035-aws #37-Ubuntu SMP Mon Mar 18 16:15:14 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux |


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
|  CBRProxy | 512M | 100 | 500 | 0 | 0 | 2121.64 | 35.55 | 35.9 | 122 | 96.87 | 28.107 |
|  CBRProxy | 512M | 100 | 500 | 30 | 0 | 2305.77 | 32.7 | 4.16 | 55 | 98.19 | 28.146 |
|  CBRProxy | 512M | 100 | 500 | 100 | 0 | 744.41 | 101.25 | 1.6 | 111 | 99.12 | 28.032 |
|  CBRProxy | 512M | 100 | 500 | 500 | 0 | 149.05 | 501.57 | 1.12 | 503 | 99.42 | 28.093 |
|  CBRProxy | 512M | 100 | 500 | 1000 | 0 | 74.02 | 1002.26 | 3.38 | 1007 | 99.5 | 28.239 |
|  CBRProxy | 512M | 100 | 1024 | 0 | 0 | 1922.05 | 39.24 | 38.02 | 125 | 97.2 | 28.168 |
|  CBRProxy | 512M | 100 | 1024 | 30 | 0 | 2228.69 | 33.81 | 5.45 | 58 | 98.01 | 28.076 |
|  CBRProxy | 512M | 100 | 1024 | 100 | 0 | 742.7 | 101.36 | 1.75 | 113 | 99.09 | 28.084 |
|  CBRProxy | 512M | 100 | 1024 | 500 | 0 | 148.81 | 501.57 | 1.09 | 503 | 99.48 | 28.121 |
|  CBRProxy | 512M | 100 | 1024 | 1000 | 0 | 73.89 | 1002.04 | 0.62 | 1003 | 99.59 | 28.165 |
|  CBRProxy | 512M | 100 | 10240 | 0 | 0 | 594.26 | 127.17 | 84.23 | 397 | 96.18 | 28.076 |
|  CBRProxy | 512M | 100 | 10240 | 30 | 0 | 647.31 | 116.36 | 42.97 | 234 | 96.42 | 28.085 |
|  CBRProxy | 512M | 100 | 10240 | 100 | 0 | 624.58 | 120.7 | 25.74 | 199 | 97.12 | 28.094 |
|  CBRProxy | 512M | 100 | 10240 | 500 | 0 | 148.71 | 503.38 | 2.72 | 523 | 99.08 | 28.099 |
|  CBRProxy | 512M | 100 | 10240 | 1000 | 0 | 73.98 | 1002.81 | 3.52 | 1023 | 99.3 | 28.149 |
|  CBRProxy | 512M | 100 | 102400 | 0 | 0 | 53.36 | 1402.1 | 520.9 | 2607 | 81.36 | 143.105 |
|  CBRProxy | 512M | 100 | 102400 | 30 | 0 | 52.69 | 1431.46 | 564.35 | 2911 | 81.08 | 148.792 |
|  CBRProxy | 512M | 100 | 102400 | 100 | 0 | 53.37 | 1405.84 | 559.2 | 2831 | 79.98 | 152.669 |
|  CBRProxy | 512M | 100 | 102400 | 500 | 0 | 53.82 | 1400.87 | 445.13 | 2543 | 83.12 | 146.458 |
|  CBRProxy | 512M | 100 | 102400 | 1000 | 0 | 53.43 | 1372.1 | 467.94 | 2383 | 86.28 | 138.03 |
|  CBRProxy | 512M | 200 | 500 | 0 | 0 | 2044.63 | 73.61 | 48.69 | 205 | 96.93 | 28.122 |
|  CBRProxy | 512M | 200 | 500 | 30 | 0 | 2880.03 | 52.2 | 18.04 | 102 | 97.12 | 24.104 |
|  CBRProxy | 512M | 200 | 500 | 100 | 0 | 1472.56 | 102.02 | 2.89 | 118 | 98.51 | 28.144 |
|  CBRProxy | 512M | 200 | 500 | 500 | 0 | 297.33 | 501.5 | 1.46 | 503 | 99.41 | 28.059 |
|  CBRProxy | 512M | 200 | 500 | 1000 | 0 | 146.94 | 1002.14 | 1.42 | 1007 | 99.39 | 28.097 |
|  CBRProxy | 512M | 200 | 1024 | 0 | 0 | 1832.72 | 82.02 | 53.71 | 229 | 96.17 | 28.147 |
|  CBRProxy | 512M | 200 | 1024 | 30 | 0 | 2548.76 | 58.98 | 21.77 | 114 | 96.84 | 28.136 |
|  CBRProxy | 512M | 200 | 1024 | 100 | 0 | 1463.25 | 102.62 | 3.58 | 120 | 98.31 | 28.15 |
|  CBRProxy | 512M | 200 | 1024 | 500 | 0 | 297.25 | 501.78 | 1.97 | 507 | 99.23 | 28.104 |
|  CBRProxy | 512M | 200 | 1024 | 1000 | 0 | 147.21 | 1002.13 | 1.4 | 1007 | 99.44 | 28.157 |
|  CBRProxy | 512M | 200 | 10240 | 0 | 0 | 538.84 | 279.82 | 160.59 | 775 | 92.45 | 71.043 |
|  CBRProxy | 512M | 200 | 10240 | 30 | 0 | 549.12 | 274.23 | 141.62 | 715 | 92.63 | 71.852 |
|  CBRProxy | 512M | 200 | 10240 | 100 | 0 | 528.51 | 284 | 117.18 | 627 | 92.48 | 72.184 |
|  CBRProxy | 512M | 200 | 10240 | 500 | 0 | 293.55 | 508.67 | 40.51 | 583 | 96.95 | 54.5 |
|  CBRProxy | 512M | 200 | 10240 | 1000 | 0 | 146.86 | 1005.25 | 12.68 | 1079 | 98.36 | 28.071 |
|  CBRProxy | 512M | 200 | 102400 | 0 | 0 | 42.18 | 3524.84 | 1382.63 | 7519 | 73.46 | 235.245 |
|  CBRProxy | 512M | 200 | 102400 | 30 | 0 | 43.33 | 3360.77 | 1442.47 | 6623 | 72.83 | 233.813 |
|  CBRProxy | 512M | 200 | 102400 | 100 | 0 | 41.34 | 3639.85 | 1306.02 | 6879 | 73.66 | 228.2 |
|  CBRProxy | 512M | 200 | 102400 | 500 | 0 | 42.79 | 3411.41 | 1228.31 | 7711 | 74.32 | 232.893 |
|  CBRProxy | 512M | 200 | 102400 | 1000 | 0 | 41.14 | 3569.99 | 1290.12 | 6815 | 76.6 | 239.006 |
|  CBRProxy | 512M | 500 | 500 | 0 | 0 | 2052.23 | 183.18 | 99.38 | 479 | 93.19 | 86.061 |
|  CBRProxy | 512M | 500 | 500 | 30 | 0 | 2191.01 | 171.65 | 80.76 | 485 | 92.7 | 89.427 |
|  CBRProxy | 512M | 500 | 500 | 100 | 0 | 2648.46 | 141.54 | 49.75 | 359 | 93.56 | 86.524 |
|  CBRProxy | 512M | 500 | 500 | 500 | 0 | 734.83 | 505.02 | 15.17 | 571 | 98.02 | 61.318 |
|  CBRProxy | 512M | 500 | 500 | 1000 | 0 | 366.63 | 1002.67 | 5.17 | 1031 | 99.04 | 28.146 |
|  CBRProxy | 512M | 500 | 1024 | 0 | 0 | 1858.77 | 202.28 | 105.66 | 507 | 92.66 | 92.596 |
|  CBRProxy | 512M | 500 | 1024 | 30 | 0 | 2111.39 | 177.64 | 76.96 | 411 | 91.87 | 92.724 |
|  CBRProxy | 512M | 500 | 1024 | 100 | 0 | 2317.25 | 161.98 | 61.74 | 397 | 92.85 | 93.513 |
|  CBRProxy | 512M | 500 | 1024 | 500 | 0 | 731.45 | 508.03 | 20.37 | 575 | 97.75 | 61.248 |
|  CBRProxy | 512M | 500 | 1024 | 1000 | 0 | 365.5 | 1002.72 | 5.58 | 1039 | 98.76 | 28.109 |
|  CBRProxy | 512M | 500 | 10240 | 0 | 0 | 506.15 | 740.82 | 355.86 | 1703 | 85.52 | 128.662 |
|  CBRProxy | 512M | 500 | 10240 | 30 | 0 | 480.89 | 775.9 | 350.78 | 1831 | 85.49 | 120.872 |
|  CBRProxy | 512M | 500 | 10240 | 100 | 0 | 482.93 | 775.47 | 324.5 | 1679 | 85.79 | 122.989 |
|  CBRProxy | 512M | 500 | 10240 | 500 | 0 | 482.49 | 767.22 | 206.39 | 1447 | 88.07 | 124.108 |
|  CBRProxy | 512M | 500 | 10240 | 1000 | 0 | 342.98 | 1072.35 | 96.8 | 1431 | 92.19 | 118.578 |
|  CBRProxy | 512M | 500 | 102400 | 0 | 98.76 | 2.1 | 122773.77 | 15535.24 | 161791 | 22.53 | 467.507 |
|  CBRProxy | 512M | 500 | 102400 | 30 | 85.27 | 3.85 | 100496.64 | 40499.03 | 142335 | 24.4 | 464.363 |
|  CBRProxy | 512M | 500 | 102400 | 100 | 97.77 | 1.76 | 123338.98 | 23878.07 | 228351 | 16.02 | 476.45 |
|  CBRProxy | 512M | 500 | 102400 | 500 | 96.53 | 1.91 | 112544.78 | 24018.37 | 164863 | 16.4 | 475.761 |
|  CBRProxy | 512M | 500 | 102400 | 1000 | 87.32 | 2.1 | 118999.35 | 43320.68 | 171007 | 17.06 | 475.017 |
|  CBRProxy | 512M | 1000 | 500 | 0 | 0 | 1931.01 | 386.89 | 186.57 | 919 | 90.79 | 121.084 |
|  CBRProxy | 512M | 1000 | 500 | 30 | 0 | 2061.38 | 363.66 | 162.17 | 879 | 90.47 | 125.701 |
|  CBRProxy | 512M | 1000 | 500 | 100 | 0 | 1919.98 | 390.03 | 151.78 | 827 | 91.17 | 125.959 |
|  CBRProxy | 512M | 1000 | 500 | 500 | 0 | 1345.05 | 549.98 | 68.59 | 807 | 94.95 | 115.217 |
|  CBRProxy | 512M | 1000 | 500 | 1000 | 0 | 719.77 | 1020.6 | 49.06 | 1255 | 97.09 | 110.092 |
|  CBRProxy | 512M | 1000 | 1024 | 0 | 0 | 1684.49 | 445.55 | 221.78 | 1087 | 91.03 | 124.85 |
|  CBRProxy | 512M | 1000 | 1024 | 30 | 0 | 1764.87 | 424.97 | 196.03 | 1003 | 89.89 | 129.063 |
|  CBRProxy | 512M | 1000 | 1024 | 100 | 0 | 1703.32 | 441.38 | 171.27 | 911 | 90.17 | 127.643 |
|  CBRProxy | 512M | 1000 | 1024 | 500 | 0 | 1286.24 | 575.91 | 84.37 | 859 | 94.21 | 123.822 |
|  CBRProxy | 512M | 1000 | 1024 | 1000 | 0 | 711 | 1032.04 | 60.52 | 1263 | 96.63 | 117.705 |
|  CBRProxy | 512M | 1000 | 10240 | 0 | 0 | 410.04 | 1799.87 | 801.47 | 3919 | 85.13 | 189.414 |
|  CBRProxy | 512M | 1000 | 10240 | 30 | 0 | 431.94 | 1727.09 | 780.91 | 3903 | 83.74 | 193.612 |
|  CBRProxy | 512M | 1000 | 10240 | 100 | 0 | 426.3 | 1718.93 | 815.48 | 4223 | 84.64 | 184.156 |
|  CBRProxy | 512M | 1000 | 10240 | 500 | 0 | 421.76 | 1740.37 | 665.81 | 3519 | 85.04 | 192.446 |
|  CBRProxy | 512M | 1000 | 10240 | 1000 | 0 | 403.73 | 1840.85 | 528.03 | 3135 | 86.51 | 199.795 |
|  CBRProxy | 512M | 1000 | 102400 | 0 | 100 | 3.33 | 120957.18 | 2047.44 | 127487 | 25.11 | 479.993 |
|  CBRProxy | 512M | 1000 | 102400 | 30 | 100 | 3.34 | 120501.25 | 1386.72 | 123391 | 22.87 | 480.875 |
|  CBRProxy | 512M | 1000 | 102400 | 100 | 100 | 2.75 | 120188.27 | 483.71 | 123391 | 25.98 | 483.303 |
|  CBRProxy | 512M | 1000 | 102400 | 500 | 100 | 2.85 | 120071.21 | 85.63 | 120319 | 22.64 | 483.47 |
|  CBRProxy | 512M | 1000 | 102400 | 1000 | 100 | 2.73 | 116035.6 | 12243.55 | 120319 | 23.29 | 482.14 |
|  CBRSOAPHeaderProxy | 512M | 100 | 500 | 0 | 0 | 2358.92 | 31.95 | 33.71 | 111 | 97.29 | 28.143 |
|  CBRSOAPHeaderProxy | 512M | 100 | 500 | 30 | 0 | 2350.05 | 32.07 | 3.31 | 47 | 98.29 | 28.065 |
|  CBRSOAPHeaderProxy | 512M | 100 | 500 | 100 | 0 | 744.07 | 101.16 | 1.56 | 111 | 99.19 | 28.13 |
|  CBRSOAPHeaderProxy | 512M | 100 | 500 | 500 | 0 | 148.75 | 501.71 | 1.24 | 503 | 99.52 | 28.043 |
|  CBRSOAPHeaderProxy | 512M | 100 | 500 | 1000 | 0 | 73.92 | 1002.05 | 0.69 | 1003 | 99.49 | 28.13 |
|  CBRSOAPHeaderProxy | 512M | 100 | 1024 | 0 | 0 | 2398.11 | 31.42 | 33.83 | 119 | 97.24 | 28.057 |
|  CBRSOAPHeaderProxy | 512M | 100 | 1024 | 30 | 0 | 2319.92 | 32.5 | 3.57 | 49 | 98.12 | 28.126 |
|  CBRSOAPHeaderProxy | 512M | 100 | 1024 | 100 | 0 | 744.74 | 101.18 | 1.51 | 109 | 99.2 | 28.081 |
|  CBRSOAPHeaderProxy | 512M | 100 | 1024 | 500 | 0 | 149.1 | 501.65 | 1.35 | 503 | 99.49 | 28.071 |
|  CBRSOAPHeaderProxy | 512M | 100 | 1024 | 1000 | 0 | 74.07 | 1002.08 | 0.91 | 1007 | 99.52 | 28.143 |
|  CBRSOAPHeaderProxy | 512M | 100 | 10240 | 0 | 0 | 821.29 | 91.88 | 61.08 | 283 | 97.18 | 28.085 |
|  CBRSOAPHeaderProxy | 512M | 100 | 10240 | 30 | 0 | 840.27 | 89.62 | 25.25 | 162 | 97.83 | 28.11 |
|  CBRSOAPHeaderProxy | 512M | 100 | 10240 | 100 | 0 | 722.93 | 104.13 | 5.9 | 134 | 98.53 | 28.089 |
|  CBRSOAPHeaderProxy | 512M | 100 | 10240 | 500 | 0 | 148.86 | 502.88 | 1.81 | 507 | 99.32 | 28.142 |
|  CBRSOAPHeaderProxy | 512M | 100 | 10240 | 1000 | 0 | 74.3 | 998.86 | 61.78 | 1015 | 99.44 | 28.111 |
|  CBRSOAPHeaderProxy | 512M | 100 | 102400 | 0 | 0 | 95.53 | 791.07 | 303.59 | 1591 | 89.33 | 140.202 |
|  CBRSOAPHeaderProxy | 512M | 100 | 102400 | 30 | 0 | 93.47 | 806.36 | 273.47 | 1503 | 90.02 | 135.53 |
|  CBRSOAPHeaderProxy | 512M | 100 | 102400 | 100 | 0 | 93.38 | 806.57 | 288.06 | 1607 | 89.86 | 135.306 |
|  CBRSOAPHeaderProxy | 512M | 100 | 102400 | 500 | 0 | 90.29 | 825.54 | 255.79 | 1407 | 91.21 | 130.314 |
|  CBRSOAPHeaderProxy | 512M | 100 | 102400 | 1000 | 0 | 78.34 | 942.96 | 392.14 | 1471 | 93.44 | 129.086 |
|  CBRSOAPHeaderProxy | 512M | 200 | 500 | 0 | 0 | 2326.22 | 64.7 | 46 | 197 | 96.26 | 28.044 |
|  CBRSOAPHeaderProxy | 512M | 200 | 500 | 30 | 0 | 3046.5 | 49.35 | 16.58 | 100 | 97.18 | 28.011 |
|  CBRSOAPHeaderProxy | 512M | 200 | 500 | 100 | 0 | 1472.77 | 102.03 | 3.03 | 118 | 98.79 | 28.063 |
|  CBRSOAPHeaderProxy | 512M | 200 | 500 | 500 | 0 | 297.05 | 501.67 | 1.5 | 503 | 99.37 | 28.112 |
|  CBRSOAPHeaderProxy | 512M | 200 | 500 | 1000 | 0 | 147.18 | 1002.08 | 1.05 | 1003 | 99.45 | 28.147 |
|  CBRSOAPHeaderProxy | 512M | 200 | 1024 | 0 | 0 | 2176.31 | 69.19 | 45.72 | 200 | 96.64 | 28.043 |
|  CBRSOAPHeaderProxy | 512M | 200 | 1024 | 30 | 0 | 2802.59 | 53.65 | 18.62 | 103 | 97.14 | 28.067 |
|  CBRSOAPHeaderProxy | 512M | 200 | 1024 | 100 | 0 | 1470.32 | 102.09 | 2.96 | 119 | 98.51 | 28.058 |
|  CBRSOAPHeaderProxy | 512M | 200 | 1024 | 500 | 0 | 297.34 | 501.72 | 1.75 | 507 | 99.35 | 28.113 |
|  CBRSOAPHeaderProxy | 512M | 200 | 1024 | 1000 | 0 | 146.91 | 1002.09 | 1.17 | 1003 | 99.52 | 28.086 |
|  CBRSOAPHeaderProxy | 512M | 200 | 10240 | 0 | 0 | 786.57 | 191.75 | 109.36 | 519 | 94.5 | 65.611 |
|  CBRSOAPHeaderProxy | 512M | 200 | 10240 | 30 | 0 | 807.33 | 186.44 | 82.85 | 429 | 94.53 | 65.292 |
|  CBRSOAPHeaderProxy | 512M | 200 | 10240 | 100 | 0 | 753.65 | 199.19 | 77.54 | 495 | 94.33 | 67.21 |
|  CBRSOAPHeaderProxy | 512M | 200 | 10240 | 500 | 0 | 294.73 | 506.03 | 11.03 | 563 | 98.56 | 28.088 |
|  CBRSOAPHeaderProxy | 512M | 200 | 10240 | 1000 | 0 | 146.93 | 1002.63 | 4.53 | 1031 | 99.09 | 28.107 |
|  CBRSOAPHeaderProxy | 512M | 200 | 102400 | 0 | 0 | 84.84 | 1752.98 | 598.09 | 3295 | 83.72 | 212.328 |
|  CBRSOAPHeaderProxy | 512M | 200 | 102400 | 30 | 0 | 83.64 | 1789.71 | 604.26 | 3359 | 84.44 | 210.103 |
|  CBRSOAPHeaderProxy | 512M | 200 | 102400 | 100 | 0 | 84.94 | 1753.09 | 645.39 | 3423 | 83.6 | 215.119 |
|  CBRSOAPHeaderProxy | 512M | 200 | 102400 | 500 | 0 | 84.31 | 1745.69 | 567.96 | 3199 | 84.45 | 212.452 |
|  CBRSOAPHeaderProxy | 512M | 200 | 102400 | 1000 | 0 | 84.53 | 1755.86 | 510.34 | 2895 | 85.39 | 218.787 |
|  CBRSOAPHeaderProxy | 512M | 500 | 500 | 0 | 0 | 2056.67 | 182.57 | 219.41 | 497 | 93.58 | 82.892 |
|  CBRSOAPHeaderProxy | 512M | 500 | 500 | 30 | 0 | 2183.86 | 171.89 | 74.63 | 403 | 92.98 | 87.706 |
|  CBRSOAPHeaderProxy | 512M | 500 | 500 | 100 | 0 | 2743.2 | 136.82 | 44.27 | 315 | 93.81 | 87.248 |
|  CBRSOAPHeaderProxy | 512M | 500 | 500 | 500 | 0 | 706.01 | 510.41 | 24.61 | 615 | 98.08 | 59.969 |
|  CBRSOAPHeaderProxy | 512M | 500 | 500 | 1000 | 0 | 366.87 | 1002.6 | 4.52 | 1031 | 98.84 | 28.151 |
|  CBRSOAPHeaderProxy | 512M | 500 | 1024 | 0 | 0 | 2068.47 | 181.71 | 97.11 | 481 | 92.77 | 88.733 |
|  CBRSOAPHeaderProxy | 512M | 500 | 1024 | 30 | 0 | 2405.48 | 156.03 | 67.57 | 383 | 92.13 | 93.039 |
|  CBRSOAPHeaderProxy | 512M | 500 | 1024 | 100 | 0 | 2306.03 | 162.55 | 56.58 | 389 | 93.37 | 93.907 |
|  CBRSOAPHeaderProxy | 512M | 500 | 1024 | 500 | 0 | 733.33 | 507.2 | 20.63 | 579 | 98.03 | 60.007 |
|  CBRSOAPHeaderProxy | 512M | 500 | 1024 | 1000 | 0 | 367.17 | 1002.57 | 4.82 | 1031 | 98.97 | 28.018 |
|  CBRSOAPHeaderProxy | 512M | 500 | 10240 | 0 | 0 | 738.42 | 511.25 | 248.36 | 1183 | 89.12 | 125.134 |
|  CBRSOAPHeaderProxy | 512M | 500 | 10240 | 30 | 0 | 738.81 | 506.65 | 236.53 | 1199 | 89.07 | 121.896 |
|  CBRSOAPHeaderProxy | 512M | 500 | 10240 | 100 | 0 | 722.96 | 518.56 | 219.98 | 1111 | 89.1 | 121.612 |
|  CBRSOAPHeaderProxy | 512M | 500 | 10240 | 500 | 0 | 606.6 | 611.74 | 112.94 | 951 | 92.55 | 120.059 |
|  CBRSOAPHeaderProxy | 512M | 500 | 10240 | 1000 | 0 | 360.54 | 1017.47 | 64.1 | 1223 | 96.05 | 112.228 |
|  CBRSOAPHeaderProxy | 512M | 500 | 102400 | 0 | 37.4 | 6.13 | 51946.43 | 56881.54 | 145407 | 22.47 | 477.207 |
|  CBRSOAPHeaderProxy | 512M | 500 | 102400 | 30 | 0 | 38.88 | 9782.88 | 5862.92 | 22655 | 67.32 | 378.413 |
|  CBRSOAPHeaderProxy | 512M | 500 | 102400 | 100 | 48.5 | 6.87 | 61650.56 | 58653.22 | 138239 | 26.86 | 470.855 |
|  CBRSOAPHeaderProxy | 512M | 500 | 102400 | 500 | 0 | 31.92 | 12059.72 | 9509.34 | 32383 | 58.88 | 401.109 |
|  CBRSOAPHeaderProxy | 512M | 500 | 102400 | 1000 | 33.26 | 5.33 | 36423.27 | 46523.77 | 142335 | 18.64 | 480.661 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 500 | 0 | 0 | 1915.61 | 391.17 | 196.6 | 975 | 91.13 | 122.443 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 500 | 30 | 0 | 2027.83 | 369.96 | 160.94 | 807 | 90.91 | 125.13 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 500 | 100 | 0 | 1988.54 | 377.93 | 144.4 | 799 | 91.22 | 123.018 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 500 | 500 | 0 | 1345.32 | 551.77 | 73.28 | 831 | 95.09 | 116.683 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 500 | 1000 | 0 | 718.27 | 1019.28 | 43.82 | 1247 | 97.15 | 110.112 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 1024 | 0 | 0 | 1869.63 | 402.39 | 190.16 | 971 | 90.56 | 128.296 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 1024 | 30 | 0 | 1960.9 | 382.83 | 158.87 | 859 | 90.56 | 125.388 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 1024 | 100 | 0 | 1910.39 | 392.13 | 155.56 | 827 | 90.02 | 128.201 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 1024 | 500 | 0 | 1338.2 | 556.03 | 73.71 | 795 | 94.52 | 123.997 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 1024 | 1000 | 0 | 716.31 | 1022.81 | 46.36 | 1247 | 97.07 | 109.81 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 10240 | 0 | 0 | 648.11 | 1146.57 | 529.71 | 2591 | 87.74 | 173.71 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 10240 | 30 | 0 | 649.73 | 1151.9 | 491.02 | 2527 | 86.87 | 189.968 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 10240 | 100 | 0 | 618.89 | 1210.33 | 499.08 | 2623 | 86.98 | 191.722 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 10240 | 500 | 0 | 624.44 | 1184.67 | 439.97 | 2319 | 87.68 | 197.331 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 10240 | 1000 | 0 | 600.58 | 1235.3 | 208.16 | 1863 | 90.14 | 198.375 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 102400 | 0 | 100 | 1.95 | 115070.63 | 33056.66 | 212991 | 27.15 | 483.309 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 102400 | 30 | 100 | 2.04 | 60189.41 | 53014.71 | 186367 | 38.94 | 473.307 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 102400 | 100 | 100 | 2.86 | 90713.24 | 27305.17 | 185343 | 37.11 | 472.474 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 102400 | 500 | 100 | 2.59 | 120567.11 | 3924.21 | 136191 | 21.55 | 486.549 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 102400 | 1000 | 100 | 2.54 | 113643.68 | 33626.29 | 186367 | 23.78 | 485.395 |
|  CBRTransportHeaderProxy | 512M | 100 | 500 | 0 | 0 | 3081.65 | 24.44 | 31.29 | 108 | 97.11 | 28.104 |
|  CBRTransportHeaderProxy | 512M | 100 | 500 | 30 | 0 | 2400.83 | 31.4 | 2.92 | 45 | 98.51 | 28.146 |
|  CBRTransportHeaderProxy | 512M | 100 | 500 | 100 | 0 | 761.17 | 98.98 | 11.95 | 105 | 99.32 | 28.215 |
|  CBRTransportHeaderProxy | 512M | 100 | 500 | 500 | 0 | 149.43 | 500.32 | 24.58 | 503 | 99.45 | 28.1 |
|  CBRTransportHeaderProxy | 512M | 100 | 500 | 1000 | 0 | 73.92 | 1002.02 | 0.55 | 1003 | 99.5 | 28.103 |
|  CBRTransportHeaderProxy | 512M | 100 | 1024 | 0 | 0 | 3477.17 | 21.64 | 29.13 | 112 | 98.09 | 28.186 |
|  CBRTransportHeaderProxy | 512M | 100 | 1024 | 30 | 0 | 2396.61 | 31.46 | 2.62 | 45 | 98.61 | 28.097 |
|  CBRTransportHeaderProxy | 512M | 100 | 1024 | 100 | 0 | 745.49 | 101.03 | 2.23 | 104 | 99.25 | 28.081 |
|  CBRTransportHeaderProxy | 512M | 100 | 1024 | 500 | 0 | 149.38 | 501.12 | 7.47 | 503 | 99.5 | 28.044 |
|  CBRTransportHeaderProxy | 512M | 100 | 1024 | 1000 | 0 | 73.98 | 1002.05 | 0.92 | 1003 | 99.53 | 28.162 |
|  CBRTransportHeaderProxy | 512M | 100 | 10240 | 0 | 0 | 2066.26 | 36.45 | 33.05 | 103 | 98.08 | 28.097 |
|  CBRTransportHeaderProxy | 512M | 100 | 10240 | 30 | 0 | 2354.12 | 32 | 5.21 | 51 | 98.62 | 28.17 |
|  CBRTransportHeaderProxy | 512M | 100 | 10240 | 100 | 0 | 764.68 | 98.57 | 14.68 | 104 | 99.29 | 28.101 |
|  CBRTransportHeaderProxy | 512M | 100 | 10240 | 500 | 0 | 149.41 | 499.84 | 26.59 | 503 | 99.55 | 28.076 |
|  CBRTransportHeaderProxy | 512M | 100 | 10240 | 1000 | 0 | 74.96 | 990.25 | 107.28 | 1011 | 99.53 | 28.178 |
|  CBRTransportHeaderProxy | 512M | 100 | 102400 | 0 | 0 | 722.05 | 104.38 | 42.76 | 200 | 99.02 | 28.151 |
|  CBRTransportHeaderProxy | 512M | 100 | 102400 | 30 | 0 | 789.95 | 95.18 | 28.2 | 185 | 99.18 | 28.057 |
|  CBRTransportHeaderProxy | 512M | 100 | 102400 | 100 | 0 | 679.47 | 110.72 | 48.25 | 193 | 98.92 | 28.145 |
|  CBRTransportHeaderProxy | 512M | 100 | 102400 | 500 | 0 | 159.02 | 470.2 | 112.87 | 503 | 99.43 | 28.154 |
|  CBRTransportHeaderProxy | 512M | 100 | 102400 | 1000 | 0 | 74.86 | 989.09 | 103.47 | 1007 | 99.54 | 28.024 |
|  CBRTransportHeaderProxy | 512M | 200 | 500 | 0 | 0 | 2996.97 | 50.04 | 42.72 | 189 | 96.66 | 28.07 |
|  CBRTransportHeaderProxy | 512M | 200 | 500 | 30 | 0 | 3740.24 | 40.21 | 11.8 | 81 | 97.69 | 28.042 |
|  CBRTransportHeaderProxy | 512M | 200 | 500 | 100 | 0 | 1481.23 | 101.45 | 3.9 | 117 | 98.86 | 28.152 |
|  CBRTransportHeaderProxy | 512M | 200 | 500 | 500 | 0 | 297.24 | 501.33 | 1.16 | 503 | 99.37 | 28.065 |
|  CBRTransportHeaderProxy | 512M | 200 | 500 | 1000 | 0 | 147.19 | 1002.07 | 1 | 1003 | 99.47 | 28.08 |
|  CBRTransportHeaderProxy | 512M | 200 | 1024 | 0 | 0 | 2901.29 | 51.74 | 42.52 | 181 | 97.02 | 28.106 |
|  CBRTransportHeaderProxy | 512M | 200 | 1024 | 30 | 0 | 3665.7 | 39.99 | 11.47 | 74 | 97.69 | 28.037 |
|  CBRTransportHeaderProxy | 512M | 200 | 1024 | 100 | 0 | 1462.56 | 101.6 | 5 | 119 | 98.89 | 28.168 |
|  CBRTransportHeaderProxy | 512M | 200 | 1024 | 500 | 0 | 297.26 | 501.39 | 1.35 | 503 | 99.41 | 28.121 |
|  CBRTransportHeaderProxy | 512M | 200 | 1024 | 1000 | 0 | 147.28 | 1002.06 | 0.91 | 1003 | 99.48 | 28.047 |
|  CBRTransportHeaderProxy | 512M | 200 | 10240 | 0 | 0 | 1948.84 | 77.02 | 43.06 | 193 | 97.76 | 28.116 |
|  CBRTransportHeaderProxy | 512M | 200 | 10240 | 30 | 0 | 2741 | 54.8 | 20.19 | 103 | 97.93 | 28.086 |
|  CBRTransportHeaderProxy | 512M | 200 | 10240 | 100 | 0 | 1502.21 | 99.96 | 12.51 | 118 | 98.82 | 28.132 |
|  CBRTransportHeaderProxy | 512M | 200 | 10240 | 500 | 0 | 296.72 | 501.57 | 1.33 | 503 | 99.37 | 28.131 |
|  CBRTransportHeaderProxy | 512M | 200 | 10240 | 1000 | 0 | 147.34 | 1001.63 | 20.96 | 1003 | 99.54 | 28.14 |
|  CBRTransportHeaderProxy | 512M | 200 | 102400 | 0 | 0 | 648.84 | 231.82 | 74.66 | 415 | 98.98 | 28.142 |
|  CBRTransportHeaderProxy | 512M | 200 | 102400 | 30 | 0 | 691.4 | 217.12 | 73.3 | 401 | 98.95 | 28.105 |
|  CBRTransportHeaderProxy | 512M | 200 | 102400 | 100 | 0 | 631.15 | 238.11 | 65.1 | 405 | 99.18 | 28.045 |
|  CBRTransportHeaderProxy | 512M | 200 | 102400 | 500 | 0 | 326.36 | 456.86 | 141.13 | 527 | 99.3 | 28.131 |
|  CBRTransportHeaderProxy | 512M | 200 | 102400 | 1000 | 0 | 151.39 | 973.27 | 163.88 | 1007 | 99.46 | 28.121 |
|  CBRTransportHeaderProxy | 512M | 500 | 500 | 0 | 0 | 3170.65 | 118.12 | 70.79 | 365 | 95.52 | 28.254 |
|  CBRTransportHeaderProxy | 512M | 500 | 500 | 30 | 0 | 3840.44 | 97.67 | 30.92 | 195 | 96.18 | 28.042 |
|  CBRTransportHeaderProxy | 512M | 500 | 500 | 100 | 0 | 3371.31 | 110.97 | 16.9 | 175 | 97.03 | 28.111 |
|  CBRTransportHeaderProxy | 512M | 500 | 500 | 500 | 0 | 741.28 | 501.42 | 4.31 | 515 | 99.04 | 28.109 |
|  CBRTransportHeaderProxy | 512M | 500 | 500 | 1000 | 0 | 367.06 | 1002.13 | 1.5 | 1003 | 99.33 | 28.119 |
|  CBRTransportHeaderProxy | 512M | 500 | 1024 | 0 | 0 | 2968.93 | 126.5 | 73.61 | 373 | 95.92 | 28.026 |
|  CBRTransportHeaderProxy | 512M | 500 | 1024 | 30 | 0 | 4067.35 | 92.18 | 34.23 | 197 | 95.67 | 28.09 |
|  CBRTransportHeaderProxy | 512M | 500 | 1024 | 100 | 0 | 3390.52 | 110.57 | 15.74 | 170 | 97.16 | 28.058 |
|  CBRTransportHeaderProxy | 512M | 500 | 1024 | 500 | 0 | 742.59 | 500.68 | 19.69 | 515 | 99.09 | 28.15 |
|  CBRTransportHeaderProxy | 512M | 500 | 1024 | 1000 | 0 | 367.32 | 1002.13 | 1.5 | 1003 | 99.34 | 28.147 |
|  CBRTransportHeaderProxy | 512M | 500 | 10240 | 0 | 0 | 2196.33 | 170.72 | 75.65 | 381 | 96.26 | 28.111 |
|  CBRTransportHeaderProxy | 512M | 500 | 10240 | 30 | 0 | 2499.88 | 149.89 | 51.11 | 293 | 96.79 | 28.115 |
|  CBRTransportHeaderProxy | 512M | 500 | 10240 | 100 | 0 | 2547.58 | 147.19 | 42.48 | 275 | 96.85 | 28.043 |
|  CBRTransportHeaderProxy | 512M | 500 | 10240 | 500 | 0 | 743.89 | 498.27 | 40.15 | 519 | 99.07 | 28.096 |
|  CBRTransportHeaderProxy | 512M | 500 | 10240 | 1000 | 0 | 369.02 | 996 | 76.86 | 1007 | 99.33 | 28.17 |
|  CBRTransportHeaderProxy | 512M | 500 | 102400 | 0 | 0 | 630.68 | 594 | 156.66 | 1019 | 98.86 | 28.14 |
|  CBRTransportHeaderProxy | 512M | 500 | 102400 | 30 | 0 | 637.57 | 587.33 | 158.68 | 1003 | 99.01 | 28.23 |
|  CBRTransportHeaderProxy | 512M | 500 | 102400 | 100 | 0 | 599.62 | 623.06 | 171.3 | 1103 | 98.92 | 28.084 |
|  CBRTransportHeaderProxy | 512M | 500 | 102400 | 500 | 0 | 640.3 | 579.6 | 188.23 | 887 | 98.83 | 28.105 |
|  CBRTransportHeaderProxy | 512M | 500 | 102400 | 1000 | 0 | 414.24 | 886.77 | 325.65 | 1111 | 99.21 | 28.088 |
|  CBRTransportHeaderProxy | 512M | 1000 | 500 | 0 | 0 | 3015.47 | 248.79 | 123.77 | 607 | 95.41 | 28.142 |
|  CBRTransportHeaderProxy | 512M | 1000 | 500 | 30 | 0 | 3017.01 | 248.87 | 104.47 | 567 | 95 | 28.192 |
|  CBRTransportHeaderProxy | 512M | 1000 | 500 | 100 | 0 | 3360.44 | 223.17 | 68.35 | 415 | 95.31 | 28.071 |
|  CBRTransportHeaderProxy | 512M | 1000 | 500 | 500 | 0 | 1477.25 | 503.3 | 18.57 | 547 | 98.41 | 28.09 |
|  CBRTransportHeaderProxy | 512M | 1000 | 500 | 1000 | 0 | 734.57 | 1001.19 | 38.1 | 1031 | 98.97 | 28.111 |
|  CBRTransportHeaderProxy | 512M | 1000 | 1024 | 0 | 0 | 2927.29 | 256.67 | 119.24 | 599 | 94.95 | 28.139 |
|  CBRTransportHeaderProxy | 512M | 1000 | 1024 | 30 | 0 | 3010.36 | 248.83 | 104.11 | 539 | 95.04 | 24.111 |
|  CBRTransportHeaderProxy | 512M | 1000 | 1024 | 100 | 0 | 3504.96 | 214.27 | 69.03 | 411 | 95.07 | 28.119 |
|  CBRTransportHeaderProxy | 512M | 1000 | 1024 | 500 | 0 | 1477.06 | 503.36 | 16 | 547 | 98.37 | 28.017 |
|  CBRTransportHeaderProxy | 512M | 1000 | 1024 | 1000 | 0 | 734.11 | 1002.47 | 13.9 | 1031 | 99.02 | 28.079 |
|  CBRTransportHeaderProxy | 512M | 1000 | 10240 | 0 | 0 | 2015.75 | 372.01 | 144.16 | 783 | 97.18 | 28.069 |
|  CBRTransportHeaderProxy | 512M | 1000 | 10240 | 30 | 0 | 2116.65 | 354.27 | 133.78 | 699 | 96.3 | 28.025 |
|  CBRTransportHeaderProxy | 512M | 1000 | 10240 | 100 | 0 | 2283.18 | 328.56 | 101.65 | 611 | 96.06 | 28.123 |
|  CBRTransportHeaderProxy | 512M | 1000 | 10240 | 500 | 0 | 1496.78 | 495.43 | 72.25 | 563 | 98.3 | 28.141 |
|  CBRTransportHeaderProxy | 512M | 1000 | 10240 | 1000 | 0 | 737.01 | 996.72 | 77.08 | 1039 | 98.97 | 28.096 |
|  CBRTransportHeaderProxy | 512M | 1000 | 102400 | 0 | 0 | 561.76 | 1323.91 | 355.8 | 2319 | 98.5 | 28.139 |
|  CBRTransportHeaderProxy | 512M | 1000 | 102400 | 30 | 0 | 627.01 | 1187.64 | 303.16 | 1999 | 98.6 | 28.085 |
|  CBRTransportHeaderProxy | 512M | 1000 | 102400 | 100 | 0 | 603.82 | 1231.54 | 341.05 | 2191 | 98.35 | 28.117 |
|  CBRTransportHeaderProxy | 512M | 1000 | 102400 | 500 | 0 | 585.25 | 1272.08 | 323.74 | 2191 | 98.58 | 28.065 |
|  CBRTransportHeaderProxy | 512M | 1000 | 102400 | 1000 | 0 | 601.33 | 1217.06 | 359.92 | 1783 | 98.45 | 28.085 |
|  DirectProxy | 512M | 100 | 500 | 0 | 0 | 3361.3 | 22.41 | 29.24 | 102 | 97.04 | 28.187 |
|  DirectProxy | 512M | 100 | 500 | 30 | 0 | 2393.93 | 31.5 | 2.77 | 45 | 98.63 | 28.074 |
|  DirectProxy | 512M | 100 | 500 | 100 | 0 | 752.8 | 100.17 | 9.04 | 112 | 99.25 | 28.096 |
|  DirectProxy | 512M | 100 | 500 | 500 | 0 | 149.2 | 501.38 | 0.91 | 503 | 99.51 | 28.171 |
|  DirectProxy | 512M | 100 | 500 | 1000 | 0 | 74.11 | 1002.02 | 0.34 | 1003 | 99.54 | 28.111 |
|  DirectProxy | 512M | 100 | 1024 | 0 | 0 | 3240.48 | 23.21 | 29.93 | 111 | 97.1 | 28.071 |
|  DirectProxy | 512M | 100 | 1024 | 30 | 0 | 2393.57 | 31.47 | 2.8 | 45 | 98.6 | 24.835 |
|  DirectProxy | 512M | 100 | 1024 | 100 | 0 | 745.82 | 101.01 | 3.12 | 106 | 99.32 | 28.104 |
|  DirectProxy | 512M | 100 | 1024 | 500 | 0 | 149.27 | 501.34 | 0.96 | 503 | 99.52 | 28.077 |
|  DirectProxy | 512M | 100 | 1024 | 1000 | 0 | 74 | 1002.33 | 4.3 | 1003 | 99.45 | 28.092 |
|  DirectProxy | 512M | 100 | 10240 | 0 | 0 | 2083.58 | 36.16 | 33.46 | 106 | 98.3 | 28.098 |
|  DirectProxy | 512M | 100 | 10240 | 30 | 0 | 2350.2 | 32.05 | 4.71 | 50 | 98.59 | 28.085 |
|  DirectProxy | 512M | 100 | 10240 | 100 | 0 | 757.42 | 99.51 | 12.54 | 109 | 99.3 | 28.129 |
|  DirectProxy | 512M | 100 | 10240 | 500 | 0 | 149.47 | 500.62 | 20.88 | 503 | 99.49 | 28.125 |
|  DirectProxy | 512M | 100 | 10240 | 1000 | 0 | 74.01 | 1001.6 | 20.93 | 1003 | 99.53 | 28.08 |
|  DirectProxy | 512M | 100 | 102400 | 0 | 0 | 652.33 | 115.43 | 42.85 | 205 | 98.84 | 28.079 |
|  DirectProxy | 512M | 100 | 102400 | 30 | 0 | 822.7 | 91.26 | 27.29 | 172 | 99.16 | 28.115 |
|  DirectProxy | 512M | 100 | 102400 | 100 | 0.01 | 651.34 | 115.51 | 48.09 | 194 | 99.03 | 28.121 |
|  DirectProxy | 512M | 100 | 102400 | 500 | 0 | 152.48 | 490.14 | 72.99 | 503 | 99.58 | 28.071 |
|  DirectProxy | 512M | 100 | 102400 | 1000 | 0 | 78.76 | 939.99 | 235.18 | 1007 | 99.51 | 28.132 |
|  DirectProxy | 512M | 200 | 500 | 0 | 0 | 3016.11 | 49.92 | 41.39 | 187 | 96.83 | 28.121 |
|  DirectProxy | 512M | 200 | 500 | 30 | 0 | 3829.22 | 39.23 | 10.62 | 72 | 97.62 | 28.042 |
|  DirectProxy | 512M | 200 | 500 | 100 | 0 | 1488.43 | 100.9 | 7.4 | 117 | 98.85 | 28.092 |
|  DirectProxy | 512M | 200 | 500 | 500 | 0 | 296.52 | 501.43 | 1.48 | 503 | 99.41 | 28.138 |
|  DirectProxy | 512M | 200 | 500 | 1000 | 0 | 147.16 | 1002.07 | 1.06 | 1003 | 99.44 | 28.082 |
|  DirectProxy | 512M | 200 | 1024 | 0 | 0 | 3014.22 | 49.91 | 40.97 | 181 | 96.9 | 28.091 |
|  DirectProxy | 512M | 200 | 1024 | 30 | 0 | 3752.98 | 40.05 | 11 | 76 | 97.6 | 28.152 |
|  DirectProxy | 512M | 200 | 1024 | 100 | 0 | 1499.93 | 100.29 | 9.39 | 117 | 98.85 | 28.114 |
|  DirectProxy | 512M | 200 | 1024 | 500 | 0 | 297.31 | 501.36 | 1.19 | 503 | 99.41 | 28.173 |
|  DirectProxy | 512M | 200 | 1024 | 1000 | 0 | 147.23 | 1002.06 | 0.93 | 1003 | 99.53 | 28.083 |
|  DirectProxy | 512M | 200 | 10240 | 0 | 0 | 2156.58 | 69.65 | 44.48 | 199 | 97.37 | 28.173 |
|  DirectProxy | 512M | 200 | 10240 | 30 | 0 | 2761.6 | 54.45 | 20 | 104 | 98 | 28.071 |
|  DirectProxy | 512M | 200 | 10240 | 100 | 0 | 1511.7 | 99.23 | 13.91 | 118 | 98.79 | 28.104 |
|  DirectProxy | 512M | 200 | 10240 | 500 | 0 | 299.75 | 497.08 | 45.39 | 503 | 99.37 | 28.075 |
|  DirectProxy | 512M | 200 | 10240 | 1000 | 0 | 147.79 | 998.37 | 59.73 | 1003 | 99.51 | 28.078 |
|  DirectProxy | 512M | 200 | 102400 | 0 | 0 | 639.71 | 235.57 | 72.74 | 413 | 98.75 | 28.063 |
|  DirectProxy | 512M | 200 | 102400 | 30 | 0 | 662.19 | 226.8 | 71.52 | 403 | 98.67 | 28.067 |
|  DirectProxy | 512M | 200 | 102400 | 100 | 0 | 651.45 | 230.56 | 64.75 | 403 | 98.84 | 28.096 |
|  DirectProxy | 512M | 200 | 102400 | 500 | 0 | 311.08 | 478.99 | 103.35 | 515 | 99.47 | 28.074 |
|  DirectProxy | 512M | 200 | 102400 | 1000 | 0 | 149.18 | 988.68 | 110.11 | 1007 | 99.44 | 28.096 |
|  DirectProxy | 512M | 500 | 500 | 0 | 0 | 3057.08 | 122.94 | 70.45 | 375 | 95.69 | 24.042 |
|  DirectProxy | 512M | 500 | 500 | 30 | 0 | 3929.45 | 95.4 | 29.63 | 190 | 95.85 | 28.076 |
|  DirectProxy | 512M | 500 | 500 | 100 | 0 | 3416.88 | 109.73 | 15.38 | 173 | 97.14 | 28.149 |
|  DirectProxy | 512M | 500 | 500 | 500 | 0 | 740.98 | 501.29 | 3.9 | 509 | 99.14 | 28.093 |
|  DirectProxy | 512M | 500 | 500 | 1000 | 0 | 367.03 | 1001.03 | 40.5 | 1031 | 99.41 | 28.134 |
|  DirectProxy | 512M | 500 | 1024 | 0 | 0 | 3157.59 | 119 | 68.83 | 325 | 96.2 | 28.077 |
|  DirectProxy | 512M | 500 | 1024 | 30 | 0 | 3705.78 | 101.25 | 30.45 | 196 | 95.94 | 28.167 |
|  DirectProxy | 512M | 500 | 1024 | 100 | 0 | 3390.68 | 110.6 | 16.19 | 171 | 97.04 | 28.084 |
|  DirectProxy | 512M | 500 | 1024 | 500 | 0 | 742.85 | 499.92 | 26.25 | 509 | 99.16 | 28.149 |
|  DirectProxy | 512M | 500 | 1024 | 1000 | 0 | 367.29 | 1002.12 | 1.42 | 1003 | 99.35 | 28.125 |
|  DirectProxy | 512M | 500 | 10240 | 0 | 0 | 2201.83 | 170.24 | 77.27 | 393 | 96.63 | 28.147 |
|  DirectProxy | 512M | 500 | 10240 | 30 | 0 | 2456.89 | 152.55 | 53.18 | 299 | 96.62 | 28.033 |
|  DirectProxy | 512M | 500 | 10240 | 100 | 0 | 2631.26 | 141.99 | 41.63 | 263 | 96.99 | 28.069 |
|  DirectProxy | 512M | 500 | 10240 | 500 | 0 | 752.53 | 494.21 | 59.32 | 523 | 99.04 | 28.059 |
|  DirectProxy | 512M | 500 | 10240 | 1000 | 0 | 366.02 | 1001.95 | 12.97 | 1003 | 99.31 | 28.125 |
|  DirectProxy | 512M | 500 | 102400 | 0 | 0 | 642.59 | 581.05 | 155.43 | 1007 | 98.8 | 28.118 |
|  DirectProxy | 512M | 500 | 102400 | 30 | 0 | 640.39 | 584.06 | 153.8 | 995 | 98.66 | 28.13 |
|  DirectProxy | 512M | 500 | 102400 | 100 | 0 | 614.47 | 609.8 | 167.15 | 1095 | 98.53 | 28.17 |
|  DirectProxy | 512M | 500 | 102400 | 500 | 0 | 618.42 | 600.74 | 176.27 | 887 | 99.11 | 28.091 |
|  DirectProxy | 512M | 500 | 102400 | 1000 | 0 | 394.22 | 932.88 | 252.02 | 1031 | 99.22 | 28.121 |
|  DirectProxy | 512M | 1000 | 500 | 0 | 0 | 3000.95 | 250.28 | 126.83 | 611 | 95.07 | 28.07 |
|  DirectProxy | 512M | 1000 | 500 | 30 | 0 | 2979.11 | 251.82 | 110.2 | 563 | 94.65 | 28.132 |
|  DirectProxy | 512M | 1000 | 500 | 100 | 0 | 3083.5 | 243.24 | 73.82 | 427 | 95.13 | 28.107 |
|  DirectProxy | 512M | 1000 | 500 | 500 | 0 | 1481.56 | 502.29 | 23.84 | 543 | 98.64 | 28.051 |
|  DirectProxy | 512M | 1000 | 500 | 1000 | 0 | 734.96 | 1001.57 | 31.2 | 1031 | 99.11 | 28.125 |
|  DirectProxy | 512M | 1000 | 1024 | 0 | 0 | 2943.41 | 254.19 | 114.73 | 591 | 95.22 | 28.13 |
|  DirectProxy | 512M | 1000 | 1024 | 30 | 0 | 3023.49 | 248.44 | 110.03 | 571 | 95.5 | 28.159 |
|  DirectProxy | 512M | 1000 | 1024 | 100 | 0 | 3098.83 | 241.58 | 74.17 | 469 | 95.03 | 28.15 |
|  DirectProxy | 512M | 1000 | 1024 | 500 | 0 | 1476.77 | 502.98 | 22.18 | 547 | 98.42 | 28.09 |
|  DirectProxy | 512M | 1000 | 1024 | 1000 | 0 | 733.86 | 1001.92 | 27.61 | 1031 | 99.17 | 28.082 |
|  DirectProxy | 512M | 1000 | 10240 | 0 | 0 | 2037.01 | 368.5 | 154.54 | 787 | 96.33 | 28.188 |
|  DirectProxy | 512M | 1000 | 10240 | 30 | 0 | 2126.3 | 352.24 | 132.06 | 703 | 96.18 | 28.132 |
|  DirectProxy | 512M | 1000 | 10240 | 100 | 0 | 2349.24 | 319.04 | 94.95 | 579 | 96.42 | 28.122 |
|  DirectProxy | 512M | 1000 | 10240 | 500 | 0 | 1529.26 | 486 | 95.67 | 571 | 98.45 | 28.17 |
|  DirectProxy | 512M | 1000 | 10240 | 1000 | 0 | 740.28 | 994.03 | 92.28 | 1039 | 99.01 | 28.106 |
|  DirectProxy | 512M | 1000 | 102400 | 0 | 0 | 628.8 | 1184.78 | 321.94 | 2111 | 98.56 | 28.073 |
|  DirectProxy | 512M | 1000 | 102400 | 30 | 0 | 607.13 | 1226.93 | 324.03 | 2111 | 98.53 | 28.124 |
|  DirectProxy | 512M | 1000 | 102400 | 100 | 0 | 600.78 | 1238 | 323.31 | 2111 | 98.34 | 28.104 |
|  DirectProxy | 512M | 1000 | 102400 | 500 | 0 | 592.37 | 1253.71 | 310.82 | 2127 | 98.48 | 28.082 |
|  DirectProxy | 512M | 1000 | 102400 | 1000 | 0 | 610.77 | 1200.33 | 320.32 | 1727 | 98.63 | 28.05 |
|  XSLTEnhancedProxy | 512M | 100 | 500 | 0 | 0 | 1737.84 | 43.38 | 42.19 | 193 | 97.08 | 38.728 |
|  XSLTEnhancedProxy | 512M | 100 | 500 | 30 | 0 | 2127.53 | 35.43 | 6.82 | 62 | 97.74 | 38.634 |
|  XSLTEnhancedProxy | 512M | 100 | 500 | 100 | 0 | 744.16 | 101.26 | 1.33 | 110 | 98.96 | 38.752 |
|  XSLTEnhancedProxy | 512M | 100 | 500 | 500 | 0 | 148.89 | 501.64 | 1.26 | 503 | 99.48 | 28.125 |
|  XSLTEnhancedProxy | 512M | 100 | 500 | 1000 | 0 | 73.86 | 1002.48 | 5.15 | 1011 | 99.42 | 39.121 |
|  XSLTEnhancedProxy | 512M | 100 | 1024 | 0 | 0 | 1420.2 | 53.06 | 48.15 | 239 | 97.15 | 28.103 |
|  XSLTEnhancedProxy | 512M | 100 | 1024 | 30 | 0 | 1902.83 | 39.58 | 10.11 | 72 | 97.77 | 28.086 |
|  XSLTEnhancedProxy | 512M | 100 | 1024 | 100 | 0 | 740.27 | 101.79 | 2.13 | 113 | 98.82 | 28.099 |
|  XSLTEnhancedProxy | 512M | 100 | 1024 | 500 | 0 | 148.28 | 502.9 | 7.7 | 539 | 99.27 | 40.345 |
|  XSLTEnhancedProxy | 512M | 100 | 1024 | 1000 | 0 | 74.08 | 1002.78 | 5.94 | 1019 | 99.52 | 28.072 |
|  XSLTEnhancedProxy | 512M | 100 | 10240 | 0 | 0 | 332.74 | 226.75 | 97.52 | 509 | 96.98 | 39.614 |
|  XSLTEnhancedProxy | 512M | 100 | 10240 | 30 | 0 | 334.96 | 225.11 | 83.07 | 473 | 97.36 | 41.035 |
|  XSLTEnhancedProxy | 512M | 100 | 10240 | 100 | 0 | 323.05 | 233.49 | 74.9 | 465 | 97.61 | 24.01 |
|  XSLTEnhancedProxy | 512M | 100 | 10240 | 500 | 0 | 157.21 | 475.67 | 112.54 | 519 | 99.06 | 39.953 |
|  XSLTEnhancedProxy | 512M | 100 | 10240 | 1000 | 0 | 73.75 | 1001.65 | 46.76 | 1015 | 99.16 | 40.391 |
|  XSLTEnhancedProxy | 512M | 100 | 102400 | 0 | 0 | 47.9 | 1548.58 | 581.83 | 3039 | 95.14 | 75.526 |
|  XSLTEnhancedProxy | 512M | 100 | 102400 | 30 | 0 | 47.99 | 1545.03 | 532.17 | 3119 | 95.57 | 73.179 |
|  XSLTEnhancedProxy | 512M | 100 | 102400 | 100 | 0 | 48.17 | 1540.58 | 555.11 | 2879 | 95.62 | 76.134 |
|  XSLTEnhancedProxy | 512M | 100 | 102400 | 500 | 0 | 45.63 | 1608.4 | 462.67 | 2895 | 95.73 | 69.595 |
|  XSLTEnhancedProxy | 512M | 100 | 102400 | 1000 | 0 | 46.05 | 1594.55 | 370.19 | 2527 | 96.51 | 67.459 |
|  XSLTEnhancedProxy | 512M | 200 | 500 | 0 | 0 | 1847.59 | 81.53 | 52.95 | 269 | 95.77 | 44.298 |
|  XSLTEnhancedProxy | 512M | 200 | 500 | 30 | 0 | 2390.25 | 62.96 | 22.61 | 115 | 96.44 | 28.129 |
|  XSLTEnhancedProxy | 512M | 200 | 500 | 100 | 0 | 1460.5 | 103.06 | 7.28 | 122 | 97.93 | 45.423 |
|  XSLTEnhancedProxy | 512M | 200 | 500 | 500 | 0 | 296.84 | 502.45 | 6.17 | 519 | 99.14 | 43.289 |
|  XSLTEnhancedProxy | 512M | 200 | 500 | 1000 | 0 | 147.22 | 1002.36 | 4.55 | 1011 | 99.32 | 41.742 |
|  XSLTEnhancedProxy | 512M | 200 | 1024 | 0 | 0 | 1517.05 | 99.27 | 67.43 | 385 | 96.34 | 44.193 |
|  XSLTEnhancedProxy | 512M | 200 | 1024 | 30 | 0 | 1920.25 | 78.27 | 26.23 | 154 | 96.49 | 45.273 |
|  XSLTEnhancedProxy | 512M | 200 | 1024 | 100 | 0 | 1449.1 | 103.66 | 4.6 | 122 | 97.75 | 39.443 |
|  XSLTEnhancedProxy | 512M | 200 | 1024 | 500 | 0 | 296.69 | 503.23 | 6.62 | 531 | 98.94 | 43.192 |
|  XSLTEnhancedProxy | 512M | 200 | 1024 | 1000 | 0 | 146.91 | 1002.13 | 1.35 | 1007 | 99.43 | 28.083 |
|  XSLTEnhancedProxy | 512M | 200 | 10240 | 0 | 0 | 334.53 | 449 | 184.78 | 987 | 96.23 | 40.337 |
|  XSLTEnhancedProxy | 512M | 200 | 10240 | 30 | 0 | 340.42 | 440.36 | 172.21 | 967 | 95.43 | 39.955 |
|  XSLTEnhancedProxy | 512M | 200 | 10240 | 100 | 0 | 329.73 | 455.56 | 167.08 | 907 | 96.45 | 39.877 |
|  XSLTEnhancedProxy | 512M | 200 | 10240 | 500 | 0 | 300.15 | 496.16 | 177.56 | 779 | 97.2 | 48.406 |
|  XSLTEnhancedProxy | 512M | 200 | 10240 | 1000 | 0 | 155.16 | 951.57 | 217.85 | 1031 | 98.62 | 40.787 |
|  XSLTEnhancedProxy | 512M | 200 | 102400 | 0 | 0 | 47.35 | 3087.27 | 927.3 | 5119 | 93.05 | 111.609 |
|  XSLTEnhancedProxy | 512M | 200 | 102400 | 30 | 0 | 47.22 | 3069.04 | 959.18 | 5535 | 93.01 | 115.147 |
|  XSLTEnhancedProxy | 512M | 200 | 102400 | 100 | 0 | 46.08 | 3148.8 | 973.81 | 5727 | 92.91 | 112.282 |
|  XSLTEnhancedProxy | 512M | 200 | 102400 | 500 | 0 | 47.06 | 3102.08 | 850.06 | 5119 | 93.09 | 109.804 |
|  XSLTEnhancedProxy | 512M | 200 | 102400 | 1000 | 0 | 46.42 | 3155.74 | 817.25 | 5535 | 93.68 | 113.364 |
|  XSLTEnhancedProxy | 512M | 500 | 500 | 0 | 0 | 1636.12 | 230.02 | 148.09 | 739 | 91.15 | 92.723 |
|  XSLTEnhancedProxy | 512M | 500 | 500 | 30 | 0 | 1798.25 | 209.2 | 96.04 | 505 | 90.89 | 89.956 |
|  XSLTEnhancedProxy | 512M | 500 | 500 | 100 | 0 | 2024.08 | 185.36 | 63.99 | 403 | 90.93 | 89.08 |
|  XSLTEnhancedProxy | 512M | 500 | 500 | 500 | 0 | 722.28 | 513.38 | 27.91 | 647 | 97.11 | 71.444 |
|  XSLTEnhancedProxy | 512M | 500 | 500 | 1000 | 0 | 366.88 | 1005.57 | 14.99 | 1071 | 98.03 | 66.39 |
|  XSLTEnhancedProxy | 512M | 500 | 1024 | 0 | 0 | 1448.09 | 259.25 | 165.52 | 847 | 92.11 | 88.943 |
|  XSLTEnhancedProxy | 512M | 500 | 1024 | 30 | 0 | 1485.18 | 252.85 | 110.24 | 587 | 91.65 | 88.026 |
|  XSLTEnhancedProxy | 512M | 500 | 1024 | 100 | 0 | 1577.35 | 237.9 | 79.96 | 477 | 91.11 | 90.023 |
|  XSLTEnhancedProxy | 512M | 500 | 1024 | 500 | 0 | 717.4 | 515.45 | 26.45 | 635 | 96.24 | 76.627 |
|  XSLTEnhancedProxy | 512M | 500 | 1024 | 1000 | 0 | 365.55 | 1004.71 | 14.94 | 1055 | 98.09 | 54.044 |
|  XSLTEnhancedProxy | 512M | 500 | 10240 | 0 | 0 | 330.37 | 1130.75 | 467.98 | 2303 | 93.73 | 91.835 |
|  XSLTEnhancedProxy | 512M | 500 | 10240 | 30 | 0 | 336.36 | 1103.63 | 433.91 | 2335 | 93.05 | 92.041 |
|  XSLTEnhancedProxy | 512M | 500 | 10240 | 100 | 0 | 338.05 | 1101.84 | 406.39 | 2207 | 93.62 | 89.461 |
|  XSLTEnhancedProxy | 512M | 500 | 10240 | 500 | 0 | 315.83 | 1164.23 | 324.21 | 2007 | 93.23 | 79.55 |
|  XSLTEnhancedProxy | 512M | 500 | 10240 | 1000 | 0 | 313.2 | 1171.99 | 336.05 | 1831 | 94.33 | 79.721 |
|  XSLTEnhancedProxy | 512M | 500 | 102400 | 0 | 0 | 42.23 | 7950.78 | 1815.69 | 12031 | 89.63 | 189.75 |
|  XSLTEnhancedProxy | 512M | 500 | 102400 | 30 | 0 | 42.39 | 8418.18 | 2123.71 | 12991 | 90.21 | 209.773 |
|  XSLTEnhancedProxy | 512M | 500 | 102400 | 100 | 0 | 40.23 | 8631.92 | 1925.63 | 13183 | 89.58 | 212.627 |
|  XSLTEnhancedProxy | 512M | 500 | 102400 | 500 | 0 | 41.56 | 8459.54 | 2431.26 | 14463 | 87.09 | 230.803 |
|  XSLTEnhancedProxy | 512M | 500 | 102400 | 1000 | 0 | 42.72 | 7929.76 | 1916.86 | 12607 | 89.74 | 210.819 |
|  XSLTEnhancedProxy | 512M | 1000 | 500 | 0 | 0 | 1489.22 | 503.45 | 262.96 | 1319 | 90.74 | 119.807 |
|  XSLTEnhancedProxy | 512M | 1000 | 500 | 30 | 0 | 1620.55 | 461.71 | 235.21 | 1295 | 89.01 | 117.909 |
|  XSLTEnhancedProxy | 512M | 1000 | 500 | 100 | 0 | 1535.58 | 487.25 | 201.12 | 1183 | 89.89 | 122.119 |
|  XSLTEnhancedProxy | 512M | 1000 | 500 | 500 | 0 | 1329.17 | 559.53 | 65.17 | 775 | 92.79 | 114.697 |
|  XSLTEnhancedProxy | 512M | 1000 | 500 | 1000 | 0 | 709.66 | 1029.74 | 48.71 | 1239 | 95.98 | 104.026 |
|  XSLTEnhancedProxy | 512M | 1000 | 1024 | 0 | 0 | 1272.04 | 588.78 | 283.17 | 1423 | 90.3 | 118.287 |
|  XSLTEnhancedProxy | 512M | 1000 | 1024 | 30 | 0 | 1360.79 | 550.22 | 245.12 | 1295 | 90.35 | 119.029 |
|  XSLTEnhancedProxy | 512M | 1000 | 1024 | 100 | 0 | 1403.69 | 534.16 | 238.32 | 1351 | 89.07 | 120.379 |
|  XSLTEnhancedProxy | 512M | 1000 | 1024 | 500 | 0 | 1286.61 | 577.02 | 78.95 | 863 | 92.4 | 114.202 |
|  XSLTEnhancedProxy | 512M | 1000 | 1024 | 1000 | 0 | 722.67 | 1025.83 | 42.78 | 1223 | 95.59 | 109.343 |
|  XSLTEnhancedProxy | 512M | 1000 | 10240 | 0 | 0 | 302.77 | 2411.75 | 779.99 | 4223 | 93.26 | 135.414 |
|  XSLTEnhancedProxy | 512M | 1000 | 10240 | 30 | 0 | 326.05 | 2238.11 | 777.59 | 4127 | 92.9 | 136.552 |
|  XSLTEnhancedProxy | 512M | 1000 | 10240 | 100 | 0 | 325.74 | 2256.3 | 752.91 | 4255 | 93.01 | 136.816 |
|  XSLTEnhancedProxy | 512M | 1000 | 10240 | 500 | 0 | 331.47 | 2239.91 | 746.71 | 4319 | 92.43 | 133.372 |
|  XSLTEnhancedProxy | 512M | 1000 | 10240 | 1000 | 0 | 326.78 | 2254.07 | 599.1 | 3727 | 93.01 | 126.141 |
|  XSLTEnhancedProxy | 512M | 1000 | 102400 | 0 | 0 | 27.04 | 24091.46 | 8050.64 | 38143 | 84.02 | 331.537 |
|  XSLTEnhancedProxy | 512M | 1000 | 102400 | 30 | 0 | 25.4 | 24266.86 | 5982.27 | 36607 | 83.08 | 331.994 |
|  XSLTEnhancedProxy | 512M | 1000 | 102400 | 100 | 0 | 23.79 | 24774.56 | 3261.43 | 32767 | 86.05 | 322.839 |
|  XSLTEnhancedProxy | 512M | 1000 | 102400 | 500 | 0 | 30.25 | 21503.36 | 5888.55 | 32767 | 83.6 | 313.025 |
|  XSLTEnhancedProxy | 512M | 1000 | 102400 | 1000 | 0 | 25.77 | 22415.33 | 6077.1 | 33791 | 82.49 | 309.732 |
|  XSLTProxy | 512M | 100 | 500 | 0 | 0 | 1207.25 | 62.49 | 53.63 | 269 | 96.45 | 41.35 |
|  XSLTProxy | 512M | 100 | 500 | 30 | 0 | 1546.93 | 48.72 | 15.23 | 96 | 96.84 | 40.231 |
|  XSLTProxy | 512M | 100 | 500 | 100 | 0 | 736.62 | 102.33 | 2.9 | 117 | 98.55 | 40.935 |
|  XSLTProxy | 512M | 100 | 500 | 500 | 0 | 148.78 | 502.37 | 1.82 | 507 | 99.18 | 40.586 |
|  XSLTProxy | 512M | 100 | 500 | 1000 | 0 | 73.88 | 1003.23 | 7.75 | 1055 | 99.25 | 41.392 |
|  XSLTProxy | 512M | 100 | 1024 | 0 | 0 | 947.89 | 79.74 | 61.03 | 293 | 96.75 | 40.979 |
|  XSLTProxy | 512M | 100 | 1024 | 30 | 0 | 1222.48 | 61.74 | 21.62 | 112 | 97.13 | 40.698 |
|  XSLTProxy | 512M | 100 | 1024 | 100 | 0 | 729.24 | 103.26 | 3.58 | 119 | 98.3 | 41.132 |
|  XSLTProxy | 512M | 100 | 1024 | 500 | 0 | 148.27 | 503.06 | 2.06 | 519 | 99.21 | 41.157 |
|  XSLTProxy | 512M | 100 | 1024 | 1000 | 0 | 73.8 | 1002.18 | 1.31 | 1007 | 99.26 | 41.521 |
|  XSLTProxy | 512M | 100 | 10240 | 0 | 0 | 189.26 | 398.18 | 250.85 | 1175 | 95.41 | 99.408 |
|  XSLTProxy | 512M | 100 | 10240 | 30 | 0 | 194.03 | 387.35 | 205.35 | 1095 | 95.61 | 86.228 |
|  XSLTProxy | 512M | 100 | 10240 | 100 | 0 | 181.95 | 414.42 | 180.03 | 943 | 95.45 | 89.869 |
|  XSLTProxy | 512M | 100 | 10240 | 500 | 0 | 142.38 | 526.27 | 37.01 | 683 | 98.01 | 42.588 |
|  XSLTProxy | 512M | 100 | 10240 | 1000 | 0 | 73.23 | 1007.59 | 5.57 | 1039 | 98.71 | 43.44 |
|  XSLTProxy | 512M | 100 | 102400 | 0 | 0 | 17.28 | 4252.09 | 1215.38 | 7135 | 89.22 | 161.152 |
|  XSLTProxy | 512M | 100 | 102400 | 30 | 0 | 17.47 | 4207.31 | 1109.38 | 7135 | 88.3 | 148.613 |
|  XSLTProxy | 512M | 100 | 102400 | 100 | 0 | 16.94 | 4231.87 | 1234.18 | 7327 | 89.27 | 147.304 |
|  XSLTProxy | 512M | 100 | 102400 | 500 | 0 | 16.76 | 4297.16 | 1261.25 | 7519 | 88.6 | 149.011 |
|  XSLTProxy | 512M | 100 | 102400 | 1000 | 0 | 17.68 | 4114.76 | 1147.37 | 7135 | 89.93 | 147.058 |
|  XSLTProxy | 512M | 200 | 500 | 0 | 0 | 1116.88 | 134.84 | 87.61 | 415 | 95.46 | 67.54 |
|  XSLTProxy | 512M | 200 | 500 | 30 | 0 | 1442.3 | 103.87 | 35.91 | 214 | 94.77 | 84.494 |
|  XSLTProxy | 512M | 200 | 500 | 100 | 0 | 1070.1 | 140.39 | 38.42 | 273 | 96.33 | 40.475 |
|  XSLTProxy | 512M | 200 | 500 | 500 | 0 | 296.68 | 502.54 | 2.64 | 519 | 98.92 | 41.364 |
|  XSLTProxy | 512M | 200 | 500 | 1000 | 0 | 147.16 | 1002.23 | 2 | 1011 | 99.09 | 41.747 |
|  XSLTProxy | 512M | 200 | 1024 | 0 | 0 | 948.15 | 158.79 | 93.91 | 481 | 95.18 | 72.628 |
|  XSLTProxy | 512M | 200 | 1024 | 30 | 0 | 1016.38 | 148.03 | 58.73 | 327 | 95.7 | 66.944 |
|  XSLTProxy | 512M | 200 | 1024 | 100 | 0 | 1144.67 | 131.48 | 30.55 | 211 | 96.4 | 40.554 |
|  XSLTProxy | 512M | 200 | 1024 | 500 | 0 | 296.47 | 503 | 2.88 | 523 | 98.87 | 42.064 |
|  XSLTProxy | 512M | 200 | 1024 | 1000 | 0 | 147.09 | 1002.32 | 1.98 | 1011 | 99.05 | 41.928 |
|  XSLTProxy | 512M | 200 | 10240 | 0 | 0 | 187.23 | 802.26 | 490.47 | 2383 | 93.08 | 117.047 |
|  XSLTProxy | 512M | 200 | 10240 | 30 | 0 | 190.25 | 783.88 | 379.31 | 1911 | 92.18 | 123.769 |
|  XSLTProxy | 512M | 200 | 10240 | 100 | 0 | 189.8 | 787.94 | 377.29 | 1895 | 92.34 | 121.171 |
|  XSLTProxy | 512M | 200 | 10240 | 500 | 0 | 173.7 | 858.34 | 250.83 | 1679 | 94.06 | 109.376 |
|  XSLTProxy | 512M | 200 | 10240 | 1000 | 0 | 136.14 | 1079.32 | 99.82 | 1407 | 95.91 | 103.295 |
|  XSLTProxy | 512M | 200 | 102400 | 0 | 0 | 13.88 | 9661.14 | 2501.58 | 16895 | 80.68 | 254.393 |
|  XSLTProxy | 512M | 200 | 102400 | 30 | 0 | 14.2 | 9855.46 | 3182.88 | 16639 | 80.95 | 265.038 |
|  XSLTProxy | 512M | 200 | 102400 | 100 | 0 | 14.72 | 9644.41 | 2769.03 | 16255 | 81.51 | 254.929 |
|  XSLTProxy | 512M | 200 | 102400 | 500 | 0 | 13.89 | 9906.3 | 2664.92 | 16895 | 81.5 | 252.384 |
|  XSLTProxy | 512M | 200 | 102400 | 1000 | 0 | 14.55 | 9554.52 | 2259.31 | 14911 | 81.65 | 248.103 |
|  XSLTProxy | 512M | 500 | 500 | 0 | 0 | 1035.18 | 362.82 | 219.32 | 1391 | 89.62 | 130.858 |
|  XSLTProxy | 512M | 500 | 500 | 30 | 0 | 1141.24 | 328.63 | 160.92 | 811 | 88.42 | 131.773 |
|  XSLTProxy | 512M | 500 | 500 | 100 | 0 | 1118.56 | 334.61 | 136.18 | 787 | 88.99 | 130.213 |
|  XSLTProxy | 512M | 500 | 500 | 500 | 0 | 666.95 | 560.47 | 75.11 | 775 | 94.74 | 115.472 |
|  XSLTProxy | 512M | 500 | 500 | 1000 | 0 | 354.21 | 1027.22 | 57.03 | 1271 | 96.85 | 101.953 |
|  XSLTProxy | 512M | 500 | 1024 | 0 | 0 | 840.29 | 445.53 | 219.71 | 1071 | 89.13 | 128.654 |
|  XSLTProxy | 512M | 500 | 1024 | 30 | 0 | 931.95 | 402.1 | 194.33 | 1047 | 89.01 | 130.382 |
|  XSLTProxy | 512M | 500 | 1024 | 100 | 0 | 949.86 | 394.6 | 167.36 | 947 | 89.08 | 128.499 |
|  XSLTProxy | 512M | 500 | 1024 | 500 | 0 | 665.12 | 558.01 | 65.27 | 787 | 93.93 | 114.92 |
|  XSLTProxy | 512M | 500 | 1024 | 1000 | 0 | 358.51 | 1024.26 | 44.71 | 1231 | 96.4 | 102.675 |
|  XSLTProxy | 512M | 500 | 10240 | 0 | 0 | 178.17 | 2047.09 | 1058.42 | 5215 | 88.21 | 166.345 |
|  XSLTProxy | 512M | 500 | 10240 | 30 | 0 | 174.39 | 2086.63 | 1161.63 | 6399 | 89.52 | 161.104 |
|  XSLTProxy | 512M | 500 | 10240 | 100 | 0 | 178 | 2068.82 | 939.21 | 4607 | 89.21 | 165.289 |
|  XSLTProxy | 512M | 500 | 10240 | 500 | 0 | 168.18 | 2162.94 | 1104.61 | 5599 | 88.75 | 158.212 |
|  XSLTProxy | 512M | 500 | 10240 | 1000 | 0 | 167.45 | 2162.34 | 708.46 | 4415 | 91.37 | 153.415 |
|  XSLTProxy | 512M | 500 | 102400 | 0 | 100 | 2.1 | 120086.83 | 151.18 | 121343 | 20.74 | 482.725 |
|  XSLTProxy | 512M | 500 | 102400 | 30 | 100 | 1.46 | 126451.77 | 19301.57 | 186367 | 18.91 | 486.511 |
|  XSLTProxy | 512M | 500 | 102400 | 100 | 100 | 2.46 | 120119.65 | 349.68 | 123391 | 24.55 | 476.023 |
|  XSLTProxy | 512M | 500 | 102400 | 500 | 100 | 1.39 | 121178.48 | 17158.95 | 185343 | 19.09 | 486.476 |
|  XSLTProxy | 512M | 500 | 102400 | 1000 | 100 | 2.21 | 120067.09 | 56.2 | 120319 | 25.16 | 477.457 |
|  XSLTProxy | 512M | 1000 | 500 | 0 | 0 | 975.6 | 762.84 | 374.02 | 1903 | 87.9 | 157.751 |
|  XSLTProxy | 512M | 1000 | 500 | 30 | 0 | 977.74 | 758.88 | 315.09 | 1607 | 87.72 | 156.936 |
|  XSLTProxy | 512M | 1000 | 500 | 100 | 0 | 1020.47 | 731.49 | 303.27 | 1839 | 87.97 | 158.342 |
|  XSLTProxy | 512M | 1000 | 500 | 500 | 0 | 1069.44 | 695.13 | 144.87 | 1159 | 89.86 | 155.033 |
|  XSLTProxy | 512M | 1000 | 500 | 1000 | 0 | 700.58 | 1051.5 | 69.5 | 1303 | 93.67 | 147.286 |
|  XSLTProxy | 512M | 1000 | 1024 | 0 | 0 | 793.71 | 935.31 | 447.09 | 2383 | 88.68 | 162.62 |
|  XSLTProxy | 512M | 1000 | 1024 | 30 | 0 | 816.6 | 911.6 | 432.99 | 2383 | 89.08 | 156.646 |
|  XSLTProxy | 512M | 1000 | 1024 | 100 | 0 | 821.41 | 904.54 | 369.47 | 2039 | 88.12 | 157.671 |
|  XSLTProxy | 512M | 1000 | 1024 | 500 | 0 | 840.77 | 883.94 | 256.78 | 1679 | 89.42 | 159.192 |
|  XSLTProxy | 512M | 1000 | 1024 | 1000 | 0 | 626.34 | 1170.07 | 158.8 | 1679 | 92.61 | 145.035 |
|  XSLTProxy | 512M | 1000 | 10240 | 0 | 0 | 135.56 | 5278.43 | 2498.54 | 11711 | 88.86 | 235.211 |
|  XSLTProxy | 512M | 1000 | 10240 | 30 | 0 | 144.03 | 4921.22 | 2506.91 | 12543 | 85.87 | 252.536 |
|  XSLTProxy | 512M | 1000 | 10240 | 100 | 0 | 141.31 | 5041.08 | 2760.53 | 14143 | 86.51 | 247.177 |
|  XSLTProxy | 512M | 1000 | 10240 | 500 | 0 | 143.91 | 4830 | 1924.7 | 9535 | 86.81 | 235.234 |
|  XSLTProxy | 512M | 1000 | 10240 | 1000 | 0 | 155.94 | 4579.28 | 2116.43 | 11327 | 87.51 | 214.034 |
|  XSLTProxy | 512M | 1000 | 102400 | 0 | 100 | 2.05 | 124254.98 | 14468.75 | 185343 | 22.92 | 487.911 |
|  XSLTProxy | 512M | 1000 | 102400 | 30 | 100 | 3.32 | 119615.87 | 1942.29 | 121343 | 28.42 | 486.663 |
|  XSLTProxy | 512M | 1000 | 102400 | 100 | 100 | 2.82 | 114039.83 | 13800.78 | 123391 | 26.74 | 487.933 |
|  XSLTProxy | 512M | 1000 | 102400 | 500 | 100 | 2.66 | 117347.7 | 14268.56 | 120319 | 27.04 | 486.506 |
|  XSLTProxy | 512M | 1000 | 102400 | 1000 | 100 | 2.26 | 138299.77 | 29104.88 | 186367 | 21.9 | 490.951 |
