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

/**
 * All constants related to app deployer
 */
public class AppDeployerConstants {

    public static final String WSO2_APP_NAME_HEADER = "WSO2-Application-Name";
    public static final String PARENT_APP_HEADER = "ParentApplication";
    public static final String WSO2_APP_DEPLOYER_HEADER = "WSO2-Application-Deployer";
    public static final String DEFAULT_APP = "Default Application";

    //registry constants
    public static final String ROOT = "repository/";
    public static final String APPLICATIONS = ROOT + "applications/";
    public static final String APP_DEPENDENCIES = "/dependencies/";
    public static final String APP_ARTIFACTS_XML = "/artifacts-xml/";
    public static final String ARTIFACT_XML = "/artifact-xml/";
    public static final String REG_CONFIG_XML = "/regconfig-xml/";

    //registry path mapping constants
    public static final String REG_PATH_MAPPING = "/repository/carbonapps/path_mapping/";
    public static final String REG_PATH_MAPPING_RESOURCE = "resource";
    public static final String REG_PATH_MAPPING_RESOURCE_ATTR_PATH = "path";
    public static final String REG_PATH_MAPPING_RESOURCE_TARGET = "target";
    public static final String REG_GAR_PATH_MAPPING = "/repository/carbonapps/gar_mapping/";
    public static final String REG_GAR_MEDIATYPE = "application/vnd.wso2.governance-archive";
    public static final String REG_GAR_PATH_MAPPING_RESOURCE_TARGET = "target";

    public static final String RESOURCES_DIR = "resources";
    public static final String META_DIR = ".meta";
    public static final String META_FILE_PREFIX = "~";
    public static final String META_FILE_POSTFIX = ".xml";

    public static final String NAME = "name";
    public static final String TYPE = "type";
    public static final String LAST_UPDATED_TIME = "last.updated.time";
    public static final String HASH_VALUE = "capp.hash.value";
    public static final String FILE_NAME = "file.name";
    public static final String APP_FILE_PATH = "app.file.path";
    public static final String RUNTIME_OBJECT_NAME = "runtime.object.name";
    public static final String APP_VERSION = "app.version";

    public static final String ARTIFACT_VERSION = "version";
    public static final String ARTIFACT_SERVER_ROLE = "server.role";

    public static final String CARBON_SERVER_ROLE = "ServerRoles.Role";
    public static final String SERVER_ROLES_CMD_OPTION = "serverRoles";

    public static final String REPOSITORY = "repository";
    public static final String CARBON_APPS = "carbonapps";
    public static final String WORK_DIR = "work";

    public static final String REQ_FEATURES_XML = "required-features.xml";

    // artifact types
    public static final String CARBON_APP_TYPE = "carbon/application";

    // meta constants
    public static final String META_MEDIA_TYPE = "mediaType";

    // required-features.xml stuff
    public static final String FEATURE_SET = "featureSet";
    public static final String FEATURE = "feature";
    public static final String ARTIFACT_TYPE = "artifactType";

    public static final String DEPLOYMENT_STATUS_DEPLOYED = "Deployed";
    public static final String DEPLOYMENT_STATUS_PENDING = "Pending";
    public static final String DEPLOYMENT_STATUS_FAILED = "Failed";
}
