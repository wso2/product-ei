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
import org.wso2.carbon.base.CarbonBaseUtils;
import org.wso2.carbon.tomcat.api.CarbonTomcatService;
import org.wso2.carbon.utils.ConfigurationContextService;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Declarative service component Integrator component.
 */

/**
 * @scr.component name="org.wso2.carbon.integrator.core.internal.IntegratorComponent" immediate="true"
 * @scr.reference name="configuration.context.service" interface="org.wso2.carbon.utils.ConfigurationContextService"
 * cardinality="1..1"
 * policy="dynamic" bind="setConfigurationContextService" unbind="unsetConfigurationContextService"
 * @scr.reference name="1..1" interface="org.wso2.carbon.tomcat.api.CarbonTomcatService"
 * cardinality="1..1" policy="dynamic" bind="setCarbonTomcatService"  unbind="unsetCarbonTomcatService"
 */
public class IntegratorComponent {
    private static final Log log = LogFactory.getLog(IntegratorComponent.class);

    private static ConfigurationContextService contextService;
    private static CarbonTomcatService carbonTomcatService;

    protected void activate(ComponentContext context) {
        log.debug("Activating Integrator component");
    }

    protected void deactivate(ComponentContext context) {
        log.debug("Deactivating Integrator component");
    }

    protected void setConfigurationContextService(ConfigurationContextService contextService) {
        setContextService(contextService);
    }

    protected static void setContextService(ConfigurationContextService contextService) {
        IntegratorComponent.contextService = contextService;
    }

    protected void unsetConfigurationContextService(ConfigurationContextService contextService) {
        unsetConfigurationService();
    }

    protected static void unsetConfigurationService() {
        IntegratorComponent.contextService = null;
    }

    protected static void unsetTomcatService() {
        IntegratorComponent.carbonTomcatService = null;
    }

    protected static void setTomcatService(CarbonTomcatService carbonTomcatService) {
        IntegratorComponent.carbonTomcatService = carbonTomcatService;
    }

    protected void setCarbonTomcatService(CarbonTomcatService contextService) {
        setTomcatService(contextService);
    }

    protected void unsetCarbonTomcatService(CarbonTomcatService contextService) {
        unsetTomcatService();
    }

    public static CarbonTomcatService getCarbonTomcatService() {
        return carbonTomcatService;
    }

    public static ConfigurationContextService getContextService() {
        return contextService;
    }
}

