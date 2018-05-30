/*
 * Copyright 2004,2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.carbon.core.deployment;

import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.deployment.Deployer;
import org.apache.axis2.deployment.DeploymentException;
import org.apache.axis2.deployment.repository.util.DeploymentFileData;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.wso2.carbon.utils.deployment.Axis2ServiceRegistry;

/*
* 
*/
public class OSGiAxis2ServiceDeployer implements Deployer, BundleListener {

    private BundleContext context;

    private Axis2ServiceRegistry registry;

    public OSGiAxis2ServiceDeployer(ConfigurationContext configCtx, BundleContext context) {
        this.context = context;
        this.registry = new Axis2ServiceRegistry(configCtx);
    }

    public void registerBundleListener(){
        this.context.addBundleListener(this);
    }

    public void init(ConfigurationContext configCtx) {
        //ignore
    }

    public void deploy(DeploymentFileData deploymentFileData) throws DeploymentException {
        for (Bundle bundle : context.getBundles()) {
            if (bundle.getState() == Bundle.ACTIVE) {
                registry.register(bundle);
            }
        }

    }

    public void setDirectory(String directory) {

    }

    public void setExtension(String extension) {

    }

    public void undeploy(String fileName) throws DeploymentException {

    }

    public void cleanup() throws DeploymentException {

    }

    public void bundleChanged(BundleEvent event) {
        Bundle bundle = event.getBundle();
        switch (event.getType()) {
            case BundleEvent.STARTED:
                if (context.getBundle() != bundle) {
                    registry.register(event.getBundle());
                }
                break;

            case BundleEvent.STOPPED:
                if (context.getBundle() != bundle) {
                    registry.unregister(event.getBundle());
                }
                break;
        }
    }
}
