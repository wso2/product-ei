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

import org.wso2.ei.tools.ds2ballerina.beans.helper.ConfigPropertyMapAdapter;

import java.util.Map;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Object for config element.
 */
@XmlRootElement(name = "config") public class Config {

    @XmlAttribute private boolean enableOData;

    @XmlAttribute private String id;

    @XmlJavaTypeAdapter(ConfigPropertyMapAdapter.class) @XmlElement(name = "property")
    private Map<String, String> propertiesMap;

    public Map<String, String> getPropertiesMap() {
        return propertiesMap;
    }

    public boolean isEnableOData() {
        return enableOData;
    }

    public String getId() {
        return id;
    }

}
