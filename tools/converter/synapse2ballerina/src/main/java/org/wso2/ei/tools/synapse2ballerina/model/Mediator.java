package org.wso2.ei.tools.synapse2ballerina.model;

import org.wso2.ei.tools.converter.common.builder.BallerinaASTModelBuilder;

import java.util.Map;

public interface Mediator {

    void build(BallerinaASTModelBuilder ballerinaASTModelBuilder, Map<String, Object> parameters);

}
