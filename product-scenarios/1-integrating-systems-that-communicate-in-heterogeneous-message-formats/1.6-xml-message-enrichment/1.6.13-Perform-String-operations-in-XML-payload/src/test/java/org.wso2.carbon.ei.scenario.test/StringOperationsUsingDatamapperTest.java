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

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.esb.scenario.test.common.ScenarioConstants;
import org.wso2.carbon.esb.scenario.test.common.ScenarioTestBase;
import org.wso2.carbon.esb.scenario.test.common.http.HTTPUtils;
import org.apache.axis2.transport.http.HTTPConstants;

import java.io.IOException;
import javax.xml.stream.XMLStreamException;

/**
 * This class include the test cases to test the behaviour of String operations using
 *  Datamapper mediator.
 */
public class StringOperationsUsingDatamapperTest extends ScenarioTestBase {

    private static final String API_NAME = "1_6_13_API_performStringOperations";

    @BeforeClass
    public void init() throws Exception {
        super.init();
        skipTestsForIncompatibleProductVersions(ScenarioConstants.VERSION_490);
    }

    /**
     * This test is to verify if strings can be concatanated with given xml payload using
     *  datamapper mediator.
     *  Usecase: Build the employee name by concatanating employee's initials with last name.
     */
    @Test(description = "1.6.13.1")
    public void concatanateStringUsingDatamapper() throws IOException, XMLStreamException {
        String testCaseId = "1.6.13.1";
        String apiInvocationUrl = getApiInvocationURLHttp(API_NAME +"/concat");
        String request =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "<Employees>\n"
                        + "    <Employee>\n"
                        + "        <initials>S.J.R.S </initials>\n"
                        + "        <firstName>Jayanka</firstName>\n"
                        + "        <lastName>Serasinghe</lastName>\n"
                        + "        <gender>M</gender>\n"
                        + "        <age>34</age>\n"
                        + "    </Employee>\n"
                        + "    <Employee>\n"
                        + "        <initials>G.P.S </initials>\n"
                        + "        <firstName>Supun</firstName>\n"
                        + "        <lastName>De Silva</lastName>\n"
                        + "        <gender>M</gender>\n"
                        + "        <age>31</age>\n"
                        + "    </Employee>\n"
                        + "    <Employee>\n"
                        + "        <initials>S.T </initials>\n"
                        + "        <firstName>Sanath</firstName>\n"
                        + "        <lastName>Jayasuriya</lastName>\n"
                        + "        <gender>M</gender>\n"
                        + "        <age>42</age>\n"
                        + "    </Employee>\n"
                        + "</Employees>";

        String expectedResponse =
                "<Employees>\n"
                        + "    <Employee>\n"
                        + "        <name>S.J.R.S Serasinghe</name>\n"
                        + "        <gender>M</gender>\n"
                        + "    </Employee>\n"
                        + "    <Employee>\n"
                        + "        <name>G.P.S De Silva</name>\n"
                        + "        <gender>M</gender>\n"
                        + "    </Employee>\n"
                        + "    <Employee>\n"
                        + "        <name>S.T Jayasuriya</name>\n"
                        + "        <gender>M</gender>\n"
                        + "    </Employee>\n"
                        + "</Employees>";

        HTTPUtils.invokePoxEndpointAndAssert(apiInvocationUrl,request, HTTPConstants.MEDIA_TYPE_APPLICATION_XML,
                testCaseId, expectedResponse, 200, "concatanateStringUsingDatamapper");

    }

