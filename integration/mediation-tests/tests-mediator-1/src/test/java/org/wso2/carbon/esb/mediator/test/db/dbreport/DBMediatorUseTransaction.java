/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.esb.mediator.test.db.dbreport;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.net.MalformedURLException;
import java.net.URL;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class DBMediatorUseTransaction extends ESBIntegrationTest {

    private static final String API_URL = "dbReportMeditorUseTransactionTestAPI/";
    private static final String INIT_CONTEXT_1 = "init1";
    private static final String INIT_CONTEXT_2 = "init2";
    private static final String COMMIT_CONTEXT = "commit";
    private static final String TEST_CONTEXT_1 = "test1";
    private static final String TEST_CONTEXT_2 = "test2";
    private static final String CLEANUP_CONTEXT = "cleanup";

    /**
     * Initialize database by creating necessary tables and entries for following tests
     *
     * @throws Exception
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        HttpRequestUtil.doPost(new URL(getApiInvocationURL(API_URL + INIT_CONTEXT_1)), "");
        HttpRequestUtil.doPost(new URL(getApiInvocationURL(API_URL + INIT_CONTEXT_2)), "");
    }

    /**
     * Test with UseTransaction flag. The transaction mediator is used to handle transactional behaviour.
     * The commit operation is checked.
     * @throws Exception
     */
    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.esb", description = "Test UseTransaction option .Use in conjunction with Transaction mediator "
    )
    public void testDBmediatorSuccessCase() throws Exception {
        String ibmStringDB1, ibmStringDB2;
        ibmStringDB1 = getDatabaseResultsForDB1();
        assertTrue(ibmStringDB1.contains("IBM"), "Fault, invalid response");
        ibmStringDB2 = getDatabaseResultsForDB2();
        assertFalse(ibmStringDB2.contains("IBM"), "Fault, invalid response");
        HttpRequestUtil.doPost(new URL(getApiInvocationURL(API_URL + COMMIT_CONTEXT + "?nameEntry=IBM")), "");
        ibmStringDB1 = getDatabaseResultsForDB1();
        assertFalse(ibmStringDB1.contains("IBM"), "Fault, Record Not Deleted from Database1");
        ibmStringDB2 = getDatabaseResultsForDB2();
        assertTrue(ibmStringDB2.contains("IBM"), "Fault, Record Not Inserted to Database2");

    }

    /**
     * Test with UseTransaction flag. The transaction mediator is used to handle transactional behaviour.
     * The rollback operation is checked.
     * @throws Exception
     */
    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    /*JIRA issue: https://wso2.org/jira/browse/ESBJAVA-1553*/
    @Test(groups = "wso2.esb", description = "Test UseTransaction option ." +
            "Use in conjunction with Transaction mediator. Fail casse"
    )
    public void testDBmediatorFailCase() throws Exception {
        String sunStringtDB1, sunStringtDB2;
        sunStringtDB1 = getDatabaseResultsForDB1FailCase();
        assertTrue(sunStringtDB1.contains("SUN"), "Fault, invalid response");
        sunStringtDB2 = getDatabaseResultsForDB2FailCase();
        assertTrue(sunStringtDB2.contains("SUN"), "Fault, invalid response");
        HttpRequestUtil.doPost(new URL(getApiInvocationURL(API_URL + COMMIT_CONTEXT + "?nameEntry=SUN")), "");
        sunStringtDB1 = getDatabaseResultsForDB1FailCase();
        assertTrue(sunStringtDB1.contains("SUN"), "Fault, invalid response. Transaction is not rollbacked.");
        sunStringtDB2 = getDatabaseResultsForDB2FailCase();
        assertTrue(sunStringtDB2.contains("SUN"), "Fault, invalid response.Transaction is not rollbacked.");

    }

    private String getDatabaseResultsForDB1() throws MalformedURLException, AutomationFrameworkException {
        HttpResponse httpResponse = HttpRequestUtil.doPost(new URL(getApiInvocationURL(API_URL + TEST_CONTEXT_1
                + "?testEntry=IBM")), "");
        return httpResponse.getData();
    }

    private String getDatabaseResultsForDB1FailCase() throws MalformedURLException, AutomationFrameworkException {
        HttpResponse httpResponse = HttpRequestUtil.doPost(new URL(getApiInvocationURL(API_URL + TEST_CONTEXT_1
                + "?testEntry=SUN")), "");
        return httpResponse.getData();
    }

    private String getDatabaseResultsForDB2FailCase() throws MalformedURLException, AutomationFrameworkException {
        HttpResponse httpResponse = HttpRequestUtil.doPost(new URL(getApiInvocationURL(API_URL + TEST_CONTEXT_2
                + "?testEntry=SUN")), "");
        return httpResponse.getData();
    }

    private String getDatabaseResultsForDB2() throws MalformedURLException, AutomationFrameworkException {
        HttpResponse httpResponse = HttpRequestUtil.doPost(new URL(getApiInvocationURL(API_URL + TEST_CONTEXT_2
                + "?testEntry=IBM")), "");
        return httpResponse.getData();
    }

    /**
     * Remove tables from the database
     *
     * @throws Exception
     */
    @AfterClass(alwaysRun = true)
    public void cleanup() throws Exception {
        HttpRequestUtil.doPost(new URL(getApiInvocationURL(API_URL + CLEANUP_CONTEXT)), "");
    }
}
