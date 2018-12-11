/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.ei.businessprocess.integration.tests.bpmn;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.wso2.ei.businessprocess.integration.common.clients.bpmn.WorkflowServiceClient;
import org.wso2.ei.businessprocess.integration.common.utils.BPSMasterTest;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.integration.common.admin.client.UserManagementClient;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.user.mgt.stub.UserAdminUserAdminException;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;

public class BPMNUserSubstitutionTestCase extends BPSMasterTest {
    private static final Log log = LogFactory.getLog(BPMNUserSubstitutionTestCase.class);

    private ServerConfigurationManager serverConfigurationManager;
    private UserManagementClient userManagementClient;
    private WorkflowServiceClient workflowServiceClient;

    public static final String USER_PW = "password";
    private static final String USER1 = "testUser1";
    private static final String USER2 = "testUser2";
    private static final String USER3 = "testUser3";
    private static final String USER4 = "testUser4";
    private static final String USER5 = "testUser5";
    private static final String USER6 = "testUser6";
    private static final String USER7 = "testUser7";
    private static final String SUBSTTUTER_ROLE = "subRole"; //has substitute permission
    private static final String NON_SUB_ROLE = "nonSubRole"; //has login permission only
    public static final String SUBSTITUTION_PERMISSION_PATH = "/permission/admin/manage/bpmn/substitute";
    public static final String LOGIN_PERMISSION_PATH = "/permission/admin/login";
    public static final String PROCESS_NAME = "UserTaskProcess";
    public static final String PROCESS_KEY = "userTaskProcess";
    public static final String SUBSTITUTE_URL = "runtime/substitutes";
    public static final String NEW_CONF_DIR = "wso2/business-process/conf";

    @BeforeTest(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        serverConfigurationManager = new ServerConfigurationManager(bpsServer);
        updateConfigFiles();
        super.init();
        loginLogoutClient.login();
        userManagementClient = new UserManagementClient(backEndUrl, sessionCookie);
        workflowServiceClient = new WorkflowServiceClient(backEndUrl, sessionCookie);
        serverConfigurationManager = new ServerConfigurationManager(bpsServer);
        log.info("Server setup completed with substitution configs enabled.");
        addRoles();
    }

    /**
     * Update content in activiti.xml & restart server
     *
     * @throws Exception
     */
    private void updateConfigFiles() throws Exception {
        final String artifactLocation = FrameworkPathUtil.getSystemResourceLocation()
                + BPMNTestConstants.DIR_ARTIFACTS + File.separator + BPMNTestConstants.DIR_CONFIG + File.separator;
        //Adding new config file for activiti.xml
        File activitiConfigNew = new File(artifactLocation + BPMNTestConstants.ACTIVITI_CONFIGURATION_FILE_NAME);
        File activitiConfigOriginal = new File(FrameworkPathUtil.getCarbonHome() + File.separator + NEW_CONF_DIR + File.separator
                + BPMNTestConstants.ACTIVITI_CONFIGURATION_FILE_NAME);
        serverConfigurationManager.applyConfiguration(activitiConfigNew, activitiConfigOriginal, true, true);

    }

    @Test(groups = {
            "wso2.bps.bpmn.substitution" }, description = "new substitute addition", priority = 1, singleThreaded = true)
    public void testAddSubstitute() throws Exception {
        addUser(USER1,new String[]{SUBSTTUTER_ROLE});
        addUser(USER2,new String[]{NON_SUB_ROLE});
        addUser(USER4, new String[]{NON_SUB_ROLE});
        uploadBPMNForTest(PROCESS_NAME);
        BPMNTestUtils.waitForProcessDeployment(workflowServiceClient, PROCESS_NAME, 0);
        for (int i=0; i < 10; i++) {
            startProcessInstance(USER2, "admin", "-1234"); // will create a task for user2
        }

        JSONObject existingTasks = findTasksWithGivenAssignee(USER2);
        //add substitute with no task list. user2 is unavailable for 1 hour from now
        HttpResponse response = addSubstituteUser(USER1, USER2, new DateTime(), new DateTime(System.currentTimeMillis() + 60*60*1000), null, USER1, USER1);

        Assert.assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.SC_CREATED, "New substitute creation test");

        //check task reassignment. It may take time to reassign. Keep trying 5 times if total > 0,
        // if still false assertion fails
        JSONObject obj = findTasksWithGivenAssignee(USER2);
        for (int i=0; i < 10; i++) {
            if(obj.getInt("total") > 0) {
                obj = findTasksWithGivenAssignee(USER2);
            } else {
                break;
            }
        }
        Assert.assertEquals(obj.getInt("total"), 0, "Task reassignment after substitute request");

