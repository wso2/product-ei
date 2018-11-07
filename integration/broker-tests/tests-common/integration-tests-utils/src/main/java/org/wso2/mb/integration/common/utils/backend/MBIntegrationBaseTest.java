/*
 * Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.mb.integration.common.utils.backend;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.context.beans.User;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;

import java.io.File;
import javax.xml.xpath.XPathExpressionException;

/**
 * Base class of all MB integration tests
 */
public class MBIntegrationBaseTest {

    protected Log log = LogFactory.getLog(MBIntegrationBaseTest.class);
    protected AutomationContext automationContext;
    protected String backendURL;
    protected ServerConfigurationManager serverManager = null;

    /**
     * Initialize the base test by initializing the automation context.
     *
     * @param userMode The testing user mode
     * @throws XPathExpressionException
     */
    protected void init(TestUserMode userMode) throws XPathExpressionException {
        // org.apache.xerces.dom.ParentNode.nodeListItem which is used in AutomationContext
        // does not guarantee thread safety.
        // Hence to allow tests to run in parallel, this initialization should be synchronized
        synchronized (this.getClass()) {
            automationContext = new AutomationContext("MB", userMode);
            backendURL = automationContext.getContextUrls().getBackEndUrl();
        }
    }

    /**
     * Restart the testing MB server in In-Memory H2 database mode by applying In-Memory database configurations
     * in andes-virtualhosts-H2-mem.xml file.
     *
     * @throws Exception
     */
    protected void restartServerWithH2MemMode() throws Exception {
        serverManager = new ServerConfigurationManager(automationContext);

        // Replace the broker.xml with the new configuration and restarts the server.
        serverManager.applyConfiguration(new File(FrameworkPathUtil.getSystemResourceLocation() + File.separator +
                        "artifacts" + File.separator + "mb" + File.separator + "config" + File.separator +
                        "broker.xml"),
                new File(ServerConfigurationManager.getCarbonHome() +
                        File.separator + "repository" + File.separator + "conf" + File.separator + "broker.xml"),
                true, true);
    }

    /**
     * Gracefully restart the current server which was deployed by the test suit. This can be used when a large
     * amount or large size of messages are tested to clean up the server before or after the test.
     *
     * @throws Exception
     */
    protected void restartServer()
            throws Exception {
        serverManager = new ServerConfigurationManager(automationContext);
        serverManager.restartGracefully();
    }

    /**
     * Returns wso2 https server port based on automation.xml configurations
     * @throws Exception
     */
    protected Integer getHttpsServerPort() throws XPathExpressionException {
        return Integer.parseInt(automationContext.getInstance().getPorts().get("https"));

    }

    /**
     * Returns AMQP port based on automation.xml configurations
     * @throws Exception
     */
    protected Integer getAMQPPort() throws XPathExpressionException {
        return Integer.parseInt(automationContext.getInstance().getPorts().get("amqp"));

    }

    /**
     * Returns broker hostname based on automation.xml configurations
     *
     * @return broker hostname
     * @throws XPathExpressionException if initialization error
     */
    protected String getBrokerHost() throws XPathExpressionException {
        return automationContext.getInstance().getHosts().get("default");

    }

    /**
     * Return super admin {@link User} object
     *
     * @return super admin user
     * @throws XPathExpressionException if error occurred while reading values
     */
    protected User getSuperTenantAdminUser() throws XPathExpressionException {
        return automationContext.getSuperTenant().getTenantAdmin();
    }

    /**
     * Returns AMQP port based on automation.xml configurations
     * @throws Exception
     */
    protected Integer getSecureAMQPPort() throws XPathExpressionException {
        return Integer.parseInt(automationContext.getInstance().getPorts().get("sslamqp"));

    }


    /**
     * Returns MQTT port based on automation.xml configurations
     * @throws Exception
     */
    protected Integer getMQTTPort() throws XPathExpressionException {
        return Integer.parseInt(automationContext.getInstance().getPorts().get("mqtt"));
    }

    /**
     * Returns JMX RMI Server port based on automation.xml configurations
     * @throws Exception
     */
    protected Integer getJMXServerPort() throws XPathExpressionException {
        return Integer.parseInt(automationContext.getInstance().getPorts().get("jmxserver"));
    }

    /**
     * Returns JMX RMI Registry port based on automation.xml configurations
     * @throws Exception
     */
    protected Integer getRMIRegistryPort() throws XPathExpressionException {
        return Integer.parseInt(automationContext.getInstance().getPorts().get("rmiregistry"));
    }


}
