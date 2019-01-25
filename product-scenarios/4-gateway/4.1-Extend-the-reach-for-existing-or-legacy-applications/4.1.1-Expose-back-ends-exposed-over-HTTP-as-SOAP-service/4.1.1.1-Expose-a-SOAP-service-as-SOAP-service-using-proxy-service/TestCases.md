### 4.1.1.1 - Expose a SOAP service as SOAP service using proxy service


| Test Case ID| Test Case| Test Case Description| Status|
| ----------| --------| ----------| ------|
| 4.1.1.1.1 | Front the back-end using pass-through proxy template |**Given**:Test environment is set properly. </br> **When**: </br> **Then**:| Automated|
| 4.1.1.1.2 | Front the back-end using custom proxy template |**Given**:Test environment is set properly. </br> **When**: </br> **Then**:| Automated|
| 4.1.1.1.3 | Front the back-end using Log Forward proxy template |**Given**:Test environment is set properly. </br> **When**: </br> **Then**:| Automated|
| 4.1.1.1.4 | Front the back-end using WSDL-Based proxy proxy template |**Given**:Test environment is set properly. </br> **When**: </br> **Then**:| Automated|
| 4.1.1.1.5 | Front the back-end using proxy specifying service URL using named endpoint within endpoint tag under target tag |**Given**:Test environment is set properly. </br> **When**: </br> **Then**:| Automated|
| 4.1.1.1.6 | Front the back-end using specifying service URL using named endpoint via "endpoint" attribute in target tag |**Given**:Test environment is set properly. </br> **When**: </br> **Then**:| Automated|
| 4.1.1.1.7 | Publishing WSDL of the service by loading from the registry |**Given**:Test environment is set properly. </br> **When**: </br> **Then**:| Automated|
| 4.1.1.1.8 | Publishing WSDL of the service by loading from uri |**Given**:Test environment is set properly. </br> **When**: </br> **Then**:| Automated|
| 4.1.1.1.9 | Publishing WSDL of the service by loading from file system |**Given**:Test environment is set properly. </br> **When**: </br> **Then**:| Not Automated|
| 4.1.1.1.10| Publishing WSDL of the service by providing the WSDL inline |**Given**:Test environment is set properly. </br> **When**: </br> **Then**:| Automated|
| 4.1.1.1.11| Publishing WSDL of the service by loading from registry and loading imported resources by the WSDL from registry |**Given**:Test environment is set properly. </br> **When**: </br> **Then**:| Automated|
| 4.1.1.1.12| Publishing WSDL of the service by loading from registry and loading imported resources by the WSDL from URI |**Given**:Test environment is set properly. </br> **When**: </br> **Then**:| Not Automated|
| 4.1.1.1.13| Publishing original WSDL instead of generated WSDL by the proxy service by setting "useOriginalwsdl" to true |**Given**:Test environment is set properly. </br> **When**: </br> **Then**:| Automated|
| 4.1.1.1.14| Publishing original WSDL instead of generated WSDL by the proxy service by setting "useOriginalwsdl" to true and disable updating the port address by setting "modifyUserWSDLPortAddress" to false |**Given**:Test environment is set properly. </br> **When**: </br> **Then**:| Automated|
| 4.1.1.1.15| Mark the proxy service as faulty when WSDL is unavailable by setting "enablePublishWSDLSafeMode" to false |**Given**:Test environment is set properly. </br> **When**: </br> **Then**:| Not Automated|
| 4.1.1.1.16| Proxy service become inaccessible when WSDL is not available when "enablePublishWSDLSafeMode" set to true |**Given**:Test environment is set properly. </br> **When**: </br> **Then**:| Not Automated|
| 4.1.1.1.17| Display absolute path of the referred schemas of the WSDL instead of the relative paths by default |**Given**:Test environment is set properly. </br> **When**: </br> **Then**:| Not Automated|
| 4.1.1.1.18| Display relative path of the referred schemas of the WSDL by setting "showAbsoluteSchemaURL" to false |**Given**:Test environment is set properly. </br> **When**: </br> **Then**:| Not Automated|
| 4.1.1.1.19| Set full proxy URL as the prefix to the schema location of the imports in proxy WSDL by setting "showProxySchemaURL" true |**Given**:Test environment is set properly. </br> **When**: </br> **Then**:| Not Automated|
| 4.1.1.1.19| Send a large soap message using pass-through proxy |**Given**:Test environment is set properly. </br> **When**: </br> **Then**:| Automated|