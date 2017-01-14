package org.wso2.carbon.esb.json;

import org.json.JSONException;
import org.testng.annotations.*;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.utils.CarbonUtils;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.JSONClient;

import java.io.File;
import java.io.IOException;

import static org.testng.Assert.assertEquals;

public class CARBON14965TenantJsonFormatter extends ESBIntegrationTest {
    private ServerConfigurationManager serverManager = null;
    private JSONClient jsonClient;
    private String serviceUrl;

    @BeforeTest(alwaysRun = true)
    public void setEnvironment() throws Exception {

        super.init(TestUserMode.TENANT_ADMIN);
        serverManager = new ServerConfigurationManager(context);
        File sourceFile = new File(FrameworkPathUtil.getSystemResourceLocation() + "artifacts" + File.separator +
                                    "ESB" + File.separator + "json" + File.separator + "tenant-axis2.xml");

        File targetFile = new File(CarbonUtils.getCarbonHome() + File.separator + "repository" + File.separator + "conf"
                + File.separator + "axis2" + File.separator + "tenant-axis2.xml");

        serverManager.applyConfigurationWithoutRestart(sourceFile, targetFile, true);

        loadESBConfigurationFromClasspath("/artifacts/ESB/json/tenant-json-test-case.xml");
        //Give some time to load the configuration since this test is failing intermittently due to not deploying the artifact
        Thread.sleep(3000);
        serviceUrl = context.getContextUrls().getServiceUrl() + "/jsonproducer/";
        jsonClient = new JSONClient();
    }

    @Test
    public void testTest() throws IOException, JSONException {
        String payload = "{\"test\":\"\"}";
        String expectedResult = "{\"test\":\"\"}";
        String actualResult = jsonClient.sendUserDefineRequest(serviceUrl, payload).toString();
        assertEquals(actualResult, expectedResult, "Tenant Returned incorrectly formatted JSON response.");
    }

    @AfterTest(alwaysRun = true)
    public void cleanupEnvironment() throws Exception {
    }
}
