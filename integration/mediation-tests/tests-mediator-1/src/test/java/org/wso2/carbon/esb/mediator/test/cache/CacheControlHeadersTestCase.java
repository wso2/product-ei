/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package org.wso2.carbon.esb.mediator.test.cache;

import org.apache.axiom.om.OMElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.xml.namespace.QName;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * This class is to verify that cache mediator honor cache-control headers when enableCacheControl, includeAgeHeader
 * is set to true in it's configuration
 */
public class CacheControlHeadersTestCase extends ESBIntegrationTest {

    private String messagePayload;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/cache/CacheControlHeaders.xml");
        messagePayload = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
                "xmlns:ser=\"http://services.samples\" xmlns:xsd=\"http://services.samples/xsd\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <ser:getQuote>\n" +
                "         <ser:request>\n" +
                "            <xsd:symbol>WSO2</xsd:symbol>\n" +
                "         </ser:request>\n" +
                "      </ser:getQuote>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
    }

    /**
     * This test case checks whether the  back-end (axis2Server) has been hit for each request as no-store header is
     * returned with the response.
     */
    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Testing with a large cache time out value for cache mediator")
    public void testNoStore() throws Exception {
        String apiName = "noStore";
        OMElement response;
        response = axis2Client.sendSimpleStockQuoteRequest(null, getApiInvocationURL(apiName), "");

        String changeValue = response.getFirstElement().getFirstChildWithName(new QName
                ("http://services.samples/xsd", "change")).getText();

        response = axis2Client.sendSimpleStockQuoteRequest(null, getApiInvocationURL(apiName), "");

        String newChangeValue = response.getFirstElement().getFirstChildWithName(new QName
                ("http://services.samples/xsd", "change")).getText();

        //The 'change' value should not be equal to the initial 'change' value as the response is not cached.
        assertNotEquals(changeValue, newChangeValue, "Cache mediator cached the response");
    }

    /**
     * This test case checks whether an Age header is returned with the response.
     *
     */
    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Testing with a large cache time out value for cache mediator")
    public void testAgeHeader() throws Exception {
        String apiName = "includeAge";

        Map<String, String> headers = new HashMap<>();

        headers.put("Content-Type", "text/xml;charset=UTF-8");
        headers.put("SOAPAction", "urn:getQuote");

        //will not be a cache hit
        HttpRequestUtil.doPost(new URL(getApiInvocationURL(apiName)),
                messagePayload, headers);

        //will be a cache hit
        HttpResponse response2 = HttpRequestUtil.doPost(new URL(getApiInvocationURL(apiName)),
                messagePayload, headers);

        assertNotNull(response2.getHeaders().get("Age"), "Age header is not included");
    }

    /**
     * This test case checks whether an Age header is returned with the response.
     *
     */
    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Testing with a large cache time out value for cache mediator")
    public void testMaxAge() throws Exception {
        String apiName = "maxAge";
        OMElement response;
        response = axis2Client.sendSimpleStockQuoteRequest(null, getApiInvocationURL(apiName), "");

        String changeValue = response.getFirstElement().getFirstChildWithName(new QName
                ("http://services.samples/xsd", "change")).getText();

        Thread.sleep(30000);
        response = axis2Client.sendSimpleStockQuoteRequest(null, getApiInvocationURL(apiName), "");

        String newChangeValue = response.getFirstElement().getFirstChildWithName(new QName
                ("http://services.samples/xsd", "change")).getText();

        Thread.sleep(35000);
        response = axis2Client.sendSimpleStockQuoteRequest(null, getApiInvocationURL(apiName), "");

        String newChangeValue1 = response.getFirstElement().getFirstChildWithName(new QName
                ("http://services.samples/xsd", "change")).getText();

        //newChangeValue1 should not be equal to initial value as the max-age is 60s. Since max-age is less than cache
        // expiry time, cache should be invalidate after 60s even though the cache expiry time is 120s.
        assertTrue(changeValue.equals(newChangeValue) && !changeValue.equals(newChangeValue1));
    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        super.cleanup();
    }
}
