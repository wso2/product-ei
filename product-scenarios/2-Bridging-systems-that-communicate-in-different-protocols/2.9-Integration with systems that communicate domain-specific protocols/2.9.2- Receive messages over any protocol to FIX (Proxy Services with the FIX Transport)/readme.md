# 2.9.2- Receive messages over any protocol to FIX (Proxy Services with the FIX Transport)

## When to use
Receive messages over any protocol to FIX (Proxy Services with the FIX Transport).

## Sample use-case
In this sections walk you through the FIX (Financial Information eXchange) transport with Proxy Services.

## Supported versions

## Pre-requisites

- You will need the two sample FIX applications that come with Quickfix/J (Banzai and Executor). Configure the two 
applications to establish sessions with the integrator. 
- For information on setting up Quickfix/J, see [Configuring WSO2 Enterprise Integrator to use the FIX transport](https://docs.wso2.com/display/EI640/Setting+Up+the+ESB+Samples).
- Start Banzai and Executor.
- Enable FIX transport in the Synapse axis2.xml. 
- Configure Synapse for FIX samples. 
- Open the <EI_HOME>/samples/service-bus/synapse_sample_257.xml file and make sure that transport.fix
.AcceptorConfigURL property points to the fix-synapse.cfg file you created. Also make sure that transport.fix. InitiatorConfigURL property points to the synapse-sender.cfg file you created. Once done, you can start the Synapse configuration
- Send an order request from Banzai to the EI.

WSO2 EI will create a session with an Executor and forward the order request. The responses coming from the Executor 
will be sent back to Banzai.

```xml

<definitions xmlns="http://ws.apache.org/ns/synapse">
    <proxy name="FIXProxy" transports="fix">
        <parameter name="transport.fix.AcceptorConfigURL">file:/home/synapse_user/fix-config/fix-synapse.cfg</parameter>
        <parameter name="transport.fix.InitiatorConfigURL">file:/home/synapse_user/fix-config/synapse-sender.cfg</parameter>
        <parameter name="transport.fix.AcceptorMessageStore">file</parameter>
        <parameter name="transport.fix.InitiatorMessageStore">file</parameter>
        <target>
            <endpoint>
                <address uri="fix://localhost:19876?BeginString=FIX.4.0&amp;SenderCompID=SYNAPSE&amp;TargetCompID=EXEC"/>
            </endpoint>
        <inSequence>
        <log level="full"/>
        </inSequence>
            <outSequence>
                <log level="full"/>
                <send/>
            </outSequence>
        </target>
    </proxy>
</definitions>

```

## Development guidelines

Configuring Sample FIX Applications

If you use a binary distribution of Quickfix/J, the two samples and their configuration files are all packed to a single JAR file called quickfixj-examples.jar. You will have to extract the JAR file, modify the configuration files and pack them to a JAR file again under the same name.

You can pass the new configuration file as a command line parameter too, in that case you do not need to modify the 
quickfixj-examples.jar. You can copy the config files from <EI_HOME>/samples/service-bus/resources/fix folder to $QFJ_HOME/etc folder. Execute the sample apps from <QFJ_HOME>/bin," "./banzai.sh/bat ../etc/banzai.cfg executor.sh/bat ../etc/executor.cfg.

Locate and edit the FIX configuration file of Executor to be as follows. This file is usually named executor.cfg.

Locate and edit the FIX configuration file of Executor to be as follows. This file is usually named executor.cfg.

```xml
[default]
    FileStorePath=examples/target/data/executor
    ConnectionType=acceptor
    StartTime=00:00:00
    EndTime=00:00:00
    HeartBtInt=30
    ValidOrderTypes=1,2,F
    SenderCompID=EXEC
    TargetCompID=SYNAPSE
    UseDataDictionary=Y
    DefaultMarketPrice=12.30
 
    [session]
    BeginString=FIX.4.0
    SocketAcceptPort=19876
```

Locate and edit the FIX configuration file of Banzai to be as follows. This file is usually named banzai.cfg.

```xml
[default]
    FileStorePath=examples/target/data/banzai
    ConnectionType=initiator
    SenderCompID=BANZAI
    TargetCompID=SYNAPSE
    SocketConnectHost=localhost
    StartTime=00:00:00
    EndTime=00:00:00
    HeartBtInt=30
    ReconnectInterval=5
 
    [session]
    BeginString=FIX.4.0
    SocketConnectPort=9876
```

The FileStorePath property in the above two files should point to two directories in your local file system. The launcher scripts for the sample application can be found in the bin directory of  Quickfix/J distribution.

Setting up FIX Transport

To run the FIX samples used in this guide, you need a local Quickfix/J installation (http://www.quickfixj.org). Download Quickfix/J from: http://www.quickfixj.org/downloads.

To enable the FIX transport for samples, first you must deploy the Quickfix/J libraries into the <EI_HOME>/lib directory
 of the EI. Generally the  following libraries should be deployed into the EI.

```xml
    quickfixj-core-1.4.0.jar
    quickfixj-msg-fix40-1.4.0.jar
    quickfixj-msg-fix42-1.4.0.jar
    quickfixj-msg-fix41-1.4.0.jar
    quickfixj-msg-fix43-1.4.0.jar
    quickfixj-msg-fix44-1.4.0.jar
    quickfixj-msg-fix50-1.4.0.jar
    mina-core-1.1.0.jar
    slf4j-jdk14-1.5.3.jar
    slf4j-api-1.5.3.jar
```

Then uncomment the FIX transport sender and FIX transport receiver configurations in the <EI_HOME>/conf/axis2.xml. 
Simply locate and uncomment the FIXTransportSender and FIXTransportListener sample configurations. Alternatively if 
the FIX transport management bundle is in use, you can enable the FIX transport listener and the sender from the WSO2 EI
management console. Login to the console and navigate to "Transports" on management menu. Scroll down to locate the sections related to the FIX transport. Simply click on the "Enable" links to enable the FIX listener and the sender.


## REST API (if available)
N/A

## Deployment guidelines

## Test cases

| ID | Summary |
| ------------- | ------------- |
| 2.9.2.1  | Receive message from HTTP/S and switch them to FIX|
| 2.9.2.2  | Receive message from FIX and switch them to HTTP/S|
| 2.9.2.3  | Receive messages and switching between FIX versions|
| 2.9.2.4  | Receive message from FIX and switch them to AMQP|
| 2.9.2.5  | Failover due to backend is not started and then connection refused|
| 2.9.2.6  | Backend is not responding retry after given timeout period|