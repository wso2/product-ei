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
import org.wso2.carbon.registry.resource.stub.ResourceAdminServiceExceptionException;
import org.wso2.carbon.registry.resource.stub.ResourceAdminServiceResourceServiceExceptionException;
import org.wso2.carbon.registry.resource.stub.ResourceAdminServiceStub;
import org.wso2.carbon.registry.resource.stub.beans.xsd.CollectionContentBean;
import org.wso2.carbon.registry.resource.stub.beans.xsd.ContentBean;
import org.wso2.carbon.registry.resource.stub.beans.xsd.MetadataBean;
import org.wso2.carbon.registry.resource.stub.beans.xsd.PermissionBean;
import org.wso2.carbon.registry.resource.stub.beans.xsd.VersionPath;
import org.wso2.carbon.registry.resource.stub.beans.xsd.VersionsBean;
import org.wso2.carbon.registry.resource.stub.common.xsd.ResourceData;


import java.rmi.RemoteException;

/**
 * This is a utility class that is used to call the RemoteAdminService.
 */
public class ResourceAdminServiceClient {
	private static final Log log = LogFactory.getLog(ResourceAdminServiceClient.class);

	private final String serviceName = "ResourceAdminService";
	private ResourceAdminServiceStub resourceAdminServiceStub;

	/**
	 * Initialize the stub.
	 * @param serviceUrl URL of the back end service.
	 * @param sessionCookie sessionCookie of a logged in User
	 * @throws AxisFault throws when initialization fails
	 */
	public ResourceAdminServiceClient(String serviceUrl, String sessionCookie) throws AxisFault {
		String endPoint = serviceUrl + serviceName;
		resourceAdminServiceStub = new ResourceAdminServiceStub(endPoint);
		long soTimeout = 5 * 60 * 1000; // Three minutes
		ServiceClient client = resourceAdminServiceStub._getServiceClient();
		Options option = client.getOptions();
		option.setManageSession(true);
		option.setTimeOutInMilliSeconds(soTimeout);
		option.setProperty(org.apache.axis2.transport.http.HTTPConstants.COOKIE_STRING, sessionCookie);
	}

	/**
	 * This is used to create a resource in registry.
	 * @param parentPath path that the collection needs to be created.
	 * @param collectionName name of the collection.
	 * @param mediaType media type of the resource.
	 * @param description description of the resource
	 * @return resource path.
	 * @throws ResourceAdminServiceExceptionException exception that thrown when there is a error occurs whilst adding the resource.
	 * @throws RemoteException thrown when remote invocation fails.
	 */
	public String addCollection(String parentPath, String collectionName,
								String mediaType, String description)
			throws ResourceAdminServiceExceptionException, RemoteException {
		return resourceAdminServiceStub.addCollection(parentPath, collectionName, mediaType, description);
	}

}
