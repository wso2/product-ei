/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *   * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package samples.userguide;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.impl.builder.StAXSOAPModelBuilder;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;

public class AMQPConsumer {
    private Connection connection;
    private MessageConsumer messageConsumer;
    private Session session;
    private String inSymbol;
    private String inQty;
    private String inClOrderID;
    private int execID = 1;

    private static final String CLASS = "AMQPConsumer";
    private static final String PROPERTY_FILE =
            "../../repository/conf/sample/resources/fix/direct.properties";
    private static final String PROP_FILE_NAME = "propfile";
    private static final String LOOKUP_CODE_CON = "directQueue";
    private static final String LOOKUP_CODE_REP = "replyQueue";
    private static final String CONNECTION_FACTORY = "qpidConnectionfactory";
    private static final String FIX_MSG = "message";
    private static final String FIX_MSG_BODY = "body";
    private static final String FIX_MSG_ID = "id";
    private static final String FIX_MSG_SYMBOL = "55";
    private static final String FIX_MSG_CLORDID = "11";
    private static final String FIX_MSG_ORDQTY = "38";

    /**
     * Main method to execute the consumer sample.
     */
    public static void main(String[] args) {
        AMQPConsumer syncConsumer = new AMQPConsumer();
        syncConsumer.runTest();
    }

    /**
     * Start the sample by creating and listerning to a defined Queue
     */
    private void runTest() {
        try {
            String fileName = getProperty(PROP_FILE_NAME, PROPERTY_FILE);
            // Load JNDI properties from the configuration file
            Properties properties = new Properties();
            InputStream inStream = new FileInputStream(new File(fileName).getAbsolutePath());
            properties.load(inStream);
            //Create the initial context
            Context ctx = new InitialContext(properties);
            // look up destination
            Destination destination = (Destination) ctx.lookup(LOOKUP_CODE_CON); // Listerning queue
            Destination replyQueue = (Destination) ctx.lookup(LOOKUP_CODE_REP);  // Reply queue
            // Lookup the connection factory
            ConnectionFactory conFac = (ConnectionFactory) ctx.lookup(CONNECTION_FACTORY);
            connection = conFac.createConnection();
            // As this application is using a MessageConsumer we need to set an ExceptionListener on the connection
            // so that errors raised within the JMS client library can be reported to the application
            System.out.println(
                    CLASS +
                            ": Setting an ExceptionListener on the connection as sample uses a MessageConsumer");
            connection.setExceptionListener(new ExceptionListener() {
                public void onException(JMSException jmse) {
                    // The connection may have broken invoke reconnect code if available.
                    // The connection may have broken invoke reconnect code if available.
                    System.err.println(CLASS +
                            ": The sample received an exception through the ExceptionListener" +
                            jmse.getMessage());
                    jmse.printStackTrace();
                    System.exit(0);
                }
            });
            createSession(conFac, destination); // Call to create the sessions
            createRepQueue(conFac,
                    replyQueue); // Call to create the Reply Queue and close the session
            connection.start(); // Start the connection
            System.out.println(
                    CLASS + ": Starting connection so the MessageConsumer can receive messages");
            onMessage();
        } catch (Exception e) {
            //TODO: handle the exception
            System.out.println("ERROR : " + e.toString());
            e.printStackTrace();
        }
    }

    /**
     * @param destination
     */
    private void createSession(ConnectionFactory conFac, Destination destination)
            throws JMSException {
        // Create a session on the connection
        // This session is a default choice of non-transacted and uses the auto acknowledge feature of a session.
        System.out.println(CLASS + ": Creating a non-transacted, auto-acknowledged session");
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        // Create a MessageConsumer
        System.out.println(CLASS + ": Creating a MessageConsumer");
        messageConsumer = session.createConsumer(destination);
    }

    /**
     * Create reply queue
     *
     * @param replyQueue
     * @throws JMSException
     */
    private void createRepQueue(ConnectionFactory conFac, Destination replyQueue)
            throws JMSException {
        MessageProducer messageProducer = session.createProducer(replyQueue);
        System.out.println(CLASS + ": Reply queue created");
    }

    /**
     * Not a typical message callback, this method will listern and pole the messages
     */
    private void onMessage() throws JMSException, XMLStreamException {
        Message message;
        boolean end = false;
        while (!end) {
            message = messageConsumer.receive();
            String text;
            if (message instanceof TextMessage) {
                text = ((TextMessage) message).getText();
            } else {
                byte[] body = new byte[(int) ((BytesMessage) message).getBodyLength()];
                ((BytesMessage) message).readBytes(body);
                text = new String(body);
            }
            System.out.println(CLASS + ": Received  message:  " + text);
            if (message.getJMSReplyTo() != null) {
                // Send the Execution back to the sendar
                parseOrder(text);
                sendExecution(message);
            }
        }

    }

