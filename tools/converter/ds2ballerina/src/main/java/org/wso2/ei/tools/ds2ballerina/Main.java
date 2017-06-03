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
import org.wso2.ei.tools.ds2ballerina.configreader.DataServiceReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * This is the main execution point
 */
public class Main {

    private static Logger logger = LoggerFactory.getLogger(DataServiceReader.class);

    public static void main(String... args) {

        //        xmlParser.readXML(xmlParser.getInputStream(args[0]));
        File file = new File(
                "/home/madhawa/DSS-BAM/wso2dss-3.5.1/repository/deployment/server/dataservices/samples/ReturnUpdatedRowCountSample.dbs");
        if (logger.isDebugEnabled()) {
            logger.debug("Reading dataservice file in " + file.getPath());
        }
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(file);
            DataServiceReader.readDataServiceFile(file);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


     /*   BallerinaSourceGenerator sourceGenerator = new BallerinaSourceGenerator();
        sourceGenerator.generate(ballerinaFile, args[1]);*/
        //sourceGenerator.generate(ballerinaFile, "/home/rukshani/mule2bal/Generated/pass2.bal");

    }

}
