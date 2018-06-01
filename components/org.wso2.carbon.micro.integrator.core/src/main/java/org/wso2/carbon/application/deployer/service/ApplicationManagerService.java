/*
*  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.carbon.application.deployer.service;

import org.apache.axis2.engine.AxisConfiguration;
import org.wso2.carbon.application.deployer.CarbonApplication;
import org.wso2.carbon.application.deployer.handler.AppDeploymentHandler;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * OSGi service interface for the Application Management
 */
public interface ApplicationManagerService {

    /**
     * All app deployers register their instances throgh this method
     *
     * @param handler - app deployer which implements the AppDeploymentHandler interface
     */
    void registerDeploymentHandler(AppDeploymentHandler handler);

    /**
     * Unregister the specified handler if it is already regitered
     *
     * @param handler - input deployer handler
     */
    void unregisterDeploymentHandler(AppDeploymentHandler handler);

    /**
     * Deploy the provided carbon App..
     *
     * @param archPath - carbon app archive path
     * @param axisConfig - AxisConfiguration of the current tenant
     * @throws Exception - error on registry actions
     */
    void deployCarbonApp(String archPath, AxisConfiguration axisConfig) throws Exception;

    /**
     * Undeploy the provided carbon App..
     *
     * @param carbonApp - CarbonApplication instance
     * @param axisConfig - AxisConfiguration of the current tenant
     */
    void undeployCarbonApp(CarbonApplication carbonApp, AxisConfiguration axisConfig);

    /**
     * Returns all deployed carbon apps in the system
     *
     * @param tenantId - tenant id to find cApps
     * @return List of carbon apps
     */
    ArrayList<CarbonApplication> getCarbonApps(String tenantId);

    /**
     *  Get the list of faulty CarbonApplications for the give tenant id. If the list is null,
     *  return an empty Arraylist
     *
     * @param tenantId - tenant id to find faulty cApps
     * @return - list of tenant faulty cApps
     */
    HashMap<String, Exception> getFaultyCarbonApps(String tenantId);

}
