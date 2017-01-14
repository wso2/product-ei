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
package org.wso2.carbon.esb.nhttp.transport.test;


import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestConstant;

import java.io.File;
import java.net.URL;

import static java.io.File.separator;

/**
 * JIRA issue :     WSO2 Carbon / CARBON-11600
 * ESB fails to read responses coming from a backend service when interst ops queuing is enabled in nhttp transport
 */
public class ResponseAfterNttpEnabledTestCase extends ESBIntegrationTest {

    private String toUrl = null;
    private ServerConfigurationManager serverConfigurationManager;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {

        super.init();
        toUrl = getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE);
        serverConfigurationManager = new ServerConfigurationManager(new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN));
	    URL url = getClass().getResource(separator + "artifacts" + separator + "ESB" + separator
	                                     + "synapseconfig" + separator + "nhttp_transport" + separator
	                                     + "nhttp.properties");
	    File srcFile = new File(url.getPath());
	    serverConfigurationManager.applyConfiguration(srcFile);
	    super.init();
        loadESBConfigurationFromClasspath(separator + "artifacts" + separator + "ESB" + separator + "synapseconfig" +
                                          separator + "nhttp_transport" + separator + "response_nhttp_synapse.xml");
    }

    /**
     * To check the functionality of  ESB after enabling Nhttp transport
     * <p/>
     * Test Artifacts: /artifacts/ESB/synapseconfig/nhttp_transport/nhttp.properties
     *
     * @throws Exception
     */
    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.ALL})
    @Test(groups = "wso2.esb")
    public void testMessageMediationAfterEnablingNhttp() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), toUrl, "WSO2");

        Assert.assertTrue(response.toString().contains("WSO2 Company"), "'WSO2 Company' String " +
                                                                        "not found when nhttp enabled! ");
        Assert.assertTrue(response.toString().contains("getQuoteResponse"), "'getQuoteResponse'" +
                                                                            " String not found when nhttp enabled !");
    }

    /**
     * At the wind-up replace nhttp.properties file with previous
     *
     * @throws Exception
     */
    @AfterClass(alwaysRun = true)
    public void atEnd() throws Exception {
        try {
            super.cleanup();
        } finally {
            Thread.sleep(3000);
            serverConfigurationManager.restoreToLastConfiguration();
            serverConfigurationManager = null;
            toUrl = null;
        }
    }
}
