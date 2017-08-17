/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.ei.mb.test.utils;

import org.testng.log4testng.Logger;
import org.wso2.ei.mb.test.client.QueueReceiver;
import sun.misc.Signal;
import sun.misc.SignalHandler;

import javax.jms.JMSException;

/**
 * This class handles the signal when maximum amount of messages received in receiver side
 */
public class QueueSignalHandler implements SignalHandler {

    /**
     * The logger used in logging information, warnings, errors and etc.
     */
    private static Logger log = Logger.getLogger(QueueSignalHandler.class);

    /**
     * The queue receiver object to store the queue receiver which generates the signal
     */
    private QueueReceiver queueReceiver;

    /**
     * Constructor which takes single queue receiver object
     */
    public QueueSignalHandler(QueueReceiver queueReceiver) {
        this.queueReceiver = queueReceiver;
    }

    /**
     * Signal handling logic
     */
    @Override
    public void handle(Signal signal) {
        try {
            queueReceiver.stopReceiver();
        } catch (JMSException e) {
            log.error(" Couldn't close the receiver", e);
        }
    }
}
