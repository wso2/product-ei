/*
 * Copyright (c) 2015,WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.ei.businessprocess.integration.tests.humantasks;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.ei.businessprocess.integration.common.clients.humantasks.HumanTaskPackageManagementClient;
import org.wso2.ei.businessprocess.integration.common.utils.BPSMasterTest;
import org.wso2.ei.businessprocess.integration.common.utils.RequestSender;
import org.wso2.carbon.authenticator.stub.LogoutAuthenticationExceptionException;
import org.wso2.carbon.automation.engine.FrameworkConstants;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.humantask.stub.mgt.PackageManagementException;
import org.wso2.carbon.integration.common.admin.client.UserManagementClient;
import org.wso2.carbon.integration.common.utils.LoginLogoutClient;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;

import javax.mail.Message;
import javax.mail.MessagingException;

import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.user.mgt.stub.UserAdminUserAdminException;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;

/**
 * Human Task based email notification sending using GreenMail server
 */
public class HumanTaskNotificationsTestCase extends BPSMasterTest {

    private static final Log log = LogFactory.getLog(HumanTaskCreationTestCase.class);
    //Test Automation API Clients
    private HumanTaskPackageManagementClient humanTaskPackageManagementClient;
    private UserManagementClient userManagementClient;
    private ServerConfigurationManager serverConfigurationManager;
    private RequestSender requestSender;
    //Email notification related variables
    private static GreenMail mailServer;
    private static final String USER_PASSWORD = "testwso2123";
    private static final String USER_NAME = "wso2test1";
    private static final String EMAIL_USER_ADDRESS = "wso2test1@localhost";
    private static final String EMAIL_SUBJECT = "email subject to user";
    private static final String EMAIL_TEXT = "Hi wso2test1";
    private static final int SMTP_TEST_PORT = 3025;
    public static final String NEW_CONF_DIR = "wso2/business-process/conf";
    GreenMail greenMail;

    /**
     * Setting up Server after Applying new Configuration Files.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        init();  //init master class
        requestSender = new RequestSender();
        serverConfigurationManager = new ServerConfigurationManager(bpsServer);
        //Replacing config file content
        updateConfigFiles();
        // Need to re-initialize since we have restarted the server
        init();
        userManagementClient = new UserManagementClient(backEndUrl, sessionCookie);
        deployArtifact();
        addRoles();
        requestSender.waitForProcessDeployment(backEndUrl + HumanTaskTestConstants.REMINDER_SERVICE);
        humanTaskPackageManagementClient = new HumanTaskPackageManagementClient(backEndUrl, sessionCookie);
        serverConfigurationManager = new ServerConfigurationManager(bpsServer);
        log.info("Server setting up completed.");
        //initialize HT Client API for Clerk1 user
        AutomationContext clerk1AutomationContext = new AutomationContext("BPS", "bpsServerInstance0001",
                FrameworkConstants.SUPER_TENANT_KEY, "clerk1");
        LoginLogoutClient clerk1LoginLogoutClient = new LoginLogoutClient(clerk1AutomationContext);
        clerk1LoginLogoutClient.login();
        //initialize HT Client API for Manager1 user
        AutomationContext manager1AutomationContext = new AutomationContext("BPS", "bpsServerInstance0001",
                FrameworkConstants.SUPER_TENANT_KEY, "manager1");
        LoginLogoutClient manager1LoginLogoutClient = new LoginLogoutClient(manager1AutomationContext);
        manager1LoginLogoutClient.login();
        //Setting greenMail server in port 3025
        ServerSetup setup = new ServerSetup(SMTP_TEST_PORT, "localhost", "smtp");
        greenMail = new GreenMail(setup);
        //Creating user in greenMail server
        greenMail.setUser(EMAIL_USER_ADDRESS, USER_NAME, USER_PASSWORD);
        greenMail.start();

    }

    /**
     * Update content in humantask.xml/output-event-adapters.xml & restart server
     *
     * @throws Exception
     */
    private void updateConfigFiles() throws Exception {
        final String artifactLocation = FrameworkPathUtil.getSystemResourceLocation()
                + HumanTaskTestConstants.DIR_ARTIFACTS + File.separator + HumanTaskTestConstants.DIR_CONFIG + File.separator
                + HumanTaskTestConstants.DIR_EMAIL + File.separator;
        //Adding new config file for humantask.xml
        File humantaskConfigNew = new File(artifactLocation + HumanTaskTestConstants.HUMANTASK_XML);
        File humantaskConfigOriginal = new File(FrameworkPathUtil.getCarbonHome() + File.separator + NEW_CONF_DIR + File.separator
                + HumanTaskTestConstants.HUMANTASK_XML);
        serverConfigurationManager.applyConfiguration(humantaskConfigNew, humantaskConfigOriginal, true, false);
        //Adding new config file for axis2_client.xml
        File outputEventAdapterConfigNew = new File(artifactLocation + HumanTaskTestConstants.OUTPUT_EVENT_ADAPTERS_XML);
        File outputEventAdapterConfigOriginal = new File(FrameworkPathUtil.getCarbonHome() + File.separator + NEW_CONF_DIR + File.separator  + HumanTaskTestConstants.OUTPUT_EVENT_ADAPTERS_XML);
        serverConfigurationManager.applyConfiguration(outputEventAdapterConfigNew, outputEventAdapterConfigOriginal, true, true);
    }

