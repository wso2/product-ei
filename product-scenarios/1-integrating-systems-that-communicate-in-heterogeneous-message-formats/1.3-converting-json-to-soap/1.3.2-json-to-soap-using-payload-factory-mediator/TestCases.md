### 1.3.2 - Converting JSON message to SOAP using payload factory mediator


| Test Case ID| Test Case| Test Case Description| Status|
| ----------| --------| ----------| ------|
| 1.3.2.1| A simple message transformation with PayloadFactory mediator| **Given**:Test environment is set properly. </br> **When**:A json message is sent to wso2 EI. </br> **Then**:It should be converted to a soap message.| Automated|
| 1.3.2.2| Transformation of a JSON message with special characters| **Given**:Test environment is set properly. </br> **When**:A json message with special characters is sent to wso2 EI</br> **Then**:| Not Automated|
| 1.3.2.3| Handling malformed JSON while doing the transformation| **Given**:Test environment is set properly. </br> **When**:A malformed json is sent to wso2 EI</br> **Then**:| Not Automated|
| 1.3.2.4| Converting a JSON message into a SOAP messages with attachments (MTOM)| **Given**:Test environment is set properly. </br> **When**:A json message with attachment is sent to wso2 EI</br> **Then**:| Not Automated|
| 1.3.2.5| Use payload stored in the registry as the source payload| **Given**:Test environment is set properly. </br> **When**:A json message,which is stored in registry, wso2 EI</br> **Then**:| Not Automated|
| 1.3.2.6| Converting JSON with single element arrays into a SOAP message| **Given**:Test environment is set properly. </br> **When**:A json message with single element array is sent to wso2 EI</br> **Then**:| Not Automated|
