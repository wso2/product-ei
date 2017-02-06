/**
 *  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.bps.humantask.sample.manager;

import org.apache.axis2.context.ServiceContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.bps.humantask.sample.util.HumanTaskSampleConstants;
import org.wso2.carbon.authenticator.stub.AuthenticationAdminStub;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoginManager extends HttpServlet {

    /**
     * Servlet will manage the user login
     */
    private static Log log = LogFactory.getLog(LoginManager.class);


    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userName = null, userPassword = null;
        ServletContext servletContext = this.getServletContext();
        String backendServerURL = servletContext.getInitParameter(HumanTaskSampleConstants.BACKEND_SERVER_URL);

        // set the required system properties
        System.setProperty("javax.net.ssl.trustStore", servletContext
                .getInitParameter(HumanTaskSampleConstants.CLIENT_TRUST_STORE_PATH).trim());
        System.setProperty("javax.net.ssl.trustStorePassword", servletContext
                .getInitParameter(HumanTaskSampleConstants.CLIENT_TRUST_STORE_PASSWORD).trim());
        System.setProperty("javax.net.ssl.trustStoreType", servletContext
                .getInitParameter(HumanTaskSampleConstants.CLIENT_TRUST_STORE_TYPE).trim());
        try {
            AuthenticationAdminStub authenticationAdminStub = new AuthenticationAdminStub(backendServerURL +
                                                                                          HumanTaskSampleConstants
                                                                                                  .SERVICE_URL +
                                                                                          HumanTaskSampleConstants
                                                                                                  .AUTHENTICATION_ADMIN_SERVICE_URL);
            // handles logout
            String logout = req.getParameter("logout");
            if (logout != null) {
                authenticationAdminStub.logout();
                req.getRequestDispatcher("/Login.jsp").forward(req, resp);
                return;
            }
            if (req.getParameter("userName") != null) {
                userName = req.getParameter("userName").trim();
            }
            if (req.getParameter("userPassword") != null) {
                userPassword = req.getParameter("userPassword").trim();
            }
            // login to server with given user name and password
            if (authenticationAdminStub.login(userName, userPassword, HumanTaskSampleConstants.HOSTNAME)) {
                ServiceContext serviceContext = authenticationAdminStub._getServiceClient().getLastOperationContext()
                        .getServiceContext();
                String sessionCookie = (String) serviceContext.getProperty(HTTPConstants.COOKIE_STRING);
                HttpSession session = req.getSession();
                session.setAttribute(HumanTaskSampleConstants.USERNAME, userName);
                session.setAttribute(HumanTaskSampleConstants.SESSION_COOKIE, sessionCookie);
                req.getRequestDispatcher("/Home.jsp?queryType=assignedToMe&pageNumber=0").forward(req, resp);

            } else {
                log.warn(userName + " login failed.");
                req.setAttribute("message", "Please enter a valid user name and a password.");
                req.getRequestDispatcher("/Login.jsp").forward(req, resp);
            }

        } catch (Exception e) {
            log.error("Failed to retrieve the user session ", e);
            req.setAttribute(HumanTaskSampleConstants.MESSAGE, e);
            req.getRequestDispatcher("/Login.jsp").forward(req, resp);
        }

    }

}
