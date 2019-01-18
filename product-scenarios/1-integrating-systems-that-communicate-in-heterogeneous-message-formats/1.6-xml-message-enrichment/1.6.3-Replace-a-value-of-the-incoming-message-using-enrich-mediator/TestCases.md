### 1.6.3-Replace-a-value-of-the-incoming-message-using-enrich-mediator


| Test Case ID| Test Case| Test Case Description| Status|
| ----------| --------| ----------| ------|
| 1.6.3.1| Replacing body of payload using a payload stored in a property| **Given**:Test environment is set properly. </br> **When**:A request sends to wso2 EI to enrich a payload using a payload stored in a property. </br> **Then**:Payload should be enriched.| Automated|
| 1.6.3.2| Replacing target message defined through xpath by source body| **Given**:Test environment is set properly. </br> **When**:A request sends to wso2 EI to enrich a payload defined through xpath by source body. </br> **Then**:Payload should be enriched.| Automated|
| 1.6.3.3| Replacing target message defined through xpath by source property| **Given**:Test environment is set properly. </br> **When**:A request sends to wso2 EI to enrich a payload defined through xpath by source property. </br> **Then**:Payload should be enriched.| Automated|
| 1.6.3.4| Replacing target message defined through xpath by source inline content| **Given**:Test environment is set properly. </br> **When**:A request sends to wso2 EI to enrich a payload defined through xpath by source inline content. </br> **Then**:Payload should be enriched.| Automated|
