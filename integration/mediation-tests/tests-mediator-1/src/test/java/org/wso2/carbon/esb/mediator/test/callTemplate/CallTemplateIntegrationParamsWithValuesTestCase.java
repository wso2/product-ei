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

package org.wso2.carbon.esb.mediator.test.callTemplate;

import org.apache.axiom.om.OMElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

public class CallTemplateIntegrationParamsWithValuesTestCase extends ESBIntegrationTest {
    private LogViewerClient logViewer;
  //  private LoggingAdminClient logAdmin;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/call_template/synapse_param_with_values.xml");
    }

    @Test(groups = {"wso2.esb"}, description = "Call Template Mediator Sample Parameters with" +
                                               " values assigned test", enabled = false)
    public void testXSLTTransformationWithTemplates() throws IOException, XMLStreamException {
        OMElement response=axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp
                                                                           ("StockQuoteProxy"), null, "IBM");
       // logAdmin = new LoggingAdminClient(contextUrls.getBackEndUrl(),getSessionCookie());
        //TODO - Asserting the response from the log
        /*logViewer=new LogViewerClient(esbServer.getBackEndUrl(),esbServer.getSessionCookie());
        LogEvent[] getLogsDebug = logViewer.getLogs("PARAM", "LogMediator");*/
        /*Assert "RESPONSE PARAM VALUE" and "REQUEST PARAM VALUE" is in logs */
    }

    @AfterClass(alwaysRun = true)
    public void cleanUp() throws Exception {
        super.cleanup();
    }
}
