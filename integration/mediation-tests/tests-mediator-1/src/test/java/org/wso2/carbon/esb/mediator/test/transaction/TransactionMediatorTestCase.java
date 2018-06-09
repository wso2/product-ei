/*
 * Copyright (c) 2017 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.mediator.test.transaction;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.net.URL;

/**
 * Test the actions of Transaction Mediator including new, commit, rollback, fault-if-no-tx, use-existing-or-new, etc.
 * <p>
 * Perform different transactions and test for final indexes of the database entries
 */
public class TransactionMediatorTestCase extends ESBIntegrationTest {
    private static final String API_URL = "http://localhost:8480/transaction-test/";
    private static final String INIT_CONTEXT = "init";
    private static final String NEW_CONTEXT = "new";
    private static final String COMMIT_CONTEXT = "commit";
    private static final String ROLLBACK_CONTEXT = "rollback";
    private static final String TEST_CONTEXT = "test";
    private static final String CLEANUP_CONTEXT = "cleanup";
    private static final String FAULT_NOTX_CONTEXT = "fault-if-no-tx";
    private static final String USE_EXISTING_NEW_CONTEXT = "use-existing-or-new";
    private static final String SUSPEND_RESUME_CONTEXT = "suspend-resume";
    private static final String SUSPEND_CONTEXT = "suspend";

    /**
     * Initialize database by creating necessary tables and entries for following tests
     *
     * @throws Exception
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        HttpRequestUtil.doPost(new URL(API_URL + INIT_CONTEXT), "");
    }

    /**
     * Test for creating a new transaction with new and commit actions
     *
     * @throws Exception
     */
    @Test(groups = "wso2.esb", description = "Test commit transaction")
    public void commitTransactionTest() throws Exception {

        String expectedOutput = "<response><table1/><table2>2</table2></response>";

        HttpRequestUtil.doPost(new URL(API_URL + COMMIT_CONTEXT), "");
        HttpResponse httpResponse = HttpRequestUtil.doPost(new URL(API_URL + TEST_CONTEXT + "?testEntry=Alice"), "");

        Assert.assertEquals(httpResponse.getData(), expectedOutput, "Commit transaction fails in transaction mediator");
    }

    /**
     * Test for transaction rollback
     *
     * @throws Exception
     */
    @Test(groups = "wso2.esb", description = "Test rollback transaction")
    public void rollbackTransactionTest() throws Exception {

        String expectedOutput = "<response><table1>2</table1><table2>1</table2></response>";

        HttpRequestUtil.doPost(new URL(API_URL + ROLLBACK_CONTEXT), "");
        HttpResponse httpResponse = HttpRequestUtil
                .doPost(new URL(API_URL + TEST_CONTEXT + "?testEntry=Bob"), "");

        Assert.assertEquals(httpResponse.getData(), expectedOutput,
                            "Rollback transaction fails in transaction mediator");
    }

    /**
     * Test for identifying no transaction with fault-if-no-tx action
     *
     * @throws Exception
     */
    @Test(groups = "wso2.esb", description = "Test fault-if-no-tx transaction")
    public void faultIfNoTxTransactionTest() throws Exception {

        String expectedOutput = "<response><message>No Transactions</message></response>";

        HttpResponse httpResponse = HttpRequestUtil.doPost(new URL(API_URL + FAULT_NOTX_CONTEXT), "");

        Assert.assertEquals(httpResponse.getData(), expectedOutput,
                            "Fault-if-no-tx transaction fails in transaction mediator");
    }

    /**
     * Test for use-existing-or-new for creating a new transaction
     *
     * @throws Exception
     */
    @Test(groups = "wso2.esb", description = "Test use-existing-or-new action for creating new transaction")
    public void createNewTransactionTest() throws Exception {

        String expectedOutput = "<response><table1>3</table1><table2>3</table2></response>";

        HttpRequestUtil.doPost(new URL(API_URL + USE_EXISTING_NEW_CONTEXT + "?testEntry=Anne&entryId=3"), "");
        HttpResponse httpResponse = HttpRequestUtil
                .doPost(new URL(API_URL + TEST_CONTEXT + "?testEntry=Anne"), "");

        Assert.assertEquals(httpResponse.getData(), expectedOutput,
                            "Use-existing-or-new action fails for creating new transaction in transaction mediator");
    }

