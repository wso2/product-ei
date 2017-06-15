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

package org.wso2.ei.tools.ds2ballerina;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.ei.tools.converter.common.Utils;
import org.wso2.ei.tools.ds2ballerina.beans.DataService;
import org.wso2.ei.tools.ds2ballerina.configreader.DataServiceReader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This is the main execution point.
 */
public class DataServiceToBalConverter {

    private static Logger logger = LoggerFactory.getLogger(DataServiceReader.class);

    public static void main(String... args) {
        if (args == null || args.length == 0) {
            logger.error("Please provide source file/folder");
            System.exit(-1);
        }
        File source = new File(args[0]);
        Path sourcePath = source.toPath().toAbsolutePath();
        logger.info("Source file(s) in location: " + sourcePath.toString());

        String destination = args[1];
//        String destination = "/home/madhawa/DSS-BAM/i.bal";

        try {
            if (Files.isDirectory(sourcePath)) {
                Path path = Utils.getPath(sourcePath.toFile(), destination);
                if (!Files.exists(path)) {
                    path = Files.createDirectories(path);
                }
                destination = path.toString();
                logger.info("Converted files saved in " + destination);

                List<File> filesInFolder = Files.walk(Paths.get(args[0])).filter(Files::isRegularFile).
                        map(Path::toFile).collect(Collectors.toList());
                for (File file : filesInFolder) {
                    DataService dataService = DataServiceReader.readDataServiceFile(file);
                    String fileName = file.getName().substring(0, file.getName().indexOf('.')) + ".bal";
                    DataServiceReader.createBalModel(dataService, destination + File.separator + fileName);
                    logger.info(fileName + " created successfully.");
                }
            } else {
                if (destination == null || destination.isEmpty()) {
                    String fileName = sourcePath.toString();
                    destination = fileName.substring(0, fileName.indexOf('.')) + ".bal";
                }
                logger.info("Generated ballerina file saved as " + destination);
                DataService dataService = DataServiceReader.readDataServiceFile(sourcePath.toFile());
                DataServiceReader.createBalModel(dataService, destination);
            }
        } catch (IOException e) {
            logger.error("Unable to generate ballerina file.", e);
        }
    }
}
