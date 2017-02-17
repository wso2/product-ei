/*
*  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
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
package org.wso2.ei.dataservice.integration.test.util;

/**
 * Helper class to check the requests are finished
 */
public class LockHolder {
    /**
     * static variable to hold the singleton instance
     */
    private static LockHolder lockHolder;

    /**
     * variable to hold the limit of requests
     */
    private int requestCountLimit;

    /**
     * temp variable to hold current request count
     */
    private int requests;

    /**
     * fail safe time in seconds
     */
    private int failSafeTime;

    /**
     * private constructor
     */
    private LockHolder() {

    }

    /**
     * private constructor with limit parameter and fail safe parameter
     *
     * @param requestCountLimit
     * @param failSafeTime
     */
    private LockHolder(int requestCountLimit, int failSafeTime) {
        this.requestCountLimit = requestCountLimit;
        this.failSafeTime = failSafeTime;
        this.requests = 0;
    }

    /**
     * return the singleton instance. throw exception if it is not initialised yet.
     *
     * @return lockHolder
     */
    public static synchronized LockHolder getInstance() throws Exception {
        if (lockHolder == null) {
            throw new Exception("Lock holder is not initialised yet. initialise it with getinstance(int request" +
                    "CountLimit) method first and then invoke this");
        }
        return lockHolder;
    }

    /**
     * return singleton instance. it will instantiate with given request count and fail safe time if the
     * object is not initialised already
     *
     * @param requestCountLimit
     * @param failSafeTime
     * @return lockHolder
     */
    public static synchronized LockHolder getInstance(int requestCountLimit, int failSafeTime) {
        if (lockHolder == null) {
            lockHolder = new LockHolder(requestCountLimit, failSafeTime);
        }
        return lockHolder;
    }

    /**
     * method to update current request count when receiving response from requests
     * this will release the lock as well when the request count reach the limit
     */
    public synchronized void updateRequests() {
        requests++;
    }

    /**
     * method used to wait for request count to reach the limit
     */
    public int waitForComplete() {
        for (int i = 0; i < failSafeTime; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("Error in Thread sleep " + e.getMessage());
            }
            if (requests >= requestCountLimit) {
                break;
            }
        }
        return requests;
    }
}
