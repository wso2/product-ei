/*
 * Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
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

package org.wso2.mb.integration.common.clients.operations.clients;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.wso2.carbon.andes.stub.AndesAdminServiceBrokerManagerAdminException;
import org.wso2.carbon.andes.stub.AndesAdminServiceStub;
import org.wso2.carbon.andes.stub.admin.types.Message;
import org.wso2.carbon.andes.stub.admin.types.Queue;
import org.wso2.carbon.andes.stub.admin.types.QueueRolePermission;
import org.wso2.mb.integration.common.clients.operations.clients.utils.AuthenticateStubUtil;

import java.rmi.RemoteException;

/**
 * Andes Admin Client is a client which is used to contact the Andes Admin services
 */
public class AndesAdminClient {
    String backendUrl = null;
    String sessionCookie = null;
    AndesAdminServiceStub stub = null;
    public static final String PUBLISHER_ROLE = "publisher";

    /**
     * Initializes Andes Admin Client
     *
     * @param backendUrl           the backend url
     * @param sessionCookie        the session cookie string
     * @throws AxisFault
     */
    public AndesAdminClient(String backendUrl, String sessionCookie) throws AxisFault {

        this.backendUrl = backendUrl + "AndesAdminService";
        this.sessionCookie = sessionCookie;
        stub = new AndesAdminServiceStub(this.backendUrl);
        AuthenticateStubUtil.authenticateStub(sessionCookie, stub);
    }

    /**
     * Creates a new queue
     *
     * @param queue new queue name
     * @throws AndesAdminServiceBrokerManagerAdminException
     * @throws RemoteException
     */
    public void createQueue(String queue)
            throws AndesAdminServiceBrokerManagerAdminException, RemoteException {
        // Add permission to be able to publish
        QueueRolePermission queueRolePermission = new QueueRolePermission();
        queueRolePermission.setRoleName(PUBLISHER_ROLE);
        queueRolePermission.setAllowedToConsume(true);
        queueRolePermission.setAllowedToPublish(true);
        stub.addQueueAndAssignPermission(queue, new QueueRolePermission[]{queueRolePermission});
    }

    /**
     * Gets messages in a queue
     *
     * @param queue               the queue name
     * @param startingIndex       starting index of the messages to be returned
     * @param maximumMessageCount maximum number of messages to return
     * @return an array of messages
     * @throws AndesAdminServiceBrokerManagerAdminException
     * @throws RemoteException
     */
    public Message[] browseQueue(String queue, int startingIndex, int maximumMessageCount)
            throws AndesAdminServiceBrokerManagerAdminException, RemoteException {
        return stub.browseQueue(queue, startingIndex, maximumMessageCount);
    }

    /**
     * Deletes a queue
     *
     * @param queue the queue name
     * @throws AndesAdminServiceBrokerManagerAdminException
     * @throws RemoteException
     */
    public void deleteQueue(String queue)
            throws AndesAdminServiceBrokerManagerAdminException, RemoteException {
        stub.deleteQueue(queue);
    }

    /**
     * Deletes all messages in a queue
     *
     * @param queue the name of the queue
     * @throws RemoteException
     */
    public void purgeQueue(String queue)
            throws RemoteException, AndesAdminServiceBrokerManagerAdminException {
        stub.purgeMessagesOfQueue(queue);
    }

    /**
     * Get queue object by queue name
     *
     * @param name the name of the queue
     * @return a queue
     * @throws RemoteException
     * @throws AndesAdminServiceBrokerManagerAdminException
     */
    public Queue getQueueByName(String name)
            throws RemoteException, AndesAdminServiceBrokerManagerAdminException {
        return stub.getQueueByName(name);
    }

    /**
     * Updating permissions for a queue. Permissions may include publish, consume etc
     *
     * @param queueName   queue name
     * @param permissions new permissions
     * @throws AndesAdminServiceBrokerManagerAdminException
     * @throws RemoteException
     */
    public void updatePermissionForQueue(String queueName, QueueRolePermission permissions)
            throws AndesAdminServiceBrokerManagerAdminException, RemoteException {
        stub.updatePermission(queueName, new QueueRolePermission[]{permissions});
    }

    /**
     * Adding session cookie to service client options
     *
     * @param client the service client
     * @throws AxisFault
     */
    private void configureCookie(ServiceClient client) throws AxisFault {
        if (sessionCookie != null) {
            Options option = client.getOptions();
            option.setManageSession(true);
            option.setProperty(org.apache.axis2.transport.http.HTTPConstants.COOKIE_STRING,
                    sessionCookie);
        }
    }

    /**
     * Get dead letter channel queue
     *
     * @return queue
     * @throws AndesAdminServiceBrokerManagerAdminException
     * @throws java.rmi.RemoteException
     */
    public Queue getDlcQueue() throws AndesAdminServiceBrokerManagerAdminException,
            java.rmi.RemoteException {
        return stub.getDLCQueue();
    }

}
