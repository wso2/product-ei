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

package org.wso2.carbon.esb.rest.test.security;

import junit.framework.Assert;
import org.testng.annotations.BeforeTest;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.context.beans.User;
import org.wso2.carbon.integration.common.admin.client.UserManagementClient;
import org.wso2.carbon.integration.common.utils.LoginLogoutClient;
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;

/**
 * This test case is supposed to add a user who is not belongs to admin user group. Then this user
 * will be used in other test cases which needs non admin user and user group
 */
public class NonAdminUserCreationTestCase {
    protected static User nonAdminUser = null;
    protected final String ROLE_NAME = "nonadmin";

    @BeforeTest(alwaysRun = true)
    public void addNonAdminUser() throws Exception {
        AutomationContext esbContext = new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN);
        String sessionCookie = new LoginLogoutClient(esbContext).login();
        ResourceAdminServiceClient resourceAdmin = new ResourceAdminServiceClient(esbContext.getContextUrls().getBackEndUrl(), sessionCookie);
        UserManagementClient userManagementClient = new UserManagementClient(esbContext.getContextUrls().getBackEndUrl(), sessionCookie);

        //done this change due to a bug in UM - please refer to carbon dev mail
        // "G-Reg integration test failures due to user mgt issue."
        String[] permissions = {"/permission/admin/configure/",
                                "/permission/admin/login",
                                "/permission/admin/manage/",
                                "/permission/admin/monitor",
                                "/permission/protected"};


        if (!userManagementClient.roleNameExists(ROLE_NAME)) {
            userManagementClient.addRole(ROLE_NAME, null, permissions);
            resourceAdmin.addResourcePermission("/", ROLE_NAME, "3", "1");
            resourceAdmin.addResourcePermission("/", ROLE_NAME, "2", "1");
            resourceAdmin.addResourcePermission("/", ROLE_NAME, "4", "1");
            resourceAdmin.addResourcePermission("/", ROLE_NAME, "5", "1");
        }

        userManagementClient.addUser("nonadminuser", "password", new String[]{ROLE_NAME}, null);
        //check user creation
        nonAdminUser = new User();
        nonAdminUser.setUserName("nonadminuser");
        nonAdminUser.setPassword("password");


    }

    protected static User getUser(){
        Assert.assertNotNull( "User is not created. Please create the user first", nonAdminUser);
        return nonAdminUser;
    }

}
