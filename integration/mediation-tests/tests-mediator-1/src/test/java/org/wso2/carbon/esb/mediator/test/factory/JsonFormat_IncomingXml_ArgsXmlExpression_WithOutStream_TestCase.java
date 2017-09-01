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
package org.wso2.carbon.esb.mediator.test.factory;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.http.HttpResponse;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.extensions.servers.httpserver.SimpleHttpClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.carbon.esb.mediator.test.payload.factory.util.RequestUtil;
import org.wso2.esb.integration.common.utils.ESBTestConstant;

import static org.testng.Assert.assertTrue;

public class JsonFormat_IncomingXml_ArgsXmlExpression_WithOutStream_TestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void uploadSynapseConfig() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/payload/factory/jsonFormat_XmlExpressiosns.xml");
    }


    @Test(groups = {"wso2.esb"}, description = "WithoutStream B&F, json format, xml evaluators, incoming xml, outgoing json ")
    public void transformPayloadByArgsValue() throws Exception {

        SimpleHttpClient httpClient=new SimpleHttpClient();
        String payload = "<xml><id_str>84315710834212866</id_str><entities><hashtags><text>wso2</text><indices>35</indices>"
        +"<indices>45</indices>"
        +"</hashtags>"
        +"</entities> "
        +"<text>Maybe he'll finally find his keys. #peterfalk</text>"
        +        "<user>"
        +"<id_str>819797</id_str>"
        +"<id>819797</id>"
        +"</user></xml>";

        String url="http://localhost:8480/services/Dummy";
        String contentType="application/xml";
        HttpResponse httpResponse = httpClient.doPost(url, null, payload, contentType);
        String responsePayload = httpClient.getResponsePayload(httpResponse);


        assertTrue(responsePayload.contains("wso2"), "Symbol wso2 not found in response message");

    }

    @AfterClass(alwaysRun = true)
    private void destroy() throws Exception {
        super.cleanup();
    }

}
