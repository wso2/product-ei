/*
*  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.bps.samples.extension;

import org.w3c.dom.Element;
import org.apache.ode.bpel.runtime.extension.ExtensionContext;
import org.apache.ode.bpel.extension.ExtensionOperation;
import org.apache.ode.bpel.common.FaultException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class B4PExtensionOperation implements ExtensionOperation {
    protected final Log log = LogFactory.getLog(getClass());

    public void run(Object o, String cid, Element element) throws FaultException {
        log.info("Executing \"b4ptest\" activity");
        ExtensionContext tempEC = (ExtensionContext)o;
        ((ExtensionContext)o).complete(cid);         //complete the activity
        /* //To complete with a fault
        Exception e = new Exception("Test Exception for the Extension activity");
        ((ExtensionContext)o).completeWithFault(e);                              */
    }
}
