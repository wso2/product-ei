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
package org.wso2.carbon.core.transports;

import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.utils.AbstractAxis2ConfigurationContextObserver;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

import java.net.URL;

/**
 * This will be an OSGi service which handles persistence of the transports
 */
public class TransportAxis2ConfigurationContextObserver extends
        AbstractAxis2ConfigurationContextObserver {

    private static final Log log = LogFactory.getLog(org.wso2.carbon.core.transports.TransportAxis2ConfigurationContextObserver.class);
    private String transport;
    private URL configFileURL;

    public TransportAxis2ConfigurationContextObserver(String transport, URL configFileURL) {
        this.transport = transport;
        this.configFileURL = configFileURL;
    }

    public void createdConfigurationContext(ConfigurationContext configContext) {
        AxisConfiguration axisConfig = configContext.getAxisConfiguration();
        try {

        } catch (Exception e) {
            log.error("Cannot persist transport " + transport + " with config " + configFileURL +
                      getTenantIdAndDomainString(axisConfig));
        }
    }


    private String getTenantIdAndDomainString(AxisConfiguration axisConfig) {
        PrivilegedCarbonContext carbonContext = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        int tenantId = carbonContext.getTenantId();
        String tenantDomain = carbonContext.getTenantDomain();
        return (tenantId != MultitenantConstants.INVALID_TENANT_ID && tenantId != MultitenantConstants.SUPER_TENANT_ID) ?
                " {" + tenantDomain + "[" + tenantId + "]}" : " {super-tenant}";
    }

    public void terminatingConfigurationContext(ConfigurationContext configCtx) {
        // nothing to do
    }

    public void terminatedConfigurationContext(ConfigurationContext configCtx) {
        // nothing to do
    }
}
