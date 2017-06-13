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

    public static void main(String... args) {

        if (args == null || args.length == 0) {
            logger.error("Please provide source file/folder");
            System.exit(-1);
        }
        File source = new File(args[0]);
        Path sourcePath = source.toPath().toAbsolutePath();
        logger.info("Source file(s) in location: " + sourcePath.toString());

        String destination = args[1];
        try {
            if (Files.isDirectory(sourcePath)) {
                Path path = Utils.getPath(source, destination);
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
