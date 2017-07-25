package org.wso2.ei.tools.synapse2ballerina.util;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Synapse mediator mapper for internal wrapper classes
 */
public enum ArtifactMapper {

    CALL_MEDIATOR("CallMediator", "org.wso2.ei.tools.synapse2ballerina.wrapper.CallMediatorWrapper");

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
