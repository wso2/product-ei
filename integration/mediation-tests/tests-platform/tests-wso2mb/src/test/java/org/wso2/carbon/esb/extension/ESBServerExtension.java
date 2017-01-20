/*
*  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.esb.extension;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;
import org.wso2.carbon.automation.engine.extensions.ExecutionListenerExtension;
import org.wso2.carbon.automation.extensions.ExtensionConstants;
import org.wso2.carbon.automation.extensions.servers.carbonserver.TestServerManager;
import org.wso2.esb.integration.common.utils.common.FileManager;
import org.wso2.esb.integration.common.utils.common.TestConfigurationProvider;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;

public class ESBServerExtension extends ExecutionListenerExtension {
    private static final Log log = LogFactory.getLog(ESBServerExtension.class);
    private static TestServerManager testServerWithSecurityManager;

    @Override
    public void initiate() throws AutomationFrameworkException {

        AutomationContext context;
        try {
            context = new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN);
        } catch (XPathExpressionException e) {
            throw new AutomationFrameworkException("Error Initiating Server Information", e);
        }

        //if port offset is not set, setting it to 0
        if (getParameters().get(ExtensionConstants.SERVER_STARTUP_PORT_OFFSET_COMMAND) == null) {
            getParameters().put(ExtensionConstants.SERVER_STARTUP_PORT_OFFSET_COMMAND, "200");
        }

        testServerWithSecurityManager = new TestServerManager(context, null, getParameters()) {
            public void configureServer() throws AutomationFrameworkException {

                String jndiPtah = TestConfigurationProvider.getResourceLocation("ESB")
                                      + File.separator + "conf" + File.separator + "jndi.properties";
                String axis2Xml = TestConfigurationProvider.getResourceLocation("ESB")
                                  + File.separator + "conf" + File.separator + "axis2" + File.separator + "axis2.xml";
                String libPath = TestConfigurationProvider.getResourceLocation("ESB")
                                 + File.separator + "lib";
                String esbConfDir = testServerWithSecurityManager.getCarbonHome() + File.separator + "repository" + File.separator
                                    + "conf";
                String esbLibDir = testServerWithSecurityManager.getCarbonHome() + File.separator + "repository" + File.separator
                                    + "components" + File.separator + "lib";

                try {
                    log.info("Replacing jndi.properties");
                    FileManager.copyFile(new File(jndiPtah) , esbConfDir + File.separator + "jndi.properties");
                    FileManager.copyFile(new File(axis2Xml) , esbConfDir + File.separator +"axis2" + File.separator + "axis2.xml");
                    log.info("Copying jar files to lib folder");
                    FileManager.copyJarFile(new File(libPath + File.separator + "andes-client-3.0.1.jar"), esbLibDir + File.separator);
                    FileManager.copyJarFile(new File(libPath + File.separator + "geronimo-jms_1.1_spec-1.1.1.jar"), esbLibDir + File.separator);
                    FileManager.copyJarFile(new File(libPath + File.separator + "org.wso2.securevault-1.0.0-wso2v2.jar"), esbLibDir + File.separator);

                } catch (IOException e) {
                    throw new AutomationFrameworkException(e.getMessage(), e);
                }

            }
        };

    }

    @Override
    public void onExecutionStart()
            throws AutomationFrameworkException {

        try {
            String carbonHome = testServerWithSecurityManager.startServer();
            System.setProperty(ExtensionConstants.CARBON_HOME, carbonHome);
        } catch (IOException e) {
            throw new AutomationFrameworkException("Error while starting server " + e.getMessage(), e);
        } catch (XPathExpressionException e) {
            throw new AutomationFrameworkException("Error while starting server " + e.getMessage(), e);
        }
    }

    @Override
    public void onExecutionFinish() throws AutomationFrameworkException {

        testServerWithSecurityManager.stopServer();


    }

    public static TestServerManager getTestServer() {
        return testServerWithSecurityManager;
    }


}
