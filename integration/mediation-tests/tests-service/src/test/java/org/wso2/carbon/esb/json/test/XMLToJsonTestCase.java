/*
*Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.json.test;

import java.io.File;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.http.HttpResponse;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.extensions.servers.httpserver.SimpleHttpClient;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

public class XMLToJsonTestCase extends ESBIntegrationTest {

    private ServerConfigurationManager serverManager;
    private final String XML_PAYLOAD = "<xml><id_str>84315710834212866</id_str><entities><hashtags><text>wso2</text><indices>35</indices>"
        +"<indices>45</indices>"
        +"</hashtags>"
        +"</entities> "
        +"<text>Maybe he'll finally find his keys. #peterfalk</text>"
        +        "<user>"
        +"<id_str>819797</id_str>"
        +"<id>819797</id>"
        +"</user></xml>";    
    @BeforeClass(alwaysRun = true)
    protected void init() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/json/xml-to-json.xml");
        serverManager = new ServerConfigurationManager(context);
        serverManager.applyConfiguration(new File(FrameworkPathUtil.getSystemResourceLocation()+"artifacts"+File.separator+
                "ESB"+File.separator+ "json" + File.separator + "synapse.properties"));
        super.init();
    }
    @Test(groups = "wso2.esb", description = "XML to JSON Test")
    public void testXMLToJson() throws Exception {
        //AxisServiceClient client = new AxisServiceClient();
        //OMElement request = getPayload();;
        //OMElement response = client. sendReceive(request, contextUrls.getServiceUrl() + "/response", "application/xml");  
        String payload = "<a:test xmlns:a='testns1'><b:testb xmlns:b='testns2' a:attrb1='v1' b:attrb1='v2'/><c:testc xmlns:c='testns3' /></a:test>";
        SimpleHttpClient httpClient=new SimpleHttpClient();
        String url = getProxyServiceURLHttp("response");
        HttpResponse httpResponse = httpClient.doPost(url, null, payload, "application/xml");
        String responsePayload = httpClient.getResponsePayload(httpResponse);
        System.out.println(responsePayload);
        Assert.assertTrue(responsePayload.contains("@a_attrb1"));
    }
    private OMElement getPayload() {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNsa = fac.createOMNamespace("testns1", "a");
        OMNamespace omNsb = fac.createOMNamespace("testns2", "b");
        OMNamespace omNsc = fac.createOMNamespace("testns3", "c");
        OMElement payload = fac.createOMElement("test", omNsa);          
        OMElement p1 = fac.createOMElement("testb", omNsb);
        OMAttribute a1 = fac.createOMAttribute("attrb1", omNsa, "a");
        a1.setAttributeValue("v1");
        p1.addAttribute(a1);
        OMAttribute a2 = fac.createOMAttribute("attrb2", omNsb, "b");
        a2.setAttributeValue("v2");
        p1.addAttribute(a2);        
        OMElement p2 = fac.createOMElement("testc", omNsc);
        p1.addChild(p2);
        payload.addChild(p1);
        return payload;
    }
    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        try {
            super.cleanup();
            Thread.sleep(5000);
        } finally {
            serverManager.restartGracefully();
            serverManager.restoreToLastConfiguration();
            serverManager = null;
        }
    }
}