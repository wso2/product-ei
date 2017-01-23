/*
 *  Copyright (c) 2012, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.wso2.carbon.esb.scheduledtask.test;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.llom.util.AXIOMUtil;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import static org.testng.Assert.assertTrue;

public class InjectToProxyTestCase extends ESBIntegrationTest {

    private LogViewerClient logViewer;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/scheduledTask/InjectToProxyTestConfig.xml");
        logViewer = new LogViewerClient(context.getContextUrls().getBackEndUrl(), getSessionCookie());
    }

    @Test(groups = {"wso2.esb"})
    public void injectToProxyTest() throws Exception {
        OMElement task = AXIOMUtil.stringToOM("<task:task xmlns:task=\"http://www.wso2.org/products/wso2commons/tasks\"\n" +
                                              "           name=\"SampleInjectToProxyTask\" class=\"org.apache.synapse.startup.tasks.MessageInjector\"\n" +
                                              "           group=\"synapse.simple.quartz\">\n" +
                                              "    <task:trigger count=\"1\" interval=\"1\"/>\n" +
                                              "    <task:property name=\"message\">\n" +
                                              "        <m0:placeOrder xmlns:m0=\"http://services.samples\">\n" +
                                              "            <m0:order>\n" +
                                              "                <m0:price>100</m0:price>\n" +
                                              "                <m0:quantity>200</m0:quantity>\n" +
                                              "                <m0:symbol>IBM</m0:symbol>\n" +
                                              "            </m0:order>\n" +
                                              "        </m0:placeOrder>\n" +
                                              "    </task:property>\n" +
                                              "    <task:property name=\"proxyName\" value=\"SampleProxy\"/>\n" +
                                              "    <task:property name=\"injectTo\" value=\"proxy\"/>\n" +
                                              "</task:task>");

        int beforeLogSize = logViewer.getAllSystemLogs().length;
        addScheduledTask(task);
        Thread.sleep(4000);
        LogEvent[] logs = logViewer.getAllSystemLogs();
        int afterLogSize = logs.length;

        boolean invokedLogFound = false;
        for (int i = 0; i < (afterLogSize - beforeLogSize); i++) {
            if (logs[i].getMessage().contains("PROXY INVOKED")) {
                invokedLogFound = true;
                break;
            }
        }
        assertTrue(invokedLogFound);
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}
