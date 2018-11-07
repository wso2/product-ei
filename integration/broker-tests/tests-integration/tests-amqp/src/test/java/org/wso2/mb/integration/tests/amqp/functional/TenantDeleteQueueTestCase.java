/*
*  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/

package org.wso2.mb.integration.tests.amqp.functional;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.utils.LoginLogoutClient;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientException;
import org.wso2.mb.integration.common.clients.operations.clients.AndesAdminClient;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientUtils;
import org.wso2.mb.integration.common.utils.backend.MBIntegrationBaseTest;

import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

/**
 * This tests if a tenant user can create queues and check if they can be deleted and recreate the same queue.
 * See <a href="https://wso2.org/jira/browse/MB-1080">https://wso2.org/jira/browse/MB-1080</a>
 */
public class TenantDeleteQueueTestCase extends MBIntegrationBaseTest {

    /**
     * Initializes test case
     *
     * @throws javax.xml.xpath.XPathExpressionException
     */
    @BeforeClass
    public void prepare() throws XPathExpressionException {
        super.init(TestUserMode.SUPER_TENANT_ADMIN);
    }

    /**
     * Following steps are done by the admin user
     * 1. Create a queue.
     * 2. Delete the queue.
     * 3. Recreate a queue with same name.
     * 4. Delete the queue.
     *
     * @throws IOException
     * @throws AndesClientConfigurationException
     * @throws JMSException
     * @throws NamingException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb")
    public void performCreateDeleteQueueAdminTestCase() throws Exception {

        LoginLogoutClient loginLogoutClientForAdmin = new LoginLogoutClient(automationContext);
        String sessionCookie = loginLogoutClientForAdmin.login();
        String backEndUrl = automationContext.getContextUrls().getBackEndUrl();

        AndesAdminClient andesAdminClient = new AndesAdminClient(backEndUrl, sessionCookie);

        automationContext.getContextTenant().getContextUser().getUserName();

        andesAdminClient.createQueue("deleteAdminQueue");

        andesAdminClient.deleteQueue("deleteAdminQueue");

        AndesClientUtils.sleepForInterval(5000);

        andesAdminClient.createQueue("deleteAdminQueue");

        andesAdminClient.deleteQueue("deleteAdminQueue");

        loginLogoutClientForAdmin.logout();
    }

    /**
     * Following steps are done by a tenant user
     * 1. Create a queue.
     * 2. Delete the queue.
     * 3. Recreate a queue with same name.
     * 4. Delete the queue.
     *
     * @throws IOException
     * @throws AndesClientConfigurationException
     * @throws JMSException
     * @throws NamingException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb")
    public void performCreateDeleteQueueTenantTestCase() throws Exception {
        AutomationContext userAutomationContext = new AutomationContext("MB", TestUserMode.TENANT_USER);

        LoginLogoutClient loginLogoutClientForAdmin = new LoginLogoutClient(userAutomationContext);
        String sessionCookie = loginLogoutClientForAdmin.login();
        String backEndUrl = userAutomationContext.getContextUrls().getBackEndUrl();

        AndesAdminClient andesAdminClient = new AndesAdminClient(backEndUrl, sessionCookie);

        userAutomationContext.getContextTenant().getContextUser().getUserName();

        andesAdminClient.createQueue("deleteTenantQueue");

        andesAdminClient.deleteQueue(userAutomationContext.getContextTenant().getDomain() + "/deleteTenantQueue");

        AndesClientUtils.sleepForInterval(5000);

        andesAdminClient.createQueue("deleteTenantQueue");

        andesAdminClient.deleteQueue(userAutomationContext.getContextTenant().getDomain() + "/deleteTenantQueue");

        loginLogoutClientForAdmin.logout();
    }
}