    public void deployArtifact() throws Exception {
        uploadHumanTaskForTest("taskDeadlineWithNotificationsTest");
    }


    private void addRoles() {
        try {
            String[] clerkUsers = new String[]{HumanTaskTestConstants.CLERK1_USER};
            String[] managerUsers = new String[]{HumanTaskTestConstants.MANAGER1_USER};
            userManagementClient.addRole(HumanTaskTestConstants.REGIONAL_CLERKS_ROLE, clerkUsers,
                    new String[]{"/permission/admin/login",
                            "/permission/admin/manage/humantask/viewtasks"}, false);
            userManagementClient.addRole(HumanTaskTestConstants.REGIONAL_MANAGER_ROLE, managerUsers,
                    new String[]{"/permission/admin/login",
                            "/permission/admin/manage/humantask/viewtasks"}, false);
        } catch (RemoteException e) {
            log.error(e);
        } catch (UserAdminUserAdminException e) {
            log.error(e);
        }

    }

    @Test(groups = {"wso2.bps.task.create"}, description = "Claims approval notification support test case", priority = 1, singleThreaded = true)
    public void createTaskWithNotifications() {
        try {
            String soapBody =
                    "<p:notify xmlns:p=\"http://www.example.com/claims/\">\n" +
                            "<firstname>John</firstname>\n" +
                            "<lastname>Denver</lastname>\n" +
                            "</p:notify>";

            String operation = "notify";
            String serviceName = "ClaimReminderService";
            List<String> expectedOutput = Collections.emptyList();
            log.info("Calling Service: " + backEndUrl + serviceName);
            requestSender.sendRequest(backEndUrl + serviceName, operation, soapBody, 1,
                    expectedOutput, false);
            //Wait for email notification to be received
            greenMail.waitForIncomingEmail(5000, 1);
            Message[] messages = greenMail.getReceivedMessages();
            Assert.assertNotNull(messages.length);
            Assert.assertEquals(messages[0].getSubject(), EMAIL_SUBJECT);
            Assert.assertTrue(String.valueOf(messages[0].getContent()).contains(EMAIL_TEXT));
        } catch (IOException e) {
            log.error(e);
        } catch (InterruptedException e) {
            log.error(e);
        } catch (MessagingException e) {
            log.error(e);
        } catch (Exception e) {
            log.error(e);
        }
    }

    @Test(groups = {"wso2.bps.task.clean"}, description = "Clean up server notifications", priority = 17, singleThreaded = true)
    public void removeArtifacts() {
        try {
            greenMail.stop();
            log.info("Undeploy claim reminder service");
            userManagementClient.deleteRole(HumanTaskTestConstants.REGIONAL_CLERKS_ROLE);
            userManagementClient.deleteRole(HumanTaskTestConstants.REGIONAL_MANAGER_ROLE);
            Assert.assertFalse(userManagementClient.roleNameExists(HumanTaskTestConstants.REGIONAL_CLERKS_ROLE));
            Assert.assertFalse(userManagementClient.roleNameExists(HumanTaskTestConstants.REGIONAL_MANAGER_ROLE));
            humanTaskPackageManagementClient.unDeployHumanTask("ClaimReminderService", "notify");
            loginLogoutClient.logout();
        } catch (InterruptedException e) {
            log.error(e);
        } catch (RemoteException e) {
            log.error(e);
        } catch (LogoutAuthenticationExceptionException e) {
            log.error(e);
        } catch (PackageManagementException e) {
            log.error(e);
        } catch (UserAdminUserAdminException e) {
            log.error(e);
        } catch (Exception e) {
            log.error(e);
        }
    }


}