    /**
     * This test is to verify if strings can be splited with given xml payload using
     *  datamapper mediator.
     *  Usecase: Split the bill reference of patient to pharmacy Reference and Medical Reference
     */
    @Test(description = "1.6.13.2")
    public void splitStringUsingDatamapper() throws IOException, XMLStreamException {
        String testCaseId = "1.6.13.2";
        String apiInvocationUrl = getApiInvocationURLHttp(API_NAME +"/split");
        String request =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "<Patients>\n"
                        + "    <Patient>\n"
                        + "        <name>Isuru Uyanage</name>\n"
                        + "        <reference>P2341_M9842</reference>\n"
                        + "        <age>29</age>\n"
                        + "    </Patient>\n"
                        + "    <Patient>\n"
                        + "        <name>Jayanka Serasinghe</name>\n"
                        + "        <reference>P2221_M9452</reference>\n"
                        + "        <age>35</age>\n"
                        + "    </Patient>\n"
                        + "    <Patient>\n"
                        + "        <name>Hector Dias</name>\n"
                        + "        <reference>P9878_M1112</reference>\n"
                        + "        <age>46</age>\n"
                        + "    </Patient>\n"
                        + "</Patients>";

        String expectedResponse =
                "<Patients>\n"
                        + "    <Patient>\n"
                        + "        <name>Isuru Uyanage</name>\n"
                        + "        <pharmacyRef>P2341</pharmacyRef>\n"
                        + "        <channelRef>M9842</channelRef>\n"
                        + "        <age>29</age>\n"
                        + "    </Patient>\n"
                        + "    <Patient>\n"
                        + "        <name>Jayanka Serasinghe</name>\n"
                        + "        <pharmacyRef>P2221</pharmacyRef>\n"
                        + "        <channelRef>M9452</channelRef>\n"
                        + "        <age>35</age>\n"
                        + "    </Patient>\n"
                        + "    <Patient>\n"
                        + "        <name>Hector Dias</name>\n"
                        + "        <pharmacyRef>P9878</pharmacyRef>\n"
                        + "        <channelRef>M1112</channelRef>\n"
                        + "        <age>46</age>\n"
                        + "    </Patient>\n"
                        + "</Patients>";

        HTTPUtils.invokePoxEndpointAndAssert(apiInvocationUrl,request, HTTPConstants.MEDIA_TYPE_APPLICATION_XML,
                testCaseId, expectedResponse, 200, "splitStringUsingDatamapper");

    }

    /**
     * This test is to verify if strings can be converted to lower case with given xml payload using
     *  datamapper mediator.
     *  Usecase: Convert Employee's name and city to Lower case.
     */
    @Test(description = "1.6.13.3")
    public void toLowerCaseUsingDatamapper() throws IOException, XMLStreamException {
        String testCaseId = "1.6.13.3";
        String apiInvocationUrl = getApiInvocationURLHttp(API_NAME +"/lower");
        String request =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "<Employees>\n"
                        + "    <Employee>\n"
                        + "        <name>LEONARDO DICAPRIO</name>\n"
                        + "        <city>CALIFORNIA</city>\n"
                        + "        <gender>M</gender>\n"
                        + "    </Employee>\n"
                        + "    <Employee>\n"
                        + "        <name>MADONA</name>\n"
                        + "        <city>MICHIGAN</city>\n"
                        + "        <gender>F</gender>\n"
                        + "    </Employee>\n"
                        + "    <Employee>\n"
                        + "        <name>EMINEM</name>\n"
                        + "        <city>MISSOURI</city>\n"
                        + "        <gender>M</gender>\n"
                        + "    </Employee>\n"
                        + "</Employees>";

        String expectedResponse =
                "<Employees>\n"
                        + "    <Employee>\n"
                        + "        <name>leonardo dicaprio</name>\n"
                        + "        <city>california</city>\n"
                        + "        <gender>M</gender>\n"
                        + "    </Employee>\n"
                        + "    <Employee>\n"
                        + "        <name>madona</name>\n"
                        + "        <city>michigan</city>\n"
                        + "        <gender>F</gender>\n"
                        + "    </Employee>\n"
                        + "    <Employee>\n"
                        + "        <name>eminem</name>\n"
                        + "        <city>missouri</city>\n"
                        + "        <gender>M</gender>\n"
                        + "    </Employee>\n"
                        + "</Employees>";

        HTTPUtils.invokePoxEndpointAndAssert(apiInvocationUrl,request, HTTPConstants.MEDIA_TYPE_APPLICATION_XML,
                testCaseId, expectedResponse, 200, "toLowerCaseUsingDatamapper");

    }

