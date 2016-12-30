/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.integrator.core.internal;

import com.hazelcast.core.HazelcastInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;

/**
 * Declarative service component Dispatcher component.
 */

/**
 * @scr.component name="org.wso2.carbon.integrator.internal.DispatcherComponent" immediate="true"
 * @scr.reference name="hazelcast.instance.service" interface="com.hazelcast.core.HazelcastInstance"
 * cardinality="0..1" policy="dynamic" bind="setHazelcastInstance" unbind="unsetHazelcastInstance"
 */
public class DispatcherComponent {
    private static final Log log = LogFactory.getLog(DispatcherComponent.class);

    private static HazelcastInstance hazelcastInstance;

    protected void activate(ComponentContext context) {
        log.info("Activating GoogleTokengen DS component");
    }

    protected void deactivate(ComponentContext context) {
        log.info("Deactivating Google Tokengen DS component");
    }

    protected void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        if (log.isDebugEnabled()) {
            log.debug("Setting Hazelcast instance");
        }
        DispatcherComponent.hazelcastInstance = hazelcastInstance;
    }

    protected void unsetHazelcastInstance(HazelcastInstance hazelcastInstance) {
        if (log.isDebugEnabled()) {
            log.debug("Un-setting Hazelcast instance");
        }
        DispatcherComponent.hazelcastInstance = null;
    }

    public static HazelcastInstance getHazelcastInstance() {
        return hazelcastInstance;
    }
}

