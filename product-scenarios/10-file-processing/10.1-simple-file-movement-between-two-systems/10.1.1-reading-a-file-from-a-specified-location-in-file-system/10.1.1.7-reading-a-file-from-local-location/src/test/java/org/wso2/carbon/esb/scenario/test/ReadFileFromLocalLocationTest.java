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
import org.apache.http.HttpResponse;
import org.awaitility.Awaitility;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.esb.scenario.test.common.ScenarioConstants;
import org.wso2.carbon.esb.scenario.test.common.ScenarioTestBase;
import org.wso2.carbon.esb.scenario.test.common.StringUtil;
import org.wso2.carbon.esb.scenario.test.common.http.RESTClient;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static org.wso2.carbon.esb.scenario.test.common.http.HTTPUtils.getResponsePayload;

/**
 * This test class test reading a file from a specified location in file system
 */
public class ReadFileFromLocalLocationTest extends ScenarioTestBase {

    private static final Log log = LogFactory.getLog(ReadFileFromLocalLocationTest.class);
    private static String queryFilePath = "&filePath=";
    private static String querySourcePath = "&sourcePath=";
    private static String queryDestinationPath = "&destinationPath=";
    private static String queryFileName = "&fileName=";

    @BeforeClass(description = "Test init")
    public void init() throws Exception {
        super.init();
    }

    /**
     * This test case test reading a file from a local location using VFS listener
     *
     * @throws IOException - if any REST API request fails
     */
    @Test(description = "10.1.1.7.1", enabled = false)
    public void testReadFileFromLocalLocationUsingVFSListener() throws IOException {
        String messageID = "10.1.1.7.1";
        String createInputDirUrl = getApiInvocationURLHttp("fileOperation") + "/createFolder?" +
                                   queryFilePath + getLocalVfsLocation("vfs/input");

        String createOutputDirUrl = getApiInvocationURLHttp("fileOperation") + "/createFolder?" +
                                    queryFilePath + getLocalVfsLocation("vfs/output");

        String createOriginalDirUrl = getApiInvocationURLHttp("fileOperation") + "/createFolder?" +
                                      queryFilePath +  getLocalVfsLocation("vfs/original");

        String quoteRequestDirUrl = getVfsSourceDir("vfs/source_files");

        String copyQuoteRequestUrl = getApiInvocationURLHttp("fileOperation") + "/copyFile?" +
                                     querySourcePath + quoteRequestDirUrl +
                                     queryDestinationPath + getLocalVfsLocation("vfs/input") +
                                     queryFileName + "requestQuote.xml";

        String stockQuoteFileExistUrl = getApiInvocationURLHttp("fileOperation") + "/isFileExist?" +
                                        queryFilePath + getLocalVfsLocation("vfs/output") + "/stockQuote.xml";

        doPutRestClient(createInputDirUrl, null, "", "application/json", "Create input directory");
        doPutRestClient(createOutputDirUrl, null, "", "application/json", "Create output directory");
        doPutRestClient(createOriginalDirUrl, null, "", "application/json", "Create original directory");
        doPostRestClient(copyQuoteRequestUrl, "", "application/json", "Copy quote request");

        Awaitility.await()
                  .pollInterval(500, TimeUnit.MILLISECONDS)
                  .atMost(ScenarioConstants.FILE_WRITE_WAIT_TIME_MS, TimeUnit.MILLISECONDS)
                  .until(isFileExist(stockQuoteFileExistUrl));

        String readStockQuoteUrl = getApiInvocationURLHttp("fileOperation") + "/readFile?" +
                                   queryFilePath + getLocalVfsLocation("vfs/output") + "/stockQuote.xml";

        JSONObject stockQuoteResponse = doGetRestClientGetResponse(readStockQuoteUrl, null);
        JSONObject stockQuote = stockQuoteResponse.getJSONObject("Envelope").getJSONObject("Body")
                                                  .getJSONObject("getQuoteResponse").getJSONObject("return");

        boolean regexMatchStockQuote = StringUtil.stockQuoteJsonRegexMatch(stockQuote.toString(), "IBM", "IBM Company");
        Assert.assertTrue(regexMatchStockQuote, "Expected response not received in " + messageID);
    }

    private void doPutRestClient(String apiUrl, Map<String, String> headers, String payload, String contentType,
                                 String operation) throws IOException {
        RESTClient restClient = new RESTClient();
        HttpResponse httpResponse = restClient.doPut(apiUrl, headers, payload, contentType);
        Assert.assertEquals(httpResponse.getStatusLine().getStatusCode(),201, "Expected status code mismatch");
        JSONObject responsePayload = new JSONObject(getResponsePayload(httpResponse));
        responsePayload.getBoolean("success");
        Assert.assertTrue(responsePayload.getBoolean("success"), operation + " failed");
    }

    private void doPostRestClient(String apiUrl, String payload, String contentType,
                                  String operation) throws IOException {
        RESTClient restClient = new RESTClient();
        HttpResponse httpResponse = restClient.doPost(apiUrl, payload, contentType);
        Assert.assertEquals(httpResponse.getStatusLine().getStatusCode(),200, "Expected status code mismatch");
        JSONObject responsePayload = new JSONObject(getResponsePayload(httpResponse));
        responsePayload.getBoolean("success");
        Assert.assertTrue(responsePayload.getBoolean("success"), operation + " failed");
    }

    private JSONObject doGetRestClientGetResponse(String apiUrl, Map<String, String> headers) throws IOException {
        RESTClient restClient = new RESTClient();
        HttpResponse httpResponse = restClient.doGet(apiUrl, headers);
        String responsePayload = getResponsePayload(httpResponse);
        return new JSONObject(responsePayload);
    }

    private  boolean checkFileExist(String filePath) throws IOException{
        JSONObject responseJsonObj = doGetRestClientGetResponse(filePath, null);
        return responseJsonObj.getBoolean("fileExist");
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
}
