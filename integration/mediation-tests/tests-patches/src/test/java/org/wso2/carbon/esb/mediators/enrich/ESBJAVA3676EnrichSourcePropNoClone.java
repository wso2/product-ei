package org.wso2.carbon.esb.mediators.enrich;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.commons.lang3.CharEncoding;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

/**
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
public class ESBJAVA3676EnrichSourcePropNoClone extends ESBIntegrationTest {
    private static final String PROXY_NAME = "ESBJAVA3676";

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        String proxy = "<proxy xmlns=\"http://ws.apache.org/ns/synapse\"\n" +
                "       name=\"" + PROXY_NAME + "\"\n" +
                "       transports=\"https,http\"\n" +
                "       statistics=\"disable\"\n" +
                "       trace=\"disable\"\n" +
                "       startOnLoad=\"true\">\n" +
                "    <target>\n" +
                "        <inSequence>\n" +
                "            <property name=\"PAYLOAD\" expression=\"//payload/content\" type=\"OM\"/>\n" +
                "            <enrich>\n" +
                "                <source type=\"property\" clone=\"false\" property=\"PAYLOAD\"/>\n" +
                "                <target type=\"body\"/>\n" +
                "            </enrich>\n" +
                "            <respond/>\n" +
                "        </inSequence>\n" +
                "    </target>\n" +
                "    <description/>\n" +
                "</proxy>\n";

        OMElement omProxy = AXIOMUtil.stringToOM(proxy);
        addProxyService(omProxy);
        isProxyDeployed(PROXY_NAME);
    }

    @Test(groups = "wso2.esb", description = "Tests Enrich Source OM Property without clone")
    public void testEnrichSourceTypePropertyAndCloneFalse() throws Exception {

        String endpoint = getProxyServiceURLHttp(PROXY_NAME);

        String requestXml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <payload>\n" +
                "         <content>\n" +
                "            <abc>\n" +
                "               <def>123</def>\n" +
                "            </abc>\n" +
                "         </content>\n" +
                "      </payload>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        DefaultHttpClient httpclient = new DefaultHttpClient();

        HttpPost httpPost = new HttpPost(endpoint);
        httpPost.addHeader("SOAPAction", "\"urn:mediate\"");
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "text/xml;charset=UTF-8");
        HttpEntity stringEntity = new StringEntity(requestXml, CharEncoding.UTF_8);
        httpPost.setEntity(stringEntity);
        HttpResponse response = httpclient.execute(httpPost);
        HttpEntity resEntity = response.getEntity();

        BufferedReader rd = new BufferedReader(new InputStreamReader(resEntity.getContent()));
        String result = "";
        String line;
        while ((line = rd.readLine()) != null) {
            result += line;
        }
        Assert.assertTrue(!result.contains("payload"));
    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        super.cleanup();
    }
}
