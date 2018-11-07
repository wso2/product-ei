/*
*  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.wso2.mb.platform.common.utils;

import com.google.common.net.HostAndPort;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.andes.stub.AndesAdminServiceBrokerManagerAdminException;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.context.beans.Instance;
import org.wso2.carbon.integration.common.utils.LoginLogoutClient;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.mb.integration.common.clients.operations.clients.AndesAdminClient;
import org.wso2.mb.integration.common.clients.operations.clients.TopicAdminClient;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Base class of all MB integration tests
 */
public class MBPlatformBaseTest {

    protected Log log = LogFactory.getLog(MBPlatformBaseTest.class);
    protected Map<String, AutomationContext> contextMap;
    protected Map<String, AndesAdminClient> andesAdminClients;
    protected Map<String, TopicAdminClient> topicAdminClients;
    private Stack<String> stack = null;

    /**
     * Create automation context objects for every node in config
     *
     * @param userMode User mode for which the automation context should use
     * @throws XPathExpressionException
     */
    protected void initCluster(TestUserMode userMode) throws XPathExpressionException {
        contextMap = new HashMap<String, AutomationContext>();
        AutomationContext automationContext = new AutomationContext("MB_Cluster", userMode);
        log.info("Cluster instance loading");
        Map<String, Instance> instanceMap = automationContext.getProductGroup().getInstanceMap();

        if (instanceMap != null && instanceMap.size() > 0) {
            for (Map.Entry<String, Instance> entry : instanceMap.entrySet()) {
                String instanceKey = entry.getKey();
                contextMap.put(instanceKey, new AutomationContext("MB_Cluster", instanceKey, userMode));
                log.info(instanceKey);
            }
        }

        stack = new Stack<String>();

    }

    /**
     * Get automation context object with given node key
     *
     * @param key The key value for automation context map
     * @return Respective automation context
     */

    protected AutomationContext getAutomationContextWithKey(String key) {

        if (contextMap != null && contextMap.size() > 0) {
            for (Map.Entry<String, AutomationContext> entry : contextMap.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(key)) {
                    return entry.getValue();
                }
            }
        }

