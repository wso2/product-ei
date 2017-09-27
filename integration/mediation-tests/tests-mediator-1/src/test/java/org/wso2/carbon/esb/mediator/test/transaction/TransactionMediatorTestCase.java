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
 * Test the actions of Transaction Mediator including commit, rollback, etc.
 * <p>
 * Perform different transactions and test for final id of the database entries in two tables
 */
public class TransactionMediatorTestCase extends ESBIntegrationTest {
    private static final String API_URL = "http://localhost:8480/transaction-test/";
    private static final String INIT_CONTEXT = "init";
    private static final String COMMIT_CONTEXT = "commit";
    private static final String ROLLBACK_CONTEXT = "rollback";
    private static final String TEST_CONTEXT = "test";
    private static final String CLEANUP_CONTEXT = "cleanup";

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        HttpRequestUtil.doPost(new URL(API_URL + INIT_CONTEXT), "");
    }

    @Test(groups = "wso2.esb", description = "Test commit transaction")
    public void testCommitTransaction() throws Exception {

        String expectedOutput = "<response><table1/><table2>2</table2></response>";

        HttpRequestUtil.doPost(new URL(API_URL + COMMIT_CONTEXT), "");
        HttpResponse httpResponse = HttpRequestUtil.doPost(new URL(API_URL + TEST_CONTEXT + "?testEntry=Alice"), "");

        Assert.assertEquals(httpResponse.getData(), expectedOutput, "Commit transaction fails in transaction mediator");
    }

    @Test(groups = "wso2.esb", description = "Test rollback transaction")
    public void testRollbackTransaction() throws Exception {

        String expectedOutput = "<response><table1/><table2>1</table2></response>";

        HttpRequestUtil.doPost(new URL(API_URL + ROLLBACK_CONTEXT), "");
        HttpResponse httpResponse = HttpRequestUtil.doPost(new URL(API_URL + TEST_CONTEXT + "?testEntry=Bob"), "");

        Assert.assertEquals(httpResponse.getData(), expectedOutput,
                            "Rollback transaction fails in transaction mediator");
    }

    @AfterClass(alwaysRun = true)
    public void cleanup() throws Exception {
        HttpRequestUtil.doPost(new URL(API_URL + CLEANUP_CONTEXT), "");
    }
}
