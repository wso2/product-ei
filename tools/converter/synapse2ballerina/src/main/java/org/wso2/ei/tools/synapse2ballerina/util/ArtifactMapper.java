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

package org.wso2.ei.tools.synapse2ballerina.util;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Synapse mediator mapper for internal wrapper classes
 */
public enum ArtifactMapper {

    CALL_MEDIATOR("CallMediator", "org.wso2.ei.tools.synapse2ballerina.wrapper.CallMediatorWrapper"),
    RESPOND_MEDIATOR("RespondMediator", "org.wso2.ei.tools.synapse2ballerina.wrapper.RespondMediatorWrapper"),
    PAYLOAD_FACTORY_MEDIATOR("PayloadFactoryMediator", "org.wso2.ei.tools.synapse2ballerina.wrapper"
            + ".PayloadFactoryWrapper"),
    SWITCH_MEDIATOR("SwitchMediator", "org.wso2.ei.tools.synapse2ballerina.wrapper.SwitchMediatorWrapper"),
    SEQUENCE_MEDIATOR("SequenceMediator", "org.wso2.ei.tools.synapse2ballerina.wrapper.SequenceMediatorWrapper");

    ArtifactMapper(String type, String wrapperClassName) {
        this.type = type;
        this.wrapperClassName = wrapperClassName;
    }

    private String type;
    private String wrapperClassName;
    private static final Map<String, String> ENUM_MAP;

    public String getType() {
        return type;
    }

    public String getWrapperClassName() {
        return wrapperClassName;
    }

    public static Map<String, String> getEnumMap() {
        return ENUM_MAP;
    }

    static {
        Map<String, String> map = new ConcurrentHashMap<String, String>();
        for (ArtifactMapper instance : ArtifactMapper.values()) {
            map.put(instance.getType(), instance.getWrapperClassName());
        }
        ENUM_MAP = Collections.unmodifiableMap(map);
    }
}
