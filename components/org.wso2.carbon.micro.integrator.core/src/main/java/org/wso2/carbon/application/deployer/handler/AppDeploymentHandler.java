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
package org.wso2.carbon.application.deployer.handler;

import org.apache.axis2.deployment.DeploymentException;
import org.apache.axis2.engine.AxisConfiguration;
import org.wso2.carbon.application.deployer.CarbonApplication;

/**
 * This is the common interface for all App deployers. Different deployers deploy different
 * artifacts..
 */

public interface AppDeploymentHandler {

    /**
     * Deploy the artifacts which can be deployed through this deployer.
     * @param carbonApp - store info in this object after deploying
     * @param axisConfig - AxisConfiguration of the current tenant
     */
    void deployArtifacts(CarbonApplication carbonApp, AxisConfiguration axisConfig) throws DeploymentException;

    /**
     * Delete the artifacts which can be deleted through this deployer.
     * @param carbonApp - all information about the existing artifacts are in this instance
     * @param axisConfig - AxisConfiguration of the current tenant
     */
    void undeployArtifacts(CarbonApplication carbonApp, AxisConfiguration axisConfig) throws DeploymentException;

}
