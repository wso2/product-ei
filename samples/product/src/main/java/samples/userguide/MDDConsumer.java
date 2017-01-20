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


public class MDDConsumer implements MessageListener {

    private static String getProperty(String name, String def) {
        String result = System.getProperty(name);
        if (result == null || result.length() == 0) {
            result = def;
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        String dest  = getProperty("jms_topic", "mdd.MSFT");
        MDDConsumer app = new MDDConsumer();
        app.run(dest);
    }
    public void run(String dest) throws Exception {
        InitialContext ic = getInitialContext();
        TopicConnectionFactory confac = (TopicConnectionFactory) ic.lookup("ConnectionFactory");
        TopicConnection connection = confac.createTopicConnection();
        TopicSession session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
        Topic topic = session.createTopic(dest);
        TopicSubscriber topicSubscriber = session.createSubscriber(topic);
        topicSubscriber.setMessageListener(this);
        System.out.println("MDD-Consumer listening for topic : "+topic.getTopicName());        
        connection.start();
    }
    public void onMessage(Message message){
        try {
            System.out.println(" Market data recived for symbol : "+ message.getJMSDestination().toString());
        } catch (JMSException e) {
            System.out.println("Error : "+e.getMessage());
        }
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
}
