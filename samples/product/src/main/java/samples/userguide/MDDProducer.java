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
import javax.naming.NamingException;
import java.util.Properties;
import java.io.*;

public class MDDProducer {

    private static String getProperty(String name, String def) {
        String result = System.getProperty(name);
        if (result == null || result.length() == 0) {
            result = def;
        }
        return result;
    }

    public static void main(String[] args) throws Exception {

        String dest  = getProperty("jms_dest", "dynamicQueues/JMSBinaryProxy");
        String type  = getProperty("jms_type", "binary");
        String symbol = getProperty("symbol","MSFT");
        String price = getProperty("price","100.20");
        String market = getProperty("market","NYSE");

        MDDProducer app = new MDDProducer();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        dos.writeDouble(getRandom(1,1,true));
        dos.writeUTF(symbol);
        dos.writeDouble(Double.valueOf(price));
        dos.writeUTF(market);
        dos.flush();
        app.sendBytesMessage(dest,bos.toByteArray());
        dos.close();
        bos.close();
    }


    private void sendBytesMessage(String destName,byte[] buffer) throws Exception {
        InitialContext ic = getInitialContext();
        QueueConnectionFactory confac = (QueueConnectionFactory) ic.lookup("ConnectionFactory");
        QueueConnection connection = confac.createQueueConnection();
        QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
        BytesMessage bm = session.createBytesMessage();
        bm.writeBytes(buffer);
        QueueSender sender = session.createSender((Queue)ic.lookup(destName));
        sender.send(bm);
        sender.close();
        session.close();
        connection.close();
    }


    private InitialContext getInitialContext() throws NamingException {
        Properties env = new Properties();
        if (System.getProperty("java.naming.provider.url") == null) {
            env.put("java.naming.provider.url", "tcp://localhost:61616");
        }
        if (System.getProperty("java.naming.factory.initial") == null) {
            env.put("java.naming.factory.initial",
                "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        }
        return new InitialContext(env);
    }

    private static double getRandom(double base, double varience, boolean onlypositive) {
        double rand = Math.random();
        return (base + ((rand > 0.5 ? 1 : -1) * varience * base * rand))
            * (onlypositive ? 1 : (rand > 0.5 ? 1 : -1));
    }
    private static byte[] intToByteArray(int value) {
            return new byte[] {
                    (byte)(value >>> 24),
                    (byte)(value >>> 16),
                    (byte)(value >>> 8),
                    (byte)value};
    }
}
