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

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import static org.testng.Assert.assertFalse;

/**
 * https://wso2.org/jira/browse/ESBJAVA-3022
 */

public class ESBJAVA3022SendingSoapRequestAfterRestRequestTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath(File.separator + "artifacts" + File.separator + "ESB" + File.separator +
                "synapseconfig" + File.separator + "esbjava3022" + File.separator + "synapse.xml");
    }

    @Test(groups = "wso2.esb", description = "test to verify that the soap builder/formatter is invoked properly" +
            "when the soap request is made after rest request.")
	public void testSendingSoapCallAfterRestCall() throws Exception {
    	String restURL = getApiInvocationURL("api_poc_messagetype");//esbServer.getServiceUrl() + "/testmessagetype";
    	DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpGet request = new HttpGet(restURL);
    	HttpResponse response = httpclient.execute(request);
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String responseText = "";
        String line = "";
        while ((line = rd.readLine()) != null) {
            responseText = responseText.concat(line);
        }
        assertFalse(responseText.contains("soapenv:Envelope"), "Another <soapenv:Envelope> tag found inside soap body.");
        assertFalse(responseText.contains("soapenv:Body"), "Another <soapenv:Body> tag found inside soap body.");
    }


    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}
