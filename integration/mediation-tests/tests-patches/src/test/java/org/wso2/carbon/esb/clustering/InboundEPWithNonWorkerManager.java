/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.clustering;

import junit.framework.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
//import org.wso2.carbon.logging.view.stub.LogViewerLogViewerException;
import org.wso2.carbon.logging.view.data.xsd.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.common.ServerConfigurationManager;

import java.io.File;
import java.rmi.RemoteException;

/*
This testcase is to check whether the node is treated as a manager or not when the clustering pattern is set to
nonWorkerManager.
https://github.com/wso2/product-ei/issues/532
 */

public class InboundEPWithNonWorkerManager extends ESBIntegrationTest {

    private ServerConfigurationManager serverConfigurationManager;
    private LogViewerClient logViewerClient = null;
    private final String FULL_RESOURCE_PATH = getESBResourceLocation() + File.separator + "clustering" + File.separator;
    private final String RELATIVE_RESOURCE_PATH = "/artifacts/ESB/" + "clustering" + File.separator;
    private final String INBOUND_EP_MESSAGE_FOR_MANAGER = "InboundRunner Inbound EP will not run in manager node. " +
            "Same will run on worker(s)";
    private final String HAWTBUF = "hawtbuf-1.9.jar";
    private final String ACTIVEMQ_CLIENT = "activemq-client-5.9.1.jar";
    private final String ACTIVEMQ_BROKER = "activemq-broker-5.9.1.jar";
    private final String GERONIMO_J2EE_MANAGEMENT = "geronimo-j2ee-management_1.1_spec-1.0.1.jar";
    private final String GERONIMO_JMS = "geronimo-jms_1.1_spec-1.1.1.jar";
    private final String JAR_LOCATION = "/artifacts/ESB/jar";

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        serverConfigurationManager =
                new ServerConfigurationManager(new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN));
        serverConfigurationManager.applyConfigurationWithoutRestart(new File(FULL_RESOURCE_PATH + "axis2.xml"));
        serverConfigurationManager.copyToComponentLib
                (new File(getClass().getResource(JAR_LOCATION + File.separator + HAWTBUF).toURI()));
        serverConfigurationManager.copyToComponentLib
                (new File(getClass().getResource(JAR_LOCATION + File.separator + ACTIVEMQ_BROKER).toURI()));
        serverConfigurationManager.copyToComponentLib
                (new File(getClass().getResource(JAR_LOCATION + File.separator + ACTIVEMQ_CLIENT).toURI()));
        serverConfigurationManager.copyToComponentLib
                (new File(getClass().getResource(JAR_LOCATION + File.separator + GERONIMO_J2EE_MANAGEMENT).toURI()));
        serverConfigurationManager.copyToComponentLib
                (new File(getClass().getResource(JAR_LOCATION + File.separator + GERONIMO_JMS).toURI()));
        serverConfigurationManager.restartGracefully();
        super.init();
        addInboundEndpoint(esbUtils.loadResource(RELATIVE_RESOURCE_PATH + "JMSEndpoint.xml"));
        logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
    }

    @Test(groups = "wso2.esb", description = "Test Inbound EP in clustered environment " +
            "when the clustering pattern is nonWorkerManager")
    public void testInboundEPWithNonWorkerManager() throws Exception {
        Assert.assertFalse("Check whether the node is considered as manager or not when the clustering pattern" +
                " is nonWorkerManager", this.stringExistsInLog(INBOUND_EP_MESSAGE_FOR_MANAGER));
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
        serverConfigurationManager.restoreToLastConfiguration();
        serverConfigurationManager = null;
    }

    protected boolean stringExistsInLog(String value) throws RemoteException {
        LogEvent[] logs = logViewerClient.getAllRemoteSystemLogs();
        boolean logFound = false;
        for (LogEvent logEvent : logs) {
            String msg = logEvent.getMessage();
            if (msg.contains(value)) {
                logFound = true;
                break;
            }
        }
        return logFound;
    }
}