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

import java.util.Map;

/**
 * Ballerina message related work is handled through this
 */
public class Message {

    /**
     * Get header values
     *
     * @param ballerinaASTModelBuilder
     * @param parameters
     */
    public static void getHeaderValues(BallerinaASTModelBuilder ballerinaASTModelBuilder,
            Map<String, Object> parameters) {
        ballerinaASTModelBuilder
                .addTypes(org.wso2.ei.tools.converter.common.util.Constant.BLANG_TYPE_STRING); //type of the variable
        ballerinaASTModelBuilder
                .createNameReference(org.wso2.ei.tools.converter.common.util.Constant.BLANG_PKG_MESSAGES,
                        org.wso2.ei.tools.converter.common.util.Constant.BLANG_GET_HEADER);
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.startExprList();
        ballerinaASTModelBuilder.createNameReference(null, (String) parameters.get(Constant.INBOUND_MSG));
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.createStringLiteral((String) parameters.get(Constant.HEADER_NAME));
        ballerinaASTModelBuilder.endExprList(2);
        ballerinaASTModelBuilder.addFunctionInvocationExpression(true);
        ballerinaASTModelBuilder
                .createVariable((String) parameters.get(Constant.VARIABLE_NAME), true); //name of the variable
        //  ballerinaASTModelBuilder.addParameter(0,true,inboundMsg);
        ballerinaASTModelBuilder
                .addTypes(org.wso2.ei.tools.converter.common.util.Constant.BLANG_TYPE_STRING); //type of the variable
        ballerinaASTModelBuilder.addReturnTypes();
    }

    /**
     * Get payload
     *
     * @param ballerinaASTModelBuilder
     * @param parameters
     */
    public static void getPayload(BallerinaASTModelBuilder ballerinaASTModelBuilder, Map<String, Object> parameters) {
        ballerinaASTModelBuilder.addTypes((String) parameters.get(Constant.TYPE)); //type of the variable
        ballerinaASTModelBuilder
                .createNameReference(org.wso2.ei.tools.converter.common.util.Constant.BLANG_PKG_MESSAGES,
                        (String) parameters.get(Constant.FUNCTION_NAME));
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.startExprList();
        ballerinaASTModelBuilder.createNameReference(null, (String) parameters.get(Constant.INBOUND_MSG));
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.endExprList(1);
        ballerinaASTModelBuilder.addFunctionInvocationExpression(true);
        ballerinaASTModelBuilder
                .createVariable((String) parameters.get(Constant.VARIABLE_NAME), true); //name of the variable
        ballerinaASTModelBuilder.addTypes((String) parameters.get(Constant.TYPE)); //type of the variable
        ballerinaASTModelBuilder.addReturnTypes();
    }

    /**
     * Set the payload of a message
     */
    public static void setPayload(BallerinaASTModelBuilder ballerinaASTModelBuilder, Map<String, Object> parameters) {

        if (Constant.JSON.equals((String) parameters.get(Constant.TYPE))) {
            ballerinaASTModelBuilder.addComment(Constant.BLANG_COMMENT_JSON);
            ballerinaASTModelBuilder.addTypes(Constant.BLANG_TYPE_JSON); //type of the variable
            ballerinaASTModelBuilder.createStringLiteral((String) parameters.get(Constant.FORMAT));
            ballerinaASTModelBuilder.createVariable((String) parameters.get(Constant.PAYLOAD_VAR_NAME), true); //name of
            // the variable
            ballerinaASTModelBuilder.createNameReference(Constant.BLANG_PKG_MESSAGES, Constant.BLANG_SET_JSON_PAYLOAD);

        } else if (Constant.XML.equals((String) parameters.get(Constant.TYPE))) {
            ballerinaASTModelBuilder.addTypes(Constant.BLANG_TYPE_XML); //type of the variable
            ballerinaASTModelBuilder.addComment("//TODO: Change the double quotes to back tick. ");
            ballerinaASTModelBuilder.createXMLLiteral((String) parameters.get(Constant.FORMAT));
            ballerinaASTModelBuilder.createVariable((String) parameters.get(Constant.PAYLOAD_VAR_NAME), true); //name of
            // the variable
            ballerinaASTModelBuilder.createNameReference(Constant.BLANG_PKG_MESSAGES, Constant.BLANG_SET_XML_PAYLOAD);
        }/* else if (Constant.STRING.equals((String) parameters.get(Constant.TYPE))) {

        }*/
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.startExprList();
        ballerinaASTModelBuilder.createNameReference(null, (String) parameters.get(Constant.OUTBOUND_MSG));
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.createNameReference(null, (String) parameters.get(Constant.PAYLOAD_VAR_NAME));
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.endExprList(2);
        ballerinaASTModelBuilder.createFunctionInvocation(true);
    }

}
