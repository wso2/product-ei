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
package org.wso2.carbon.ei.migration.util;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.core.util.CryptoException;
import org.wso2.carbon.core.util.CryptoUtil;
import org.wso2.carbon.ei.migration.MigrationClientException;
import org.wso2.carbon.ei.migration.internal.MigrationServiceDataHolder;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Util class.
 */
public class Utility {

    private static Log log = LogFactory.getLog(Utility.class);

    public static String getMigrationResourceDirectoryPath() {
        Path path = Paths.get(System.getProperty(Constant.CARBON_HOME), Constant.MIGRATION_RESOURCE_HOME);
        return path.toString();
    }

    public static OMElement toOM(InputStream inputStream) throws XMLStreamException {
        XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(inputStream);
        StAXOMBuilder builder = new StAXOMBuilder(reader);
        return builder.getDocumentElement();
    }

    public static String getNewEncryptedValue(String encryptedValue) throws CryptoException {
        CryptoUtil cryptoUtil = getDefaultCryptoUtil();
        if (StringUtils.isNotEmpty(encryptedValue) && !isNewlyEncrypted(cryptoUtil, encryptedValue)
                && isEncryptedByRSA(cryptoUtil, encryptedValue)) {
            byte[] decryptedPassword = cryptoUtil.base64DecodeAndDecrypt(encryptedValue, Constant.RSA);
            return cryptoUtil.encryptAndBase64Encode(decryptedPassword);
        }
        return null;
    }

    public static boolean isNewlyEncrypted(CryptoUtil cryptoUtil, String encryptedValue) throws CryptoException {
        return cryptoUtil.base64DecodeAndIsSelfContainedCipherText(encryptedValue);
    }

    public static boolean isEncryptedByRSA(CryptoUtil cryptoUtil, String password) throws CryptoException {
        return password.equals(cryptoUtil.encryptAndBase64Encode(cryptoUtil.base64DecodeAndDecrypt(password, Constant.RSA)
                , Constant.RSA, false));
    }

    public static CryptoUtil getDefaultCryptoUtil() {
        CryptoUtil cryptoUtil = CryptoUtil.getDefaultCryptoUtil(MigrationServiceDataHolder.getServerConfigurationService(),
                MigrationServiceDataHolder.getRegistryService());
        return cryptoUtil;
    }

    public static void delete(File file) {
        if (file.isDirectory()) {
            //directory is empty, then delete it
            if (file.list().length == 0) {
                file.delete();
            } else {
                //list all the directory contents
                String files[] = file.list();
                for (String temp : files) {
                    //construct the file structure
                    File fileDelete = new File(file, temp);
                    //recursive delete
                    delete(fileDelete);
                }
                //check the directory again, if empty then delete it
                if (file.list().length == 0) {
                    file.delete();
                }
            }
        } else {
            //if file, then delete it
            file.delete();
        }
    }

    public static List<String> generateFileList(String sourceFolder) {
        List<String> fileList;
        fileList = new ArrayList<String>();
        // add files

        File[] files = new File(sourceFolder).listFiles();
        if (files != null) {
            for (File file : files) {
                fileList.add(file.getName());
            }
        }
        return fileList;
    }

    public static void zipIt(String sourceFolder, String zipFile, List<String> files) throws MigrationClientException {
        byte[] buffer = new byte[1024];
        String source = new File(sourceFolder).getName();
        FileOutputStream fos;
        ZipOutputStream zos = null;
        try {
            fos = new FileOutputStream(zipFile);
            zos = new ZipOutputStream(fos);
            FileInputStream in = null;

            for (String file : files) {
                ZipEntry ze = new ZipEntry(source + File.separator + file);
                zos.putNextEntry(ze);
                try {
                    in = new FileInputStream(sourceFolder + File.separator + file);
                    int len;
                    while ((len = in.read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                } finally {
                    in.close();
                }
            }
            zos.closeEntry();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                zos.close();
            } catch (IOException e) {
                throw new MigrationClientException(e.getMessage());
            }
        }
    }

    /**
     * Unzip the zip file
     *
     * @param zipFile      input zip file
     * @param outputFolder zip file output folder
     */
    public static void unZipIt(String zipFile, String outputFolder) throws MigrationClientException {
        byte[] buffer = new byte[1024];
        try {
            //create output directory is not exists
            File folder = new File(outputFolder);
            if (!folder.exists()) {
                folder.mkdir();
            }
            //get the zip file content
            ZipInputStream zis =
                    new ZipInputStream(new FileInputStream(zipFile));
            //get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();

            while (ze != null) {
                String fileName = ze.getName();
                if (fileName.toLowerCase().endsWith(".xml") || fileName.toLowerCase().endsWith(".wsdl")
                        || fileName.toLowerCase().endsWith(".bpel") || fileName.endsWith("README")) {
                    File newFile = new File(outputFolder + File.separator + fileName);
                    //create all non exists folders
                    //else you will hit FileNotFoundException for compressed folder
                    new File(newFile.getParent()).mkdirs();
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }
                ze = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
        } catch (IOException e) {
            throw new MigrationClientException(e.getMessage());
        }
    }
}
