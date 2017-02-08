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
package org.wso2.esb.integration.common.extensions.carbonserver;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.engine.FrameworkConstants;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.extensions.ExtensionConstants;
import org.wso2.carbon.integration.common.utils.FileManager;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class TestServerManager {
    protected CarbonServerManager carbonServer;
    protected String carbonZip;
    protected int portOffset;
    protected Map<String, String> commandMap = new HashMap<>();
    private static final Log log = LogFactory.getLog(TestServerManager.class);
    protected String carbonHome;
    protected String runtimePath;

    public TestServerManager(AutomationContext context) {
        carbonServer = new CarbonServerManager(context);
    }

    public TestServerManager(AutomationContext context, String carbonZip) {
        carbonServer = new CarbonServerManager(context);
        this.carbonZip = carbonZip;
    }

    public TestServerManager(AutomationContext context, int portOffset) {
        carbonServer = new CarbonServerManager(context);
        this.portOffset = portOffset;
        commandMap.put(ExtensionConstants.SERVER_STARTUP_PORT_OFFSET_COMMAND, String.valueOf(portOffset));
    }

    public TestServerManager(AutomationContext context, String carbonZip,
                             Map<String, String> commandMap) {
        carbonServer = new CarbonServerManager(context);
        this.carbonZip = carbonZip;
        if (commandMap.get(ExtensionConstants.SERVER_STARTUP_PORT_OFFSET_COMMAND) != null) {
            this.portOffset = Integer.parseInt(commandMap.get(ExtensionConstants.SERVER_STARTUP_PORT_OFFSET_COMMAND));
        } else {
            throw new IllegalArgumentException("portOffset value must be set in command list");
        }
        this.commandMap = commandMap;
    }

    public String getCarbonZip() {
        return carbonZip;
    }

    public String getCarbonHome() {
        return carbonHome;
    }

    public int getPortOffset() {
        return portOffset;
    }

    public String getRuntimePath() { return runtimePath; }

    public void configureServer() throws AutomationFrameworkException {
        log.info("Updating catalina-server.xml for product EI");
        String catalinaResourcePath = Paths.get(FrameworkPathUtil.getSystemResourceLocation(), "tomcat",
                                                "catalina-server.xml").toString();
        String eiConfDir = Paths.get(carbonHome, "conf").toString();
        try {
            FileManager.copyFile(Paths.get(catalinaResourcePath).toFile(), Paths.get(eiConfDir, "tomcat",
                                                                                     "catalina-server.xml").toString());
        } catch (IOException e) {
            throw new AutomationFrameworkException(e.getMessage(), e);
        }
    }


    public Map<String, String> getCommands() {
        return commandMap;
    }

    /**
     * This method is called for starting a Carbon server in preparation for execution of a
     * TestSuite
     * <p/>
     * Add the @BeforeSuite TestNG annotation in the method overriding this method
     *
     * @return The CARBON_HOME
     * @throws IOException If an error occurs while copying the deployment artifacts into the
     *                             Carbon server
     */
    public String startServer()
            throws AutomationFrameworkException, IOException, XPathExpressionException {
        if(carbonHome == null) {
            if (carbonZip == null) {
                carbonZip = System.getProperty(FrameworkConstants.SYSTEM_PROPERTY_CARBON_ZIP_LOCATION);
            }
            if (carbonZip == null) {
                throw new IllegalArgumentException("carbon zip file cannot find in the given location");
            }
            carbonHome = carbonServer.setUpCarbonHome(carbonZip);
            configureServer();
        }
        log.info("Carbon Home - " + carbonHome);
        if (commandMap.get(ExtensionConstants.SERVER_STARTUP_PORT_OFFSET_COMMAND) != null) {
            this.portOffset = Integer.parseInt(commandMap.get(ExtensionConstants.SERVER_STARTUP_PORT_OFFSET_COMMAND));
        } else {
            this.portOffset = 0;
        }

        if(commandMap.get("runtimePath") != null){
            this.runtimePath = commandMap.get("runtimePath");
        }
        carbonServer.startServerUsingCarbonHome(carbonHome, commandMap);
        return carbonHome;
    }

    /**
     * Restarting server already started by the method startServer
     * @throws AutomationFrameworkException
     */
    public void restartGracefully() throws AutomationFrameworkException {
        if(carbonHome == null) {
            throw new AutomationFrameworkException("No Running Server found to restart. " +
                                                   "Please make sure whether server is started");
        }
        carbonServer.restartGracefully();
    }

    /**
     * This method is called for stopping a Carbon server
     * <p/>
     * Add the @AfterSuite annotation in the method overriding this method
     *
     * @throws AutomationFrameworkException If an error occurs while shutting down the server
     */
    public void stopServer() throws AutomationFrameworkException {
        carbonServer.serverShutdown(portOffset);
    }




}
