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

package org.wso2.ei.tools.synapse2ballerina.wrapper;

import org.apache.synapse.rest.Resource;
import org.wso2.ei.tools.synapse2ballerina.visitor.VisitableWrapper;
import org.wso2.ei.tools.synapse2ballerina.visitor.Visitor;

/**
 * {@code ResourceWrapper} Wrapper class for synapse Resource
 */
public class ResourceWrapper implements VisitableWrapper {

    private Resource resource;

    public ResourceWrapper(Resource original) {
        this.resource = original;
    }

    public void accept(Visitor visitor) {
        visitor.visit(resource);
    }
}
