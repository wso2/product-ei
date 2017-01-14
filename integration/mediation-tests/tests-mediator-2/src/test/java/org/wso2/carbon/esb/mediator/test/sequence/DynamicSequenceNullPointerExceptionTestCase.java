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
package org.wso2.carbon.esb.mediator.test.sequence;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.extensions.axis2server.ServiceNameConstants;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.xml.namespace.QName;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * https://wso2.org/jira/browse/CARBON-12746
 */

public class DynamicSequenceNullPointerExceptionTestCase extends ESBIntegrationTest {


    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception{
        super.init();
        String filePath="/artifacts/ESB/mediatorconfig/sequence/synapse_proxy.xml" ;
        loadESBConfigurationFromClasspath(filePath);
    }
    /*
        create a http header with name "Sequence" and value "correctsequence" and send a request. The
         correctsequence will direct the request to the correct end point.
    */
    @Test(groups = "wso2.esb" , description = "When we have a dynamic sequence defined in a " +
                                              "proxy " +
                                              "service; ESB throws null pointer exception"
    )
    public void testSequenceMediator() throws AxisFault{

        OMElement response;
        axis2Client.addHttpHeader("Sequence","correctsequence");
        response=axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("simpleProxy"),null,"WSO2");

        assertNotNull(response, "Response message null");
        OMElement returnElement=response.getFirstElement();

        OMElement symbolElement=returnElement.getFirstChildWithName(
                new QName("http://services.samples/xsd","symbol"));

        assertEquals(symbolElement.getText(),"WSO2","Fault, invalid response");

    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        super.cleanup();
    }
}
