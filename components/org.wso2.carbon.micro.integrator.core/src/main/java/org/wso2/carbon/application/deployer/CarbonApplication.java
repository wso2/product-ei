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
package org.wso2.carbon.application.deployer;

import org.wso2.carbon.application.deployer.config.ApplicationConfiguration;

import java.io.File;


public class CarbonApplication {

    public static final String P2_REPO = "p2-repo";

    private String appName;
    private String extractedPath;
    private String appFilePath;
    private String appVersion;
    private boolean deploymentCompleted;

    private ApplicationConfiguration appConfig;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setAppVersion(String appVersion){
        this.appVersion = appVersion;
    }

    public String getAppVersion(){
        return appVersion;
    }

    public String getAppNameWithVersion() {
        if (getAppName() != null) {
            if (getAppVersion() != null) {
                return getAppName() + "_" + getAppVersion();
            }else{
                return getAppName();
            }
        }else{
            return null;
        }
    }

    /**
     * Always retruns the path with a '/' at the end
     * @return - extracted path of the application
     */
    public String getExtractedPath() {
        return extractedPath;
    }

    public void setExtractedPath(String extractedPath) {
        if (!extractedPath.endsWith(File.separator)) {
            extractedPath = extractedPath + File.separator;
        }
        this.extractedPath = extractedPath;
    }

    public ApplicationConfiguration getAppConfig() {
        return appConfig;
    }

    public void setAppConfig(ApplicationConfiguration appConfig) {
        this.appConfig = appConfig;
    }

    public String getAppFilePath() {
        return appFilePath;
    }

    public void setAppFilePath(String appFilePath) {
        this.appFilePath = appFilePath;
    }

    public boolean isDeploymentCompleted() {
        return deploymentCompleted;
    }

    public void setDeploymentCompleted(boolean deploymentCompleted) {
        this.deploymentCompleted = deploymentCompleted;
    }
}

