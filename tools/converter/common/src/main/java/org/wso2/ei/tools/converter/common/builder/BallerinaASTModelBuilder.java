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
 * {@code BallerinaASTModelBuilder} is a high level API to build ballerina AST from BLangModelBuilder class, but
 * methods of this class shouldn't be called directly from any converter's visitor. These methods should be
 * accessed through ballerinahelper package classes.
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

    /**
     * Create an object of ballerina model that needs to be filled out from converter information and keep a map with
     * native ballerina package against it's name reference as this is needed for import statements.
     */
    public BallerinaASTModelBuilder() {
        GlobalScope globalScope = BLangPrograms.populateGlobalScope();
        NativeScope nativeScope = BLangPrograms.populateNativeScope();
        programScope = new BLangProgram(globalScope, nativeScope);
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

    /**
     * Check whether action statement is ready to be processed.
     *
     * @return a boolean expressing processingActionInvocationStmt
     */
    public boolean isProcessingActionInvocationStmt() {
        return processingActionInvocationStmt;
    }

    /**
     * Set whether action statement needs to be processed or not
     *
     * @param processingActionInvocationStmt boolean to set the processing to true or false
     */
    public void setProcessingActionInvocationStmt(boolean processingActionInvocationStmt) {
        this.processingActionInvocationStmt = processingActionInvocationStmt;
    }

    /**
     * Add import statement.
     *
     * @param pkgPath   package path
     * @param asPkgName package name
     */
    public void addImportPackage(String pkgPath, String asPkgName) {
        modelBuilder.addImportPackage(null, null, pkgPath, asPkgName);
    }

    /**
     * Create an annotation.
     *
     * @param pkgName name of the package
     * @param name    functionality name you want to use
     */
    public void createAnnotationAttachment(String pkgName, String name) {
        modelBuilder.startAnnotationAttachment();

        createNameReference(pkgName, name);
    }

    /**
     * Create annotation attributes - literal type.
     *
     * @param key         attribute name
     * @param actualvalue attribute value
     */
    public void createAnnotationAttributeValue(String key, String actualvalue) {
        if (key != null && actualvalue != null) {
            modelBuilder.createStringLiteral(null, null, actualvalue);
            modelBuilder.createLiteralTypeAttributeValue(null, null);
            modelBuilder.createAnnotationKeyValue(null, null, key);
        }
    }

    /**
     * Create annotation with array type attributes.
     *
     * @param key          attribute name
     * @param actualValues attribute values
     */
    public void createAnnotationAttributeArrayType(String key, String[] actualValues) {
        if (key != null && actualValues != null) {
            for (String actualValue : actualValues) {
                modelBuilder.createStringLiteral(null, null, actualValue);
                modelBuilder.createLiteralTypeAttributeValue(null, null);
            }
            modelBuilder.createArrayTypeAttributeValue(null, null);
            modelBuilder.createAnnotationKeyValue(null, null, key);
        }
    }

    /**
     * Adds an annotation. For an attachment to be added first it needs to be created using
     * 'createAnnotationAttachment' method.
     *
     * @param attributesCount is never used in ballerina side, even though it expects a value
     */
    public void addAnnotationAttachment(int attributesCount) {
        modelBuilder.addAnnotationAttachment(null, null, nameReferenceStack.pop(), attributesCount);
    }

    /**
     * Start ballerina service.
     */
    public void startService() {
        modelBuilder.startServiceDef();
    }

    /**
     * Start ballerina resource.
     */
    public void startResource() {
        modelBuilder.startResourceDef();
    }

    /**
     * End of ballerina service.
     *
     * @param serviceName     service name
     * @param protocolPkgName protocol package name
     */
    public void endOfService(String serviceName, String protocolPkgName) {
        modelBuilder.createService(null, null, serviceName, protocolPkgName);

    }

    /**
     * End of ballerina resource.
     *
     * @param resourceName    resource name
     * @param annotationCount annotation count of the resource
     */
    public void endOfResource(String resourceName, int annotationCount) {
        modelBuilder.addResource(null, null, resourceName, annotationCount);
    }

    /**
     * Start ballerina function.
     */
    public void startFunction() {
        modelBuilder.startFunctionDef();
    }

    /**
     * End of ballerina function.
     *
     * @param functionName function name
     */
    public void endOfFunction(String functionName) {
        modelBuilder.addFunction(null, null, functionName, false, false); //isNative is false
    }

    /**
     * Add built in ref types.
     *
     * @param builtInRefTypeName built in type
     */
    public void addTypes(String builtInRefTypeName) {
        SimpleTypeName simpleTypeName = new SimpleTypeName(builtInRefTypeName);
        typeNameStack.push(simpleTypeName);
    }

    /**
     * Will be used when creating connectors.
     */
    public void createRefereceTypeName() {
        BLangModelBuilder.NameReference nameReference = nameReferenceStack.pop();
        SimpleTypeName typeName = new SimpleTypeName(nameReference.getName(), nameReference.getPackageName(),
                nameReference.getPackagePath());

        typeNameStack.push(typeName);
    }

    /**
     * Create a function parameter.
     *
     * @param annotationCount        number of annotations - this is required in case of PathParam or QueryParam
     *                               annotations
     * @param processingReturnParams return parameter or not
     * @param paramName              name of the function parameter
     */
    public void addParameter(int annotationCount, boolean processingReturnParams, String paramName) {
        modelBuilder.addParam(null, null, typeNameStack.pop(), paramName, annotationCount, processingReturnParams);
    }

    /**
     * Start the body of a ballerina resource or a function.
     */
    public void startCallableBody() {
        modelBuilder.startCallableUnitBody();
    }

    /**
     * End the body of a ballerina resource or a function.
     */
    public void endCallableBody() {
        modelBuilder.endCallableUnitBody(null);
    }

    /**
     * Start of ballerina map.
     */
    public void startMapStructLiteral() {
        modelBuilder.startMapStructLiteral();
    }

    /**
     * Add ballerina map.
     */
    public void createMapStructLiteral() {
        modelBuilder.createMapStructLiteral(null, null);
    }

    /**
     * Add key value pairs to ballerina map.
     */
    public void addMapStructKeyValue() {
        modelBuilder.addKeyValueExpr(null, null);
    }

    /**
     * Create a ballerina variable.
     *
     * @param varName       name of the variable
     * @param exprAvailable expression availability
     */
    public void createVariable(String varName, boolean exprAvailable) {
        SimpleTypeName typeName = typeNameStack.pop();

        modelBuilder.addVariableDefinitionStmt(null, null, typeName, varName, exprAvailable);
    }

    /**
     * Create a name reference. This will be used to call ballerina connector functions, native functions etc..
     *
     * @param pkgName package name
     * @param name    functionality name you want to use
     */
    public void createNameReference(String pkgName, String name) {
        BLangModelBuilder.NameReference nameReference;

        nameReference = new BLangModelBuilder.NameReference(pkgName, name);
        modelBuilder.validateAndSetPackagePath(null, nameReference);
        nameReferenceStack.push(nameReference);
    }

    /**
     * Start the expression list.
     */
    public void startExprList() {
        modelBuilder.startExprList();
    }

    /**
     * Mark end of expression list.
     *
     * @param noOfArguments number of arguments
     */
    public void endExprList(int noOfArguments) {
        modelBuilder.endExprList(noOfArguments);
    }

    /**
     * Needs to be used when creating variable references.
     */
    public void createSimpleVarRefExpr() {
        BLangModelBuilder.NameReference nameReference = nameReferenceStack.pop();
        modelBuilder.resolvePackageFromNameReference(nameReference);
        // simple variable ref whitespaces are already captured through name ref
        modelBuilder.createSimpleVarRefExpr(null, nameReference.getWhiteSpaceDescriptor(), nameReference);
    }

    /**
     * Create a string literal.
     *
     * @param stringLiteral string literal
     */
    public void createStringLiteral(String stringLiteral) {
        modelBuilder.createStringLiteral(null, null, stringLiteral);
    }

    /**
     * Create an integer literal.
     *
     * @param intLiteral integer value
     */
    public void createIntegerLiteral(String intLiteral) {
        modelBuilder.createIntegerLiteral(null, null, intLiteral);
    }

    /**
     * Will be used when calling a function.
     *
     * @param argsAvailable arguments available or not
     */
    public void createFunctionInvocation(boolean argsAvailable) {
        modelBuilder.createFunctionInvocationStmt(null, null, argsAvailable);
    }

    /**
     * Create reply statement.
     */
    public void createReplyStatement() {
        modelBuilder.createReplyStmt(null, null);
    }

    /**
     * Initialize ballerina connector.
     *
     * @param argsAvailable arguments available or not.
     */
    public void initializeConnector(boolean argsAvailable) {
        BLangModelBuilder.NameReference nameReference = nameReferenceStack.pop();
        SimpleTypeName connectorTypeName = new SimpleTypeName(nameReference.getName(), nameReference.getPackageName(),
                null);

        modelBuilder.createConnectorInitExpr(null, null, connectorTypeName, argsAvailable);
    }

    /**
     * Use reference of a variable.
     */
    public void createVariableRefList() {
        modelBuilder.startVarRefList();
    }

    /**
     * This will be called to mark the end of varaible reference list.
     *
     * @param noArguments number of arguments
     */
    public void endVariableRefList(int noArguments) {
        modelBuilder.endVarRefList(noArguments);
    }

    /**
     * Call connector action.
     *
     * @param actionName    action name
     * @param argsAvailable whether arguments available or not
     */
    public void createAction(String actionName, boolean argsAvailable) {
        if (processingActionInvocationStmt) {
            modelBuilder.createActionInvocationStmt(null, null);
        } else {
            BLangModelBuilder.NameReference nameReference = nameReferenceStack.pop();
            modelBuilder.addActionInvocationExpr(null, null, nameReference, actionName, argsAvailable);
        }
    }

    /**
     * This will called after calling connector action to assign the return value to a reference variable.
     */
    public void createAssignmentStatement() {
        modelBuilder.createAssignmentStmt(null, null, false);
    }

    /**
     * Return BallerinaFile object that represent the ballerina file.
     *
     * @return BallerinaFile
     */
    public BallerinaFile buildBallerinaFile() {
        BallerinaFile bFile = modelBuilder.build();
        return bFile;
    }

    /**
     * Get ballerina package map.
     *
     * @return ballerinaPackageMap
     */
    public Map<String, String> getBallerinaPackageMap() {
        return ballerinaPackageMap;
    }

    /**
     * Add a comment.
     *
     * @param comment comment in string format
     */
    public void addComment(String comment) {
        modelBuilder.addCommentStmt(null, null, comment);
    }

    /**
     * Invoke ballerina native function.
     *
     * @param argsAvailable arguments availability
     */
    public void addFunctionInvocationStatement(boolean argsAvailable) {
        modelBuilder.createFunctionInvocationStmt(null, null, argsAvailable);
    }

    /**
     * Call ballerina worker.
     *
     * @param workerName worker name
     */
    public void createWorkerInvocationStmt(String workerName) {
        modelBuilder.createWorkerInvocationStmt(workerName, null, null);
    }

    /**
     * Enter ballerina worker declaration.
     */
    public void enterWorkerDeclaration() {
        modelBuilder.startWorkerUnit();
        modelBuilder.startCallableUnitBody();
    }

    /**
     * Create worker definition.
     *
     * @param workerName worker name
     */
    public void createWorkerDefinition(String workerName) {
        modelBuilder.createWorkerDefinition(null, workerName);
    }

    /**
     * Create worker reply statement.
     *
     * @param defaultWorkerName default name of the worker
     */
    public void exitWorkerReply(String defaultWorkerName) {
        modelBuilder.createWorkerReplyStmt(defaultWorkerName, null, null);
    }

    /**
     * Exit worker declaration.
     *
     * @param workerName worker name
     */
    public void exitWorkerDeclaration(String workerName) {
        modelBuilder.endCallableUnitBody(null);
        modelBuilder.createWorker(null, null, workerName);
    }

    /**
     * Enter ballerina if statement.
     */
    public void enterIfStatement() {
        modelBuilder.startIfElseStmt();
        modelBuilder.startIfClause();
    }

    /**
     * Exit ballerina if clause.
     */
    public void exitIfClause() {
        modelBuilder.addIfClause(null, null);
    }

    /**
     * Enter 'else if' clause.
     */
    public void enterElseIfClause() {
        modelBuilder.startElseIfClause();
    }

    /**
     * Exit 'else if' clause.
     */
    public void exitElseIfClause() {
        modelBuilder.addElseIfClause(null, null);
    }

    /**
     * Enter else clause.
     */
    public void enterElseClause() {
        modelBuilder.startElseClause();
    }

    /**
     * Exit else clause.
     */
    public void exitElseClause() {
        modelBuilder.addElseClause(null, null);
    }

    /**
     * End of if else. This will be used after else's exit.
     */
    public void exitIfElseStatement() {
        modelBuilder.addIfElseStmt(null);
    }

    /**
     * Invoke ballerina function.
     *
     * @param argsAvailable argument availability
     */
    public void addFunctionInvocationExpression(boolean argsAvailable) {
        modelBuilder.addFunctionInvocationExpr(null, null, argsAvailable);
    }

    /**
     * Return from a function. Only support one return type for the moment.
     */
    public void addReturnTypes() {
        SimpleTypeName[] list = new SimpleTypeName[1];
        for (int i = 0; i >= 0; i--) {
            list[i] = typeNameStack.pop();
        }
        modelBuilder.addReturnTypes(null, list);
    }

}
