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

import org.wso2.carbon.user.api.RealmConfiguration;
import org.wso2.carbon.user.core.UserStoreException;

/**
 * This is the default implementation of the AbstractPasswordCallbackHandler which loads the realm configuration from
 * the user-mgt.xml file
 */
public class DefaultPasswordCallback extends AbstractPasswordCallback {

    @Override
    public RealmConfiguration getRealmConfig() {
        RealmConfigXMLProcessor processor = new RealmConfigXMLProcessor();
        RealmConfiguration realmConfig = null;
        try {
            realmConfig = processor.buildRealmConfigurationFromFile();

        } catch (UserStoreException e) {
            log.error("Error while loading Realm Configuration");
        }
        return realmConfig;
    }
}
