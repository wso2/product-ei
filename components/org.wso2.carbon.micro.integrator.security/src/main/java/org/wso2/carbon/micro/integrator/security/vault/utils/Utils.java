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
package org.wso2.carbon.micro.integrator.security.vault.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.wso2.carbon.micro.integrator.security.vault.SecureVaultException;
import org.wso2.carbon.micro.integrator.security.vault.Constants;
import org.xml.sax.SAXException;

import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/*
 *  Contains utils required by the secure vault implementation for the micro integrator profile
 */
public class Utils {

    /**
     * Writes a given alias and encrypted password to the given file path
     *
     * @param alias alias of the new user
     * @param encryptedPass encrypted value of the password given by the user
     * @param filePath file path where the alias and password needs to be stored
     */
    public static void writeKeyToFile(String alias, String encryptedPass, String filePath) {
        System.out.println("writing to: " + filePath);
        Properties keys = new Properties();
        File file = new File(filePath);
        File fileParent = file.getParentFile();
        boolean parentExists = fileParent.exists();
        if (!parentExists) {
            if (!fileParent.mkdirs()) {
                String msg = "Error in registry directory structure creation at : " + fileParent.getPath();
                throw new SecureVaultException(msg);
            }
        } else {
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                keys.load(fileInputStream);
                if (keys.containsKey(alias)) {
                    String change = System.getProperty(Constants.CONSOLE_PASS_CHANGE_PARAM);
                    if (change != null && change.equals(Constants.TRUE)) {
                        keys.remove(alias);
                        keys.put(alias, encryptedPass);
                        System.out.println("changed password for alias: " + alias);
                    } else {
                        String msg = "An entry with the alias: " + alias + " already exists";
                        throw new SecureVaultException(msg);
                    }
                }
            } catch (IOException e) {
                String msg = "Error loading properties from a file at : " + filePath;
                throw new SecureVaultException(msg + " Error : " + e.getMessage(), e);
            }
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(file, false)) {
            keys.setProperty(alias, encryptedPass);
            //The parent folder is touched to support versioning done by the registry
            if (parentExists) {
                touchFile(fileParent);
            }
            keys.store(fileOutputStream, "Secure Vault keys for micro integrator");
        } catch (IOException e) {
            String msg = "Error loading properties from a file at : " + filePath;
            throw new SecureVaultException(msg + " Error : " + e.getMessage(), e);
        }
    }

    /**
     * Update last modified time of a given file to the current time
     *
     * @param file file that needs to be modified
     */
    public static void touchFile(File file) {
        long timestamp = System.currentTimeMillis();
        if (!file.setLastModified(timestamp)) {
            System.err.println("Last Modified Time Update Failed");
        }
    }

    /**
     * Retrieve value via the console
     *
     * @param msg        message to display in the inquiry
     * @param isPassword is the requested value a password or not
     * @return value provided by the user
     */
    public static String getValueFromConsole(String msg, boolean isPassword) {
        Console console = System.console();
        if (console != null) {
            if (isPassword) {
                char[] password;
                if ((password = console.readPassword("[%s]", msg)) != null) {
                    return String.valueOf(password);
                }
            } else {
                String value;
                if ((value = console.readLine("[%s]", msg)) != null) {
                    return value;
                }
            }
        }
        throw new SecureVaultException("String cannot be null");
    }

    /**
     * read values from property file
     *
     * @param filePath file path
     * @return Properties properties
     */
    public static Properties loadProperties(String filePath) {
        Properties properties = new Properties();
        File file = new File(filePath);
        if (!file.exists()) {
            return properties;
        }
        try (InputStream inputStream = new FileInputStream(file)) {
            properties.load(inputStream);
        } catch (IOException e) {
            String msg = "Error loading properties from a file at :" + filePath;
            throw new SecureVaultException(msg + " Error : " + e.getMessage());
        }
        return properties;
    }

    /**
     * returns the configuration file
     *
     * @param fileName file name
     * @return File file
     */
    public static String getConfigFilePath(String fileName) {
        String homeFolder = System.getProperty(Constants.HOME_FOLDER);
        Path filePath = Paths.get(homeFolder, fileName);
        if (!Files.exists(filePath)) {
            filePath = Paths.get(fileName);
            if (!Files.exists(filePath)) {
                throw new SecureVaultException("Cannot find file : " + fileName);
            }
        }
        return filePath.toAbsolutePath().toString();
    }

    /**
     * retrieve the value for the given xpath from the file
     *
     * @param element element
     * @param xPath   xpath
     * @return value from xpath
     */
    public static String getValueFromXPath(Element element, String xPath) {
        String nodeValue = null;
        try {
            XPathFactory xpf = XPathFactory.newInstance();
            XPath xp = xpf.newXPath();
            XPathExpression xPathExpression = xp.compile(xPath);
            Node text = (Node) xPathExpression.evaluate(element, XPathConstants.NODE);
            if (text != null) {
                nodeValue = text.getTextContent();
            }
        } catch (XPathExpressionException e) {
            throw new SecureVaultException("Error reading primary key Store details from carbon.xml file ", e);
        }
        return nodeValue;
    }

    /**
     * Set the system properties
     */
    public static void setSystemProperties() {
        String keyStoreFile, keyType, keyAlias, secretConfPropFile, secretConfFile, cipherTextPropFile,
                cipherToolPropFile;
        String homeFolder = System.getProperty(Constants.CARBON_HOME);
        //Verify if this is WSO2 environment
        Path path = Paths.get(homeFolder, Constants.REPOSITORY_DIR, Constants.CONF_DIR, Constants.CARBON_CONFIG_FILE);

        boolean hasConfigInRepository = true;
        if (!Files.exists(path)) {
            //Try WSO2 EI alternate path
            path = Paths.get(homeFolder, Constants.CONF_DIR, Constants.CARBON_CONFIG_FILE);
            hasConfigInRepository = false;
        }

        if (Files.exists(path)) {
            //WSO2 Environment
            try {
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                Document document = docBuilder.parse(path.toAbsolutePath().toString());

                keyStoreFile = Utils.getValueFromXPath(document.getDocumentElement(),
                        Constants.PrimaryKeyStore.PRIMARY_KEY_LOCATION_XPATH);
                keyStoreFile = keyStoreFile.substring((keyStoreFile.indexOf('}')) + 1);
                keyType = Utils.getValueFromXPath(document.getDocumentElement(),
                        Constants.PrimaryKeyStore.PRIMARY_KEY_TYPE_XPATH);
                keyAlias = Utils.getValueFromXPath(document.getDocumentElement(),
                        Constants.PrimaryKeyStore.PRIMARY_KEY_ALIAS_XPATH);

                if (hasConfigInRepository) {
                    secretConfFile = Constants.REPOSITORY_DIR + File.separator + Constants.CONF_DIR + File.separator +
                            Constants.SECURITY_DIR + File.separator + Constants.SECRET_PROPERTY_FILE;
                    cipherTextPropFile = Constants.REPOSITORY_DIR + File.separator + Constants.CONF_DIR + File.separator +
                            Constants.SECURITY_DIR + File.separator + Constants.CIPHER_TEXT_PROPERTY_FILE;
                    cipherToolPropFile = Constants.REPOSITORY_DIR + File.separator + Constants.CONF_DIR + File.separator +
                            Constants.SECURITY_DIR + File.separator + Constants.CIPHER_TOOL_PROPERTY_FILE;
                } else {
                    secretConfFile = Constants.CONF_DIR + File.separator + Constants.SECURITY_DIR + File.separator +
                            Constants.SECRET_PROPERTY_FILE;
                    cipherTextPropFile = Constants.CONF_DIR + File.separator + Constants.SECURITY_DIR + File.separator +
                            Constants.CIPHER_TEXT_PROPERTY_FILE;
                    cipherToolPropFile = Constants.CONF_DIR + File.separator + Constants.SECURITY_DIR + File.separator +
                            Constants.CIPHER_TOOL_PROPERTY_FILE;
                }
                secretConfFile = Paths.get(homeFolder, secretConfFile).toString();

            } catch (ParserConfigurationException e) {
                throw new SecureVaultException(
                        "Error reading primary key Store details from " + Constants.CARBON_CONFIG_FILE + " file ", e);
            } catch (SAXException e) {
                throw new SecureVaultException(
                        "Error reading primary key Store details from " + Constants.CARBON_CONFIG_FILE + " file ", e);
            } catch (IOException e) {
                throw new SecureVaultException(
                        "Error reading primary key Store details from " + Constants.CARBON_CONFIG_FILE + " file ", e);
            }
        } else {
            Path standaloneConfigPath =
                    Paths.get(homeFolder, Constants.CONF_DIR, Constants.CIPHER_STANDALONE_CONFIG_PROPERTY_FILE);
            if (!Files.exists(standaloneConfigPath)) {
                throw new SecureVaultException(
                        "File, " + standaloneConfigPath + " does not exist.");
            }
            Properties standaloneConfigProp = Utils.loadProperties(standaloneConfigPath.toAbsolutePath().toString());
            if (standaloneConfigProp.size() <= 0) {
                throw new SecureVaultException(
                        "File, " + Constants.CIPHER_STANDALONE_CONFIG_PROPERTY_FILE + " cannot be empty");
            }
            keyStoreFile = standaloneConfigProp.getProperty(Constants.PrimaryKeyStore.PRIMARY_KEY_LOCATION_PROPERTY);
            keyType = standaloneConfigProp.getProperty(Constants.PrimaryKeyStore.PRIMARY_KEY_TYPE_PROPERTY);
            keyAlias = standaloneConfigProp.getProperty(Constants.PrimaryKeyStore.PRIMARY_KEY_ALIAS_PROPERTY);

            secretConfFile = standaloneConfigProp.getProperty(Constants.SECRET_PROPERTY_FILE_PROPERTY);
            cipherTextPropFile = standaloneConfigProp.getProperty(Constants.CIPHER_TEXT_PROPERTY_FILE_PROPERTY);
            cipherToolPropFile = standaloneConfigProp.getProperty(Constants.CIPHER_TOOL_PROPERTY_FILE_PROPERTY);

            if (!Paths.get(secretConfFile).isAbsolute()) {
                secretConfFile = Paths.get(homeFolder, standaloneConfigProp.getProperty(Constants
                        .SECRET_PROPERTY_FILE_PROPERTY)).toString();
            }
        }
        if (keyStoreFile.trim().isEmpty()) {
            throw new SecureVaultException("KeyStore file path cannot be empty");
        }
        if (keyAlias == null || keyAlias.trim().isEmpty()) {
            throw new SecureVaultException("Key alias cannot be empty");
        }
        if (keyStoreFile.startsWith(File.pathSeparator)) {
            keyStoreFile = keyStoreFile.substring(1);
        }
        System.setProperty(Constants.HOME_FOLDER, homeFolder);
        System.setProperty(Constants.PrimaryKeyStore.PRIMARY_KEY_LOCATION_PROPERTY, getConfigFilePath(keyStoreFile));
        System.setProperty(Constants.PrimaryKeyStore.PRIMARY_KEY_TYPE_PROPERTY, keyType);
        System.setProperty(Constants.PrimaryKeyStore.PRIMARY_KEY_ALIAS_PROPERTY, keyAlias);
        System.setProperty(Constants.SECRET_PROPERTY_FILE_PROPERTY, secretConfFile);
        System.setProperty(Constants.SecureVault.SECRET_FILE_LOCATION, cipherTextPropFile);
        System.setProperty(Constants.CIPHER_TEXT_PROPERTY_FILE_PROPERTY, getConfigFilePath(cipherTextPropFile));
        System.setProperty(Constants.CIPHER_TOOL_PROPERTY_FILE_PROPERTY, getConfigFilePath(cipherToolPropFile));
    }
}
