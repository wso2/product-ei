/*
*  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/
package org.wso2.carbon.esb.rabbitmq.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.esb.integration.common.utils.common.TestConfigurationProvider;

import java.net.URI;
import java.net.URISyntaxException;
import javax.xml.xpath.XPathExpressionException;

public class RabbitMQServerInstance {
    private static final Log log = LogFactory.getLog(RabbitMQServerInstance.class);
    private static final String RABBITMQ_HOST_XPATH = "//rabbitmq/host";
    private static final String RABBITMQ_PORT_XPATH = "//rabbitmq/port";
    private static final String DOCKER_HOST = "DOCKER_HOST";

    public static String getHost() {
        //setting the default value
        String host = "192.168.99.100";
        try {
            host = TestConfigurationProvider.getAutomationContext().getConfigurationValue(RABBITMQ_HOST_XPATH);
        } catch (XPathExpressionException e) {
            log.warn("Error reading the rabbitmq host in automation.xml. Proceed with default value " + host);
        }
        if (DOCKER_HOST.equalsIgnoreCase(host)) {
            String dockerHost = System.getenv("DOCKER_HOST");
            if (!StringUtils.isEmpty(dockerHost)) {
                URI uri = null;
                try {
                    uri = new URI(dockerHost);
                    host = uri.getHost();
                } catch (URISyntaxException e) {
                    log.error("Error getting DOCKER_HOST", e);
                }
            }
        }
        return host;
    }

    public static int getPort() {
        //set default port
        int port = 5672;
        try {
            port = Integer.parseInt(
                    TestConfigurationProvider.getAutomationContext().getConfigurationValue(RABBITMQ_PORT_XPATH));
        } catch (XPathExpressionException e) {
            log.warn("Error reading the rabbitmq port in automation.xml. Proceed with default value " + port);
        }
        return port;
    }
}
