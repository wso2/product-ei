/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.ei.scenario.test;

import org.apache.axis2.transport.http.HTTPConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.esb.scenario.test.common.ScenarioTestBase;
import org.wso2.carbon.esb.scenario.test.common.http.HTTPUtils;

import java.io.IOException;
import javax.xml.stream.XMLStreamException;

public class ArithmeticOperationsInXMLUsingDatamapperTest extends ScenarioTestBase {

    @BeforeClass
    public void init() throws Exception {
        super.init();
    }

    /**
     * This test is to verify if add operation can be performed with given xml payload using
     *  datamapper mediator.
     */
    @Test(description = "1.6.12.1")
    public void performAddOperationUsingDatamapper() throws IOException, XMLStreamException {
        String apiName = "1_6_12_1_API_performAddOperationUsingDatamapper";
        String testCaseId = "1.6.12.1";
        String apiInvocationUrl = getApiInvocationURLHttp(apiName);

        String request =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "<AddNumbers>\n"
                        + "    <num1>51</num1>\n"
                        + "    <num2>6</num2>\n"
                        + "</AddNumbers>";

        String expectedResponse =
                "<ResultAdd>\n"
                        + "    <result>57.0</result>\n"
                        + "</ResultAdd>";

        HTTPUtils.invokePoxEndpointAndAssert(apiInvocationUrl,request, HTTPConstants.MEDIA_TYPE_APPLICATION_XML,
                testCaseId, expectedResponse, 200, "performAddOperationUsingDatamapper");

    }

    @AfterClass(description = "Server Cleanup", alwaysRun = true)
    public void cleanup() throws Exception {
        super.cleanup();
    }
}
