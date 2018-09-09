/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.carbon.micro.integrator.security.vault.utils;

import org.wso2.carbon.micro.integrator.security.vault.SecureVaultException;
import org.wso2.carbon.micro.integrator.security.vault.Constants;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

public class KeyStoreUtil {

    /**
     * Initializes the Cipher
     *
     * @return cipher cipher
     */
    public static Cipher initializeCipher() {
        Cipher cipher;
        String keyStoreFile = System.getProperty(Constants.PrimaryKeyStore.PRIMARY_KEY_LOCATION_PROPERTY);
        String keyType = System.getProperty(Constants.PrimaryKeyStore.PRIMARY_KEY_TYPE_PROPERTY);
        String keyAlias = System.getProperty(Constants.PrimaryKeyStore.PRIMARY_KEY_ALIAS_PROPERTY);
        String password;
        if (System.getProperty(Constants.KEYSTORE_PASSWORD) != null &&
                System.getProperty(Constants.KEYSTORE_PASSWORD).length() > 0) {
            password = System.getProperty(Constants.KEYSTORE_PASSWORD);
        } else {
            password = Utils.getValueFromConsole("Please Enter Primary KeyStore Password of Carbon Server : ", true);
        }
        if (password == null) {
            throw new SecureVaultException("KeyStore password can not be null");
        }

        KeyStore primaryKeyStore = getKeyStore(keyStoreFile, password, keyType);
        try {
            Certificate certs = primaryKeyStore.getCertificate(keyAlias);
            String cipherTransformation = System.getProperty(Constants.CIPHER_TRANSFORMATION_SYSTEM_PROPERTY);
            if (cipherTransformation != null) {
                cipher = Cipher.getInstance(cipherTransformation);
            } else {
                cipher = Cipher.getInstance("RSA/ECB/OAEPwithSHA1andMGF1Padding");
            }
            cipher.init(Cipher.ENCRYPT_MODE, certs);
        } catch (KeyStoreException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            throw new SecureVaultException("Error initializing Cipher ", e);
        }

        System.out.println("\nPrimary KeyStore of Carbon Server is initialized Successfully\n");
        return cipher;
    }

    /**
     * generate keystore object from a given keystore location
     *
     * @param location      the location of the keystore
     * @param storePassword keystore password
     * @param storeType     keystore type
     * @return keystore object
     */
    private static KeyStore getKeyStore(String location, String storePassword, String storeType) {
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(location))) {
            KeyStore keyStore = KeyStore.getInstance(storeType);
            keyStore.load(bufferedInputStream, storePassword.toCharArray());
            return keyStore;
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException e) {
            throw new SecureVaultException("Error loading keyStore from ' " + location + " ' ", e);
        }
    }
}
