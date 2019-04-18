/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.ei.scenario.test;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Tests the functionality of scheduled task configured using a cron job.
 */
public class InjectMessageToSequenceUsingScheduleTaskWithCronTest extends Scenario14TestBase {

    @Override
    @BeforeClass()
    public void init() throws Exception {
        super.init(System.getProperty("invocation.uuid") + "-cron");
    }

    /**
     * Tests a schedules task which is configured to run with an interval of 3 seconds using a cron job.
     *
     * @throws Exception if any error occurs during the execution of the test
     */
    @Test(description = "14.1.2.1")
    public void testInjectMessageToSequenceUsingScheduledTaskWithCron() throws Exception {
        super.executeTest();
    }

    @AfterClass(description = "Server Cleanup", alwaysRun = true)
    public void cleanup() throws Exception {
        super.cleanup();
    }
}
