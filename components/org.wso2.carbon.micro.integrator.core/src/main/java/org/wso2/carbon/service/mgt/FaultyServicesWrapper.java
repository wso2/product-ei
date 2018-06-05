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
public class FaultyServicesWrapper implements Pageable {
    private FaultyService[] faultyServices;
    private int numberOfFaultyServiceGroups;
    private int numberOfPages;

    public FaultyService[] getFaultyServices() {
        return CarbonUtils.arrayCopyOf(faultyServices);
    }

    public void setFaultyServices(FaultyService[] faultyServices) {
        this.faultyServices = CarbonUtils.arrayCopyOf(faultyServices);
    }

    public int getNumberOfFaultyServiceGroups() {
        return numberOfFaultyServiceGroups;
    }

    public void setNumberOfFaultyServiceGroups(int numberOfFaultyServiceGroups) {
        this.numberOfFaultyServiceGroups = numberOfFaultyServiceGroups;
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public <T> void set(List<T> items) {
        this.faultyServices = items.toArray(new FaultyService[items.size()]);
    }
}

