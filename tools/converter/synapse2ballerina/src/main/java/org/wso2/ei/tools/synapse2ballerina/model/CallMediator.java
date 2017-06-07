package org.wso2.ei.tools.synapse2ballerina.model;

import org.wso2.ei.tools.converter.common.builder.BallerinaASTModelBuilder;
import org.wso2.ei.tools.synapse2ballerina.builder.Constants;

import java.util.Map;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "call")
public class CallMediator implements Mediator {

    EndpointElement endpointElement;

    boolean blocking = false;
    private int variableCounter = 1;

    public EndpointElement getEndpointElement() {
        return endpointElement;
    }

    @XmlElement(name="endpointElement", type = EndpointElement.class)
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
        ballerinaASTModelBuilder.addMapStructLiteral();
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
        ballerinaASTModelBuilder.createVariableRefExpr();
        ballerinaASTModelBuilder.endVariableRefList(1);
        ballerinaASTModelBuilder.createNameReference(Constants.BLANG_HTTP, Constants.BLANG_CLIENT_CONNECTOR);
        ballerinaASTModelBuilder.startExprList();
        ballerinaASTModelBuilder.createNameReference(null, connectorVarName);
        ballerinaASTModelBuilder.createVariableRefExpr();
        ballerinaASTModelBuilder.createStringLiteral("/"); // todo: get it from the URL mapping or split the endpoint
        ballerinaASTModelBuilder.createNameReference(null, resourceParamName);
        ballerinaASTModelBuilder.createVariableRefExpr();
        ballerinaASTModelBuilder.endVariableRefList(3);
        ballerinaASTModelBuilder.createAction(Constants.BLANG_CLIENT_CONNECTOR_GET_ACTION, true);
        ballerinaASTModelBuilder.createAssignmentStatement();
    }

    private String getURL() {
        if(endpointElement == null) {
            //log endpointElement cannot be null if we are calling this method.
            return null;
        }
        Endpoint endpoint = endpointElement.getEndpoint();
        if(endpoint instanceof HttpEndpoint) {
            HttpEndpoint httpEndpoint = (HttpEndpoint) endpoint;
            return httpEndpoint.getUriTemplate();
        }

        //do later
        return null;
    }

}
