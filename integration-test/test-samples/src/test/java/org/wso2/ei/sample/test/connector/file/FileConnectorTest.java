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

package org.wso2.ei.sample.test.connector.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.ei.test.utils.TestUtils;

/**
 * Tests for File connector.
 */
public class FileConnectorTest {

    private static final Logger log = LoggerFactory.getLogger(FileConnectorTest.class);

    private boolean serverStarted;

    @BeforeClass
    public void startServer() {
        serverStarted = TestUtils.startServer("samples/file-connector/file-connector-sample.balx");
    }

    @Test
    public void testFileConnector() {
        if (!serverStarted) {
            //this assertion makes sure that the file connector is deployed properly to EI7 pack
            Assert.fail("Error running the test, server is not started");
        }
    }

    @AfterClass
    public void stopServer() {
        if (!TestUtils.stopServer()) {
            log.error("Error stopping the server");
        }
    }
}
