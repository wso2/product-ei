### 1.2.2-Converting POX message to JSON using the MessageType property

| Test Case ID| Test Case| Test Case Description| Status|
| ----------| --------| ----------| ------|
| 1.2.2.1| Converting a POX message to JSON using Messsage Type Property| **Given**:Test environment is set properly. </br> **When**:A POX message is sent to wso2 EI. </br> **Then**:It should be converted to a json message.| Automated|
| 1.2.2.2| Handling malformed POX messages while converting to JSON using Messsage Type Property| **Given**:Test environment is set properly. </br> **When**:A malformed POX message is sent to wso2 EI</br> **Then**:| Automated|
| 1.2.2.3| Converting large sized POX message to JSON using MessageType property| **Given**:Test environment is set properly. </br> **When**:A large size POX message is sent to wso2 EI</br> **Then**:| Automated|
| 1.2.2.4| Converting POX message with special characters using MessageType Property| **Given**:Test environment is set properly. </br> **When**:A pox message with special characters is sent to wso2 EI. </br> **Then**:It should be converted to a json message.| Not Automated|
