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

/**
 * {@code Flow} This class represents both a flow and a private flow
 */
public class Flow extends BaseFlow implements Visitable {

    /*protected String name;
    protected LinkedList<Processor> flowProcessors; *//*All the processors inside a flow needs to be in FIFO order to
    generate Ballerina code in its proper order*//*

    public Flow() {
        flowProcessors = new LinkedList<Processor>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LinkedList<Processor> getFlowProcessors() {
        return flowProcessors;
    }

    public void addProcessor(Processor processor) {
        flowProcessors.add(processor);
    }*/

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    /**
     * Keep a list of flows separately for tree navigation
     *
     * @param dataCarrierDTO
     */
    @Override
    public void buildTree(DataCarrierDTO dataCarrierDTO) {

        BaseObject baseObj = dataCarrierDTO.getBaseObject();
        Root rootObj = dataCarrierDTO.getRootObject();

        Flow flow = (Flow) baseObj;
        if (dataCarrierDTO.isFlowStarted()) { //If the flow has just started
            rootObj.addMFlow(flow); //Add it to root's flow stack
        }
    }
}
