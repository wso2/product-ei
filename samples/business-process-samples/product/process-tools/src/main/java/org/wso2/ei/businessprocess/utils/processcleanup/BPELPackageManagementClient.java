/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.ei.businessprocess.utils.processcleanup;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ServiceContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.authenticator.stub.AuthenticationAdminStub;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.bpel.stub.mgt.BPELPackageManagementServiceStub;
import org.wso2.carbon.bpel.stub.mgt.PackageManagementException;
import org.wso2.carbon.bpel.stub.mgt.types.DeployedPackagesPaginated;
import org.wso2.carbon.utils.NetworkUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Properties;

/**
 * Client class to call BPEL Package management service stub
 */
public class BPELPackageManagementClient {

    private final static Log log = LogFactory.getLog(BPELPackageManagementClient.class);

    private BPELPackageManagementServiceStub stub;
    private static Properties prop = new Properties();

    public BPELPackageManagementClient(String bpsURL, String username, String password) throws AxisFault {
        stub = new BPELPackageManagementServiceStub(bpsURL +
                CleanupConstants.BPEL_PACKAGE_MANAGEMENT_ADMIN_SERVICE_URL);
        ServiceClient serviceClient = stub._getServiceClient();

        Options options = serviceClient.getOptions();

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

            String clientTrustStorePath = prop.getProperty(CleanupConstants.CLIENT_TRUST_STORE_PATH);
            String trustStorePassword = prop.getProperty(CleanupConstants.CLIENT_TRUST_STORE_PASSWORD);
            String trustStoreType = prop.getProperty(CleanupConstants.CLIENT_TRUST_STORE_TYPE);
            setKeyStore(clientTrustStorePath, trustStorePassword, trustStoreType);

            options.setManageSession(true);
        } catch (IOException e) {
            throw new AxisFault("Error reading process-cleanup.properties file", e);
        }

        try {
            options.setProperty(HTTPConstants.COOKIE_STRING,
                    login(username, password));
        } catch (IOException | LoginAuthenticationExceptionException e) {
            throw new AxisFault("Error authenticating BPEL Package Management Client", e);
        }
    }

    /**
     * This method list deployed BPS packages
     *
     * @param page
     * @param packageSearchString
     * @return
     * @throws PackageManagementException
     * @throws RemoteException
     */
    public DeployedPackagesPaginated listDeployedPackagesPaginated(int page, String packageSearchString)
            throws RemoteException, PackageManagementException {
        return stub.listDeployedPackagesPaginated(page, packageSearchString);
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
    public static String login(String userName, String password) throws RemoteException,
            LoginAuthenticationExceptionException, MalformedURLException, SocketException {
        AuthenticationAdminStub authenticationAdminStub;
        String authenticationAdminServiceURL = prop.getProperty(CleanupConstants.TENANT_CONTEXT) +
                CleanupConstants.SERVICE_AUTHENTICATION_ADMIN_PATH;
        authenticationAdminStub = new AuthenticationAdminStub(authenticationAdminServiceURL);

        ServiceClient client = authenticationAdminStub._getServiceClient();
        Options options = client.getOptions();
        options.setManageSession(true);

        String hostName = new URL(prop.getProperty(CleanupConstants.TENANT_CONTEXT)).getHost();

        authenticationAdminStub.login(userName, password, hostName);

        ServiceContext serviceContext =
                authenticationAdminStub._getServiceClient().getLastOperationContext()
                        .getServiceContext();

        return (String) serviceContext.getProperty(HTTPConstants.COOKIE_STRING);
    }
}
