/*
* Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
* WSO2 Inc. licenses this file to you under the Apache License,
* Version 2.0 (the "License"); you may not use this file except
* in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.wso2.ei.analytics.elk.observer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.elasticsearch.ElasticsearchSecurityException;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.xpack.client.PreBuiltXPackTransportClient;

import org.apache.synapse.aspects.flow.statistics.publishing.PublishingFlow;
import org.wso2.carbon.base.ServerConfiguration;
import org.wso2.carbon.das.messageflow.data.publisher.observer.MessageFlowObserver;
import org.wso2.securevault.SecretResolver;
import org.wso2.securevault.SecretResolverFactory;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.wso2.ei.analytics.elk.publisher.ElasticStatisticsPublisher;
import org.wso2.ei.analytics.elk.services.ElasticsearchPublisherThread;
import org.wso2.ei.analytics.elk.util.ElasticObserverConstants;

/**
 * This class is instantiated by MediationStatisticsComponent.
 * Gets stored in MessageFlowObserverStore and updateStatistics() is notified by the MessageFlowReporterThread.
 */
public class ElasticMediationFlowObserver implements MessageFlowObserver {
    private static final Log log = LogFactory.getLog(ElasticMediationFlowObserver.class);

    // Defines elasticsearch Transport Client as client
    private TransportClient client = null;

    // Thread to publish json strings to Elasticsearch
    private ElasticsearchPublisherThread publisherThread = null;

    // Whether the event queue exceeded or not, accessed by MessageFlowReporter threads
    private volatile boolean bufferExceeded = false;

    // ServerConfiguration
    private ServerConfiguration serverConf = ServerConfiguration.getInstance();

    // Keep all needed configurations (final configurations)
    private Map<String, Object> configurations = new HashMap<>();

