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

package org.wso2.ei.mb.test.client;


import org.testng.log4testng.Logger;
import org.wso2.ei.mb.test.utils.JMSAcknowledgeMode;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * Queue message listener for receive messages.
 */
public class QueueMessageListener implements MessageListener {


    /**
     * The logger used in logging information, warnings, errors and etc.
     */
    private static Logger log = Logger.getLogger(QueueMessageListener.class);

    /**
     * Default wait delay for message listener.
     */
    private int delay = 0;

    /**
     * Acknoledgement mode
     */
    private JMSAcknowledgeMode acknowledgeMode = JMSAcknowledgeMode.AUTO_ACKNOWLEDGE;

    /**
     * current message count.
     */
    private int currentMessageCount = 0;

    /**
     * Queue message listener for receive messages.
     *
     * @param delay wait delay for message listener.
     */
    public QueueMessageListener(int delay, JMSAcknowledgeMode acknowledgeMode) {
        this.delay = delay;
        this.acknowledgeMode = acknowledgeMode;
    }

    /**
     * On message received to the message listener.
     *
     * @param message Passes a message to the listener.
     */
    public void onMessage(Message message) {
        TextMessage receivedMessage = (TextMessage) message;

        try {
            currentMessageCount++;

            if ((JMSAcknowledgeMode.CLIENT_ACKNOWLEDGE.getType() == acknowledgeMode.getType())
                    && (getMessageCount() % 10 == 0)) {
                receivedMessage.acknowledge();
            }

            if (delay != 0) {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    log.error("Error occurred while waiting for messages", e);
                }
            }
        } catch (JMSException e) {
            log.error("Error occurred while reading message.", e);
        }
    }

    /**
     * Get total message counts for queue receiver.
     *
     * @return currentMessageCount
     */
    public int getMessageCount() {
        return currentMessageCount;
    }
}
