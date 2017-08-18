/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.rest.test.api;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;

/**
 * This test class written to verify the empty query param will correctly invoke the API resource.
 * There was an issue in the current implementation where resource defines like below and pass nothing
 * to last query param won't dispatch the request.
 *
 * /queries?latitude={+latitude}&longitude={+longitude}&floor={+floor}
 *
 * Invoking as /queries?latitude=10&longitude=20&floor=
 *
 */
public class LastQueryParamEmptyTestCase extends ESBIntegrationTest {

    /**
     * Deploying LastQueryParamEmptyTestAPI at the begin
     *
     * @throws Exception
     */
    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath(File.separator + "artifacts" + File.separator + "ESB"
                + File.separator + "synapseconfig" + File.separator + "rest"
                + File.separator + "LastQueryParamEmptyTestAPI.xml");
    }

    /**
     * This test method verify all query params return expected response for given resource template
     *
     * Resource - /pattern1?latitude={+latitude}&longitude={+longitude}&floor={+floor}
     *
     * @throws Exception
     */
    @Test(groups = { "wso2.esb" })
    public void testResourcePattern1WithParameters() throws Exception {
        HttpResponse response = HttpRequestUtil.sendGetRequest(
                getApiInvocationURL("last-query-param-empty/pattern1?latitude=10&longitude=20&floor=30"),
                null);
        Assert.assertNotNull(response);
        int responseCode = response.getResponseCode();
        String responseData = response.getData();
        Assert.assertEquals(responseCode, 200);
        Assert.assertEquals(responseData, "<m:checkQueryParam xmlns:m=\"http://services.samples/xsd\">" +
                "<m:latitude>10</m:latitude><m:longitude>20</m:longitude><m:floor>30</m:floor></m:checkQueryParam>");
    }

    /**
     * This test method verify last query param empty return expected response for given resource template
     *
     * Resource - /pattern1?latitude={+latitude}&longitude={+longitude}&floor={+floor}
     *
     * @throws Exception
     */
    @Test(groups = { "wso2.esb" })
    public void testResourcePattern1WithLastParameterEmpty() throws Exception {
        HttpResponse response = HttpRequestUtil.sendGetRequest(
                getApiInvocationURL("last-query-param-empty/pattern1?latitude=10&longitude=20&floor="),
                null);
        Assert.assertNotNull(response);
        int responseCode = response.getResponseCode();
        String responseData = response.getData();
        Assert.assertEquals(responseCode, 200);
        Assert.assertEquals(responseData, "<m:checkQueryParam xmlns:m=\"http://services.samples/xsd\">" +
                "<m:latitude>10</m:latitude><m:longitude>20</m:longitude><m:floor/></m:checkQueryParam>");
    }

    /**
     * This test method verify first query param empty return expected response for given resource template
     *
     * Resource - /pattern1?latitude={+latitude}&longitude={+longitude}&floor={+floor}
     *
     * @throws Exception
     */
    @Test(groups = { "wso2.esb" })
    public void testResourcePattern1WithFirstParameterEmpty() throws Exception {
        HttpResponse response = HttpRequestUtil.sendGetRequest(
                getApiInvocationURL("last-query-param-empty/pattern1?latitude=&longitude=20&floor=30"),
                null);
        Assert.assertNotNull(response);
        int responseCode = response.getResponseCode();
        String responseData = response.getData();
        Assert.assertEquals(responseCode, 200);
        Assert.assertEquals(responseData, "<m:checkQueryParam xmlns:m=\"http://services.samples/xsd\">" +
                "<m:latitude/><m:longitude>20</m:longitude><m:floor>30</m:floor></m:checkQueryParam>");
    }

    /**
     * This test method verify second query param empty return expected response for given resource template
     *
     * Resource - /pattern1?latitude={+latitude}&longitude={+longitude}&floor={+floor}
     *
     * @throws Exception
     */
    @Test(groups = { "wso2.esb" })
    public void testResourcePattern1WithSecondtParameterEmpty() throws Exception {
        HttpResponse response = HttpRequestUtil.sendGetRequest(
                getApiInvocationURL("last-query-param-empty/pattern1?latitude=10&longitude=&floor=30"),
                null);
        Assert.assertNotNull(response);
        int responseCode = response.getResponseCode();
        String responseData = response.getData();
        Assert.assertEquals(responseCode, 200);
        Assert.assertEquals(responseData, "<m:checkQueryParam xmlns:m=\"http://services.samples/xsd\">" +
                "<m:latitude>10</m:latitude><m:longitude/><m:floor>30</m:floor></m:checkQueryParam>");
    }

    /**
     * This test method verify all query params empty return expected response for given resource template
     *
     * Resource - /pattern1?latitude={+latitude}&longitude={+longitude}&floor={+floor}
     *
     * @throws Exception
     */
    @Test(groups = { "wso2.esb" })
    public void testResourcePattern1WithAllParametersEmpty() throws Exception {
        HttpResponse response = HttpRequestUtil.sendGetRequest(
                getApiInvocationURL("last-query-param-empty/pattern1?latitude=&longitude=&floor="),
                null);
        Assert.assertNotNull(response);
        int responseCode = response.getResponseCode();
        String responseData = response.getData();
        Assert.assertEquals(responseCode, 200);
        Assert.assertEquals(responseData, "<m:checkQueryParam xmlns:m=\"http://services.samples/xsd\">" +
                "<m:latitude/><m:longitude/><m:floor/></m:checkQueryParam>");
    }

    /**
     * This test method verify last query param empty and missing equal sign return expected response for given
     * resource template
     *
     * Resource - /pattern1?latitude={+latitude}&longitude={+longitude}&floor={+floor}
     *
     * @throws Exception
     */
    @Test(groups = { "wso2.esb" })
    public void testResourcePattern1WithLastParameterEmptyAndMissingEqualSign() throws Exception {
        HttpResponse response = HttpRequestUtil.sendGetRequest(
                getApiInvocationURL("last-query-param-empty/pattern1?latitude=10&longitude=20&floor"),
                null);
        Assert.assertNotNull(response);
        int responseCode = response.getResponseCode();
        String responseData = response.getData();
        Assert.assertEquals(responseCode, 404);
    }

    /**
     * This test method verify last query param empty return expected response for given resource template
     *
     * Resource - /pattern2*
     *
     * @throws Exception
     */
    @Test(groups = { "wso2.esb" })
    public void testResourcePattern2WithLastParameterEmpty() throws Exception {
        HttpResponse response = HttpRequestUtil.sendGetRequest(
                getApiInvocationURL("last-query-param-empty/pattern2?latitude=10&longitude=20&floor="),
                null);
        Assert.assertNotNull(response);
        int responseCode = response.getResponseCode();
        String responseData = response.getData();
        Assert.assertEquals(responseCode, 200);
        Assert.assertEquals(responseData, "<m:checkQueryParam xmlns:m=\"http://services.samples/xsd\">" +
                "<m:latitude>10</m:latitude><m:longitude>20</m:longitude><m:floor/></m:checkQueryParam>");
    }

    /**
     * This test method verify last query param empty and missing equal sign return expected response for given
     * resource template
     *
     * Resource - /pattern2*
     *
     * @throws Exception
     */
    @Test(groups = { "wso2.esb" })
    public void testResourcePattern2WithLastParameterEmptyAndMissingEqualSign() throws Exception {
        HttpResponse response = HttpRequestUtil.sendGetRequest(
                getApiInvocationURL("last-query-param-empty/pattern2?latitude=10&longitude=20&floor"),
                null);
        Assert.assertNotNull(response);
        int responseCode = response.getResponseCode();
        String responseData = response.getData();
        Assert.assertEquals(responseCode, 200);
        Assert.assertEquals(responseData, "<m:checkQueryParam xmlns:m=\"http://services.samples/xsd\">" +
                "<m:latitude>10</m:latitude><m:longitude>20</m:longitude><m:floor/></m:checkQueryParam>");
    }

    /**
     * This test method verify last query param empty return expected response for given resource template
     *
     * Resource - /pattern3/*
     *
     * @throws Exception
     */
    @Test(groups = { "wso2.esb" })
    public void testResourcePattern3WithLastParameterEmpty() throws Exception {
        HttpResponse response = HttpRequestUtil.sendGetRequest(
                getApiInvocationURL("last-query-param-empty/pattern3/sample?latitude=10&longitude=20&floor="),
                null);
        Assert.assertNotNull(response);
        int responseCode = response.getResponseCode();
        String responseData = response.getData();
        Assert.assertEquals(responseCode, 200);
        Assert.assertEquals(responseData, "<m:checkQueryParam xmlns:m=\"http://services.samples/xsd\">" +
                "<m:latitude>10</m:latitude><m:longitude>20</m:longitude><m:floor/></m:checkQueryParam>");
    }

    /**
     * This test method verify last query param empty and missing equal sign return expected response for given
     * resource template
     *
     * Resource - /pattern3/*
     *
     * @throws Exception
     */
    @Test(groups = { "wso2.esb" })
    public void testResourcePattern3WithLastParameterEmptyAndMissingEqualSign() throws Exception {
        HttpResponse response = HttpRequestUtil.sendGetRequest(
                getApiInvocationURL("last-query-param-empty/pattern3/sample?latitude=10&longitude=20&floor"),
                null);
        Assert.assertNotNull(response);
        int responseCode = response.getResponseCode();
        String responseData = response.getData();
        Assert.assertEquals(responseCode, 200);
        Assert.assertEquals(responseData, "<m:checkQueryParam xmlns:m=\"http://services.samples/xsd\">" +
                "<m:latitude>10</m:latitude><m:longitude>20</m:longitude><m:floor/></m:checkQueryParam>");
    }

    /**
     * Tear down the deployed artifacts
     *
     * @throws Exception
     */
    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}
