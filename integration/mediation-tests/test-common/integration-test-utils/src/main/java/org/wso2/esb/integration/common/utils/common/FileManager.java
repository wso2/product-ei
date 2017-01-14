/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/
package org.wso2.esb.integration.common.utils.common;


import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;


public class FileManager {
    private static final Log log = LogFactory.getLog(FileManager.class);

    public static String readFile(String filePath) throws IOException {
        BufferedReader reader;
        StringBuilder stringBuilder;
        String line;
        String ls;
        log.debug("Path to file : " + filePath);
        reader = new BufferedReader(new FileReader(filePath));
        stringBuilder = new StringBuilder();
        ls = System.getProperty("line.separator");

        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }
        reader.close();
        return stringBuilder.toString();
    }

    public static String readFile(File file) throws IOException {
        BufferedReader reader;
        StringBuilder stringBuilder;
        String line;
        String ls;

        reader = new BufferedReader(new FileReader(file));
        stringBuilder = new StringBuilder();
        ls = System.getProperty("line.separator");

        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }
        reader.close();
        return stringBuilder.toString();
    }

    public static void writeToFile(String filePath, String content) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true));
        try {
            writer.write(content);
            writer.newLine();
            writer.flush();
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                //ignore
            }
        }


    }

    public static void copyFile(File sourceFile, String destinationPath) throws IOException {
        File destinationFile = new File(destinationPath);

        FileReader in = new FileReader(sourceFile);
        FileWriter out = new FileWriter(destinationFile);
        int c;
        try {
            while ((c = in.read()) != -1) {
                out.write(c);
            }
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                //ignore
            }
            try {
                out.close();
            } catch (IOException e) {
                //ignore
            }
        }

    }

    public static File copyResourceToFileSystem(String sourcePath, String targetPath,
                                                String fileName)
            throws IOException {

        File file = new File(targetPath + File.separator + fileName);
        if (file.exists()) {
            FileUtils.deleteQuietly(file);
        }

        FileUtils.touch(file);
        OutputStream os = FileUtils.openOutputStream(file);

        InputStream is = new FileInputStream(sourcePath);

        if (is != null) {
            byte[] data = new byte[1024];
            int len;
            while ((len = is.read(data)) != -1) {
                os.write(data, 0, len);
            }
            os.flush();
            os.close();
            is.close();
        }
        return file;
    }


    public void copyJarFile(String sourceFileLocationWithFileName, String destinationDirectory)
            throws IOException, URISyntaxException {
        File sourceFile = new File(getClass().getResource(sourceFileLocationWithFileName).toURI());
        File destinationFileDirectory = new File(destinationDirectory);
        JarFile jarFile = new JarFile(sourceFile);
        String fileName = jarFile.getName();
        String fileNameLastPart = fileName.substring(fileName.lastIndexOf(File.separator));
        File destinationFile = new File(destinationFileDirectory, fileNameLastPart);

        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(destinationFile));
        Enumeration<JarEntry> entries = jarFile.entries();

        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            InputStream inputStream = jarFile.getInputStream(jarEntry);

            //jarOutputStream.putNextEntry(jarEntry);
            //create a new jarEntry to avoid ZipException: invalid jarEntry compressed size
            jarOutputStream.putNextEntry(new JarEntry(jarEntry.getName()));
            byte[] buffer = new byte[4096];
            int bytesRead = 0;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                jarOutputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            jarOutputStream.flush();
            jarOutputStream.closeEntry();
        }
        jarOutputStream.close();
    }

    public static void copyJarFile(File sourceFile, String destinationDirectory)
            throws IOException {
        File destinationFileDirectory = new File(destinationDirectory);
        JarFile jarFile = new JarFile(sourceFile);
        String fileName = jarFile.getName();
        String fileNameLastPart = fileName.substring(fileName.lastIndexOf(File.separator));
        File destinationFile = new File(destinationFileDirectory, fileNameLastPart);
        JarOutputStream jarOutputStream = null;
        try {
            jarOutputStream = new JarOutputStream(new FileOutputStream(destinationFile));
            Enumeration<JarEntry> entries = jarFile.entries();

            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                InputStream inputStream = jarFile.getInputStream(jarEntry);

                //jarOutputStream.putNextEntry(jarEntry);
                //create a new jarEntry to avoid ZipException: invalid jarEntry compressed size
                jarOutputStream.putNextEntry(new JarEntry(jarEntry.getName()));
                byte[] buffer = new byte[4096];
                int bytesRead = 0;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    jarOutputStream.write(buffer, 0, bytesRead);
                }
                inputStream.close();
                jarOutputStream.flush();
                jarOutputStream.closeEntry();
            }
        } finally {
            if (jarOutputStream != null) {
                try {
                    jarOutputStream.close();
                } catch (IOException e) {

                }
            }
        }

    }

    public static boolean deleteFile(String filePathWithFileName) {
        File jarFile = new File(filePathWithFileName);
        return !jarFile.isDirectory() && jarFile.delete();
    }

    public static byte[] getBytesFromFile(String fileName) throws IOException {

        File file = new File(fileName);
        InputStream is = new FileInputStream(file);
        long length = file.length();

        byte[] bytes = new byte[(int) length];

        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }

        is.close();
        return bytes;
    }
}

