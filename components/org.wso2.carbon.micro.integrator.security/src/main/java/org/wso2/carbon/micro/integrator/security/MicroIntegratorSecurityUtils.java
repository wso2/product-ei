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

package org.wso2.carbon.micro.integrator.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.user.api.RealmConfiguration;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.claim.ClaimManager;
import org.wso2.carbon.user.core.profile.ProfileConfigurationManager;

import java.lang.reflect.Constructor;

/**
 * This class contains utils required by the micro integrator security component.
 */
public class MicroIntegratorSecurityUtils {

    private static Log log = LogFactory.getLog(MicroIntegratorSecurityUtils.class);

    /**
     * This method initializes the user store manager class
     *
     * @param className   class name of the user store manager class
     * @param realmConfig realm configuration defined
     * @return initialized UserStoreManager class
     * @throws UserStoreException
     */
    public static Object createObjectWithOptions(String className, RealmConfiguration realmConfig) throws UserStoreException {
        /*
            Since different User Store managers contain constructors requesting different sets of arguments, this method
            tries to invoke the constructor with different combinations of arguments
         */
        Class[] initClassOpt1 = new Class[]{RealmConfiguration.class, ClaimManager.class, ProfileConfigurationManager.class};
        Object[] initObjOpt1 = new Object[]{realmConfig, null, null};
        Class[] initClassOpt2 = new Class[]{RealmConfiguration.class, Integer.class};
        Object[] initObjOpt2 = new Object[]{realmConfig, -1234};
        Class[] initClassOpt3 = new Class[]{RealmConfiguration.class};
        Object[] initObjOpt3 = new Object[]{realmConfig};
        Class[] initClassOpt4 = new Class[]{};
        Object[] initObjOpt4 = new Object[]{};
        try {
            Class clazz = Class.forName(className);
            Object newObject = null;
            if (log.isDebugEnabled()) {
                log.debug("Start initializing the UserStoreManager class with first option");
            }

            Constructor constructor;
            try {
                constructor = clazz.getConstructor(initClassOpt1);
                newObject = constructor.newInstance(initObjOpt1);
                return newObject;
            } catch (NoSuchMethodException e) {
                if (log.isDebugEnabled()) {
                    log.debug("Cannont initialize " + className + " trying second option");
                }
            }

            try {
                constructor = clazz.getConstructor(initClassOpt2);
                newObject = constructor.newInstance(initObjOpt2);
                return newObject;
            } catch (NoSuchMethodException e) {
                if (log.isDebugEnabled()) {
                    log.debug("Cannont initialize " + className + " using the option 2");
                }
            }

            try {
                constructor = clazz.getConstructor(initClassOpt3);
                newObject = constructor.newInstance(initObjOpt3);
                return newObject;
            } catch (NoSuchMethodException e) {
                if (log.isDebugEnabled()) {
                    log.debug("Cannont initialize " + className + " using the option 3");
                }
            }

            try {
                constructor = clazz.getConstructor(initClassOpt4);
                newObject = constructor.newInstance(initObjOpt4);
                return newObject;
            } catch (NoSuchMethodException e) {
                if (log.isDebugEnabled()) {
                    log.debug("Cannont initialize " + className + " using the option 4");
                }
                throw new UserStoreException(e.getMessage(), e);
            }
        } catch (Throwable e) {
            if (log.isDebugEnabled()) {
                log.debug("Cannot create " + className, e);
            }
            throw new UserStoreException(e.getMessage() + "Type " + e.getClass(), e);
        }
    }

    /**
     * This method converts a given stacktrace array to a string
     *
     * @param arr The stack trace array
     * @return the string generated from the stack trace array
     */
    public static String stackTraceToString(StackTraceElement[] arr) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : arr) {
            sb.append("\t" + element.toString() + "\n");
        }
        return sb.toString();
    }
}
