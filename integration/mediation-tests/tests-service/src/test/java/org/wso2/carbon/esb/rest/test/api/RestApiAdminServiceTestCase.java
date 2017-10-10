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
import org.wso2.carbon.rest.api.stub.types.carbon.APIData;
import org.wso2.carbon.rest.api.stub.types.carbon.ResourceData;
import org.wso2.esb.integration.common.clients.rest.api.RestApiAdminClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

/**
 * Testcase for RestAPIAdminService
 */
public class RestApiAdminServiceTestCase extends ESBIntegrationTest {

    private RestApiAdminClient restAdminClient;
    private APIData api1, api2;
    private static final String tenantAPIName = "TenantServiceAPI";
    private static final String tenantDomain = "carbon.super";

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        restAdminClient = new RestApiAdminClient(context.getContextUrls().getBackEndUrl(), getSessionCookie());
        api1 = new APIData();
        api2 = new APIData();
        //  sample API resource
        ResourceData resource = new ResourceData();
        String[] methods = { "POST" };
        resource.setMethods(methods);
        resource.setUrlMapping("/usage");
        ResourceData[] resourceList = new ResourceData[] { resource };

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

    }

    @Test(groups = { "wso2.esb" },
          description = "Test API creation service",
          priority = 1)
    public void testCreateAPI() throws Exception {
        boolean apiAdded = restAdminClient.addAPI(api1);
        Assert.assertEquals(apiAdded, true, "API was not created");

    }

    @Test(groups = { "wso2.esb" },
          description = "Test API creation service under given tenant",
          priority = 2)
    public void testCreateAPIForTenant() throws Exception {
        String tenantAPISource = restAdminClient.getAPISource(api2);
        boolean apiAdded = restAdminClient.addAPIFromTenant(tenantAPISource, tenantDomain);
        Assert.assertEquals(apiAdded, true, "API was not created for tenant");

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
          description = "Test API update service",
          priority = 5)
    public void testUpdateAPI() throws Exception {
        String updateData =
                "<api xmlns=\"http://ws.apache.org/ns/synapse\" name=\"SampleServiceAPI\" context=\"/createApi\">\n"
                        + "    <resource methods=\"POST PUT\" uri-template=\"/\"> </resource></api>";
        boolean apiUpdated = restAdminClient.updateAPIFromString(api1.getName(), updateData);
        Assert.assertEquals(apiUpdated, true, "API was not updated");
    }

    @Test(groups = { "wso2.esb" },
          description = "Test API update service for tenant",
          priority = 6)
    public void testUpdateAPIForTenant() throws Exception {
        String updateData =
                "<api xmlns=\"http://ws.apache.org/ns/synapse\" name=\"TenantServiceAPI\" context=\"/tenant\">\n"
                        + "    <resource methods=\"POST\" uri-template=\"/\"> </resource></api>";
        boolean apiUpdated = restAdminClient.updateAPIForTenant(tenantAPIName, updateData, "carbon.super");
        Assert.assertEquals(apiUpdated, true, "API was not updated for tenant");
    }

    @Test(groups = { "wso2.esb" },
          description = "Test API delete service ",
          priority = 7)
    public void testDeleteAPI() throws Exception {
        boolean apiResult = restAdminClient.deleteApi(api1.getName());
        Assert.assertEquals(apiResult, true, "API was not removed");

    }

    @Test(groups = { "wso2.esb" },
          description = "Test API delete service for tenant ",
          priority = 8)
    public void testDeleteAPIForTenant() throws Exception {
        boolean apiResult = restAdminClient.deleteApiForTenant(tenantAPIName, tenantDomain);
        Assert.assertEquals(apiResult, true, "API was not removed");

    }

    @AfterClass(groups = "wso2.esb")
    public void close() throws Exception {
        if (restAdminClient.getAPICount() > 0) {
            restAdminClient.deleteAllApis();
        }
        restAdminClient = null;
        super.cleanup();
    }

}
