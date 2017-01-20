/*
*  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

/**
 * https://wso2.org/jira/browse/ESBJAVA-4617
 * This Test class will test accessing the source view of a proxy service having a new line character
 * in parameters in java 8
 */
public class ESBJAVA4617SourceViewTestCase extends ESBIntegrationUITest {
    private WebDriver driver;

    @BeforeClass(alwaysRun = true)
    public void setUp() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("artifacts/ESB/synapseconfig/proxyadmin/ESBJAVA4617Proxy.xml");
        driver = BrowserManager.getWebDriver();
        driver.get(getLoginURL());
    }

    @Test(groups = "wso2.esb", description = "Accessing source view of a proxy service having new line character" +
                                             " in parameters")
    public void sourceViewTest() throws Exception {
        LoginPage test = new LoginPage(driver);
        HomePage home = test.loginAs(userInfo.getUserName(), userInfo.getPassword());
        home.clickMenu("Services","List");
        DeployedServicesPage listPage = new DeployedServicesPage(driver);
        ProxySourcePage sourcePage = listPage.gotoSourceView("ProxyHavingSourceViewIssueInJava8");
        sourcePage.save();
        driver.close();
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() throws Exception {
        driver.quit();
        super.cleanup();
    }
}
