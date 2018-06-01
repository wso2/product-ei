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

/**
 * MBean interface for exposing Service Adminstration functionalities
 */
public interface ServiceAdminMBean {

    /**
     * Get the currently active number of services
     *
     * @return Currently active number of services
     * @throws Exception If an error occurs while getting the service count
     */
    int getNumberOfActiveServices() throws Exception;

    /**
     * Get the currently inactive number of services
     *
     * @return Currently inactive number of services
     * @throws Exception If an error occurs while getting the service count
     */
    public int getNumberOfInactiveServices() throws Exception;

    /**
     * Get the number of faulty services
     *
     * @return number of faulty services
     * @throws Exception If an error occurs
     */
    int getNumberOfFaultyServices() throws Exception;

    /**
     * Start the service specified by <code>serviceName</code>
     *
     * @param serviceName Name of the service to be restarted
     * @throws Exception If an error occurs while starting the service
     */
    void startService(String serviceName) throws Exception;

    /**
     * Stop the service specified by <code>serviceName</code>
     *
     * @param serviceName Name of the service to be restarted
     * @throws Exception If an error occurs while stopping the service
     */
    void stopService(String serviceName) throws Exception;

    /**
     * Get all active service endpoints
     * @return All active service endpoints
     */
//    String[] getActiveEndPoints();

}