package org.wso2.esb.integration.common.ui.page.main;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.util.List;

public class DeployedServicesPage {
    private final WebDriver driver;

    public DeployedServicesPage(WebDriver driver) throws IOException {
        this.driver = driver;
        if (!"Deployed Services".equals(driver.findElement(By.id("middle")).findElement(By.tagName("h2")).getText())) {
            throw new IllegalStateException("This is not the Deployed Service Page");
        }
    }

    public ProxySourcePage gotoSourceView(String serviceName) throws IOException {
        List<WebElement> servicesTable = driver.findElements(By.xpath("//*[@id=\"sgTable\"]/tbody/tr"));
        for (int i = 0; i < servicesTable.size(); i++) {
            WebElement serviceLink = servicesTable.get(i);
            if (serviceName.equals(serviceLink.findElements(By.tagName("td")).get(1).getText())) {
                WebElement showSource = serviceLink.findElement(By.linkText("Source View"));
                showSource.click();
                return new ProxySourcePage(driver);
            }
        }
        throw new IllegalStateException("service named '" + serviceName + "' is not visible in the UI");
    }
}