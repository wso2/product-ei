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
import org.wso2.carbon.utils.Pageable;

import java.util.List;

/**
 *
 */
public class ServiceGroupMetaDataWrapper implements Pageable {
    private ServiceGroupMetaData[] serviceGroups;
    private int numberOfActiveServices;
    private int numberOfCorrectServiceGroups;
    private int numberOfFaultyServiceGroups;
    private int numberOfPages;
    private String[] serviceTypes;

    public ServiceGroupMetaDataWrapper() {
    }

    public ServiceGroupMetaData[] getServiceGroups() {
        return CarbonUtils.arrayCopyOf(serviceGroups);
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }

    public void setServiceGroups(ServiceGroupMetaData[] serviceGroups) {
        this.serviceGroups = CarbonUtils.arrayCopyOf(serviceGroups);
    }

    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public <T> void set(List<T> items) {
        this.serviceGroups = items.toArray(new ServiceGroupMetaData[items.size()]);
    }

    public int getNumberOfActiveServices() {
        return numberOfActiveServices;
    }

    public void setNumberOfActiveServices(int numberOfActiveServices) {
        this.numberOfActiveServices = numberOfActiveServices;
    }

    public int getNumberOfCorrectServiceGroups() {
        return numberOfCorrectServiceGroups;
    }

    public void setNumberOfCorrectServiceGroups(int numberOfCorrectServiceGroups) {
        this.numberOfCorrectServiceGroups = numberOfCorrectServiceGroups;
    }

    public int getNumberOfFaultyServiceGroups() {
        return numberOfFaultyServiceGroups;
    }

    public void setNumberOfFaultyServiceGroups(int numberOfFaultyServiceGroups) {
        this.numberOfFaultyServiceGroups = numberOfFaultyServiceGroups;
    }

    public String[] getServiceTypes() {
        return CarbonUtils.arrayCopyOf(serviceTypes);
    }

    public void setServiceTypes(String[] serviceTypes) {
        this.serviceTypes = CarbonUtils.arrayCopyOf(serviceTypes);
    }
}
