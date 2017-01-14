/*
*Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.mediator.test.property;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.FileRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;

import static org.testng.Assert.assertEquals;

/**
 * This class verifies the functionality of HTTP_SC property.
 */
public class PropertyIntegrationHTTP_SCTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath
                ("/artifacts/ESB/mediatorconfig/property/HTTP_SC.xml");

    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        super.cleanup();
    }

    @Test(groups = "wso2.esb", description = "Getting HTTP status code number ")
    public void testEnrichMediator() throws Exception {

        int responseStatus = 0;

        String strXMLFilename = FrameworkPathUtil.getSystemResourceLocation() + "artifacts"
                                + File.separator + "ESB" + File.separator + "mediatorconfig" +
                                File.separator + "property" + File.separator + "GetQuoteRequest.xml";

        File input = new File(strXMLFilename);
        PostMethod post = new PostMethod(getProxyServiceURLHttp("Axis2ProxyService"));
        RequestEntity entity = new FileRequestEntity(input, "text/xml");
        post.setRequestEntity(entity);
        post.setRequestHeader("SOAPAction", "getQuote");

        HttpClient httpclient = new HttpClient();

        try {
            responseStatus = httpclient.executeMethod(post);
        } finally {
            post.releaseConnection();
        }

        assertEquals(responseStatus, 404, "Response status should be 404");
    }
}
