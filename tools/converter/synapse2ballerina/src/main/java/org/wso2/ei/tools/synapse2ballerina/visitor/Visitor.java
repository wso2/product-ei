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

package org.wso2.ei.tools.synapse2ballerina.visitor;

import org.apache.synapse.Mediator;
import org.apache.synapse.core.axis2.ProxyService;
import org.apache.synapse.mediators.base.SequenceMediator;
import org.apache.synapse.mediators.builtin.CallMediator;
import org.apache.synapse.mediators.builtin.RespondMediator;
import org.apache.synapse.mediators.filters.SwitchMediator;
import org.apache.synapse.mediators.transform.PayloadFactoryMediator;
import org.apache.synapse.rest.API;
import org.apache.synapse.rest.Resource;

/**
 * {@code Visitor} interface for SynapseConfigVisitor
 */
public interface Visitor {

     void visit(API api);

     void visit(ProxyService proxyService);

     void visit(Resource api);

     void visit(SequenceMediator sequenceMediator);

     void visit(Mediator mediator);

     void visit(CallMediator callMediator);

     void visit(RespondMediator respondMediator);

     void visit(PayloadFactoryMediator payloadFactoryMediator);

     void visit(SwitchMediator switchMediator);
}
