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

package org.wso2.carbon.integrator.core.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.endpoints.Endpoint;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Endpoint Holder class to endpoint caching
 */
public class EndpointHolder implements Runnable {
    private static final Log log = LogFactory.getLog(EndpointHolder.class);
    private static EndpointHolder endpointHolder = new EndpointHolder();
    private static ConcurrentHashMap<String, EndpointRef> endpointHashMap = new ConcurrentHashMap<>(2);

    private EndpointHolder() {
        //retry interval 2 hour
        long interval = 2;
        ScheduledExecutorService globalExecutorService = Executors.newSingleThreadScheduledExecutor();
        globalExecutorService.scheduleAtFixedRate(this, interval, interval, TimeUnit.HOURS);
    }

    private static class EndpointRef {
        private Endpoint endpoint;
        private long lastAccessTime;

        EndpointRef(Endpoint endpoint) {
            this.endpoint = endpoint;
        }

    }

    /**
     * Method to return the instance.
     *
     * @return singleton instance of the class
     */
    public static EndpointHolder getInstance() {
        return endpointHolder;
    }

    /**
     * Helper method to cleanup least frequently used endpoints from map time to time.
     */
    private void cleanupMap() {
        long currentTime = System.currentTimeMillis();
        for (String key : new ArrayList<>(endpointHashMap.keySet())) {
            EndpointRef endpoint = endpointHashMap.get(key);
            if (endpoint != null) {
                long expirationTime = 1000 * 60 * 30;
                if ((currentTime - endpoint.lastAccessTime) > expirationTime) {
                    endpointHashMap.remove(key);
                }
            }
        }
    }

    public Endpoint getEndpoint(String endpoint) {
        if (endpointHashMap.get(endpoint) != null) {
            endpointHashMap.get(endpoint).lastAccessTime = System.currentTimeMillis();
            return endpointHashMap.get(endpoint).endpoint;
        } else {
            return null;
        }
    }

    public void putEndpoint(String endpointKey, Endpoint endpoint) {
        endpointHashMap.put(endpointKey, new EndpointRef(endpoint));
    }

    public boolean containsEndpoint(String endpoint) {
        return endpointHashMap.containsKey(endpoint);
    }

    @Override
    public void run() {
        try {
            this.cleanupMap();
        } catch (Exception e) {
            log.warn("Error occurred while cleaning up Endpoint map, Error - " + e.getMessage(), e);
        }
    }
}
