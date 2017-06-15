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

import org.wso2.ei.tools.mule2ballerina.visitor.Visitable;
import org.wso2.ei.tools.mule2ballerina.visitor.Visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

/**
 * {@code Root} This is the root of the intermediate object tree
 */
public class Root extends BaseObject implements Visitable {

    private List<GlobalConfiguration> globalConfigurations;
    private Stack<Flow> flowList;
    private Map<String, GlobalConfiguration> configMap;
    private Map<String, Queue<Flow>> serviceMap;
    private Map<String, SubFlow> subFlowMap;
    private Stack<SubFlow> subFlowStack;
    private Map<String, Flow> privateFlowMap;
    private Stack<Flow> privateFlowList;

    public Root() {
        flowList = new Stack<Flow>();
        globalConfigurations = new ArrayList<GlobalConfiguration>();
        configMap = new HashMap<String, GlobalConfiguration>();
        serviceMap = new HashMap<String, Queue<Flow>>();
        subFlowMap = new HashMap<String, SubFlow>();
        subFlowStack = new Stack<SubFlow>();
        privateFlowMap = new HashMap<String, Flow>();
        privateFlowList = new Stack<Flow>();
    }

    public List<GlobalConfiguration> getGlobalConfigurations() {
        return globalConfigurations;
    }

    public Stack<Flow> getFlowList() {
        return flowList;
    }

    public void addGlobalConfiguration(GlobalConfiguration globalConfiguration) {
        this.globalConfigurations.add(globalConfiguration);
    }

    public void addMFlow(Flow muleFlow) {
        this.flowList.add(muleFlow);
    }

    public void addPrivateFlow(Flow privateFlow) {
        this.privateFlowList.add(privateFlow);
    }

    public void addGlobalConfigurationMap(String name, GlobalConfiguration configuration) {
        configMap.put(name, configuration);
    }

    public void addSubFlow(String name, SubFlow subFlow) {
        SubFlow subFlowRef = subFlowMap.get(name);
        if (subFlowRef == null) {
            subFlowMap.put(name, subFlow);
        }
        subFlowStack.add(subFlow);
    }

    public void addPrivateFlow(String name, Flow privateFlow) {
        SubFlow subFlowRef = subFlowMap.get(name);
        if (subFlowRef == null) {
            privateFlowMap.put(name, privateFlow);
        }
    }

    public Map<String, GlobalConfiguration> getConfigMap() {
        return configMap;
    }

    public Map<String, Queue<Flow>> getServiceMap() {
        return serviceMap;
    }

    public Map<String, SubFlow> getSubFlowMap() {
        return subFlowMap;
    }

    public Stack<SubFlow> getSubFlowStack() {
        return subFlowStack;
    }

    public Map<String, Flow> getPrivateFlowMap() {
        return privateFlowMap;
    }

    public Stack<Flow> getPrivateFlowList() {
        return privateFlowList;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