        return null;
    }

    /**
     * Get andes admin client for given node
     *
     * @param key The key for the map which the andes admin clients are stored
     * @return An {@link org.wso2.mb.integration.common.clients.operations.clients.AndesAdminClient}.
     */

    protected AndesAdminClient getAndesAdminClientWithKey(String key) {

        if (andesAdminClients != null && andesAdminClients.size() > 0) {
            for (Map.Entry<String, AndesAdminClient> entry : andesAdminClients.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(key)) {
                    return entry.getValue();
                }
            }
        }

        return null;
    }

    /**
     * Get topic admin client for given node.
     * Suppressing "UnusedDeclaration" warning as this method can be used later in writing test
     * cases.
     *
     * @param key The key for the map which the topic admin clients are stored
     * @return An {@link org.wso2.mb.integration.common.clients.operations.clients.TopicAdminClient}.
     */
    @SuppressWarnings("UnusedDeclaration")
    protected TopicAdminClient getTopicAdminClientWithKey(String key) {

        if (topicAdminClients != null && topicAdminClients.size() > 0) {
            for (Map.Entry<String, TopicAdminClient> entry : topicAdminClients.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(key)) {
                    return entry.getValue();
                }
            }
        }

        return null;
    }

    /**
     * Login and provide session cookie for node
     *
     * @param context The automation context to be used.
     * @return The session cookie of the login user
     * @throws IOException
     * @throws XPathExpressionException
     * @throws URISyntaxException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws LoginAuthenticationExceptionException
     */
    protected String login(AutomationContext context)
            throws IOException, XPathExpressionException, URISyntaxException, SAXException,
            XMLStreamException, LoginAuthenticationExceptionException, AutomationUtilException {
        LoginLogoutClient loginLogoutClient = new LoginLogoutClient(context);
        return loginLogoutClient.login();
    }

    /**
     * Make MB instances in random mode to support pick a random instance for test cases
     */
    protected void makeMBInstancesRandom() {

        Object[] keys = contextMap.keySet().toArray();

        List<Object> list = new ArrayList<Object>();

        Collections.addAll(list, keys);

        Collections.shuffle(list);

        for (int i = 0; i < list.size(); i++) {
            keys[i] = list.get(i);
            stack.push(list.get(i).toString());
        }
    }

    /**
     * Gets an MB instance randomly
     *
     * @return Random instance key of the MB node
     */
    protected String getRandomMBInstance() {

        if (stack.empty()) {
            makeMBInstancesRandom();
        }

        return stack.pop();
    }

    /**
     * Create and login andes admin client to nodes in cluster
     *
     * @throws XPathExpressionException
     * @throws URISyntaxException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws LoginAuthenticationExceptionException
     * @throws IOException
     */
    protected void initAndesAdminClients()
            throws XPathExpressionException, URISyntaxException, SAXException, XMLStreamException,
            LoginAuthenticationExceptionException, IOException, AutomationUtilException {
        andesAdminClients = new HashMap<String, AndesAdminClient>();

        if (contextMap != null && contextMap.size() > 0) {
            for (Map.Entry<String, AutomationContext> entry : contextMap.entrySet()) {
                AutomationContext tempContext = entry.getValue();
                andesAdminClients.put(entry.getKey(), new AndesAdminClient(tempContext.getContextUrls().getBackEndUrl(),
                        login(tempContext)));
            }
        }
    }

    /**
     * Create and login topic admin client to nodes in cluster
     * Suppressing "UnusedDeclaration" warning as this method can be used later in writing test
     * cases.
     *
     * @throws LoginAuthenticationExceptionException
     * @throws IOException
     * @throws XPathExpressionException
     * @throws URISyntaxException
     * @throws SAXException
     * @throws XMLStreamException
     */
    @SuppressWarnings("UnusedDeclaration")
    protected void initTopicAdminClients()
            throws LoginAuthenticationExceptionException, IOException, XPathExpressionException,
            URISyntaxException, SAXException, XMLStreamException, AutomationUtilException {

        topicAdminClients = new HashMap<String, TopicAdminClient>();

        if (contextMap != null && contextMap.size() > 0) {
            for (Map.Entry<String, AutomationContext> entry : contextMap.entrySet()) {
                AutomationContext tempContext = entry.getValue();
                topicAdminClients.put(entry.getKey(), new TopicAdminClient(tempContext.getContextUrls().getBackEndUrl(),
                            login(tempContext)));
            }
        }
    }

    /**
     * Check whether given queue is deleted from the cluster nodes
     * Suppressing "UnusedDeclaration" warning as this method can be used later in writing test
     * cases.
     *
     * @param queue The queue name
     * @return true if queue deleted successfully, false otherwise.
     * @throws AndesAdminServiceBrokerManagerAdminException
     * @throws RemoteException
     */
    @SuppressWarnings("UnusedDeclaration")
    protected boolean isQueueDeletedFromCluster(String queue)
            throws AndesAdminServiceBrokerManagerAdminException, RemoteException {
        AndesAdminClient andesAdminClient;
        boolean queueDeleted = true;
        if (andesAdminClients != null && andesAdminClients.size() > 0) {
            for (Map.Entry<String, AndesAdminClient> entry : andesAdminClients.entrySet()) {
                andesAdminClient = entry.getValue();

                if (andesAdminClient.getQueueByName(queue) != null) {
                    queueDeleted = false;
                }

            }
        }

        return queueDeleted;
    }

    /**
     * Check whether given queue is created in the cluster nodes
     * Suppressing "UnusedDeclaration" warning as this method can be used later in writing test
     * cases.
     *
     * @param queue The queue name
     * @return true if queue exists in cluster, false otherwise.
     * @throws AndesAdminServiceBrokerManagerAdminException
     * @throws RemoteException
     */
    @SuppressWarnings("UnusedDeclaration")
    protected boolean isQueueCreatedInCluster(String queue)
            throws AndesAdminServiceBrokerManagerAdminException, RemoteException {
        AndesAdminClient andesAdminClient;
        boolean queueExists = true;
        if (andesAdminClients != null && andesAdminClients.size() > 0) {
            for (Map.Entry<String, AndesAdminClient> entry : andesAdminClients.entrySet()) {
                andesAdminClient = entry.getValue();

                if (andesAdminClient.getQueueByName(queue) == null) {
                    queueExists = false;
                }

            }
        }

        return queueExists;
    }

    /**
     * Give a random AMQP broker URL.
     *
     * @return Broker URL in host:port format (E.g "127.0.0.1:5672")
     * @throws XPathExpressionException
     */
    protected HostAndPort getRandomAMQPBrokerAddress() throws XPathExpressionException {
        String randomInstanceKey = getRandomMBInstance();
        AutomationContext tempContext = getAutomationContextWithKey(randomInstanceKey);

        return HostAndPort.fromString(tempContext.getInstance().getHosts().get
                ("default") + ":" + tempContext.getInstance().getPorts().get("amqp"));
    }

    /**
     * Give a random AMQP broker URL.
     *
     * @return Broker URL in host:port format (E.g "127.0.0.1:5672")
     * @throws XPathExpressionException
     */
    protected HostAndPort getRandomMQTTBrokerAddress() throws XPathExpressionException {
        String randomInstanceKey = getRandomMBInstance();
        AutomationContext tempContext = getAutomationContextWithKey(randomInstanceKey);

        return HostAndPort.fromString(tempContext.getInstance().getHosts().get
                ("default") + ":" + tempContext.getInstance().getPorts().get("mqtt"));
    }

    

}
