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

public class ESBJAVA5021MultiplePrefixesAndDashSupport extends DataMapperIntegrationTest{

    private final String DM_ARTIFACT_ROOT_PATH = "/artifacts/ESB/mediatorconfig/datamapper/multiplePrefix/";
    private final String DM_REGISTRY_ROOT_PATH = "datamapper/";


    @Test(groups = { "wso2.esb" }, description = "Datamapper : test support for multiple prefixes for same namespace")
    public void testMultiplePrefixesToSameNamespace() throws Exception {
        loadESBConfigurationFromClasspath(DM_ARTIFACT_ROOT_PATH + File.separator + "synapse.xml");
        uploadResourcesToGovernanceRegistry(DM_REGISTRY_ROOT_PATH + "multiplePrefix/", DM_ARTIFACT_ROOT_PATH,
                                "simpleDataAPI_XMLtoXML2_withDash_regConf.dmc",
                                "simpleDataAPI_XMLtoXML2_withDash_regConf_inputSchema.json",
                                "simpleDataAPI_XMLtoXML2_withDash_regConf_outputSchema.json");


        String requestMsg = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"
                + "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" "
                + "         xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsd2=\"http://www.w3.org/2001/XMLSchema\" "
                + "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n"
                + " <SOAP-ENV:Header/>\n"
                + " <SOAP-ENV:Body>\n"
                + "     <data xmlns:test1=\"http://test.com/schema\" xmlns:test2=\"http://test.com/schema\">\n"
                + "         <test1:aaa-aaa>testA</test1:aaa-aaa>\n"
                + "         <test2:b>testB</test2:b>\n"
                + "         <item>\n"
                + "             <test2:name test1:nameType=\"productID\" xsi:type=\"xsd2:string\">car</test2:name>\n"
                + "             <new_price xsi:type=\"xsd:float\">100</new_price>\n"
                + "             <DiscountedPrice xsi2:type=\"xsd2:float\" xmlns:xsi2=\"http://www.w3.org/2001/XMLSchema-instance\" "
                + "                                 test1:discountReason=\"bulk\">100</DiscountedPrice>\n"
                + "         </item>\n"
                + "     </data>\n"
                + " </SOAP-ENV:Body>\n"
                + "</SOAP-ENV:Envelope>";

        String response = sendRequest(getApiInvocationURL("simpleDataAPI_XMLtoXML2_withDash"), requestMsg, "text/xml");
        Assert.assertEquals(response, "<data2><test1:a2 xmlns:test1=\"http://test.com/schema\">testA</test1:a2>"
                + "<test1:b2 xmlns:test1=\"http://test.com/schema\">testB</test1:b2><item2><test1:name2 "
                + "xmlns:test1=\"http://test.com/schema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                + "xsi:type=\"xsd2:string\" test1:nameType=\"productID\">car</test1:name2><test2:price2 "
                + "xmlns:test2=\"http://test.com/schema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xsi:type=\"xsd:float\">100.0</test2:price2>"
                + "<DiscountedPrice2 xmlns:test1=\"http://test.com/schema\" "
                + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" test1:discountReason=\"bulk\" "
                + "xsi:type=\"xsd2:float\">100.0</DiscountedPrice2><Discounted_Price2 "
                + "xmlns:test1=\"http://test.com/schema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                + "test1:discountReason=\"bulk\" xsi:type=\"xsd2:float\">100.0</Discounted_Price2></item2></data2>");

    }


}
