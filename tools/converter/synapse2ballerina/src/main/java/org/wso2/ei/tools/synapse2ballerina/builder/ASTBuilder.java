package org.wso2.ei.tools.synapse2ballerina.builder;

import org.ballerinalang.model.BallerinaFile;
import org.wso2.ei.tools.converter.common.builder.BallerinaASTModelBuilder;
import org.wso2.ei.tools.synapse2ballerina.model.API;
import org.wso2.ei.tools.synapse2ballerina.model.Mediator;
import org.wso2.ei.tools.synapse2ballerina.model.Resource;
import org.wso2.ei.tools.synapse2ballerina.model.Sequence;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ASTBuilder {

    private static String serviceName;

    private static int resourceParamNameCount = 0;
    private static Map<String, Object> parameters = new HashMap<>();
    private static Set<String> importKeys = new HashSet<>();
    private static BallerinaASTModelBuilder ballerinaASTModelBuilder = new BallerinaASTModelBuilder();
    private static int serviceAnnotationCount = 0;

    public static BallerinaFile build(API api) {
        serviceName = api.getName();
        ballerinaASTModelBuilder = new BallerinaASTModelBuilder();

        ballerinaASTModelBuilder.startService();

        addImport(Constants.BLANG_HTTP);
        ballerinaASTModelBuilder
                .createAnnotationAttachment(Constants.BLANG_HTTP, Constants.BLANG_BASEPATH, Constants.BLANG_VALUE,
                        api.getContext());
        serviceAnnotationCount++;
        ballerinaASTModelBuilder.addAnnotationAttachment(serviceAnnotationCount);

        for (Resource resource : api.getResourceList()) {
            buildResource(resource);
        }

        ballerinaASTModelBuilder.endOfService(api.getName());

        return ballerinaASTModelBuilder.buildBallerinaFile();
    }

    private static void buildResource(Resource resource) {
        ballerinaASTModelBuilder.startResource();

        ballerinaASTModelBuilder.addTypes(Constants.BLANG_TYPE_MESSAGE); //type of the parameter
        String resourceParamName = Constants.BLANG_DEFAULT_MSG_PARAM_NAME + ++resourceParamNameCount;
        parameters.put(Constants.RESOURCE_PARAM_NAME,
                resourceParamName); // todo: this will cause issue for services with multiple resources
        ballerinaASTModelBuilder.addParameter(0, false, resourceParamName);

        int resourceAnnotationCount = 0;
        for (String method : resource.getMethodList()) {
            ballerinaASTModelBuilder.createAnnotationAttachment(Constants.BLANG_HTTP, method, null, null);
            ballerinaASTModelBuilder.addAnnotationAttachment(resourceAnnotationCount);
            resourceAnnotationCount++;
        }

        ballerinaASTModelBuilder.startCallableBody();
        for (Sequence sequence : resource.getSequenceList()) {
            buildSequence(sequence);
        }
        ballerinaASTModelBuilder.endCallableBody();

        ballerinaASTModelBuilder.endOfResource(serviceName + "Resource", resourceAnnotationCount);
    }

    private static void buildSequence(Sequence sequence) {
        parameters.put(Constants.RESPONSE_VAR_NAME, Constants.BLANG_RES_VARIABLE_NAME);
        for (Mediator mediator : sequence.getMediatorList()) {
            mediator.build(ballerinaASTModelBuilder, parameters);
        }
    }

    public static void addImport(String packageName) {
        if (importKeys.add(packageName)) {
            ballerinaASTModelBuilder
                    .addImportPackage(ballerinaASTModelBuilder.getBallerinaPackageMap().get(packageName), null);
        }
    }

}
