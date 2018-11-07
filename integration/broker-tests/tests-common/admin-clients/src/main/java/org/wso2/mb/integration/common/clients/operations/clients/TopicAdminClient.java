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
import org.wso2.carbon.andes.event.stub.core.TopicNode;
import org.wso2.carbon.andes.event.stub.core.TopicRolePermission;
import org.wso2.carbon.andes.event.stub.service.AndesEventAdminServiceEventAdminException;
import org.wso2.carbon.andes.event.stub.service.AndesEventAdminServiceStub;

import java.rmi.RemoteException;

/**
 * Topic Admin Client is a client which is used to contact the Topic Admin services
 */
public class TopicAdminClient {

    String backendUrl = null;
    String SessionCookie = null;
    AndesEventAdminServiceStub stub = null;

    /**
     * Initializes Topic Admin Client
     *
     * @param backendUrl           the backend url
     * @param sessionCookie        the session cookie string
     * @throws AxisFault
     */
    public TopicAdminClient(String backendUrl, String sessionCookie) throws AxisFault {

        this.backendUrl = backendUrl + "AndesEventAdminService.AndesEventAdminServiceHttpsSoap12Endpoint";
        this.SessionCookie = sessionCookie;
        stub = new AndesEventAdminServiceStub(this.backendUrl);
        configureCookie(stub._getServiceClient());

    }

    /**
     * Adds a new topic
     *
     * @param newTopicName new topic name
     * @throws AndesEventAdminServiceEventAdminException
     * @throws RemoteException
     */
    public void addTopic(String newTopicName)
            throws AndesEventAdminServiceEventAdminException, RemoteException {
        stub.addTopic(newTopicName);
    }

    /**
     * Removes a topic
     *
     * @param topicName topic name
     * @throws AndesEventAdminServiceEventAdminException
     * @throws RemoteException
     */
    public void removeTopic(String topicName)
            throws AndesEventAdminServiceEventAdminException, RemoteException {
        stub.removeTopic(topicName);
    }

    /**
     * Get topic node by topic name
     *
     * @param topicName the topic name
     * @return a topic node
     * @throws AndesEventAdminServiceEventAdminException
     * @throws RemoteException
     */
    public TopicNode getTopicByName(String topicName)
            throws AndesEventAdminServiceEventAdminException, RemoteException {
        TopicNode[] topicNodes = stub.getAllTopics().getChildren();
        if (topicNodes != null && topicNodes.length > 0) {
            for (TopicNode topicNode : topicNodes) {
                if (topicNode.getTopicName().equalsIgnoreCase(topicName)) {
                    return topicNode;
                }
            }
        }

        return null;
    }

    /**
     * Updating permissions for a topic. Permissions may include publish, consume etc
     *
     * @param topicName   topic name
     * @param permissions new permissions
     * @throws AndesEventAdminServiceEventAdminException
     * @throws RemoteException
     */
    public void updatePermissionForTopic(String topicName, TopicRolePermission permissions)
            throws AndesEventAdminServiceEventAdminException, RemoteException {
        stub.updatePermission(topicName, new TopicRolePermission[]{permissions});
    }

    /**
     * Adding session cookie to service client options
     *
     * @param client the service client
     * @throws AxisFault
     */
    private void configureCookie(ServiceClient client) throws AxisFault {
        if (SessionCookie != null) {
            Options option = client.getOptions();
            option.setManageSession(true);
            option.setProperty(org.apache.axis2.transport.http.HTTPConstants.COOKIE_STRING,
                               SessionCookie);
        }
    }
}
