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
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.wso2.mb.integration.common.utils.backend;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.extensions.selenium.BrowserManager;
import org.wso2.carbon.integration.common.utils.LoginLogoutClient;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.mb.integration.common.utils.ui.UIElementMapper;
import org.wso2.mb.integration.common.utils.ui.pages.login.LoginPage;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * The following class is the base class for all the UI test cases for MB.
 */
public class MBIntegrationUiBaseTest {
    private static final Log log = LogFactory.getLog(MBIntegrationUiBaseTest.class);
    protected AutomationContext mbServer;
    protected String sessionCookie;
    protected String backendURL;
    protected ServerConfigurationManager serverManager;
    protected LoginLogoutClient loginLogoutClient;
    protected WebDriver driver;
    /** custom admin role name set with restartServerWithDifferentAdminRoleName() method */
    protected static final String CUSTOM_ADMIN_ROLE_NAME = "administrator";

    /**
     * Initializes the automation context, login client, session cookie and the backend url by {@link org.wso2.carbon
     * .automation.engine.context.TestUserMode#SUPER_TENANT_ADMIN}.
     *
     * @throws AutomationUtilException
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    protected void init() throws AutomationUtilException, IOException, XPathExpressionException {
        mbServer = new AutomationContext("MB", TestUserMode.SUPER_TENANT_ADMIN);
        loginLogoutClient = new LoginLogoutClient(mbServer);
        sessionCookie = loginLogoutClient.login();
        backendURL = mbServer.getContextUrls().getBackEndUrl();
        this.driver = BrowserManager.getWebDriver();
    }

    /**
     * Initializes the automation context, login client, session cookie and the backend url by a {@link org.wso2
     * .carbon.automation.engine.context.TestUserMode}.
     *
     * @param testUserMode The testing user mode.
     * @throws XPathExpressionException
     * @throws AutomationUtilException
     * @throws MalformedURLException
     */
    protected void init(TestUserMode testUserMode) throws XPathExpressionException, AutomationUtilException,
                                                                                                MalformedURLException {
        mbServer = new AutomationContext("MB", testUserMode);
        loginLogoutClient = new LoginLogoutClient(mbServer);
        sessionCookie = loginLogoutClient.login();
        backendURL = mbServer.getContextUrls().getBackEndUrl();
        this.driver = BrowserManager.getWebDriver();
    }

    /**
     * Get current test user's Username according to the automation context
     *
     * @throws XPathExpressionException
     */
    protected String getCurrentUserName() throws XPathExpressionException {
        return mbServer.getContextTenant().getContextUser().getUserName();
    }

    /**
     * Get current test user's password according to the automation context
     *
     * @throws XPathExpressionException
     */
    protected String getCurrentPassword() throws XPathExpressionException {
        return mbServer.getContextTenant().getContextUser().getPassword();
    }

    /**
     * Return the admin user name of current context tenant
     * @return admin name as a String
     * @throws XPathExpressionException
     */
    protected String getAdminUserName() throws XPathExpressionException {
        return mbServer.getContextTenant().getTenantAdmin().getUserName();
    }

    /**
     * Get the password of admin user of current context tenant
     * @return password as a String
     * @throws XPathExpressionException
     */
    protected String getAdminPassword() throws XPathExpressionException {
        return mbServer.getContextTenant().getTenantAdmin().getPassword();
    }

    /**
     * Restart the testing MB server with WSO2 domain name set under user management
     *
     * @throws AutomationUtilException
     * @throws XPathExpressionException
     * @throws IOException
     */
    protected void restartServerWithDomainName() throws AutomationUtilException, XPathExpressionException,
            IOException {
        serverManager = new ServerConfigurationManager(mbServer);

        // Replace the user-mgt.xml with the new configuration and restarts the server.
        serverManager.applyConfiguration(new File(FrameworkPathUtil.getSystemResourceLocation() + File.separator +
                "artifacts" + File.separator + "mb" + File.separator + "config" + File.separator
                + "user-mgt.xml"), new File(ServerConfigurationManager.getCarbonHome() +
                File.separator + "repository" + File.separator + "conf" + File.separator +
                "user-mgt.xml"), true, true);
    }

    /**
     * Restart the server with admin role name set to "administrator" instead of default value admin.
     *
     * @throws IOException
     * @throws AutomationUtilException
     * @throws XPathExpressionException
     */
    protected void restartServerWithDifferentAdminRoleName() throws IOException, AutomationUtilException,
            XPathExpressionException {
        serverManager = new ServerConfigurationManager(mbServer);

        // Replace the user-mgt.xml with the new configuration and restarts the server.
        serverManager.applyConfiguration(new File(FrameworkPathUtil.getSystemResourceLocation() + File.separator +
                "artifacts" + File.separator + "mb" + File.separator + "config" + File.separator
                + "user-mgt-admin-role-name.xml"), new File(ServerConfigurationManager.getCarbonHome() +
                File.separator + "repository" + File.separator + "conf" + File.separator +
                "user-mgt.xml"), true, true);
    }

    /**
     * Restart the server with previous configuration.
     *
     * @throws IOException
     * @throws AutomationUtilException
     */
    protected void restartInPreviousConfiguration() throws IOException, AutomationUtilException {
        serverManager.restoreToLastConfiguration(true);
    }

    /**
     * Gets the default login url for management console.
     * @return The URL.
     */
    protected String getLoginURL() throws XPathExpressionException {
        return mbServer.getContextUrls().getWebAppURLHttps() + "/carbon/admin/login.jsp";
    }

    /**
     * Logs out from MB management console
     *
     * @return The login page.
     * @throws IOException
     */
    protected LoginPage logout() throws IOException {
        driver.findElement(By.xpath(UIElementMapper.getInstance().getElement("home.mb.sign.out.xpath"))).click();
        return new LoginPage(driver);
    }

    /**
     * Returns MQTT port based on automation.xml configurations
     * @throws XPathExpressionException
     */
    protected Integer getMQTTPort() throws XPathExpressionException {
        return Integer.parseInt(mbServer.getInstance().getPorts().get("mqtt"));
    }

    /**
     * Returns MQTT port based on automation.xml configurations
     * @throws XPathExpressionException
     */
    protected Integer getAMQPPort() throws XPathExpressionException {
        return Integer.parseInt(mbServer.getInstance().getPorts().get("amqp"));
    }
}
