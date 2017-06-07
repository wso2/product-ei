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
