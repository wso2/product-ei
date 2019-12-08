/*
* Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.ei.analytics.elk.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

import org.wso2.ei.analytics.elk.publisher.ElasticStatisticsPublisher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * This thread dequeues data maps from the allMappingsQueue, converts them to json strings and
 * invokes publishing to Elasticsearch
 */
public class ElasticsearchPublisherThread extends Thread {

    private static final Log log = LogFactory.getLog(ElasticsearchPublisherThread.class);

    // To stop running
    private volatile boolean shutdownRequested = false;

    private RestHighLevelClient client;
    private int bulkSize;
    private long bulkTimeOut;
    private long bufferEmptySleep;
    private long noNodesSleep;

    // Whether nodes connected and can publish
    boolean isPublishing = true;

    /**
     * This adds the following configurable values to the thread.
     *
     * @param restClient       configured rest client object
     * @param bulkSize         maximum size of the bulk to be published at a time
     * @param bulkTimeOut      time out for bulk collecting from the buffer
     * @param bufferEmptySleep thread sleep time when the event buffer is empty
     * @param noNodesSleep     thread sleep time when the Elasticsearch cluster is down
     */
    public void init(RestHighLevelClient restClient, int bulkSize, long bulkTimeOut, long bufferEmptySleep,
                     long noNodesSleep) {
        this.client = restClient;
        this.bulkSize = bulkSize;
        this.bulkTimeOut = bulkTimeOut;
        this.bufferEmptySleep = bufferEmptySleep;
        this.noNodesSleep = noNodesSleep;
    }

    @Override
    public void run() {
        if (log.isDebugEnabled()) {
            log.debug("Elasticsearch publisher thread started.");
        }

        // While not shutdown
        while (!shutdownRequested) {
            // First check whether the event queue/buffer is empty
            if (ElasticStatisticsPublisher.getAllMappingsQueue().isEmpty()) {
                try {
                    // Sleep if the queue/buffer is empty (default 1000 millis)
                    Thread.sleep(bufferEmptySleep);
                } catch (InterruptedException e) {
                    log.warn("Publisher Thread interrupted", e);
                    Thread.currentThread().interrupt();
                    throw new RuntimeException();
                }
            } else {
                try {
                    if (isPublishing) {
                        // If nodes has connected before, check whether nodes are not connected now
                        if (!client.ping(RequestOptions.DEFAULT)) {
                            // Log only once
                            log.info("No available Elasticsearch nodes to connect. Waiting for nodes... ");
                            isPublishing = false;
                        }
                    } else {
                        // If nodes has not connected before, check whether node are connected now
                        if (client.ping(RequestOptions.DEFAULT)) {
                            // Log only once
                            log.info("Elasticsearch node connected");
                            isPublishing = true;
                        }
                    }
                } catch (IOException e) {
                    log.debug("Elasticsearch connection error.", e);
                }

                if (!isPublishing) {
                    try {
                        // Sleep if no nodes are available (default 5000 millis)
                        Thread.sleep(noNodesSleep);
                    } catch (InterruptedException e) {
                        log.warn("Publisher Thread interrupted", e);
                        Thread.currentThread().interrupt();
                        throw new RuntimeException();
                    }
                } else {
                    ObjectMapper objectMapper = new ObjectMapper();
                    ArrayList<String> jsonStringList = new ArrayList<>();

                    // Entering time to the below while loop, to count the time out
                    long startTime = System.currentTimeMillis();

                    // This while loop is collecting maps from the buffering queue until the maximum
                    // bulk size is reached.
                    while (bulkSize > jsonStringList.size()) {
                        // Dequeue Map from the queue
                        Map<String, Object> map = ElasticStatisticsPublisher.getAllMappingsQueue().poll();

                        // Polling from the queue gives null after the queue is empty.
                        if (map != null) {
                            try {
                                String jsonString = objectMapper.writeValueAsString(map);
                                jsonStringList.add(jsonString);

                                if (log.isDebugEnabled()) {
                                    log.debug("Added JSON String: " + jsonString);
                                }
                            } catch (JsonProcessingException e) {
                                log.error("Cannot convert to json", e);
                            }
                        }

                        /*
                        If the buffering queue has fewer events than the maximum size of the bulk, the loop will not
                        stop. So when the time out is reached collected bulk will get published.
                          */
                        if ((System.currentTimeMillis() - startTime) > bulkTimeOut) {
                            if (log.isDebugEnabled()) {
                                log.debug("Polling time-out exceeded. Publishing collected events.(<500)");
                            }
                            break;
                        }
                    }

                    // Publish the json string list
                    ElasticStatisticsPublisher.publish(jsonStringList, client);

                    if (log.isDebugEnabled()) {
                        log.debug("Published :" + jsonStringList.size() + " events");
                    }
                }
            }
        }
    }

    /**
     * Shutdown thread, stop running
     */
    public void shutdown() {
        if (log.isDebugEnabled()) {
            log.debug("Statistics reporter thread is being stopped");
        }
        shutdownRequested = true;
    }

    /**
     * @return boolean shutdownRequested
     */
    public boolean getShutdown() {
        return shutdownRequested;
    }
}