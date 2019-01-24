### 1.1.1 - Converting SOAP message to JSON using PayloadFactory mediator

| Test Case ID| Test Case| Test Case Description| Status|
| ----------| --------| ----------| ------|
| 1.1.1.1| Converting a valid SOAP message to JSON| **Given**:Test environment is set properly. </br> **When**:A soap request is sent to wso2 EI. </br> **Then**:It should be converted to a json message.| Automated|
| 1.1.1.2| Handling a malformed SOAP messages| **Given**:Test environment is set properly. </br> **When**:A malformed soap request is sent to wso2 EI</br> **Then**:Payload should be enriched.| Automated|
| 1.1.1.3| Converting large sized SOAP message to JSON| **Given**:Test environment is set properly. </br> **When**:A large size soap request is sent to wso2 EI</br> **Then**:It should be converted to a json message.| Not Automated|
| 1.1.1.4| Converting SOAP message to JSON when arguments with deepCheck="false"| **Given**:Test environment is set properly. </br> **When**:A soap request is sent to wso2 EI with argument deepCheck="false"</br> **Then**:.| Not Automated|
| 1.1.1.5| Converting SOAP message to JSON when required arguments values are not provided| **Given**:Test environment is set properly. </br> **When**:A soap request is sent to wso2 EI without providing the required arguments.</br> **Then**:.| Not Automated|
| 1.1.1.6| Converting SOAP message with special characters  | **Given**:Test environment is set properly. </br> **When**:A soap request is sent to wso2 EI with special characters</br> **Then**:.| Not Automated|
