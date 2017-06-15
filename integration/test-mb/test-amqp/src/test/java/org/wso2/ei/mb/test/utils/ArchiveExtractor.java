/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.ei.mb.test.utils;


import org.testng.log4testng.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Extract archived distribution and related methods
 */
public class ArchiveExtractor {

    /**
     * The logger used in logging information, warnings, errors and etc.
     */
    private static Logger log = Logger.getLogger(ServerManager.class);

    /**
     * Extract archived file from given file path.
     *
     * @param sourceFilePath source archive file path
     * @param extractedDir   target directory path
     * @throws IOException
     */
    public void extractFile(String sourceFilePath, String extractedDir) throws IOException {
        FileOutputStream fileOutputStream = null;
        String fileDestination = extractedDir + File.separator;
        byte[] buffer = new byte[1024];
        ZipInputStream zipInputStream = null;
        ZipEntry zipEntry;
        boolean newDirectoriesCreated;

        try {
            zipInputStream = new ZipInputStream(new FileInputStream(sourceFilePath));
            zipEntry = zipInputStream.getNextEntry();

            while (zipEntry != null) {
                //for each entry to be extracted
                String entryName = fileDestination + zipEntry.getName();
                entryName = entryName.replace('/', File.separatorChar);
                entryName = entryName.replace('\\', File.separatorChar);
                int numberOfBytes;

                File newFile = new File(entryName);

                if (zipEntry.isDirectory()) {
                    if (!newFile.exists()) {
                        newDirectoriesCreated = newFile.mkdirs();
                    }
                    zipEntry = zipInputStream.getNextEntry();
                    continue;
                } else {
                    File resourceFile =
                            new File(entryName.substring(0, entryName.lastIndexOf(File.separator)));
                    if (!resourceFile.exists() && !resourceFile.mkdirs()) {
                        break;
                    }
                }
                fileOutputStream = new FileOutputStream(entryName);

                while ((numberOfBytes = zipInputStream.read(buffer, 0, 1024)) > -1) {
                    fileOutputStream.write(buffer, 0, numberOfBytes);
                }

                fileOutputStream.close();
                zipInputStream.closeEntry();
                zipEntry = zipInputStream.getNextEntry();

            }
            zipInputStream.close();
        } catch (IOException e) {
            if (zipInputStream != null) {
                zipInputStream.close();
            }
            log.error("Error on archive extraction ", e);
            throw new IOException("Error on archive extraction ", e);

        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
            if (zipInputStream != null) {
                zipInputStream.close();
            }
        }
    }
}

