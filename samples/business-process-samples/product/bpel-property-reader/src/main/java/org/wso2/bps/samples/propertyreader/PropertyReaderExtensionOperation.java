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
import org.apache.ode.bpel.common.FaultException;
import org.apache.ode.bpel.runtime.extension.AbstractSyncExtensionOperation;
import org.apache.ode.bpel.runtime.extension.ExtensionContext;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.wso2.bps.samples.internal.RegistryService;

import java.io.*;
import java.util.Properties;

public class PropertyReaderExtensionOperation extends AbstractSyncExtensionOperation {
    protected final Log log = LogFactory.getLog(getClass());

    @Override
    protected void runSync(ExtensionContext extensionContext, Element element) throws FaultException {

        Properties properties = null;
        String fileLocation = element.getAttribute("location").trim();

        String source = fileLocation.split(":")[0];

        if (source.equals("file")) {
            properties = getPropertiesFromLocalFile(fileLocation.split(":")[1]);
        } else if (source.equals("conf")) {
            properties = getPropertiesFromConfigRegistryFile(fileLocation.split(":")[1]);
        } else if (source.equals("gov")) {
            properties = getPropertiesFromGovernanceFile(fileLocation.split(":")[1]);
        } else {
            System.out.println("File Location Error !!");
        }

        NodeList propertyNameList = element.getElementsByTagName("property");

        for (int i = 0; i < propertyNameList.getLength() && !properties.equals(null); i++) {
            Element property = (Element) propertyNameList.item(i);
            String propertyName = property.getAttribute("name").trim();
            String value = properties.getProperty(propertyName, "");
            Element to = (Element) property.getElementsByTagName("to").item(0);
            String variable = to.getAttribute("variable").trim();
            extensionContext.readVariable(variable.trim()).setTextContent(value.trim());
        }

    }

    private Properties getPropertiesFromLocalFile(String location) {
        Properties properties = null;
        try {
            properties = new Properties();
            properties.load(new FileInputStream(location));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    private Properties getPropertiesFromConfigRegistryFile(String location) {
        Properties properties = null;
        try {
            properties = RegistryService.getRegistryProperties("/_system/config/" + location);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
    }

    private Properties getPropertiesFromGovernanceFile(String location) {
        Properties properties = null;
        try {
            properties = RegistryService.getRegistryProperties("/_system/governance/" + location);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
    }

}
