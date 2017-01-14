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
package org.wso2.carbon.esb.rest.test.security;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.utils.LoginLogoutClient;
import org.wso2.esb.integration.common.utils.ESBTestConstant;
import org.wso2.esb.integration.common.utils.ServiceDeploymentUtil;
import org.wso2.esb.integration.common.utils.common.TestConfigurationProvider;
import org.wso2.esb.integration.common.utils.servers.axis2.SampleAxis2Server;

import java.io.File;

public class Axis2ServerStartupTestCase {
    private SampleAxis2Server axis2Server1 = null;
    private AutomationContext asContext;

    @BeforeTest(alwaysRun = true)
    public void deployServices() throws Exception {

        if (TestConfigurationProvider.isIntegration()) {
            axis2Server1 = new SampleAxis2Server("test_axis2_server_9009.xml");
            axis2Server1.start();
            axis2Server1.deployService(ESBTestConstant.STUDENT_REST_SERVICE);
            axis2Server1.deployService(ESBTestConstant.SIMPLE_AXIS2_SERVICE);
            axis2Server1.deployService(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE);

        } else {
            asContext = new AutomationContext("AS", TestUserMode.SUPER_TENANT_ADMIN);
            int deploymentDelay = TestConfigurationProvider.getServiceDeploymentDelay();
            String serviceName = ESBTestConstant.SIMPLE_AXIS2_SERVICE;
            String serviceFilePath = TestConfigurationProvider.getResourceLocation("AXIS2")
                                     + File.separator + "aar" + File.separator + serviceName + ".aar";
            ServiceDeploymentUtil deployer = new ServiceDeploymentUtil();
            String sessionCookie = new LoginLogoutClient(asContext).login();
            deployer.deployArrService(asContext.getContextUrls().getBackEndUrl(), sessionCookie
                    , serviceName, serviceFilePath, deploymentDelay);

            String studentServiceName = ESBTestConstant.STUDENT_REST_SERVICE;
            String studentServiceFilePath = TestConfigurationProvider.getResourceLocation("AXIS2")
                                            + File.separator + "aar" + File.separator + studentServiceName + ".aar";
            deployer.deployArrService(asContext.getContextUrls().getBackEndUrl(), sessionCookie
                    , studentServiceName, studentServiceFilePath, deploymentDelay);

        }
    }

    @AfterTest(alwaysRun = true)
    public void unDeployServices() throws Exception {
        if (axis2Server1 != null && axis2Server1.isStarted()) {
            axis2Server1.stop();
        } else {
            if (TestConfigurationProvider.isPlatform() && asContext!=null) {
                int deploymentDelay = TestConfigurationProvider.getServiceDeploymentDelay();
                String serviceName = ESBTestConstant.SIMPLE_AXIS2_SERVICE;
                String studentServiceName = ESBTestConstant.STUDENT_REST_SERVICE;
                ServiceDeploymentUtil deployer = new ServiceDeploymentUtil();
                String sessionCookie = new LoginLogoutClient(asContext).login();
                deployer.unDeployArrService(asContext.getContextUrls().getBackEndUrl(), sessionCookie
                        , serviceName, deploymentDelay);
                deployer.unDeployArrService(asContext.getContextUrls().getBackEndUrl(), sessionCookie
                        , studentServiceName, deploymentDelay);

            }
        }
    }
}
