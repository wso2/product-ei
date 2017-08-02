package org.wso2.ei.tools.converter.common.ballerinahelper;

import org.wso2.ei.tools.converter.common.builder.BallerinaASTModelBuilder;
import org.wso2.ei.tools.converter.common.util.Constant;

import java.util.Map;

/**
 * Represent a ballerina service
 */
public class Service {

    public static void startService(BallerinaASTModelBuilder ballerinaASTModelBuilder) {
        ballerinaASTModelBuilder.startService();
    }

    public static void endOfService(BallerinaASTModelBuilder ballerinaASTModelBuilder, Map<String, Object> parameters) {
        ballerinaASTModelBuilder.endOfService((String) parameters.get(Constant.SERVICE_NAME),
                (String) parameters.get(Constant.PROTOCOL_PKG_NAME));
    }

}
