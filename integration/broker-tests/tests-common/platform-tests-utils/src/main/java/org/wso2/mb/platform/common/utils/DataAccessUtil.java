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

import org.wso2.mb.platform.common.utils.exceptions.DataAccessUtilException;

import javax.xml.xpath.XPathExpressionException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class is used for testing database records directly.
 */
public class DataAccessUtil {

    /**
     * Get database connection
     * @return database connection
     * @throws SQLException
     * @throws XPathExpressionException
     * @throws ClassNotFoundException
     */
    private Connection getConnection() throws SQLException, XPathExpressionException,
            ClassNotFoundException {
        return RDBMSConnectionManager.getConnection();
    }

    /**
     * Get current number of messages in database for a given queue name.
     * @param queueName queue name
     * @return number of messages in database
     * @throws DataAccessUtilException
     */
    public long getMessageCountForQueue(String queueName) throws DataAccessUtilException{
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        long count = 0;
        try {
            connection = getConnection();
            long queueId = getQueueId(queueName);
            preparedStatement = connection.prepareStatement(RDBMSConstants.PS_GET_MESSAGE_COUNT_FOR_QUEUE);
            preparedStatement.setLong(1, queueId);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                count = resultSet.getLong(RDBMSConstants.MSG_COUNT);
            }
        } catch (Exception e) {
            throw new DataAccessUtilException("Failed to get message count for queue: " + queueName, e);
        }
        finally {
            close(resultSet, "getMessageCountForQueue");
            close(preparedStatement, "getMessageCountForQueue");
            close(connection, "getMessageCountForQueue");
        }
        return count;
    }

    /**
     * Get queue id for a given queue.
     * @param queueName queue name
     * @return queue id
     * @throws DataAccessUtilException
     */
    public long getQueueId(String queueName) throws DataAccessUtilException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        long count = 0;
        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(RDBMSConstants.PS_GET_QUEUE_ID);
            preparedStatement.setString(1, queueName);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                count = resultSet.getLong(RDBMSConstants.QUEUE_ID);
            }
        } catch (Exception e) {
            throw new DataAccessUtilException("Failed to get queue id for queue: " + queueName, e);
        } finally {
            close(resultSet, "getQueueId");
            close(preparedStatement, "getQueueId");
            close(connection, "getQueueId");
        }
        return count;
    }

    /**
     * Get current number of slots for a given queue which are in assigned state.
     * @param queueName queue name
     * @return number of slots
     * @throws DataAccessUtilException
     */
    public long getAssignedSlotCountForQueue(String queueName) throws DataAccessUtilException{
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        long count = 0;
        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(RDBMSConstants.PS_GET_ASSIGNED_SLOTS_FOR_QUEUE);
            preparedStatement.setString(1, queueName);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                count = resultSet.getLong(RDBMSConstants.SLOT_COUNT);
            }
        } catch (Exception e) {
            throw new DataAccessUtilException("Failed to get slot count for queue: " + queueName, e);
        }
        finally {
            close(resultSet, "getAssignedSlotCountForQueue");
            close(preparedStatement, "getAssignedSlotCountForQueue");
            close(connection, "getAssignedSlotCountForQueue");
        }
        return count;
    }

    /**
     * closes the result set resources
     *
     * @param resultSet ResultSet
     * @param task      task that was done by the closed result set.
     */
    protected void close(ResultSet resultSet, String task) throws DataAccessUtilException{
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                throw new DataAccessUtilException("Failed to close result set", e);
            }
        }
    }

    /**
     * close the prepared statement resource
     *
     * @param preparedStatement PreparedStatement
     * @param task              task that was done by the closed prepared statement.
     */
    protected void close(PreparedStatement preparedStatement, String task) throws DataAccessUtilException{
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                throw new DataAccessUtilException("Failed to close prepared statement", e);
            }
        }
    }

    /**
     * Closes the provided connection. on failure log the error;
     *
     * @param connection Connection
     * @param task       task that was done before closing
     */
    protected void close(Connection connection, String task) throws DataAccessUtilException{
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new DataAccessUtilException("Failed to close database connection", e);
            }
        }
    }

}
