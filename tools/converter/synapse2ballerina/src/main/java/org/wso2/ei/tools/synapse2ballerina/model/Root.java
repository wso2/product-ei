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
package org.wso2.ei.tools.synapse2ballerina.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This will hold all the information (api, proxies, endpoints etc..) in a car file
 */
public class Root {

    private List<API> apiList;
    private Map<String, Endpoint> endpointMap;

    public Root() {
        apiList = new ArrayList<API>();
        endpointMap = new HashMap<String, Endpoint>();
    }

    public List<API> getApiList() {
        return apiList;
    }

    public void addApi(API api) {
        apiList.add(api);
    }

    public Map<String, Endpoint> getEndpointMap() {
        return endpointMap;
    }

    public void setEndpointMap(Map<String, Endpoint> endpointMap) {
        this.endpointMap = endpointMap;
    }
}
