/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.scenario.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.awaitility.Awaitility;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.esb.scenario.test.common.ScenarioConstants;
import org.wso2.carbon.esb.scenario.test.common.ScenarioTestBase;
import org.wso2.carbon.esb.scenario.test.common.StringUtil;
import org.wso2.carbon.esb.scenario.test.common.ftp.FTPClientWrapper;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * This test class test reading a file from an FTP location
 */
public class ReadFileFromFTPLocationTest extends ScenarioTestBase {

    private static final Log log = LogFactory.getLog(ReadFileFromFTPLocationTest.class);
    private static FTPClientWrapper ftpClient;
    private static int port = 21;

    @BeforeClass(description = "Test init")
    public void init() throws Exception {
        super.init();
    }

    /**
     * This test case test reading a file from a FTP location using VFS listener
     *
     * @throws IOException - if any REST API request fails
     */
    // TODO This testcase is disabled until infra is re-designed to enable FTP
    @Test(description = "10.1.1.1.1", enabled = false)
    public void testReadFileFromFTPLocationUsingVFSListener() throws IOException {
        String messageID = "10.1.1.1.1";

        ftpClient = connectToFTP(port);

        ftpClient.makeDirectories("vfs/input");
        ftpClient.makeDirectories("vfs/output");
        ftpClient.makeDirectories("vfs/original");
        ftpClient.makeDirectories("vfs/failure");

        String quoteRequestDirUrl = getVfsSourceDir("vfs/source_files/requestQuote.xml");
        ftpClient.ftpFileUpload(quoteRequestDirUrl, "/vfs/input/requestQuote.xml");

        Awaitility.await()
                  .pollInterval(500, TimeUnit.MILLISECONDS)
                  .atMost(ScenarioConstants.FILE_WRITE_WAIT_TIME_MS, TimeUnit.MILLISECONDS)
                  .until(isFileExist("vfs/output/stockQuote.xml"));

        String stockQuoteResponse = ftpClient.readFile("vfs/output/stockQuote.xml");

        boolean regexMatchStockQuote = StringUtil.stockQuoteXMLRegexMatch(stockQuoteResponse, "IBM", "IBM Company");
        Assert.assertTrue(regexMatchStockQuote, "Expected response not received in " + messageID);
    }

    private boolean checkFileExist(String filePath) throws IOException {
        if (ftpClient.checkFileExists(filePath)) {
            String fileContent = ftpClient.readFile(filePath);
            ftpClient.connectToFtp();
            return !fileContent.isEmpty();
        } else {
            return false;
        }
    }

    private Callable<Boolean> isFileExist(final String filepath) {
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                log.info("Check file exist : " + filepath);
                return checkFileExist(filepath);
            }
        };
    }

    @AfterClass(description = "Server Cleanup", alwaysRun = true)
    public void cleanup() throws Exception {
        super.cleanup();
    }

}
