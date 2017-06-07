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
import org.wso2.ei.tools.ds2ballerina.beans.DataService;
import org.wso2.ei.tools.ds2ballerina.configreader.DataServiceReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * This is the main execution point.
 */
public class DataServiceToBalConverter {

    private static Logger logger = LoggerFactory.getLogger(DataServiceReader.class);

    public static void main(String... args) throws IOException {

        if (args == null || args.length == 0) {
            logger.error("Please provide source file/folder");
            System.exit(-1);
        }
        File source = new File(args[0]);
        Path sourcePath = source.toPath().toAbsolutePath();
        logger.info("Source file(s) in location: " + sourcePath.toString());

        String destination = args[1];
        if (logger.isDebugEnabled()) {
            logger.debug("Reading dataservice file in " + source.getPath());
        }

        try {
//            DataService dataService = DataServiceReader.readDataServiceFile(
//             new File("/home/madhawa/DSS-BAM/wso2dss-3.5.1/samples/dbs/rdbms/ResourcesSample.dbs"));
            DataService dataService = DataServiceReader.readDataServiceFile(source);

            DataServiceReader.createBalModel(dataService, destination);

        } catch (IOException e) {
            logger.error("Exception occurred, while converting " + source + " data service.");
            throw e;
        }

    }

}
