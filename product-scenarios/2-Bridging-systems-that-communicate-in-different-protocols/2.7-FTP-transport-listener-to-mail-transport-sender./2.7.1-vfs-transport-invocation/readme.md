# 2.7.1-VFS transport listener will pick the file from the directory in the FTP server and send it to the Axis2 service.

## When to use

VFS transport listener will pick the file from the directory in the FTP server and send it to the Axis2 service.

## Sample use-case

## Supported versions

## Pre-requisites
- You will need access to an FTP server and an SMTP server to try this sample.
- Start the Axis2 server and deploy the SimpleStockQuoteService if not already done.
- Enable mail transport sender in the axis2.xml inside <EI_HOME>/conf.
- Create a new test directory in the FTP server. Open <EI_HOME>/samples/service-bus/synapse_sample_255.xml and edit the following values. Change the transport.vfs.FileURI parameter value point to the test directory at the FTP server. Change the outSequence endpoint address URI email address to a working email address. Values you have to change are marked with "<!--CHANGE-->".
- Start the Synapse configuration numbered 255: wso2esb-samples.sh -sn 255
- Copy <EI_HOME>/samples/service-bus/resources/vfs/test.xml to the ftp directory given in transport.vfs.FileURI below.

```xml
<definitions xmlns="http://ws.apache.org/ns/synapse">
    <proxy name="StockQuoteProxy" transports="vfs">
        <parameter name="transport.vfs.FileURI">vfs:ftp://guest:guest@localhost/test?vfs.passive=true</parameter> <!--CHANGE-->
        <parameter name="transport.vfs.ContentType">text/xml</parameter>
        <parameter name="transport.vfs.FileNamePattern">.*\.xml</parameter>
        <parameter name="transport.PollInterval">15</parameter>
        <target>
            <inSequence>
                <header name="Action" value="urn:getQuote"/>
            </inSequence>
            <endpoint>
                <address uri="http://localhost:9000/services/SimpleStockQuoteService"/>
            </endpoint>
            <outSequence>
                <property action="set" name="OUT_ONLY" value="true"/>
                <send>
                    <endpoint>
                        <address uri="mailto:user@host"/> <!--CHANGE-->
                    </endpoint>
                </send>
            </outSequence>
        </target>
        <publishWSDL uri="file:samples/service-bus/resources/proxy/sample_proxy_1.wsdl"/>
    </proxy>
</definitions>
```
VFS transport listener will pick the file from the directory in the FTP server and send it to the Axis2 service. The file in the FTP directory will be deleted. The response will be sent to the given e-mail address.

Setting up Mail Transport Sender

To enable the mail transport sender for samples, you need to uncomment the mail transport sender configuration in the <EI_HOME>/conf/axis2/axis2.xml. Uncomment the mail transport sender sample configuration and make sure it points to a valid SMTP configuration for any actual scenarios.

```xml
<transportSender name="mailto" class="org.apache.synapse.transport.mail.MailTransportSender">
    <parameter name="mail.smtp.host">smtp.gmail.com</parameter>
    <parameter name="mail.smtp.port">587</parameter>
    <parameter name="mail.smtp.starttls.enable">true</parameter>
    <parameter name="mail.smtp.auth">true</parameter>
    <parameter name="mail.smtp.user">synapse.demo.0</parameter>
    <parameter name="mail.smtp.password">mailpassword</parameter>
    <parameter name="mail.smtp.from">synapse.demo.0@gmail.com</parameter>
</transportSender>
'''

## Development guidelines

## REST API (if available)
N/A

## Deployment guidelines

## Test cases

| ID | Summary |
| ------------- | ------------- |
| 2.7.1.1  | Switching from FTP Transport Listener to Mail Transport Sender by start FTP server and SMTP server.   |
| 2.7.1.2  | Failover due to FTP server is not started and then connection refused.    |
| 2.7.1.3  | Failover due to SMTP server is not started and then connection refused.   |
| 2.7.1.3  |Backend is not responding retry after given timeout period.|
