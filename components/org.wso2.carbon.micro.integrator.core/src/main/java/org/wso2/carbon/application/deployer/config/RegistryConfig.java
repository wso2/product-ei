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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * This will hold the registry resources config file declarations. This can include resources,
 * collections and associations
 */
public class RegistryConfig {

    public static final String FILE = "file";
    public static final String ITEM = "item";
    public static final String DUMP = "dump";
    public static final String COLLECTION = "collection";
    public static final String ASSOCIATION = "association";
    public static final String PATH = "path";
    public static final String DIRECTORY = "directory";
    public static final String SOURCE_PATH = "sourcePath";
    public static final String TARGET_PATH = "targetPath";
    public static final String TYPE = "type";
    public static final String REGISTRY_TYPE = "registry-type";
    public static final String MEDIA_TYPE = "mediaType";

    // supported registry types
    public static final String LOCAL_REGISTRY = "local";
    public static final String CONFIG_REGISTRY = "config";
    public static final String GOVERNANCE_REGISTRY = "governance";

    private Log log = LogFactory.getLog(RegistryConfig.class);

    private List<Resourse> resources;
    private List<Dump> dumps;
    private List<Collection> collections;
    private List<Association> associations;

    private String extractedPath;
    private String parentArtifactName;
    private String configFileName;
    private String appName;

    public RegistryConfig() {
        resources = new ArrayList<Resourse>();
        dumps = new ArrayList<Dump>();
        collections = new ArrayList<Collection>();
        associations = new ArrayList<Association>();
    }

    /**
     * Add a resource which belongs to the artifact represented by this component.
     *
     * @param path    - path at which this resource should be stored
     * @param content - name of the file to store as a resource
     * @param regType - type of registry to store the resource (local, config or governance)
     * @param mediaType - mediaType of the registry resource
     */
    public void addResource(String path, String content, String regType, String mediaType) {
        if (path == null || path.length() == 0) {
            log.error("Resource path not found");
            return;
        }
        if (content == null || content.length() == 0) {
            log.error("Content name of the resource not found");
            return;
        }
        resources.add(new Resourse(path, content, regType, mediaType));
    }

    public List<Collection> getCollections() {
        return collections;
    }

    /**
     * Add a dump which belongs to the artifact represented by this component.
     *
     * @param path    - path at which the dump should be stored
     * @param content - name of the dump file
     * @param regType - type of registry to store the resource (local, config or governance)
     */
    public void addDump(String path, String content, String regType) {
        if (path == null || path.length() == 0) {
            log.error("Dump path not found");
            return;
        }
        if (content == null || content.length() == 0) {
            log.error("Content name of the dump not found");
            return;
        }
        dumps.add(new Dump(path, content, regType));
    }

    /**
     * Add a directory and all it's contents which belongs to the artifact represented by
     * this component.
     *
     * @param path      - path at which this collection should be stored
     * @param directory - name of the root directory to store as a resource
     * @param regType   - type of registry to store the resource (local, config or governance)
     */
    public void addCollection(String path, String directory, String regType) {
        if (path == null || path.length() == 0) {
            log.error("Collection path not found");
            return;
        }
        if (directory == null || directory.length() == 0) {
            log.error("Directory name not found");
            return;
        }
        collections.add(new Collection(path, directory, regType));
    }

    public List<Association> getAssociations() {
        return associations;
    }

    /**
     * Add an association between two registry resources
     *
     * @param sourcePath - source path of the association
     * @param targetPath - target path of the association
     * @param assType - type of the association
     * @param regType - type of registry to add the association (local, config or governance)
     */
    public void addAssociation(String sourcePath, String targetPath,
                               String assType, String regType) {
        if (sourcePath == null || sourcePath.length() == 0) {
            log.error("SourcePath of the association not found");
            return;
        }
        if (targetPath == null || targetPath.length() == 0) {
            log.error("TargetPath of the association not found");
            return;
        }
        if (assType == null || assType.length() == 0) {
            log.error("Type of the association not found");
            return;
        }
        associations.add(new Association(sourcePath, targetPath, assType, regType));
    }

    public List<Resourse> getResources() {
        return resources;
    }

    public List<Dump> getDumps() {
        return dumps;
    }

    public String getExtractedPath() {
        return extractedPath;
    }

    public void setExtractedPath(String extractedPath) {
        this.extractedPath = extractedPath;
    }

    public String getParentArtifactName() {
        return parentArtifactName;
    }

    public void setParentArtifactName(String parentArtifactName) {
        this.parentArtifactName = parentArtifactName;
    }

    public String getConfigFileName() {
        return configFileName;
    }

    public void setConfigFileName(String configFileName) {
        this.configFileName = configFileName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    /**
     * This class represents a resource in a the reg-config.xml
     */
    public class Resourse {
        private String path;
        private String fileName;
        private String registryType;
        private String mediaType;

        public Resourse(String path, String fileName, String regType, String mediaType) {
            this.path = path;
            this.fileName = fileName;
            this.registryType = regType;
            this.mediaType = mediaType;
        }

        public String getPath() {
            return path;
        }

        public String getFileName() {
            return fileName;
        }

        public String getRegistryType() {
            return registryType;
        }

		public void setMediaType(String mediaType) {
			this.mediaType = mediaType;
		}

		public String getMediaType() {
			return mediaType;
		}
    }

    /**
     * This class represents a dump in a the reg-config.xml
     */
    public class Dump {
        private String path;
        private String dumpFileName;
        private String registryType;

        public Dump(String path, String fileName, String regType) {
            this.path = path;
            this.dumpFileName = fileName;
            this.registryType = regType;
        }

        public String getPath() {
            return path;
        }

        public String getDumpFileName() {
            return dumpFileName;
        }

        public String getRegistryType() {
            return registryType;
        }
    }

    /**
     * This class represents a collection in a the reg-config.xml
     */
    public class Collection {
        private String path;
        private String directory;
        private String registryType;

        public Collection(String path, String directory, String regType) {
            this.path = path;
            this.directory = directory;
            this.registryType = regType;
        }

        public String getPath() {
            return path;
        }

        public String getDirectory() {
            return directory;
        }

        public String getRegistryType() {
            return registryType;
        }
    }

    /**
     * This class represents an association in a the reg-config.xml
     */
    public class Association {
        private String sourcePath;
        private String targetPath;
        private String associationType;
        private String registryType;

        public Association(String sourcePath, String targetPath, String assType, String regType) {
            this.sourcePath = sourcePath;
            this.targetPath = targetPath;
            this.associationType = assType;
            this.registryType = regType;
        }

        public String getSourcePath() {
            return sourcePath;
        }

        public String getTargetPath() {
            return targetPath;
        }

        public String getAssociationType() {
            return associationType;
        }

        public String getRegistryType() {
            return registryType;
        }
    }
}
