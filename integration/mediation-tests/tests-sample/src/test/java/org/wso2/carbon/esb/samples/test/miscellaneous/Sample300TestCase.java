/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/
package org.wso2.carbon.esb.samples.test.miscellaneous;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.clients.tasks.TaskAdminClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.util.List;

public class Sample300TestCase extends ESBIntegrationTest {

    private String taskName = "CheckPrice";
    private TaskAdminClient taskAdminClient = null;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadSampleESBConfiguration(300);

        taskAdminClient = new TaskAdminClient(contextUrls.getBackEndUrl(), getSessionCookie());
    }

    @Test(groups = { "wso2.esb" }, description = "Test ESB mediation with adding task" , enabled = false)
    public void testAddTask() throws Exception {

        Thread.sleep(30000);
        Assert.assertEquals( taskAdminClient.getScheduleTaskList().size(), 1, "task added successfully");

        List<String> taskList = taskAdminClient.getScheduleTaskList();

        for (String task : taskList) {
            Assert.assertTrue(task.equalsIgnoreCase(taskName),"Task name is invalid");
        }

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}
