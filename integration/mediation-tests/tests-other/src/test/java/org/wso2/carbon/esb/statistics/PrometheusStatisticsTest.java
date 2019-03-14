/*
 *  Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.carbon.esb.statistics;

import org.apache.http.HttpResponse;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.extensions.servers.httpserver.SimpleHttpClient;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.utils.CarbonUtils;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class PrometheusStatisticsTest extends ESBIntegrationTest {

    private ServerConfigurationManager scm;
    private SimpleHttpClient client;
    private Map<String, String> headers;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {

        super.init();
        client = new SimpleHttpClient();
        headers = new HashMap<>();
        changeESBConfig();
    }

    @Test(groups = {"wso2.esb"}, description = "Test if metric data are exposed, when it is enabled in config")
    public void testPrometheusPublisher() throws Exception {

        headers.put("Accept", "application/json");
        String endpoint = "http://localhost:9091/metric-service/metrics";
        HttpResponse response = client.doGet(endpoint, headers);
        String responsePayload = client.getResponsePayload(response);
        Assert.assertEquals(response.getStatusLine().getStatusCode(), 200,
                "Metric retrieval failed");
        Assert.assertFalse(responsePayload.isEmpty(), "Metric response is empty");

    }

    private void changeESBConfig() throws Exception {

        String carbonHome = CarbonUtils.getCarbonHome();

        // apply prometheus-conf.xml config
        File prometheusConf = new File(
                carbonHome + File.separator + "conf" + File.separator + "prometheus-conf.xml");
        String resourcePath = "artifacts" + File.separator + "ESB" + File.separator + "mediationStatConfig" +
                File.separator + "prometheus-conf.xml";
        File modifiedPrometheusConf = new File(
                FrameworkPathUtil.getSystemResourceLocation(), resourcePath);

        // apply synapse config
        File synapseProps = new File(carbonHome + File.separator + "conf" + File.separator
                + "synapse.properties");
        String testSynapseResource = "artifacts" + File.separator + "ESB" + File.separator + "mediationStatConfig" +
                File.separator + "synapse.properties";
        File configuredSynapseProps = new File(FrameworkPathUtil.getSystemResourceLocation(), testSynapseResource);

        scm = new ServerConfigurationManager(context);
        scm.applyConfigurationWithoutRestart(modifiedPrometheusConf, prometheusConf, true);
        scm.applyConfigurationWithoutRestart(configuredSynapseProps, synapseProps, true);
        scm.restartGracefully();
        super.init();
    }

    @AfterClass(alwaysRun = true)
    public void cleanup() throws Exception {

        super.cleanup();
        if (scm != null) {
            scm.restoreToLastConfiguration(true);
        }
    }

}
