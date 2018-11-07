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

package org.wso2.mb.platform.common.utils;

/**
 * This class contains prepared statements for DataAccessUtil
 */
public class RDBMSConstants {

    // Message Store tables
    protected static final String METADATA_TABLE = "MB_METADATA";
    protected static final String QUEUES_TABLE = "MB_QUEUE_MAPPING";

    // Message Store table columns
    protected static final String MESSAGE_ID = "MESSAGE_ID";
    protected static final String QUEUE_ID = "QUEUE_ID";
    protected static final String QUEUE_NAME = "QUEUE_NAME";

    // Slot related tables
    protected static final String SLOT_TABLE = "MB_SLOT";

    //Slot table columns
    protected static final String SLOT_ID = "SLOT_ID";
    protected static final String STORAGE_QUEUE_NAME = "STORAGE_QUEUE_NAME";
    protected static final String SLOT_STATE = "SLOT_STATE";

    //Return variables
    protected static final String MSG_COUNT = "MSG_COUNT";
    protected static final String SLOT_COUNT = "SLOT_COUNT";

    /**
     * Prepared statement for getting queue id
     */
    protected static final String PS_GET_QUEUE_ID =
            "SELECT " + QUEUE_ID
                + " FROM " + QUEUES_TABLE
                + " WHERE " + QUEUE_NAME + "=?";

    /**
     * Prepared statement for getting message count for a given queue
     */
    protected static final String PS_GET_MESSAGE_COUNT_FOR_QUEUE =
            "SELECT COUNT(" + MESSAGE_ID + ") AS " + MSG_COUNT
                + " FROM " + METADATA_TABLE
                + " WHERE " + QUEUE_ID + "=?";

    /**
     * Prepared statement for getting slots for a given queue which are in assigned state
     */
    protected static final String PS_GET_ASSIGNED_SLOTS_FOR_QUEUE =
            " SELECT COUNT(" + SLOT_ID + ") AS " + SLOT_COUNT
                + " FROM " + SLOT_TABLE
                + " WHERE " + STORAGE_QUEUE_NAME + "=?"
                + " AND " + SLOT_STATE + "=2";

}
