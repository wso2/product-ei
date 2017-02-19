/*
 * Copyright (c) 2012, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.ei.businessprocess.utils.processcleanup;

import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ServiceContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.authenticator.stub.AuthenticationAdminStub;
import org.wso2.carbon.registry.resource.stub.ResourceAdminServiceStub;
import org.wso2.carbon.utils.NetworkUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * Class to delete relevant versions from Registry
 */
public class RegistryCleaner {
	private static final Log log = LogFactory.getLog(RegistryCleaner.class);

	private static Properties prop = new Properties();

	/**
	 * Cleans the registry at the regPath location
	 *
	 * @param regPath              registry path for the given package
	 * @param packageName          given package name
	 * @param clientTrustStorePath client trust store path
	 * @param trustStorePassword   client trust store password
	 * @param trustStoreType       trust store type
	 * @return true if the given package is removed from the carbon registry
	 */
	public static boolean deleteRegistry(String regPath, String packageName,
	                                     String clientTrustStorePath, String trustStorePassword,
	                                     String trustStoreType) {
		ResourceAdminServiceStub resourceAdminServiceStub;
		setKeyStore(clientTrustStorePath, trustStorePassword, trustStoreType);

		try {

			if (System.getProperty(CleanupConstants.OS_NAME).startsWith(CleanupConstants.WINDOWS)) {
				prop.load(new FileInputStream(
						System.getProperty(CleanupConstants.CARBON_HOME) + File.separator + CleanupConstants.CONF +
						File.separator + CleanupConstants.CLEANUP_PROPERTIES));
			} else {
				prop.load(new FileInputStream(System.getProperty(CleanupConstants.CARBON_HOME) +
				                              File.separator +
				                              CleanupConstants.CONF + File.separator +
				                              CleanupConstants.CLEANUP_PROPERTIES));
			}

			String resourceAdminServiceURL = prop.getProperty(CleanupConstants.TENANT_CONTEXT) +
			                                 CleanupConstants.RESOURCE_ADMIN_SERVICE_PATH;

			resourceAdminServiceStub = new ResourceAdminServiceStub(resourceAdminServiceURL);
			ServiceClient client = resourceAdminServiceStub._getServiceClient();
			Options option = client.getOptions();
			option.setManageSession(true);
			option.setProperty(org.apache.axis2.transport.http.HTTPConstants.COOKIE_STRING,
			                   login());
			resourceAdminServiceStub._getServiceClient().getOptions()
			                        .setTimeOutInMilliSeconds(CleanupConstants.TIME_OUT_MILLS);

			String regPathAppend = regPath + packageName.split("-\\d*$")[0];
			String regPathVersionsAppend = regPathAppend + CleanupConstants.VERSIONS_PATH;
			int count = resourceAdminServiceStub.getCollectionContent(regPathVersionsAppend)
			                                    .getChildCount();

			/* if the number of deployment units of the given package exceed one, then removes the
			   relevant deployment unit from the path that it exists. */
			if (count > 1) {
				resourceAdminServiceStub.delete(regPathVersionsAppend + packageName);
				return true;
			}
			/* if the number of deployment units of the given package equals to one, then removes
			   it from /_system/config/bpel/packages/<package> */
			else if (count == 1) {
				resourceAdminServiceStub.delete(regPathAppend);
				return true;
			}
		} catch (Exception e) {
			log.error("Error occurred while cleaning the registry.", e);
		}
		return false;
	}

	/**
	 * Setup key store according to the processCleanup.properties
	 *
	 * @param clientTrustStorePath client trust store path
	 * @param trustStorePassword   trust store password
	 * @param trustStoreType       trust store type
	 */
	private static void setKeyStore(String clientTrustStorePath, String trustStorePassword,
	                                String trustStoreType) {
		System.setProperty(CleanupConstants.JAVAX_SSL_TRUST_STORE, clientTrustStorePath);
		System.setProperty(CleanupConstants.JAVAX_SSL_TRUST_STORE_PASSWORD, trustStorePassword);
		System.setProperty(CleanupConstants.JAVAX_SSL_TRUST_STORE_TYPE, trustStoreType);
	}

	/**
	 * Creates the login session BPS login
	 *
	 * @return cookie
	 * @throws Exception
	 */
	public static String login() throws Exception {

		AuthenticationAdminStub authenticationAdminStub;
		String authenticationAdminServiceURL = prop.getProperty(CleanupConstants.TENANT_CONTEXT) +
		                                       CleanupConstants.SERVICE_AUTHENTICATION_ADMIN_PATH;
		authenticationAdminStub = new AuthenticationAdminStub(authenticationAdminServiceURL);

		ServiceClient client = authenticationAdminStub._getServiceClient();
		Options options = client.getOptions();
		options.setManageSession(true);

		String userName = prop.getProperty(CleanupConstants.BPS_USER_NAME);
		String password = prop.getProperty(CleanupConstants.BPS_PASSWORD);
		String hostName = NetworkUtils.getLocalHostname();

		authenticationAdminStub.login(userName, password, hostName);

		ServiceContext serviceContext =
				authenticationAdminStub._getServiceClient().getLastOperationContext()
				                       .getServiceContext();

		return (String) serviceContext.getProperty(HTTPConstants.COOKIE_STRING);
	}
}
