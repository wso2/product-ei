/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.ei.tools.converter.common.generator;

import org.ballerinalang.model.BallerinaFile;

import java.io.IOException;

/**
 * {@link BallerinaSourceGenerator} generates ballerina source from ballerina model
 */
public class BallerinaSourceGenerator {

    public void generate(BallerinaFile ballerinaFile, String targetFilePath) throws IOException {

        CodeGenVisitor codeGenVisitor = new CodeGenVisitor();

        ballerinaFile.accept(codeGenVisitor);
        Utils.writeToBalFile(targetFilePath, codeGenVisitor.getBallerinaSourceStr());
        /*Service targetService = null;
        if (ballerinaFile.getCompilationUnits().length > 0) {
            targetService = (ballerinaFile.getCompilationUnits()[0] instanceof Service) ?
                    (Service) ballerinaFile.getCompilationUnits()[0] :
                    null;
        }

        if (targetService != null) {
            targetService.accept(codeGenVisitor);
            Utils.writeToBalFile(targetFilePath, codeGenVisitor.getBallerinaSourceStr());
        }*/
    }
}
