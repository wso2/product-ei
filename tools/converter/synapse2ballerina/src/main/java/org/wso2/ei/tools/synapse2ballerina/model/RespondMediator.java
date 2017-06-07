package org.wso2.ei.tools.synapse2ballerina.model;

import org.wso2.ei.tools.converter.common.builder.BallerinaASTModelBuilder;
import org.wso2.ei.tools.synapse2ballerina.builder.Constants;

import java.util.Map;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="respond")
public class RespondMediator implements Mediator {

    @Override
    public void build(BallerinaASTModelBuilder ballerinaASTModelBuilder, Map<String, Object> parameters) {
        String variableName = (String) parameters.get(Constants.RESPONSE_VAR_NAME);

        ballerinaASTModelBuilder.createNameReference(null, variableName);
        ballerinaASTModelBuilder.createVariableRefExpr();
        ballerinaASTModelBuilder.createReplyStatement();

    }
}
