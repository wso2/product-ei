/*
* Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
* WSO2 Inc. licenses this file to you under the Apache License,
* Version 2.0 (the "License"); you may not use this file except
* in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.wso2.ei.connector.as2.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.core.util.KeyStoreManager;
import org.wso2.ei.connector.as2.util.AS2Constants;

import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages Keys and Certificates using carbon KeyStoreManager
 */
public class KeyCertManager {
    public static final Log log = LogFactory.getLog(KeyCertManager.class);

    private String keyStoreName = null;
    private KeyStoreManager carbonKeyStoreManager = null;

    /**
     * Initialize the KeyCertManager
     *
     * @param keyStoreName name of the key store as given in EI Management Console; Home > Configure > Keystores > List
     */
    public KeyCertManager(String keyStoreName) {
        this.keyStoreName = keyStoreName;
        this.carbonKeyStoreManager = KeyStoreManager.getInstance(CarbonContext.getThreadLocalCarbonContext().getTenantId());
    }

    /**
     * Take a private key from the key store for a given alias
     *
     * @param alias alias of the private key in the store
     * @return private key
     */
    public PrivateKey getPrivateKey(String alias) {
        Key key = carbonKeyStoreManager.getPrivateKey(keyStoreName, alias);
        return (PrivateKey) key;
    }

    /**
     * Take a certificate from the key store for a given alias
     *
     * @param alias alias of the certificate
     * @return X509Certificate for the alias
     */
    public X509Certificate getCertificate(String alias) throws Exception{
        Certificate cert = carbonKeyStoreManager.getKeyStore(keyStoreName).getCertificate(alias);
        return (X509Certificate) cert;
    }

    /**
     * Return a CertStore with the certificates, mentioned in send_template, to go with the signature
     *
     * @param aliasList string array of certificate aliases
     * @return CertStore
     * @throws InvalidAlgorithmParameterException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws Exception
     */
    public CertStore getSignCertStore(String[] aliasList)
            throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, Exception{

        // list of certificates to go with the sign
        List<Certificate> certList = new ArrayList();

        // Take certificates and add to the list
        for(String alias: aliasList){
            certList.add(carbonKeyStoreManager.getKeyStore(keyStoreName).getCertificate(alias));
        }

        CertStore certStore = CertStore.getInstance("Collection",
                new CollectionCertStoreParameters(certList), AS2Constants.BC);
        return certStore;
    }
}