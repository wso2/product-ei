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
 * Represents a ballerina function.
 */
public class Function {

    /**
     * Call a ballerina function.
     *
     * @param ballerinaASTModelBuilder High level API to build ballerina model
     * @param parameters               function name and outbound message
     */
    public static void callFunction(BallerinaASTModelBuilder ballerinaASTModelBuilder,
            Map<Property, String> parameters) {
        ballerinaASTModelBuilder.createNameReference(null, parameters.get(Property.FUNCTION_NAME));
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.startExprList();
        ballerinaASTModelBuilder.createNameReference(null, parameters.get(Property.OUTBOUND_MSG));
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.endExprList(1);
        ballerinaASTModelBuilder.createFunctionInvocation(true);
    }

    /**
     * Start ballerina function creation; Function will have only message type as an argument for now.
     *
     * @param ballerinaASTModelBuilder High level API to build ballerina model
     * @param parameters               parameters needed for function creation
     */
    public static void startFunction(BallerinaASTModelBuilder ballerinaASTModelBuilder,
            Map<Property, String> parameters) {
        ballerinaASTModelBuilder.startFunction();
        ballerinaASTModelBuilder.addTypes(Constant.BLANG_TYPE_MESSAGE); //type of the parameter
        ballerinaASTModelBuilder.addParameter(0, false, parameters.get(Property.OUTBOUND_MSG));
        ballerinaASTModelBuilder.startCallableBody();
    }

    /**
     * End of ballerina function creation.
     *
     * @param ballerinaASTModelBuilder High level API to build ballerina model
     * @param parameters               parameters needed for function creation
     */
    public static void endFunction(BallerinaASTModelBuilder ballerinaASTModelBuilder,
            Map<Property, String> parameters) {
        ballerinaASTModelBuilder.endCallableBody();
        ballerinaASTModelBuilder.endOfFunction(
                parameters.get(Property.FUNCTION_NAME)); //Function name will be the same as sequence name
    }

}
