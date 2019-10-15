package org.wso2.carbon.esb.scheduledtask.test;

import org.apache.commons.lang.ArrayUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.ApplicationAdminClient;
import org.wso2.carbon.integration.common.admin.client.CarbonAppUploaderClient;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.data.xsd.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import java.io.File;
import java.net.URL;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertTrue;

/**
 * Test case related to task redeployment via car file. Related to : https://wso2.org/jira/browse/ESBJAVA-3186
 */
public class TaskRedeployWithCappTestCase extends ESBIntegrationTest {
    private CarbonAppUploaderClient carbonAppUploaderClient;
    private ApplicationAdminClient applicationAdminClient;
    private final int MAX_TIME = 120000;
    private final String carFileName = "task_deploy_car_1.0.0";
    private final String carFileFullName = carFileName + ".car";
    private boolean isCarFileUploaded = false;

    private LogViewerClient logViewer;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        carbonAppUploaderClient =
                new CarbonAppUploaderClient(context.getContextUrls().getBackEndUrl(), getSessionCookie());
        logViewer = new LogViewerClient(context.getContextUrls().getBackEndUrl(), getSessionCookie());
    }

    @Test(groups = { "wso2.esb" })
    public void taskRedeployWithCappTest() throws Exception {

        logViewer.clearLogs();
        carbonAppUploaderClient.uploadCarbonAppArtifact(carFileFullName, new DataHandler( new FileDataSource( new File
                (getESBResourceLocation() + File.separator +
                        "scheduledTask" + File.separator + carFileFullName))));
        isCarFileUploaded = true;
        applicationAdminClient =
                new ApplicationAdminClient(context.getContextUrls().getBackEndUrl(), getSessionCookie());
        Assert.assertTrue(isCarFileDeployed(carFileName), "Car file deployment failed");
        TimeUnit.SECONDS.sleep(50);//wait for some tasks to execute

        carbonAppUploaderClient.uploadCarbonAppArtifact(carFileFullName, new DataHandler( new FileDataSource( new File
                (getESBResourceLocation() + File.separator +
                        "scheduledTask" + File.separator + carFileFullName))));

        TimeUnit.SECONDS
                .sleep(150); //10 seconds proxy sleep time * 5 exeutions * 3 seconds interval  : wait for all tasks
        // to finish executing

        LogEvent[] logs = logViewer.getAllRemoteSystemLogs();
        int afterLogSize = logs.length;

        int startLogCount = 0;
        int endLogCount = 0;

        for (int i = 0; i < afterLogSize; i++) {
            if (logs[i].getMessage().contains("STARTED PROXY")) {
                startLogCount++;

            } else if (logs[i].getMessage().contains("ENDED PROXY")) {
                endLogCount++;

            }
        }
        assertTrue(startLogCount > 5);
        assertTrue(endLogCount > 5);

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

    @AfterClass(alwaysRun = true) public void destroy() throws Exception {
        super.cleanup();
    }
}
