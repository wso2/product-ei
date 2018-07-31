/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.micro.integrator.security.vault;

import java.io.File;

public class Constants {

    public static final String UTF8 = "UTF-8";
    public static final String CONSOLE_SKIP_STORE_PARAM = "skipStore";
    public static final String CONSOLE_PASS_CHANGE_PARAM = "change";
    public static final String INVALID_OPTION = "Invalid Option!";
    public static final String KEYSTORE_PASSWORD = "keystore.password";
    public static final String CARBON_HOME = "carbon.home";
    public static final String HOME_FOLDER = "home.folder";
    public static final String TRUE = "true";

    public static final String REPOSITORY_DIR = "repository";
    public static final String CONF_DIR = "conf";
    public static final String SECURITY_DIR = "security";
    public static final String DEFAULT_CONFIG_REGISTRY_PATH = "registry" + File.separator + "config" + File.separator;
    public static final String CONFIG_REGISTRY_PARAM = "ConfigRegRoot";
    public static final String REGISTRY = "registry";
    public static final String SECURE_VAULT_PROPERTIES_FILE = "repository" + File.separator + "components" +
            File.separator + "secure-vault" + File.separator + "secure-vault.properties";
    public static final String REGISTRY_PROPERTIES_FILE = "repository" + File.separator + "deployment" +
            File.separator + "server" + File.separator + "synapse-configs" + File.separator + "default" +
            File.separator + "registry.xml";
    public static final String CARBON_CONFIG_FILE = "carbon.xml";
    public static final String CIPHER_TEXT_PROPERTY_FILE = "cipher-text.properties";
    public static final String CIPHER_TOOL_PROPERTY_FILE = "cipher-tool.properties";
    public static final String SECRET_PROPERTY_FILE = "secret-conf.properties";

    public static final String CIPHER_TEXT_PROPERTY_FILE_PROPERTY = "cipher.text.properties.file";
    public static final String CIPHER_TOOL_PROPERTY_FILE_PROPERTY = "cipher.tool.properties.file";
    public static final String CIPHER_STANDALONE_CONFIG_PROPERTY_FILE = "cipher-standalone-config.properties";
    public static final String SECRET_PROPERTY_FILE_PROPERTY = "secret.conf.properties.file";
    public static final String CIPHER_TRANSFORMATION_SYSTEM_PROPERTY = "org.wso2.CipherTransformation";

    public static final class PrimaryKeyStore {
        public static final String PRIMARY_KEY_LOCATION_XPATH = "//Server/Security/KeyStore/Location";
        public static final String PRIMARY_KEY_TYPE_XPATH = "//Server/Security/KeyStore/Type";
        public static final String PRIMARY_KEY_ALIAS_XPATH = "//Server/Security/KeyStore/KeyAlias";

        public static final String PRIMARY_KEY_LOCATION_PROPERTY = "primary.key.location";
        public static final String PRIMARY_KEY_TYPE_PROPERTY = "primary.key.type";
        public static final String PRIMARY_KEY_ALIAS_PROPERTY = "primary.key.alias";
    }

    public static final class SecureVault {
        public static final String SECRET_FILE_LOCATION = "secretRepositories.file.location";
    }
}
