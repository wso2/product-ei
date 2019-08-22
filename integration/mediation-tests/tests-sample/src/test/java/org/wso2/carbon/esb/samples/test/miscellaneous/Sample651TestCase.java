package org.wso2.carbon.esb.samples.test.miscellaneous;

import junit.framework.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.base.CarbonBaseUtils;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.esb.integration.common.utils.common.ServerConfigurationManager;
import org.wso2.carbon.logging.view.data.xsd.LogEvent;
import org.wso2.carbon.esb.samples.test.util.ESBSampleIntegrationTest;

import java.io.File;

public class Sample651TestCase extends ESBSampleIntegrationTest {

    private ServerConfigurationManager serverManager = null;
    private LogViewerClient logViewerClient = null;

    @BeforeClass(alwaysRun = true)
    public void startJMSBrokerAndConfigureESB() throws Exception {

        super.init();
        serverManager = new ServerConfigurationManager(context);

        String sourceLog4j = FrameworkPathUtil.getSystemResourceLocation() + File.separator
                + "artifacts" + File.separator + "ESB"
                + File.separator + "synapseconfig" + File.separator + "observer" + File.separator
                + "log4j.properties";

        String targetLog4j = CarbonBaseUtils.getCarbonHome() + File.separator + "repository" +
                File.separator + "conf" + File.separator + "log4j.properties";

        File sourceLog4jFile = new File(sourceLog4j);
        File targetLog4jFile = new File(targetLog4j);
        serverManager.applyConfigurationWithoutRestart(sourceLog4jFile, targetLog4jFile, true);

        String sourceSynapsePrp = FrameworkPathUtil.getSystemResourceLocation() + File.separator
                + "artifacts" + File.separator + "ESB"
                + File.separator + "synapseconfig" + File.separator + "observer" + File.separator
                + "synapse.properties";

        String targetSynapsePrp = CarbonBaseUtils.getCarbonHome() + File.separator + "repository" +
                File.separator + "conf" + File.separator + "synapse.properties";

        File sourceSynapsePrpFile = new File(sourceSynapsePrp);
        File targetSynapsePrpFile = new File(targetSynapsePrp);
        serverManager.applyConfiguration(sourceSynapsePrpFile, targetSynapsePrpFile);

        super.init();
        logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());

        loadSampleESBConfiguration(100);
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Test synapse observer ",enabled = false)
    public void testSynapseObservers() throws Exception {

        Thread.sleep(30000);

        boolean isRequestLogFound = false;

        LogEvent[] logEvents = logViewerClient.getAllRemoteSystemLogs();
        for (LogEvent event : logEvents) {
            if (event.getMessage().contains("Simple logging observer initialized")) {
                isRequestLogFound = true;
                break;
            }
        }

        Assert.assertTrue("Simple observer not working", isRequestLogFound);

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {

        //reverting the changes done to esb sever

        super.cleanup();
        Thread.sleep(10000); //let server to clear the artifact undeployment
        if (serverManager != null) {
            serverManager.restoreToLastConfiguration();
        }
    }
}
