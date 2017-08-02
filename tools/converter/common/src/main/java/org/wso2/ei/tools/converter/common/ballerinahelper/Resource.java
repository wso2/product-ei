package org.wso2.ei.tools.converter.common.ballerinahelper;

import org.wso2.ei.tools.converter.common.builder.BallerinaASTModelBuilder;
import org.wso2.ei.tools.converter.common.util.Constant;

import java.util.Map;

/**
 * Represent ballerina resource
 */
public class Resource {

    public static void startResource(BallerinaASTModelBuilder ballerinaASTModelBuilder) {
        ballerinaASTModelBuilder.startResource();
    }

    public static void endOfService(BallerinaASTModelBuilder ballerinaASTModelBuilder, Map<String, Object> parameters) {
        ballerinaASTModelBuilder.endOfResource((String) parameters.get(Constant.RESOURCE_NAME),
                (Integer) parameters.get(Constant.RESOURCE_ANNOTATION_COUNT));
    }

    public static void startCallableBody(BallerinaASTModelBuilder ballerinaASTModelBuilder) {
        ballerinaASTModelBuilder.startCallableBody();
    }

}
