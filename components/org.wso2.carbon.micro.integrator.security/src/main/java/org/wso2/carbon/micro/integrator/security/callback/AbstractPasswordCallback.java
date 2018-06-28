/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.micro.integrator.security.callback;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.security.WSPasswordCallback;
import org.wso2.carbon.micro.integrator.security.MicroIntegratorSecurityUtils;
import org.wso2.carbon.micro.integrator.security.internal.DataHolder;
import org.wso2.carbon.user.api.RealmConfiguration;
import org.wso2.carbon.user.api.UserStoreManager;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;

/**
 * This class handles the authentication of the username token via the defined user store.
 * This class can be inherited to write a password callback handler, by implementing the getRealmConfig method.
 */
public abstract class AbstractPasswordCallback implements CallbackHandler {

    protected final Log log = LogFactory.getLog(AbstractPasswordCallback.class);
    public abstract RealmConfiguration getRealmConfig();
    private UserStoreManager userStoreManager;
    private RealmConfiguration realmConfig;
    private DataHolder dataHolder = DataHolder.getInstance();

    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        try {
            boolean isAuthenticated = false;
            if (realmConfig == null) {
                realmConfig = dataHolder.getRealmConfig();
                if (realmConfig == null) {
                    realmConfig = getRealmConfig();
                }
            }
            if (userStoreManager == null) {
                userStoreManager = dataHolder.getUserStoreManager();
                if (userStoreManager == null) {
                    userStoreManager = (UserStoreManager) MicroIntegratorSecurityUtils.
                            createObjectWithOptions(realmConfig.getUserStoreClass(), realmConfig);
                }
            }
            for (Callback callback : callbacks) {
                if (callback instanceof WSPasswordCallback) {
                    WSPasswordCallback passwordCallback = (WSPasswordCallback) callback;

                    String username = passwordCallback.getIdentifer();
                    String receivedPasswd = null;
                    switch (passwordCallback.getUsage()) {

                        // TODO - Handle SIGNATURE, DECRYPT AND KERBEROS_TOKEN password callback usages

                        case WSPasswordCallback.USERNAME_TOKEN_UNKNOWN:

                            receivedPasswd = passwordCallback.getPassword();
                            try {
                                if (receivedPasswd != null
                                        && this.authenticateUser(username, receivedPasswd)) {
                                    isAuthenticated = true;
                                } else {
                                    throw new UnsupportedCallbackException(callback, "check failed");
                                }
                            } catch (Exception e) {
                                /*
                                 * As the UnsupportedCallbackException does not accept the exception as a parameter,
                                 * the stack trace is added to the error message.
                                 *
                                 */
                                throw new UnsupportedCallbackException(callback, "Check failed : System error\n" +
                                        MicroIntegratorSecurityUtils.stackTraceToString(e.getStackTrace()));
                            }
                            break;

                        case WSPasswordCallback.USERNAME_TOKEN:

                            /*
                             * In username token scenario, if user sends the digested password, callback handler needs
                             * to provide plain text password. We get plain text password through
                             * UserCredentialRetriever interface, which is implemented by custom user store managers.
                             */

                            UserCredentialRetriever userCredentialRetriever;
                            String storedPassword = null;
                            if (userStoreManager instanceof UserCredentialRetriever) {
                                userCredentialRetriever = (UserCredentialRetriever) userStoreManager;
                                storedPassword = userCredentialRetriever.getPassword(username);
                            } else {
                                log.error("Can not set user password in callback because primary userstore class" +
                                        " has not implemented UserCredentialRetriever interface.");

                            }
                            if (storedPassword != null) {
                                try {
                                    if (!this.authenticateUser(username, storedPassword)) {
                                        log.error("User is not authorized!");
                                        throw new UnsupportedCallbackException(callback, "check failed");
                                    }
                                } catch (Exception e) {
                                    /*
                                     * As the UnsupportedCallbackException does not accept the exception as a parameter,
                                     * the stack trace is added to the error message.
                                     *
                                     */
                                    throw new UnsupportedCallbackException(callback, "Check failed : System error\n" +
                                            MicroIntegratorSecurityUtils.stackTraceToString(e.getStackTrace()));
                                }
                                passwordCallback.setPassword(storedPassword);
                                break;
                            }

                        default:

                            /*
                             * When the password is null WS4J reports an error saying no password available for the
                             * user. But its better if we simply report authentication failure. Therefore setting the
                             * password to be the empty string in this situation.
                             */

                            passwordCallback.setPassword(receivedPasswd);
                            break;
                    }
                    if (isAuthenticated) {
                        return;
                    }
                } else {
                    throw new UnsupportedCallbackException(callback, "Unrecognized Callback");
                }
            }
        } catch (UnsupportedCallbackException | IOException e) {
            if (log.isDebugEnabled()) {
                //logging invlaid attempts
                log.debug("Error in handling PasswordCallbackHandler", e);
                throw e;
            }
            throw e;
        } catch (Exception e) {
            log.error("Error in handling PasswordCallbackHandler", e);
            throw new UnsupportedCallbackException(null, e.getMessage());
        }
    }

    private boolean authenticateUser(String user, String password) throws Exception {
        boolean isAuthenticated;
        try {
            isAuthenticated = userStoreManager.authenticate(user, password);

            // TODO - Handle Authorization of users, once they are authenticated

            return isAuthenticated;
        } catch (Exception e) {
            log.error("Error in authenticating user.", e);
            throw e;
        }
    }

}