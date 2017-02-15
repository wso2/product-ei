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
 */
public class IntegratorComponent {
    private static final Log log = LogFactory.getLog(IntegratorComponent.class);

    private static ArrayList<String> whiteListContextPaths = new ArrayList<>();

    private static ConfigurationContextService contextService;

    protected void activate(ComponentContext context) {
        readProperties();
        log.info("Activating Integrator component");
    }

    protected void deactivate(ComponentContext context) {
        log.info("Deactivating Integrator component");
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

    public static ConfigurationContextService getContextService() {
        return contextService;
    }

    public static ArrayList<String> getWhiteListContextPaths() {
        return whiteListContextPaths;
    }

    private void readProperties() {
        try (InputStream file = new FileInputStream(
                Paths.get(CarbonBaseUtils.getCarbonConfigDirPath(), "security", "integrator-whitelist.properties")
                     .toString())) {
            Properties properties = new Properties();
            properties.load(file);
            Enumeration<?> e = properties.propertyNames();
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                String value = properties.getProperty(key);
                whiteListContextPaths.add(value);
            }
        } catch (IOException e) {
            log.error("Error occurred while reading integrator-whitelist.properties file");
        }
    }
}

