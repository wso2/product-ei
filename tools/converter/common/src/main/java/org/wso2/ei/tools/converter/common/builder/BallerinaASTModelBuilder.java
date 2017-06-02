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

package org.wso2.ei.tools.converter.common.builder;

import org.ballerinalang.model.BLangPackage;
import org.ballerinalang.model.BLangProgram;
import org.ballerinalang.model.BallerinaFile;
import org.ballerinalang.model.GlobalScope;
import org.ballerinalang.model.NativeScope;
import org.ballerinalang.model.builder.BLangModelBuilder;
import org.ballerinalang.model.types.SimpleTypeName;
import org.ballerinalang.util.program.BLangPackages;
import org.ballerinalang.util.program.BLangPrograms;
import org.ballerinalang.util.repository.PackageRepository;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * {@code BallerinaASTModelBuilder} is a high level API to build ballerina AST from BLangModelBuilder class
 */
public class BallerinaASTModelBuilder {

    private static final PackageRepository PACKAGE_REPOSITORY = new PackageRepository() {
        @Override
        public PackageSource loadPackage(Path path) {
            return null;
        }

        @Override
        public PackageSource loadFile(Path path) {
            return null;
        }
    };
    private boolean processingActionInvocationStmt = false;
    private Stack<BLangModelBuilder.NameReference> nameReferenceStack = new Stack<>();
    private Stack<SimpleTypeName> typeNameStack = new Stack<>();
    private BLangProgram programScope;
    private BLangPackage bLangPackage;
    private BLangModelBuilder modelBuilder;
    private Map<String, String> ballerinaPackageMap = new HashMap<String, String>();

    public BallerinaASTModelBuilder() {

        GlobalScope globalScope = BLangPrograms.populateGlobalScope();
        NativeScope nativeScope = BLangPrograms.populateNativeScope();

        programScope = new BLangProgram(globalScope, nativeScope, BLangProgram.Category.SERVICE_PROGRAM);
        bLangPackage = new BLangPackage(".", PACKAGE_REPOSITORY, programScope);
        BLangPackage.PackageBuilder packageBuilder = new BLangPackage.PackageBuilder(bLangPackage);
        modelBuilder = new BLangModelBuilder(packageBuilder, ".");

        String[] packages = BLangPackages.getBuiltinPackageNames();
        for (String aPackage : packages) {
            String[] bits = aPackage.split("\\.");
            String pkgName = bits[bits.length - 1];
            ballerinaPackageMap.put(pkgName, aPackage);
        }
    }

    public void addImportPackage(String pkgPath, String asPkgName) {
        modelBuilder.addImportPackage(null, null, pkgPath, asPkgName);
    }

    /**
     * TODO: Need to refactor this to support multiple key value pairs of an annotation
     * @param pkgName name of the package
     * @param name functionality name you want to use
     * @param key annotation attribute key
     * @param actualvalue annotation attribute value
     */
    public void createAnnotationAttachment(String pkgName, String name, String key, String actualvalue) {
        modelBuilder.startAnnotationAttachment(null);

        createNameReference(pkgName, name);

        if (key != null && actualvalue != null) {
            modelBuilder.createStringLiteral(null, null, actualvalue);
            modelBuilder.createLiteralTypeAttributeValue(null, null);
            modelBuilder.createAnnotationKeyValue(null, key);
        }
    }

    /**
     * Adds an annotation. For an attachment to be added first it needs to be created using
     * 'createAnnotationAttachment' method
     * @param attributesCount is never used in ballerina side, even though it expects a value
     */
    public void addAnnotationAttachment(int attributesCount) {
        modelBuilder.addAnnotationAttachment(null, null, nameReferenceStack.pop(), attributesCount);
    }

    public void startService() {
        modelBuilder.startServiceDef(null);
    }

    public void startResource() {
        modelBuilder.startResourceDef();
    }

    public void endOfService(String serviceName) {
        modelBuilder.createService(null, serviceName);

    }

    public void endOfResource(String resourceName, int annotationCount) {
        modelBuilder.addResource(null, null, resourceName, annotationCount);
    }

