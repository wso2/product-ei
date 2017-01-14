/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License, 
 * Version 2.0 (the "License"); you may not use this file except 
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.esb.passthru.transport.test;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.axis2client.AxisOperationClient;
import org.wso2.esb.integration.common.utils.servers.axis2.SampleAxis2Server;

/**
 * Test the SOAP version conversion to client version after a format switching inside sequences.
 */
public class ESBJAVA1994SOAPFormatSwitchingTestcase extends ESBIntegrationTest {
    private ServerConfigurationManager serverConfigurationManager;
    private SampleAxis2Server axis2Server1 = null;
    private AxisOperationClient operationClient;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        serverConfigurationManager = new ServerConfigurationManager(
                new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN));
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/passthru/transport/httpproxy/httpProxySwitchingSoap12.xml");

    }

    @Test(groups = "wso2.esb",
          description = "test to verify soap format of response converted properly "
                  + "when the soap format switching occurred in sequences.")
    public void testSendingSoap12AfterSoap11Request() throws Exception {
        operationClient = new AxisOperationClient();
        OMElement response = operationClient
                .send(getProxyServiceURLHttp("EchoTest"), null, createEchoRequestBody(), "urn:mediate");
        Assert.assertTrue(response.getNamespace().getNamespaceURI().toString()
                        .contains("http://schemas.xmlsoap.org/soap/envelope/"),
                "SOAP format conversion " + "after format switching failed");
    }

    /**
     * Create the echo request body.
     * @return Echo request OMElement
     */
    private OMElement createEchoRequestBody() {
        SOAPFactory fac = OMAbstractFactory.getSOAP11Factory();
        OMNamespace omNs = fac.createOMNamespace("http://echo.services.core.carbon.wso2.org", "ns");
        OMElement method = fac.createOMElement("echoInt", omNs);
        OMElement value1 = fac.createOMElement("in", omNs);

        value1.addChild(fac.createOMText(value1, "565"));
        method.addChild(value1);
        return method;
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
        if (operationClient != null) {
            operationClient.destroy();
        }
    }
}
