/*
* Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.registry.task;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.llom.util.AXIOMUtil;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.data.xsd.LogEvent;
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import java.io.File;
import java.net.URL;
import java.nio.file.Paths;

/**
 * ESBJAVA-4565
 * NullPointerException when registry resource is accessed inside a scheduled task.
 */

public class ESBJAVA4565TestCase extends ESBIntegrationTest {

    private static final String REGISTRY_ARTIFACT = "/_system/governance/services/test/config/ftp.xml";
    private ResourceAdminServiceClient resourceAdminServiceStub;

    @BeforeClass(alwaysRun = true)
    protected void init() throws Exception {
        super.init();
        verifySequenceExistence("ESBJAVA4565TestSequence");
        resourceAdminServiceStub =
                new ResourceAdminServiceClient(contextUrls.getBackEndUrl(), getSessionCookie());

        String ftpXmlPath = Paths.get(getESBResourceLocation(), "registry", "ftp.xml").toString();
        resourceAdminServiceStub.addResource(REGISTRY_ARTIFACT, "application/xml", "FTP Test account details",
                                             new DataHandler(new FileDataSource(new File(ftpXmlPath))));

        OMElement task = AXIOMUtil.stringToOM("<task:task xmlns:task=\"http://www.wso2.org/products/wso2commons/tasks\"\n" +
                                              "           name=\"TestTask\"\n" +
                                              "           class=\"org.apache.synapse.startup.tasks.MessageInjector\" group=\"synapse.simple.quartz\">\n" +
                                              "    <task:trigger interval=\"10\"/>\n" +
                                              "    <task:property name=\"format\" value=\"get\"/>\n" +
                                              "    <task:property name=\"sequenceName\" value=\"ESBJAVA4565TestSequence\"/>\n" +
                                              "    <task:property name=\"injectTo\" value=\"sequence\"/>\n" +
                                              "    <task:property name=\"message\"><empty/></task:property>\n" +
                                              "</task:task>");
        this.addScheduledTask(task);
    }

    @Test(groups = "wso2.esb", description = "Analyze carbon logs to find NPE due to unresolved tenant domain.")
    public void checkErrorLog() throws Exception {
        LogViewerClient cli = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
        LogEvent[] logs = cli.getAllRemoteSystemLogs();
        Assert.assertNotNull(logs, "No logs found");
        Assert.assertTrue(logs.length > 0, "No logs found");
        boolean hasErrorLog = false;
        for (LogEvent logEvent : logs) {
            String msg = logEvent.getMessage();
            if (msg.contains("java.lang.NullPointerException: Tenant domain has not been set in CarbonContext")) {
                hasErrorLog = true;
                break;
            }
        }
        Assert.assertFalse(hasErrorLog, "Tenant domain not resolved when registry resource is accessed inside " +
                                        "a scheduled task");
    }

    @AfterClass(alwaysRun = true, enabled=false)
    public void UndeployService() throws Exception {
        resourceAdminServiceStub.deleteResource(REGISTRY_ARTIFACT);
        super.cleanup();
    }
}
