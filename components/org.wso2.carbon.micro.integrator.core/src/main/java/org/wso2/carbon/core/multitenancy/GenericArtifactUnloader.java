/*
 * Copyright (c) 2005-2012, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.core.multitenancy;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.wso2.carbon.core.ArtifactUnloader;
import org.wso2.carbon.micro.integrator.core.internal.CarbonCoreDataHolder;

/**
 * The thread which runs the unload task using the ServiceTracker by calling unload methods of all
 * ArtifactUnloaders which are registered as OSGI services.
 */
public class GenericArtifactUnloader implements Runnable {

    @Override
    public void run() {

        BundleContext bundleContext = CarbonCoreDataHolder.getInstance().getBundleContext();
        if (bundleContext != null) {
            ServiceTracker tracker =
                    new ServiceTracker(bundleContext,
                                       ArtifactUnloader.class.getName(), null);
            tracker.open();
            Object[] services = tracker.getServices();
            if (services != null) {
                for (Object service : services) {
                    ((ArtifactUnloader) service).unload();
                }
            }
            tracker.close();
        }
    }
}
