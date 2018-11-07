/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.mb.platform.tests.clustering.mqtt;

import javax.xml.xpath.XPathExpressionException;

import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.mb.integration.common.clients.MQTTClientConnectionConfiguration;
import org.wso2.mb.integration.common.clients.MQTTConstants;
import org.wso2.mb.platform.common.utils.MBPlatformBaseTest;


/**
 * Holds functionality common to all MQTT cluster tests classes.
 */
public class MQTTPlatformBaseTest extends MBPlatformBaseTest{
    
    /**
     * Builds a {@link MQTTClientConnectionConfiguration} with information in a {@link AutomationContext}
     * @param automationContext the automation context.
     * @return a {@link MQTTClientConnectionConfiguration}
     * @throws XPathExpressionException an error
     */
    protected MQTTClientConnectionConfiguration buildConfiguration(AutomationContext automationContext)
                                                                                                       throws XPathExpressionException {

        MQTTClientConnectionConfiguration clientConnectionConfiguration = new MQTTClientConnectionConfiguration();

        String mbServer2Host = automationContext.getInstance().getHosts().get("default");
        String mbServer2Port = automationContext.getInstance().getPorts().get("mqtt");

        clientConnectionConfiguration.setBrokerHost(mbServer2Host);
        clientConnectionConfiguration.setBrokerPort(mbServer2Port);
        clientConnectionConfiguration.setBrokerPassword(automationContext.getSuperTenant().getTenantAdmin()
                                                                         .getPassword());
        clientConnectionConfiguration.setBrokerUserName(automationContext.getSuperTenant().getTenantAdmin()
                                                                         .getUserName());
        clientConnectionConfiguration.setBrokerProtocol(MQTTConstants.BROKER_PROTOCOL); // default
                                                                                        // is
                                                                                        // tcp
        clientConnectionConfiguration.setCleanSession(true);

        return clientConnectionConfiguration;
    }
}
