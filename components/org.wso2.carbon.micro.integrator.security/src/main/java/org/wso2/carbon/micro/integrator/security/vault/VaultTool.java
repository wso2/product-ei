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

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.carbon.micro.integrator.security.vault.utils.KeyStoreUtil;
import org.wso2.carbon.micro.integrator.security.vault.utils.Utils;

import java.io.File;
import java.nio.charset.Charset;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/*
 *  This class contains the main method that the micro integrator secure vault script invokes
 */
public class VaultTool {

    private static boolean storeKeyInFile = true;

    /**
     * This is the main method invoked by the micro integrator secure vault script
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {

        initialize(args);
        String alias = Utils.getValueFromConsole("Please Enter the Secret Alias", false);
        try {
            Cipher cipher = KeyStoreUtil.initializeCipher();
            String encryptedPass = encryptedValue(cipher);
            String configRegRoot;
            String carbonHome = System.getProperty("carbon.home");
            if (!carbonHome.endsWith(File.separator)) {
                carbonHome += File.separator;
            }
            configRegRoot = getConfigRegRoot(carbonHome);
            String registryPath = configRegRoot + Constants.SECURE_VAULT_PROPERTIES_FILE;
            if (storeKeyInFile) {
                Utils.writeKeyToFile(alias, encryptedPass, registryPath);
            } else {
                System.out.println("Encrypted key for alias: " + alias + "\n" + encryptedPass);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * init the mode of operation of secure vault using command line argument
     *
     * @param args command line arguments
     */
    private static void initialize(String[] args) {
        String property;
        for (String arg : args) {
            if (arg.equals("-help")) {
                printHelp();
                System.exit(0);
            } else if (arg.substring(0, 2).equals("-D")) {
                property = arg.substring(2);
                switch (property) {
                    case Constants.CONSOLE_SKIP_STORE_PARAM:
                        storeKeyInFile = false;
                        break;
                    case Constants.CONSOLE_PASS_CHANGE_PARAM:
                        System.setProperty(Constants.CONSOLE_PASS_CHANGE_PARAM, Constants.TRUE);
                        break;
                    default:
                        System.out.println(Constants.INVALID_OPTION);
                        System.exit(-1);
                }
            }
        }
        Utils.setSystemProperties();
    }

    /**
     * print the help on command line
     */
    private static void printHelp() {
        System.out.println("\n---------Secure Vault Help---------\n");
        System.out.println("Secure Vault allows you to store encrypted passwords that are mapped to aliases. " +
                "That is, you can use the aliases instead of the actual passwords in your configuration files for " +
                "better security.\n");
        System.out.println("Options :\n");
        System.out.println("\t-DskipStore\t\t This option allows the user to obtain the encrypted " +
                "string of a password and not store it in the registry\n");
        System.out.println("\t-Dchange\t\t This option would allow user to change the specific password which has" +
                " been secured\n");
    }

    /**
     * encrypt text retrieved from Console
     *
     * @param cipher cipher
     */
    private static String encryptedValue(Cipher cipher) {
        String firstPassword;
        String secondPassword;
        while (true) {
            firstPassword = Utils.getValueFromConsole("Enter Plain Text Value : ", true);
            secondPassword = Utils.getValueFromConsole("Please Enter Value Again : ", true);
            if (!firstPassword.isEmpty() && firstPassword.equals(secondPassword)) {
                if (firstPassword.length() < 5 || firstPassword.length() > 30) {
                    System.out.println("Password must contain 5 to 30 characters," +
                            " Please Re-enter password\n\n");
                    continue;
                }
                return doEncryption(cipher, firstPassword);
            } else {
                System.out.println("Passwords does not match, Please Re-enter password\n\n");
            }
            //the following variables are set to null to avoid an infinite loop
            firstPassword = null;
            secondPassword = null;
        }
    }

    /**
     * get the synapse configuration registry root defined by the user
     *
     * @param carbonHome carbon home directory
     * @return user defined config registry root or the default registry root in case the user haven't defined any
     */
    private static String getConfigRegRoot(String carbonHome) {
        String configRegRoot = carbonHome + Constants.DEFAULT_CONFIG_REGISTRY_PATH;
        File registryConfig = new File(carbonHome + Constants.REGISTRY_PROPERTIES_FILE);
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document document = docBuilder.parse(registryConfig);
            Node n = document.getElementsByTagName(Constants.REGISTRY).item(0);
            NodeList nl = n.getChildNodes();

            for (int i = 0; i < nl.getLength(); i++) {
                Node item = nl.item(i);
                if (item.hasAttributes()) {
                    String name = item.getAttributes().item(0).getNodeValue();
                    if (name != null) {
                        if (name.equalsIgnoreCase(Constants.CONFIG_REGISTRY_PARAM)) {
                            configRegRoot = item.getTextContent();
                            if (!configRegRoot.endsWith(File.separator)) {
                                configRegRoot += File.separator;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error in initiating registry");
            e.printStackTrace();
        }
        if (configRegRoot.startsWith("file:")) {
            configRegRoot = configRegRoot.substring("file:".length());
        }
        return configRegRoot;
    }

    /**
     * encrypt the plain text password
     *
     * @param cipher       cipher
     * @param plainTextPwd plain text password
     * @return encrypted password
     */
    private static String doEncryption(Cipher cipher, String plainTextPwd) {
        String encodedValue;
        try {
            byte[] encryptedPassword = cipher.doFinal(plainTextPwd.getBytes(Charset.forName(Constants.UTF8)));
            encodedValue = DatatypeConverter.printBase64Binary(encryptedPassword);
        } catch (BadPaddingException e) {
            throw new SecureVaultException("Error encrypting password ", e);
        } catch (IllegalBlockSizeException e) {
            throw new SecureVaultException("Error encrypting password ", e);
        }
        System.out.println("\nEncryption is done Successfully\n");
        return encodedValue;
    }
}
