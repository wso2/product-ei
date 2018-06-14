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

package org.wso2.carbon.micro.integrator.core.internal;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.wso2.carbon.context.CarbonCoreInitializedEvent;
import org.wso2.carbon.context.CarbonCoreInitializedEventImpl;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.utils.deployment.GhostMetaArtifactsLoader;
import org.wso2.carbon.utils.multitenancy.GhostServiceMetaArtifactsLoader;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

import java.io.File;
import java.lang.management.ManagementPermission;
import java.security.Security;

public class Activator implements BundleActivator {

    private static Log log = LogFactory.getLog(Activator.class);

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        try {
            // Need permissions in order to activate Carbon Core
            SecurityManager secMan = System.getSecurityManager();
            if (secMan != null) {
                secMan.checkPermission(new ManagementPermission("control"));
            }
            // We assume it's super tenant during the deployment time
            PrivilegedCarbonContext privilegedCarbonContext = PrivilegedCarbonContext
                    .getThreadLocalCarbonContext();
            privilegedCarbonContext.setTenantDomain(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
            privilegedCarbonContext.setTenantId(MultitenantConstants.SUPER_TENANT_ID);
            log.info("Starting WSO2 Micro Integrator ...");
            log.info("Operating System : " + System.getProperty("os.name") + " " +
                    System.getProperty("os.version") + ", " + System.getProperty("os.arch"));
            if (log.isDebugEnabled()) {
                log.debug("Java Home        : " + System.getProperty("java.home"));
            }
            log.info("Java Version     : " + System.getProperty("java.version"));
            log.info("Java VM          : " + System.getProperty("java.vm.name") + " " +
                    System.getProperty("java.vm.version") +
                    "," +
                    System.getProperty("java.vendor"));

            String carbonHome;
            if ((carbonHome = System.getProperty("carbon.home")).equals(".")) {
                carbonHome = new File(".").getAbsolutePath();
            }
            log.info("Micro Integrator Home      : " + carbonHome);

            if (log.isDebugEnabled()) {
                log.info("Java Temp Dir    : " + System.getProperty("java.io.tmpdir"));
                log.info("User             : " + System.getProperty("user.name") + ", " +
                         System.getProperty("user.language") + "-" + System.getProperty("user.country") +
                         ", " + System.getProperty("user.timezone"));
            }
            Security.addProvider(new BouncyCastleProvider());
            if (log.isDebugEnabled()){
                log.debug("BouncyCastle security provider is successfully registered in JVM.");
            }
            bundleContext.registerService(CarbonCoreInitializedEvent.class.getName(), new CarbonCoreInitializedEventImpl(), null);
            GhostServiceMetaArtifactsLoader serviceMetaArtifactsLoader = new GhostServiceMetaArtifactsLoader();
            bundleContext.registerService(GhostMetaArtifactsLoader.class.getName(), serviceMetaArtifactsLoader, null);
            CarbonCoreDataHolder.getInstance().setBundleContext(bundleContext);
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        log.debug("Stopping Micro Integrator");
    }

}
