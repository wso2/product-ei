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

package org.wso2.ei.tools.ds2ballerina.beans;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Object for data service.
 */
@XmlRootElement(name = "data") public class DataService {

    @XmlAttribute private String name;
    @XmlElement(name = "config") private List<Config> configs;
    @XmlElement(name = "query") private List<Query> queries;
    @XmlElement(name = "resource") private List<Resource> resources;
    @XmlAttribute private boolean disableLegacyBoxcarringMode;
    @XmlAttribute private boolean enableBatchRequests;
    @XmlAttribute private boolean enableBoxcarring;
    @XmlAttribute private String transports;

    private Map<String, Query> queryMap = null;

    public boolean isDisableLegacyBoxcarringMode() {
        return disableLegacyBoxcarringMode;
    }

    public boolean isEnableBatchRequests() {
        return enableBatchRequests;
    }

    public boolean isEnableBoxcarring() {
        return enableBoxcarring;
    }

    public String getTransports() {
        return transports;
    }

    public String getName() {
        return name;
    }

    public List<Config> getConfigs() {
        return configs;
    }

    public Map<String, Query> getQueries() {
        if (queryMap == null) {
            queryMap = new HashMap<>();
            for (Query query : this.queries) {
                queryMap.put(query.getQueryId(), query);
            }
        }
        return queryMap;
    }

    public List<Resource> getResources() {
        return resources;
    }

}
