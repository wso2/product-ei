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
import org.wso2.carbon.esb.scenario.test.common.ScenarioConstants;
import org.wso2.carbon.esb.scenario.test.common.ScenarioTestBase;
import org.wso2.carbon.esb.scenario.test.common.http.HTTPUtils;

import java.io.IOException;
import javax.xml.stream.XMLStreamException;

/**
 * This class has the test cases to test the Arithmetic operations with the Datamapper mediator
 *  mediator with given xml payload.
 */
public class ArithmeticOperationsInXMLUsingDatamapperTest extends ScenarioTestBase {

    private static final String API_NAME = "1_6_12_API_performArithmeticOperations";

    @BeforeClass
    public void init() throws Exception {
        super.init();
        skipTestsForIncompatibleProductVersions(ScenarioConstants.VERSION_490);
    }

    /**
     * This test is to verify if add operation can be performed with given xml payload using
     *  datamapper mediator.
     */
    @Test(description = "1.6.12.1")
    public void performAddOperationUsingDatamapper() throws IOException, XMLStreamException {
        String testCaseId = "1.6.12.1";
        String apiInvocationUrl = getApiInvocationURLHttp(API_NAME +"/add");
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

        String jdk11ExpectedResponse =
                "<ResultAdd>\n"
                        + "    <result>57</result>\n"
                        + "</ResultAdd>";

        HTTPUtils.invokePoxEndpointAndAssertTwoPayloads(apiInvocationUrl, request,
                HTTPConstants.MEDIA_TYPE_APPLICATION_XML,
                testCaseId, expectedResponse, jdk11ExpectedResponse, 200, "performAddOperationUsingDatamapper");

    }

    /**
     * This test is to verify if substract operation can be performed with given xml payload using
     *  datamapper mediator.
     */
    @Test(description = "1.6.12.2")
    public void performSubstractOperationUsingDatamapper() throws IOException, XMLStreamException {
        String testCaseId = "1.6.12.2";
        String apiInvocationUrl = getApiInvocationURLHttp(API_NAME +"/substract");
        String request =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "<SubstractNumbers>\n"
                        + "    <num1>51</num1>\n"
                        + "    <num2>6</num2>\n"
                        + "</SubstractNumbers>";

        String expectedResponse =
                "<ResultSubstract>\n"
                        + "    <result>45.0</result>\n"
                        + "</ResultSubstract>";

        String jdk11ExpectedResponse =
                "<ResultSubstract>\n"
                        + "    <result>45</result>\n"
                        + "</ResultSubstract>";

        HTTPUtils.invokePoxEndpointAndAssertTwoPayloads(apiInvocationUrl, request,
                HTTPConstants.MEDIA_TYPE_APPLICATION_XML,
                testCaseId, expectedResponse, jdk11ExpectedResponse, 200, "performSubstractOperationUsingDatamapper");

    }

    /**
     * This test is to verify if multiplication operation can be performed with given xml payload using
     *  datamapper mediator.
     */
    @Test(description = "1.6.12.3")
    public void performMultiplicationOperationUsingDatamapper() throws IOException, XMLStreamException {
        String testCaseId = "1.6.12.3";
        String apiInvocationUrl = getApiInvocationURLHttp(API_NAME +"/multiply");
        String request =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "<MultiplyNumbers>\n"
                        + "    <num1>5</num1>\n"
                        + "    <num2>6</num2>\n"
                        + "</MultiplyNumbers>";

        String expectedResponse =
                "<ResultMultiply>\n"
                        + "    <result>30.0</result>\n"
                        + "</ResultMultiply>";

        String jdk11ExpectedResponse =
                "<ResultMultiply>\n"
                        + "    <result>30</result>\n"
                        + "</ResultMultiply>";

        HTTPUtils.invokePoxEndpointAndAssertTwoPayloads(apiInvocationUrl,request, HTTPConstants.MEDIA_TYPE_APPLICATION_XML,
                testCaseId, expectedResponse, jdk11ExpectedResponse, 200,
                "performMultiplicationOperationUsingDatamapper");

    }

