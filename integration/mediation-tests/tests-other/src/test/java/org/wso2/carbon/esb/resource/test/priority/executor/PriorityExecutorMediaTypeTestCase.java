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
package org.wso2.carbon.esb.resource.test.priority.executor;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.registry.resource.stub.beans.xsd.MetadataBean;
import org.wso2.esb.integration.common.clients.executor.PriorityMediationAdminClient;
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;


public class PriorityExecutorMediaTypeTestCase extends ESBIntegrationTest{
    private Log log = LogFactory.getLog(PriorityExecutorMediaTypeTestCase.class);

    private PriorityMediationAdminClient priorityMediationAdminClient;
    private ResourceAdminServiceClient resourceAdmin;
    private final String PRIORITY_EXECUTOR_NAME = "automationPriorityExecutor";
    private boolean isPriorityExecutorExist = false;

    @BeforeClass
    public void init() throws Exception {
        super.init();
        priorityMediationAdminClient = new PriorityMediationAdminClient(contextUrls.getBackEndUrl(), getSessionCookie());
        resourceAdmin = new ResourceAdminServiceClient(contextUrls.getBackEndUrl(),getSessionCookie());
    }
    //since Registry persistence is no longer available
    @Test(groups = {"wso2.esb"}, description = "Test Priority Executor media type - text/xml", enabled = false)
    public void priorityExecutorMediaTypeTest() throws Exception {

        OMElement priorityConfig = AXIOMUtil.stringToOM("<priority-executor xmlns=\"http://ws.apache.org/ns/synapse\" " +
                                                        "name=\"" + PRIORITY_EXECUTOR_NAME + "\">" +
                                                        "<queues>" +
                                                        "<queue size=\"10\" priority=\"1\" />" +
                                                        "<queue size=\"5\" priority=\"2\" />" +
                                                        "</queues>" +
                                                        "<threads max=\"100\" core=\"20\" " +
                                                        "keep-alive=\"5\" />" +
                                                        "</priority-executor>");
        priorityMediationAdminClient.addPriorityMediator(PRIORITY_EXECUTOR_NAME, priorityConfig);
        isPriorityExecutorExist = true;
        //addEndpoint is a a asynchronous call, it will take some time to write to a registry
        Thread.sleep(10000);
        MetadataBean metadata = resourceAdmin.getMetadata("/_system/config/repository/synapse/default/synapse-executors/" + PRIORITY_EXECUTOR_NAME);
        Assert.assertEquals(metadata.getMediaType(), "text/xml", "Media Type mismatched for PriorityExecutor");

    }

    @AfterClass
    public void destroy() throws Exception {
        if (isPriorityExecutorExist) {
            priorityMediationAdminClient.remove(PRIORITY_EXECUTOR_NAME);
        }
        super.cleanup();
    }
}
