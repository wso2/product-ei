/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.ei.test.connector.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.wso2.ei.test.connector.soap.SoapConnectorTest;
import org.wso2.ei.test.utils.TestUtils;

/**
 * Tests for File connector.
 */
public class FileConnectorTest {

    private static final Logger log = LoggerFactory.getLogger(SoapConnectorTest.class);

    private boolean serverStarted;

    @BeforeTest
    public void startServer() {
        serverStarted = TestUtils.startServer("samples/file-connector/file-connector-sample.balx");
    }

    @Test
    public void testFileConnector() {
        if (!serverStarted) {
            Assert.fail("Error running the test, server not stated");
        }
    }

    @AfterTest
    public void stopServer() {
        if (!TestUtils.stopServer()) {
            log.error("Error Stopping the server");
        }
    }
}