    /**
     * This test is to verify if strings can be converted to upper case with given xml payload using
     *  datamapper mediator.
     *  Usecase: Convert Employee's name and city to Upper case.
     */
    @Test(description = "1.6.13.4")
    public void toUpperCaseUsingDatamapper() throws IOException, XMLStreamException {
        String testCaseId = "1.6.13.4";
        String apiInvocationUrl = getApiInvocationURLHttp(API_NAME +"/upper");
        String request =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "<Employees>\n"
                        + "    <Employee>\n"
                        + "        <name>Leonardo Dicaprio</name>\n"
                        + "        <city>California</city>\n"
                        + "        <gender>m</gender>\n"
                        + "    </Employee>\n"
                        + "    <Employee>\n"
                        + "        <name>Madona</name>\n"
                        + "        <city>Michigan</city>\n"
                        + "        <gender>f</gender>\n"
                        + "    </Employee>\n"
                        + "    <Employee>\n"
                        + "        <name>Eminem</name>\n"
                        + "        <city>Missouri</city>\n"
                        + "        <gender>m</gender>\n"
                        + "    </Employee>\n"
                        + "</Employees>";

        String expectedResponse =
                "<Employees>\n"
                        + "    <Employee>\n"
                        + "        <name>LEONARDO DICAPRIO</name>\n"
                        + "        <city>CALIFORNIA</city>\n"
                        + "        <gender>m</gender>\n"
                        + "    </Employee>\n"
                        + "    <Employee>\n"
                        + "        <name>MADONA</name>\n"
                        + "        <city>MICHIGAN</city>\n"
                        + "        <gender>f</gender>\n"
                        + "    </Employee>\n"
                        + "    <Employee>\n"
                        + "        <name>EMINEM</name>\n"
                        + "        <city>MISSOURI</city>\n"
                        + "        <gender>m</gender>\n"
                        + "    </Employee>\n"
                        + "</Employees>";

        HTTPUtils.invokePoxEndpointAndAssert(apiInvocationUrl,request, HTTPConstants.MEDIA_TYPE_APPLICATION_XML,
                testCaseId, expectedResponse, 200, "toUpperCaseUsingDatamapper");

    }

    /**
     * This test is to verify if strings can be converted to substring [Substring(startIndex:2, length:7)]
     * with given xml payload using datamapper mediator.
     * usecase: substring SSN and implement employee epf number.
     */
    @Test(description = "1.6.13.5")
    public void substringUsingDatamapper() throws IOException, XMLStreamException {
        String testCaseId = "1.6.13.5";
        String apiInvocationUrl = getApiInvocationURLHttp(API_NAME +"/substring");
        String request =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "<Employees>\n"
                        + "    <Employee>\n"
                        + "        <name>Isuru Uyanage</name>\n"
                        + "        <SSN>896201328V</SSN>\n"
                        + "    </Employee>\n"
                        + "     <Employee>\n"
                        + "        <name>Supun Silva</name>\n"
                        + "        <SSN>876901228V</SSN>\n"
                        + "    </Employee>\n"
                        + "     <Employee>\n"
                        + "        <name>Jayanka Serasinghe</name>\n"
                        + "        <SSN>836603298V</SSN>\n"
                        + "    </Employee>\n"
                        + "</Employees>";

        String expectedResponse =
                "<Employees>\n"
                        + "    <Employee>\n"
                        + "        <name>Isuru Uyanage</name>\n"
                        + "        <epf>6201328</epf>\n"
                        + "    </Employee>\n"
                        + "    <Employee>\n"
                        + "        <name>Supun Silva</name>\n"
                        + "        <epf>6901228</epf>\n"
                        + "    </Employee>\n"
                        + "    <Employee>\n"
                        + "        <name>Jayanka Serasinghe</name>\n"
                        + "        <epf>6603298</epf>\n"
                        + "    </Employee>\n"
                        + "</Employees>";

        HTTPUtils.invokePoxEndpointAndAssert(apiInvocationUrl,request, HTTPConstants.MEDIA_TYPE_APPLICATION_XML,
                testCaseId, expectedResponse, 200, "substringUsingDatamapper");

    }

