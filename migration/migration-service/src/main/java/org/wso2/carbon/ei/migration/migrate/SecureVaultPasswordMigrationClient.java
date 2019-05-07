/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.ei.migration.migrate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.ei.migration.internal.EIMigrationServiceDataHolder;
import org.wso2.carbon.registry.core.Resource;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.core.session.UserRegistry;
import org.wso2.carbon.registry.core.utils.RegistryUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Decrypt passwords in RSA algorithm and decrypts passwords according to provided algorithm and migrate the passwords.
 */
public class SecureVaultPasswordMigrationClient {
    private static final String SECURE_VAULT_PATH = MigrationConstants.SECURE_VAULT_PATH;
    private static final Log log = LogFactory.getLog(SecureVaultPasswordMigrationClient.class);
    private UserRegistry userRegistry;
    private Resource userRegistryResource;

    /**
     * Default constructor
     *
     * @throws RegistryException
     */
    public SecureVaultPasswordMigrationClient() throws RegistryException {
        String adminUserName = MigrationClientConfig.getInstance()
                                                    .getMigrationConfiguration()
                                                    .getProperty(MigrationConstants.ADMIN_USERNAME);
        if (adminUserName.isEmpty()) {
            throw new MigrationClientException("Invalid admin username");
        }

        this.userRegistry = EIMigrationServiceDataHolder.getRegistryService().getRegistry(adminUserName);

        this.userRegistryResource = userRegistry.get(SECURE_VAULT_PATH);
    }

    /**
     * Decrypt passwords using RSA and encrypt passwords and migrate passwords
     */
    public void migratePasswords() {
        Properties userRegistryResourceProperties = userRegistryResource.getProperties();
        Map<String, String> secureVaultProperties = getSecureVaultProperties(userRegistryResourceProperties);
        Map<String, String> decryptedPasswords = getDecryptedPasswords(secureVaultProperties);
        Map<String, String> encryptedPasswords = getEncryptedPasswords(decryptedPasswords);
        updatePasswords(encryptedPasswords);
    }

    /**
     * update passwords in registry
     *
     * @param encryptedPasswords encrypted passwords map
     */
    private void updatePasswords(Map<String, String> encryptedPasswords) {
        log.info("Initiating password migration");
        if (RegistryUtils.isRegistryReadOnly(userRegistry.getRegistryContext())) {
            return;
        }
        for (Map.Entry<String, String> entry : encryptedPasswords.entrySet()) {
            removeProperty(entry.getKey());
            updateProperty(entry.getKey(), entry.getValue());
        }
    }

    /**
     * remove secure vault password property
     *
     * @param name property name
     */
    private void removeProperty(String name) {
        log.info("Removing old passwords.");
        try {
            Resource resource = userRegistry.get(SECURE_VAULT_PATH);
            resource.removeProperty(name);
            userRegistry.put(resource.getPath(), resource);
            resource.discard();
        } catch (RegistryException e) {
            throw new MigrationClientException("Error occurred while removing property", e);
        }
    }

    /**
     * Method to update property
     *
     * @param name property name
     * @param value property value
     */
    private void updateProperty(String name, String value) {
        log.info("Inserting new passwords.");
        try {
            Resource resource = userRegistry.get(SECURE_VAULT_PATH);
            resource.addProperty(name, value);
            userRegistry.put(resource.getPath(), resource);
            resource.discard();
        } catch (RegistryException e) {
            throw new MigrationClientException("Error occurred while updating property", e);
        }
    }

    /**
     * filter registry properties and extract only system properties
     *
     * @param userRegistryResourceProperties resource properties
     * @return secure vault properties
     */
    private Map<String, String> getSecureVaultProperties(Properties userRegistryResourceProperties) {
        Map<String, String> systemProperties = new HashMap<>();

        Set<Object> keys = userRegistryResourceProperties.keySet();
        for (Object key: keys){
            if (!key.toString().startsWith("registry")) {
                systemProperties.put(key.toString(), userRegistryResource.getProperty(key.toString()));
            }
        }
        return systemProperties;
    }

    /**
     * decrypt  passwords using RSA algorithm
     *
     * @param secureVaultProperties secure vault property-value map
     * @return decrypted passwords
     */
    private Map<String, String> getDecryptedPasswords(Map<String, String> secureVaultProperties) {
        log.info("Initiating Password decryption");
        Map<String, String> decryptedPasswords = new HashMap<>();
        PasswordDecryptor passwordDecryptor = new PasswordDecryptor();

        for (Map.Entry<String, String> entry : secureVaultProperties.entrySet()) {
            String decryptedPassword = passwordDecryptor.decrypt(entry.getValue());
            decryptedPasswords.put(entry.getKey(), decryptedPassword);
        }
        return decryptedPasswords;
    }

    /**
     * encrypt passwords
     *
     * @param passwords password key-value map
     * @return encrypted passwords
     */
    private Map<String, String> getEncryptedPasswords(Map<String, String> passwords) {
        log.info("Initiating Password encryption");
        Map<String, String> encryptedPasswords = new HashMap<>();
        PasswordEncryptor passwordEncryptor = new PasswordEncryptor();

        for (Map.Entry<String, String> entry : passwords.entrySet()) {
            String encryptedPassword = passwordEncryptor.encrypt(entry.getValue());
            encryptedPasswords.put(entry.getKey(), encryptedPassword);
        }
        return encryptedPasswords;
    }
}
