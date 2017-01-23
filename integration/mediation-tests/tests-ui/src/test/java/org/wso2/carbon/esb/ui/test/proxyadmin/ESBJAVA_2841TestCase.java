/*
 * Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.ui.test.proxyadmin;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.extensions.selenium.BrowserManager;
import org.wso2.esb.integration.common.ui.page.LoginPage;
import org.wso2.esb.integration.common.utils.ESBIntegrationUITest;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ESBJAVA_2841TestCase extends ESBIntegrationUITest {
    private WebDriver driver;

    @BeforeClass(alwaysRun = true)
    public void setUp() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("artifacts/ESB/synapseconfig/proxyadmin/log_with_custom_property.xml");
        driver = BrowserManager.getWebDriver();
        driver.get(getLoginURL());

        TimeUnit.SECONDS.sleep(2);
    }


    @Test(groups = "wso2.esb", description = "verify log mediator can be accessed via mgmt console with custom properties")
    public void addLogMediatorCustomProperty() throws Exception {
        LoginPage logPage = new LoginPage(driver);
        logPage.loginAs(userInfo.getUserName(), userInfo.getPassword());

        //Click on API view
        driver.findElement(By.linkText("List")).click();

        //Reading the table of APIs
        WebElement table = driver.findElement(By.id("sgTable"));

        //getting all the rows
        List<WebElement> allRows = table.findElements(By.tagName("tr"));

        outerLoop:
        for (WebElement row : allRows) {
            List<WebElement> cells = row.findElements(By.tagName("nobr"));

            //Selecting the LogMediatorProxy service
            for (WebElement proxyService : cells) {
                if (proxyService.getText().equals("LogMediatorProxy")) {

                    //go to Edit -> Design view
                    row.findElement(By.linkText("Design View")).click();
                    driver.findElement(By.id("nextBtn")).click();

                    //clicking the edit button of in sequence
                    driver.findElement(By.id("inAnonAddEdit")).click();

                    //look for the log mediator
                    WebElement logMediator = driver.findElement(By.linkText("Log"));
                    logMediator.click();

                    TimeUnit.SECONDS.sleep(1);
                    // look for the 'Add Property' link
                    driver.findElement(By.linkText("Add Property")).click();

                    // specify the name for the custom property
                    driver.findElement(By.xpath("//*[@id=\"propertyName0\"]")).sendKeys("testProperty");

                    // specify the value for the custom property
                    driver.findElement(By.xpath("//*[@id=\"propertyValue0\"]")).sendKeys("testValue");

                    // add the property
                    driver.findElement(By.xpath("//*[@id=\"mediator-editor-form\"]/table/tbody/tr/td[1]/input[4]")).click();

                    TimeUnit.SECONDS.sleep(1);

                    // check if loading the log mediator editor works now
                    logMediator.click();

                    //checking the log mediator editor is successfully loaded
                    String logMediatorEditorText = driver.findElement(By.xpath("//*[@id=\"mediator-editor-form\"]/div/table/tbody/tr[1]/td/h2")).getText();

                    Assert.assertEquals(logMediatorEditorText, "Log Mediator", "Log mediator editor did not load properly");

                    /***
                     * need to check with error message without the patch
                     */

                    TimeUnit.SECONDS.sleep(1);

                    break outerLoop;
                }
            }
        }
        TimeUnit.SECONDS.sleep(2);
    }


    @AfterClass(alwaysRun = true)
    public void tearDown() throws Exception {
        driver.close();
        super.cleanup();
    }


}
