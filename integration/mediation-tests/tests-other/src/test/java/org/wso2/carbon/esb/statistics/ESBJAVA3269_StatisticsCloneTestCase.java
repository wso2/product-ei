/*
*Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.statistics;

import org.apache.axiom.om.OMElement;
import org.apache.commons.lang.ArrayUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.ApplicationAdminClient;
import org.wso2.carbon.integration.common.admin.client.CarbonAppUploaderClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.activation.DataHandler;
import java.io.File;
import java.net.URL;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class ESBJAVA3269_StatisticsCloneTestCase extends ESBIntegrationTest {
    private CarbonAppUploaderClient carbonAppUploaderClient;
    private ApplicationAdminClient applicationAdminClient;
    private final int MAX_TIME = 120000;
    private final String carFileName = "TestCloneStatsCappProj_1.0.0";
    private boolean isCarFileUploaded = false;

    @BeforeClass(alwaysRun = true)
    protected void initialize() throws Exception {
        super.init();
        carbonAppUploaderClient = new CarbonAppUploaderClient(contextUrls.getBackEndUrl(), getSessionCookie());
        carbonAppUploaderClient.uploadCarbonAppArtifact("TestCloneStatsCappProj_1.0.0.car"
                , new DataHandler(new URL("file:" + File.separator + File.separator + getESBResourceLocation()
                                          + File.separator + "car" + File.separator + "TestCloneStatsCappProj_1.0.0.car")));
        isCarFileUploaded = true;
        applicationAdminClient = new ApplicationAdminClient(contextUrls.getBackEndUrl(), getSessionCookie());
        Assert.assertTrue(isCarFileDeployed(carFileName), "Artifact Car deployment failed");
        TimeUnit.SECONDS.sleep(5);
    }

    @Test(groups = {"wso2.esb"}, description = "statistics stack clone test", enabled = false)
    public void statisticsStackCloneTest() throws Exception {
        // Test need to be enabled after fixing Statistics Issue with clone mediator
        OMElement response =
                axis2Client.sendMultipleQuoteRequest(getProxyServiceURLHttp("SplitAggregateProxy"),
                                                     null, "WSO2",2);
        Assert.assertNotNull(response);
        boolean logsFound = response.toString().contains("LogsFound");
        Assert.assertTrue(logsFound, "Log entries not found");
    }

    @AfterClass(alwaysRun = true)
    public void cleanupArtifactsIfExist() throws Exception {
        if (isCarFileUploaded) {
            applicationAdminClient.deleteApplication(carFileName);
        }
        super.cleanup();
    }

    private boolean isCarFileDeployed(String carFileName) throws Exception {

        log.info("waiting " + MAX_TIME + " millis for car deployment " + carFileName);
        boolean isCarFileDeployed = false;
        Calendar startTime = Calendar.getInstance();
        long time;
        while ((time = (Calendar.getInstance().getTimeInMillis() - startTime.getTimeInMillis())) < MAX_TIME) {
            String[] applicationList = applicationAdminClient.listAllApplications();
            if (applicationList != null) {
                if (ArrayUtils.contains(applicationList, carFileName)) {
                    isCarFileDeployed = true;
                    log.info("car file deployed in " + time + " mills");
                    return isCarFileDeployed;
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                //ignore
            }

        }
        return isCarFileDeployed;
    }

/*
Custom Mediator Source code
==========================

package org.wso2.test;

import java.util.List;
import org.apache.synapse.MessageContext;
import org.apache.synapse.aspects.statistics.StatisticsRecord;
import org.apache.synapse.mediators.AbstractMediator;

public class TestCloneStatsMediator extends AbstractMediator {
  public boolean mediate(MessageContext context) {
    int logsCount = 0;

    StatisticsRecord record = (StatisticsRecord)context.getProperty("synapse.statistics.stack");

    if (record != null) {
      logsCount = record.getAllStatisticsLogs().size();
    }

    context.setProperty("LogsCount", Integer.valueOf(logsCount));

    return true;
  }
}
*/


}
