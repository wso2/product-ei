/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.scenario.test.common.elasticsearch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.awaitility.Awaitility;
import org.json.JSONObject;
import org.wso2.carbon.esb.scenario.test.common.ScenarioConstants;
import org.wso2.carbon.esb.scenario.test.common.http.HTTPUtils;
import org.wso2.carbon.esb.scenario.test.common.http.RESTClient;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * This ElasticSearchClient is used to search and acquire log entries from ELK stack
 */
public class ElasticSearchClient {

    private static final Log log = LogFactory.getLog(ElasticSearchClient.class);
    private String deploymentStackID;
    private String hostName;

    public ElasticSearchClient(String hostName, String deploymentStackName) {

        this.deploymentStackID = deploymentStackName;
        this.hostName = hostName;

    }

    /**
     * Function to search and acquire log entries published from wso2carbon log
     *
     * @param logSnippet log snippet need to match
     * @return found log hits
     * @throws IOException if an error occurs during the invocation
     */
    private JSONObject searchCarbonLogs(String logSnippet) throws IOException {

        String baseUrl = hostName + "/" + deploymentStackID + "-carbonlogs*/_search?q=";
        String queryParam = URLEncoder.encode("\"" + logSnippet + "\"", "UTF-8");
        String searchUrl = baseUrl + queryParam;

        log.info("searchURL:" + searchUrl);
        RESTClient restClient = new RESTClient();
        HttpResponse response = restClient.doGet(searchUrl);

        JSONObject resp = new JSONObject(HTTPUtils.getResponsePayload(response));
        log.info("Response" + resp.toString());
        return resp.getJSONObject("hits");
    }

    /**
     * Function to assert for single log entry in ELK stack
     *
     * @param elkHostName Hostname of the elasticsearch server
     * @param infraStackName EI infrastructure stack name
     * @param logSnippet log entry snippet to search
     * @return true if single entry log is found
     * @throws IOException
     */
    public static boolean assertForSingleLogEntry(final String elkHostName,
                                           final String infraStackName, final String logSnippet) throws IOException {
        Awaitility.await()
                .pollInterval(ScenarioConstants.LOG_ASSERT_POLL_INTERVAL_MS, TimeUnit.MILLISECONDS)
                .atMost(ScenarioConstants.LOG_ASSERT_MAX_WAIT_TIME_MS, TimeUnit.MILLISECONDS)
                .pollDelay(ScenarioConstants.LOG_ASSERT_INITIAL_WAIT_TIME_MS, TimeUnit.MILLISECONDS)
                .until(ElasticSearchClient.isSingleLogAvailable(elkHostName, infraStackName, logSnippet));
        return true;
    }

    /**
     * Check for single log entry in logs.
     * 
     * @param elkHostName Hostname of the elasticsearch server
     * @param infraStackName EI infrastructure stack name
     * @param logSnippet log entry snippet to search
     * @return true if single log entry is found, false otherwise (no entry found or multiple logs found)
     */
    public static Callable<Boolean> isSingleLogAvailable(final String elkHostName,
                                                              final String infraStackName, final String logSnippet) {
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                log.info("Check log entry with : " + logSnippet);

                ElasticSearchClient elasticSearchClient = new ElasticSearchClient(elkHostName, infraStackName);
                JSONObject searchResult = elasticSearchClient.searchCarbonLogs(logSnippet);

                if (log.isDebugEnabled()) {
                    log.debug("Search result : " + searchResult);
                }
                return searchResult.getInt("total") == 1;
            }
        };
    }
}
