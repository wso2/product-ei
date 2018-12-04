# 2.9.1.3- Configuring application acknowledgement

## When to use
If you want to wait for the application's response before sending the acknowledgment message (see Configuring application acknowledgement), you add the HL7_APPLICATION_ACK property to the inSequence and any additional HL7 properties and transport parameters as needed.

## Sample use-case
--sample use-case

## Supported versions

## Pre-requisites
In general, we don't wait for the back-end application's response before sending an "accept-acknowledgement" message to the client. If you do want to wait for the application's response before sending the message, define the following property in the InSequence:
<property name="HL7_APPLICATION_ACK" value="true" scope="axis2"/> 

In this case, the request thread will wait until the back-end application returns the response before sending the "accept-acknowledgement" message to the client. You can configure how long request threads wait for the application's response by configuring the time-out in milliseconds at the transport level:

```xml
<transportReceiver name="hl7" class="org.wso2.carbon.business.messaging.hl7.transport.HL7TransportListener">
    <parameter name="transport.hl7.TimeOut">1000</parameter>
</transportReceiver>
```

## Development guidelines

## REST API (if available)
N/A

## Deployment guidelines

## Test cases

| ID | Summary |
| ------------- | ------------- |
| 2.9.1.3.1  | Set HL7_APPLICATION_ACK" value="true" .(don't wait for the back-end application's response before sending an "accept-acknowledgement" message to the client)|
| 2.9.1.3.2  | Failover due to backend is not started and then connection refused       |
| 2.9.1.3.3  | Backend is not responding retry after given timeout period|
