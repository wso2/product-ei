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

package org.wso2.carbon.esb.scheduledtask.test;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.llom.util.AXIOMUtil;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.Utils;

import static org.testng.Assert.assertTrue;

/**
 * Test case for https://wso2.org/jira/browse/ESBJAVA-5234
 * Test whether the scheduled task is created successfully when large interval value(> Integer.MAX_VALUE) is defined.
 */
public class TaskWithLargeIntervalValueTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
    }

    @Test(groups = {"wso2.esb"}, description = "Test successful deployment of scheduled task with large interval value")
    public void testDeployWithLargeIntervalValue() throws Exception {

        OMElement task = AXIOMUtil.stringToOM("<task:task xmlns:task=\"http://www.wso2.org/products/wso2commons/tasks\"\n" +
                "           name=\"ESBJAVA5234TestTask\"\n" +
                "           class=\"org.apache.synapse.startup.tasks.MessageInjector\" group=\"synapse.simple.quartz\">\n" +
                "    <task:trigger count=\"1\" interval=\"25920000\"/>\n" +
                "    <task:property name=\"message\">\n" +
                "        <m0:placeOrder xmlns:m0=\"http://services.samples\">\n" +
                "            <m0:order>\n" +
                "                <m0:price>100</m0:price>\n" +
                "                <m0:quantity>200</m0:quantity>\n" +
                "                <m0:symbol>IBM</m0:symbol>\n" +
                "            </m0:order>\n" +
                "        </m0:placeOrder>\n" +
                "    </task:property>\n" +
                "</task:task>");

        addScheduledTask(task);

        LogViewerClient logViewerClient = new LogViewerClient(context.getContextUrls().getBackEndUrl(), getSessionCookie());
        boolean assertValue = Utils.checkForLog(logViewerClient,
                "ESBJAVA5234TestTask was added to the Synapse configuration successfully",
                5);
        assertTrue(assertValue, "Scheduled task with large interval value has not deployed.");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}