    /**
     * Instantiates the TransportClient as this class is instantiated
     */
    public ElasticMediationFlowObserver() {
        try {
            // Take config, resolve and validates , and put into configurations field
            getConfigurations();

            String clusterName = (String) configurations.get(ElasticObserverConstants.CLUSTER_NAME);
            String username = (String) configurations.get(ElasticObserverConstants.USERNAME);
            String password = (String) configurations.get(ElasticObserverConstants.PASSWORD);
            String sslKey = (String) configurations.get(ElasticObserverConstants.SSL_KEY);
            String sslCert = (String) configurations.get(ElasticObserverConstants.SSL_CERT);
            String sslCa = (String) configurations.get(ElasticObserverConstants.SSL_CA);
            String host = (String) configurations.get(ElasticObserverConstants.HOST);
            int port = (int) configurations.get(ElasticObserverConstants.PORT);

            // Elasticsearch settings builder object
            Settings.Builder settingsBuilder = Settings.builder()
                    .put("cluster.name", clusterName)
                    .put("transport.tcp.compress", true);

            if (username != null && password != null) {
                // Can use password without ssl
                settingsBuilder.put("xpack.security.user", username + ":" + password)
                        .put("request.headers.X-Found-Cluster", clusterName);

                if (sslKey != null && sslCert != null && sslCa != null) {
                    settingsBuilder.put("xpack.ssl.key", sslKey)
                            .put("xpack.ssl.certificate", sslCert)
                            .put("xpack.ssl.certificate_authorities", sslCa)
                            .put("xpack.security.transport.ssl.enabled", "true");

                    if (log.isDebugEnabled()) {
                        log.debug("SSL keys and certificates added.");
                    }
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("SSL is not configured.");
                    }
                }
            }

            client = new PreBuiltXPackTransportClient(settingsBuilder.build());

            if (log.isDebugEnabled()) {
                log.debug("Transport Client is built.");
            }

            client.addTransportAddress(new TransportAddress(InetAddress.getByName(host), port));

            if (log.isDebugEnabled()) {
                log.debug("Host & Port added to the client.");
            }

            // Wrong cluster name provided or given cluster is down or wrong access credentials
            if (client.connectedNodes().isEmpty()) {
                log.error("Can not connect to any Elasticsearch nodes. Please give correct configurations, " +
                        "run Elasticsearch and restart WSO2-EI.");
                client.close();

                if (log.isDebugEnabled()) {
                    log.debug("No nodes connected. Reasons: Wrong cluster name/ Given cluster is down/ " +
                            "Wrong access credentials");
                }
            } else {
                /*
                    Client needs access rights to read and write to Elasticsearch cluster as described in the article.
                    If the given user credential has no access to write, it only can be identified when the first bulk
                    of events are published.
                    So, to check the access privileges before hand, here put a test json string and delete it.
                */
                client.prepareIndex("eidata", "data", "1")
                        .setSource("{" +
                                "\"test_att\":\"test\"" +
                                "}", XContentType.JSON)
                        .get();

                client.prepareDelete("eidata", "data", "1").get();

                if (log.isDebugEnabled()) {
                    log.debug("Access privileges for given user is sufficient.");
                }

                startPublishing();
                log.info("Elasticsearch mediation statistic publishing enabled.");
            }
        } catch (UnknownHostException e) {
            log.error("Unknown Elasticsearch Host.", e);
            client.close();
        } catch (NumberFormatException e) {
            log.error("Invalid port number, queue size or time value.", e);
        } catch (ElasticsearchSecurityException e) { // lacks access privileges
            log.error("Elasticsearch user credentials lacks access privileges.", e);
            client.close();
        } catch (Exception e) {
            log.error("Elasticsearch connection error.", e);
            client.close();
        }
    }

    /**
     * TransportClient gets closed
     */
    @Override
    public void destroy() {
        publisherThread.shutdown();

        if (client != null) {
            client.close();
        }

        if (log.isDebugEnabled()) {
            log.debug("Shutting down the mediation statistics observer of Elasticsearch");
        }
    }

    /**
     * Method is called when this observer is notified.
     * Invokes the process method considering about the queue size.
     *
     * @param publishingFlow PublishingFlow object is passed when notified.
     */
    @Override
    public void updateStatistics(PublishingFlow publishingFlow) {
        if (publisherThread != null) {
            int bufferSize = (int) configurations.get(ElasticObserverConstants.BUFFER_SIZE);
            if (bufferExceeded) {
                // If the queue has exceeded before, check the queue is not exceeded now
                if (ElasticStatisticsPublisher.getAllMappingsQueue().size() < bufferSize) {
                    // Log only once
                    log.info("Event buffering started.");
                    bufferExceeded = false;
                }
            } else {
                // If the queue has not exceeded before, check the queue is exceeded now
                if (ElasticStatisticsPublisher.getAllMappingsQueue().size() >= bufferSize) {
                    // Log only once
                    log.warn("Maximum buffer size reached. Dropping incoming events.");
                    bufferExceeded = true;
                }
            }

            if (!bufferExceeded) {
                try {
                    if (!(publisherThread.getShutdown())) {
                        ElasticStatisticsPublisher.process(publishingFlow);
                    }
                } catch (Exception e) {
                    log.error("Failed to update statistics from Elasticsearch publisher", e);
                }
            }
        }
    }

    /**
     * Instantiates the publisher thread, passes the transport client and starts.
     */
    private void startPublishing() {
        publisherThread = new ElasticsearchPublisherThread();
        publisherThread.setName("ElasticsearchPublisherThread");
        publisherThread.init(
                client,
                (int) configurations.get(ElasticObserverConstants.BULK_SIZE),
                (long) configurations.get(ElasticObserverConstants.BULK_TIME_OUT),
                (long) configurations.get(ElasticObserverConstants.BUFFER_EMPTY_SLEEP),
                (long) configurations.get(ElasticObserverConstants.NO_NODES_SLEEP)
        );
        publisherThread.start();
    }

    /**
     * Takes needed configurations for the client to connect from the carbon.xml file
     * Validates the configurations, resolves password and returns final settings
     *
     * @see ElasticObserverConstants for the keys of the configurations object
     */
    private void getConfigurations() {
        // Event buffering queue size = 5000
        int bufferSize = ElasticObserverConstants.DEFAULT_BUFFER_SIZE;

        // Size of the event publishing bulk = 500
        int bulkSize = ElasticObserverConstants.DEFAULT_PUBLISHING_BULK_SIZE;

        // Time out for collecting configured fixed size bulk = 5000
        long bulkTimeOut = ElasticObserverConstants.DEFAULT_BULK_COLLECTING_TIMEOUT;

        // PublisherThread sleep time when the buffer is empty = 1000 (in millis)
        long bufferEmptySleep = ElasticObserverConstants.DEFAULT_BUFFER_EMPTY_SLEEP_TIME;

        // PublisherThread sleep time when the Elasticsearch server is down = 5000 (in millis)
        long noNodesSleep = ElasticObserverConstants.DEFAULT_NO_NODES_SLEEP_TIME;

        // Takes configuration details form carbon.xml
        String clusterName = serverConf.getFirstProperty(ElasticObserverConstants.CLUSTER_NAME_CONFIG);
        String host = serverConf.getFirstProperty(ElasticObserverConstants.HOST_CONFIG);
        String portString = serverConf.getFirstProperty(ElasticObserverConstants.PORT_CONFIG);
        String bufferSizeString = serverConf.getFirstProperty(ElasticObserverConstants.BUFFER_SIZE_CONFIG);
        String bulkSizeString = serverConf.getFirstProperty(ElasticObserverConstants.BULK_SIZE_CONFIG);
        String bulkCollectingTimeOutString = serverConf.getFirstProperty(
                ElasticObserverConstants.BULK_COLLECTING_TIME_OUT_CONFIG);
        String bufferEmptySleepString = serverConf.getFirstProperty(
                ElasticObserverConstants.BUFFER_EMPTY_SLEEP_TIME_CONFIG);
        String noNodesSleepString = serverConf.getFirstProperty(ElasticObserverConstants.NO_NODES_SLEEP_TIME_CONFIG);
        String username = serverConf.getFirstProperty(ElasticObserverConstants.USERNAME_CONFIG);
        String passwordInConfig = serverConf.getFirstProperty(ElasticObserverConstants.PASSWORD_CONFIG);
        String sslKey = serverConf.getFirstProperty(ElasticObserverConstants.SSL_KEY_CONFIG);
        String sslCert = serverConf.getFirstProperty(ElasticObserverConstants.SSL_CERT_CONFIG);
        String sslCa = serverConf.getFirstProperty(ElasticObserverConstants.SSL_CA_CONFIG);

        if (log.isDebugEnabled()) {
            log.debug("Configurations taken from carbon.xml.");
        }

        int port = Integer.parseInt(portString);

        // If the value is not in config, keep the default value defined in constants
        if (bufferSizeString != null) {
            bufferSize = Integer.parseInt(bufferSizeString);
        }

        if (bulkSizeString != null) {
            bulkSize = Integer.parseInt(bulkSizeString);
        }

        if (bulkCollectingTimeOutString != null) {
            bulkTimeOut = Integer.parseInt(bulkCollectingTimeOutString);
        }

        if (bufferEmptySleepString != null) {
            bufferEmptySleep = Integer.parseInt(bufferEmptySleepString);
        }

        if (noNodesSleepString != null) {
            noNodesSleep = Integer.parseInt(noNodesSleepString);
        }

        if (log.isDebugEnabled()) {
            log.debug("Cluster Name: " + clusterName);
            log.debug("Host: " + host);
            log.debug("Port: " + port);
            log.debug("Buffer Size: " + bufferSize + " events");
            log.debug("Bullk Size: " + bulkSize + " events");
            log.debug("Bulk Timeout: " + bulkTimeOut + " millis");
            log.debug("Buffer Empty Sleep Time: " + bufferEmptySleep + " millis");
            log.debug("No Nodes Sleep Time: " + noNodesSleep + " millis");
            log.debug("Username: " + username);
            log.debug("SSL Key Path: " + sslKey);
            log.debug("SSL Certificate Path: " + sslCert);
            log.debug("SSL CA Cert Path: " + sslCa);
        }

        // Put resolved configurations into configuration field
        configurations.put(ElasticObserverConstants.CLUSTER_NAME, clusterName);
        configurations.put(ElasticObserverConstants.HOST, host);
        configurations.put(ElasticObserverConstants.PORT, port);
        configurations.put(ElasticObserverConstants.BUFFER_SIZE, bufferSize);
        configurations.put(ElasticObserverConstants.BULK_SIZE, bulkSize);
        configurations.put(ElasticObserverConstants.BULK_TIME_OUT, bulkTimeOut);
        configurations.put(ElasticObserverConstants.BUFFER_EMPTY_SLEEP, bufferEmptySleep);
        configurations.put(ElasticObserverConstants.NO_NODES_SLEEP, noNodesSleep);
        configurations.put(ElasticObserverConstants.USERNAME, username);
        configurations.put(ElasticObserverConstants.SSL_KEY, sslKey);
        configurations.put(ElasticObserverConstants.SSL_CERT, sslCert);
        configurations.put(ElasticObserverConstants.SSL_CA, sslCa);

        // If username is not null; password can be in plain or Secure Vault
        // <Password> in config must be present
        if (username != null && passwordInConfig != null) {
            // Resolve password and add to configurations
            configurations.put(ElasticObserverConstants.PASSWORD, resolvePassword(passwordInConfig));
        }
    }

    /**
     * Checks whether the password is configured from Secure Vault or not
     * Resolves the password or directly assigns the passwordInConfig
     *
     * @param passwordInConfig value of the <Password> element found in carbon.xml
     * @return final plain text password
     */
    private String resolvePassword(String passwordInConfig) {
        // Plain password resolved/directly from carbon.xml
        String password;

        boolean secureVaultPassword = false;

        // Checking the <Password> element for the attribute "svns:secretAlias" to identify whether the
        // password is to be taken from Secure Vault or as plain text

        // nodeList contains all the nodes with the tag <Password>
        NodeList nodeList = serverConf.getDocumentElement().getElementsByTagName("Password");

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            // Find the node which has <ElasticObserver> as parent node
            if ("ElasticObserver".equals(node.getParentNode().getLocalName())) {
                // Take attributes map of the node and look into it for the defined secret alias
                NamedNodeMap attributeMap = node.getAttributes();
                for (int j = 0; j < attributeMap.getLength(); j++) {
                    if ("svns:secretAlias".equals(attributeMap.item(j).getNodeName())
                            && ElasticObserverConstants.PASSWORD_ALIAS.equals(attributeMap.item(j).getNodeValue())) {
                        secureVaultPassword = true;
                        break;
                    }
                }
            }
        }

        if (secureVaultPassword) {
            // Creates Secret Resolver from carbon.xml document element
            SecretResolver secretResolver = SecretResolverFactory.create(serverConf.getDocumentElement(),
                    true);

            // Resolves password using the defined alias
            password = secretResolver.resolve(ElasticObserverConstants.PASSWORD_ALIAS);

            // If the alias is wrong and there is no password, resolver returns the alias string again
            if (ElasticObserverConstants.PASSWORD_ALIAS.equals(password)) {
                log.error("Wrong password alias in Secure Vault. Use alias: " +
                        ElasticObserverConstants.PASSWORD_ALIAS);
                password = null;
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Password resolved from Secure Vault.");
                }
            }
        } else {
            // If not secure vault password take directly
            password = passwordInConfig;
            if (log.isDebugEnabled()) {
                log.debug("Password taken directly from carbon.xml");
            }
        }

        return password;
    }
}
