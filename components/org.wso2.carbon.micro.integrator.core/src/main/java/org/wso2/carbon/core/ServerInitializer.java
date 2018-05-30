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

package org.wso2.carbon.core;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.wso2.carbon.utils.ServerException;

/*
 * Server intializing code which will be run when the Carbon server is initialized
 *
 * Define these in the "Initializers" section of the carbon.xml file
 */
public interface ServerInitializer {

    /**
     * Intialize the Server instance
     *
     * @param configurationContext The Configuration Context
     * @throws AxisFault
     * @throws ServerException
     */
    void init(ConfigurationContext configurationContext) throws AxisFault, ServerException;


}
