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
package org.wso2.carbon.esb.nhttp.transport.test;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.FileRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.base.CarbonBaseUtils;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;

import static java.io.File.separator;

/**
 * To ensure that the body of the message is not get dropped when,
 * Content-Type of the message is not mentioned
 * https://wso2.org/jira/browse/ESBJAVA-2183 - need to be fixed to complete test case.
 */

public class MessageWithoutContentTypeTestCase extends ESBIntegrationTest {

    private static final Log log = LogFactory.getLog(MessageWithoutContentTypeTestCase.class);
    private ServerConfigurationManager serverConfigurationManager;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        AutomationContext autoCtx = new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN);
        serverConfigurationManager = new ServerConfigurationManager(autoCtx);
        serverConfigurationManager.applyConfiguration(
                new File(getClass().getResource("/artifacts/ESB/nhttp/transport/axis2.xml").getPath()));
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/messagewithoutcontent/synapse.xml");
    }

    /**
     * Sending a message without mentioning Content Type and check the body part at the listening port
     * <p/>
     * Public JIRA:    WSO2 Carbon/CARBON-6029
     * Responses With No Content-Type Header not handled properly
     * <p/>
     * Test Artifacts: ESB Sample 0
     *
     * @throws Exception   - if the scenario fail
     */
    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.ALL})
    @Test(groups = "wso2.esb")
    public void testMessageWithoutContentType() throws Exception {

        // Get target URL
        String strURL = getMainSequenceURL();
        // Get SOAP action
        String strSoapAction = "getQuote";
        // Get file to be posted
        String strXMLFilename = FrameworkPathUtil.getSystemResourceLocation() + "artifacts" + File.separator +
                "ESB" + File.separator + "synapseconfig" + File.separator + "messagewithoutcontent" +
                File.separator + "request.xml";

        File input = new File(strXMLFilename);
        // Prepare HTTP post
        PostMethod post = new PostMethod(strURL);
        // Request content will be retrieved directly
        // from the input stream
        RequestEntity entity = new FileRequestEntity(input, "text/xml");
        post.setRequestEntity(entity);
        // consult documentation for your web service
        post.setRequestHeader("SOAPAction", strSoapAction);
        // Get HTTP client
        HttpClient httpclient = new HttpClient();
        // Execute request
        try {
            int result = httpclient.executeMethod(post);
            // Display status code
            log.info("Response status code: " + result);
            // Display response
            log.info("Response body: ");
            log.info(post.getResponseBodyAsString());
        } finally {
            // Release current connection to the connection pool once you are done
            post.releaseConnection();
        }
    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        try {
            super.cleanup();
        } finally {
            serverConfigurationManager.restoreToLastConfiguration();
            serverConfigurationManager = null;
        }
    }
}
