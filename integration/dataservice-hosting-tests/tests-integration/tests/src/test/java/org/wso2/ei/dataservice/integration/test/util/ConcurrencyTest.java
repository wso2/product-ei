/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.ei.dataservice.integration.test.util;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.test.utils.axis2client.AxisServiceClient;
import org.wso2.carbon.automation.test.utils.concurrency.test.exception.ConcurrencyTestFailedError;
import org.wso2.carbon.automation.test.utils.concurrency.test.exception.ExceptionHandler;

import java.util.LinkedList;
import java.util.Queue;

/*
This class was added til https://wso2.org/jira/browse/TA-1004 issue fix.
For temporary purpose.
 */
public class ConcurrencyTest {
    private static final Log log = LogFactory.getLog(ConcurrencyTest.class);
    private int concurrencyNumber;
    private int numberOfIterations;
    private Queue<OMElement> messageQueue;
    private Queue<AxisFault> errorQueue;

    public ConcurrencyTest(int threadGroup, int loopCount) {
        this.concurrencyNumber = threadGroup;
        this.numberOfIterations = loopCount;
        this.messageQueue = new LinkedList();
        this.errorQueue = new LinkedList();
    }

    public Queue<OMElement> getMessages() {
        return this.messageQueue;
    }

    public void clearQueue() {
        this.messageQueue.clear();
    }

    public Queue<AxisFault> getErrorMessages() {
        return this.errorQueue;
    }

    public void clearErrorQueue() {
        this.errorQueue.clear();
    }

    public void run(final String serviceEndPoint, final OMElement payload, final String operation)
            throws ConcurrencyTestFailedError, InterruptedException {
        log.info("Starting Concurrency test with " + this.concurrencyNumber + " Threads and " +
                 this.numberOfIterations + " loop count");
        this.clearQueue();
        this.clearErrorQueue();
        final ExceptionHandler handler = new ExceptionHandler();
        Thread[] clientThread = new Thread[this.concurrencyNumber];
        final AxisServiceClient serviceClient = new AxisServiceClient();

        int aliveCount;
        for (aliveCount = 0; aliveCount < this.concurrencyNumber; ++aliveCount) {
            clientThread[aliveCount] = new Thread(new Runnable() {
                public void run() {
                    for (int j = 0; j < ConcurrencyTest.this.numberOfIterations; ++j) {
                        try {
                            ConcurrencyTest.this.addToMessageQueue(serviceClient.sendReceive(payload.cloneOMElement(),
                                                                                             serviceEndPoint,
                                                                                             operation));
                        } catch (AxisFault var3) {
                            handler.setException(var3);
                            ConcurrencyTest.this.addToErrorQueue(var3);
                        }
                    }
                }
            });
            clientThread[aliveCount].setUncaughtExceptionHandler(handler);
        }
        for (aliveCount = 0; aliveCount < this.concurrencyNumber; ++aliveCount) {
            clientThread[aliveCount].start();
        }
        for (Thread thread : clientThread) {
            thread.join();
        }
        if (!handler.isTestPass()) {
            throw new ConcurrencyTestFailedError(handler.getFailCount() + " service invocation/s failed out of " +
                                                 this.concurrencyNumber * this.numberOfIterations +
                                                 " service invocations.\n" + "Concurrency Test Failed for Thread Group=" +
                                                 this.concurrencyNumber + " and loop count=" + this.numberOfIterations,
                                                 handler.getException());
        }
    }

    private synchronized void addToMessageQueue(OMElement response) {
        this.messageQueue.add(response);
    }

    private synchronized void addToErrorQueue(AxisFault fault) {
        this.errorQueue.add(fault);
    }
}


