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
package org.wso2.carbon.esb.mediator.test.callOut;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.apache.axis2.AxisFault;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.common.FileManager;

import javax.xml.namespace.QName;

import static org.testng.Assert.assertTrue;

public class ValidPathAxis2XmlTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        super.init();
        String axis2XmlPath = FrameworkPathUtil.getSystemResourceLocation()
                + "/artifacts/ESB/mediatorconfig/callout/client_repo/conf/axis2.xml";
        String uriSynapse = FrameworkPathUtil.getSystemResourceLocation()
                + "/artifacts/ESB/mediatorconfig/callout/ValidPath_Axis2Xml.xml";

        OMElement lineItem = AXIOMUtil.stringToOM(FileManager.readFile(uriSynapse));
        AXIOMXPath xPath = new AXIOMXPath("//ns:configuration");
        xPath.addNamespace("ns", "http://ws.apache.org/ns/synapse");
        OMElement configuration = (OMElement) xPath.selectSingleNode(lineItem);
        //replacing the path
        configuration.getAttribute(new QName("axis2xml")).setAttributeValue(axis2XmlPath);
        updateESBConfiguration(lineItem);

    }

    @SetEnvironment(executionEnvironments = { ExecutionEnvironment.STANDALONE })
    @Test(groups = "wso2.esb")
    public void TestPath() throws AxisFault {

        OMElement response = axis2Client
                .sendSimpleStockQuoteRequest(getProxyServiceURLHttp("CalloutMediatorAxis2RepoPathTestProxy"), "",
                        "IBM");    // send the simplestockquote request. service url is set at the synapse
        boolean responseContainsIBM = response.getFirstElement().toString()
                .contains("IBM");      //checks whether the  response contains IBM

        assertTrue(responseContainsIBM, "Symbol Mismatched");

    }

    @AfterClass(alwaysRun = true)
    public void cleanup() throws Exception {
        super.cleanup();
    }

}
