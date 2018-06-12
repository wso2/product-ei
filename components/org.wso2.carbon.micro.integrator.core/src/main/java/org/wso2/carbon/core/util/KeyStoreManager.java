/*
*  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.core.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.CarbonException;
import org.wso2.carbon.base.api.ServerConfigurationService;
import org.wso2.carbon.core.RegistryResources;
import org.wso2.carbon.micro.integrator.core.internal.CarbonCoreDataHolder;
import org.wso2.carbon.registry.core.Resource;
import org.wso2.carbon.registry.core.service.RegistryService;
import org.wso2.carbon.utils.CarbonUtils;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The purpose of this class is to centrally manage the key stores.
 * Load key stores only once.
 * Reloading them over and over result a in a performance penalty.
 */
public class KeyStoreManager {

    private KeyStore primaryKeyStore = null;
    private KeyStore registryKeyStore = null;
    private KeyStore internalKeyStore = null;
    private static ConcurrentHashMap<String, KeyStoreManager> mtKeyStoreManagers =
            new ConcurrentHashMap<String, KeyStoreManager>();
    private static Log log = LogFactory.getLog(KeyStoreManager.class);

    private int tenantId = MultitenantConstants.SUPER_TENANT_ID;

    private ServerConfigurationService serverConfigService;

    private RegistryService registryService;

    /**
     * Private Constructor of the KeyStoreManager
     *
     * @param tenantId
     * @param serverConfigService
     * @param registryService
     */
    private KeyStoreManager(int tenantId, ServerConfigurationService serverConfigService,
                            RegistryService registryService) {
        this.serverConfigService = serverConfigService;
        this.registryService = registryService;
        this.tenantId = tenantId;
    }

    private KeyStoreManager(int tenantId, ServerConfigurationService serverConfigService) {
        this.serverConfigService = serverConfigService;
        this.tenantId = tenantId;
    }

    public ServerConfigurationService getServerConfigService() {
        return serverConfigService;
    }

    public RegistryService getRegistryService() {
        return registryService;
    }

    /**
     * Get a KeyStoreManager instance for that tenant. This method will return an KeyStoreManager
     * instance if exists, or creates a new one. Only use this at runtime, or else,
     * use KeyStoreManager#getInstance(UserRegistry, ServerConfigurationService).
     *
     * @param tenantId id of the corresponding tenant
     * @return KeyStoreManager instance for that tenant
     */
    public static KeyStoreManager getInstance(int tenantId) {
        return getInstance(tenantId, CarbonCoreDataHolder.getInstance().
                getServerConfigurationService(), CryptoUtil.lookupRegistryService());
    }

    public static KeyStoreManager getInstance(int tenantId,
                                              ServerConfigurationService serverConfigService,
                                              RegistryService registryService) {
        CarbonUtils.checkSecurity();
        String tenantIdStr = Integer.toString(tenantId);
        if (!mtKeyStoreManagers.containsKey(tenantIdStr)) {
            mtKeyStoreManagers.put(tenantIdStr, new KeyStoreManager(tenantId,
                    serverConfigService, registryService));
        }
        return mtKeyStoreManagers.get(tenantIdStr);
    }

    public static KeyStoreManager getInstance(int tenantId,
                                              ServerConfigurationService serverConfigService) {
        CarbonUtils.checkSecurity();
        String tenantIdStr = Integer.toString(tenantId);
        if (!mtKeyStoreManagers.containsKey(tenantIdStr)) {
            mtKeyStoreManagers.put(tenantIdStr, new KeyStoreManager(tenantId,
                    serverConfigService));
        }
        return mtKeyStoreManagers.get(tenantIdStr);
    }
    /**
     * Get the key store object for the given key store name
     *
     * @param keyStoreName key store name
     * @return KeyStore object
     * @throws Exception If there is not a key store with the given name
     */
    public KeyStore getKeyStore(String keyStoreName) throws Exception {
            return getPrimaryKeyStore();
    }

    /**
     * This method loads the private key of a given key store
     *
     * @param keyStoreName name of the key store
     * @param alias        alias of the private key
     * @return private key corresponding to the alias
     */
    public Key getPrivateKey(String keyStoreName, String alias) {
        try {
                return getDefaultPrivateKey();

        } catch (Exception e) {
            log.error("Error loading the private key from the key store : " + keyStoreName);
            throw new SecurityException("Error loading the private key from the key store : " +
                    keyStoreName, e);
        }
    }

    /**
     * Get the key store password of the given key store resource
     *
     * @param resource key store resource
     * @return password of the key store
     * @throws Exception Error when reading the registry resource of decrypting the password
     */
    public String getPassword(Resource resource) throws Exception {
        CryptoUtil cryptoUtil = CryptoUtil.getDefaultCryptoUtil();
        String encryptedPassword = resource
                .getProperty(RegistryResources.SecurityManagement.PROP_PRIVATE_KEY_PASS);
        return new String(cryptoUtil.base64DecodeAndDecrypt(encryptedPassword));

    }


