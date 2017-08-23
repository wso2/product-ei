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
import org.wso2.ei.tools.converter.common.util.Property;

import java.util.Map;

/**
 * Represent ballerina resource.
 */
public class Resource {

    /**
     * Start ballerina resource.
     *
     * @param ballerinaASTModelBuilder High level API to build ballerina model
     */
    public static void startResource(BallerinaASTModelBuilder ballerinaASTModelBuilder) {
        ballerinaASTModelBuilder.startResource();
    }

    /**
     * End of ballerina resource.
     *
     * @param ballerinaASTModelBuilder High level API to build ballerina model
     * @param parameters               parameters needed to end ballerina resource
     */
    public static void endOfResource(BallerinaASTModelBuilder ballerinaASTModelBuilder,
            Map<Property, Object> parameters) {
        ballerinaASTModelBuilder.endOfResource((String) parameters.get(Property.RESOURCE_NAME),
                (Integer) parameters.get(Property.RESOURCE_ANNOTATION_COUNT));
    }

    /**
     * Start the body of ballerina resource.
     *
     * @param ballerinaASTModelBuilder High level API to build ballerina model
     */
    public static void startCallableBody(BallerinaASTModelBuilder ballerinaASTModelBuilder) {
        ballerinaASTModelBuilder.startCallableBody();
    }

}
