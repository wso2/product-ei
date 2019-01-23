### 1.6.2 - Modify payload by adding a sibling using enrich mediator


| Test Case ID| Test Case| Test Case Description| Status|
| ----------| --------| ----------| ------|
| 1.6.2.1| Adding an inline content as a sibling to the message body | **Given**:Test environment is set properly. </br> **When**:A soap message is sent to wso2 EI to enrich a payload by adding an inline content as a sibling to the message body.</br> **Then**:The message should be enriched by adding a sibling element before it goes to the endpoint.| Automated|
| 1.6.2.2| Adding an element defined by a xpath as a sibling to message body| **Given**:Test environment is set properly. </br> **When**:A soap message is sent to wso2 EI to enrich a payload by adding an element defined by a xpath as a sibling to message body.</br> **Then**:The message should be enriched by adding a sibling element before it goes to the endpoint.| Automated|
| 1.6.2.3| Adding current message body as a sibling to the resulting message body| **Given**:Test environment is set properly. </br> **When**:A soap message is sent to wso2 EI to enrich a payload by adding current message body as a sibling to the resulting message body. </br> **Then**:The message should be enriched by adding a sibling element before it goes to the endpoint.| Automated|
| 1.6.2.4| Adding a payload stored in a property as a sibling to the message body| **Given**:Test environment is set properly. </br> **When**:AA soap message is sent to wso2 EI to enrich a payload by adding a payload stored in a property as a sibling to the message body.</br> **Then**:The message should be enriched by adding a sibling element before it goes to the endpoint.| Automated|
