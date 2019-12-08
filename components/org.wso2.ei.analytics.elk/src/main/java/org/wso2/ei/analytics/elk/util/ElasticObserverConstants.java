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

package org.wso2.ei.analytics.elk.util;

import org.wso2.carbon.das.data.publisher.util.AnalyticsDataPublisherConstants;

/**
 * Stores needed constant values
 */
public class ElasticObserverConstants {

    private ElasticObserverConstants() {
    }

    // Configuration in carbon.xml, related const names in caps
    /*
     <MediationFlowStatisticConfig>
        <AnalyticPublishingDisable>true</AnalyticPublishingDisable>
        <Observers>
            org.wso2.ei.analytics.elk.observer.ElasticMediationFlowObserver
        </Observers>
        <ElasticObserver>   const: OBSERVER_ELEMENT
            <Host>localhost</Host>  HOST_CONFIG
            <Port>9200</Port>   PORT_CONFIG
            <BufferSize>5000</BufferSize>   BUFFER_SIZE_CONFIG
            <BulkSize>500</BulkSize>    BULK_SIZE_CONFIG
            <BulkCollectingTimeOut>5000</BulkCollectingTimeOut> BULK_COLLECTING_TIME_OUT_CONFIG
            <BufferEmptySleepTime>1000</BufferEmptySleepTime>   BUFFER_EMPTY_SLEEP_TIME_CONFIG, in milliseconds
            <NoNodesSleepTime>5000</NoNodesSleepTime>   NO_NODES_SLEEP_TIME_CONFIG, in milliseconds
            <Username>transport_client_user</Username>  USERNAME_CONFIG
            <Password svns:secretAlias="Elastic.User.Password">password</Password>   PASSWORD_CONFIG, PASSWORD_ALIAS
            <SSLEnabled>true</SSLEnabled>   SSL_ENABLED_CONFIG
            <TrustStorePath>/path/to/truststore.jks</TrustStorePath>    TRUST_STORE_PATH_CONFIG
            <TrustStoreType>jks</TrustStoreType>    TRUST_STORE_TYPE_CONFIG
            <TrustStorePassword>wso2carbon</TrustStorePassword>  TYPE_STORE_PASSWORD_CONFIG

        </ElasticObserver>
     </MediationFlowStatisticConfig>
     */

    // Constants to get data from carbon.xml
    private static final String OBSERVER_ELEMENT = AnalyticsDataPublisherConstants.STAT_CONFIG_ELEMENT + ".ElasticObserver";
    public static final String HOST_CONFIG = OBSERVER_ELEMENT + ".Host";
    public static final String PORT_CONFIG = OBSERVER_ELEMENT + ".Port";
    public static final String BUFFER_SIZE_CONFIG = OBSERVER_ELEMENT + ".BufferSize";
    public static final String BULK_SIZE_CONFIG = OBSERVER_ELEMENT + ".BulkSize";
    public static final String BULK_COLLECTING_TIME_OUT_CONFIG = OBSERVER_ELEMENT + ".BulkCollectingTimeOut";
    public static final String BUFFER_EMPTY_SLEEP_TIME_CONFIG = OBSERVER_ELEMENT + ".BufferEmptySleepTime";
    public static final String NO_NODES_SLEEP_TIME_CONFIG = OBSERVER_ELEMENT + ".NoNodesSleepTime";
    public static final String USERNAME_CONFIG = OBSERVER_ELEMENT + ".Username";
    public static final String PASSWORD_CONFIG = OBSERVER_ELEMENT + ".Password";
    // path to Truststore that contains the certificate for this client
    public static final String TRUST_STORE_PATH_CONFIG = OBSERVER_ELEMENT + ".TrustStorePath";
    // type of Truststore
    public static final String TRUST_STORE_TYPE_CONFIG = OBSERVER_ELEMENT + ".TrustStoreType";
    // password of Truststore
    public static final String TRUST_STORE_PASSWORD_CONFIG = OBSERVER_ELEMENT + ".TrustStorePassword";
    public static final String SSL_ENABLED_CONFIG = OBSERVER_ELEMENT + ".SSLEnabled";

    // type of Protocol
    public static final String HTTP_PROTOCOL = "http";
    public static final String HTTPS_PROTOCOL = "https";

    // Password alias to check in Secure Vault
    public static final String PASSWORD_ALIAS = "Elastic.User.Password";
    public static final String TRUST_STORE_PASSWORD_ALIAS = "Elastic.TrustStore.Password";

    // Default buffering queue size, overrides with the size config in carbon.xml
    public static final int DEFAULT_BUFFER_SIZE = 5000;

    // Size of the event bulk published at one request
    public static final int DEFAULT_PUBLISHING_BULK_SIZE = 500;

    // Time out for collecting the fixed size bulk (in milliseconds)
    public static final long DEFAULT_BULK_COLLECTING_TIMEOUT = 5000;

    // Default sleep time of the PublisherThread when the event buffer is empty (in milliseconds)
    public static final long DEFAULT_BUFFER_EMPTY_SLEEP_TIME = 1000;

    // Default sleep time of the PublisherThread when the Elasticsearch server is down (in milliseconds)
    public static final long DEFAULT_NO_NODES_SLEEP_TIME = 5000;

    public static final String DEFAULT_HOSTNAME = "localhost";
    public static final String DEFAULT_USERNAME = "elastic";
    public static final String DEFAULT_PASSWORD = "changeme";
    public static final int DEFAULT_PORT = 9200;
    public static final boolean DEFAULT_SSL_ENABLED = false;
    public static final String DEFAULT_TRUSTSTORE_PASSWORD = "wso2carbon";
    public static final String DEFAULT_TRUSTSTORE_TYPE = "jks";

    // Monitoring service types
    public static final String SEQUENCE = "Sequence";
    public static final String ENDPOINT = "Endpoint";
    public static final String API = "API";
    public static final String PROXY_SERVICE = "Proxy Service";
    public static final String INBOUND_ENDPOINT = "Inbound EndPoint";

    // Keys for the configurations object in ElasticMediationFlowObserver getConfigurations()
    public static final String HOST = "host";
    public static final String PORT = "port";
    public static final String BUFFER_SIZE = "bufferSize";
    public static final String BULK_SIZE = "bulkSize";
    public static final String BULK_TIME_OUT = "bulkTimeOut";
    public static final String BUFFER_EMPTY_SLEEP = "bufferEmptySleep";
    public static final String NO_NODES_SLEEP = "noNodesSleep";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String TRUST_STORE_PASSWORD = "trustStorePass";
    public static final String TRUST_STORE_TYPE = "trustStoreType";
    public static final String TRUST_STORE_PATH = "trustStorePath";
    public static final String SSL_ENABLED = "sslEnabled";
}
