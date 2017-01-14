/*
 * Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.nhttp.transport.test;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.utils.ServerConstants;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.common.TestConfigurationProvider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class DefaultRequestContentTypeTestCase extends ESBIntegrationTest {
    private ServerConfigurationManager serverConfigurationManager;


    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        super.init();
        serverConfigurationManager =
                new ServerConfigurationManager(new AutomationContext("ESB",
                                                                     TestUserMode.SUPER_TENANT_ADMIN));

        String carbonHome = System.getProperty(ServerConstants.CARBON_HOME);
        String confDir = carbonHome + File.separator + "repository" + File.separator + "conf" + File.separator;
        File originalConfig = new File(getESBResourceLocation() + File.separator + "synapseconfig" + File.separator +
                "nhttp_transport" + File.separator + "default_content_type_axis2.xml");
        File destDir = new File(confDir + "axis2" + File.separator);
        FileUtils.copyFileToDirectory(originalConfig, destDir);
        serverConfigurationManager.restartGracefully();
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/nhttp_transport"
                                          + "/default_content_type_synapse.xml");

    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Test for DEFAULT_REQUEST_TYPE for nhttp transport")
    public void testReturnContentType() throws Exception {
        try {
            String url = getApiInvocationURL("defaultRequestContentTypeApi").concat("?symbol=wso2");
            HttpUriRequest request = new HttpPut(url);
            DefaultHttpClient client = new DefaultHttpClient();

            HttpResponse response = client.execute(request);
            Assert.assertNotNull(response);
            Assert.assertTrue(getResponse(response).toString().contains("WSO2"));
        } catch (IOException e) {
            log.error("Error while sending the request to the endpoint. ", e);
        }

    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        String carbonHome = System.getProperty(ServerConstants.CARBON_HOME);
        String confDir = carbonHome + File.separator + "repository" + File.separator + "conf" + File.separator;
        File configTemp = new File(confDir + "axis2" + File.separator + "default_content_type_axis2.xml");
        FileUtils.deleteQuietly(configTemp);
        cleanup();
        serverConfigurationManager.restoreToLastConfiguration();
    }

    private String getResponse(HttpResponse response) throws Exception {
        StringBuffer buffer = new StringBuffer();

        if (response.getEntity() != null) {
            InputStream in = response.getEntity().getContent();
            int length;
            byte[] tmp = new byte[2048];
            while ((length = in.read(tmp)) != -1) {
                buffer.append(new String(tmp, 0, length));
            }
        }
        return buffer.toString();
    }

}