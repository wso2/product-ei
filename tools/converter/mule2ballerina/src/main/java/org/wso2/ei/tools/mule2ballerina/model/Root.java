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
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * {@code Root} This is the root of the intermediate object model.
 */
public class Root extends BaseObject implements Visitable {

    private List<GlobalConfiguration> globalConfigurations;
    private Stack<Flow> flowList; //Flows in LIFO order
    private Map<String, GlobalConfiguration> configMap; //All global configurations against it's name
    private Map<String, Queue<Flow>> serviceMap; //Map of services and it's resources maintained as a queue
    private Map<String, SubFlow> subFlowMap; //All subflows against their names
    private Stack<SubFlow> subFlowStack; //Subflows maintained in LIFO order
    private Map<String, Flow> privateFlowMap; //Map of private flows against their names
    private Stack<Flow> privateFlowList; //Private flow list in LIFO order
    private List<AsynchronousTask> asyncTaskList;
    private Stack<Scope> scopeStack;

    public Root() {
        flowList = new Stack<Flow>();
        globalConfigurations = new ArrayList<GlobalConfiguration>();
        configMap = new HashMap<String, GlobalConfiguration>();
        serviceMap = new HashMap<String, Queue<Flow>>();
        subFlowMap = new HashMap<String, SubFlow>();
        subFlowStack = new Stack<SubFlow>();
        privateFlowMap = new HashMap<String, Flow>();
        privateFlowList = new Stack<Flow>();
        asyncTaskList = new CopyOnWriteArrayList<AsynchronousTask>();
        scopeStack = new Stack<Scope>();
    }

    public List<GlobalConfiguration> getGlobalConfigurations() {
        return globalConfigurations;
    }

    public Stack<Flow> getFlowList() {
        return flowList;
    }

    /**
     * Add global configuration to the list.
     *
     * @param globalConfiguration
     */
    public void addGlobalConfiguration(GlobalConfiguration globalConfiguration) {
        this.globalConfigurations.add(globalConfiguration);
    }

    /* Maintain main flows in LIFO order */
    public void addMFlow(Flow flow) {
        this.flowList.add(flow);
    }

    /**
     * Add private flow to private flow list.
     *
     * @param privateFlow private flow
     */
    public void addToPrivateFlowStack(Flow privateFlow) {
        this.privateFlowList.add(privateFlow);
    }

    /**
     * Maintain global configuration map to keep configurations against it's name.
     *
     * @param name          global configuration name
     * @param configuration global configuration
     */
    public void addGlobalConfigurationMap(String name, GlobalConfiguration configuration) {
        configMap.put(name, configuration);
    }

    /**
     * Keep sub flows against their name.
     *
     * @param name    sub flow name
     * @param subFlow sub flow
     */
    public void addSubFlow(String name, SubFlow subFlow) {
        SubFlow subFlowRef = subFlowMap.get(name);
        if (subFlowRef == null) {
            subFlowMap.put(name, subFlow);
        }
        subFlowStack.add(subFlow);
    }

    /**
     * Maintain private flow map.
     *
     * @param name        private flow name
     * @param privateFlow private flow
     */
    public void addPrivateFlow(String name, Flow privateFlow) {
        Flow privateFlowRef = privateFlowMap.get(name);
        if (privateFlowRef == null) {
            privateFlowMap.put(name, privateFlow);
        }
    }

    /**
     * Maintain scope stack.
     *
     * @param scopeProcessor mule scope
     */
    public void addToScopeStack(Scope scopeProcessor) {
        scopeStack.add(scopeProcessor);
    }

    /**
     * Add async task to async list.
     *
     * @param task async task
     */
    public void addAsynchronousTask(AsynchronousTask task) {
        asyncTaskList.add(task);
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

    public List<AsynchronousTask> getAsyncTaskList() {
        return asyncTaskList;
    }

    public Stack<Scope> getScopeStack() {
        return scopeStack;
    }

    /**
     * Visit Root object.
     *
     * @param visitor Visitor object
     */
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
