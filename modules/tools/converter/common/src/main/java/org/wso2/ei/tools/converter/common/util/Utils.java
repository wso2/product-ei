/*
 *     Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *     WSO2 Inc. licenses this file to you under the Apache License,
 *     Version 2.0 (the "License"); you may not use this file except
 *     in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing,
 *    software distributed under the License is distributed on an
 *    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *    KIND, either express or implied.  See the License for the
 *    specific language governing permissions and limitations
 *    under the License.
 */

package org.wso2.ei.tools.converter.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Class containing util functions.
 */
public class Utils {

    private static final String GENERATED_BALLERINA_SOURCE = "generated-ballerina-source";
    private static final String EXTRACTED_FILES = "extracted-files";
    private static final byte[] MAGIC = {
            'P', 'K', 0x3, 0x4
    }; //The first four bytes of a valid ZIP file should be {'P', 'K', 0x3, 0x4}
    private static final String CLASSES_FOLDER = "classes/";
    private static final String XML_EXTENSION = "xml";
    private static final String PROPERTY_EXTENSION = "properties";

    private static Logger logger = LoggerFactory.getLogger(Utils.class);

    public static void writeToBalFile(String filePath, String content) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath))) {
            writer.write(content);
            writer.flush();
        }
    }

    /**
     * Given a source get the destination path.
     *
     * @param source      source file
     * @param destination destination to save generated code
     * @param isExtract   whether this is an extraction or a generation of bal source
     * @return
     */
    public static Path getPath(File source, String destination, boolean isExtract) {
        Path path;
        String folderName = ((isExtract) ? EXTRACTED_FILES : GENERATED_BALLERINA_SOURCE);

        if (destination == null || destination.isEmpty()) {
            path = Paths.get(source.getAbsolutePath() + File.separator + folderName);
        } else {
            //If file seperator '/' is mentioned at the end of the destination folder
            if (destination.charAt(destination.length() - 1) == File.separatorChar) {
                path = Paths.get(destination + folderName);
            } else {
                path = Paths.get(destination + File.separator + folderName);
            }
        }
        return path;
    }

    /**
     * Check whether the inputstream is a zip archive.
     *
     * @param inputStream the input stream
     * @return zip archive or not
     */
    public static boolean isZipStream(InputStream inputStream) {
        //if this input stream doesn't support the mark and reset methods
        if (!inputStream.markSupported()) {
            inputStream = new BufferedInputStream(inputStream);
        }
        boolean isZip = true;
        try {
            //Marks the current position in this input stream
            inputStream.mark(MAGIC.length);
            for (int i = 0; i < MAGIC.length; i++) {
                if (MAGIC[i] != (byte) inputStream.read()) {
                    isZip = false;
                    break;
                }
            }
            //Repositions this stream to the position at the time the mark method was last called on this input stream
            inputStream.reset();
        } catch (IOException e) {
            isZip = false;
            logger.error("Error in checking zip stream", e);
        }
        return isZip;
    }

    /**
     * Given a car or a zip, unzip it. If a folder is given as the source, unzip all the compressed files within that
     * folder
     *
     * @param sourceFileOrFolder compressed file or the folder that contains multiple compressed files
     * @param destination        compressed files will be extracted here
     */
    public static String extractFiles(String sourceFileOrFolder, String destination, boolean isNormalExtractOn) {

        File source = new File(sourceFileOrFolder);
        Path sourcePath = source.toPath().toAbsolutePath();
        //Get the destination of given a source. isExtract is set to true
        Path path = Utils.getPath(source, destination, true);
        destination = path.toAbsolutePath().toString();
        String returnDestFolder = destination;

        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            logger.info("Extracted files are saved in " + destination);
            if (Files.isDirectory(sourcePath)) {
                logger.debug("Compressed files are in " + sourcePath + " folder.");

                try {
                    List<File> filesInFolder = Files.walk(Paths.get(sourceFileOrFolder)).filter(Files::isRegularFile).
                            map(Path::toFile).collect(Collectors.toList());
                    logger.debug("Following files have been extracted!");
                    logger.debug("-----------------------------------------------------------------------------------");
                    for (File file : filesInFolder) {
                        boolean isZipStream = isZipStream(new FileInputStream(file));
                        if (isZipStream) {
                            logger.debug(file.getAbsolutePath());
                            if (isNormalExtractOn) {
                                unZip(file, destination);
                            } else {
                                unzipMule(file, destination);
                            }
                        }
                    }
                    logger.debug("-----------------------------------------------------------------------------------");
                } catch (IOException e) {
                    logger.error("Error in extracting files from zip or car file", e);
                }
            } else {
                boolean isZipStream = isZipStream(new FileInputStream(source));
                if (isZipStream) {
                    logger.debug(sourcePath + " is the file that needs unzipping.");
                    if (isNormalExtractOn) {
                        unZip(source, destination);
                    } else {
                        unzipMule(source, destination);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            logger.error("File not found when trying to extract zip files", e);
        } catch (IOException e) {
            logger.error("Error in creating directories for extracted files", e);
        }
        return returnDestFolder;
    }

    /**
     * Actual unzipping happens here
     *
     * @param source      zip or car file
     * @param destination zip or car file will be extracted here
     * @throws IOException
     */
    private static void unZip(File source, String destination) {
        //Remove the extension to use the file name as the outer folder
        destination = destination + File.separator + source.getName().replaceFirst("[.][^.]+$", "");
        File outerFolder = new File(destination);
        FileOutputStream fos = null;

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(source));) {
            Files.createDirectories(outerFolder.toPath());
            //buffer for read and write data to file
            byte[] buffer = new byte[1024];

            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                String fileName = zipEntry.getName();
                File newFile = new File(destination + File.separator + fileName);

                //create directories for sub directories in zip
                if (zipEntry.isDirectory()) {
                    Files.createDirectories(newFile.toPath());
                } else {
                    fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }
                zipEntry = zis.getNextEntry();
            }
        } catch (FileNotFoundException e) {
            logger.error("FileNotFoundException in unZip", e);
        } catch (IOException e) {
            logger.error("IOException in unZip", e);
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                logger.error("IOException in unZip when trying to close file output stream", e);
            }
        }
    }

    /**
     * Only extract mule configs and property files inside classes folder
     *
     * @param source      mule zip file
     * @param destination files will be extracted here
     */
    private static void unzipMule(File source, String destination) {
        //Remove the extension to use the file name as the outer folder
        destination = destination + File.separator + source.getName().replaceFirst("[.][^.]+$", "");
        File outerFolder = new File(destination);
        FileOutputStream fos = null;
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(source));) {
            Files.createDirectories(outerFolder.toPath());

            //buffer for read and write data to file
            byte[] buffer = new byte[1024];

            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                String fileName = zipEntry.getName();
                File newFile = new File(destination + File.separator + fileName);
                //When classes folder is encountered
                if (zipEntry.isDirectory() && CLASSES_FOLDER.equals(fileName)) {
                    Files.createDirectories(newFile.toPath());
                } else {
                    /*Files needed for the conversion are in classes folder and only xml and property files need to
                     be considered*/
                    if (fileName.startsWith(CLASSES_FOLDER) && (XML_EXTENSION.equals(getFileExtension(newFile))
                            || PROPERTY_EXTENSION.equals(getFileExtension(newFile)))) {
                        fos = new FileOutputStream(newFile);
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                        fos.close();
                    }
                }
                zipEntry = zis.getNextEntry();
            }
        } catch (FileNotFoundException e) {
            logger.error("FileNotFoundException in unzipMule", e);
        } catch (IOException e) {
            logger.error("IOException in unzipMule", e);
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                logger.error("IOException in unzipMule when trying to close file output stream", e);
            }
        }
    }

    /**
     * Given a file get it's extension
     *
     * @param file
     * @return file extension as a string
     */
    public static String getFileExtension(File file) {
        String fileName = file.getName();
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }
}
