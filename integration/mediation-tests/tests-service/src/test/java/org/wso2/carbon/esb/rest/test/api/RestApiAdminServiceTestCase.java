/*
 *Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.esb.rest.test.api;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.rest.api.stub.RestApiAdminAPIException;
import org.wso2.carbon.rest.api.stub.types.carbon.APIData;
import org.wso2.carbon.rest.api.stub.types.carbon.ResourceData;
import org.wso2.esb.integration.common.clients.rest.api.RestApiAdminClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.rmi.RemoteException;

/**
 * Testcase for RestAPIAdminService
 */
public class RestApiAdminServiceTestCase extends ESBIntegrationTest {

    private RestApiAdminClient restAdminClient;
    private APIData api1, api2, api3;
    private static final String tenantAPIName = "TenantServiceAPI";
    private static final String tenantDomain = "carbon.super";
    private static final String sampleInSequence = "<inSequence>\n" + "<log level=\"custom\">\n"
            + " <property name=\"processing sequence\" value=\"Executing sequence\"/>\n" + " </log>\n"
            + " </inSequence>";

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        restAdminClient = new RestApiAdminClient(context.getContextUrls().getBackEndUrl(), getSessionCookie());
        api1 = new APIData();
        api2 = new APIData();
        api3 = new APIData();

        //  sample API resource
        ResourceData resource = new ResourceData();
        String[] methods = { "POST" };
        resource.setMethods(methods);
        resource.setUrlMapping("/usage");

        ResourceData resource2 = new ResourceData();
        String[] methods2 = { "GET" };
        resource2.setMethods(methods2);
        resource2.setInSeqXml(sampleInSequence);
        resource2.setUrlMapping("/usage");

        ResourceData[] resourceList = new ResourceData[] { resource };

        ResourceData[] resourceList2 = new ResourceData[] { resource2 };

        api1.setName("SampleServiceAPI");
        api1.setContext("/createApi");
        api1.setHost("localhost");
        api1.setPort(8480);
        api1.setResources(resourceList);

        api2 = new APIData();
        api2.setName("TenantServiceAPI");
        api2.setContext("/tenant");
        api2.setHost("localhost");
        api2.setPort(8480);
        api2.setResources(resourceList);

