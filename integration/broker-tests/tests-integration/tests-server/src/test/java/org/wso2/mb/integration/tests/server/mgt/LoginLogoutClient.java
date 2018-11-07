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

package org.wso2.mb.integration.tests.server.mgt;


import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.authenticator.stub.LogoutAuthenticationExceptionException;
import org.wso2.carbon.integration.common.admin.client.AuthenticatorClient;
import java.rmi.RemoteException;

/**
 * A utility for logging into & logging out of Carbon servers
 */
public class LoginLogoutClient {
    private static final Log log = LogFactory.getLog(LoginLogoutClient.class);
    private String userName;
    private String password;

    private AuthenticatorClient loginClient;

    public LoginLogoutClient(String backendURL,String userName,String password) throws AxisFault {

        this.userName=userName;
        this.password = password;
        this.loginClient = new AuthenticatorClient(backendURL);
    }

    /**
     * Log in to a Carbon server
     *
     * @return The session cookie on successful login
     */
    public String login() throws RemoteException, LoginAuthenticationExceptionException {
        return loginClient.login(userName, password, "localhost");

    }

    /**
     * Log out from carbon server
     */
    public void logout() throws LogoutAuthenticationExceptionException, RemoteException {
        loginClient.logOut();
    }
}

