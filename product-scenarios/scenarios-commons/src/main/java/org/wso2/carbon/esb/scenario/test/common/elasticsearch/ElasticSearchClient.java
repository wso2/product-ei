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
import org.json.JSONObject;
import org.wso2.carbon.esb.scenario.test.common.http.HTTPUtils;
import org.wso2.carbon.esb.scenario.test.common.http.RESTClient;

import java.io.IOException;
import java.util.concurrent.Callable;

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
     * @throws IOException
     */
    public JSONObject searchCarbonLogs(String logSnippet) throws IOException {

        String searchURL = "https://" + hostName + "/" + deploymentStackID + "-carbonlogs*/_search?q=" + logSnippet;

        RESTClient restClient = new RESTClient();
        HttpResponse response = restClient.doGet(searchURL);

        JSONObject resp = new JSONObject(HTTPUtils.getResponsePayload(response));
        return resp.getJSONObject("hits");
    }

    public static Callable<Boolean> isLogAvailable(final String elkHostName,
                                             final String infraStackName, final String logSnippet) {
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                log.info("Check log entry with : " + logSnippet);

                ElasticSearchClient elasticSearchClient = new ElasticSearchClient(elkHostName, infraStackName);
                JSONObject searchResult = elasticSearchClient.searchCarbonLogs(logSnippet);

                log.info(searchResult);

                int total = searchResult.getInt("total");

                return total == 1;

            }
        };
    }
}
