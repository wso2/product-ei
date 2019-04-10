# WSO2 Enterprise Micro Integrator Performance Test Results

During each release, we execute various automated performance test scenarios and publish the results.

| Test Scenarios | Description |
| --- | --- |
| DirectProxy |  |
| CBRProxy |  |
| XSLTProxy |  |
| CBRSOAPHeaderProxy |  |
| CBRTransportHeaderProxy |  |
| SecureProxy |  |
| XSLTEnhancedProxy |  |

Our test client is [Apache JMeter](https://jmeter.apache.org/index.html). We test each scenario for a fixed duration of
time. We split the test results into warmup and measurement parts and use the measurement part to compute the
performance metrics.

Test scenarios use a [Netty](https://netty.io/) based back-end service which echoes back any request
posted to it after a specified period of time.

We run the performance tests under different numbers of concurrent users, message sizes (payloads) and back-end service
delays.

The main performance metrics:

1. **Throughput**: The number of requests that the WSO2 Enterprise Integrator processes during a specific time interval (e.g. per second).
2. **Response Time**: The end-to-end latency for an operation of invoking a service in WSO2 Enterprise  Integrator. The complete distribution of response times was recorded.

In addition to the above metrics, we measure the load average and several memory-related metrics.

The following are the test parameters.

| Test Parameter | Description | Values |
| --- | --- | --- |
| Scenario Name | The name of the test scenario. | Refer to the above table. |
| Heap Size | The amount of memory allocated to the application | 512M |
| Concurrent Users | The number of users accessing the application at the same time. | 100, 200, 500, 1000 |
| Message Size (Bytes) | The request payload size in Bytes. | 500, 1024, 10240, 102400 |
| Back-end Delay (ms) | The delay added by the back-end service. | 0, 30, 100, 500, 1000 |

The duration of each test is **60 seconds**. The warm-up period is **30 seconds**.
The measurement results are collected after the warm-up period.

A [**c5.xlarge** Amazon EC2 instance](https://aws.amazon.com/ec2/instance-types/) was used to install EI.

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
|  DirectProxy | 512M | 100 | 500 | 0 | 0 | 753.09 | 100.67 | 47.46 | 201 | 98.58 | 28.156 |
|  DirectProxy | 512M | 100 | 500 | 30 | 0 | 813.35 | 99.47 | 27.81 | 200 | 98.66 | 28.16 |
|  DirectProxy | 512M | 100 | 500 | 100 | 0 | 662.6 | 126.89 | 39.46 | 205 | 98.59 | 28.203 |
|  DirectProxy | 512M | 100 | 500 | 500 | 0 | 171.22 | 503.94 | 11.93 | 571 | 99.48 | 28.158 |
|  DirectProxy | 512M | 100 | 500 | 1000 | 0 | 87.61 | 1004.46 | 11.97 | 1071 | 99.47 | 28.107 |
|  DirectProxy | 512M | 100 | 1024 | 0 | 0 | 828.75 | 108.59 | 47.65 | 203 | 98.2 | 28.181 |
|  DirectProxy | 512M | 100 | 1024 | 30 | 0 | 1091.02 | 83.36 | 31.2 | 198 | 98.3 | 28.117 |
|  DirectProxy | 512M | 100 | 1024 | 100 | 0 | 790.64 | 116.21 | 32.49 | 199 | 98.9 | 24.003 |
|  DirectProxy | 512M | 100 | 1024 | 500 | 0 | 183.79 | 502.77 | 21.26 | 571 | 99.48 | 28.119 |
|  DirectProxy | 512M | 100 | 1024 | 1000 | 0 | 91.83 | 1005.75 | 21.92 | 1095 | 99.69 | 28.26 |
|  DirectProxy | 512M | 100 | 10240 | 0 | 0 | 640.28 | 146.25 | 54.64 | 295 | 98.57 | 28.189 |
|  DirectProxy | 512M | 100 | 10240 | 30 | 0 | 794.9 | 118.21 | 45.3 | 215 | 98.54 | 28.134 |
|  DirectProxy | 512M | 100 | 10240 | 100 | 0 | 692.93 | 136.27 | 50.18 | 289 | 98.37 | 28.224 |
|  DirectProxy | 512M | 100 | 10240 | 500 | 0 | 209.28 | 452.16 | 138.03 | 599 | 99.58 | 28.194 |
|  DirectProxy | 512M | 100 | 10240 | 1000 | 0 | 97.5 | 970.57 | 189.98 | 1103 | 99.65 | 28.204 |
|  DirectProxy | 512M | 100 | 102400 | 0 | 0 | 207.49 | 459.68 | 112.99 | 711 | 99.29 | 28.149 |
|  DirectProxy | 512M | 100 | 102400 | 30 | 0 | 252.67 | 378.38 | 109.38 | 695 | 99.31 | 28.17 |
|  DirectProxy | 512M | 100 | 102400 | 100 | 0 | 224.26 | 427.23 | 127.76 | 791 | 99.27 | 28.125 |
|  DirectProxy | 512M | 100 | 102400 | 500 | 0 | 236.55 | 405.41 | 207.54 | 699 | 99.45 | 28.107 |
|  DirectProxy | 512M | 100 | 102400 | 1000 | 0 | 177.61 | 539.46 | 411.43 | 1103 | 99.36 | 28.088 |
|  DirectProxy | 512M | 200 | 500 | 0 | 0 | 1090.76 | 176.54 | 74.93 | 397 | 97.66 | 28.176 |
|  DirectProxy | 512M | 200 | 500 | 30 | 0 | 1156.26 | 166.69 | 63.34 | 309 | 97.48 | 28.222 |
|  DirectProxy | 512M | 200 | 500 | 100 | 0 | 1026.3 | 188.21 | 66.48 | 395 | 97.58 | 28.191 |
|  DirectProxy | 512M | 200 | 500 | 500 | 0 | 372.76 | 518.84 | 38.28 | 607 | 99.15 | 28.123 |
|  DirectProxy | 512M | 200 | 500 | 1000 | 0 | 191.43 | 1010.02 | 59.03 | 1103 | 99.59 | 28.131 |
|  DirectProxy | 512M | 200 | 1024 | 0 | 0 | 1195.23 | 162.24 | 69.92 | 391 | 97.4 | 28.269 |
|  DirectProxy | 512M | 200 | 1024 | 30 | 0 | 1208.78 | 160.52 | 63.44 | 313 | 97.29 | 28.097 |
|  DirectProxy | 512M | 200 | 1024 | 100 | 0 | 1027.93 | 189 | 67.49 | 395 | 97.51 | 28.137 |
|  DirectProxy | 512M | 200 | 1024 | 500 | 0 | 381.88 | 509 | 28.14 | 599 | 99.36 | 28.125 |
|  DirectProxy | 512M | 200 | 1024 | 1000 | 0 | 192.92 | 1008.34 | 65.33 | 1103 | 99.56 | 28.104 |
|  DirectProxy | 512M | 200 | 10240 | 0 | 0 | 775.34 | 251.5 | 83.4 | 493 | 98.07 | 28.17 |
|  DirectProxy | 512M | 200 | 10240 | 30 | 0 | 860.42 | 226.74 | 79.07 | 407 | 98.03 | 28.194 |
|  DirectProxy | 512M | 200 | 10240 | 100 | 0 | 810.5 | 240.88 | 73.9 | 487 | 98.05 | 28.183 |
|  DirectProxy | 512M | 200 | 10240 | 500 | 0 | 433.88 | 449.97 | 141.9 | 599 | 99.31 | 28.197 |
|  DirectProxy | 512M | 200 | 10240 | 1000 | 0 | 219.87 | 888.29 | 314.53 | 1103 | 99.51 | 24.144 |
|  DirectProxy | 512M | 200 | 102400 | 0 | 0 | 250.86 | 779.6 | 199.27 | 1399 | 99.25 | 28.127 |
|  DirectProxy | 512M | 200 | 102400 | 30 | 0 | 279.65 | 699.79 | 192.28 | 1303 | 99.26 | 28.084 |
|  DirectProxy | 512M | 200 | 102400 | 100 | 0 | 255.26 | 766.97 | 203.43 | 1399 | 99.08 | 28.142 |
|  DirectProxy | 512M | 200 | 102400 | 500 | 0 | 250.52 | 781.55 | 199.38 | 1303 | 99.23 | 28.022 |
|  DirectProxy | 512M | 200 | 102400 | 1000 | 0 | 262.48 | 745.35 | 391.51 | 1399 | 99.28 | 28.143 |
|  DirectProxy | 512M | 500 | 500 | 0 | 0 | 1178.6 | 416.14 | 147.15 | 807 | 95.8 | 28.219 |
|  DirectProxy | 512M | 500 | 500 | 30 | 0 | 1237.37 | 396.5 | 147.52 | 807 | 95.44 | 28.144 |
|  DirectProxy | 512M | 500 | 500 | 100 | 0 | 1105.82 | 443.86 | 150.53 | 903 | 95.47 | 28.147 |
|  DirectProxy | 512M | 500 | 500 | 500 | 0 | 913.11 | 537.5 | 55.67 | 707 | 96.78 | 28.179 |
|  DirectProxy | 512M | 500 | 500 | 1000 | 0 | 483.53 | 1014.52 | 39.85 | 1111 | 98.59 | 28.144 |
|  DirectProxy | 512M | 500 | 1024 | 0 | 0 | 1189.79 | 413.08 | 150.93 | 891 | 95.2 | 28.354 |
|  DirectProxy | 512M | 500 | 1024 | 30 | 0 | 1258.2 | 390.82 | 145.35 | 803 | 95.02 | 28.159 |
|  DirectProxy | 512M | 500 | 1024 | 100 | 0 | 1113.01 | 441.91 | 146.44 | 899 | 95.28 | 28.142 |
|  DirectProxy | 512M | 500 | 1024 | 500 | 0 | 922.37 | 533.16 | 52.57 | 707 | 96.38 | 28.211 |
|  DirectProxy | 512M | 500 | 1024 | 1000 | 0 | 482.39 | 1019.56 | 40.58 | 1111 | 98.41 | 28.237 |
|  DirectProxy | 512M | 500 | 10240 | 0 | 0 | 835.56 | 589.22 | 159.06 | 1011 | 96.68 | 28.142 |
|  DirectProxy | 512M | 500 | 10240 | 30 | 0 | 855.33 | 575.58 | 173.83 | 1103 | 96.21 | 28.232 |
|  DirectProxy | 512M | 500 | 10240 | 100 | 0 | 816.56 | 603.24 | 175.83 | 1111 | 96.38 | 28.122 |
|  DirectProxy | 512M | 500 | 10240 | 500 | 0 | 809.85 | 608.16 | 159.22 | 999 | 96.64 | 28.159 |
|  DirectProxy | 512M | 500 | 10240 | 1000 | 0 | 535.34 | 919.51 | 263.08 | 1111 | 98.6 | 28.05 |
|  DirectProxy | 512M | 500 | 102400 | 0 | 0 | 262.29 | 1876.52 | 435.51 | 3311 | 98.72 | 28.163 |
|  DirectProxy | 512M | 500 | 102400 | 30 | 0 | 277.51 | 1774.03 | 426.62 | 3199 | 98.74 | 28.114 |
|  DirectProxy | 512M | 500 | 102400 | 100 | 0 | 262.58 | 1875.25 | 460.75 | 3407 | 98.67 | 28.173 |
|  DirectProxy | 512M | 500 | 102400 | 500 | 0 | 264.45 | 1862.68 | 438.13 | 3311 | 98.88 | 28.188 |
|  DirectProxy | 512M | 500 | 102400 | 1000 | 0 | 257.36 | 1914.29 | 426.47 | 3423 | 98.8 | 28.126 |
|  DirectProxy | 512M | 1000 | 500 | 0 | 0 | 1170.97 | 842.43 | 284.7 | 1695 | 88.93 | 134.961 |
|  DirectProxy | 512M | 1000 | 500 | 30 | 0 | 1194.47 | 826.1 | 303.64 | 1791 | 88.85 | 136.14 |
|  DirectProxy | 512M | 1000 | 500 | 100 | 0 | 1118.46 | 882.35 | 304.69 | 1807 | 89.67 | 135.146 |
|  DirectProxy | 512M | 1000 | 500 | 500 | 0 | 1093.93 | 902.19 | 207 | 1607 | 91.57 | 122.247 |
|  DirectProxy | 512M | 1000 | 500 | 1000 | 0 | 892.17 | 1105.88 | 116.75 | 1511 | 94.06 | 87.222 |
|  DirectProxy | 512M | 1000 | 1024 | 0 | 0 | 1136.65 | 868.92 | 301.5 | 1719 | 88.81 | 135.445 |
|  DirectProxy | 512M | 1000 | 1024 | 30 | 0 | 1286 | 768.43 | 265.03 | 1519 | 89.11 | 137.67 |
|  DirectProxy | 512M | 1000 | 1024 | 100 | 0 | 1237.43 | 798.69 | 257.37 | 1599 | 90.14 | 135.707 |
|  DirectProxy | 512M | 1000 | 1024 | 500 | 0 | 1173.15 | 842.35 | 176.04 | 1407 | 92.34 | 123.997 |
|  DirectProxy | 512M | 1000 | 1024 | 1000 | 0 | 915.32 | 1078.74 | 99.99 | 1495 | 94.65 | 87.22 |
|  DirectProxy | 512M | 1000 | 10240 | 0 | 0 | 879.73 | 1123.22 | 291.85 | 1991 | 90.84 | 142.36 |
|  DirectProxy | 512M | 1000 | 10240 | 30 | 0 | 890.27 | 1110.43 | 310.89 | 2007 | 91.06 | 142.293 |
|  DirectProxy | 512M | 1000 | 10240 | 100 | 0 | 858.4 | 1151.84 | 328.74 | 2111 | 91.94 | 142.454 |
|  DirectProxy | 512M | 1000 | 10240 | 500 | 0 | 855.62 | 1145.86 | 276.71 | 2007 | 94.38 | 116.223 |
|  DirectProxy | 512M | 1000 | 10240 | 1000 | 0 | 790.54 | 1210.33 | 374.02 | 1999 | 94.95 | 86.52 |
|  DirectProxy | 512M | 1000 | 102400 | 0 | 0 | 232.21 | 4117.31 | 1055.51 | 6911 | 97.1 | 91.142 |
