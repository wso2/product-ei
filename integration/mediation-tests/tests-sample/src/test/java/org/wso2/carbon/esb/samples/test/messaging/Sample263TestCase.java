/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/
package org.wso2.carbon.esb.samples.test.messaging;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.common.TestConfigurationProvider;
import org.wso2.esb.integration.common.utils.servers.ActiveMQServer;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.io.File;
import java.util.Hashtable;

// this class is disabled
public class Sample263TestCase extends ESBIntegrationTest {

//    private ServerConfigurationManager serverManager = null;
//
//    private final String JBOSS_CLIENT = "jboss-client.jar";
//
//    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
//    @BeforeClass(alwaysRun = false)
//    public void startJMSBrokerAndConfigureESB() throws Exception {
//        super.init();
//        serverManager = new ServerConfigurationManager(context);
//
//
//        //copping dependency jms jar files to component/lib
//        serverManager.copyToComponentLib(new File(TestConfigurationProvider.getResourceLocation()
//                + File.separator + "artifacts" + File.separator + "ESB" + File.separator + "jar"
//                + File.separator + JBOSS_CLIENT));
//
//        serverManager.applyConfigurationWithoutRestart(new File(TestConfigurationProvider.getResourceLocation()
//                + File.separator + "artifacts" + File.separator + "ESB"
//                + File.separator + "jms" + File.separator + "transport"
//                + File.separator + "axis2config" + File.separator
//                + "hornetq" + File.separator + "axis2.xml"));
//
//        // restart the server with the changes done to carbon.xml & axis2.xml
//        serverManager.applyConfiguration(new File(TestConfigurationProvider.getResourceLocation()
//                + File.separator + "artifacts" + File.separator + "ESB"
//                + File.separator + "jms" + File.separator + "transport"
//                + File.separator + "axis2config" + File.separator
//                + "hornetq" + File.separator + "carbon.xml"));
//
//        super.init();
//       // activeMQServer.startJMSBrokerAndConfigureESB();
//        loadSampleESBConfiguration(263);
//
//    }
//
//    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
//    @AfterClass(alwaysRun = false)
//    public void stopJMSBrokerRevertESBConfiguration() throws Exception {
//
//        //reverting the changes done to esb sever
//        Thread.sleep(10000); //let server to clear the artifact undeployment
//        super.cleanup();
//
//        if (serverManager != null) {
//            serverManager.removeFromComponentLib(JBOSS_CLIENT);
//            serverManager.restoreToLastConfiguration();
//        }
//    }
//
//    @Test(groups = {"wso2.esb"}, description = "Test JMS to Proxy with JBoss messaging ", enabled = false)
//    public void testJMSProxyWithHornetq() throws Exception {
//
//        Queue testQueue;
//        Connection connection = null;
//
//        Hashtable<String, String> env = new Hashtable<String, String>();
//
//        env.put(Context.PROVIDER_URL, "remote://localhost:14447");
//        env.put(Context.INITIAL_CONTEXT_FACTORY,
//                "org.jboss.naming.remote.client.InitialContextFactory");
//        env.put(Context.URL_PKG_PREFIXES, "org.jboss.naming:org.jnp.interfaces");
//
//        Context ctx = new InitialContext(env);
//
//        ConnectionFactory cf = (ConnectionFactory) ctx.lookup("jms/RemoteConnectionFactory");
//        testQueue = (Queue) ctx.lookup("java:/StockQuoteProxy");
//
//        connection = cf.createConnection();
//
//        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
//        MessageProducer producer = session.createProducer(testQueue);
//
//        connection.start();
//
//        TextMessage txtMsg = session.createTextMessage();
//        txtMsg.setJMSDeliveryMode(Message.DEFAULT_DELIVERY_MODE);
//
//        txtMsg.setText("<m:placeOrder xmlns:m=\"http://services.samples\">\n" +
//                "    <m:order>\n" +
//                "        <m:price>10.50</m:price>\n" +
//                "        <m:quantity>100</m:quantity>\n" +
//                "        <m:symbol>IBM</m:symbol>\n" +
//                "    </m:order>\n" +
//                "</m:placeOrder>");
//
//
//        producer.send(txtMsg);
//
//    }
}
