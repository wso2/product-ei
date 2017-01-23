/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.endpoint.test;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.http.client.HttpURLConnectionClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.*;
import java.net.URL;

public class EndpointFormatRestPTTestCase extends ESBIntegrationTest {

        @BeforeClass(alwaysRun = true)
        protected void init() throws Exception {
            super.init();
            loadESBConfigurationFromClasspath(File.separator + "artifacts" + File.separator + "ESB" + File.separator + "endpoint" + File.separator + "endpointFotmatRestPTTestConfig.xml");
        }

        @Test(groups = {"wso2.esb"}, description = "Test whether message is not built when endpoint format is set to rest")
        public void testEndpointFormatRestPTTestCase() throws Exception {

            URL endpoint = new URL(getProxyServiceURLHttp("EndpointFormatRestPTTestProxy"));
            Reader data = new StringReader("<Customer>\n" +
                                           "    <name>WSO2</name>\n" +
                                           "</Customer>");
            Writer writer = new StringWriter();
            HttpURLConnectionClient.sendPostRequest(data, endpoint, writer, "text/xml");
            Assert.assertTrue(!writer.toString().contains("com.ctc.wstx.exc.WstxEOFException"));
        }

        @AfterClass(alwaysRun = true)
        public void destroy() throws Exception {
            super.cleanup();
        }

}

