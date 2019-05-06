/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.ui.test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.extensions.selenium.BrowserManager;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.utils.CarbonUtils;
import org.wso2.esb.integration.common.utils.ESBIntegrationUITest;

import java.io.File;

public class XssCsrfSkipPatternsTestCase extends ESBIntegrationUITest {

    private WebDriver driver;
    private ServerConfigurationManager scm;
    private File carbonXML;
    private File catalinaXML;

    @BeforeClass(alwaysRun = true)
    public void setUp() throws Exception {
        super.init();
        changeESBConfiguration();
    }

    @Test(groups = "wso2.is", description = "verify XSS prevention exist")
    public void testXSSPrevention() throws Exception {

        LogViewerClient logViewerClient =  new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());

        driver = BrowserManager.getWebDriver();
        driver.get(getLoginURL());

        driver.findElement(By.id("txtUserName")).clear();
        driver.findElement(By.id("txtUserName")).sendKeys("admin");
        driver.findElement(By.id("txtPassword")).clear();
        driver.findElement(By.id("txtPassword")).sendKeys("admin");
        driver.findElement(By.cssSelector("input.button")).click();
        driver.findElement(By.cssSelector("#menu-panel-button3 > span")).click();
        driver.findElement(By.linkText("Add New Tenant")).click();
        driver.findElement(By.id("domain")).clear();
        driver.findElement(By.id("domain")).sendKeys("domain1.com");
        driver.findElement(By.id("admin-firstname")).clear();
        driver.findElement(By.id("admin-firstname")).sendKeys("admin");
        driver.findElement(By.id("admin-lastname")).clear();
        driver.findElement(By.id("admin-lastname")).sendKeys("admin");
        driver.findElement(By.id("admin")).clear();
        driver.findElement(By.id("admin")).sendKeys("admin");
        driver.findElement(By.id("admin-password")).clear();
        driver.findElement(By.id("admin-password")).sendKeys("Test#1234");
        driver.findElement(By.id("admin-password-repeat")).clear();
        driver.findElement(By.id("admin-password-repeat")).sendKeys("Test#1234");
        driver.findElement(By.id("admin-email")).clear();
        driver.findElement(By.id("admin-email")).sendKeys("admin@gmail.com");
        driver.findElement(By.cssSelector("input.button")).click();
        driver.findElement(By.cssSelector("button[type=\"button\"]")).click();
        driver.findElement(By.linkText("Sign-out")).click();
        driver.findElement(By.id("txtUserName")).clear();
        driver.findElement(By.id("txtUserName")).sendKeys("admin@domain1.com");
        driver.findElement(By.id("txtPassword")).clear();
        driver.findElement(By.id("txtPassword")).sendKeys("Test#1234");
        driver.findElement(By.cssSelector("input.button")).click();
        driver.findElement(By.cssSelector("span")).click();
        driver.findElement(By.linkText("Templates")).click();

        driver.findElement(By.linkText("Add Sequence Template")).click();
        driver.findElement(By.linkText("switch to source view")).click();
        driver.switchTo().frame("frame_sequence_source");
        driver.findElement(By.id("textarea")).click();
        driver.findElement(By.id("textarea")).click();
        driver.findElement(By.id("textarea")).clear();
        driver.findElement(By.id("textarea")).sendKeys(
                "<template xmlns=\"http://ws.apache.org/ns/synapse\" name=\"test7\">\n"
                    + "<sequence>\n"
                        + "<script language=\"js\"/>\n"
                    + "</sequence>\n"
                + " </template>");
        driver.switchTo().defaultContent();
        driver.findElement(By.cssSelector("input.button")).click();
        Assert.assertTrue(
                !logViewerClient.getAllRemoteSystemLogs()[0].getMessage().contains("Could not handle request"),
                "XSS attack prevention failed.");
    }

    /**
     * Configures ESB as required for the test case.
     *
     * @throws Exception if an error occurs while configuring ESB
     */
    private void changeESBConfiguration() throws Exception {

        String carbonHome = CarbonUtils.getCarbonHome();
        carbonXML = new File(
                carbonHome + File.separator + "conf" + File.separator + "carbon.xml");
        File configuredCarbonXML = new File(
                getESBResourceLocation() + File.separator + "XssCsrfSkipPatterns" + File.separator
                + "carbon-security.xml");

        catalinaXML = new File(
                carbonHome + File.separator + "conf" + File.separator + "tomcat"
                + File.separator + "catalina-server.xml");

        File configuredCatalinaXML = new File(
                getESBResourceLocation() + File.separator + "XssCsrfSkipPatterns" + File.separator
                + "catalina-server-security.xml");

        scm = new ServerConfigurationManager(context);
        scm.applyConfigurationWithoutRestart(configuredCarbonXML, carbonXML, true);
        scm.applyConfigurationWithoutRestart(configuredCatalinaXML, catalinaXML, true);
        scm.restartGracefully();
        super.init();
    }

    /**
     * Resets the configuration changes done to what was there before.
     *
     * @throws Exception if an error occurs while applying previous configurations
     */
    private void resetESBConfiguration() throws Exception {

        String carbonHome = CarbonUtils.getCarbonHome();
        carbonXML = new File(
                carbonHome + File.separator + "conf" + File.separator + "carbon.xml");
        File configuredCarbonXML = new File(
                getESBResourceLocation() + File.separator + "XssCsrfSkipPatterns" + File.separator
                + "carbon-default.xml");

        catalinaXML = new File(
                carbonHome + File.separator + "conf" + File.separator + "tomcat"
                + File.separator + "catalina-server.xml");
        File configuredCatalinaXML = new File(
                getESBResourceLocation() + File.separator + "XssCsrfSkipPatterns" + File.separator
                + "catalina-server-default.xml");

        scm = new ServerConfigurationManager(context);
        scm.applyConfigurationWithoutRestart(configuredCarbonXML, carbonXML, true);
        scm.applyConfigurationWithoutRestart(configuredCatalinaXML, catalinaXML, true);
        scm.restartGracefully();
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() throws Exception {
        driver.quit();
        resetESBConfiguration();
    }
}
