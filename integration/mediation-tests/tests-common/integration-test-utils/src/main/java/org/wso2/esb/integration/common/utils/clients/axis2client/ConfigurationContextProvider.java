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
package org.wso2.esb.integration.common.utils.clients.axis2client;

import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.esb.integration.common.utils.common.TestConfigurationProvider;

import java.io.File;

public class ConfigurationContextProvider {
    private static final Log log = LogFactory.getLog(ConfigurationContextProvider.class);
    private static ConfigurationContext configurationContext = null;
    private static ConfigurationContextProvider instance = new ConfigurationContextProvider();

    public ConfigurationContextProvider() {
        try {
            MultiThreadedHttpConnectionManager httpConnectionManager;
            HttpClient httpClient;
            HttpConnectionManagerParams params;
           configurationContext = ConfigurationContextFactory.
                   createConfigurationContextFromFileSystem(TestConfigurationProvider.getResourceLocation() + File.separator + "client", null);

            httpConnectionManager = new MultiThreadedHttpConnectionManager();
            params = new HttpConnectionManagerParams();
            params.setDefaultMaxConnectionsPerHost(25);
            httpConnectionManager.setParams(params);
            httpClient = new HttpClient(httpConnectionManager);

            configurationContext.setProperty(HTTPConstants.CACHED_HTTP_CLIENT, httpClient);
            configurationContext.setProperty(HTTPConstants.REUSE_HTTP_CLIENT, Constants.VALUE_TRUE);

        } catch (AxisFault axisFault) {
            log.error(axisFault);
        }
    }

    public static ConfigurationContextProvider getInstance() {
        return instance;
    }

    public ConfigurationContext getConfigurationContext() {
        return configurationContext;
    }

}
