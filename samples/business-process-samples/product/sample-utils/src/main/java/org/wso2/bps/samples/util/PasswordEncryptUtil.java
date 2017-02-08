/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.bps.samples.util;


import org.apache.axiom.om.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

/**
 * This class can be used to encrypt a password
 */
public class PasswordEncryptUtil {
    public static void main(String args[]) throws IOException, NoSuchProviderException, NoSuchAlgorithmException,
            NoSuchPaddingException, KeyStoreException, CertificateException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        if(args.length < 4) {
            /** Print help message */
            System.out.println("Please pass the following parameters for encrypting a given text");
            System.out.println("KeyStorePath KeyAlias KeyPassword TextToBeEncrypted");

        }


        if(args.length == 4) {
            String keyStorePath = args[0];
            String keyAlias = args[1];
            String keyPassword = args[2];
            String textToEncrypt = args[3];
            String keyStoreAbsPath = "";

            /** Check whether keystore path is correct */
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            File keyFile = new File(keyStorePath);
            if(keyFile.exists()) {
            keyStoreAbsPath = keyFile.getCanonicalPath();
            } else {
                System.out.println("Invalid key store path specified");
                return;
            }

            Cipher cipher = Cipher.getInstance("RSA", "BC");
            KeyStore store = KeyStore.getInstance("JKS");

            FileInputStream in = null;
            try {
                in = new FileInputStream(keyFile);
                store.load(in, keyPassword.toCharArray());
            } finally {
                if(in != null) {
                    in.close();
                }
            }

            Certificate[] certs = store.getCertificateChain(keyAlias);
            cipher.init(Cipher.ENCRYPT_MODE, certs[0]);

            byte[] encryptedText = cipher.doFinal(textToEncrypt.getBytes());
            String encodedString = Base64.encode(encryptedText);
            System.out.println(encodedString);

        }
    }
}
