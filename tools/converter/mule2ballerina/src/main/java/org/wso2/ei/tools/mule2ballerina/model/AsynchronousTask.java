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

import org.wso2.ei.tools.mule2ballerina.dto.DataCarrierDTO;
import org.wso2.ei.tools.mule2ballerina.visitor.Visitable;
import org.wso2.ei.tools.mule2ballerina.visitor.Visitor;

import java.util.LinkedList;

/**
 * {@code AsynchronousTask} represents mule async element
 */
public class AsynchronousTask extends BaseObject implements Visitable, Processor {

    private String name;
    protected LinkedList<Processor> asyncProcessors;

    public AsynchronousTask() {
        asyncProcessors = new LinkedList<Processor>();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getConfigName() {
        return null;
    }

    public void addProcessor(Processor processor) {
        asyncProcessors.add(processor);
    }

    public LinkedList<Processor> getAsyncProcessors() {
        return asyncProcessors;
    }

    @Override
    public void buildTree(DataCarrierDTO dataCarrierDTO) {
        BaseObject baseObj = dataCarrierDTO.getBaseObject();
        Root rootObj = dataCarrierDTO.getRootObject();

        Flow lastAddedFlow = rootObj.getFlowList().peek(); //Get the last added flow from flow stack
        //Add processor to processor queue
        if (dataCarrierDTO.isAsyncFlowStarted()) { //If the async flow has just started
            lastAddedFlow.addProcessor((Processor) baseObj); //Add it as a processor
        }
    }
}
