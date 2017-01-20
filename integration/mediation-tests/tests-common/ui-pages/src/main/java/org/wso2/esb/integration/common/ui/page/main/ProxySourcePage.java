package org.wso2.esb.integration.common.ui.page.main;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.util.List;

public class ProxySourcePage {

    private final WebDriver driver;

    public ProxySourcePage(WebDriver driver) throws IOException {
        this.driver = driver;
        if (!"Modify Proxy Service".equals(driver.findElement(By.id("middle")).findElement(By.tagName("h2")).getText())) {
            throw new IllegalStateException("This is not the 'Modify Proxy Service' page");
        }
    }

    public DeployedServicesPage save() throws IOException {
        WebElement saveButton = driver.findElement(By.cssSelector("#saveBtn"));
        saveButton.click();
        List<WebElement> error = driver.findElements(By.id("messagebox-error"));
        if (error.size() > 0) {
            throw new IllegalStateException("saving proxy caused an error : " + error.get(0).getText());
        } else {
            return new DeployedServicesPage(driver);
        }
    }
}