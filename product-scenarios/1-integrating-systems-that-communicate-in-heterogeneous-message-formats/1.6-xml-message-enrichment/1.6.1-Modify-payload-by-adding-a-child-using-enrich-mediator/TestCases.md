### 1.6.1 - Modify payload by adding a child using enrich mediator 


| Test Case ID| Test Case| Test Case Description| Status|
| ----------| --------| ----------| ------|
| 1.6.1.1| Adding an inline content as a child to the message body | **Given**:Test environment is set properly. </br> **When**:A soap message is sent to wso2 EI.</br> **Then**:The message should be enriched by adding a child element before it goes to the endpoint.| Automated|
| 1.6.1.2| Adding an element defined by a xpath as a child to message body| **Given**:Test environment is set properly. </br> **When**:A soap message is sent to wso2 EI.</br> **Then**:The message should be enriched by adding a child element before it goes to the endpoint.| Automated|
| 1.6.1.3| Adding current message body as a child to a new message body| **Given**:Test environment is set properly. </br> **When**:A soap message is sent to wso2 EI. </br> **Then**:The message should be enriched by adding a child element before it goes to the endpoint.|Not Automated|
| 1.6.1.4| Adding a payload stored in a property (OM type) as a child to the message body| **Given**:Test environment is set properly. </br> **When**:AA soap message is sent to wso2 EI.</br> **Then**:The message should be enriched by adding a child element before it goes to the endpoint.| Not Automated|