        api3.setName("SampleServiceAPI");
        api3.setContext("/createApi");
        api3.setHost("localhost");
        api3.setPort(8480);
        api3.setResources(resourceList2);

    }

    @Test(groups = { "wso2.esb" },
          description = "Test API creation service",
          priority = 1)
    public void testCreateAPI() throws Exception {
        boolean apiAdded = restAdminClient.addAPI(api1);
        Assert.assertEquals(apiAdded, true, "API was not created");
        verifyAPIExistence(api1.getName());
    }

    @Test(groups = { "wso2.esb" },
          description = "Test API creation service under given tenant",
          priority = 2)
    public void testCreateAPIForTenant() throws Exception {
        String tenantAPISource = restAdminClient.getAPISource(api2);
        boolean apiAdded = restAdminClient.addAPIFromTenant(tenantAPISource, tenantDomain);
        Assert.assertEquals(apiAdded, true, "API was not created for tenant");
        verifyAPIExistence(api2.getName());
    }

    @Test(groups = { "wso2.esb" },
          description = "Test API list service ",
          priority = 3)
    public void testGetAPI() throws Exception {
        APIData apiResult = restAdminClient.getAPIbyName(api1.getName());
        Assert.assertEquals(apiResult.getName(), api1.getName(), "API was not retrieved");

    }

    @Test(groups = { "wso2.esb" },
          description = "Test API list service of a given tenant",
          priority = 4)
    public void testGetAPIForTenant() throws Exception {
        APIData apiResult = restAdminClient.getAPIForTenantByName(tenantAPIName, tenantDomain);
        Assert.assertEquals(apiResult.getName(), tenantAPIName, "API for tenant was not retrieved");
    }

    @Test(groups = { "wso2.esb" },
          description = "Test API update service with string param",
          priority = 5)
    public void testUpdateAPIFromString() throws Exception {
        String updateData =
                "<api xmlns=\"http://ws.apache.org/ns/synapse\" name=\"SampleServiceAPI\" context=\"/createApi\">\n"
                        + "    <resource methods=\"POST PUT\" uri-template=\"/\"> </resource></api>";
        boolean apiUpdated = restAdminClient.updateAPIFromString(api1.getName(), updateData);
        APIData updatedAPI = restAdminClient.getAPIbyName(api1.getName());
        ResourceData[] updatedResourceData = updatedAPI.getResources();
        String updatedResource = restAdminClient.getAPIResource(updatedResourceData[0]);
        Assert.assertEquals(apiUpdated, true, "API was not updated");
        Assert.assertTrue(updatedResource.contains("POST PUT"), "API was not updated with new method");
    }

    @Test(groups = { "wso2.esb" },
          description = "Test API update service with API data param",
          priority = 6)
    public void testUpdateAPIFromAPIData() throws Exception {
        boolean apiUpdated = restAdminClient.updateAPIFromAPIData(api1.getName(), api3);
        String[] sequences = restAdminClient.getAPISequences();
        Assert.assertEquals(apiUpdated, true, "API was not updated");
        Assert.assertNotNull(sequences, "API does not contained updated sequence");
    }

    @Test(groups = { "wso2.esb" },
          description = "Test API update service for tenant",
          priority = 7)
    public void testUpdateAPIForTenant() throws Exception {
        String updateData =
                "<api xmlns=\"http://ws.apache.org/ns/synapse\" name=\"TenantServiceAPI\" context=\"/tenant\">\n"
                        + "    <resource methods=\"POST\" uri-template=\"/\"> </resource><resource methods=\"GET\" uri-template=\"/updateUser\"> </resource></api>";
        boolean apiUpdated = restAdminClient.updateAPIForTenant(tenantAPIName, updateData, "carbon.super");
        APIData updatedAPI = restAdminClient.getAPIbyName(tenantAPIName);
        Assert.assertEquals(apiUpdated, true, "API was not updated for tenant");
        Assert.assertEquals(updatedAPI.getResources().length, 2, "API is not updated with 2 resources");
    }

    @Test(groups = { "wso2.esb" },
          description = "Test  enabling statistics for non-existent API",
          priority = 8,
          expectedExceptions = RestApiAdminAPIException.class)
    public void testEnableStatisticsForInvalidAPI() throws Exception {
        restAdminClient.enableStatisticsForAPI("invalidAPI");
        Assert.fail("Expected exception not thrown for non-existent API");

    }

    @Test(groups = { "wso2.esb" },
          description = "Test  enabling tracing for non-existent API",
          priority = 9,
          expectedExceptions = RestApiAdminAPIException.class)
    public void testEnableTracingForInvalidAPI() throws Exception {
        restAdminClient.enableTracingForAPI("invalidAPI");
        Assert.fail("Expected exception not thrown for non-existent API");

    }

    @Test(groups = { "wso2.esb" },
          description = "Test API statistics enable operation",
          priority = 10)
    public void testEnableAPIStatistics() throws Exception {
        restAdminClient.enableStatisticsForAPI(tenantAPIName);
        APIData statEnabledAPI = getAPIFromList(tenantAPIName);
        Assert.assertEquals(statEnabledAPI.getStatisticsEnable(), true, "Statistics not enabled for API");

    }

    @Test(groups = { "wso2.esb" },
          description = "Test API statistics disable operation ",
          priority = 11)
    public void testDisabledAPIStatistics() throws Exception {
        restAdminClient.disableStatisticsForAPI(tenantAPIName);
        APIData statDisabledAPI = getAPIFromList(tenantAPIName);
        Assert.assertNotNull(statDisabledAPI, "Unable to get requested API");
        Assert.assertEquals(statDisabledAPI.getStatisticsEnable(), false, "Statistics not disabled for API");

    }

    @Test(groups = { "wso2.esb" },
          description = "Test API tracing enable operation",
          priority = 12)
    public void testEnableAPITracing() throws Exception {
        restAdminClient.enableTracingForAPI(tenantAPIName);
        APIData traceEnabledAPI = getAPIFromList(tenantAPIName);
        Assert.assertNotNull(traceEnabledAPI, "Unable to get requested API");
        Assert.assertEquals(traceEnabledAPI.getTracingEnable(), true, "Tracing not enabled for API");

    }

    @Test(groups = { "wso2.esb" },
          description = "Test API tracing disable operation",
          priority = 13)
    public void testDisableAPITracing() throws Exception {
        restAdminClient.disableTracingForAPI(tenantAPIName);
        APIData traceDisabledAPI = getAPIFromList(tenantAPIName);
        Assert.assertNotNull(traceDisabledAPI, "Unable to get requested API");
        Assert.assertEquals(traceDisabledAPI.getTracingEnable(), false, "Tracing not disabled for API");

    }

    @Test(groups = { "wso2.esb" },
          description = "Test API delete service ",
          priority = 14)
    public void testDeleteAPI() throws Exception {
        boolean apiResult = restAdminClient.deleteApi(api1.getName());
        Assert.assertEquals(apiResult, true, "API was not removed");

    }

    @Test(groups = { "wso2.esb" },
          description = "Test API delete service for tenant ",
          priority = 15)
    public void testDeleteAPIForTenant() throws Exception {
        boolean apiResult = restAdminClient.deleteApiForTenant(tenantAPIName, tenantDomain);
        Assert.assertEquals(apiResult, true, "API was not removed");

    }

    @Test(groups = { "wso2.esb" },
          description = "Test fault handling for empty API update ",
          priority = 16,
          expectedExceptions = RestApiAdminAPIException.class)
    public void testAPIUpdateWithEmptyAPIName() throws Exception {
        String emptyAPIName = "";
        restAdminClient.updateAPIFromAPIData(emptyAPIName, api3);
        Assert.fail("Expected exception not thrown for updating empty API");

    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        if (restAdminClient.getAPICount() > 0) {
            restAdminClient.deleteAllApis();
        }
        restAdminClient = null;
        super.cleanup();
    }

    private APIData getAPIFromList(String apiName)
            throws RestApiAdminAPIException, InterruptedException, RemoteException {
        int pageNumber = 1;
        int itemCount = (restAdminClient.getAPICount() - 1);
        APIData[] apiList = restAdminClient.getAPIList(pageNumber, itemCount);
        for (APIData api : apiList) {
            if (api.getName().equals(apiName)) {
                return api;
            }
        }

        return null;
    }

}
