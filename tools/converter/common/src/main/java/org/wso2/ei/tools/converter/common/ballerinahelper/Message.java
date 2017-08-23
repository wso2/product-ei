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
import org.wso2.ei.tools.converter.common.util.Constant;
import org.wso2.ei.tools.converter.common.util.Property;

import java.util.Map;

/**
 * Ballerina message related work is handled through this.
 */
public class Message {

    /**
     * Get header values.
     *
     * @param ballerinaASTModelBuilder High level API to build ballerina model
     * @param parameters               parameters required to create ballerina statement to get header values from
     *                                 message
     */
    public static void getHeaderValues(BallerinaASTModelBuilder ballerinaASTModelBuilder,
            Map<Property, String> parameters) {
        ballerinaASTModelBuilder
                .addTypes(org.wso2.ei.tools.converter.common.util.Constant.BLANG_TYPE_STRING); //type of the variable
        ballerinaASTModelBuilder
                .createNameReference(org.wso2.ei.tools.converter.common.util.Constant.BLANG_PKG_MESSAGES,
                        org.wso2.ei.tools.converter.common.util.Constant.BLANG_GET_HEADER);
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.startExprList();
        ballerinaASTModelBuilder.createNameReference(null, parameters.get(Property.INBOUND_MSG));
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.createStringLiteral(parameters.get(Property.HEADER_NAME));
        ballerinaASTModelBuilder.endExprList(2);
        ballerinaASTModelBuilder.addFunctionInvocationExpression(true);
        ballerinaASTModelBuilder.createVariable(parameters.get(Property.VARIABLE_NAME), true); //name of the variable
        ballerinaASTModelBuilder
                .addTypes(org.wso2.ei.tools.converter.common.util.Constant.BLANG_TYPE_STRING); //type of the variable
        ballerinaASTModelBuilder.addReturnTypes();
    }

    /**
     * Get payload.
     *
     * @param ballerinaASTModelBuilder High level API to build ballerina model
     * @param parameters               parameters required to create ballerina statement to get payload from message
     */
    public static void getPayload(BallerinaASTModelBuilder ballerinaASTModelBuilder, Map<Property, String> parameters) {
        ballerinaASTModelBuilder.addTypes(parameters.get(Property.TYPE)); //type of the variable
        ballerinaASTModelBuilder
                .createNameReference(org.wso2.ei.tools.converter.common.util.Constant.BLANG_PKG_MESSAGES,
                        parameters.get(Property.FUNCTION_NAME));
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.startExprList();
        ballerinaASTModelBuilder.createNameReference(null, parameters.get(Property.INBOUND_MSG));
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.endExprList(1);
        ballerinaASTModelBuilder.addFunctionInvocationExpression(true);
        ballerinaASTModelBuilder.createVariable(parameters.get(Property.VARIABLE_NAME), true); //name of the variable
        ballerinaASTModelBuilder.addTypes(parameters.get(Property.TYPE)); //type of the variable
        ballerinaASTModelBuilder.addReturnTypes();
    }

    /**
     * Set the payload of a message.
     *
     * @param ballerinaASTModelBuilder High level API to build ballerina model
     * @param parameters               parameters required to create ballerina statement to set the payload of a message
     * @param isVariableCreationNeeded
     */
    public static void setPayload(BallerinaASTModelBuilder ballerinaASTModelBuilder, Map<Property, String> parameters,
            boolean isVariableCreationNeeded) {

        switch (parameters.get(Property.TYPE)) {
        case Constant.JSON:
            if (isVariableCreationNeeded) {
                ballerinaASTModelBuilder.addComment(Constant.BLANG_COMMENT_JSON);
                ballerinaASTModelBuilder.addTypes(Constant.BLANG_TYPE_JSON); //type of the variable
                ballerinaASTModelBuilder.createStringLiteral(parameters.get(Property.FORMAT));
                ballerinaASTModelBuilder.createVariable(parameters.get(Property.PAYLOAD_VAR_NAME), true);
            }
            ballerinaASTModelBuilder.createNameReference(Constant.BLANG_PKG_MESSAGES, Constant.BLANG_SET_JSON_PAYLOAD);
            break;
        case Constant.XML:
            if (isVariableCreationNeeded) {
                ballerinaASTModelBuilder.addTypes(Constant.BLANG_TYPE_STRING); //type of the variable
                ballerinaASTModelBuilder.createStringLiteral(parameters.get(Property.FORMAT));
                ballerinaASTModelBuilder.createVariable(parameters.get(Property.PAYLOAD_VAR_NAME), true);
            }
            ballerinaASTModelBuilder.createNameReference(Constant.BLANG_PKG_MESSAGES, Constant.BLANG_SET_XML_PAYLOAD);
            break;
        case Constant.STRING:
            if (isVariableCreationNeeded) {
                ballerinaASTModelBuilder.addTypes(Constant.BLANG_TYPE_STRING); //type of the variable
                ballerinaASTModelBuilder.createStringLiteral(parameters.get(Property.FORMAT));
                ballerinaASTModelBuilder.createVariable(parameters.get(Property.PAYLOAD_VAR_NAME), true);
            }
            ballerinaASTModelBuilder
                    .createNameReference(Constant.BLANG_PKG_MESSAGES, Constant.BLANG_SET_STRING_PAYLOAD);
            break;
        default:
            break;
        }

        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.startExprList();
        ballerinaASTModelBuilder.createNameReference(null, parameters.get(Property.OUTBOUND_MSG));
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.createNameReference(null, parameters.get(Property.PAYLOAD_VAR_NAME));
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.endExprList(2);
        ballerinaASTModelBuilder.createFunctionInvocation(true);
    }
}
