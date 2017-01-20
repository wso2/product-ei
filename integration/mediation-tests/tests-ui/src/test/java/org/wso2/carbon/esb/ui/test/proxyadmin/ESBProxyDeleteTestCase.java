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
import org.wso2.esb.integration.common.ui.page.main.DeployedServicesPage;
import org.wso2.esb.integration.common.ui.page.main.HomePage;
import org.wso2.esb.integration.common.utils.ESBIntegrationUITest;

import java.util.List;

public class ESBProxyDeleteTestCase extends ESBIntegrationUITest {
    private WebDriver driver;

    @BeforeClass(alwaysRun = true)
    public void setUp() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("artifacts/ESB/synapseconfig/proxyadmin/synapse.xml");
        driver = BrowserManager.getWebDriver();
        driver.get(getLoginURL());
    }

    @Test(groups = "wso2.esb", description = "verify proxy service can be deleted.")
    public void testLogin() throws Exception {
//        boolean isCloud = isRunningOnCloud();
        LoginPage test = new LoginPage(driver);
        HomePage home = test.loginAs(userInfo.getUserName(), userInfo.getPassword());
        home.clickMenu("Services","List");
        DeployedServicesPage listPage = new DeployedServicesPage(driver);
        deleteService("testWithAngleBrackets");
        boolean bol = false;
        long stop = System.currentTimeMillis() +30000;
        while (stop > System.currentTimeMillis()){
            if (!isServiceExists("testWithAngleBrackets")){
                bol = true;
                break;
            }
            Thread.sleep(2000);
        }
        Assert.assertTrue(bol);
        driver.close();
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() throws Exception {
        driver.quit();
        super.cleanup();
    }

    private void deleteService(String serviceName){
        List<WebElement> tr;
        tr = driver.findElement(By.id("sgTable")).findElements(By.xpath("/html/body/table/tbody/tr[2]/td[3]/table/tbody/tr[2]/td/div/div/form[2]/table/tbody/tr"));
        for (WebElement service : tr) {
            if (service.getText().substring(0, service.getText().indexOf(" ")).trim().equals(serviceName.trim())) {
                service.findElement(By.name("serviceGroups")).click();
                driver.findElement(By.id("delete1")).click();
                driver.findElement(By.xpath("/html/body/div[3]/div[2]/button")).click();
                break;
            }
        }
    }

    private boolean isServiceExists(String serviceName){
        List<WebElement> tr;
        tr = driver.findElement(By.id("sgTable")).findElements(By.xpath("/html/body/table/tbody/tr[2]/td[3]/table/tbody/tr[2]/td/div/div/form[2]/table/tbody/tr"));
        for (WebElement service : tr) {
            if (service.getText().substring(0,service.getText().indexOf(" ")).trim().equals(serviceName.trim())) {
                return true;
            }
        }
        return false;
    }
}
