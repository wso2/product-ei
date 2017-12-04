/*
*  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/
package org.wso2.carbon.esb.ui.test.templates;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.extensions.selenium.BrowserManager;
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;
import org.wso2.esb.integration.common.ui.page.LoginPage;
import org.wso2.esb.integration.common.utils.ESBIntegrationUITest;

/**
 * Test the mediation template service which was used by the management console template creation wizard
 */
public class TemplateMediatorUITestCase extends ESBIntegrationUITest {
    private WebDriver driver;
    private ResourceAdminServiceClient resourceAdminServiceClient;

    /**
     * Initialize drive and set the console URL
     *
     * @throws Exception
     */
    @BeforeClass(alwaysRun = true)
    public void setUp() throws Exception {
        super.init();
        driver = BrowserManager.getWebDriver();
        driver.get(getLoginURL());
        resourceAdminServiceClient = new ResourceAdminServiceClient(contextUrls.getBackEndUrl(), getSessionCookie());
    }

    /**
     * Test for template create, edit and delete functions
     *
     * @throws Exception
     */
    @Test(groups = "wso2.esb",
          description = "Verify that endpoints page renders when an invalid dynamic endpoint is present.")
    public void testTemplatesListing() throws Exception {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.loginAs(userInfo.getUserName(), userInfo.getPassword());

        //go to template listing
        driver.findElement(By.linkText("Templates")).click();

        //create a new template
        By newTemplateButoon = By.linkText("Add Sequence Template");
        Assert.assertTrue(isElementPresent(newTemplateButoon),
                          "Unable to find the button to create a new sequence template");
        driver.findElement(newTemplateButoon).click();
        driver.findElement(By.id("sequence.name")).clear();
        driver.findElement(By.id("sequence.name")).sendKeys("sampleTemplate");

        //create template configuration
        driver.findElement(By.linkText("Add Child")).click();
        driver.findElement(By.linkText("Core")).click();
        driver.findElement(By.linkText("Log")).click();
        new Select(driver.findElement(By.id("mediator.log.log_level"))).selectByVisibleText("Custom");
        driver.findElement(By.linkText("Add Property")).click();
        driver.findElement(By.id("propertyName0")).clear();
        driver.findElement(By.id("propertyName0")).sendKeys("MESSAGE");
        new Select(driver.findElement(By.id("propertyTypeSelection0"))).selectByVisibleText("Expression");
        driver.findElement(By.id("propertyValue0")).clear();
        driver.findElement(By.id("propertyValue0")).sendKeys("$func:message");
        driver.findElement(By.cssSelector("input.button")).click();
        driver.findElement(By.linkText("Add Parameter")).click();
        driver.findElement(By.id("templatePropertyName0")).clear();
        driver.findElement(By.id("templatePropertyName0")).sendKeys("message");
        driver.findElement(By.id("saveButton")).click();

        By editButton = By.linkText("Edit");
        Assert.assertTrue(isElementPresent(editButton),
                          "New template creation failed, unable to find the created template");

        //edit and save the template
        driver.findElement(editButton).click();
        driver.findElement(By.linkText("switch to source view")).click();
        driver.findElement(By.linkText("switch to design view")).click();

        driver.findElement(By.linkText("Log")).click();
        driver.findElement(By.id("propertyName0")).clear();
        driver.findElement(By.id("propertyName0")).sendKeys("GREETING_MESSAGE");
        driver.findElement(By.id("saveButton")).click();

        Assert.assertTrue(isElementPresent(editButton), "Unable to find the edited template entry.");

        //delete the create template
        driver.findElement(By.linkText("Delete")).click();
        driver.findElement(By.xpath("/html/body/div[3]/div[2]/button[1]")).click();

        Assert.assertFalse(isElementPresent(editButton), "Deleted template entry is still available.");

        driver.close();
    }

    /**
     * Return whether a particular element is present or not
     *
     * @param by
     * @return
     */
    private boolean isElementPresent(By by) {
        try {
            return null != driver.findElement(by);
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Quit web driver
     *
     * @throws Exception
     */
    @AfterClass(alwaysRun = true)
    public void tearDown() throws Exception {
        driver.quit();
        resourceAdminServiceClient = null;
        super.cleanup();

    }
}
