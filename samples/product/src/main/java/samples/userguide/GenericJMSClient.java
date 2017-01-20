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

import javax.jms.*;
import javax.naming.InitialContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class GenericJMSClient {
    private QueueConnection connection;
    private QueueSession session;
    private QueueSender sender;

    private static String getProperty(String name, String def) {
        String result = System.getProperty(name);
        if (result == null || result.length() == 0) {
            result = def;
        }
        return result;
    }

    public static void main(String[] args) throws Exception {

        String dest  = getProperty("jms_dest", "dynamicQueues/JMSTextProxy");
        String type  = getProperty("jms_type", "text");
        String param = getProperty("jms_payload",
            getRandom(100, 0.9, true) + " " + (int) getRandom(10000, 1.0, true) + " IBM");
        String sMsgCount = getProperty("jms_msgcount", null);

        GenericJMSClient app = new GenericJMSClient();
        int msgCount = sMsgCount == null ? 1 : Integer.parseInt(sMsgCount);
        app.connect(dest);
        if ("text".equalsIgnoreCase(type)) {
            for (int i=0; i<msgCount; i++) {
                app.sendTextMessage(param);
            }
        } else if ("binary".equalsIgnoreCase(type)) {
            for (int i=0; i<msgCount; i++) {
                app.sendBytesMessage(getBytesFromFile(param));
            }
        } else if ("pox".equalsIgnoreCase(type)) {
            for (int i=0; i<msgCount; i++) {
                app.sendTextMessage(
                    "<m:placeOrder xmlns:m=\"http://services.samples\">\n" +
                    "    <m:order>\n" +
                    "        <m:price>" + getRandom(100, 0.9, true) + "</m:price>\n" +
                    "        <m:quantity>" + (int) getRandom(10000, 1.0, true) + "</m:quantity>\n" +
                    "        <m:symbol>" + param + "</m:symbol>\n" +
                    "    </m:order>\n" +
                    "</m:placeOrder>");
            }
        } else {
            System.out.println("Unknown JMS message type");
        }
        app.shutdown();
    }

    private void connect(String destName) throws Exception {

        Properties env = new Properties();
        String factoryURL = System.getProperty("java.naming.factory.url.pkgs");
        String connectionFactoryName
                = getProperty("transport.jms.ConnectionFactoryJNDIName", "ConnectionFactory");

        if (System.getProperty("java.naming.provider.url") == null) {
            env.put("java.naming.provider.url", "tcp://localhost:61616");
        }
        if (System.getProperty("java.naming.factory.initial") == null) {
            env.put("java.naming.factory.initial",
                "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        }
        if (connectionFactoryName != null) {
            env.put("transport.jms.ConnectionFactoryJNDIName", connectionFactoryName);
        }
        if (factoryURL != null) {
            env.put("java.naming.factory.url.pkgs", factoryURL);
        }
        
        InitialContext ic = new InitialContext(env);
        QueueConnectionFactory confac = (QueueConnectionFactory) ic.lookup("ConnectionFactory");
        connection = confac.createQueueConnection();
        session = connection.createQueueSession(false, QueueSession.AUTO_ACKNOWLEDGE);
        sender = session.createSender((Queue)ic.lookup(destName));
    }

    private void shutdown() throws Exception {
        sender.close();
        session.close();
        connection.close();
    }

    private void sendBytesMessage(byte[] payload) throws Exception {
        BytesMessage bm = session.createBytesMessage();
        bm.writeBytes(payload);
        sender.send(bm);
    }

    private void sendTextMessage(String payload) throws Exception {
        TextMessage tm = session.createTextMessage(payload);
        sender.send(tm);
    }

    public static byte[] getBytesFromFile(String fileName) throws IOException {

        File file = new File(fileName);
        InputStream is = new FileInputStream(file);
        long length = file.length();

        byte[] bytes = new byte[(int) length];

        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
            && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }

        is.close();
        return bytes;
    }

    private static double getRandom(double base, double varience, boolean onlypositive) {
        double rand = Math.random();
        return (base + ((rand > 0.5 ? 1 : -1) * varience * base * rand))
            * (onlypositive ? 1 : (rand > 0.5 ? 1 : -1));
    }

}
