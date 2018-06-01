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
package org.wso2.carbon.service.mgt;

import org.wso2.carbon.utils.CarbonUtils;

/*
* 
*/

public class ServiceGroupMetaData {
    private String serviceGroupName;
    private ServiceMetaData[] services;
    private String serviceContextPath;
    private String mtomStatus;
    private String[] engagedModules;
    private boolean disableDeletion;

    public String getServiceGroupName() {
        return serviceGroupName;
    }

    public void setServiceGroupName(String serviceGroupName) {
        this.serviceGroupName = serviceGroupName;
    }

    public ServiceMetaData[] getServices() {
        return CarbonUtils.arrayCopyOf(services);
    }

    public void setServices(ServiceMetaData[] services) {
        this.services = CarbonUtils.arrayCopyOf(services);
    }

    public String getServiceContextPath() {
        return serviceContextPath;
    }

    public void setServiceContextPath(String serviceContextPath) {
        this.serviceContextPath = serviceContextPath;
    }

    /**
     * Check if MTOM is available at this point
     *
     * @return boolean
     */
    public String getMtomStatus() {
        return mtomStatus;
    }

    /**
     * Set MTOM status
     *
     * @param mtomStatus
     */
    public void setMtomStatus(String mtomStatus) {
        this.mtomStatus = mtomStatus;
    }

    public String[] getEngagedModules() {
        return CarbonUtils.arrayCopyOf(engagedModules);
    }

    public void setEngagedModules(String[] engagedModules) {
        this.engagedModules = CarbonUtils.arrayCopyOf(engagedModules);
    }

    public boolean isDisableDeletion() {
        return disableDeletion;
    }

    public void setDisableDeletion(boolean disableDeletion) {
        this.disableDeletion = disableDeletion;
    }
}
