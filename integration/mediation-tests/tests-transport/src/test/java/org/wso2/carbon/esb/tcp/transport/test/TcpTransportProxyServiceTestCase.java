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
package org.wso2.carbon.esb.tcp.transport.test;

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
import org.wso2.carbon.esb.tcp.transport.test.util.TcpClient;

import java.io.File;

public class TcpTransportProxyServiceTestCase extends ESBIntegrationTest {
    private ServerConfigurationManager serverConfigurationManager;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        //parsing the super admin context to restart the server
        serverConfigurationManager = new ServerConfigurationManager(new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN));
        serverConfigurationManager.applyConfiguration(new File(getESBResourceLocation() + File.separator
                                                               + "tcp" + File.separator + "transport" + File.separator + "axis2.xml"));
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/tcp/transport/tcpProxy.xml");

    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.esb", description = "Proxy service with tcp transport")
    public void tcpTransportProxy() throws Exception {
        TcpClient tcpClient = new TcpClient();
        OMElement response;
        String tcProxyUrl;
        if (isRunningOnStratos()) {
            tcProxyUrl = "tcp://localhost:8290/services/t/" + context.getContextTenant().getDomain() + "/tcpProxy/tcpProxy?contentType=application/soap+xml";
        } else {
            tcProxyUrl = "tcp://localhost:8290/services/tcpProxy/tcpProxy?contentType=application/soap+xml";
        }
        response = tcpClient.sendSimpleStockQuote12(tcProxyUrl, "TCPPROXY", tcpClient.CONTENT_TYPE_APPLICATIONS_SOAP_XML);
        Assert.assertTrue(response.toString().contains("TCPPROXY"), "Symbol not found in response message");

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
        serverConfigurationManager.restoreToLastConfiguration();
    }
}
