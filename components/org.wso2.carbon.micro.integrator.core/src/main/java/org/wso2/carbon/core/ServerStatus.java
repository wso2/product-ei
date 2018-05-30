/*                                                                             
 * Copyright 2004,2005 The Apache Software Foundation.                         
 *                                                                             
 * Licensed under the Apache License, Version 2.0 (the "License");             
 * you may not use this file except in compliance with the License.            
 * You may obtain a copy of the License at                                     
 *                                                                             
 *      http://www.apache.org/licenses/LICENSE-2.0                             
 *                                                                             
 * Unless required by applicable law or agreed to in writing, software         
 * distributed under the License is distributed on an "AS IS" BASIS,           
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.    
 * See the License for the specific language governing permissions and         
 * limitations under the License.                                              
 */
package org.wso2.carbon.core;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;

/**
 * The status in which this Carbon instance can currently be in
 */
public final class ServerStatus {
    /**
     * Server is starting
     */
    private static final String STATUS_STARTING = "STARTING";

    /**
     * Server is running
     */
    public static final String STATUS_RUNNING = "RUNNING";

    /**
     * Server has switched to maintenace mode
     */
    public static final String STATUS_IN_MAINTENANCE = "IN_MAINTENANCE";

    /**
     * Server is restarting
     */
    public static final String STATUS_RESTARTING = "RESTARTING";

    /**
     * Server is shutting down
     */
    public static final String STATUS_SHUTTING_DOWN = "SHUTTING_DOWN";

    /**
     * The current status of this WSAS instance
     */
    private static final String CURRENT_SERVER_STATUS = "local_current.server.status";

    private ServerStatus() {
    }

    /**
     * Get the current server status
     *
     * @return The current server status
     * @throws AxisFault If an error occurs while getting the ConfigurationContext
     */
    public static String getCurrentStatus() {
        ConfigurationContext configCtx =
                CarbonConfigurationContextFactory.getConfigurationContext();
        String currentStatus = (String) configCtx.getProperty(CURRENT_SERVER_STATUS);
        if (currentStatus == null) {
            configCtx.setProperty(CURRENT_SERVER_STATUS, STATUS_STARTING);
            return STATUS_STARTING;
        }
        return currentStatus;
    }

    /**
     * Set server to running mode
     *
     * @throws AxisFault If an error occurs while getting the ConfigurationContext
     */
    public static void setServerRunning() throws AxisFault {
        ConfigurationContext configCtx =
                CarbonConfigurationContextFactory.getConfigurationContext();
        configCtx.setProperty(CURRENT_SERVER_STATUS, STATUS_RUNNING);
    }

    /**
     * Set server to shutting-down mode
     *
     * @throws AxisFault If an error occurs while getting the ConfigurationContext
     */
    public static void setServerShuttingDown() throws AxisFault {
        ConfigurationContext configCtx =
                CarbonConfigurationContextFactory.getConfigurationContext();
        configCtx.setProperty(CURRENT_SERVER_STATUS, STATUS_SHUTTING_DOWN);
    }

    /**
     * Set server to restarting-down mode
     *
     * @throws AxisFault If an error occurs while getting the ConfigurationContext
     */
    public static void setServerRestarting() throws AxisFault {
        ConfigurationContext configCtx =
                CarbonConfigurationContextFactory.getConfigurationContext();
        configCtx.setProperty(CURRENT_SERVER_STATUS, STATUS_RESTARTING);
    }

    /**
     * Set server to maintenace mode
     *
     * @throws AxisFault If an error occurs while getting the ConfigurationContext
     */
    public static void setServerInMaintenance() throws AxisFault {
        ConfigurationContext configCtx =
                CarbonConfigurationContextFactory.getConfigurationContext();
        configCtx.setProperty(CURRENT_SERVER_STATUS, STATUS_IN_MAINTENANCE);
    }
}
