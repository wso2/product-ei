/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.mediator.test.aggregate;


import org.apache.axis2.AxisFault;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * This test class is to check if fault sequence is executed during onComplete sequence error
 * when complete condition is timeout. OnComplete sequence error is simulated using a non-existing
 * sequence (https://github.com/wso2/product-ei/issues/758).
 */
public class FaultSeqInvokeWithAggregateTimeoutTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void deployArtifacts() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath(File.separator + "artifacts" + File.separator + "ESB"
                + File.separator + "mediatorconfig" + File.separator + "aggregate" + File.separator
                + "OnCompleteWithTimeoutTest.xml");
    }

    @Test(groups = "wso2.esb", description = "Add a non-existing mediator and check if fault sequence is hit upon " +
            "onComplete of aggregate mediator with timeout condition")
    public void testFaultResponseIsReceived() throws AxisFault, MalformedURLException, AutomationFrameworkException {
        Map<String, String> requestHeader = new HashMap<>();
        requestHeader.put("Content-type", "application/xml");
        String message = "<marketDetail>\n" +
                "    <market>\n" +
                "        <id>100</id>\n" +
                "        <openTime>10.00AM</openTime>\n" +
                "        <closeTime>4.00PM</closeTime>\n" +
                "        <name>New York</name>\n" +
                "    </market>\n" +
                "    <market>\n" +
                "        <id>200</id>\n" +
                "        <openTime>9.00AM</openTime>\n" +
                "        <closeTime>5.00PM</closeTime>\n" +
                "        <name>London</name>\n" +
                "    </market>\n" +
                "    <market>\n" +
                "        <id>300</id>\n" +
                "        <openTime>8.00AM</openTime>\n" +
                "        <closeTime>3.00PM</closeTime>\n" +
                "        <name>Colombo</name>\n" +
                "    </market>\n" +
                "    <market>\n" +
                "        <id>250</id>\n" +
                "        <openTime>8.00AM</openTime>\n" +
                "        <closeTime>8.00PM</closeTime>\n" +
                "        <name>London</name>\n" +
                "    </market>\n" +
                "</marketDetail>";
        HttpResponse response = HttpRequestUtil.
                doPost(new URL(getApiInvocationURL("testApiAggregate")), message, requestHeader);

        Assert.assertTrue(response.getData().contains("SEQUENCE_ERROR_HANDLER"), "Expected response was not"
                + " received. Got " + response.getData());
    }


    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        super.cleanup();
    }
}