    /**
     * This test is to verify if division operation can be performed with given xml payload using
     *  datamapper mediator.
     */
    @Test(description = "1.6.12.4")
    public void performDivisionOperationUsingDatamapper() throws IOException, XMLStreamException {
        String testCaseId = "1.6.12.4";
        String apiInvocationUrl = getApiInvocationURLHttp(API_NAME +"/divide");
        String request =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "<DivideNumbers>\n"
                        + "    <num1>10</num1>\n"
                        + "    <num2>5</num2>\n"
                        + "</DivideNumbers>";

        String expectedResponse =
                "<ResultDivide>\n"
                        + "    <result>2.0</result>\n"
                        + "</ResultDivide>";
        String jdk11ExpectedResponse =
                "<ResultDivide>\n"
                        + "    <result>2</result>\n"
                        + "</ResultDivide>";

        HTTPUtils.invokePoxEndpointAndAssertTwoPayloads(apiInvocationUrl, request,
                HTTPConstants.MEDIA_TYPE_APPLICATION_XML,
                testCaseId, expectedResponse, jdk11ExpectedResponse, 200, "performDivisionOperationUsingDatamapper");

    }

    /**
     * This test is to verify if it can get the round value with given xml payload using
     *  datamapper mediator.
     */
    @Test(description = "1.6.12.5")
    public void getRoundValueUsingDatamapper() throws IOException, XMLStreamException {
        String testCaseId = "1.6.12.5";
        String apiInvocationUrl = getApiInvocationURLHttp(API_NAME +"/round");
        String request =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "<RoundValue>\n"
                        + "    <num1>1.56</num1>\n"
                        + "</RoundValue>";

        String expectedResponse =
                "<ResultRoundValue>\n"
                        + "    <result>2.0</result>\n"
                        + "</ResultRoundValue>";

        String jdk11ExpectedResponse =
                "<ResultRoundValue>\n"
                        + "    <result>2</result>\n"
                        + "</ResultRoundValue>";

        HTTPUtils.invokePoxEndpointAndAssertTwoPayloads(apiInvocationUrl, request,
                HTTPConstants.MEDIA_TYPE_APPLICATION_XML,
                testCaseId, expectedResponse, jdk11ExpectedResponse, 200, "getRoundValueUsingDatamapper");

    }

    /**
     * This test is to verify if it can get the ceiling value with given xml payload using
     *  datamapper mediator.
     */
    @Test(description = "1.6.12.6")
    public void getCeilingValueUsingDatamapper() throws IOException, XMLStreamException {
        String testCaseId = "1.6.12.6";
        String apiInvocationUrl = getApiInvocationURLHttp(API_NAME +"/ceiling");
        String request =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "<CeilingValue>\n"
                        + "    <num1>1.1</num1>\n"
                        + "</CeilingValue>";

        String expectedResponse =
                "<ResultCeilingValue>\n"
                        + "    <result>2.0</result>\n"
                        + "</ResultCeilingValue>";

        String jdk11ExpectedResponse =
                "<ResultCeilingValue>\n"
                        + "    <result>2</result>\n"
                        + "</ResultCeilingValue>";

        HTTPUtils.invokePoxEndpointAndAssertTwoPayloads(apiInvocationUrl, request,
                HTTPConstants.MEDIA_TYPE_APPLICATION_XML,
                testCaseId, expectedResponse, jdk11ExpectedResponse, 200, "getCeilingValueUsingDatamapper");

    }

    /**
     * This test is to verify if it can get the absolute value with given xml payload using
     *  datamapper mediator.
     */
    @Test(description = "1.6.12.7")
    public void getAbsoluteValueUsingDatamapper() throws IOException, XMLStreamException {
        String testCaseId = "1.6.12.7";
        String apiInvocationUrl = getApiInvocationURLHttp(API_NAME +"/absolute");
        String request =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "<AbsoluteValue>\n"
                        + "    <num1>-7</num1>\n"
                        + "</AbsoluteValue>";

        String expectedResponse =
                "<ResultAbsoluteValue>\n"
                        + "    <result>7.0</result>\n"
                        + "</ResultAbsoluteValue>";

        String jdk11ExpectedResponse =
                "<ResultAbsoluteValue>\n"
                        + "    <result>7</result>\n"
                        + "</ResultAbsoluteValue>";

        HTTPUtils.invokePoxEndpointAndAssertTwoPayloads(apiInvocationUrl, request,
                HTTPConstants.MEDIA_TYPE_APPLICATION_XML,
                testCaseId, expectedResponse, jdk11ExpectedResponse, 200, "getAbsoluteValueUsingDatamapper");

    }