    /**
     * This test is to verify if strings can be replaced [Replace(Target:"Trainee", Replace With:"permanent")]
     * with given xml payload using datamapper mediator.
     * usecase: Replace all the Trainee employee as Permanent Employees.
     */
    @Test(description = "1.6.13.6")
    public void replaceStringUsingDatamapper() throws IOException, XMLStreamException {
        String testCaseId = "1.6.13.6";
        String apiInvocationUrl = getApiInvocationURLHttp(API_NAME +"/replace");
        String request =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "<Employees>\n"
                        + "    <Employee>\n"
                        + "        <name>Isuru Uyanage</name>\n"
                        + "        <category>Trainee</category>\n"
                        + "    </Employee>\n"
                        + "     <Employee>\n"
                        + "        <name>Jayanka Serasinghe</name>\n"
                        + "        <category>Intern</category>\n"
                        + "    </Employee>\n"
                        + "     <Employee>\n"
                        + "        <name>Supun Silva</name>\n"
                        + "        <category>Trainee</category>\n"
                        + "    </Employee>\n"
                        + "</Employees>";

        String expectedResponse =
                "<Employees>\n"
                        + "    <Employee>\n"
                        + "        <name>Isuru Uyanage</name>\n"
                        + "        <category>permanent</category>\n"
                        + "    </Employee>\n"
                        + "    <Employee>\n"
                        + "        <name>Jayanka Serasinghe</name>\n"
                        + "        <category>Intern</category>\n"
                        + "    </Employee>\n"
                        + "    <Employee>\n"
                        + "        <name>Supun Silva</name>\n"
                        + "        <category>permanent</category>\n"
                        + "    </Employee>\n"
                        + "</Employees>";

        HTTPUtils.invokePoxEndpointAndAssert(apiInvocationUrl,request, HTTPConstants.MEDIA_TYPE_APPLICATION_XML,
                testCaseId, expectedResponse, 200, "replaceStringUsingDatamapper");

    }

    /**
     * This test is to verify if strings can be trimmed, which is removing white spaces from the begining and
     * in the end of the specified string in the given xml payload using the datamapper mediator.
     * usecase: Trim the name of the employees.
     */
    @Test(description = "1.6.13.7")
    public void trimStringUsingDatamapper() throws IOException, XMLStreamException {
        String testCaseId = "1.6.13.7";
        String apiInvocationUrl = getApiInvocationURLHttp(API_NAME +"/trim");
        String request =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "<Employees>\n"
                        + "    <Employee>\n"
                        + "        <name> Isuru Uyanage  </name>\n"
                        + "        <gender>F </gender>\n"
                        + "        <age>29</age>\n"
                        + "    </Employee>\n"
                        + "    <Employee>\n"
                        + "        <name>Supun Silva </name>\n"
                        + "        <gender>M </gender>\n"
                        + "        <age>31</age>\n"
                        + "    </Employee>\n"
                        + "    <Employee>\n"
                        + "        <name>  Leo </name>\n"
                        + "        <gender>M</gender>\n"
                        + "        <age> 27 </age>\n"
                        + "    </Employee>\n"
                        + "</Employees>";

        String expectedResponse =
                "<Employees>\n"
                        + "    <Employee>\n"
                        + "        <name>Isuru Uyanage</name>\n"
                        + "        <gender>F </gender>\n"
                        + "        <age>29</age>\n"
                        + "    </Employee>\n"
                        + "    <Employee>\n"
                        + "        <name>Supun Silva</name>\n"
                        + "        <gender>M </gender>\n"
                        + "        <age>31</age>\n"
                        + "    </Employee>\n"
                        + "    <Employee>\n"
                        + "        <name>Leo</name>\n"
                        + "        <gender>M</gender>\n"
                        + "        <age> 27 </age>\n"
                        + "    </Employee>\n"
                        + "</Employees>";

        HTTPUtils.invokePoxEndpointAndAssert(apiInvocationUrl,request, HTTPConstants.MEDIA_TYPE_APPLICATION_XML,
                testCaseId, expectedResponse, 200, "trimStringUsingDatamapper");

    }

    @AfterClass(description = "Server Cleanup", alwaysRun = true)
    public void cleanup() throws Exception {
        super.cleanup();
    }

}
