/*
*  Copyright (c) 2005-2011, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.carbon.core;

import org.apache.axis2.context.MessageContext;
import org.apache.axis2.transport.base.threads.ThreadCleanup;
import org.wso2.carbon.base.CarbonContextHolderBase;

/**
 * This ThreadCleanup implementation wil clear out all the ThreadLocal variables, and do other
 * Thread cleanup related work
 */
public class CarbonThreadCleanup implements ThreadCleanup {
    @Override
    public void cleanup() {
        CarbonContextHolderBase.destroyCurrentCarbonContextHolder();
        MessageContext.destroyCurrentMessageContext();
    }
}