        if (existingTasks.getInt("total") > 0) {
            JSONArray tasks = (JSONArray) existingTasks.get("data");
            for (int i = 0; i < existingTasks.getInt("total"); i++ ) {
                String id = ((JSONObject) tasks.get(i)).getString("id");
                String res = BPMNTestUtils.getRequest(backEndUrl + "runtime/tasks/" + id);
                String newAssignee = (new JSONObject(res)).getString("assignee");
                Assert.assertEquals(newAssignee, USER1, "Task reassignment to the substitute");
            }
        }

        //future task reassignment
        int user1TaskCount = findTasksWithGivenAssignee(USER1).getInt("total");
        startProcessInstance(USER2, "admin", "-1234"); // will create a task for user2
        //got to wait till task get assigned
        waitForTaskAssignments(user1TaskCount+1, USER1);
        Assert.assertTrue(user1TaskCount < findTasksWithGivenAssignee(USER1).getInt("total"), "Future task assignment to the substitute");
        obj = findTasksWithGivenAssignee(USER2);
        Assert.assertEquals(obj.getInt("total"), 0, "Future task substitution");

        startProcessInstance(USER4, "admin", "-1234");
        //add substitute to be activated in future, this should be activated after the activation time,
        // if not can activated immediately
        long waitingTime = 90 * 1000;
        addSubstituteUser(USER1, USER4, new DateTime(System.currentTimeMillis() + waitingTime),
                null , null, USER1, USER1);
        String getSubResponse = getSubstitute(USER4);
        JSONObject jsonResponse = new JSONObject(getSubResponse);
        Assert.assertTrue("null".equals(jsonResponse.getString("endTime")), "Add sub with endTime null");

        //task should be assigned to user1, user4 should have no tasks
        Awaitility.await()
                .pollInterval(50, TimeUnit.MILLISECONDS)
                .atMost(300000, TimeUnit.MILLISECONDS)
                .until(isTaskRemoveFromUser(USER4));

