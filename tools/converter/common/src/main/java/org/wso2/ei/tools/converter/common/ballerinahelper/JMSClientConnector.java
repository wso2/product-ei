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
 * Represent ballerina jms connector
 */
public class JMSClientConnector {

    public static void createConnector(BallerinaASTModelBuilder ballerinaASTModelBuilder,
            Map<String, Object> parameters) {
        ballerinaASTModelBuilder.createNameReference(Constant.BLANG_PKG_JMS, Constant.BLANG_CLIENT_CONNECTOR);
        ballerinaASTModelBuilder.createRefereceTypeName();
        ballerinaASTModelBuilder.createNameReference(Constant.BLANG_PKG_JMS, Constant.BLANG_CLIENT_CONNECTOR);
        ballerinaASTModelBuilder.startExprList();
        ballerinaASTModelBuilder.createNameReference(null, (String) parameters.get(Constant.VARIABLE_NAME));
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.endExprList(1); // no of arguments
        ballerinaASTModelBuilder.initializeConnector(true); //arguments available
        ballerinaASTModelBuilder.createVariable((String) parameters.get(Constant.JMS_EP_VAR_NAME), true);
    }

    /**
     * Call jms connector's send method
     *
     * @param ballerinaASTModelBuilder
     * @param parameters
     */
    public static void callAction(BallerinaASTModelBuilder ballerinaASTModelBuilder, Map<String, Object> parameters) {
        //Fill RHS - Call client connector
        ballerinaASTModelBuilder.createNameReference(null, (String) parameters.get(Constant.JMS_EP_VAR_NAME));
        ballerinaASTModelBuilder.startExprList();
        ballerinaASTModelBuilder.createStringLiteral((String) parameters.get(Constant.JMS_QUEUE_NAME));
        ballerinaASTModelBuilder.createNameReference(null, (String) parameters.get(Constant.JMS_MSG));
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.endVariableRefList(2);
        ballerinaASTModelBuilder.createAction(Constant.BLANG_JMS_CONNECTOR_SEND_ACTION, true);
        ballerinaASTModelBuilder.setProcessingActionInvocationStmt(true);
        ballerinaASTModelBuilder.createAction(null, false);
    }


}
