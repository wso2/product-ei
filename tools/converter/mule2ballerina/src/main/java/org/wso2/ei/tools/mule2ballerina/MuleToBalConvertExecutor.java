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

package org.wso2.ei.tools.mule2ballerina;

import org.ballerinalang.model.BallerinaFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.ei.tools.converter.common.Utils;
import org.wso2.ei.tools.converter.common.generator.BallerinaSourceGenerator;
import org.wso2.ei.tools.mule2ballerina.configreader.ConfigReader;
import org.wso2.ei.tools.mule2ballerina.model.Root;
import org.wso2.ei.tools.mule2ballerina.util.Constant;
import org.wso2.ei.tools.mule2ballerina.visitor.TreeVisitor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@code MuleToBalConvertExecutor} This is the main execution point
 */
public class MuleToBalConvertExecutor {

    private static Logger logger = LoggerFactory.getLogger(ConfigReader.class);
    private static final String XML_EXTENSION = "xml";
    private static final String MULE_TAG = "</mule>";

    public static void main(String... args) {

        if (args == null || args.length == 0) {
            logger.error("Please provide mule-config-file/mule-zip/folder containing these");
            System.exit(-1);
        }

        //If the user provides a zip file or a folder containing zip files
        if (Constant.OPTION.equalsIgnoreCase(args[0])) {

            String generatedBalLocation = null;
            if (args.length < 2) { //Check whether source location is given
                logger.error("Please provide mule-config-file/mule-zip/folder containing these");
                System.exit(-1);
            } else if (args.length == 2) {
                generatedBalLocation = null;
            } else {
                generatedBalLocation = args[2];
            }

            //isNormalExtractOn set to false
            String destinationFolder = Utils.extractFiles(args[1], generatedBalLocation, false);
            logger.debug("Extracted files are in: " + destinationFolder);

            File source = new File(args[1]);

            //Loop through every extracted folder to generate bal source
            File[] files = new File(destinationFolder).listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        logger.debug("Outer Directory: " + file.getName());
                        File[] innerFiles = file.listFiles();
                        if (innerFiles != null) {
                            for (File innerFile : innerFiles) {
                                if (innerFile.isDirectory()) {
                                    iterateMuleFiles(innerFile, source, generatedBalLocation);
                                }
                            }
                        }
                    }
                }
            }
        } else {
            File source = new File(args[0]);
            Path sourcePath = source.toPath().toAbsolutePath();
            logger.info("Source file(s) in location: " + sourcePath.toString());
            String destination = null;
            if (args.length < 2) {
                destination = null;
            } else {
                destination = args[1];
            }

            try {
                if (Files.isDirectory(sourcePath)) {
                    Path path = Utils.getPath(source, destination, false);
                    if (!Files.exists(path)) {
                        path = Files.createDirectories(path);
                    }
                    destination = path.toString();
                    logger.info("Converted files saved in " + destination);

                    List<File> filesInFolder = Files.walk(Paths.get(args[0])).filter(Files::isRegularFile).
                            map(Path::toFile).collect(Collectors.toList());
                    for (File file : filesInFolder) {
                        ConfigReader xmlParser = new ConfigReader();
                        try (InputStream inputStream = xmlParser.getInputStream(file)) {
                            String fileName = file.getName().substring(0, file.getName().indexOf('.')) + ".bal";
                            createBalFile(xmlParser, inputStream, destination + File.separator + fileName);
                            logger.info(fileName + " created successfully.");
                        }
                    }
                } else {
                    if (destination == null || destination.isEmpty()) {
                        String fileName = source.getName();
                        destination = fileName.substring(0, fileName.indexOf('.')) + ".bal";
                    }
                    logger.info("Generated ballerina file saved as " + destination);
                    ConfigReader xmlParser = new ConfigReader();
                    try (InputStream inputStream = xmlParser.getInputStream(source)) {
                        createBalFile(xmlParser, inputStream, destination);
                    }
                }
            } catch (IOException e) {
                logger.error("Unable to generate ballerina file.", e);
            }
        }
    }

    private static void createBalFile(ConfigReader xmlParser, InputStream inputStream, String destination)
            throws IOException {
        xmlParser.readXML(inputStream);
        Root muleRootObj = xmlParser.getRootObj();
        if (xmlParser.getUnIdentifiedElements() != null && !xmlParser.getUnIdentifiedElements().isEmpty()) {
            logger.warn("Following Elements are not supported by the converter yet!");
            logger.warn("-----------------------------------------------------------");
            xmlParser.getUnIdentifiedElements().forEach(element -> logger.warn(element));
            logger.warn("-----------------------------------------------------------");
        }
        TreeVisitor treeVisitor = new TreeVisitor(muleRootObj);
        treeVisitor.visit(muleRootObj);
        BallerinaFile ballerinaFile = treeVisitor.getBallerinaFile();

        BallerinaSourceGenerator sourceGenerator = new BallerinaSourceGenerator();
        sourceGenerator.generate(ballerinaFile, destination);
    }

    private static void iterateMuleFiles(File innerDirectory, File source, String generatedBalSourceLocation) {
        logger.debug("Inner Directory: " + innerDirectory.getName());
        ConfigReader xmlParser = new ConfigReader();
        File[] actualFiles = innerDirectory.listFiles();
        //Get the destination path where you want to save your bal files. isExtract = false
        Path path = Utils.getPath(source, generatedBalSourceLocation, false);
        logger.info("Converted files are saved in " + path.toString());
        if (!Files.exists(path)) {
            try {
                path = Files.createDirectories(path);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        if (actualFiles != null) {
            for (File actualFile : actualFiles) {
                try {
                    String content = new String(Files.readAllBytes(Paths.get(actualFile.getAbsolutePath())),
                            StandardCharsets.UTF_8);
                    if (XML_EXTENSION.equals(Utils.getFileExtension(actualFile)) && content.contains(MULE_TAG)) {
                        try (InputStream inputStream = xmlParser.getInputStream(actualFile)) {
                            String fileName =
                                    actualFile.getName().substring(0, actualFile.getName().indexOf('.')) + ".bal";
                            createBalFile(xmlParser, inputStream, path + File.separator +
                                    fileName);
                            logger.info(fileName + " created successfully.");
                        } catch (IOException e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }
}
