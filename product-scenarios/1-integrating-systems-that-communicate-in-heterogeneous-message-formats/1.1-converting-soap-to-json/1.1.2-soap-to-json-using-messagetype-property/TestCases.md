### 1.1.2-Converting SOAP message to JSON using the MessageType property

| Test Case ID| Test Case| Test Case Description| Status|
| ----------| --------| ----------| ------|
| 1.1.2.1| Converting a SOAP message to JSON using Messsage Type Property| **Given**:Test environment is set properly. </br> **When**:A soap request is sent to wso2 EI. </br> **Then**:It should be converted to a json message.| Automated|
| 1.1.2.2| Handling malformed SOAP messages while converting to JSON using Messsage Type Property| **Given**:Test environment is set properly. </br> **When**:A malformed soap request is sent to wso2 EI</br> **Then**:| Automated|
| 1.1.2.3| Converting large sized SOAP message to JSON using MessageType property| **Given**:Test environment is set properly. </br> **When**:A large size soap request is sent to wso2 EI</br> **Then**:| Automated|
