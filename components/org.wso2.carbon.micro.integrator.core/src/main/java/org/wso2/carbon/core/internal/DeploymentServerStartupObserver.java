/*
 * Copyright 2005-2014 WSO2, Inc. (http://wso2.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.core.internal;

import org.apache.axis2.engine.AxisConfigurator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.core.CarbonAxisConfigurator;
import org.wso2.carbon.core.ServerStartupObserver;
import org.wso2.carbon.utils.ConfigurationContextService;

/**
 * This will call all registered deployers during the server start-up.
 *
 * @since 4.3.0
 */
public class DeploymentServerStartupObserver implements ServerStartupObserver {

    private static Log log = LogFactory.getLog(DeploymentServerStartupObserver.class);

    @Override
    public void completedServerStartup() {
        //Do nothing
    }

    @Override
    public void completingServerStartup() {
        //Any exceptions delegated from here will cause the server to not start.
        //Artifact deployment is not critical. So, we will catch it, and log it.
        try {
            log.debug("Invoke registered deployers");
            PrivilegedCarbonContext carbonContext = PrivilegedCarbonContext.getThreadLocalCarbonContext();
            ConfigurationContextService configurationContextService = (ConfigurationContextService) carbonContext.getOSGiService(ConfigurationContextService.class);
            AxisConfigurator axisConfigurator = configurationContextService.getServerConfigContext().getAxisConfiguration().getConfigurator();
            if (axisConfigurator instanceof CarbonAxisConfigurator) {
                ((CarbonAxisConfigurator) axisConfigurator).deployServices();
            }
        } catch (Exception e) {
            log.error("Runtime exception while deploying artifacts ", e);
        }
    }
}