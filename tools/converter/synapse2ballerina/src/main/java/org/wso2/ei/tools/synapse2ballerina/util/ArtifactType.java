package org.wso2.ei.tools.synapse2ballerina.util;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Synapse artifact types
 */
public enum ArtifactType {

    API("synapse/api"),
    ENDPOINT("synapse/endpoint");

    ArtifactType(String type) {
        this.type = type;
    }

    private String type;
    private static final Map<String, ArtifactType> ENUM_MAP;

    public String getType() {
        return type;
    }

    // Build an immutable map of String name to enum pairs.
    static {
        Map<String, ArtifactType> map = new ConcurrentHashMap<String, ArtifactType>();
        for (ArtifactType instance : ArtifactType.values()) {
            map.put(instance.getType(), instance);
        }
        ENUM_MAP = Collections.unmodifiableMap(map);
    }

    public static ArtifactType get(String type) {
        return ENUM_MAP.get(type);
    }

}