    /**
     * Get the key store password for the given key store name.
     * Note:  Caching has been not implemented for this method
     *
     * @param keyStoreName key store name
     * @return KeyStore object
     * @throws Exception If there is not a key store with the given name
     */
    public String getKeyStorePassword(String keyStoreName) throws Exception {

        // TODO need to implement this properly
        return "admin";
    }

    /**
     * Update the key store with the given name using the modified key store object provided.
     *
     * @param name     key store name
     * @param keyStore modified key store object
     * @throws Exception Registry exception or Security Exception
     */
    public void updateKeyStore(String name, KeyStore keyStore) throws Exception {
        ServerConfigurationService config = this.getServerConfigService();

        if (KeyStoreUtil.isPrimaryStore(name)) {
            String file = new File(
                    config
                            .getFirstProperty(RegistryResources.SecurityManagement.SERVER_PRIMARY_KEYSTORE_FILE))
                    .getAbsolutePath();
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);
                String password = config
                        .getFirstProperty(RegistryResources.SecurityManagement.SERVER_PRIMARY_KEYSTORE_PASSWORD);
                keyStore.store(out, password.toCharArray());
            } finally {
                if (out != null) {
                    out.close();
                }
            }
            return;
        }

    }

    /**
     * Load the primary key store, this is allowed only for the super tenant
     *
     * @return primary key store object
     * @throws Exception Carbon Exception when trying to call this method from a tenant other
     *                   than tenant 0
     */
    public KeyStore getPrimaryKeyStore() throws Exception {
        if (tenantId == MultitenantConstants.SUPER_TENANT_ID) {
            if (primaryKeyStore == null) {

                ServerConfigurationService config = this.getServerConfigService();
                String file =
                        new File(config
                                .getFirstProperty(RegistryResources.SecurityManagement.SERVER_PRIMARY_KEYSTORE_FILE))
                                .getAbsolutePath();
                KeyStore store = KeyStore
                        .getInstance(config
                                .getFirstProperty(RegistryResources.SecurityManagement.SERVER_PRIMARY_KEYSTORE_TYPE));
                String password = config
                        .getFirstProperty(RegistryResources.SecurityManagement.SERVER_PRIMARY_KEYSTORE_PASSWORD);
                FileInputStream in = null;
                try {
                    in = new FileInputStream(file);
                    store.load(in, password.toCharArray());
                    primaryKeyStore = store;
                } finally {
                    if (in != null) {
                        in.close();
                    }
                }
            }
            return primaryKeyStore;
        } else {
            throw new CarbonException("Permission denied for accessing primary key store. The primary key store is " +
                    "available only for the super tenant.");
        }
    }

    /**
     * Load the internal key store, this is allowed only for the super tenant
     *
     * @return internal key store object
     * @throws Exception Carbon Exception when trying to call this method from a tenant other
     *                   than tenant 0
     */
    public KeyStore getInternalKeyStore() throws Exception {

        if (tenantId == MultitenantConstants.SUPER_TENANT_ID) {
            if (internalKeyStore == null) {
                ServerConfigurationService config = this.getServerConfigService();
                if (config.
                        getFirstProperty(RegistryResources.SecurityManagement.SERVER_INTERNAL_KEYSTORE_FILE) == null) {
                    return null;
                }
                String file = new File(config
                        .getFirstProperty(RegistryResources.SecurityManagement.SERVER_INTERNAL_KEYSTORE_FILE))
                        .getAbsolutePath();
                KeyStore store = KeyStore.getInstance(config
                        .getFirstProperty(RegistryResources.SecurityManagement.SERVER_INTERNAL_KEYSTORE_TYPE));
                String password = config
                        .getFirstProperty(RegistryResources.SecurityManagement.SERVER_INTERNAL_KEYSTORE_PASSWORD);
                try (FileInputStream in = new FileInputStream(file)) {
                    store.load(in, password.toCharArray());
                    internalKeyStore = store;
                }
            }
            return internalKeyStore;
        } else {
            throw new CarbonException("Permission denied for accessing internal key store. The internal key store is " +
                    "available only for the super tenant.");
        }
    }

    /**
     * Load the register key store, this is allowed only for the super tenant
     *
     * @deprecated use {@link #getPrimaryKeyStore()} instead.
     *
     * @return register key store object
     * @throws Exception Carbon Exception when trying to call this method from a tenant other
     *                   than tenant 0
     */
    @Deprecated
    public KeyStore getRegistryKeyStore() throws Exception {
        if (tenantId == MultitenantConstants.SUPER_TENANT_ID) {
            if (registryKeyStore == null) {

                ServerConfigurationService config = this.getServerConfigService();
                String file =
                        new File(config
                                .getFirstProperty(RegistryResources.SecurityManagement.SERVER_REGISTRY_KEYSTORE_FILE))
                                .getAbsolutePath();
                KeyStore store = KeyStore
                        .getInstance(config
                                .getFirstProperty(RegistryResources.SecurityManagement.SERVER_REGISTRY_KEYSTORE_TYPE));
                String password = config
                        .getFirstProperty(RegistryResources.SecurityManagement.SERVER_REGISTRY_KEYSTORE_PASSWORD);
                FileInputStream in = null;
                try {
                    in = new FileInputStream(file);
                    store.load(in, password.toCharArray());
                    registryKeyStore = store;
                } finally {
                    if (in != null) {
                        in.close();
                    }
                }
            }
            return registryKeyStore;
        } else {
            throw new CarbonException("Permission denied for accessing registry key store. The registry key store is" +
                    " available only for the super tenant.");
        }
    }

    /**
     * Get the default private key, only allowed for tenant 0
     *
     * @return Private key
     * @throws Exception Carbon Exception for tenants other than tenant 0
     */
    public PrivateKey getDefaultPrivateKey() throws Exception {
        if (tenantId == MultitenantConstants.SUPER_TENANT_ID) {
            ServerConfigurationService config = this.getServerConfigService();
            String password = config
                    .getFirstProperty(RegistryResources.SecurityManagement.SERVER_PRIMARY_KEYSTORE_PASSWORD);
            String alias = config
                    .getFirstProperty(RegistryResources.SecurityManagement.SERVER_PRIMARY_KEYSTORE_KEY_ALIAS);
            return (PrivateKey) primaryKeyStore.getKey(alias, password.toCharArray());
        }
        throw new CarbonException("Permission denied for accessing primary key store. The primary key store is " +
                "available only for the super tenant.");
    }

    /**
     * Get default pub. key
     *
     * @return Public Key
     * @throws Exception Exception Carbon Exception for tenants other than tenant 0
     */
    public PublicKey getDefaultPublicKey() throws Exception {
        if (tenantId == MultitenantConstants.SUPER_TENANT_ID) {
            ServerConfigurationService config = this.getServerConfigService();
            String alias = config
                    .getFirstProperty(RegistryResources.SecurityManagement.SERVER_PRIMARY_KEYSTORE_KEY_ALIAS);
            return (PublicKey) primaryKeyStore.getCertificate(alias).getPublicKey();
        }
        throw new CarbonException("Permission denied for accessing primary key store. The primary key store is " +
                "available only for the super tenant.");
    }

    /**
     * Get the private key password
     *
     * @return private key password
     * @throws CarbonException Exception Carbon Exception for tenants other than tenant 0
     */
    public String getPrimaryPrivateKeyPasssword() throws CarbonException {
        if (tenantId == MultitenantConstants.SUPER_TENANT_ID) {
            ServerConfigurationService config = this.getServerConfigService();
            return config
                    .getFirstProperty(RegistryResources.SecurityManagement.SERVER_PRIMARY_KEYSTORE_PASSWORD);
        }
        throw new CarbonException("Permission denied for accessing primary key store. The primary key store is " +
                "available only for the super tenant.");
    }

    /**
     * This method is used to load the default public certificate of the primary key store
     *
     * @return Default public certificate
     * @throws Exception Permission denied for accessing primary key store
     */
    public X509Certificate getDefaultPrimaryCertificate() throws Exception {
        if (tenantId == MultitenantConstants.SUPER_TENANT_ID) {
            ServerConfigurationService config = this.getServerConfigService();
            String alias = config
                    .getFirstProperty(RegistryResources.SecurityManagement.SERVER_PRIMARY_KEYSTORE_KEY_ALIAS);
            return (X509Certificate) getPrimaryKeyStore().getCertificate(alias);
        }
        throw new CarbonException("Permission denied for accessing primary key store. The primary key store is " +
                "available only for the super tenant.");
    }



    public KeyStore loadKeyStoreFromFileSystem(String keyStorePath, String password, String type) {
        CarbonUtils.checkSecurity();
        String absolutePath = new File(keyStorePath).getAbsolutePath();
        FileInputStream inputStream = null;
        try {
            KeyStore store = KeyStore.getInstance(type);
            inputStream = new FileInputStream(absolutePath);
            store.load(inputStream, password.toCharArray());
            return store;
        } catch (Exception e) {
            String errorMsg = "Error loading the key store from the given location.";
            log.error(errorMsg);
            throw new SecurityException(errorMsg, e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                log.warn("Error when closing the input stream.", e);
            }
        }
    }
}
