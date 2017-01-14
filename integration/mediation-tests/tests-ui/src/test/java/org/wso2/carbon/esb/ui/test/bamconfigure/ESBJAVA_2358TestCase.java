package org.wso2.carbon.esb.ui.test.bamconfigure;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.api.selenium.home.HomePage;
import org.wso2.carbon.automation.api.selenium.login.LoginPage;
import org.wso2.carbon.automation.core.BrowserManager;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.esb.ui.test.ESBIntegrationUITest;

/**
 * This is to test the availability of Mediation Data Publisher Page
 */
public class ESBJAVA_2358TestCase extends ESBIntegrationUITest {
    private WebDriver driver;

    @BeforeClass(alwaysRun = true)
    public void setUp() throws Exception {
        super.init();
        driver = BrowserManager.getWebDriver();
        driver.get(getLoginURL(ProductConstant.ESB_SERVER_NAME));
    }

    @Test(groups = "wso2.esb", description = "verify Mediation Data publisher Page")
    public void testMedPubPage() throws Exception {
        boolean isCloud = isRunningOnCloud();
        LoginPage test = new LoginPage(driver, isCloud);
        HomePage home = test.loginAs(userInfo.getUserName(), userInfo.getPassword());
        driver.findElement(By.id("menu-panel-button3")).click();
        driver.findElement(By.linkText("Mediation Data Publishing")).click();
        try {
            driver.findElement(By.xpath("//*[@id=\"middle\"]/h2")).getText();
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("Mediation Data Publisher Page not found", e);
        }
        Assert.assertEquals(driver.findElement(By.xpath("//*[@id=\"middle\"]/h2")).getText()
                , "Mediation Data Publisher Configuration"
                , "Mediation Data Publisher Page not found");
        home.logout();
        driver.close();
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() throws Exception {
        driver.quit();
    }
}
