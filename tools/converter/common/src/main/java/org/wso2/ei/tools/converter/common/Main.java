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

import org.ballerinalang.BLangProgramLoader;
import org.ballerinalang.model.BLangProgram;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.ei.tools.converter.common.generator.CodeGenVisitor;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Test Main exec class
 */
public class Main {

    private static Logger logger = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) throws IOException {
        /*logger.info("hello");*/

        //Path balHome = Paths.get("/home/milinda/WSO2/Integration/EI/EI7/Ballerina/ballerina-0.88-SNAPSHOT");
        //Path balFile = Paths.get("/home/milinda/WSO2/Integration/EI/EI7/Ballerina/ballerina-tools-0.87/samples/" +
        //        "helloWorldService/passthroughService.bal");
        Path balHome = Paths.get(args[0]);
        Path balFile = Paths.get(args[1]);

        BLangProgram bLangProgram = new BLangProgramLoader().loadService(balHome, balFile);

        CodeGenVisitor codeGenVisitor = new CodeGenVisitor();
        bLangProgram.accept(codeGenVisitor);
        logger.info(codeGenVisitor.getBallerinaSourceStr());
        /*BallerinaFile ballerinaFile = TestBallerinaASTBuilder.buildBallerinaAST();

        Service targetService = null;
        if (ballerinaFile.getCompilationUnits().length > 0) {
            targetService = (ballerinaFile.getCompilationUnits()[0] instanceof Service) ?
                    (Service) ballerinaFile.getCompilationUnits()[0] :
                    null;
        }

        if (targetService != null) {
            System.out.print("CodeVisit start");

            targetService.accept(codeGenVisitor);
            System.out.print(codeGenVisitor.getBallerinaSourceStr());
            //String targetFile = System.getProperty("targetFilePath");
            //Utils.writeToBalFile(targetFile, codeGenVisitor.getBallerinaSourceStr());
        }*/
    }
}
