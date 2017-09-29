/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.esb.mediator.test.script;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import static org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil.doPost;

/**
 * This test case verifies that, adding headers is supported by NashornJs.
 */
public class AddHeadersUsingNashornJsTestCase extends ESBIntegrationTest {

    private static final String ADD_HEADER_API = "addHeadersWithNashornJsAPI";
    @BeforeClass(alwaysRun = true)
    protected void init() throws Exception {
        super.init();
    }

    @Test(groups = {"wso2.esb"}, description = "Adding soap headers by NashornJS with String")
    public void testAddHeaderUsingString() throws Exception {

        Map<String, String> httpHeaders = new HashMap<>();
        httpHeaders.put("Content-Type", "application/xml");
        String payload = "<m:CheckPriceResponse xmlns:m=\"http://services.samples/xsd\">\n"
                + "<m:stringHead>STRING</m:stringHead>\n" + "    <m:domHead>DOM</m:domHead>\n"
                + "<m:omHead>OM</m:omHead>\n" + "</m:CheckPriceResponse>";
        HttpResponse response = doPost(new URL(getApiInvocationURL(ADD_HEADER_API)), payload,
                httpHeaders);
        Assert.assertTrue((response.getData().contains("STRING")), "Response does not contain the keyword "
                + "\"STRING\". Response: " + response.getData());
    }

    @Test(groups = {"wso2.esb"}, description = "Adding soap headers by NashornJS with DOMElement")
    public void testAddHeaderUsingDomElement() throws Exception {

        Map<String, String> httpHeaders = new HashMap<>();
        httpHeaders.put("Content-Type", "application/xml");
        String payload = "<m:CheckPriceResponse xmlns:m=\"http://services.samples/xsd\">\n"
                + "<m:stringHead>STRING</m:stringHead>\n" + "    <m:domHead>DOM</m:domHead>\n"
                + "<m:omHead>OM</m:omHead>\n" + "</m:CheckPriceResponse>";
        HttpResponse response = doPost(new URL(getApiInvocationURL(ADD_HEADER_API)), payload,
                httpHeaders);
        Assert.assertTrue((response.getData().contains("DOM")), "Response does not contain the keyword \"DOM\". "
                + "Response: " + response.getData());

    }

    @Test(groups = {"wso2.esb"}, description = "Adding soap headers by NashornJS with OMElement")
    public void testAddHeaderUsingOmElement() throws Exception {

        Map<String, String> httpHeaders = new HashMap<>();
        httpHeaders.put("Content-Type", "application/xml");
        String payload = "<m:CheckPriceResponse xmlns:m=\"http://services.samples/xsd\">\n"
                + "<m:stringHead>STRING</m:stringHead>\n" + "    <m:domHead>DOM</m:domHead>\n"
                + "<m:omHead>OM</m:omHead>\n" + "</m:CheckPriceResponse>";
        HttpResponse response = doPost(new URL(getApiInvocationURL(ADD_HEADER_API)), payload,
                httpHeaders);
        Assert.assertTrue((response.getData().contains("OM")), "Response does not contain the keyword \"OM\". "
                + "Response: " + response.getData());

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}
