/*
 * Copyright 2005-2007 WSO2, Inc. (http://wso2.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.carbon.core.util;

import org.apache.axis2.deployment.Deployer;
import org.apache.axis2.deployment.DeploymentEngine;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.wso2.carbon.CarbonConstants;
import org.wso2.carbon.core.transports.HttpGetRequestProcessor;
import org.wso2.carbon.utils.component.xml.Component;
import org.wso2.carbon.utils.component.xml.ComponentConfigFactory;
import org.wso2.carbon.utils.component.xml.ComponentConstants;
import org.wso2.carbon.utils.component.xml.config.HTTPGetRequestProcessorConfig;

import java.io.InputStream;
import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;

public class Utils {

    private static Log log = LogFactory.getLog(Utils.class);

    /**
     * Given a bundleContext this method will register any HTTPGetRequestProcessors found in that
     * bundle
     * @param bundleContext The bundleContext of the bundle that may have HTTPGetRequestProcessors
     * @throws Exception Thrown in case the component.xml cannot be processes
     */
    public static void registerHTTPGetRequestProcessors(BundleContext bundleContext)
            throws Exception {
        URL url = bundleContext.getBundle().getEntry("META-INF/component.xml");
        if (url == null) {
            return;
        }

        InputStream inputStream = url.openStream();
        Component component = ComponentConfigFactory.build(inputStream);
        HTTPGetRequestProcessorConfig[] getRequestProcessorConfigs = null;
        if (component != null) {
            getRequestProcessorConfigs = (HTTPGetRequestProcessorConfig[])
                    component.getComponentConfig(ComponentConstants.HTTP_GET_REQUEST_PROCESSORS);
        }

        if (getRequestProcessorConfigs != null) {
            for (HTTPGetRequestProcessorConfig getRequestProcessorConfig :
                    getRequestProcessorConfigs) {
                Class getRequestProcessorClass;
                try {
                    getRequestProcessorClass = bundleContext.getBundle().
                            loadClass(getRequestProcessorConfig.getClassName());
                } catch (ClassNotFoundException e) {
                    getRequestProcessorClass = Class.forName(getRequestProcessorConfig.
                            getClassName());
                }
                HttpGetRequestProcessor getRequestProcessor =
                        (HttpGetRequestProcessor) getRequestProcessorClass.newInstance();
                String item = getRequestProcessorConfig.getItem();
                Dictionary<String,String> propsMap = new Hashtable<String,String>(2);
                propsMap.put(ComponentConstants.ELE_ITEM, item);
                propsMap.put(CarbonConstants.HTTP_GET_REQUEST_PROCESSOR_SERVICE,
                        HttpGetRequestProcessor.class.getName());

                //Registering the HttpGetRequestProcessor implementation in the OSGi registry
                bundleContext.registerService(HttpGetRequestProcessor.class.getName(),
                        getRequestProcessor, propsMap);
            }
        }
    }

    public static String replaceSystemProperty(String text) {
        int indexOfStartingChars = -1;
        int indexOfClosingBrace;

        // The following condition deals with properties.
        // Properties are specified as ${system.property},
        // and are assumed to be System properties
        while (indexOfStartingChars < text.indexOf("${") &&
                (indexOfStartingChars = text.indexOf("${")) != -1 &&
                (indexOfClosingBrace = text.indexOf('}')) != -1) { // Is a property used?
            String sysProp = text.substring(indexOfStartingChars + 2,
                    indexOfClosingBrace);
            String propValue = System.getProperty(sysProp);
            if (propValue != null) {
                text = text.substring(0, indexOfStartingChars) + propValue +
                        text.substring(indexOfClosingBrace + 1);
            }
        }
        return text;
    }

    public static boolean addCAppDeployer(AxisConfiguration axisConfiguration) {
        boolean successfullyAdded = false;
        try {
            String appsRepo = "carbonapps";
            // Initialize CApp deployer here
            Class deployerClass = Class.
                    forName("org.wso2.carbon.application.deployer.CappAxis2Deployer");

            Deployer deployer = (Deployer) deployerClass.newInstance();
            deployer.setDirectory(appsRepo);
            deployer.setExtension("car");

            //Add the deployer to deployment engine
            //We need to synchronize on the axisConfig object here to avoid issues such as CARBON-14471
            synchronized (axisConfiguration) {
                DeploymentEngine deploymentEngine =
                        (DeploymentEngine) axisConfiguration.getConfigurator();
                deploymentEngine.addDeployer(deployer, appsRepo, "car");
            }
            successfullyAdded = true;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error while adding CAppDeploymentManager to axis configuration", e);
        }
        return successfullyAdded;
    }

}
