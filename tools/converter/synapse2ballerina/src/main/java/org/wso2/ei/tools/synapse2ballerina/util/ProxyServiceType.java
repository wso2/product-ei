package org.wso2.ei.tools.synapse2ballerina.util;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum ProxyServiceType {

    HTTP("http"), HTTPS("https"), JMS("jms");

    ProxyServiceType(String type) {
        this.type = type;
    }

    private String type;
    private static final Map<String, ProxyServiceType> ENUM_MAP;

    public String getType() {
        return type;
    }

    // Build an immutable map of String name to enum pairs.
    static {
        Map<String, ProxyServiceType> map = new ConcurrentHashMap<String, ProxyServiceType>();
        for (ProxyServiceType instance : ProxyServiceType.values()) {
            map.put(instance.getType(), instance);
        }
        ENUM_MAP = Collections.unmodifiableMap(map);
    }

    public static ProxyServiceType get(String type) {
        return ENUM_MAP.get(type);
    }
}
