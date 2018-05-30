/*
 * Copyright 2005-2014 WSO2, Inc. (http://wso2.com)
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

package org.wso2.carbon.core;

/**
 * This class is used as a listener for getting notifications when the server startup happens.
 */
public interface ServerStartupObserver {

    /**
     * This method will be invoked just before completing server startup.
     * E.g. before starting all the transports.
     */
    public void completingServerStartup();


    /**
     * This method will be invoked just after completing server startup.
     * E.g. after starting all the transports.
     */
    public void completedServerStartup();

}
