/*
 *  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.ei.deployer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.ei.deployer.common.DirChangeCallback;
import org.wso2.ei.deployer.common.DirectoryListner;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Temporary test main class
 * TODO Remove when not needed
 */
public class MainTestClass {

    private static Logger logger = LoggerFactory.getLogger(MainTestClass.class);

    public static void main(String[] args) {

        Path targetDir = Paths.get(args[0]);
        //Path targetDir = Paths.get("/home/milinda/WSO2/Integration/EI/EI7/hotDeployment/testTargetDir");
        try {

            DirectoryListner directoryListner = new DirectoryListner();
            directoryListner.registerTargetDir(targetDir, new DirChangeCallback());
            directoryListner.startListening();

        } catch (IOException e) {
            logger.error("Error occurred while instantiating DirectoryListner", e);
        }

    }
}
