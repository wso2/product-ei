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

package org.wso2.ei.tools.converter.common.ballerinahelper;

import org.wso2.ei.tools.converter.common.builder.BallerinaASTModelBuilder;
import org.wso2.ei.tools.converter.common.generator.Constants;
import org.wso2.ei.tools.converter.common.util.Constant;

import java.util.Map;

/**
 * Define ballerina helper methods
 */
public class BallerinaProgramHelper {

    /**
     * Add Import
     *
     * @param ballerinaASTModelBuilder
     * @param packageName
     * @param importTracker
     */
    public static void addImport(BallerinaASTModelBuilder ballerinaASTModelBuilder, String packageName,
            Map<String, Boolean> importTracker) {
        if (importTracker.isEmpty() || importTracker.get(packageName) == null) {
            ballerinaASTModelBuilder
                    .addImportPackage(ballerinaASTModelBuilder.getBallerinaPackageMap().get(packageName), null);
            importTracker.put(packageName, true);
        }
    }

    /**
     * Create Ballerina reply statement
     *
     * @param ballerinaASTModelBuilder
     * @param parameters
     */
    public static void createReply(BallerinaASTModelBuilder ballerinaASTModelBuilder, Map<String, Object> parameters) {
        ballerinaASTModelBuilder.createNameReference(null, (String) parameters.get(Constant.OUTBOUND_MSG));
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.createReplyStatement();
        ballerinaASTModelBuilder.endCallableBody();
    }

    /**
     * Create empty map
     *
     * @param ballerinaASTModelBuilder
     * @param typeOfTheParamater
     * @param variableName
     * @param exprAvailable
     * @return
     */
    public static String createVariableWithEmptyMap(BallerinaASTModelBuilder ballerinaASTModelBuilder,
            String typeOfTheParamater, String variableName, boolean exprAvailable) {
        ballerinaASTModelBuilder.addTypes(typeOfTheParamater);
        ballerinaASTModelBuilder.startMapStructLiteral();
        ballerinaASTModelBuilder.createMapStructLiteral();
        ballerinaASTModelBuilder.createVariable(variableName, exprAvailable);
        String outboundMsg = variableName;
        return outboundMsg;
    }

    /**
     * Create ballerina map and initialize it with values
     *
     * @param ballerinaASTModelBuilder
     * @param parameters
     */
    public static void createAndInitializeMap(BallerinaASTModelBuilder ballerinaASTModelBuilder,
            Map<String, Object> parameters) {

        ballerinaASTModelBuilder.addTypes(Constant.BLANG_TYPE_MAP);
        ballerinaASTModelBuilder.startMapStructLiteral();

        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            ballerinaASTModelBuilder.createStringLiteral(entry.getKey());
            ballerinaASTModelBuilder.createStringLiteral((String) entry.getValue());
        }

        for (int i = 0; i < parameters.size(); i++) {
            ballerinaASTModelBuilder.addMapStructKeyValue();
        }

        ballerinaASTModelBuilder.createMapStructLiteral();
    }

    /**
     * Define function parameter
     *
     * @param ballerinaASTModelBuilder
     * @param parameters
     */
    public static void addFunctionParameter(BallerinaASTModelBuilder ballerinaASTModelBuilder,
            Map<String, Object> parameters) {
        ballerinaASTModelBuilder.addTypes((String) parameters.get(Constant.TYPE)); //type of the parameter
        ballerinaASTModelBuilder.addParameter(0, false, (String) parameters.get(Constant.INBOUND_MSG));
    }

    public static void enterIfStatement(BallerinaASTModelBuilder ballerinaASTModelBuilder) {
        ballerinaASTModelBuilder.enterIfStatement();
    }

    public static void exitIfClause(BallerinaASTModelBuilder ballerinaASTModelBuilder) {
        ballerinaASTModelBuilder.exitIfClause();
    }

    public static void enterElseIfClause(BallerinaASTModelBuilder ballerinaASTModelBuilder) {
        ballerinaASTModelBuilder.enterElseIfClause();
    }

    public static void exitElseIfClause(BallerinaASTModelBuilder ballerinaASTModelBuilder) {
        ballerinaASTModelBuilder.exitElseIfClause();
    }

    public static void enterElseClause(BallerinaASTModelBuilder ballerinaASTModelBuilder) {
        ballerinaASTModelBuilder.enterElseClause();
    }

    public static void exitElseClause(BallerinaASTModelBuilder ballerinaASTModelBuilder) {
        ballerinaASTModelBuilder.exitElseClause();
    }

    public static void exitIfElseStatement(BallerinaASTModelBuilder ballerinaASTModelBuilder) {
        ballerinaASTModelBuilder.exitIfElseStatement();
    }

    public static void createExpression(BallerinaASTModelBuilder ballerinaASTModelBuilder,
            Map<String, Object> parameters) {
        ballerinaASTModelBuilder
                .createNameReference(null, (String) parameters.get(Constant.VARIABLE_NAME) + Constant.EQUALS_SIGN +
                        Constant.QUOTE_STR + (String) parameters.get(Constant.EXPRESSION) + Constant.QUOTE_STR);
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
    }

    /**
     * Get json or xml path value into a string variable
     */
    public static String getPathValue(BallerinaASTModelBuilder ballerinaASTModelBuilder, Map<String, Object> parameters,
            Map<String, Boolean> importTracker) {
        if (Constant.BLANG_TYPE_JSON.equals((String) parameters.get(Constant.TYPE))) {
            BallerinaProgramHelper.addImport(ballerinaASTModelBuilder, Constant.BLANG_PKG_JSON, importTracker);
        } else if (Constant.BLANG_TYPE_XML.equals((String) parameters.get(Constant.TYPE))) {
            BallerinaProgramHelper.addImport(ballerinaASTModelBuilder, Constant.BLANG_PKG_XML, importTracker);
        }

        String jsonOrXMLVarName = (String) parameters.get(Constant.VARIABLE_NAME);
        String variableName = (String) parameters.get(Constant.VARIABLE_NAME_NEW);

        ballerinaASTModelBuilder.addTypes(Constant.BLANG_TYPE_STRING); //type of the variable
        ballerinaASTModelBuilder
                .createNameReference((String) parameters.get(Constant.PACKAGE_NAME), Constant.BLANG_GET_STRING);
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.startExprList();
        ballerinaASTModelBuilder.createNameReference(null, jsonOrXMLVarName);
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.createStringLiteral((String) parameters.get(Constant.EXPRESSION));
        ballerinaASTModelBuilder.endExprList(2);
        ballerinaASTModelBuilder.addFunctionInvocationExpression(true);
        ballerinaASTModelBuilder.createVariable(variableName, true); //name of the variable
        ballerinaASTModelBuilder.addTypes(Constant.BLANG_TYPE_STRING); //type of the variable
        ballerinaASTModelBuilder.addReturnTypes();
        return variableName;
    }

    public static void addComment(BallerinaASTModelBuilder ballerinaASTModelBuilder, String comment) {
        ballerinaASTModelBuilder.addComment(Constants.START_OF_COMMENT + comment);
    }

}
