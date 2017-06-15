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

package org.wso2.ei.tools.mule2ballerina.model;

import org.wso2.ei.tools.mule2ballerina.builder.TreeBuilder;
import org.wso2.ei.tools.mule2ballerina.dto.DataCarrierDTO;
import org.wso2.ei.tools.mule2ballerina.visitor.Visitable;

import java.util.LinkedList;
import java.util.Queue;

/**
 * {@code Processor} class represents any mule processor element
 */
public interface Processor extends Visitable, TreeBuilder {

    String getConfigName();

    /**
     * If the intermediate object is a processor within a flow add it to the correct flow
     * If it's an inbound connector, add the flow which has that connector to the global config map
     *
     * @param dataCarrierDTO
     */
    @Override
    public default void buildTree(DataCarrierDTO dataCarrierDTO) {
        BaseObject muleObj = dataCarrierDTO.getBaseObject();
        Root rootObj = dataCarrierDTO.getRootObject();

        if (dataCarrierDTO.isFlowStarted()) {
            Flow lastAddedFlow = rootObj.getFlowList().peek();
            lastAddedFlow.addProcessor((Processor) muleObj);
            if (lastAddedFlow.getFlowProcessors().size() < 2) { //If this is the first processor
                if (muleObj instanceof Inbound) {
                    Queue<Flow> flowQueue = null;
                    if (rootObj.getServiceMap() != null) {
                        Inbound inboundObj = (Inbound) muleObj;
                        flowQueue = rootObj.getServiceMap().get(inboundObj.getName());
                        if (flowQueue == null) {
                            flowQueue = new LinkedList<Flow>();
                            flowQueue.add(lastAddedFlow);
                            rootObj.getServiceMap().put(inboundObj.getName(), flowQueue);
                        } else {
                            flowQueue.add(lastAddedFlow);
                        }
                    }
                } else {
                    //If there are no inbound connectors and if this is the first processor that means this needs to
                    // be added to a private flow and remove it from the main flow stack
                    rootObj.getFlowList().pop();
                    rootObj.addPrivateFlow(lastAddedFlow.getName(), lastAddedFlow); //This might not need
                    rootObj.addPrivateFlow(lastAddedFlow);
                }
            }
        } else if (dataCarrierDTO.isSubFlowStarted()) {
            SubFlow lastAddedSubFlow = rootObj.getSubFlowStack().peek();
            lastAddedSubFlow.addProcessor((Processor) muleObj);
        }
    }
}
