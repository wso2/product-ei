### 1.2.1-Converting POX message to JSON using PayloadFactory mediator

| Test Case ID| Test Case| Test Case Description| Status|
| ----------| --------| ----------| ------|
| 1.2.1.1| Converting a POX message to JSON using PayloadFactory mediator| **Given**:Test environment is set properly. </br> **When**:A soap request is sent to wso2 EI. </br> **Then**:It should be converted to a json message.| Automated|
| 1.2.1.2| Handling malformed POX messages while converting to JSON using PayloadFactory mediator| **Given**:Test environment is set properly. </br> **When**:A malformed soap request is sent to wso2 EI</br> **Then**:| Automated|
| 1.2.1.3| Converting large sized POX message to JSON using PayloadFactory mediator| **Given**:Test environment is set properly. </br> **When**:A large size soap request is sent to wso2 EI</br> **Then**:| Automated|
| 1.2.1.4| Converting POX message to JSON using PayloadFactory mediator when arguments with deepCheck="false"| **Given**:Test environment is set properly. </br> **When**:A soap request is sent to wso2 EI. </br> **Then**:It should be converted to a json message.| Automated|
| 1.2.1.5| Converting POX message to JSON using PayloadFactory mediator when required arguements values are not provided| **Given**:Test environment is set properly. </br> **When**:A malformed soap request is sent to wso2 EI</br> **Then**:| Automated|
| 1.2.1.6| Converting POX message with special characters using PayloadFactory mediator| **Given**:Test environment is set properly. </br> **When**:A large size soap request is sent to wso2 EI</br> **Then**:| Automated|
