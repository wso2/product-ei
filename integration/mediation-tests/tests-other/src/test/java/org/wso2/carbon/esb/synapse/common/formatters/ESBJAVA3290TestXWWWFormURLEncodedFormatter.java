package org.wso2.carbon.esb.synapse.common.formatters;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.base.CarbonBaseUtils;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.servers.WireMonitorServer;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * This test case is to validate the "Unable to create POST request against REST endpoint where
 * body parameter name starts with digit" issue  * reported in https://wso2
 * .org/jira/browse/ESBJAVA-3290
 */
public class ESBJAVA3290TestXWWWFormURLEncodedFormatter extends ESBIntegrationTest {

    private static final String synapseConfig = "x_www_form_url_encoded_formatter_test.xml";
    public WireMonitorServer wireServer;
    private ServerConfigurationManager serverConfigurationManager;

    @BeforeTest(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        wireServer = new WireMonitorServer(8991);
        wireServer.start();
        serverConfigurationManager = new ServerConfigurationManager(context);
        serverConfigurationManager.applyConfiguration(new File(getClass().getResource(File.separator + "artifacts"
                                                                                      + File.separator + "ESB" + File.separator +
                                                                                      "xwwwformurlencodedformatter" + File.separator +
                                                                                      "axis2.xml").getPath()),
                                                      new File(CarbonBaseUtils.getCarbonHome() +
                                                               File.separator + "repository" +
                                                               File.separator
                                                               + "conf" + File.separator +
                                                               "axis2" + File.separator + "axis2" +
                                                               ".xml"));
        super.init();
        loadESBConfigurationFromClasspath(File.separator + "artifacts" + File.separator + "ESB" +
                                          File.separator + "xwwwformurlencodedformatter"
                                          + File.separator + synapseConfig);
        Thread.sleep(5000);
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.esb", description = "POST request against REST endpoint where " +
                                             "body parameter name starts with digit")
    public void testPostRequest() throws Exception {

        URL endpoint = new URL(getProxyServiceURLHttp("RestProxy"));
        try {
            Map<String, String> header = new HashMap<String, String>();
            header.put("Content-Type", "application/x-www-form-urlencoded");
            HttpRequestUtil.doPost(endpoint, "paramName=abc&2paramName=def&$paramName=ghi", header);

        } catch (Exception e){
            //ignore
        }

        String response = wireServer.getCapturedMessage();
        Assert.assertNotNull(response);
        Assert.assertTrue(response.contains("2paramName"), "POST request does not contain the " +
                                                           "body " +
                                                           "parameter name starts with digit " +
                                                           "specified");
        Assert.assertTrue(response.contains("$paramName"), "POST request does not contain the " +
                                                           "body " +
                                                           "parameter name starts with $ character " +
                                                           "specified");
    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        cleanup();
        Thread.sleep(3000);
        serverConfigurationManager.restoreToLastConfiguration();
        serverConfigurationManager = null;
    }
}
