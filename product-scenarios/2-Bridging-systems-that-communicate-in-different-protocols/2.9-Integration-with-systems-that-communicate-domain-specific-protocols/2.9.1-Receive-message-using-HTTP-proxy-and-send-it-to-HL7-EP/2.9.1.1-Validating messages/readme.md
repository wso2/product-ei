# 2.9.1.1-Validating messages 

## When to use
By default, the HL7 transport validates messages before building their XML representation.

## Sample use-case
--sample use-case

## Supported versions

## Pre-requisites
By default, the HL7 transport validates messages before building their XML representation. You configure validation with the following parameter in the proxy service:

```xml
<proxy>...
   <parameter name="transport.hl7.ValidateMessage">true|false</parameter> <!-- default is true -->
</proxy>
```

When transport.hl7.ValidateMessage is set to false, you can set the following parameters to handle invalid messages:

1. transport.hl7.BuildInvalidMessages : when set to true, builds a SOAP envelope with the contents of the raw HL7 message inside the <rawMessage> element.
2. transport.hl7.PassThroughInvalidMessages : when  BuildInvalidMessages is set to true, you use this parameter to specify whether to pass this message through (true) or to throw a fault (false).

## Development guidelines

## REST API (if available)
N/A

## Deployment guidelines

## Test cases

| ID | Summary |
| ------------- | ------------- |
| 2.9.1.1.1  | Validating messages transport.hl7.BuildInvalidMessages : when set to true    |
| 2.1.1.1.2  | Validating messages transport.hl7. PassThroughInvalidMessages : when  BuildInvalidMessages is set to true  |
| 2.1.1.1.3  | Failover due to backend is not started and then connection refused     |
| 2.1.1.1.4  | Backend is not responding retry after given timeout period |