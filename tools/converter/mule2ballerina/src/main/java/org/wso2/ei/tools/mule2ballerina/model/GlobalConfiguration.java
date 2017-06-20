/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.ei.tools.mule2ballerina.model;

import org.wso2.ei.tools.mule2ballerina.builder.TreeBuilder;
import org.wso2.ei.tools.mule2ballerina.dto.DataCarrierDTO;
import org.wso2.ei.tools.mule2ballerina.visitor.Visitable;

import java.util.LinkedList;
import java.util.Queue;

/**
 * {@code GlobalConfiguration} represents any mule global configuration and any global configuration in mule config
 * file should be added to the intermediate object stack through this class
 */
public interface GlobalConfiguration extends Visitable, TreeBuilder {

    String getName();

    /**
     * If the intermediate object represents a global configuration in mule, add it to global config list
     * Further if it's an inbound config, keep all the flows that belong to that config in a map, as it is needed
     * to determine the end of service point in ballerina stack
     *
     * @param dataCarrierDTO
     */
    @Override
    public default void buildTree(DataCarrierDTO dataCarrierDTO) {

        BaseObject baseObj = dataCarrierDTO.getBaseObject();
        Root rootObj = dataCarrierDTO.getRootObject();
        GlobalConfiguration globalConfiguration = (GlobalConfiguration) baseObj;
        rootObj.addGlobalConfiguration((GlobalConfiguration) baseObj);

         /* If the element is a global configuration keep it against it's name as this will
          * be useful when navigating the processors to identify their global configuration */
        rootObj.addGlobalConfigurationMap(globalConfiguration.getName(), (GlobalConfiguration) globalConfiguration);

        /*If the global configuration represents an inbound global config, check whether a flow queue is maintained
          for that inbound config.If there's not create an empty flow queue against the global config and put it in the
          service map of root
         */
        if (baseObj instanceof Inbound && rootObj.getServiceMap() != null) {
            Inbound inboundObj = (Inbound) baseObj;
            Queue<Flow> flowQueue = rootObj.getServiceMap().get(inboundObj.getName());
            if (flowQueue == null) {
                flowQueue = new LinkedList<Flow>();
                rootObj.getServiceMap().put(inboundObj.getName(), flowQueue);
            }
        }
    }
}
