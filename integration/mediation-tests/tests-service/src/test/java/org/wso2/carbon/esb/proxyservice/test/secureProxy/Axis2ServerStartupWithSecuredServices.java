/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/
package org.wso2.carbon.esb.proxyservice.test.secureProxy;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.wso2.carbon.aarservices.stub.ExceptionException;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.utils.LoginLogoutClient;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.esb.integration.common.utils.ServiceDeploymentUtil;
import org.wso2.esb.integration.common.utils.common.TestConfigurationProvider;
import org.wso2.esb.integration.common.utils.servers.axis2.SampleAxis2Server;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class Axis2ServerStartupWithSecuredServices {
    private SampleAxis2Server axis2Server1 = null;

    @BeforeTest(alwaysRun = true)
    public void deployServices()
            throws IOException, LoginAuthenticationExceptionException, ExceptionException,
            XPathExpressionException, XMLStreamException, SAXException, URISyntaxException, AutomationUtilException {

        if (TestConfigurationProvider.isIntegration()) {
            axis2Server1 = new SampleAxis2Server("test_axis2_server_9007.xml");
            axis2Server1.deployService("SecureStockQuoteServiceScenario1");
            axis2Server1.start();

            axis2Server1.deployService("SecureStockQuoteServiceScenario2");
            axis2Server1.deployService("SecureStockQuoteServiceScenario3");
            axis2Server1.deployService("SecureStockQuoteServiceScenario4");
            axis2Server1.deployService("SecureStockQuoteServiceScenario5");
            axis2Server1.deployService("SecureStockQuoteServiceScenario6");
            axis2Server1.deployService("SecureStockQuoteServiceScenario7");
            axis2Server1.deployService("SecureStockQuoteServiceScenario8");
            //        axis2Server1.deployService("SecureStockQuoteServiceScenario9");
            //        axis2Server1.deployService("SecureStockQuoteServiceScenario10");

        } else {
            AutomationContext asContext = new AutomationContext("AS", TestUserMode.SUPER_TENANT_ADMIN);
            int deploymentDelay = TestConfigurationProvider.getServiceDeploymentDelay();
            String serviceName = "SecureStockQuoteServiceScenario";
            String serviceFilePath = TestConfigurationProvider.getResourceLocation("AXIS2")
                                     + File.separator + "aar" + File.separator + serviceName;
            ServiceDeploymentUtil deployer = new ServiceDeploymentUtil();
            LoginLogoutClient loginLogoutClient = new LoginLogoutClient(asContext);
            for (int i = 1; i < 9; i++) {
                deployer.deployArrService(asContext.getContextUrls().getBackEndUrl(), loginLogoutClient.login()
                        , serviceName + i, serviceFilePath + i + ".aar", deploymentDelay);
            }
        }
    }

    @AfterTest(alwaysRun = true)
    public void unDeployServices()
            throws IOException, LoginAuthenticationExceptionException, ExceptionException,
            XPathExpressionException, URISyntaxException, SAXException, XMLStreamException, AutomationUtilException {
        if (TestConfigurationProvider.isIntegration() && axis2Server1 != null && axis2Server1.isStarted()) {
            axis2Server1.stop();
        } else {
            AutomationContext asContext = new AutomationContext("AS", TestUserMode.SUPER_TENANT_ADMIN);
            int deploymentDelay = TestConfigurationProvider.getServiceDeploymentDelay();
            String serviceName = "SecureStockQuoteServiceScenario";
            ServiceDeploymentUtil deployer = new ServiceDeploymentUtil();
            LoginLogoutClient loginLogoutClient = new LoginLogoutClient(asContext);
            for (int i = 1; i < 9; i++) {
                deployer.unDeployArrService(asContext.getContextUrls().getBackEndUrl(), loginLogoutClient.login()
                        , serviceName + i, deploymentDelay);
            }

        }
    }
}
