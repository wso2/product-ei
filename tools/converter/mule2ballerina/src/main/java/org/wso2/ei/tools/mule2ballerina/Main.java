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
import org.wso2.ei.tools.converter.common.generator.BallerinaSourceGenerator;
import org.wso2.ei.tools.mule2ballerina.configreader.ConfigReader;
import org.wso2.ei.tools.mule2ballerina.model.Root;
import org.wso2.ei.tools.mule2ballerina.visitor.TreeVisitor;

import java.io.IOException;

/**
 * This is the main execution point
 */
public class Main {

    public static void main(String... args) throws IOException {

        ConfigReader xmlParser = new ConfigReader();
        xmlParser.readXML(xmlParser.getInputStream(args[0]));

        //xmlParser.readXML(xmlParser.getInputStream("passThrough.xml"));
        Root muleRootObj = xmlParser.getmRoot();
        if (xmlParser.getUnIdentifiedElements() != null && !xmlParser.getUnIdentifiedElements().isEmpty()) {

            System.exit(0);
        }

        TreeVisitor treeVisitor = new TreeVisitor(muleRootObj);
        treeVisitor.visit(muleRootObj);
        BallerinaFile ballerinaFile = treeVisitor.getBallerinaFile();

        BallerinaSourceGenerator sourceGenerator = new BallerinaSourceGenerator();
        sourceGenerator.generate(ballerinaFile, args[1]);

        /*CodeGenVisitor codeGenVisitor = new CodeGenVisitor();
        Service targetService = null;
        if (ballerinaFile.getCompilationUnits().length > 0) {
            targetService = (ballerinaFile.getCompilationUnits()[0] instanceof Service) ?
                (Service) ballerinaFile.getCompilationUnits()[0] :
                null;
        }

        if (targetService != null) {
            targetService.accept(codeGenVisitor);
          //  String targetFile = System.getProperty("targetFilePath");
           Utils.writeToBalFile(args[1], codeGenVisitor.getBallerinaSourceStr());

        }*/
    }

}
