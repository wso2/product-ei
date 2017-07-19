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

/**
 * Maps the synapse to ballerina
 */
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

        ballerinaASTModelBuilder.endOfService(api.getName(), "http");

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
