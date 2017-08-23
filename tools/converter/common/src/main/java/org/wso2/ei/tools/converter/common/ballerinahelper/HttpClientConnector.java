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
 * Represent Ballerina client connector.
 */
public class HttpClientConnector {

    /**
     * Create ballerina http client connector.
     *
     * @param ballerinaASTModelBuilder High level API to build ballerina model
     * @param parameters               parameters needed to create ballerina http client connector
     */
    public static void createConnector(BallerinaASTModelBuilder ballerinaASTModelBuilder,
            Map<Property, String> parameters) {
        ballerinaASTModelBuilder.createNameReference(Constant.BLANG_HTTP, Constant.BLANG_CLIENT_CONNECTOR);
        ballerinaASTModelBuilder.createRefereceTypeName();
        /*Create an object out of above created ref type and initialize it with values*/
        ballerinaASTModelBuilder.createNameReference(Constant.BLANG_HTTP, Constant.BLANG_CLIENT_CONNECTOR);
        ballerinaASTModelBuilder.startExprList();
        ballerinaASTModelBuilder.createStringLiteral(parameters.get(Property.URL));
        ballerinaASTModelBuilder.endExprList(1); // no of arguments
        ballerinaASTModelBuilder.initializeConnector(true); //arguments available
        ballerinaASTModelBuilder.createVariable(parameters.get(Property.CONNECTOR_VAR_NAME), true);
    }

    /**
     * Call Ballerina client connector connector action.
     *
     * @param ballerinaASTModelBuilder High level API to build ballerina model
     * @param parameters               parameters needed to call an action on ballerina http client connector
     */
    public static void callAction(BallerinaASTModelBuilder ballerinaASTModelBuilder, Map<Property, String> parameters) {
        //Fill LHS - Assign response to outbound message
        ballerinaASTModelBuilder.createVariableRefList();
        ballerinaASTModelBuilder.createNameReference(null, parameters.get(Property.OUTBOUND_MSG));
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.endVariableRefList(1);

        //Fill RHS - Call client connector
        ballerinaASTModelBuilder.createNameReference(null, parameters.get(Property.CONNECTOR_VAR_NAME));
        ballerinaASTModelBuilder.startExprList();
        ballerinaASTModelBuilder.createStringLiteral(parameters.get(Property.PATH));
        ballerinaASTModelBuilder.createNameReference(null, parameters.get(Property.INBOUND_MSG));
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.endVariableRefList(2);
        //TODO: Support for other http methods as well
        ballerinaASTModelBuilder.createAction(Constant.BLANG_CLIENT_CONNECTOR_GET_ACTION, true);
        ballerinaASTModelBuilder.createAssignmentStatement();
    }

}
