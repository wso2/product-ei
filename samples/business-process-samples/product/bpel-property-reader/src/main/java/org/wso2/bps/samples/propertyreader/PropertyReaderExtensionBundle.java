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
package org.wso2.bps.samples.propertyreader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ode.bpel.runtime.extension.AbstractExtensionBundle;


/**
 * org.wso2.bps.samples.propertyreader.PropertyReaderExtensionBundle
 */
public class PropertyReaderExtensionBundle extends AbstractExtensionBundle {
    protected final Log log = LogFactory.getLog(getClass());
    public static final String NS = "http://wso2.org/bps/extensions/propertyReader";

    public String getNamespaceURI() {
        return NS;
    }

    public void registerExtensionActivities() {
        log.info("Registering property reader extension bundle.");
        registerExtensionOperation("readProperties", PropertyReaderExtensionOperation.class);
    }
}
