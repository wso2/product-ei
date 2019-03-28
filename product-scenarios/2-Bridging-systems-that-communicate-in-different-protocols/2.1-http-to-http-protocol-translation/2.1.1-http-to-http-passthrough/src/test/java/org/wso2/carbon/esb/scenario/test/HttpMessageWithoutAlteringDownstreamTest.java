/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.scenario.test;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.esb.scenario.test.common.http.HTTPUtils;
import org.wso2.carbon.esb.scenario.test.common.http.HttpConstants;
import org.wso2.carbon.esb.scenario.test.common.ScenarioTestBase;

import static org.wso2.carbon.esb.scenario.test.common.ScenarioConstants.GET_QUOTE_REQUEST;

/**
 * Class with tests - Receive an HTTP request from a client and deliver it over HTTP message to the
 * downstream without altering the content
 */
public class HttpMessageWithoutAlteringDownstreamTest extends ScenarioTestBase {

    private final String header = "2_1_1_1";
    private final String expectedResponse = "getQuoteResponse";

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
    }

    @Test(description = "2.1.1.1")
    public void httpEndpointViaCallMediatorNonBlockingMode() throws Exception {
        String proxyServiceUrl = getProxyServiceURLHttp("2_1_1_1_Proxy_httpEndpointViaCallMediatorNonBlockingMode");
        HTTPUtils.invokeSoapActionAndCheckContains(proxyServiceUrl, GET_QUOTE_REQUEST, header, expectedResponse,
                                                   HttpConstants.HTTP_SC_SUCCESS, "urn:mediate",
                                                   "httpEndpointViaCallMediatorNonBlockingMode");
    }

    //TODO: Need to fix : https://github.com/wso2/product-ei/issues/3015
    @Test(description = "2.1.1.2", enabled = false)
    public void httpEndpointViaCallMediatorBlockingMode() throws
            Exception {
        String proxyServiceUrl = getProxyServiceURLHttp("2_1_1_2_Proxy_httpEndpointViaCallMediatorBlockingMode");
        HTTPUtils.invokeSoapActionAndCheckContains(proxyServiceUrl, GET_QUOTE_REQUEST, header, expectedResponse,
                                                   HttpConstants.HTTP_SC_SUCCESS, "urn:mediate",
                                                   "httpEndpointViaCallMediatorBlockingMode");
    }

    @Test(description = "2.1.1.3")
    public void httpEndpointViaSendMediatorTohttpEp() throws Exception {
        String proxyServiceUrl = getProxyServiceURLHttp("2_1_1_3_Proxy_httpEndpointViaSendMediatorTohttpEp");

        HTTPUtils.invokeSoapActionAndCheckContains(proxyServiceUrl, GET_QUOTE_REQUEST, header, expectedResponse,
                                                   HttpConstants.HTTP_SC_SUCCESS, "urn:mediate",
                                                   "httpEndpointViaSendMediatorTohttpEp");
    }

    @Test(description = "2.1.1.5")
    public void addressEndpointViaCallMediatorNonBlockingMode() throws Exception {
        String proxyServiceUrl = getProxyServiceURLHttp("2_1_1_5_Proxy_addressEndpointViaCallMediatorNonBlockingMode");

        HTTPUtils.invokeSoapActionAndCheckContains(proxyServiceUrl, GET_QUOTE_REQUEST, header, expectedResponse,
                                                   HttpConstants.HTTP_SC_SUCCESS, "urn:mediate",
                                                   "addressEndpointViaCallMediatorNonBlockingMode");
    }

    @Test(description = "2.1.1.7")
    public void addressEndpointViaSendMediator() throws Exception {
        String proxyServiceUrl = getProxyServiceURLHttp("2_1_1_7_Proxy_addressEndpointViaSendMediator");

        HTTPUtils.invokeSoapActionAndCheckContains(proxyServiceUrl, GET_QUOTE_REQUEST, header, expectedResponse,
                                                   HttpConstants.HTTP_SC_SUCCESS, "urn:mediate",
                                                   "addressEndpointViaSendMediator");
    }

}
