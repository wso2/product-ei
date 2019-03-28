# 2.8.1-MailTo transport supports sending messages (E-Mail) over SMTP and receiving messages over POP3 or IMAP. 

## When to use

MailTo transport supports sending messages (E-Mail) over SMTP and receiving messages over POP3 or IMAP. 

## Sample use-case

## Supported versions

## Pre-requisites

- You will need access to an e-mail account.
- Start the Axis2 server and deploy the SimpleStockQuoteService if not already done.
- Enable the mail transport listener in the axis2.xml located inside <EI_HOME>/conf. Simply uncomment the relevant 
  transport receiver entry in the file.
- Enable mail transport sender in the axis2.xml located in <EI_HOME>/conf. See [MailTo transport](https://docs.wso2
.com/display/EI640/MailTo+Transport) for details.
- Start the Synapse configuration.
- Send a plain/text e-mail (Make sure you switch to Plain text mode when you are composing the email) with the following body and any custom Subject from your mail account to the mail address synapse.demo.1@gmail.com.

```xml
<m0:getQuote xmlns:m0="http://services.samples">
    <m0:request>
        <m0:symbol>IBM</m0:symbol>
    </m0:request>
</m0:getQuote>
```

- After a few seconds (for example 30 seconds), you should receive a POX response in your e-mail account with the stock quote reply.

```xml
<!-- Using the mail transport -->
<definitions xmlns="http://ws.apache.org/ns/synapse">
    <proxy name="StockQuoteProxy" transports="mailto">
        <parameter name="transport.mail.Address">synapse.demo.1@gmail.com</parameter>
        <parameter name="transport.mail.Protocol">pop3</parameter>
        <parameter name="transport.PollInterval">5</parameter>
        <parameter name="mail.pop3.host">pop.gmail.com</parameter>
        <parameter name="mail.pop3.port">995</parameter>
        <parameter name="mail.pop3.user">synapse.demo.1</parameter>
        <parameter name="mail.pop3.password">mailpassword1</parameter>
        <parameter name="mail.pop3.socketFactory.class">javax.net.ssl.SSLSocketFactory</parameter>
        <parameter name="mail.pop3.socketFactory.fallback">false</parameter>
        <parameter name="mail.pop3.socketFactory.port">995</parameter>
        <parameter name="transport.mail.ContentType">application/xml</parameter>
        <target>
            <inSequence>
                <property name="senderAddress" expression="get-property('transport', 'From')"/>
                <log level="full">
                    <property name="Sender Address" expression="get-property('senderAddress')"/>
                </log>
                <send>
                    <endpoint>
                        <address uri="http://localhost:9000/services/SimpleStockQuoteService"/>
                    </endpoint>
                </send>
            </inSequence>
            <outSequence>
                <property name="Subject" value="Custom Subject for Response" scope="transport"/>
                <header name="To" expression="fn:concat('mailto:', get-property('senderAddress'))"/>
                <log level="full">
                    <property name="message" value="Response message"/>
                    <property name="Sender Address" expression="get-property('senderAddress')"/>
                </log>
                <send/>
            </outSequence>
        </target>
        <publishWSDL uri="file:samples/service-bus/resources/proxy/sample_proxy_1.wsdl"/>
    </proxy>
</definitions>
```

## Development guidelines

## REST API (if available)
N/A

## Deployment guidelines

## Test cases

| ID | Summary |
| ------------- | ------------- |
| 2.8.1.1  | MailTo transport supports sending messages (E-Mail) over SMTP  and receiving messages over POP3.   |
| 2.8.1.2  | Failover due to backend is not started and then connection refused in POP3.   |
| 2.8.1.3  | When backend is not responding retry after given timeout period in POP3.    |
| 2.8.1.4  | MailTo transport supports sending messages (E-Mail) over SMTP and receiving messages over IMAP.|
| 2.8.1.5  |Failover due to backend is not started and then connection refused in IMAP. |
| 2.8.1.6  |When backend is not responding retry after given timeout period in IMAP. |