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
package org.wso2.carbon.application.deployer.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Keeps information about a single artifact deployed through an application.
 */
public class Artifact {

    public static final String NAME = "name";
    public static final String TYPE = "type";
    public static final String VERSION = "version";
    public static final String ARTIFACT_XML = "artifact.xml";
    public static final String REG_INFO_XML = "registry-info.xml";

    public static final String ARTIFACT = "artifact";
    public static final String DEPENDENCY = "dependency";
    public static final String SERVER_ROLE = "serverRole";

    public static final String SUB_ARTIFACTS = "subArtifacts";
    public static final String FILE = "file";

    public int unresolvedDepCount = 0;

    private String type;
    private String version;
    private String name;
    private String serverRole;
    private String extractedPath;
    private String runtimeObjectName;
    private String deploymentStatus;

    private List<Dependency> dependencies;
    private List<Artifact> subArtifacts;
    private List<CappFile> files;

    // this will be set only if the artifact is a registry config artifact
    private RegistryConfig regConfig;

    public Artifact() {
        dependencies = new ArrayList<Dependency>();
        subArtifacts = new ArrayList<Artifact>();
        files = new ArrayList<CappFile>();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServerRole() {
        return serverRole;
    }

    public String getExtractedPath() {
        return extractedPath;
    }

    public void setExtractedPath(String extractedPath) {
        this.extractedPath = extractedPath;
    }

    public void setServerRole(String serverRole) {
        this.serverRole = serverRole;
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    public List<Artifact> getSubArtifacts() {
        return subArtifacts;
    }

    public List<CappFile> getFiles() {
        return files;
    }

    public void addDependency(Dependency dep) {
        this.dependencies.add(dep);
        unresolvedDepCount++;
    }

    public void removeDependency(Dependency dep) {
        this.dependencies.remove(dep);
        unresolvedDepCount--;
    }

    public void addFile(CappFile file) {
        this.files.add(file);
    }

    public void addSubArtifact(Artifact subArtifact) {
        this.subArtifacts.add(subArtifact);
    }

    public String getRuntimeObjectName() {
        return runtimeObjectName;
    }

    public void setRuntimeObjectName(String runtimeObjectName) {
        this.runtimeObjectName = runtimeObjectName;
    }

    public RegistryConfig getRegConfig() {
        return regConfig;
    }

    public void setRegConfig(RegistryConfig regConfig) {
        this.regConfig = regConfig;
    }

    public String getDeploymentStatus() {
        return deploymentStatus;
    }

    public void setDeploymentStatus(String deploymentStatus) {
        this.deploymentStatus = deploymentStatus;
    }

    public static class Dependency {
        private String name;
        private String version;
        private String serverRole;
        private Artifact artifact;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public Artifact getArtifact() {
            return artifact;
        }

        public String getServerRole() {
            return serverRole;
        }

        public void setServerRole(String serverRole) {
            this.serverRole = serverRole;
        }

        public void setArtifact(Artifact artifact) {
            this.artifact = artifact;
        }
    }

}
