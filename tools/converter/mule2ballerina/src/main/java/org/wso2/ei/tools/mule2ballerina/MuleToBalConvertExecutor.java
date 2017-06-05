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
import org.wso2.ei.tools.converter.common.generator.BallerinaSourceGenerator;
import org.wso2.ei.tools.mule2ballerina.configreader.ConfigReader;
import org.wso2.ei.tools.mule2ballerina.model.Root;
import org.wso2.ei.tools.mule2ballerina.visitor.TreeVisitor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This is the main execution point
 */
public class MuleToBalConvertExecutor {

    private static Logger logger = LoggerFactory.getLogger(ConfigReader.class);
    private static final String GENERATED_BALLERINA_SOURCE = "generated-ballerina-source";

    public static void main(String... args) throws IOException {

        if (args == null || args.length == 0) {
            logger.error("Please provide source file/folder");
            System.exit(-1);
        }

        ConfigReader xmlParser = new ConfigReader();
        File source = new File(args[0]);
        Path sourcePath = source.toPath().toAbsolutePath();
        logger.info("Source file(s) in location: " + sourcePath.toString());

        String destination = args[1];

        if (Files.isDirectory(sourcePath)) {
            Path path;
            if (destination == null || destination.isEmpty()) {
                path = Paths.get(source.getAbsolutePath() + File.separator + GENERATED_BALLERINA_SOURCE);
            } else {
                if (destination.charAt(destination.length() - 1) == File.separatorChar) {
                    path = Paths.get(destination + GENERATED_BALLERINA_SOURCE);
                } else {
                    path = Paths.get(destination + File.separator + GENERATED_BALLERINA_SOURCE);
                }
            }
            if (!Files.exists(path)) {
                try {
                    path = Files.createDirectories(path);
                    destination = path.toString();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                    return;
                }
            }
            logger.info("Converted files saved in " + destination);
            List<File> filesInFolder = Files.walk(Paths.get(args[0])).filter(Files::isRegularFile).map(Path::toFile).
                    collect(Collectors.toList());
            for (File file : filesInFolder) {
                logger.info(file.getAbsolutePath());
                try (InputStream inputStream = xmlParser.getInputStream(file)) {
                    String fileName = file.getName().substring(0, file.getName().indexOf('.')) + ".bal";
                    createBalFile(xmlParser, inputStream, destination + File.separator + fileName);
                }
            }
        } else {
            if (destination == null || destination.isEmpty()) {
                String fileName = source.getName();
                destination = fileName.substring(0, fileName.indexOf('.')) + ".bal";
            }
            logger.info("Generated ballerina file saved as " + destination);
            try (InputStream inputStream = xmlParser.getInputStream(source)) {
                createBalFile(xmlParser, inputStream, destination);
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
}
