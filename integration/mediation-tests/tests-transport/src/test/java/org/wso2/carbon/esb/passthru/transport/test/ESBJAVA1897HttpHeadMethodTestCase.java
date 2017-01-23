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

package org.wso2.carbon.esb.passthru.transport.test;

import org.apache.axiom.om.OMElement;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.DefaultHttpClient;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.esb.integration.common.utils.servers.axis2.SampleAxis2Server;
import org.wso2.esb.integration.common.utils.ESBTestCaseUtils;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestConstant;
import java.io.File;
import static org.testng.Assert.assertTrue;

/**
 * https://wso2.org/jira/browse/ESBJAVA-1897 Http HEAD method doesn't 
 * work with PTT.
 *
 */
public class ESBJAVA1897HttpHeadMethodTestCase extends  ESBIntegrationTest{
    private static final String SERVICE_NAME = "RestServiceProxy";
    private SampleAxis2Server axis2Server1 = null;

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
    	// start the axis2 server and deploy the Student Service

//    	if (FrameworkFactory.getFrameworkProperties(ProductConstant.ESB_SERVER_NAME).getEnvironmentSettings().is_builderEnabled()) {
    		axis2Server1 = new SampleAxis2Server("test_axis2_server_9009.xml");
    		axis2Server1.start();
    		axis2Server1.deployService(ESBTestConstant.SIMPLE_AXIS2_SERVICE);
    		axis2Server1.deployService(ESBTestConstant.STUDENT_REST_SERVICE);
//    	}
        super.init();
        // load the proxy config
        String relativePath = "artifacts" + File.separator + "ESB" +
                File.separator + "synapseconfig" + File.separator + "rest" +
                File.separator + "rest-service-proxy.xml";
        ESBTestCaseUtils util = new ESBTestCaseUtils();
        relativePath = relativePath.replaceAll("[\\\\/]", File.separator);
        OMElement proxyConfig = util.loadResource(relativePath);
        addProxyService(proxyConfig);
        
    }

    @Test(groups = "wso2.esb", description = "test to verify that the HTTP HEAD method works with PTT.")
	public void testHttpHeadMethod() throws Exception {
    	Thread.sleep(5000);
    	String restURL = (getProxyServiceURLHttp(SERVICE_NAME)) + "/students";
    	DefaultHttpClient httpclient = new DefaultHttpClient();
    	HttpHead httpHead = new HttpHead(restURL);
    	HttpResponse response = httpclient.execute(httpHead);

    	// http head method should return a 202 Accepted
    	assertTrue(response.getStatusLine().getStatusCode() == 202);
    	// it should not contain a message body
    	assertTrue(response.getEntity() == null);
    	
    }


    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
    	if (axis2Server1 != null && axis2Server1.isStarted()) {
            axis2Server1.stop();
        }
        super.cleanup();
    }
}
