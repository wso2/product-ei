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
package org.wso2.carbon.esb.local.transport.test;

import org.apache.axiom.om.OMElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.xml.namespace.QName;
import java.io.File;

import static org.testng.Assert.assertEquals;

/**
 * This class can be used as a sample test case to test ESB local transport functionality
 */
public class LocalTransportTestCase extends ESBIntegrationTest {

    private ServerConfigurationManager configurationManagerAxis2;
    private ServerConfigurationManager configurationManagerCarbon;

    @BeforeClass(alwaysRun = true)
    public void deployProxyServices() throws Exception {
        super.init();

        configurationManagerAxis2 =
                new ServerConfigurationManager(new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN));
        configurationManagerCarbon =
                new ServerConfigurationManager(new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN));

        File customAxisConfigAxis2 = new File(getESBResourceLocation() + File.separator +
                "local" + File.separator + "axis2.xml");

        // enable non-blocking transport axis2 configuration
        configurationManagerAxis2.applyConfigurationWithoutRestart(customAxisConfigAxis2);

        File customAxisConfigCarbon = new File(getESBResourceLocation() + File.separator +
                "local" + File.separator + "carbon.xml");

        // restart the server with the changes done to carbon.xml & axis2.xml
        configurationManagerCarbon.applyConfiguration(customAxisConfigCarbon);

        super.init();

        loadESBConfigurationFromClasspath(File.separator + "artifacts" + File.separator +
                "ESB" + File.separator + "local" + File.separator + "local-transport.xml");

    }

    @AfterClass(alwaysRun = true)
    public void unDeployProxyServices() throws Exception {
        super.cleanup();
        configurationManagerAxis2.restoreToLastConfiguration(false);
        configurationManagerCarbon.restoreToLastConfiguration();
    }

    @Test(groups = {"wso2.esb"}, description = "testing local transport scenario")
    public void testLocalTransportScenario() throws Exception {

        OMElement response = axis2Client.sendSimpleStockQuoteRequest
                (getProxyServiceURLHttp("LocalTransportProxy"), null, "WSO2");

        String symbol = response.getFirstElement().getFirstChildWithName
                (new QName("http://services.samples/xsd", "symbol"))
                .getText();

        log.info("Symbol is " + symbol);

        assertEquals(symbol, "WSO2", "Symbol mismatched.");
    }
}
