/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * you may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.micro.integrator.core.deployment.internal;

import org.wso2.carbon.micro.integrator.core.deployment.DeploymentService;
import org.wso2.carbon.micro.integrator.core.deployment.application.deployer.CAppDeploymentManager;
import org.wso2.carbon.micro.integrator.core.deployment.artifact.deployer.ArtifactDeploymentManager;

public class DeploymentServiceImpl implements DeploymentService{

    private ArtifactDeploymentManager artifactDeploymentManager;
    private CAppDeploymentManager cAppDeploymentManager;

    public DeploymentServiceImpl(ArtifactDeploymentManager artifactDeploymentManager,
                                 CAppDeploymentManager cAppDeploymentManager) {
        this.artifactDeploymentManager = artifactDeploymentManager;
        this.cAppDeploymentManager = cAppDeploymentManager;
    }

    @Override
    public ArtifactDeploymentManager getArtifactDeploymentManager() {
        return artifactDeploymentManager;
    }

    @Override
    public CAppDeploymentManager getCAppDeploymentManager() {
        return cAppDeploymentManager;
    }
}
