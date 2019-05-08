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
import org.wso2.securevault.CipherFactory;
import org.wso2.securevault.CipherOperationMode;
import org.wso2.securevault.DecryptionProvider;
import org.wso2.securevault.EncodingType;
import org.wso2.securevault.definition.CipherInformation;
import org.wso2.securevault.definition.IdentityKeyStoreInformation;
import org.wso2.securevault.definition.KeyStoreInformationFactory;
import org.wso2.securevault.keystore.IdentityKeyStoreWrapper;

import java.util.Properties;

/**
 * Decrypts passwords using RSA algorithm.
 */
public class PasswordDecryptor {
    private static final Log log = LogFactory.getLog(PasswordDecryptor.class);

    private CipherInformation cipherInformation;
    private IdentityKeyStoreWrapper identityKeyStoreWrapper;

    /**
     * Default constructor
     */
    protected PasswordDecryptor() {
        this.cipherInformation = getCipherInformation();
        this.identityKeyStoreWrapper = getIdentityKeyStoreWrapper();
    }

    /**
     * Decrypt encrypted string
     *
     * @param encryptedString encrypted string
     * @return decrypted string
     */
    public String decrypt(String encryptedString) {
        DecryptionProvider baseCipher = CipherFactory.createCipher(cipherInformation, identityKeyStoreWrapper);
        return new String(baseCipher.decrypt(encryptedString.trim().getBytes()));
    }

    /**
     * get cipher information
     *
     * @return cipher information
     */
    private CipherInformation getCipherInformation() {
        CipherInformation cipherInformation = new CipherInformation();
        cipherInformation.setAlgorithm(MigrationConstants.RSA);
        cipherInformation.setCipherOperationMode(CipherOperationMode.DECRYPT);
        cipherInformation.setInType(EncodingType.BASE64);
        return cipherInformation;
    }

    /**
     * get identity key store wrapper
     *
     * @return identity key store wrapper
     */
    private IdentityKeyStoreWrapper getIdentityKeyStoreWrapper() {
        Properties migrationConfigs = MigrationClientConfig.getInstance().getMigrationConfiguration();
        IdentityKeyStoreInformation identityKeyStoreInformation = KeyStoreInformationFactory
                .createIdentityKeyStoreInformation(migrationConfigs);

        if (identityKeyStoreInformation != null) {
            String identityKeyPass = migrationConfigs.getProperty(MigrationConstants.KEYSTORE_PASSWORD);
            IdentityKeyStoreWrapper identityKeyStoreWrapper = new IdentityKeyStoreWrapper();
            identityKeyStoreWrapper.init(identityKeyStoreInformation, identityKeyPass);
            return identityKeyStoreWrapper;
        } else {
            throw new MigrationClientException("Error occurred while loading identity key store information");
        }
    }
}
