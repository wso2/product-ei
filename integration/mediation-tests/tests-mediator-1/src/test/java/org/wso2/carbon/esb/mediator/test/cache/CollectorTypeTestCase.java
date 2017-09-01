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
package org.wso2.carbon.esb.mediator.test.cache;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestConstant;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPathExpressionException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.fail;

/**
 * This class will test cacheMediator mediator which has a collector type cacheMediator mediator in 'in'
 * sequence
 */
public class CollectorTypeTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void deployArtifacts() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/cache/CollectorTypeCacheMediator.xml");
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Creating Collector Type Mediator Test Case")
    public void testCollectorTypeMediator() throws AxisFault, XPathExpressionException {
        OMElement response;
        try {
            response = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL()
                    , getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE), "WSO2");
//            String changeValue = response.getFirstElement().getFirstChildWithName(new QName
//                                                                                  ("http://services.samples/xsd", "change")).getText();

            response = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL()
                    , getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE), "WSO2");
//            String change1Value = response.getFirstElement().getFirstChildWithName(new QName
//                                                                                   ("http://services.samples/xsd", "change")).getText();

            response = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL()
                    , getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE), "WSO2");
//            String change2Value = response.getFirstElement().getFirstChildWithName(new QName
//                                                                                   ("http://services.samples/xsd", "change")).getText();

            /*if (changeValue.equals(change1Value)) {
                assertFalse(false, "Response caching worked even with the controller cache in 'in' sequence");
            }

            if (change1Value.equals(change2Value)) {
                assertFalse(false, "Response caching worked even with the controller cache in 'in' sequence");
            }
            if (change2Value.equals(changeValue)) {
                assertFalse(false, "Response caching worked even with the controller cache in 'in' sequence");
            }*/
            fail("Response caching worked even with the controller cache in 'in' sequence");
        } catch (AxisFault message) {
            assertEquals(message.getReason(), ESBTestConstant.INCOMING_MESSAGE_IS_NULL
                    , "CacheMediator Request messages cannot be handled in a collector cache");
        }
    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        super.cleanup();
    }

}
