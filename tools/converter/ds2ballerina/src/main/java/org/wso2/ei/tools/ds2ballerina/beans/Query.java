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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Object for query element.
 */
@XmlRootElement(name = "query") public class Query {

    private Map<String, Param> paramMap;

    @XmlAttribute(name = "useConfig") private String configId;

    @XmlAttribute private boolean returnUpdatedRowCount;

    @XmlAttribute private boolean returnGeneratedKeys;

    @XmlAttribute(name = "id") private String queryId;

    @XmlElement(name = "sql") private String sqlQuery;

    @XmlElement(name = "param") private ArrayList<Param> params;

    @XmlElement(name = "result") private Result result;

    @XmlElement(name = "properties")
    private Properties properties;

    public String getConfigId() {
        return configId;
    }

    public boolean isReturnUpdatedRowCount() {
        return returnUpdatedRowCount;
    }

    public String getQueryId() {
        return queryId;
    }

    public String getSqlQuery() {
        return sqlQuery;
    }

    public Result getResult() {
        return result;
    }

    public Map<String, Param> getParamMap() {
        if (paramMap == null && params != null) {
            paramMap = new HashMap<>();
            for (Param param : this.params) {
                paramMap.put(param.getName(), param);
            }
        }
        return paramMap;
    }

    public Properties getProperties() {
        return properties;
    }

    public boolean isReturnGeneratedKeys() {
        return returnGeneratedKeys;
    }
}
