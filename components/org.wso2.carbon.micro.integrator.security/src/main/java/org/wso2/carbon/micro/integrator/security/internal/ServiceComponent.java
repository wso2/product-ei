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

package org.wso2.carbon.micro.integrator.security.internal;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.micro.integrator.security.callback.DefaultPasswordCallback;
import org.wso2.carbon.micro.integrator.security.MicroIntegratorSecurityUtils;
import org.wso2.carbon.user.api.RealmConfiguration;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.api.UserStoreManager;
import org.wso2.carbon.utils.ConfigurationContextService;

/**
 * @scr.component name="micro.server.security"" immediate="true"
 * @scr.reference name="org.wso2.carbon.configCtx"
 * interface="org.wso2.carbon.utils.ConfigurationContextService" cardinality="1..1"
 * policy="dynamic" bind="setConfigurationContext" unbind="unsetConfigurationContext"
 **/
public class ServiceComponent {

    private static Log log = LogFactory.getLog(ServiceComponent.class);

    private ConfigurationContext configCtx;

    protected void activate(ComponentContext ctxt) {
        try {
            setSecurityParams();
        } catch (Throwable e) {
            log.error("Failed to activate Micro Integrator security bundle ", e);
        }
    }

    private void setSecurityParams() {
        AxisConfiguration axisConfig = this.configCtx.getAxisConfiguration();

        Parameter passwordCallbackParam = new Parameter();
        DefaultPasswordCallback passwordCallbackClass = new DefaultPasswordCallback();
        passwordCallbackParam.setName("passwordCallbackRef");
        passwordCallbackParam.setValue(passwordCallbackClass);

        try {
            axisConfig.addParameter(passwordCallbackParam);
        } catch (AxisFault axisFault) {
            log.error("Failed to set axis configuration parameter ", axisFault);
        }

        DataHolder dataHolder = DataHolder.getInstance();

        RealmConfiguration config = passwordCallbackClass.getRealmConfig();
        dataHolder.setRealmConfig(config);

        try {
            UserStoreManager userStoreManager = (UserStoreManager) MicroIntegratorSecurityUtils.
                    createObjectWithOptions(config.getUserStoreClass(), config);
            dataHolder.setUserStoreManager(userStoreManager);
        } catch (UserStoreException e) {
            log.error("Error on initializing User Store Manager Class", e);
        }
    }

    protected void deactivate(ComponentContext ctxt) {
        log.debug("Micro Integrator Security bundle is deactivated ");
    }

    protected void setConfigurationContext(ConfigurationContextService configCtx) {
        this.configCtx = configCtx.getServerConfigContext();
    }

    protected void unsetConfigurationContext(ConfigurationContextService configCtx) {
        this.configCtx = null;
    }
}
