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
package org.wso2.carbon.integrator.core.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.utils.ConfigurationContextService;

/**
 * Declarative service component Integrator component.
 */

/**
 * @scr.component name="org.wso2.carbon.integrator.core.internal.IntegratorComponent" immediate="true"
 * @scr.reference name="configuration.context.service" interface="org.wso2.carbon.utils.ConfigurationContextService" cardinality="1..1"
 * policy="dynamic" bind="setConfigurationContextService" unbind="unsetConfigurationContextService"
 */
public class IntegratorComponent {
    private static final Log log = LogFactory.getLog(IntegratorComponent.class);


    private static ConfigurationContextService contextService;

    protected void activate(ComponentContext context) {
        log.info("Activating Integrator component");
    }

    protected void deactivate(ComponentContext context) {
        log.info("Deactivating Integrator component");
    }

    protected void setConfigurationContextService(ConfigurationContextService contextService) {
        IntegratorComponent.contextService = contextService;
    }

    protected void unsetConfigurationContextService(ConfigurationContextService contextService) {
        IntegratorComponent.contextService = null;
    }

    public static ConfigurationContextService getContextService() {
        return contextService;
    }
}

