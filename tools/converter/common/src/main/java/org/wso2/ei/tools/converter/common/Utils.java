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

package org.wso2.ei.tools.converter.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Class containing util functions
 */
public class Utils {

    private static final String GENERATED_BALLERINA_SOURCE = "generated-ballerina-source";

    public static void writeToBalFile(String filePath, String content) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath), StandardCharsets.UTF_8)) {
            writer.write(content);
            writer.flush();
        }
    }

    public static Path getPath(File source, String destination) {
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
        return path;
    }
}
