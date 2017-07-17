/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.ei.tools.synapse2ballerina.model;

import org.wso2.ei.tools.converter.common.builder.BallerinaASTModelBuilder;
import org.wso2.ei.tools.synapse2ballerina.builder.Constants;

import java.util.Map;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents synapse's call mediator
 */
@XmlRootElement(name = "call")
public class CallMediator implements Mediator {

    private EndpointElement endpointElement;

    boolean blocking = false;
    private int variableCounter = 1;

    public EndpointElement getEndpointElement() {
        return endpointElement;
    }

    @XmlElement(name = "endpointElement",
                type = EndpointElement.class)
    public void setEndpointElement(EndpointElement endpointElement) {
        this.endpointElement = endpointElement;
    }

    public boolean isBlocking() {
        return blocking;
    }

    public void setBlocking(boolean blocking) {
        this.blocking = blocking;
    }

    @Override
    public void build(BallerinaASTModelBuilder ballerinaASTModelBuilder, Map<String, Object> parameters) {
        String variableName = (String) parameters.get(Constants.RESPONSE_VAR_NAME);
        String resourceParamName = (String) parameters.get(Constants.RESOURCE_PARAM_NAME);

        ballerinaASTModelBuilder.addTypes(Constants.BLANG_TYPE_MESSAGE);
     //   ballerinaASTModelBuilder.addMapStructLiteral();
        ballerinaASTModelBuilder.createVariable(variableName, true);

        ballerinaASTModelBuilder.createNameReference(Constants.BLANG_HTTP, Constants.BLANG_CLIENT_CONNECTOR);
        ballerinaASTModelBuilder.createRefereceTypeName();
        /*Create an object out of above created ref type and initialize it with values*/
        ballerinaASTModelBuilder.createNameReference(Constants.BLANG_HTTP, Constants.BLANG_CLIENT_CONNECTOR);
        ballerinaASTModelBuilder.startExprList();
        ballerinaASTModelBuilder.createStringLiteral(getURL());
        ballerinaASTModelBuilder.endExprList(1); // no of arguments

        ballerinaASTModelBuilder.initializeConnector(true); //arguments available
        String connectorVarName = Constants.BLANG_CONNECT_VARIABLE_NAME + ++variableCounter;
        ballerinaASTModelBuilder.createVariable(connectorVarName, true);

        ballerinaASTModelBuilder.createVariableRefList();
        ballerinaASTModelBuilder.createNameReference(null, variableName);
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.endVariableRefList(1);
        ballerinaASTModelBuilder.createNameReference(Constants.BLANG_HTTP, Constants.BLANG_CLIENT_CONNECTOR);
        ballerinaASTModelBuilder.startExprList();
        ballerinaASTModelBuilder.createNameReference(null, connectorVarName);
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.createStringLiteral("/"); // todo: get it from the URL mapping or split the endpoint
        ballerinaASTModelBuilder.createNameReference(null, resourceParamName);
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.endVariableRefList(3);
        ballerinaASTModelBuilder.createAction(Constants.BLANG_CLIENT_CONNECTOR_GET_ACTION, true);
        ballerinaASTModelBuilder.createAssignmentStatement();
    }

    private String getURL() {
        if (endpointElement == null) {
            //log endpointElement cannot be null if we are calling this method.
            return null;
        }
        Endpoint endpoint = endpointElement.getEndpoint();
        if (endpoint instanceof HttpEndpoint) {
            HttpEndpoint httpEndpoint = (HttpEndpoint) endpoint;
            return httpEndpoint.getUriTemplate();
        }

        //do later
        return null;
    }

}