        JSONObject user4TasksJson = findTasksWithGivenAssignee(USER4);
        Assert.assertEquals(user4TasksJson.getInt("total"), 0, "Substitution activation by scheduler");
    }

    @Test(groups = {
            "wso2.bps.bpmn.substitution" }, description = "update substitute record", priority = 2, singleThreaded = true, dependsOnMethods = {"testAddSubstitute"})
    public void testUpdateSubstituteUser() throws IOException, JSONException {
        addUser(USER3, new String[]{SUBSTTUTER_ROLE} );
        //updating a active existing substitute
        int result = updateSubstitute(USER3, USER2, null, null);
        Assert.assertEquals(result, HttpStatus.SC_OK, "Update active substitution record");
        String getSubResult = BPMNTestUtils.getRequest(backEndUrl + SUBSTITUTE_URL + "/" +USER2);
        JSONObject getSubJson = new JSONObject(getSubResult);
        Assert.assertEquals(getSubJson.getString("substitute"), USER3, "Update active substitution record");
        int user1TaskCount = findTasksWithGivenAssignee(USER1).getInt("total");
        int user3taskCount = findTasksWithGivenAssignee(USER3).getInt("total");
        startProcessInstance(USER2, "admin", "-1234"); // will create a task for user2
        startProcessInstance(USER2, "admin", "-1234");
        waitForTaskAssignments(user3taskCount+2, USER3);
        Assert.assertEquals(findTasksWithGivenAssignee(USER1).getInt("total"), user1TaskCount, "Future task Substitution after a update");
        Assert.assertEquals(findTasksWithGivenAssignee(USER3).getInt("total"), user3taskCount + 2, "Future task Substitution after a update");


    }

    @Test(groups = {
            "wso2.bps.bpmn.substitution" }, description = "Add substitute record with transitivity true", priority = 3, singleThreaded = true, dependsOnMethods = {"testAddSubstitute"})
    public void testSubstituteTransitivity() throws IOException, JSONException {
        addUser(USER5,new String[]{SUBSTTUTER_ROLE});
        addUser(USER6,new String[]{SUBSTTUTER_ROLE});
        addUser(USER7,new String[]{SUBSTTUTER_ROLE});
        //user7 --> user6, user6 --> user5 ,
        int userTaskCount = findTasksWithGivenAssignee(USER5).getInt("total");
        addSubstituteUser(USER5, USER6, new DateTime(), new DateTime(System.currentTimeMillis() + (10*60*1000)), null, USER5, USER5);
        addSubstituteUser(USER6, USER7, new DateTime(), new DateTime(System.currentTimeMillis() + (10*60*1000)), null, USER5, USER5);
        startProcessInstance(USER7, "admin", "-1234");
        waitForTaskAssignments(userTaskCount + 1, USER5);
        //user4 should have new tasks
        Assert.assertTrue(findTasksWithGivenAssignee(USER5).getInt("total") > userTaskCount, "Substitution transitivity");
    }

    //keep checking the task count for 5 times
    private void waitForTaskAssignments(int expectedCount, String user) throws IOException, JSONException {
        for (int i=0; i < 10; i++) {
            if (expectedCount == findTasksWithGivenAssignee(user).getInt("total")) {
                break;
            }
        }
    }

    private JSONObject findTasksWithGivenAssignee(String assignee) throws JSONException, IOException {
        String payload = "{" + "\"assignee\":\"" + assignee + "\"" + "}";
        HttpResponse taskQueryResponse = BPMNTestUtils.postRequest(backEndUrl + "query/tasks", new JSONObject(payload));
        return new JSONObject(EntityUtils.toString(taskQueryResponse.getEntity()));
    }

    private void addUser(String user, String[] role) {
        try {
            userManagementClient.addUser(user, user, role, "test");
        } catch (RemoteException | UserAdminUserAdminException e) {
            log.error("Error adding new user for testing", e);
        }
    }

    private void addRoles() {
        try {
            userManagementClient.addRole(SUBSTTUTER_ROLE, null, new String[]{SUBSTITUTION_PERMISSION_PATH, LOGIN_PERMISSION_PATH});
            userManagementClient.addRole(NON_SUB_ROLE, null, new String[]{LOGIN_PERMISSION_PATH});
        } catch (RemoteException | UserAdminUserAdminException e) {
            log.error("Error adding a new role.", e);
        }
    }

    /**
     * Starting a UserTask process inatance. Sample request payload structure
     * {
     *  "processDefinitionKey":"userTaskProcess",
     *  "tenantId": "-1234",
     *  "variables": [
     *                  {
     *                      "name":"user",
     *                      "value":"admin"
     *                   },
     *                  {
     *                      "name":"role",
     *                      "value":"admin"
     *                  }
     *              ]
     * }
     * @param user
     * @param role
     */
    private void startProcessInstance(String user, String role, String tenantId) throws JSONException, IOException {
        String arrayString = "[{\"name\":\"user\", \"value\":\"" + user + "\"}, {\"name\":\"role\",\"value\":\"" + role + "\"}]";
        JSONArray varArray = new JSONArray(arrayString);

        JSONObject payload = new JSONObject();
        payload.put("variables", varArray);
        payload.put("tenantId", tenantId);
        payload.put("processDefinitionKey", PROCESS_KEY);

        BPMNTestUtils.postRequest(backEndUrl + "runtime/process-instances", payload);
    }

    private String getSubstitute(String assignee) throws IOException {
        return BPMNTestUtils.getRequest(backEndUrl + SUBSTITUTE_URL +"/" + assignee);
    }

    /**
     * Add substitute request. pass null for unwanted parameters.
     * {
     *  "substitute" : "vinod",
     *  "startTime" : "2010-01-01T12:00:00Z",
     *  "endTime" : "2017-01-01T12:00:00Z",
     *  "taskList" : ["1","2"]
     * }
     * @param substitute
     * @param assignee
     * @param start
     * @param end
     * @param taskList
     */
    private HttpResponse addSubstituteUser(String substitute, String assignee, DateTime start, DateTime end, List<String> taskList, String user, String password)
            throws JSONException, IOException {
        JSONObject payload = new JSONObject();
        if (assignee != null) {
            payload.put("assignee", assignee);
        }

        if (substitute != null) {
            payload.put("substitute", substitute);
        }

        DateTimeFormatter isoFormat = ISODateTimeFormat.dateTime();
        if (start != null) {
            payload.put("startTime", isoFormat.print(start));
        }

        if (end != null) {
            payload.put("endTime", isoFormat.print(end));
        }

        if (taskList != null && !taskList.isEmpty()) {
            JSONArray taskArray = new JSONArray();
            for (String id : taskList) {
                taskArray.put(id);
            }
            payload.put("taskList", taskArray);
        }
        HttpResponse response = BPMNTestUtils.doPost(backEndUrl + SUBSTITUTE_URL, payload, user, password);
        return response;
    }

    private int updateSubstitute(String substitute, String assignee, DateTime start, DateTime end)
            throws JSONException, IOException {
        JSONObject payload = new JSONObject();
        if (substitute != null) {
            payload.put("substitute", substitute);
        }

        DateTimeFormatter isoFormat = ISODateTimeFormat.dateTime();
        if (start != null) {
            payload.put("startTime", isoFormat.print(start));
        }

        if (end != null) {
            payload.put("endTime", isoFormat.print(end));
        }

        HttpResponse response = BPMNTestUtils.putRequest(backEndUrl + SUBSTITUTE_URL + "/" + assignee, payload);
        return response.getStatusLine().getStatusCode();
    }

    private Callable<Boolean> isTaskRemoveFromUser(final String assignee) {
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (findTasksWithGivenAssignee(assignee).getInt("total")==0) {
                    return true;
                } else {
                    return false;
                }
            }
        };
    }

    @AfterClass(alwaysRun = true)
    public void cleanServer () throws Exception {
        workflowServiceClient.undeploy(PROCESS_NAME);
    }
}
