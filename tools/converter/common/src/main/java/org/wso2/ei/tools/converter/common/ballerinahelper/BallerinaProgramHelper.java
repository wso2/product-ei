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
import org.wso2.ei.tools.converter.common.util.Property;

import java.util.Map;

/**
 * Define ballerina helper methods.
 */
public class BallerinaProgramHelper {

    /**
     * Add Import.
     *
     * @param ballerinaASTModelBuilder High level API to build ballerina model
     * @param packageName              Ballerina name reference
     * @param importTracker            To track whether the ballerina package has already been imported
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
     * Create Ballerina reply statement.
     *
     * @param ballerinaASTModelBuilder High level API to build ballerina model
     * @param parameters               Dynamically created variables from converter
     */
    public static void createReply(BallerinaASTModelBuilder ballerinaASTModelBuilder,
            Map<Property, String> parameters) {
        ballerinaASTModelBuilder.createNameReference(null, parameters.get(Property.OUTBOUND_MSG));
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.createReplyStatement();
        ballerinaASTModelBuilder.endCallableBody();
    }

    /**
     * Create empty map.
     *
     * @param ballerinaASTModelBuilder High level API to build ballerina model
     * @param typeOfTheParamater       Type of the parameter
     * @param variableName             Variable name
     * @param exprAvailable            Expression availability
     */
    public static void createVariableWithEmptyMap(BallerinaASTModelBuilder ballerinaASTModelBuilder,
            String typeOfTheParamater, String variableName, boolean exprAvailable) {
        ballerinaASTModelBuilder.addTypes(typeOfTheParamater);
        ballerinaASTModelBuilder.startMapStructLiteral();
        ballerinaASTModelBuilder.createMapStructLiteral();
        ballerinaASTModelBuilder.createVariable(variableName, exprAvailable);
    }

    /**
     * Create ballerina map and initialize it with values.
     *
     * @param ballerinaASTModelBuilder High level API to build ballerina model
     * @param parameters               Key value pairs of ballerina map
     */
    public static void createAndInitializeMap(BallerinaASTModelBuilder ballerinaASTModelBuilder,
            Map<String, String> parameters) {
        ballerinaASTModelBuilder.addTypes(Constant.BLANG_TYPE_MAP);
        ballerinaASTModelBuilder.startMapStructLiteral();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            ballerinaASTModelBuilder.createStringLiteral(entry.getKey());
            ballerinaASTModelBuilder.createStringLiteral(entry.getValue());
            ballerinaASTModelBuilder.addMapStructKeyValue();
        }
        ballerinaASTModelBuilder.createMapStructLiteral();
    }

    /**
     * Define function parameter.
     *
     * @param ballerinaASTModelBuilder High level API to build ballerina model
     * @param parameters               Parameters needed to create an argument to a function
     */
    public static void addFunctionParameter(BallerinaASTModelBuilder ballerinaASTModelBuilder,
            Map<Property, String> parameters) {
        ballerinaASTModelBuilder.addTypes(parameters.get(Property.TYPE)); //type of the parameter
        ballerinaASTModelBuilder.addParameter(0, false, parameters.get(Property.INBOUND_MSG));
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

    /**
     * Create an expression (eg:- variable_name == "expression").
     *
     * @param ballerinaASTModelBuilder High level API to build ballerina model
     * @param parameters               Parameters needed to create the expression
     */
    public static void createExpression(BallerinaASTModelBuilder ballerinaASTModelBuilder,
            Map<Property, String> parameters) {
        ballerinaASTModelBuilder
                .createNameReference(null, parameters.get(Property.VARIABLE_NAME) + Constant.EQUALS_SIGN +
                        Constant.QUOTE_STR + parameters.get(Property.EXPRESSION) + Constant.QUOTE_STR);
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
    }

    /**
     * Given a ballerina json or a xml variable get the string value from xpath or json path.
     *
     * @param ballerinaASTModelBuilder High level API to build ballerina model
     * @param parameters               Dynamically created variable names, ballerina package name and expression
     * @param importTracker            track whether the ballerina package has been already imported or not
     */
    public static void getPathValue(BallerinaASTModelBuilder ballerinaASTModelBuilder, Map<Property, String> parameters,
            Map<String, Boolean> importTracker) {
        if (Constant.BLANG_TYPE_JSON.equals(parameters.get(Property.TYPE))) {
            BallerinaProgramHelper.addImport(ballerinaASTModelBuilder, Constant.BLANG_PKG_JSON, importTracker);
        } else if (Constant.BLANG_TYPE_XML.equals(parameters.get(Property.TYPE))) {
            BallerinaProgramHelper.addImport(ballerinaASTModelBuilder, Constant.BLANG_PKG_XML, importTracker);
        }

        String jsonOrXMLVarName = parameters.get(Property.VARIABLE_NAME);
        String variableName = parameters.get(Property.VARIABLE_NAME_NEW);

        ballerinaASTModelBuilder.addTypes(Constant.BLANG_TYPE_STRING); //type of the variable
        ballerinaASTModelBuilder.createNameReference(parameters.get(Property.PACKAGE_NAME), Constant.BLANG_GET_STRING);
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.startExprList();
        ballerinaASTModelBuilder.createNameReference(null, jsonOrXMLVarName);
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.createStringLiteral(parameters.get(Property.EXPRESSION));
        ballerinaASTModelBuilder.endExprList(2);
        ballerinaASTModelBuilder.addFunctionInvocationExpression(true);
        ballerinaASTModelBuilder.createVariable(variableName, true); //name of the variable
        ballerinaASTModelBuilder.addTypes(Constant.BLANG_TYPE_STRING); //type of the variable
        ballerinaASTModelBuilder.addReturnTypes();
    }

    /**
     * Add a comment in ballerina code.
     *
     * @param ballerinaASTModelBuilder High level API to build ballerina model
     * @param comment                  comment that needs to be added to ballerina code
     */
    public static void addComment(BallerinaASTModelBuilder ballerinaASTModelBuilder, String comment) {
        ballerinaASTModelBuilder.addComment(Constants.START_OF_COMMENT + comment);
    }

}
