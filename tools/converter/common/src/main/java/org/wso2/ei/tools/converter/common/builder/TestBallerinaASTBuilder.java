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

package org.wso2.ei.tools.converter.common.builder;

import org.ballerinalang.model.BLangPackage;
import org.ballerinalang.model.BLangProgram;
import org.ballerinalang.model.BallerinaFile;
import org.ballerinalang.model.GlobalScope;
import org.ballerinalang.model.NativeScope;
import org.ballerinalang.model.builder.BLangModelBuilder;
import org.ballerinalang.model.types.SimpleTypeName;
import org.ballerinalang.util.program.BLangPrograms;
import org.ballerinalang.util.repository.PackageRepository;

import java.nio.file.Path;
import java.util.Stack;

/**
 * TestBallerinaASTBuilder
 * TODO: Remove this class later
 */
public class TestBallerinaASTBuilder {

    public static void main(String... args) {
        buildBallerinaAST();
    }

    public static BallerinaFile buildBallerinaAST() {

        Stack<BLangModelBuilder.NameReference> nameReferenceStack = new Stack<>();
        Stack<SimpleTypeName> typeNameStack = new Stack<>();
        boolean processingReturnParams = false;

        PackageRepository packageRepository = new PackageRepository() {
            @Override
            public PackageSource loadPackage(Path path) {
                return null;
            }

            @Override
            public PackageSource loadFile(Path path) {
                return null;
            }
        };

        GlobalScope globalScope = BLangPrograms.populateGlobalScope();
        NativeScope nativeScope = BLangPrograms.populateNativeScope();

        BLangProgram programScope = new BLangProgram(globalScope, nativeScope, BLangProgram.Category.SERVICE_PROGRAM);

        BLangPackage bLangPackage = new BLangPackage(".", packageRepository, programScope);
        BLangPackage.PackageBuilder packageBuilder = new BLangPackage.PackageBuilder(bLangPackage);

        BLangModelBuilder modelBuilder = new BLangModelBuilder(packageBuilder, ".");

        String pkgPath = "ballerina.lang.messages";
        //String asPkgName = null;
        modelBuilder.addImportPackage(null, null, pkgPath, null);

        String pkgPath1 = "ballerina.net.http";
        //String asPkgName1 = null;
        modelBuilder.addImportPackage(null, null, pkgPath1, null);

        modelBuilder.startAnnotationAttachment();

        BLangModelBuilder.NameReference nameReference;

        String pkgName = "http";
        String name = "BasePath";
        nameReference = new BLangModelBuilder.NameReference(pkgName, name);
        modelBuilder.validateAndSetPackagePath(null, nameReference);
        nameReferenceStack.push(nameReference);

        String stringLiteral = "/hello";
        modelBuilder.createStringLiteral(null, null, stringLiteral);

        modelBuilder.createLiteralTypeAttributeValue(null, null);

        String key = "value";
        modelBuilder.createAnnotationKeyValue(null, null, key);

        int attribuesAvailable = 1;
        modelBuilder.addAnnotationAttachment(null, null, nameReferenceStack.pop(), attribuesAvailable);

        modelBuilder.startServiceDef();

        modelBuilder.startResourceDef();

        modelBuilder.startAnnotationAttachment();

        BLangModelBuilder.NameReference nameReference1;

        String pkgName1 = "http";
        String name1 = "GET";
        nameReference1 = new BLangModelBuilder.NameReference(pkgName1, name1);
        modelBuilder.validateAndSetPackagePath(null, nameReference1);
        nameReferenceStack.push(nameReference1);

        int attribuesAvailable1 = 0;
        modelBuilder.addAnnotationAttachment(null, null, nameReferenceStack.pop(), attribuesAvailable1);

        String builtInRefTypeName = "message";
        SimpleTypeName simpleTypeName = new SimpleTypeName(builtInRefTypeName);
        typeNameStack.push(simpleTypeName);

        int annotationCount = 0;
        modelBuilder.addParam(null, null, typeNameStack.pop(), "m", annotationCount, processingReturnParams);

        modelBuilder.startCallableUnitBody();

        String builtInRefTypeName1 = "message";
        SimpleTypeName simpleTypeName1 = new SimpleTypeName(builtInRefTypeName1);
        typeNameStack.push(simpleTypeName1);

        modelBuilder.startMapStructLiteral();

        modelBuilder.createMapStructLiteral(null, null);

        SimpleTypeName typeName = typeNameStack.pop();
        String varName = "response";
        boolean exprAvailable = true;
        modelBuilder.addVariableDefinitionStmt(null, null, typeName, varName, exprAvailable);

        BLangModelBuilder.NameReference nameReference2;
        String pkgName2 = "messages";
        String name2 = "setStringPayload";
        nameReference2 = new BLangModelBuilder.NameReference(pkgName2, name2);
        modelBuilder.validateAndSetPackagePath(null, nameReference2);
        nameReferenceStack.push(nameReference2);

        modelBuilder.startExprList();

        BLangModelBuilder.NameReference nameReference3;
        String name3 = "response";
        nameReference3 = new BLangModelBuilder.NameReference(null, name3);
        nameReferenceStack.push(nameReference3);

        //   modelBuilder.createVarRefExpr(null, null, nameReferenceStack.pop());

        String stringLiteral1 = "Hello, World!";
        modelBuilder.createStringLiteral(null, null, stringLiteral1);

        int childCountExprList = 3;
        int noOfArguments = childCountExprList / 2 + 1;
        modelBuilder.endExprList(noOfArguments);

        boolean argsAvailable = true;
        modelBuilder.createFunctionInvocationStmt(null, null, nameReferenceStack.pop(), argsAvailable);

        BLangModelBuilder.NameReference nameReference4;
        String name4 = "response";
        nameReference4 = new BLangModelBuilder.NameReference(null, name4);
        modelBuilder.validateAndSetPackagePath(null, nameReference4);
        nameReferenceStack.push(nameReference4);

        //    modelBuilder.createVarRefExpr(null, null, nameReferenceStack.pop());

        modelBuilder.createReplyStmt(null, null);

        modelBuilder.endCallableUnitBody(null);

        String resourceName = "sayHello";
        int annotationCount2 = 1;
        modelBuilder.addResource(null, null, resourceName, annotationCount2);

        String serviceName = "helloWorld";
        modelBuilder.createService(null, null, serviceName, "http");

        BallerinaFile bFile = modelBuilder.build();
        System.out.print("Done building AST!");

        return bFile;
    }
}