    /**
     * This test is to verify if it can get the minimum value with given xml payload using
     *  datamapper mediator.
     */
    @Test(description = "1.6.12.8")
    public void getMinimumValueUsingDatamapper() throws IOException, XMLStreamException {
        String testCaseId = "1.6.12.8";
        String apiInvocationUrl = getApiInvocationURLHttp(API_NAME +"/min");
        String request =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "<MinValue>\n"
                        + "    <num1>5</num1>\n"
                        + "    <num2>9</num2>\n"
                        + "</MinValue>";

        String expectedResponse =
                "<ResultMinimumValue>\n"
                        + "    <result>5.0</result>\n"
                        + "</ResultMinimumValue>";

        String jdk11ExpectedResponse =
                "<ResultMinimumValue>\n"
                        + "    <result>5</result>\n"
                        + "</ResultMinimumValue>";

        HTTPUtils.invokePoxEndpointAndAssertTwoPayloads(apiInvocationUrl, request,
                HTTPConstants.MEDIA_TYPE_APPLICATION_XML,
                testCaseId, expectedResponse, jdk11ExpectedResponse, 200, "getMinimumValueUsingDatamapper");

    }

    /**
     * This test is to verify if it can get the maximum value with given xml payload using
     *  datamapper mediator.
     */
    @Test(description = "1.6.12.9")
    public void getMaximumValueUsingDatamapper() throws IOException, XMLStreamException {
        String testCaseId = "1.6.12.9";
        String apiInvocationUrl = getApiInvocationURLHttp(API_NAME +"/max");
        String request =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "<MaxValue>\n"
                        + "    <num1>5</num1>\n"
                        + "    <num2>9</num2>\n"
                        + "</MaxValue>";

        String expectedResponse =
                "<ResultMaximumValue>\n"
                        + "    <result>9.0</result>\n"
                        + "</ResultMaximumValue>";
        String jdk11ExpectedResponse =
                "<ResultMaximumValue>\n"
                        + "    <result>9</result>\n"
                        + "</ResultMaximumValue>";

        HTTPUtils.invokePoxEndpointAndAssertTwoPayloads(apiInvocationUrl, request,
                HTTPConstants.MEDIA_TYPE_APPLICATION_XML,
                testCaseId, expectedResponse, jdk11ExpectedResponse, 200, "getMaximumValueUsingDatamapper");

    }

    /**
     * This test is to verify if it can get the floor value with given xml payload using
     *  datamapper mediator.
     */
    @Test(description = "1.6.12.10")
    public void getFloorValueUsingDatamapper() throws IOException, XMLStreamException {
        String testCaseId = "1.6.12.10";
        String apiInvocationUrl = getApiInvocationURLHttp(API_NAME +"/floor");
        String request =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "<FloorValue>\n"
                        + "    <num1>4.5</num1>\n"
                        + "</FloorValue>";

        String expectedResponse =
                "<ResultFloorValue>\n"
                        + "    <result>4.0</result>\n"
                        + "</ResultFloorValue>";

        String jdk11ExpectedResponse =
                "<ResultFloorValue>\n"
                        + "    <result>4</result>\n"
                        + "</ResultFloorValue>";

        HTTPUtils.invokePoxEndpointAndAssertTwoPayloads(apiInvocationUrl, request,
                HTTPConstants.MEDIA_TYPE_APPLICATION_XML,
                testCaseId, expectedResponse, jdk11ExpectedResponse, 200, "getFloorValueUsingDatamapper");

    }

    @AfterClass(description = "Server Cleanup", alwaysRun = true)
    public void cleanup() throws Exception {
        super.cleanup();
    }
}
