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
package org.wso2.carbon.esb.samples.test.proxy;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.xml.namespace.QName;

import java.io.File;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Create a proxy which invokes another proxy service and the communication should happen thorough
 * local transport
 */
public class Sample286TestCase extends ESBIntegrationTest {

    /*private ServerConfigurationManager serverManager;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        serverManager = new ServerConfigurationManager(context);
        serverManager.applyConfiguration(new File(getClass().getResource("/artifacts/ESB/proxyconfig" +
                                                                         "/proxy/enablelocaltransport/axis2.xml").getPath()));
        super.init();
        loadSampleESBConfiguration(268);
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.ALL})
    @Test(groups = "wso2.esb", description = "proxy service with local transport")
    public void proxyServiceWithLocalTransportTest() throws AxisFault {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest
                (getProxyServiceURLHttp("LocalTransportProxy"), null, "WSO2");
        assertNotNull(response, "Response is null");
        assertEquals(response.getFirstElement().getFirstChildWithName
                (new QName("http://services.samples/xsd", "symbol")).getText(),
                     "WSO2", "Tag does not match");
    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        try {
            cleanup();
        } finally {
            Thread.sleep(3000);
            serverManager.restoreToLastConfiguration();
            serverManager = null;
        }


    }*/
}
