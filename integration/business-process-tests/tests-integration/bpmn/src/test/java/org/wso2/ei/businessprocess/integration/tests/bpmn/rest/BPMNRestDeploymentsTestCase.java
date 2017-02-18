/*
* Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
* WSO2 Inc. licenses this file to you under the Apache License,
* Version 2.0 (the "License"); you may not use this file except
* in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.wso2.ei.businessprocess.integration.tests.bpmn.rest;

import junit.framework.Assert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.wso2.ei.businessprocess.integration.common.clients.AuthenticateStubUtil;
import org.wso2.ei.businessprocess.integration.common.clients.bpmn.WorkflowServiceClient;
import org.wso2.ei.businessprocess.integration.common.utils.BPSMasterTest;
import org.wso2.ei.businessprocess.integration.tests.bpmn.BPMNTestUtils;
import org.wso2.carbon.bpmn.core.mgt.model.xsd.BPMNProcess;
import org.wso2.carbon.bpmn.stub.BPMNDeploymentServiceStub;

public class BPMNRestDeploymentsTestCase extends BPSMasterTest {

    private static final Log log = LogFactory.getLog(BPMNRestDeploymentsTestCase.class);
    private WorkflowServiceClient workflowServiceClient;
    String domainKey1 = "wso2.com";
    String userKey1 = "user1";
    private String deploymentUrl = "repository/deployments";

    @BeforeTest
    public void init() throws Exception {
        super.init();
        workflowServiceClient = new WorkflowServiceClient(backEndUrl, sessionCookie);
        loginLogoutClient.login();
    }

    @AfterTest
    public void removeArtifacts () throws Exception {
        workflowServiceClient.undeploy("oneTaskProcess");
        workflowServiceClient.undeploy("HelloApprove");
        workflowServiceClient.undeploy("VacationRequest");
    }

    /**
     * Test getting deployments list with GET repository/deployments.
     * Depends on deployments counts, need to be no previous deployments.
     * Required resources : oneTaskProcess.bar,HelloApprove.bar, VacationRequest.bar
     * @throws Exception
     */
    @Test(groups = {"wso2.bps.bpmn.rest"}, description = "get deployments", priority = 1, singleThreaded =
            true)
    public void testGetDeployments() throws Exception {

        int deploymentCount = 0;
        if (workflowServiceClient.getDeployments() != null) {
            deploymentCount = workflowServiceClient.getDeployments().length;
        }
        // Deploy artifacts and wait for deployment end
        uploadBPMNForTest("oneTaskProcess");
        BPMNTestUtils.waitForProcessDeployment(workflowServiceClient, "oneTaskProcess.bar",
                deploymentCount);
        uploadBPMNForTest("HelloApprove");
        BPMNTestUtils.waitForProcessDeployment(workflowServiceClient, "HelloApprove",
                workflowServiceClient.getDeployments().length);

        //HTTP get request
        String result = BPMNTestUtils.getRequest(backEndUrl + deploymentUrl);
        JSONObject jsonObject = new JSONObject(result);

        //check result count
        Assert.assertEquals("repository/deployments:total test", workflowServiceClient.getDeployments().length,
                jsonObject.getInt("total"));

        //test name filter
        result = BPMNTestUtils.getRequest(backEndUrl + deploymentUrl + "?name=" + "HelloApprove");
        jsonObject = new JSONObject(result);
        String deploymentName = ((JSONObject) ((JSONArray) jsonObject.get("data")).get(0)).getString("name");

        Assert.assertEquals("repository/deployments name query test", "HelloApprove", deploymentName);

        //TODO : this is not working in API
        //test nameLike filter
        result = BPMNTestUtils.getRequest(backEndUrl + deploymentUrl + "?nameLike=" + "Task");
        jsonObject = new JSONObject(result);
        //deploymentName = ((JSONObject)((JSONArray)jsonObject.get("data")).get(0)).getString("name");

        //Assert.assertEquals("repository/deployments nameLike query test", "1", jsonObject.getString("total"));
        //Assert.assertEquals("repository/deployments nameLike query test", "oneTaskProcess", deploymentName);

        //init new domain
        super.init(domainKey1, userKey1);
        loginLogoutClient.login();
        workflowServiceClient = new WorkflowServiceClient(backEndUrl, sessionCookie);
        uploadBPMNForTest("VacationRequest");
        BPMNTestUtils.waitForProcessDeployment(workflowServiceClient, "VacationRequest", 0);
        //test tenantId
        result = BPMNTestUtils.getRequest(backEndUrl + deploymentUrl + "?tenantId=" + "1");
        jsonObject = new JSONObject(result);
        String tenantId = ((JSONObject) ((JSONArray) jsonObject.get("data")).get(0)).getString("tenantId");
        deploymentName = ((JSONObject) ((JSONArray) jsonObject.get("data")).get(0)).getString("name");

        //new tenant id should be 1
        Assert.assertEquals("repository/deployments tenantId query test", workflowServiceClient.getDeployments().length,
                jsonObject.getInt("total"));
        Assert.assertEquals("repository/deployments tenantId query test", "1", tenantId);
        Assert.assertEquals("repository/deployments tenantId query test", "VacationRequest", deploymentName);

        init();
        loginLogoutClient.login();

        //test tenantIdLike
        //                result = BPMNTestUtils.getRequest(backEndUrl + deploymentUrl + "?tenantIdLike=34");
        //                jsonObject = new JSONObject(result);
        //                tenantId = ((JSONObject)((JSONArray)jsonObject.get("data")).get(0)).getString("tenantId");
        //
        //                Assert.assertEquals("repository/deployments tenantId query test", "2", jsonObject.getString("total"));
        //                Assert.assertEquals("repository/deployments tenantId query test", "-1234", tenantId);

        //test withoutTenantId
        result = BPMNTestUtils.getRequest(backEndUrl + deploymentUrl + "?withoutTenantId=true");
        jsonObject = new JSONObject(result);

        //should not return any, since we always set tenantId
        Assert.assertEquals("repository/deployments withoutTenantId query test", "0", jsonObject.getString("total"));

        // Check ordering by name asc
        result = BPMNTestUtils.getRequest(backEndUrl + deploymentUrl + "?sort=name&order=asc");
        jsonObject = new JSONObject(result);

        JSONArray array = ((JSONArray) jsonObject.get("data"));
        for (int i = 1; i < jsonObject.getInt("size"); i++) {
            int compare = ((JSONObject) array.get(i - 1)).getString("name")
                    .compareTo(((JSONObject) array.get(i)).getString("name"));
            Assert.assertTrue("repository/deployments sort=name,order=asc query test", compare < 0);
        }

        // Check ordering by name dec
        result = BPMNTestUtils.getRequest(backEndUrl + deploymentUrl + "?sort=name&order=desc");
        jsonObject = new JSONObject(result);

        array = ((JSONArray) jsonObject.get("data"));
        for (int i = 1; i < jsonObject.getInt("size"); i++) {
            int compare = ((JSONObject) array.get(i - 1)).getString("name")
                    .compareTo(((JSONObject) array.get(i)).getString("name"));
            Assert.assertTrue("repository/deployments sort=name,order=asc query test", compare > 0);
        }

        // Check paging
        result = BPMNTestUtils.getRequest(backEndUrl + deploymentUrl + "?sort=name&order=asc&start=1&size=2");
        jsonObject = new JSONObject(result);
        Assert.assertEquals("repository/deployments paging query test", "2", jsonObject.getString("size"));
    }

    /**
     * Test getting a single resource, deployed in a deployment.
     * GET repository/deployments/{deploymentId}/resources/{resourceId}
     */
    @Test(groups = {"wso2.bps.bpmn.rest"}, description = "get single deployment", priority = 1, singleThreaded =
            true)
    public void testGetSingleDeployment() throws Exception {

        uploadBPMNForTest("oneTaskProcess");
        BPMNTestUtils.waitForProcessDeployment(workflowServiceClient, "oneTaskProcess", 0);

        String deploymentId = null;
        //get deployments by id
        for (int i=0; i < workflowServiceClient.getDeployments().length; i++) {
            if (workflowServiceClient.getDeployments()[i].getDeploymentName().equals("oneTaskProcess")) {
                deploymentId = workflowServiceClient.getDeployments()[i].getDeploymentId();
            }
        }
        //BPMNDeployment deployment = workflowServiceClient.getDeployments()[0];
        String result = BPMNTestUtils.getRequest(backEndUrl + deploymentUrl + "/" + deploymentId);
        JSONObject jsonObject = new JSONObject(result);

        Assert.assertEquals("repository/deployments/{deploymentId} test", deploymentId,
                jsonObject.get("id"));

        //get deployments resource by id
        result = BPMNTestUtils
                .getRequest(backEndUrl + deploymentUrl + "/" + deploymentId + "/resources");
        jsonObject = new JSONObject(result);

        //only the processDefinition and diagram resources should be returned
        Assert.assertEquals("repository/deployments/{deploymentId}/resources test", 2,
                ((JSONArray) jsonObject.get("deploymentResourceResponseList")).length());
        Assert.assertEquals("repository/deployments/{deploymentId}/resources test", "processDefinition",
                ((JSONObject) ((JSONArray) jsonObject.get("deploymentResourceResponseList")).get(0)).getString("type"));

        //get resource by id
        result = BPMNTestUtils.getRequest(
                backEndUrl + deploymentUrl + "/" + deploymentId + "/resources/"
                        + "oneTaskProcess.bpmn20.xml");
        jsonObject = new JSONObject(result);

        //check the id of the result
        Assert.assertEquals("repository/deployments/{deploymentId}/resources/{resourceId} test",
                "oneTaskProcess.bpmn20.xml", jsonObject.getString("id"));

    }

    /**
     * Test getting process definitions.
     * GET repository/process-definitions
     */
    @Test(groups = {"wso2.bps.bpmn.rest"}, description = "get process definitions", priority = 1, singleThreaded =
            true)
    public void testGetProcessDefinitions() throws Exception {

        uploadBPMNForTest("oneTaskProcess");
        BPMNTestUtils.waitForProcessDeployment(workflowServiceClient, "oneTaskProcess", 0);

        BPMNProcess[] bpmnProcesses = workflowServiceClient.getProcesses();

        HttpResponse resultResponse = BPMNTestUtils.getRequestResponse(backEndUrl + "repository/process-definitions");

        BPMNDeploymentServiceStub stub = new BPMNDeploymentServiceStub(backEndUrl + "BPMNDeploymentService");
        AuthenticateStubUtil.authenticateStub(sessionCookie, stub);
        stub.getDeployedProcesses();
        Assert.assertEquals("repository/process-definitions test", 200, resultResponse.getStatusLine().getStatusCode());

        String result = BPMNTestUtils
                .getRequest(backEndUrl + "repository/process-definitions/" + bpmnProcesses[0].getProcessId());
        JSONObject jsonObject = new JSONObject(result);

        Assert.assertEquals("repository/process-definitions/{definitionId} test", bpmnProcesses[0].getProcessId(),
                jsonObject.getString("id"));

        result = BPMNTestUtils.getRequest(
                backEndUrl + "repository/process-definitions/" + bpmnProcesses[0].getProcessId() + "/resourcedata");
        //check whether some content returned, ideally xml content
        Assert.assertNotNull(result);
        //result = BPMNTestUtils.getRequest(backEndUrl + "repository/process-definitions/" + bpmnProcesses[0].getProcessId() + "/model");
        //jsonObject = new JSONObject(result);
        //repository/process-definitions/{processDefinitionId}/model not implemented
        String oneTaskProcessId = null;
        for (int i=0; i < bpmnProcesses.length; i++) {
            if (bpmnProcesses[i].getKey().equals("oneTaskProcess")) {
                oneTaskProcessId = bpmnProcesses[i].getProcessId();
            }
        }

        result = BPMNTestUtils.getRequest(
                backEndUrl + "repository/process-definitions/" + oneTaskProcessId + "/identitylinks");
        result = "{resultArray:" + result + "}";
        jsonObject = new JSONObject(result);

        //should return kermit user and admin group
        Assert.assertEquals("repository/process-definitions/{definitionId}/identitylinks test", 2,
                ((JSONArray) ((jsonObject).get("resultArray"))).length());
        Assert.assertTrue("repository/process-definitions/{definitionId}/identitylinks test",
                jsonObject.toString().contains("\"user\":\"kermit\""));
        Assert.assertTrue("repository/process-definitions/{definitionId}/identitylinks test",
                jsonObject.toString().contains("\"group\":\"admin\""));

        result = BPMNTestUtils.getRequest(
                backEndUrl + "repository/process-definitions/" + oneTaskProcessId + "/identitylinks"
                        + "/users/kermit");
        jsonObject = new JSONObject(result);

        Assert.assertEquals("repository/process-definitions/{definitionId}/identitylinks/{familly}/{identityId} test",
                "kermit", jsonObject.getString("user"));

        result = BPMNTestUtils.getRequest(
                backEndUrl + "repository/process-definitions/" + oneTaskProcessId + "/identitylinks"
                        + "/groups/admin");
        jsonObject = new JSONObject(result);

        Assert.assertEquals("repository/process-definitions/{definitionId}/identitylinks/{familly}/{identityId} test",
                "admin", jsonObject.getString("group"));
    }
}
