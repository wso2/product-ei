/*
 *     Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.ei.bpmn.analytics.core.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.wso2.ei.bpmn.analytics.core.exceptions.BPMNAnalyticsCoreException;
import org.wso2.ei.bpmn.analytics.core.services.BPMNAnalyticsCoreServer;
import org.wso2.ei.bpmn.analytics.core.services.BPMNAnalyticsCoreService;
import org.wso2.carbon.registry.core.service.RegistryService;
import org.wso2.carbon.user.core.service.RealmService;

/**
 * @scr.component name="org.wso2.ei.bpmn.analytics.core.internal.BPMNAnalyticsCoreServiceComponent" immediate="true"
 * @scr.reference name="realm.service" interface="org.wso2.carbon.user.core.service.RealmService"
 * cardinality="1..1" policy="dynamic" bind="setRealmService" unbind="unsetRealmService"
 * @scr.reference name="registry.service" interface="org.wso2.carbon.registry.core.service.RegistryService"
 * cardinality="1..1" policy="dynamic"  bind="setRegistryService" unbind="unsetRegistryService"
 */
public class BPMNAnalyticsCoreServiceComponent {
    private static Log log = LogFactory.getLog(BPMNAnalyticsCoreServiceComponent.class);

    protected void activate(ComponentContext ctxt) {
        log.info("Initializing the BPMN Analytics Core component");
        try {
            BPMNAnalyticsCoreServerHolder bpmnAnalyticsCoreHolder = BPMNAnalyticsCoreServerHolder.getInstance();
            initAnalyticsServer(bpmnAnalyticsCoreHolder);
            BPMNAnalyticsCoreService bpsAnalyticsService = new BPMNAnalyticsCoreService();
            bpsAnalyticsService.setBPMNAnalyticsCoreServer(
                    bpmnAnalyticsCoreHolder.getBPMNAnalyticsCoreServer());
            // Register BPS analytics Service OSGI Service
            ctxt.getBundleContext().registerService(BPMNAnalyticsCoreService.class.getName(),
                                                    bpsAnalyticsService, null);
        } catch (Throwable e) {
            log.error("Failed to initialize the BPMN Analytics Core Service component.", e);
        }
    }

    /**
     * Set RegistryService instance when bundle get bind to OSGI runtime.
     *
     * @param registryService
     */
    public void setRegistryService(RegistryService registryService) {
        BPMNAnalyticsCoreServerHolder.getInstance().setRegistryService(registryService);
    }

    /**
     * Unset RegistryService instance when bundle get unbind from OSGI runtime.
     *
     * @param registryService
     */
    public void unsetRegistryService(RegistryService registryService) {
        BPMNAnalyticsCoreServerHolder.getInstance().setRegistryService(null);
    }

    /**
     * Set RealmService instance when bundle get bind to OSGI runtime.
     *
     * @param realmService
     */
    public void setRealmService(RealmService realmService) {
        BPMNAnalyticsCoreServerHolder.getInstance().setRealmService(realmService);
    }

    /**
     * Unset RealmService instance when bundle get unbind from OSGI runtime.
     *
     * @param realmService
     */
    public void unsetRealmService(RealmService realmService) {
        BPMNAnalyticsCoreServerHolder.getInstance().setRealmService(null);
    }

    // Initializing the analytics server .
    private void initAnalyticsServer(BPMNAnalyticsCoreServerHolder bpmnAnalyticsHolder)
            throws BPMNAnalyticsCoreException {
        bpmnAnalyticsHolder.setBPMNAnalyticsCoreServer(new BPMNAnalyticsCoreServer());
        bpmnAnalyticsHolder.getBPMNAnalyticsCoreServer().init();
    }

}
