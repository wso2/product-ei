/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 */

package org.wso2.ei.mb.integration.tests.mqtt.functional.util;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.um.ws.api.stub.RemoteAuthorizationManagerServiceStub;
import org.wso2.carbon.um.ws.api.stub.UserStoreExceptionException;

import java.rmi.RemoteException;

/**
 * This is a utility class that is used to call the AuthorizationManagerService
 */
public class RemoteAuthorizationManagerServiceClient {
	private static final Log log = LogFactory.getLog(RemoteAuthorizationManagerServiceClient.class);
	private final String serviceName = "RemoteAuthorizationManagerService";
	private RemoteAuthorizationManagerServiceStub remoteAuthorizationManagerServiceStub;

	private String endPoint;

	/**
	 * Initialize the stub.
	 * @param backEndUrl URL of the back end service.
	 * @param sessionCookie sessionCookie of a logged in User
	 * @throws AxisFault throws when initialization fails
	 */
	public RemoteAuthorizationManagerServiceClient(String backEndUrl, String sessionCookie) throws AxisFault {
		this.endPoint = backEndUrl + serviceName;
		remoteAuthorizationManagerServiceStub = new RemoteAuthorizationManagerServiceStub(endPoint);
		long soTimeout = 5 * 60 * 1000; // Three minutes
		ServiceClient client = remoteAuthorizationManagerServiceStub._getServiceClient();
		Options option = client.getOptions();
		option.setManageSession(true);
		option.setTimeOutInMilliSeconds(soTimeout);
		option.setProperty(org.apache.axis2.transport.http.HTTPConstants.COOKIE_STRING, sessionCookie);
	}

	/**
	 * Call the internal stub to check whether the role is authorized with particular resource.
	 * @param roleName that needs to authorized.
	 * @param resourceId Permission String
	 * @param action Permission Action that relavant to the permission string
	 * @throws RemoteException thrown when remote invocation fails.
	 * @throws UserStoreExceptionException throws when any errors thrown on authorize role flow releavant to userstore impl.
	 */
	public void authorizeRole(String roleName, String resourceId, String action) throws RemoteException,
																						UserStoreExceptionException {
		remoteAuthorizationManagerServiceStub.authorizeRole(roleName, resourceId, action);
	}
}

