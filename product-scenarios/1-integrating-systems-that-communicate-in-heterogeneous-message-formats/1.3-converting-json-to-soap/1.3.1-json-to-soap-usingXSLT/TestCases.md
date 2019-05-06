### 1.3.1 - Converting JSON message to SOAP using XSLT mediator


| Test Case ID| Test Case| Test Case Description| Status|
| ----------| --------| ----------| ------|
| 1.3.1.1| A simple message transformation with XSLT mediator | **Given**:Test environment is set properly. </br> **When**:A json message is sent to wso2 EI. </br> **Then**:It should be converted to a soap message.| Automated|
| 1.3.1.2| Transformation of a JSON message with special characters| **Given**:Test environment is set properly. </br> **When**:A json message with special characters is sent to wso2 EI</br> **Then**:| Not Automated|
| 1.3.1.3| Handling malformed JSON while doing the transformation| **Given**:Test environment is set properly. </br> **When**:A malformed json is sent to wso2 EI</br> **Then**:| Not Automated|
| 1.3.1.4| Converting a JSON message into a SOAP messages with attachments (MTOM)| **Given**:Test environment is set properly. </br> **When**:A json message with attachment is sent to wso2 EI</br> **Then**:| Not Automated|
| 1.3.1.5| Converting JSON message has single element arrays into a SOAP message| **Given**:Test environment is set properly. </br> **When**:A json message with single element array is sent to wso2 EI</br> **Then**:| Not Automated|
