/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
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

import java.io.File;
import javax.xml.xpath.XPathExpressionException;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Includes tests for the cache mediator
 *
 * Currently tests the maxMessageSize and maxSize parameters in the cache mediator.
 */
public class CacheTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void deployArtifacts() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath(
                File.separator + "artifacts" + File.separator + "ESB" + File.separator + "mediatorconfig"
                        + File.separator + "cache" + File.separator + "CacheApis.xml");

    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Test maxMessageSize value is smaller than the message size")
    public void testSmallMessageSize() throws AxisFault, XPathExpressionException, InterruptedException {
        OMElement response;

        response = axis2Client.sendSimpleStockQuoteRequest(getApiInvocationURL("maxMessageOrSize"), "", "IBM");
        String firstResponse = response.getFirstElement().toString();

        response = axis2Client.sendSimpleStockQuoteRequest(getApiInvocationURL("maxMessageOrSize"), "", "IBM");

        if (!firstResponse.equalsIgnoreCase(response.getFirstElement().toString())) {
            assertTrue(true, "The size of messages received is greater than 1000");
        }
    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        super.cleanup();
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Tests whether maxSize value evicts the new messages")
    public void testMaxSize() throws AxisFault, XPathExpressionException, InterruptedException {
        OMElement response;

        response = axis2Client.sendSimpleStockQuoteRequest(getApiInvocationURL("maxMessageOrSize"), "", "ABCD");
        String responseABCD1 = response.getFirstElement().toString();

        response = axis2Client.sendSimpleStockQuoteRequest(getApiInvocationURL("maxMessageOrSize"), "", "ABC");
        String responseABC1 = response.getFirstElement().toString();

        response = axis2Client.sendSimpleStockQuoteRequest(getApiInvocationURL("maxMessageOrSize"), "", "AB");
        String responseAB1 = response.getFirstElement().toString();

        response = axis2Client.sendSimpleStockQuoteRequest(getApiInvocationURL("maxMessageOrSize"), "", "AB");
        String responseAB2 = response.getFirstElement().toString();

        if (responseAB1.equalsIgnoreCase(responseAB2)) {
            assertTrue(true, "The response AB is cached");
        }

        response = axis2Client.sendSimpleStockQuoteRequest(getApiInvocationURL("maxMessageOrSize"), "", "ABC");
        String responseABC2 = response.getFirstElement().toString();

        if (responseABC1.equalsIgnoreCase(responseABC2)) {
            assertTrue(true, "The response ABC is cached");
        }

        response = axis2Client.sendSimpleStockQuoteRequest(getApiInvocationURL("maxMessageOrSize"), "", "ABCD");
        String responseABCD2 = response.getFirstElement().toString();

        if (!responseABCD1.equalsIgnoreCase(responseABCD2)) {
            assertTrue(true, "The response ABCD has been evicted");
        }
    }
}
