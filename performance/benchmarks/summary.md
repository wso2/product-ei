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
| Concurrent Users | The number of users accessing the application at the same time. | 1000 |
| Message Size (Bytes) | The request payload size in Bytes. | 102400 |
| Back-end Delay (ms) | The delay added by the back-end service. | 1000 |

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
|  CBRProxy | 512M | 1000 | 102400 | 1000 | 100 | 3.11 | 120175.4 | 676.04 | 123391 | 29.26 | 483.589 |
|  CBRSOAPHeaderProxy | 512M | 1000 | 102400 | 1000 | 100 | 17057.29 | 43.11 | 1147.51 | 177 | 25.63 | 488.826 |
|  CBRTransportHeaderProxy | 512M | 1000 | 102400 | 1000 | 0 | 218.65 | 4461.05 | 1162.45 | 7807 | 97.21 | 93.268 |
|  DirectProxy | 512M | 1000 | 102400 | 1000 | 0 | 162 | 4453.04 | 1013.65 | 6815 | 98.43 | 28.167 |
|  SecureProxy | 512M | 1000 | 102400 | 1000 | 100 | 66.68 | 14423.05 | 2337.38 | 28415 | 71.42 | 301.053 |
|  XSLTEnhancedProxy | 512M | 1000 | 102400 | 1000 | 68.63 | 6.92 | 106299.05 | 29272.48 | 186367 | 44.66 | 476.511 |
|  XSLTProxy | 512M | 1000 | 102400 | 1000 | 100 | 5 | 122257.04 | 6074.16 | 152575 | 23.62 | 489.94 |
