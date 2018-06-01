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
package org.wso2.carbon.micro.integrator.core.internal;

import org.apache.axis2.context.ConfigurationContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.http.HttpService;
import org.wso2.carbon.application.deployer.service.ApplicationManagerService;
import org.wso2.carbon.base.api.ServerConfigurationService;

/**
 * This singleton data holder contains all the data required by the Carbon core OSGi bundle
 */
public class CarbonCoreDataHolder {
    private  static CarbonCoreDataHolder instance = new CarbonCoreDataHolder();
    private  Log log = LogFactory.getLog(CarbonCoreDataHolder.class);

    private  BundleContext bundleContext;

    public ApplicationManagerService getApplicationManager() {
        return applicationManager;
    }

    private ApplicationManagerService applicationManager;
    private  HttpService httpService;
    private  ConfigurationContext mainServerConfigContext;
    private ServerConfigurationService serverConfigurationService;


    public  static CarbonCoreDataHolder getInstance() {
        return instance;
    }

    private CarbonCoreDataHolder() {
    }

    public BundleContext getBundleContext() {
        return bundleContext;
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public  void setServerConfigurationService(ServerConfigurationService serverConfigurationService) {
        this.serverConfigurationService = serverConfigurationService;
    }

    public  void setHttpService(HttpService httpService) {
        this.httpService = httpService;
    }

    public  HttpService getHttpService() throws Exception {
        if (httpService == null) {
            String msg = "Before activating Carbon Core bundle, an instance of "
                    + HttpService.class.getName() + " should be in existance";
            log.error(msg);
            throw new Exception(msg);
        }
        return httpService;
    }


    public ServerConfigurationService getServerConfigurationService() {
        if (this.serverConfigurationService == null) {
            String msg = "Before activating Carbon Core bundle, an instance of "
                    + "ServerConfigurationService should be in existance";
            log.error(msg);
        }
        return this.serverConfigurationService;
    }

    public  void setMainServerConfigContext(ConfigurationContext mainServerConfigContext) {
        this.mainServerConfigContext = mainServerConfigContext;
    }

    public  ConfigurationContext getMainServerConfigContext() {
        return mainServerConfigContext;
    }

    public void setApplicationManager(ApplicationManagerService applicationManager) {
        this.applicationManager = applicationManager;
    }

}
