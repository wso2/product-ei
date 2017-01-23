/*
*  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.jms.utils;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.engine.FrameworkConstants;
import org.wso2.carbon.automation.extensions.servers.jmsserver.controller.config.JMSBrokerConfiguration;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is similar to org.wso2.carbon.automation.extensions.servers.jmsserver.controller.JMSBrokerController.
 * Since the above mentioned class doesn't allow creation of multiple JMS broker controllers, this class is
 * implemented temporary to serve that purpose.
 * Jira issue: https://wso2.org/jira/browse/TA-1009
 */
public class JMSBroker {
    private static final Log log = LogFactory.getLog(JMSBroker.class);
    private String serverName;
    private List<TransportConnector> transportConnectors;
    private BrokerService broker;
    private boolean isBrokerStarted = false;

    /**
     * Constructor to defined broker transport
     *
     * @param serverName    name for the server
     * @param configuration Transport configurations which should expose by the server
     */
    public JMSBroker(String serverName, JMSBrokerConfiguration configuration) {
        this.serverName = serverName;
        this.transportConnectors = new ArrayList<TransportConnector>();
        TransportConnector connector = new TransportConnector();
        connector.setName("tcp");
        try {
            connector.setUri(new URI(configuration.getProviderURL()));
        } catch (URISyntaxException e) {
            log.error("Invalid URI", e);
        }
        transportConnectors.add(connector);

    }

    /**
     * Constructor to defined broker transport
     *
     * @param serverName          name of the server
     * @param transportConnectors transport configurations which should expose by the server
     */
    public JMSBroker(String serverName, List<TransportConnector> transportConnectors) {
        this.serverName = serverName;
        this.transportConnectors = transportConnectors;
    }

    /**
     * Return the server name defined from constructor
     *
     * @return name of the broker service
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * starting ActiveMQ embedded broker
     *
     * @return true if the broker is registered successfully
     */
    public boolean start() {
        try {
            log.info("JMSServerController: Preparing to start JMS Broker: " + serverName);
            broker = new BrokerService();
            // configure the broker

            broker.setBrokerName(serverName);
            log.info(broker.getBrokerDataDirectory());
            broker.setDataDirectory(System.getProperty(FrameworkConstants.CARBON_HOME) +
                    File.separator + broker.getBrokerDataDirectory());
            broker.setTransportConnectors(transportConnectors);
            broker.setPersistent(true);

            broker.start();
            setBrokerStatus(true);
            log.info("JMSServerController: Broker is Successfully started. continuing tests");
            return true;
        } catch (Exception e) {
            log.error("JMSServerController: There was an error starting JMS broker: " + serverName, e);
            return false;
        }
    }

    /**
     * Stopping ActiveMQ embedded broker
     *
     * @return true if broker is successfully stopped
     */
    public boolean stop() {
        try {
            log.info(" ************* Stopping **************");
            if (broker.isStarted()) {
                broker.stop();
                for (TransportConnector transportConnector : transportConnectors) {
                    transportConnector.stop();
                }
                setBrokerStatus(false);
            }
            return true;
        } catch (Exception e) {
            log.error("Error while shutting down the broker", e);
            return false;
        }
    }

    /**
     * getting the broker status
     *
     * @return true if the broker is started
     */
    public boolean isBrokerStarted() {
        return isBrokerStarted;
    }

    /**
     * introduced to get rid of find bugs warning
     *
     * @param status
     */
    private void setBrokerStatus(boolean status) {
        isBrokerStarted = status;
    }
}