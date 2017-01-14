/*
*  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.mediators.store;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.extensions.servers.httpserver.SimpleHttpClient;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import javax.activation.DataHandler;

import static org.testng.Assert.assertFalse;

public class ESBJAVA4470StoreMediatorEmptyOMArraySerializeException extends ESBIntegrationTest {

    private ServerConfigurationManager serverManager = null;
    String carFileName = "StoreMediator_1.0.0.car";
    LogViewerClient logViewerClient;

    @BeforeClass(alwaysRun = true)
    protected void init() throws Exception {
        super.init();
        logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
        uploadCapp(carFileName
                , new DataHandler(new URL("file:" + File.separator + File.separator +
                getESBResourceLocation() + File.separator + "car" +
                File.separator + carFileName)));
        TimeUnit.SECONDS.sleep(30);
        log.info(carFileName + " uploaded successfully");
    }

    @Test(groups = {"wso2.esb"}, description = "Test if Store Mediator Serialize Empty OM Array without Exception")
    public void testStoreMediatorEmptyOMArrayPropertySerialize() throws Exception {
        logViewerClient.clearLogs();
        String url = getApiInvocationURL("SerializeProperty")+"/serializeOMArray";
        SimpleHttpClient httpClient = new SimpleHttpClient();
        httpClient.doGet(url, null);
        TimeUnit.SECONDS.sleep(10);
        LogEvent[] logs = logViewerClient.getAllRemoteSystemLogs();
        boolean logFound = false;
        if (logs != null) {
            for (LogEvent item : logs) {
                if (item.getMessage().contains("Index: 0, Size: 0") && item.getPriority().contains("ERROR")) {
                    logFound = true;
                    break;
                }
            }
        }
        assertFalse(logFound, "Exception thrown when serializing OM Array property by Store Mediator");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        String carlocation = FrameworkPathUtil.getCarbonHome()+"/repository/deployment/server/carbonapps/"+carFileName;
        File carFile = new File(carlocation);
        if(carFile.exists()){
            carFile.delete();
            log.info(carFileName + " car file deleted successfully");
        }
        super.cleanup();
    }

}