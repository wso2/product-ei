/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/

package org.wso2.carbon.esb.ui.test.util;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.automation.core.utils.frameworkutils.FrameworkFactory;

import javax.xml.stream.XMLStreamException;

public class JMSEndpointManager {
    public static OMElement setConfigurations(OMElement synapseConfig) throws XMLStreamException {

        if (FrameworkFactory.getFrameworkProperties(ProductConstant.ESB_SERVER_NAME).getEnvironmentSettings().is_builderEnabled()) {
            //return same configurations as for the ActiveMQ
            return synapseConfig;
        } else {
            //changing the configurations to work with WSO2 MB instead of ActiveMQ
            String config = synapseConfig.toString();
            config = config.replace("org.apache.activemq.jndi.ActiveMQInitialContextFactory"
                    , "org.wso2.andes.jndi.PropertiesFileInitialContextFactory");

            config = config.replace("tcp://127.0.0.1:61616", "repository/conf/jndi.properties");
            config = config.replace("tcp://localhost:61616", "repository/conf/jndi.properties");
            return AXIOMUtil.stringToOM(config);
        }
    }
}
