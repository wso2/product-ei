/*
*Copyright (c) 2005-2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.esb.mediator.test.enrich;

import static org.testng.Assert.assertEquals;

import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axis2.AxisFault;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.axis2client.AxisOperationClient;


public class EnrichPreserveOrder extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/enrich_mediator/order_check.xml");
    }

    @Test(groups = {"wso2.esb"}, description = "Enrichment of response message")
    public void enrichMediatorTest() throws XMLStreamException, AxisFault {
       AxisOperationClient operationClient = new AxisOperationClient();
       OMElement response = null;
       try {
           response = operationClient.send(getProxyServiceURLHttp("EnrichOrderTest"), null,  getRequest(), "urn:mediate");
       } finally {
           operationClient.destroy();
       }
       String strResponse = response.toString();
       int tag1 = strResponse.indexOf("<price>50.00</price>");
       int tag2 = strResponse.indexOf("<comment>REF 10053</comment>");
       assertEquals(tag1 < tag2, true, "Tag order not preserved after enrich mediator is used.");
    }

    private OMElement getRequest() throws XMLStreamException {
		 StringBuilder sb = new StringBuilder();
		 sb.append("<soap:Envelope xmlns:soap='http://schemas.xmlsoap.org/soap/envelope/'>");
		 sb.append("<soap:Header/>");
		 sb.append("<soap:Body>");
		 sb.append("<orders>");
		 sb.append("<order>");
		 sb.append("<price>50.00</price>");
		 sb.append("<quantity>500</quantity>");
		 sb.append("<symbol>IBM</symbol>");
		 sb.append("<comment>REF 10053</comment>");
		 sb.append("</order>");
		 sb.append("</orders>");
		 sb.append("</soap:Body>");
		 sb.append("</soap:Envelope>");
   	 return AXIOMUtil.stringToOM(sb.toString());
   }

    @AfterClass(alwaysRun = true)
    public void closeTestArtifacts() throws Exception {
         super.cleanup();
    }

}