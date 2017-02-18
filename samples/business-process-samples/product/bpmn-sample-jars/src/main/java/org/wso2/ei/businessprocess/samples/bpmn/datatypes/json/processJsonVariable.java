/*
 *     Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *     WSO2 Inc. licenses this file to you under the Apache License,
 *     Version 2.0 (the "License"); you may not use this file except
 *     in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing,
 *    software distributed under the License is distributed on an
 *    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *    KIND, either express or implied.  See the License for the
 *    specific language governing permissions and limitations
 *    under the License.
 */

package org.wso2.ei.businessprocess.samples.bpmn.datatypes.json;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.wso2.carbon.bpmn.core.types.datatypes.json.api.JsonNodeObject;

import com.fasterxml.jackson.databind.JsonNode;

public class processJsonVariable implements JavaDelegate {

    @Override
    public void execute(DelegateExecution exec) throws Exception {
        System.out.println("Java Service Task Execution");

        //When we set native JS json variable within script task, we get it as JsonNode object
        Object jsonJSVariable = exec.getVariable("jsonJSVar");

        System.out.println("jsonJSVariable type : " +jsonJSVariable.getClass());

        if (jsonJSVariable instanceof JsonNodeObject) {
            JsonNodeObject jObject = (JsonNodeObject) jsonJSVariable;
            String fullname = jObject.get("firstName").asText() + " " + jObject.get("lastName").asText();
            System.out.println("Full Name : " + fullname);
        } else {
            System.out.println("JSON variable not found");
        }
    }

}