    /**
     * @param payload XML message content came inside the JMS message
     * @throws XMLStreamException on error
     */
    private void parseOrder(String payload) throws XMLStreamException {
        InputStream is = new ByteArrayInputStream(payload.getBytes());
        javax.xml.stream.XMLStreamReader parser = XMLInputFactory
                .newInstance().createXMLStreamReader(is);
        StAXSOAPModelBuilder builder = new StAXSOAPModelBuilder(parser,
                null);
        SOAPEnvelope envelope = (SOAPEnvelope) builder.getDocumentElement();
        // retrieve SOAP body
        SOAPBody soapBody = envelope.getBody();
        OMElement messageNode = soapBody.getFirstChildWithName(new QName(
                FIX_MSG));
        Iterator<?> messageElements = (Iterator<?>) messageNode
                .getChildElements();
        while (messageElements.hasNext()) {
            OMElement node = (OMElement) messageElements.next();
            if (node.getQName().getLocalPart().equals(FIX_MSG_BODY)) {
                Iterator<?> bodyElements = (Iterator<?>) node.getChildElements();
                while (bodyElements.hasNext()) {
                    OMElement bodyNode = (OMElement) bodyElements.next();
                    String tag = bodyNode
                            .getAttributeValue(new QName(FIX_MSG_ID));
                    String value = bodyNode.getText();
                    if (tag.equals(FIX_MSG_SYMBOL)) {
                        inSymbol = value;
                    } else if (tag.equals(FIX_MSG_CLORDID)) {
                        inClOrderID = value;
                    } else if (tag.equals(FIX_MSG_ORDQTY)) {
                        inQty = value;
                    }
                }
            }
        }
    }

    /**
     * @param message incoming message
     * @throws JMSException on error
     */
    private void sendExecution(Message message) throws JMSException {
        String repValue =
                "<m0:message xmlns:m0=\"http://services.samples/xsd/\" inSeession=\"FIX.4.0:EXEC-->SYNAPSE\" count=\"2\">\n"
                        + "<m0:header>"
                        + "<m0:field m0:id=\"35\"><![CDATA[8]]></m0:field>"
                        + "<m0:field m0:id=\"52\"><![CDATA[20080618-08:41:56]]></m0:field>"
                        + "</m0:header>"
                        + "<m0:body>"
                        + "<m0:field m0:id=\"6\"><![CDATA[12.3]]></m0:field>"
                        + "<m0:field m0:id=\"11\"><![CDATA["
                        + inClOrderID
                        + "]]></m0:field>"
                        + "<m0:field m0:id=\"14\"><![CDATA["
                        + inQty
                        + "]]></m0:field>"
                        + "<m0:field m0:id=\"17\"><![CDATA["
                        + execID
                        + "]]></m0:field>"
                        + "<m0:field m0:id=\"20\"><![CDATA[0]]></m0:field>"
                        + "<m0:field m0:id=\"31\"><![CDATA[12.3]]></m0:field>"
                        + "<m0:field m0:id=\"32\"><![CDATA["
                        + inQty
                        + "]]></m0:field>"
                        + "<m0:field m0:id=\"37\"><![CDATA[2]]></m0:field>"
                        + "<m0:field m0:id=\"38\"><![CDATA["
                        + inQty
                        + "]]></m0:field>"
                        + "<m0:field m0:id=\"39\"><![CDATA[2]]></m0:field>"
                        + "<m0:field m0:id=\"54\"><![CDATA[1]]></m0:field>"
                        + "<m0:field m0:id=\"55\"><![CDATA["
                        + inSymbol
                        + "]]></m0:field>"
                        + "<m0:field m0:id=\"150\"><![CDATA[2]]></m0:field>"
                        + "<m0:field m0:id=\"151\"><![CDATA[0]]></m0:field>"
                        + "</m0:body>"
                        + "<m0:trailer>"
                        + "</m0:trailer>"
                        + "</m0:message>";
        execID++;
        TextMessage repMessage = session.createTextMessage(repValue);
        repMessage.setJMSCorrelationID(message.getJMSMessageID());
        MessageProducer replyProducer = session.createProducer(message.getJMSReplyTo());
        replyProducer.send(repMessage);
        System.out.println("Execution sent: " + repMessage.getText());
    }

    /**
     * Get the system properties
     *
     * @param name property name
     * @param def  default value
     * @return property value
     */
    private static String getProperty(String name, String def) {
        String result = System.getProperty(name);
        if (result == null || result.length() == 0) {
            result = def;
        }
        return result;
    }
}

