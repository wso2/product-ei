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
package org.wso2.mb.integration.common.clients.operations.utils;

/**
 * Enumeration for acknowledge modes in JMS
 */
public enum JMSAcknowledgeMode {
    /**
     * @see <a href="http://docs.oracle.com/javaee/7/api/javax/jms/Session.html#SESSION_TRANSACTED">SESSION_TRANSACTED</a>
     */
    SESSION_TRANSACTED(0),

    /**
     * @see <a href="http://docs.oracle.com/javaee/7/api/javax/jms/Session.html#AUTO_ACKNOWLEDGE">AUTO_ACKNOWLEDGE</a>
     */
    AUTO_ACKNOWLEDGE(1),

    /**
     * @see <a href="http://docs.oracle.com/javaee/7/api/javax/jms/Session.html#CLIENT_ACKNOWLEDGE">CLIENT_ACKNOWLEDGE</a>
     */
    CLIENT_ACKNOWLEDGE(2),

    /**
     * @see <a href="http://docs.oracle.com/javaee/7/api/javax/jms/Session.html#DUPS_OK_ACKNOWLEDGE">DUPS_OK_ACKNOWLEDGE</a>
     */
    DUPS_OK_ACKNOWLEDGE(3),

    /**
     * Per message acknowledgement.
     */
    PER_MESSAGE_ACKNOWLEDGE(259);

    private int type;

    /**
     * Initializes acknowledge mode
     * @param type Acknowledge mode as per JMS
     */
    JMSAcknowledgeMode(int type) {
        this.type = type;
    }

    /**
     * Gets acknowledge mode
     * @return The Acknowledge mode value
     */
    public int getType() {
        return type;
    }
}
