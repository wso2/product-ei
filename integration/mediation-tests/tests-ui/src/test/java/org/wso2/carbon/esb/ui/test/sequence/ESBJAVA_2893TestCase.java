package org.wso2.carbon.esb.ui.test.sequence;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.api.selenium.home.HomePage;
import org.wso2.carbon.automation.api.selenium.login.LoginPage;
import org.wso2.carbon.automation.core.BrowserManager;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.esb.ui.test.ESBIntegrationUITest;

public class ESBJAVA_2893TestCase extends ESBIntegrationUITest {

	private WebDriver driver;
	private String sequnceContent = "<sequence xmlns=\"http://ws.apache.org/ns/synapse\" name=\"sample\">"
	                                + "<property xmlns:ns=\"http://org.apache.synapse/xsd\" "
	                                + "name=\"Sample_PAYLOAD\" expression=\"//ent:sample_req\" "
	                                + "scope=\"operation\" type=\"OM\"></property></sequence>";
	private String sequnceName = "editSequence('sample')";

	@BeforeClass(alwaysRun = true)
	public void setUp() throws Exception {
		super.init();
		driver = BrowserManager.getWebDriver();
		driver.get(getLoginURL(ProductConstant.ESB_SERVER_NAME));
	}

	@Test(groups = "wso2.esb", description = "operation scope is visible in UI ")
	public void testSequnceView() throws Exception {
		boolean isCloud = isRunningOnCloud();
		LoginPage test = new LoginPage(driver, isCloud);
		HomePage home = test.loginAs(userInfo.getUserName(), userInfo.getPassword());
		driver.findElement(By.xpath("//a[contains(.,'Sequences')]")).click();
		driver.findElement(By.xpath("//a[contains(.,'Add Sequence')]")).click();
		driver.findElement(By.xpath("//a[contains(.,'switch to source view')]")).click();
		driver.switchTo().frame("frame_sequence_source");
		driver.findElement(By.xpath("//textarea[@id='textarea']")).clear();
		driver.findElement(By.xpath("//textarea[@id='textarea']")).sendKeys(sequnceContent);
		driver.switchTo().defaultContent();
		driver.findElement(By.xpath("//input[contains(@value,'Save & Close')]")).click();
		driver.findElement(By.xpath("//a[contains(.,'Sequences')]")).click();

		List<WebElement> list = driver.findElements(By.xpath("//a[contains(.,'Edit')]"));
		boolean isSequnceThere = false;
		for (WebElement webElement : list) {
			if (webElement.getAttribute("onclick").equals(sequnceName)) {
				isSequnceThere = true;
				webElement.click();

			}
		}
		junit.framework.Assert.assertTrue("Possible issue ESBJAVA-2893", isSequnceThere);

		driver.findElement(By.xpath("//a[contains(.,'Property')]")).click();;
		if (!driver.findElement(By.xpath("//option[@value='operation']")).isSelected()) {
			Assert.fail("Possible issue ESBJAVA-2893");
		}
		driver.close();
	}

	@AfterClass(alwaysRun = true)
	public void tearDown() throws Exception {
		driver.quit();
	}
}
