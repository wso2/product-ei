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

package org.wso2.esb.integration.common.extensions.carbonserver;

import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MultipleServersManager {

    private Map<String, TestServerManager> servers = new HashMap();

    public MultipleServersManager() {
    }

    public void startServers(TestServerManager... serverManagers) throws AutomationFrameworkException {
        TestServerManager[] arr$ = serverManagers;
        int len$ = serverManagers.length;

        for (int i$ = 0; i$ < len$; ++i$) {
            TestServerManager zip = arr$[i$];
            String carbonHome = null;

            try {
                carbonHome = zip.startServer();
            } catch (IOException var8) {
                throw new AutomationFrameworkException("Server start failed", var8);
            } catch (AutomationFrameworkException var9) {
                throw new AutomationFrameworkException("Server start failed", var9);
            } catch (XPathExpressionException var10) {
                throw new AutomationFrameworkException("Server start failed", var10);
            }

            this.servers.put(carbonHome, zip);
        }

    }

    public void stopAllServers() throws AutomationFrameworkException {
        Iterator i$ = this.servers.values().iterator();

        while (i$.hasNext()) {
            TestServerManager serverUtils = (TestServerManager) i$.next();
            serverUtils.stopServer();
        }

    }
}
