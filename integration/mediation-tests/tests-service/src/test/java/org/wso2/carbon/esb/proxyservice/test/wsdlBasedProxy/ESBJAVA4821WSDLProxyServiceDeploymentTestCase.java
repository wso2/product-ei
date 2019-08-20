/*
*  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/
package org.wso2.carbon.esb.proxyservice.test.wsdlBasedProxy;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.base.CarbonBaseUtils;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.data.xsd.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.common.FileManager;

import java.io.File;
import java.util.Calendar;

/**
 * https://wso2.org/jira/browse/ESBJAVA-4821
 * When ESB proxy service deployment, if proxy service is having an WSDL URI., ESB try to get the
 * WSDL from a server and if the server failed to respond with the wsdl content, ESB wait till that
 * server close the connection. This test class make sure the connection will be closed with read timeout
 */

public class ESBJAVA4821WSDLProxyServiceDeploymentTestCase extends ESBIntegrationTest {

    private final String targetProxyPath = CarbonBaseUtils.getCarbonHome() + File.separator + "repository" + File.separator
                                           + "deployment" + File.separator + "server" + File.separator + "synapse-configs"
                                           + File.separator + "default" + File.separator + "proxy-services"
                                           + File.separator + "wsdl-fault-proxy.xml";

    @BeforeClass(alwaysRun = true)
    public void deployAPI() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath(
                "/artifacts/ESB/proxyconfig/proxy/wsdlBasedProxy/delay-wsdl.xml");

    }

    @Test(groups = "wso2.esb", description = "Test the deployment of WSDL proxy which wsdl does not respond until timeout")
    public void testWSDLBasedProxyDeployment() throws Exception {

        LogViewerClient logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
        logViewerClient.clearLogs();
        File source = new File(getESBResourceLocation() + File.separator + "proxyconfig" + File.separator
                               + "proxy" + File.separator + "wsdlBasedProxy" + File.separator + "wsdl-fault-proxy.xml");
        FileManager.copyFile(source, targetProxyPath);

        long startTime = Calendar.getInstance().getTimeInMillis();
        boolean logFound = false;
        while (!logFound && (Calendar.getInstance().getTimeInMillis() - startTime) < 200000) {

            LogEvent[] logEvents = logViewerClient.getAllRemoteSystemLogs();

            if (logEvents != null) {
                for (LogEvent log : logEvents) {
                    if (log == null) {
                        continue;
                    }
                    if (log.getMessage().contains("IOError when getting a stream from given url")) {
                        logFound = true;
                    }
                }
            }
            Thread.sleep(3000);

        }
        Assert.assertTrue(logFound, "Error message not found. Deployment not failed due to read timeout of wsdl");
    }


    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        try {
            File target = new File(targetProxyPath);
            File targetBackup = new File(targetProxyPath + ".back");
            if (target.exists()) {
                target.delete();
            }

            if (targetBackup.exists()) {
                targetBackup.delete();
            }

        } finally {
            super.cleanup();
        }
    }
}
