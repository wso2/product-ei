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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import org.wso2.esb.integration.common.clients.executor.PriorityMediationAdminClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

public class Sample652TestCase extends ESBIntegrationTest {

    private Log log = LogFactory.getLog(Sample652TestCase.class);

    private PriorityMediationAdminClient priorityMediationAdminClient;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {

        super.init();
        loadSampleESBConfiguration(652);
        priorityMediationAdminClient = new PriorityMediationAdminClient(contextUrls.getBackEndUrl(),
                getSessionCookie());
    }

    @Test(groups = {"wso2.esb"}, description = "Test Priority Executor", enabled = true)
    public void priorityExecutorMediaTypeTest() throws Exception {

        Thread.sleep(10000);

        Assert.assertEquals(priorityMediationAdminClient.getExecutorList().length, 1,
                "PriorityExecutor not added");
    }

    @AfterClass
    public void destroy() throws Exception {
        super.cleanup();
    }
}