    /**
     * Add built in ref types
     *
     * @param builtInRefTypeName built in type
     */
    public void addTypes(String builtInRefTypeName) {
        SimpleTypeName simpleTypeName = new SimpleTypeName(builtInRefTypeName);
        typeNameStack.push(simpleTypeName);
    }

    public void createRefereceTypeName() {
        BLangModelBuilder.NameReference nameReference = nameReferenceStack.pop();
        SimpleTypeName typeName = new SimpleTypeName(nameReference.getName(), nameReference.getPackageName(),
                nameReference.getPackagePath());

        typeNameStack.push(typeName);
    }

    /**
     * Create a function parameter
     * @param annotationCount number of annotations - //TODO:get this clarified
     * @param processingReturnParams return parameter or not
     * @param paramName name of the function parameter
     */
    public void addParameter(int annotationCount, boolean processingReturnParams, String paramName) {
        modelBuilder.addParam(null, null, typeNameStack.pop(), paramName, annotationCount, processingReturnParams);
    }

    public void startCallableBody() {
        modelBuilder.startCallableUnitBody(null);
    }

    public void endCallableBody() {
        modelBuilder.endCallableUnitBody();
    }

    //TODO:Ask, what this does
    public void addMapStructLiteral() {
        modelBuilder.startMapStructLiteral();
        modelBuilder.createMapStructLiteral(null, null);
    }

    /**
     *
     * @param varName name of the variable
     * @param exprAvailable expression availability
     */
    public void createVariable(String varName, boolean exprAvailable) {
        SimpleTypeName typeName = typeNameStack.pop();

        modelBuilder.addVariableDefinitionStmt(null, null, typeName, varName, exprAvailable);
    }

    /**
     * Create a name reference
     * @param pkgName package name
     * @param name functionality name you want to use
     */
    public void createNameReference(String pkgName, String name) {
        BLangModelBuilder.NameReference nameReference;

        nameReference = new BLangModelBuilder.NameReference(pkgName, name);
        modelBuilder.validateAndSetPackagePath(null, nameReference);
        nameReferenceStack.push(nameReference);
    }

    public void startExprList() {
        modelBuilder.startExprList();
    }

    public void endExprList(int noOfArguments) {
        modelBuilder.endExprList(noOfArguments);
    }

    public void createVariableRefExpr() {
        modelBuilder.createVarRefExpr(null, null, nameReferenceStack.pop());
    }

    public void createStringLiteral(String stringLiteral) {
        modelBuilder.createStringLiteral(null, null, stringLiteral);
    }

    public void createFunctionInvocation(boolean argsAvailable) {
        modelBuilder.createFunctionInvocationStmt(null, null, nameReferenceStack.pop(), argsAvailable);
    }

    public void createReplyStatement() {
        modelBuilder.createReplyStmt(null, null);
    }

    public void initializeConnector(boolean argsAvailable) {
        BLangModelBuilder.NameReference nameReference = nameReferenceStack.pop();
        SimpleTypeName connectorTypeName = new SimpleTypeName(nameReference.getName(), nameReference.getPackageName(),
                null);

        modelBuilder.createConnectorInitExpr(null, null, connectorTypeName, argsAvailable);
    }

    public void createVariableRefList() {
        modelBuilder.startVarRefList();
    }

    public void endVariableRefList(int noArguments) {
        modelBuilder.endVarRefList(noArguments);
    }

    public void createAction(String actionName, boolean argsAvailable) {
        BLangModelBuilder.NameReference nameReference = nameReferenceStack.pop();
        if (processingActionInvocationStmt) {
            modelBuilder.createActionInvocationStmt(null, null, nameReference, actionName, argsAvailable);
        } else {
            modelBuilder.addActionInvocationExpr(null, null, nameReference, actionName, argsAvailable);
        }
    }

    public void createAssignmentStatement() {
        modelBuilder.createAssignmentStmt(null, null);
    }

    public BallerinaFile buildBallerinaFile() {
        BallerinaFile bFile = modelBuilder.build();
        return bFile;
    }

    public Map<String, String> getBallerinaPackageMap() {
        return ballerinaPackageMap;
    }
}
