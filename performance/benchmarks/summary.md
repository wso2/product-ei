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
| Operating System | Distribution | Kernel | Linux ip-10-0-1-170 4.15.0-1037-aws #39-Ubuntu SMP Tue Apr 16 08:09:09 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux |
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
| Operating System | Distribution | Kernel | Linux ip-10-0-1-222 4.15.0-1037-aws #39-Ubuntu SMP Tue Apr 16 08:09:09 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux |


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
|  CBR Proxy | 512M | 10 | 1024 | 0 | 0 | 722.86 | 13.8 | 28.72 | 89 | 98.59 | 28.083 |
|  CBR Proxy | 512M | 10 | 1024 | 1000 | 0 | 9.95 | 1003.3 | 8.29 | 1055 | 99.91 | 24.072 |
|  CBR Proxy | 512M | 10 | 10240 | 0 | 0 | 157.12 | 63.5 | 43.45 | 107 | 98.83 | 28.139 |
|  CBR Proxy | 512M | 10 | 10240 | 1000 | 0 | 12.33 | 810.73 | 368.02 | 1079 | 99.87 | 28.128 |
|  CBR Proxy | 512M | 20 | 1024 | 0 | 0 | 739.21 | 27.01 | 37.62 | 95 | 98.52 | 28.157 |
|  CBR Proxy | 512M | 20 | 1024 | 1000 | 0 | 19.8 | 1008.85 | 24.7 | 1103 | 99.89 | 28.221 |
|  CBR Proxy | 512M | 20 | 10240 | 0 | 0 | 156.96 | 127.33 | 53.76 | 289 | 98.26 | 28.115 |
|  CBR Proxy | 512M | 20 | 10240 | 1000 | 0 | 24.12 | 828.35 | 351.38 | 1095 | 99.8 | 28.153 |
|  CBR Proxy | 512M | 50 | 1024 | 0 | 0 | 772.89 | 64.63 | 43.35 | 113 | 98.17 | 28.137 |
|  CBR Proxy | 512M | 50 | 1024 | 1000 | 0 | 49.29 | 1012.71 | 27.94 | 1103 | 99.86 | 28.164 |
|  CBR Proxy | 512M | 50 | 10240 | 0 | 0 | 161.98 | 308.88 | 100.39 | 595 | 96.68 | 28.189 |
|  CBR Proxy | 512M | 50 | 10240 | 1000 | 0 | 63.11 | 791.43 | 377.55 | 1103 | 99.38 | 28.155 |
|  CBR Proxy | 512M | 100 | 1024 | 0 | 0 | 764.26 | 130.78 | 54.23 | 291 | 97.53 | 28.182 |
|  CBR Proxy | 512M | 100 | 1024 | 1000 | 0 | 98.8 | 1011.76 | 26.59 | 1103 | 99.75 | 28.222 |
|  CBR Proxy | 512M | 100 | 10240 | 0 | 0 | 158.67 | 630.05 | 208.47 | 1199 | 91.7 | 88.807 |
|  CBR Proxy | 512M | 100 | 10240 | 1000 | 0 | 113.32 | 881.39 | 332.72 | 1231 | 95.3 | 84.684 |
|  CBR SOAP Header Proxy | 512M | 10 | 1024 | 0 | 0 | 804.23 | 12.4 | 27.29 | 88 | 98.75 | 28.163 |
|  CBR SOAP Header Proxy | 512M | 10 | 1024 | 1000 | 0 | 9.96 | 1002.46 | 4.47 | 1015 | 99.91 | 28.1 |
|  CBR SOAP Header Proxy | 512M | 10 | 10240 | 0 | 0 | 231.21 | 43.19 | 43.21 | 99 | 99.2 | 28.163 |
|  CBR SOAP Header Proxy | 512M | 10 | 10240 | 1000 | 0 | 10.91 | 916.38 | 282.19 | 1103 | 99.89 | 28.216 |
|  CBR SOAP Header Proxy | 512M | 20 | 1024 | 0 | 0 | 834.68 | 23.91 | 36.06 | 94 | 98.5 | 28.144 |
|  CBR SOAP Header Proxy | 512M | 20 | 1024 | 1000 | 0 | 19.79 | 1009.09 | 24.39 | 1103 | 99.91 | 28.222 |
|  CBR SOAP Header Proxy | 512M | 20 | 10240 | 0 | 0 | 232.81 | 85.76 | 41.53 | 194 | 98.75 | 28.171 |
|  CBR SOAP Header Proxy | 512M | 20 | 10240 | 1000 | 0 | 24.29 | 823.3 | 358.81 | 1103 | 99.86 | 28.13 |
|  CBR SOAP Header Proxy | 512M | 50 | 1024 | 0 | 0 | 878.38 | 56.85 | 43.55 | 108 | 98.22 | 28.124 |
|  CBR SOAP Header Proxy | 512M | 50 | 1024 | 1000 | 0 | 49.38 | 1010.93 | 25.75 | 1103 | 99.88 | 28.139 |
|  CBR SOAP Header Proxy | 512M | 50 | 10240 | 0 | 0 | 245.2 | 203.93 | 72.15 | 395 | 98.01 | 28.185 |
|  CBR SOAP Header Proxy | 512M | 50 | 10240 | 1000 | 0 | 57.72 | 865.59 | 332.16 | 1103 | 99.67 | 28.127 |
|  CBR SOAP Header Proxy | 512M | 100 | 1024 | 0 | 0 | 858.87 | 116.36 | 48.69 | 209 | 97.57 | 28.168 |
|  CBR SOAP Header Proxy | 512M | 100 | 1024 | 1000 | 0 | 98.7 | 1012.44 | 27.93 | 1103 | 99.72 | 28.165 |
|  CBR SOAP Header Proxy | 512M | 100 | 10240 | 0 | 0 | 243.21 | 411.26 | 136.45 | 803 | 94.19 | 88.274 |
|  CBR SOAP Header Proxy | 512M | 100 | 10240 | 1000 | 0 | 119.37 | 836.62 | 341.27 | 1111 | 98.03 | 82.502 |
|  CBR Transport Header Proxy | 512M | 10 | 1024 | 0 | 0 | 1118.26 | 8.91 | 23.47 | 87 | 98.49 | 28.164 |
|  CBR Transport Header Proxy | 512M | 10 | 1024 | 1000 | 0 | 9.96 | 1002.76 | 6.1 | 1047 | 99.9 | 28.151 |
|  CBR Transport Header Proxy | 512M | 10 | 10240 | 0 | 0 | 706.85 | 14.09 | 29.18 | 89 | 99.04 | 28.155 |
|  CBR Transport Header Proxy | 512M | 10 | 10240 | 1000 | 0 | 19.91 | 501.82 | 415.1 | 1103 | 99.9 | 28.213 |
|  CBR Transport Header Proxy | 512M | 20 | 1024 | 0 | 0 | 1116.31 | 17.87 | 32.15 | 91 | 98.51 | 28.184 |
|  CBR Transport Header Proxy | 512M | 20 | 1024 | 1000 | 0 | 20.33 | 982.98 | 119.38 | 1055 | 99.9 | 28.24 |
|  CBR Transport Header Proxy | 512M | 20 | 10240 | 0 | 0 | 714.02 | 27.92 | 38.36 | 94 | 98.87 | 28.23 |
|  CBR Transport Header Proxy | 512M | 20 | 10240 | 1000 | 0 | 27.85 | 717.65 | 404.67 | 1103 | 99.9 | 28.19 |
|  CBR Transport Header Proxy | 512M | 50 | 1024 | 0 | 0 | 1198.81 | 41.63 | 42.14 | 101 | 98.41 | 23.975 |
|  CBR Transport Header Proxy | 512M | 50 | 1024 | 1000 | 0 | 49.82 | 1003.5 | 100.92 | 1103 | 99.85 | 28.15 |
|  CBR Transport Header Proxy | 512M | 50 | 10240 | 0 | 0 | 748.91 | 66.65 | 41.99 | 109 | 98.94 | 28.116 |
|  CBR Transport Header Proxy | 512M | 50 | 10240 | 1000 | 0 | 62.77 | 796.02 | 373.9 | 1103 | 99.87 | 28.192 |
|  CBR Transport Header Proxy | 512M | 100 | 1024 | 0 | 0 | 1230.71 | 81.15 | 42.26 | 192 | 97.72 | 28.216 |
|  CBR Transport Header Proxy | 512M | 100 | 1024 | 1000 | 0 | 98.59 | 1014.39 | 35.54 | 1103 | 99.86 | 28.188 |
|  CBR Transport Header Proxy | 512M | 100 | 10240 | 0 | 0 | 758.27 | 131.75 | 47.11 | 206 | 98.65 | 28.132 |
|  CBR Transport Header Proxy | 512M | 100 | 10240 | 1000 | 0 | 124.7 | 801.74 | 375.49 | 1103 | 99.79 | 28.162 |
|  Direct Proxy | 512M | 10 | 1024 | 0 | 0 | 1099.23 | 9.07 | 23.71 | 87 | 98.57 | 28.168 |
|  Direct Proxy | 512M | 10 | 1024 | 1000 | 0 | 10.35 | 964.99 | 179.58 | 1047 | 99.92 | 28.119 |
|  Direct Proxy | 512M | 10 | 10240 | 0 | 0 | 698.43 | 14.21 | 29.27 | 89 | 98.9 | 28.138 |
|  Direct Proxy | 512M | 10 | 10240 | 1000 | 0 | 21.83 | 457.37 | 397.38 | 1095 | 99.91 | 28.135 |
|  Direct Proxy | 512M | 20 | 1024 | 0 | 0 | 1162.84 | 17.15 | 31.6 | 91 | 98.29 | 28.157 |
|  Direct Proxy | 512M | 20 | 1024 | 1000 | 0 | 19.99 | 999.61 | 80.3 | 1103 | 99.91 | 28.156 |
|  Direct Proxy | 512M | 20 | 10240 | 0 | 0 | 741.47 | 26.9 | 37.78 | 93 | 99 | 28.155 |
|  Direct Proxy | 512M | 20 | 10240 | 1000 | 0 | 34.16 | 584.32 | 422.43 | 1095 | 99.9 | 28.19 |
|  Direct Proxy | 512M | 50 | 1024 | 0 | 0 | 1227.74 | 40.66 | 42.09 | 101 | 98.02 | 28.16 |
|  Direct Proxy | 512M | 50 | 1024 | 1000 | 0 | 49.58 | 1008.22 | 87.19 | 1103 | 99.88 | 28.228 |
|  Direct Proxy | 512M | 50 | 10240 | 0 | 0 | 752.77 | 66.34 | 42 | 109 | 99.02 | 28.14 |
|  Direct Proxy | 512M | 50 | 10240 | 1000 | 0 | 62.79 | 795.9 | 385.31 | 1103 | 99.89 | 28.164 |
|  Direct Proxy | 512M | 100 | 1024 | 0 | 0 | 1275.45 | 78.28 | 42.18 | 191 | 97.81 | 28.154 |
|  Direct Proxy | 512M | 100 | 1024 | 1000 | 0 | 98.58 | 1014.33 | 32.98 | 1103 | 99.87 | 28.127 |
|  Direct Proxy | 512M | 100 | 10240 | 0 | 0 | 766.63 | 130.34 | 46.52 | 205 | 98.64 | 28.2 |
|  Direct Proxy | 512M | 100 | 10240 | 1000 | 0 | 129.58 | 771.09 | 389.37 | 1103 | 99.76 | 28.128 |
|  XSLT Enhanced Proxy | 512M | 10 | 1024 | 0 | 0 | 616.34 | 16.19 | 31.06 | 91 | 98.63 | 38.01 |
|  XSLT Enhanced Proxy | 512M | 10 | 1024 | 1000 | 0 | 9.39 | 1064.9 | 62.12 | 1199 | 99.91 | 39.404 |
|  XSLT Enhanced Proxy | 512M | 10 | 10240 | 0 | 0 | 122.91 | 81.3 | 42.12 | 193 | 99.19 | 40.817 |
|  XSLT Enhanced Proxy | 512M | 10 | 10240 | 1000 | 0 | 11.99 | 833.51 | 338.41 | 1103 | 99.85 | 39.403 |
|  XSLT Enhanced Proxy | 512M | 20 | 1024 | 0 | 0 | 629.04 | 31.74 | 39.83 | 96 | 98.39 | 38.492 |
|  XSLT Enhanced Proxy | 512M | 20 | 1024 | 1000 | 0 | 19.61 | 1019.49 | 38.92 | 1199 | 99.88 | 38.237 |
|  XSLT Enhanced Proxy | 512M | 20 | 10240 | 0 | 0 | 123.95 | 161.3 | 59.92 | 301 | 98.97 | 39.393 |
|  XSLT Enhanced Proxy | 512M | 20 | 10240 | 1000 | 0 | 24.87 | 803.55 | 357.09 | 1095 | 99.78 | 39.561 |
|  XSLT Enhanced Proxy | 512M | 50 | 1024 | 0 | 0 | 690.77 | 72.3 | 42.32 | 188 | 97.99 | 41.131 |
|  XSLT Enhanced Proxy | 512M | 50 | 1024 | 1000 | 0 | 49.39 | 1010.88 | 25.42 | 1103 | 99.85 | 40.763 |
|  XSLT Enhanced Proxy | 512M | 50 | 10240 | 0 | 0 | 129.17 | 387.34 | 114.87 | 699 | 98.53 | 39.808 |
|  XSLT Enhanced Proxy | 512M | 50 | 10240 | 1000 | 0 | 67.62 | 738.79 | 399.09 | 1095 | 99.57 | 39.898 |
|  XSLT Enhanced Proxy | 512M | 100 | 1024 | 0 | 0 | 677.85 | 147.45 | 55.96 | 297 | 97.18 | 42.323 |
|  XSLT Enhanced Proxy | 512M | 100 | 1024 | 1000 | 0 | 98.94 | 1009.86 | 23.7 | 1103 | 99.74 | 40.103 |
|  XSLT Enhanced Proxy | 512M | 100 | 10240 | 0 | 0 | 135.12 | 739.57 | 214.87 | 1303 | 97.4 | 47.436 |
|  XSLT Enhanced Proxy | 512M | 100 | 10240 | 1000 | 0 | 114.27 | 873.87 | 283.49 | 1103 | 98.77 | 41.701 |
|  XSLT Proxy | 512M | 10 | 1024 | 0 | 0 | 391.4 | 25.51 | 37.36 | 94 | 98.2 | 40.879 |
|  XSLT Proxy | 512M | 10 | 1024 | 1000 | 0 | 9.86 | 1013.44 | 32.82 | 1199 | 99.88 | 41.86 |
|  XSLT Proxy | 512M | 10 | 10240 | 0 | 0 | 69.13 | 144.52 | 54.64 | 295 | 98.62 | 72.632 |
|  XSLT Proxy | 512M | 10 | 10240 | 1000 | 0 | 9.88 | 1011.79 | 19.95 | 1103 | 99.78 | 41.794 |
|  XSLT Proxy | 512M | 20 | 1024 | 0 | 0 | 396.86 | 50.34 | 43.97 | 105 | 97.99 | 58.791 |
|  XSLT Proxy | 512M | 20 | 1024 | 1000 | 0 | 19.81 | 1008.98 | 23.85 | 1103 | 99.88 | 40.399 |
|  XSLT Proxy | 512M | 20 | 10240 | 0 | 0 | 69.25 | 288.97 | 109.79 | 603 | 98.03 | 99.537 |
|  XSLT Proxy | 512M | 20 | 10240 | 1000 | 0 | 19.77 | 1010.77 | 25.45 | 1103 | 99.68 | 44.004 |
|  XSLT Proxy | 512M | 50 | 1024 | 0 | 0 | 402.73 | 124.07 | 50.56 | 219 | 97.37 | 84.346 |
|  XSLT Proxy | 512M | 50 | 1024 | 1000 | 0 | 49.67 | 1006.26 | 17.3 | 1103 | 99.76 | 40.398 |
|  XSLT Proxy | 512M | 50 | 10240 | 0 | 0 | 66.52 | 751.29 | 272.54 | 1599 | 95.24 | 141.125 |
|  XSLT Proxy | 512M | 50 | 10240 | 1000 | 0 | 49.21 | 1015.04 | 29.87 | 1127 | 99.06 | 92.856 |
|  XSLT Proxy | 512M | 100 | 1024 | 0 | 0 | 406.62 | 246 | 81.83 | 497 | 96.45 | 109.744 |
|  XSLT Proxy | 512M | 100 | 1024 | 1000 | 0 | 99.09 | 1007.66 | 20.68 | 1103 | 99.55 | 41.403 |
|  XSLT Proxy | 512M | 100 | 10240 | 0 | 0 | 65.67 | 1521.98 | 517.47 | 3007 | 91.45 | 138.615 |
|  XSLT Proxy | 512M | 100 | 10240 | 1000 | 0 | 64.82 | 1539.22 | 285.43 | 2511 | 93.7 | 144.168 |
