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

package org.wso2.carbon.esb.rest.test.api;

import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.endpoint.stub.types.EndpointAdminEndpointAdminException;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;

/**
 * Related to Patch Automation https://wso2.org/jira/browse/CARBON-13203
 */
public class SimpleStockQuoteRESTTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath(File.separator + "artifacts" + File.separator + "ESB"
                                          + File.separator + "synapseconfig" + File.separator + "rest" + File.separator + "synapse.xml");
    }


    @Test(groups = {"wso2.esb"}, description = "Sending a Message Via REST")
    public void testSendingStockQuoteREST() throws IOException, EndpointAdminEndpointAdminException,
                                                   LoginAuthenticationExceptionException,
                                                   XMLStreamException {

        OMElement response = axis2Client.sendSimpleStockQuoteRequestREST(getProxyServiceURLHttp("restCheck")
                , null, "WSO2");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("WSO2 Company"));

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}

