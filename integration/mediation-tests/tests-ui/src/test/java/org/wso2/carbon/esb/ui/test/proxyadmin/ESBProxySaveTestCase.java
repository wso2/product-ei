package org.wso2.carbon.esb.ui.test.proxyadmin;


import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.extensions.selenium.BrowserManager;
import org.wso2.esb.integration.common.ui.page.LoginPage;
import org.wso2.esb.integration.common.ui.page.main.DeployedServicesPage;
import org.wso2.esb.integration.common.ui.page.main.HomePage;
import org.wso2.esb.integration.common.ui.page.main.ProxySourcePage;
import org.wso2.esb.integration.common.utils.ESBIntegrationUITest;

public class ESBProxySaveTestCase  extends ESBIntegrationUITest {

    private WebDriver driver;

    @BeforeClass(alwaysRun = true)
    public void setUp() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("artifacts/ESB/synapseconfig/proxyadmin/testconfig.xml");
        driver = BrowserManager.getWebDriver();
        driver.get(getLoginURL());
    }

    @Test(groups = "wso2.esb", description = "verify proxy service can be saved.")
    public void testLogin() throws Exception {
//        boolean isCloud = isRunningOnCloud();
        LoginPage test = new LoginPage(driver);
        HomePage home = test.loginAs(userInfo.getUserName(), userInfo.getPassword());
        home.clickMenu("Services","List");
        DeployedServicesPage listPage = new DeployedServicesPage(driver);
        ProxySourcePage sourcePage = listPage.gotoSourceView("echoProxy");
        sourcePage.save();
        driver.close();
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() throws Exception {
        driver.quit();
        super.cleanup();
    }
}
