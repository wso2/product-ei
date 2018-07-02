/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
import org.wso2.carbon.micro.integrator.security.internal.DataHolder;
import org.wso2.carbon.user.api.RealmConfiguration;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.api.UserStoreManager;
import org.wso2.carbon.user.core.claim.ClaimManager;
import org.wso2.carbon.user.core.profile.ProfileConfigurationManager;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;
import java.lang.reflect.Constructor;

public abstract class MIPWCallback implements CallbackHandler {

    protected final Log log = LogFactory.getLog(MIPWCallback.class);

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
                    userStoreManager = (UserStoreManager) createObjectWithOptions(realmConfig.getUserStoreClass(), realmConfig);
                }
            }

            for (int i = 0; i < callbacks.length; i++) {
                if (callbacks[i] instanceof WSPasswordCallback) {
                    WSPasswordCallback passwordCallback = (WSPasswordCallback) callbacks[i];

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
                                    throw new UnsupportedCallbackException(callbacks[i], "check failed");
                                }
                            } catch (Exception e) {
                                throw new UnsupportedCallbackException(callbacks[i],
                                        "Check failed : System error");
                            }

                            break;

                        case WSPasswordCallback.USERNAME_TOKEN:

                            /*
                            * In username token scenario, if user sends the digested password, callback handler needs to provide plain text password.
                            * We get plain text password through UserCredentialRetriever interface, which is implemented by custom user store managers.
                            * we expect username with domain name if user resides in a secondary user store, eg, WSO2.Test/fooUser.
                            * Additionally, secondary user stores needs to implement UserCredentialRetriever interface too
                            */

                            UserCredentialRetriever userCredentialRetriever;
                            String storedPassword = null;
                            if (userStoreManager instanceof UserCredentialRetriever) {
                                userCredentialRetriever = (UserCredentialRetriever) userStoreManager;
                                storedPassword = userCredentialRetriever.getPassword(username);
                            } else {
                                if (log.isDebugEnabled()) {
                                    log.debug("Can not set user password in callback because primary userstore class" +
                                            " has not implemented UserCredentialRetriever interface.");
                                }
                            }
                            if (storedPassword != null) {
                                try {
                                    if (this.authenticateUser(username, storedPassword)) {
                                        // do nothing things are fine
                                    } else {
                                        if (log.isDebugEnabled()) {
                                            log.debug("User is not authorized!");
                                        }
                                        throw new UnsupportedCallbackException(callbacks[i], "check failed");
                                    }
                                } catch (Exception e) {
                                    throw new UnsupportedCallbackException(callbacks[i],
                                            "Check failed : System error");
                                }
                                passwordCallback.setPassword(storedPassword);
                                break;
                            }

                        default:

                            /*
                             * When the password is null WS4J reports an error
                             * saying no password available for the user. But its
                             * better if we simply report authentication failure
                             * Therefore setting the password to be the empty string
                             * in this situation.
                             */

                            passwordCallback.setPassword(receivedPasswd);
                            break;
                    }
                    if (isAuthenticated) {
                        return;
                    }
                } else {
                    throw new UnsupportedCallbackException(callbacks[i], "Unrecognized Callback");
                }
            }
        } catch (UnsupportedCallbackException | IOException e) {
            if (log.isDebugEnabled()) {
                log.debug("Error in handling PasswordCallbackHandler", e); //logging invlaid passwords and attempts
                throw e;
            }
            throw e;
        } catch (org.wso2.carbon.user.core.UserStoreException e) {
            log.error("Error in handling PasswordCallbackHandler", e);
            throw new UnsupportedCallbackException(null, e.getMessage());
        } catch (Exception e) {
            log.error("Error in handling PasswordCallbackHandler", e);
            //can't build an unsupported exception.
            throw new UnsupportedCallbackException(null, e.getMessage());
        }
    }

    public Object createObjectWithOptions(String className, RealmConfiguration realmConfig) throws UserStoreException {

        Class[] initClassOpt1 = new Class[]{RealmConfiguration.class, ClaimManager.class, ProfileConfigurationManager.class};
        Object[] initObjOpt1 = new Object[]{realmConfig, null, null};

        try {
            Class clazz = Class.forName(className);
            Object newObject = null;
            if (log.isDebugEnabled()) {
                log.debug("Start initializing the UserStoreManager class");
            }

            Constructor constructor;
            try {
                constructor = clazz.getConstructor(initClassOpt1);
                newObject = constructor.newInstance(initObjOpt1);
                return newObject;
            } catch (NoSuchMethodException var17) {
                if (log.isDebugEnabled()) {
                    log.debug("Cannont initialize " + className);
                }
                throw new UserStoreException(var17.getMessage(), var17);
            }
        } catch (Throwable var18) {
            if (log.isDebugEnabled()) {
                log.debug("Cannot create " + className, var18);
            }

            throw new UserStoreException(var18.getMessage() + "Type " + var18.getClass(), var18);
        }
    }

    private boolean authenticateUser(String user, String password) throws Exception {

        boolean isAuthenticated = false;

        String tenantAwareUserName = MultitenantUtils.getTenantAwareUsername(user);

        try {
            isAuthenticated = userStoreManager.authenticate(
                    tenantAwareUserName, password);

            // TODO - Handle Authorization of users, once they are authenticated

            return isAuthenticated;
        } catch (Exception e) {
            log.error("Error in authenticating user.", e);
            throw e;
        }
    }

}