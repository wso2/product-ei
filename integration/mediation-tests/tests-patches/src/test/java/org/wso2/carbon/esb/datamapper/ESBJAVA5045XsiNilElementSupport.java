/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.datamapper;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.carbon.esb.datamapper.common.DataMapperIntegrationTest;

import java.io.File;

/**
 * This test verifies that Number type elements mapped through datamapper does not write
 * number element instances that have xsi:nil=true attribute.
 */
public class ESBJAVA5045XsiNilElementSupport extends DataMapperIntegrationTest {

    private static final String FORWARD_SLASH = "/";
    private final String DM_ARTIFACT_ROOT_PATH = FORWARD_SLASH + "artifacts" + FORWARD_SLASH + "ESB" + FORWARD_SLASH +
                                                 "mediatorconfig" + FORWARD_SLASH + "datamapper" + FORWARD_SLASH +
                                                 "multiplePrefix" + FORWARD_SLASH;
    private final String DM_REGISTRY_ROOT_PATH = "datamapper/";

    @Test(groups = {"wso2.esb"}, description = "Datamapper : test support for xsi:nil attribute in elements")
    public void testxsiNilAttributeInElement() throws Exception {
        verifyAPIExistence("ESBJAVA5045convertMenuApi");
        uploadResourcesToGovernanceRegistry(DM_REGISTRY_ROOT_PATH + "multiplePrefix/", DM_ARTIFACT_ROOT_PATH,
                "FoodMapping.dmc",
                "FoodMapping_inputSchema.json",
                "FoodMapping_outputSchema.json");

        String requestMsg = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                            + "<breakfast_menu>\n"
                            + "<food>\n"
                            + "<name>Belgian Waffles</name>\n"
                            + "<price>$5.95</price>\n"
                            + "<description>Two of our famous Belgian Waffles with plenty of real maple syrup</description>\n"
                            + "<calories xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>\n"
                            + "<orgin>Belgian</orgin>\n"
                            + "<veg>true</veg>\n"
                            + "</food>\n"
                            + "<food>\n"
                            + "<name>Strawberry Belgian Waffles</name>\n"
                            + "<price>$7.95</price>\n"
                            + "<description>Light Belgian waffles covered with strawberries and whipped cream</description>\n"
                            + "<calories>900</calories>\n"
                            + "<orgin>Belgian</orgin>\n"
                            + "<veg>true</veg>\n"
                            + "</food>\n"
                            + "</breakfast_menu>\n";

        String response = sendRequest(getApiInvocationURL("ESBJAVA5045convertMenuApi"), requestMsg, "application/xml");

        Assert.assertEquals(response, "<menu><item><name>Belgian Waffles</name><price>$5.95</price>"
                                      + "<orgin>Belgian</orgin><veg>true</veg>"
                                      + "<description>Two of our famous Belgian Waffles with plenty of real maple syrup</description>"
                                      + "</item><item><name>Strawberry Belgian Waffles</name><price>$7.95</price>"
                                      + "<calories>900.0</calories><orgin>Belgian</orgin><veg>true</veg>"
                                      + "<description>Light Belgian waffles covered with strawberries and whipped cream</description>"
                                      + "</item></menu>");
    }


}