    /**
     * Test for use-existing-or-new action for using an existing transaction
     *
     * @throws Exception
     */
    @Test(groups = "wso2.esb", description = "Test use-existing-or-new action for using an existing transaction")
    public void useExistingTransactionTest() throws Exception {

        String expectedOutputForNick = "<response><table1>4</table1><table2>4</table2></response>";
        String expectedOutputForJohn = "<response><table1>5</table1><table2>5</table2></response>";

        HttpRequestUtil.doPost(new URL(API_URL + USE_EXISTING_NEW_CONTEXT + "?testEntry=John&entryId=5"), "");
        HttpResponse httpResponseForNick = HttpRequestUtil
                .doPost(new URL(API_URL + TEST_CONTEXT + "?testEntry=Nick"), "");
        HttpResponse httpResponseForJohn = HttpRequestUtil
                .doPost(new URL(API_URL + TEST_CONTEXT + "?testEntry=John"), "");

        boolean satisfyEntryNick = httpResponseForNick.getData().equals(expectedOutputForNick);
        boolean satisfyEntryJohn = httpResponseForJohn.getData().equals(expectedOutputForJohn);

        Assert.assertTrue(satisfyEntryJohn & satisfyEntryNick,
                          "Use-existing-or-new action fails for using an existing transaction in transaction mediator");
    }

    /**
     * Test for new transaction creation without commit action
     *
     * @throws Exception
     */
    @Test(groups = "wso2.esb", description = "Test new action without commit transaction")
    public void newActionTest() throws Exception {

        String expectedOutput = "<response><table1/><table2/></response>";

        HttpRequestUtil.doPost(new URL(API_URL + NEW_CONTEXT), "");
        HttpResponse httpResponse = HttpRequestUtil.doPost(new URL(API_URL + TEST_CONTEXT + "?testEntry=Max"), "");

        Assert.assertEquals(httpResponse.getData(), expectedOutput,
                            "New action without commit transaction fails in transaction mediator");
    }

    /**
     * Test for suspend action of a transaction (suspend action has not been implemented though it is mentioned in WSO2
     * docs. https://docs.wso2.com/display/EI630/Transaction+Mediator)
     *
     * @throws Exception
     */
    @Test(enabled = false, groups = "wso2.esb", description = "Test suspend action without commit transaction")
    public void suspendActionTest() throws Exception {

        String expectedOutput = "<response><table1/><table2/></response>";

        HttpRequestUtil.doPost(new URL(API_URL + SUSPEND_CONTEXT), "");
        HttpResponse httpResponse = HttpRequestUtil.doPost(new URL(API_URL + TEST_CONTEXT + "?testEntry=Mike"), "");

        Assert.assertEquals(httpResponse.getData(), expectedOutput,
                            "Suspend action fails in transaction mediator");
    }

    /**
     * Test for suspend and resume actions of a transaction (suspend and resume actiona have not been implemented though
     * it is mentioned in WSO2 docs. https://docs.wso2.com/display/EI630/Transaction+Mediator)
     *
     * @throws Exception
     */
    @Test(enabled = false, groups = "wso2.esb", description = "Test suspend and resume actions  transaction")
    public void suspendResumeActionTest() throws Exception {

        String expectedSuspendActionOutput = "<response1><response><table1/><table2/></response></response1>";
        String expectedResumeActionOutput =
                "<response2><response><table1>8</table1><table2>8</table2></response></response2>";

        HttpResponse httpResponse = HttpRequestUtil.doPost(new URL(API_URL + SUSPEND_RESUME_CONTEXT), "");

        boolean satisfySuspendAction = httpResponse.getData().contains(expectedSuspendActionOutput);
        boolean satisfyResumeAction = httpResponse.getData().contains(expectedResumeActionOutput);

        Assert.assertEquals(satisfySuspendAction & satisfyResumeAction,
                            "Suspend and resume actions fail in transaction mediator");
    }

    /**
     * Remove tables from the database
     *
     * @throws Exception
     */
    @AfterClass(alwaysRun = true)
    public void cleanup() throws Exception {
        HttpRequestUtil.doPost(new URL(API_URL + CLEANUP_CONTEXT), "");
    }
}
