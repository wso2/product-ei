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
 * {@code Processor} class represents any mule processor element. Any element inside a flow is considered to be a
 * message processor. This class determines where each processor belongs. Eg:- Inside flow, private flow, sub flow or
 * async flow
 */
public interface Processor extends Visitable, TreeBuilder {

    String getConfigName();

    /**
     * If the intermediate object is a processor within a flow add it to the correct flow
     * If it's an inbound connector, add the flow which has that connector to the global config map (service map in
     * root)
     *
     * @param dataCarrierDTO
     */
    @Override
    public default void buildTree(DataCarrierDTO dataCarrierDTO) {
        BaseObject baseObj = dataCarrierDTO.getBaseObject();
        Root rootObj = dataCarrierDTO.getRootObject();

        /*If flow started, but not inside an async scope*/
        if (dataCarrierDTO.isFlowStarted() && !dataCarrierDTO.isAsyncFlowStarted()) {
            Flow lastAddedFlow = rootObj.getFlowList().peek(); //Get the last added flow in flow stack
            lastAddedFlow.addProcessor((Processor) baseObj); //Add processor to processor queue
            if (lastAddedFlow.getFlowProcessors().size() < 2) { //If this is the first processor
                /*If this is an inbound connector, get the flow queue associated with it's global inbound config and
                if it's null, create a new flow queue and add the this flow to it.
                */
                if (baseObj instanceof Inbound && rootObj.getServiceMap() != null) {
                    Inbound inboundObj = (Inbound) baseObj;
                    Queue<Flow> flowQueue = rootObj.getServiceMap().get(inboundObj.getName());
                    if (flowQueue == null) {
                        flowQueue = new LinkedList<Flow>();
                        flowQueue.add(lastAddedFlow);
                        rootObj.getServiceMap().put(inboundObj.getName(), flowQueue);
                    } else {
                        flowQueue.add(lastAddedFlow);
                    }
                } else {
                    /*If this is not an inbound connector and if this is the first processor, that means this needs to
                     be added to a private flow and remove it from the main flow stack */
                    rootObj.getFlowList().pop();
                    rootObj.addPrivateFlow(lastAddedFlow.getName(), lastAddedFlow);
                    rootObj.addToPrivateFlowStack(lastAddedFlow);
                }
            }
        } else if (dataCarrierDTO.isSubFlowStarted()) { //If it's the sub flow that's been started
            SubFlow lastAddedSubFlow = rootObj.getSubFlowStack().peek();
            lastAddedSubFlow.addProcessor((Processor) baseObj); //Adds the processor to sub flow
        } else if (dataCarrierDTO.isFlowStarted() && dataCarrierDTO.isAsyncFlowStarted()) {
            //Flow started and inside async flow
            Flow lastAddedFlow = rootObj.getFlowList().peek(); //Get the last added flow in flow stack
            //Get all the processors of last added flow
            LinkedList<Processor> processors = (lastAddedFlow != null ? lastAddedFlow.getFlowProcessors() : null);
            //Get the last added processor
            Processor processor = (processors != null ? processors.getLast() : null);
            //If the last added processor is an async task
            if (processor != null && processor instanceof AsynchronousTask) {
                //Add the baseObj under asynchronous task processors
                AsynchronousTask asynchronousTask = (AsynchronousTask) processor;
                asynchronousTask.addProcessor((Processor) baseObj);
            }
        }

    }
}
