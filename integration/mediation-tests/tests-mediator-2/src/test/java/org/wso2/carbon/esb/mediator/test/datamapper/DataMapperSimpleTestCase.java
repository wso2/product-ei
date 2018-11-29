/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.mediator.test.datamapper;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;

/**
 * This class contains integration tests for Data Mapper mediator.
 *
 */
public class DataMapperSimpleTestCase extends DataMapperIntegrationTest {

	private final String ARTIFACT_ROOT_PATH = "/artifacts/ESB/mediatorconfig/datamapper/one_to_one/";
	private final String REGISTRY_ROOT_PATH = "datamapper/one_to_one/";

    /**
     * This method contains the test case for mapping single xml object element without arrays
     * to a single xml object element without array
     *
     * @throws Exception
     */
	@Test(groups = { "wso2.esb" }, description = "Datamapper simple one to one xml to xml conversion")
	public void testOneToOneXmlToXml() throws Exception {
		uploadResourcesToGovernanceRegistry(REGISTRY_ROOT_PATH + "xml_to_xml/",
		                                    ARTIFACT_ROOT_PATH + "xml_to_xml" + File.separator);

		String request = "   <company>\n" +
		                 "      <name>WSO2</name>\n" +
		                 "      <usoffice>\n" +
		                 "         <address>\n" +
		                 "            <no>787</no>\n" +
		                 "            <street>Castro Street,Mountain View</street>\n" +
		                 "            <city>CA</city>\n" +
		                 "            <code>94041</code>\n" +
		                 "            <country>US</country>\n" +
		                 "         </address>\n" +
		                 "         <phone> +1 650 745 4499</phone>\n" +
		                 "         <fax> +1 408 689 4328</fax>\n" +
		                 "      </usoffice>\n" +
		                 "      <europeoffice>\n" +
		                 "         <address>\n" +
		                 "            <no>2-6 </no>\n" +
		                 "            <street>Boundary Row</street>\n" +
		                 "            <city>London</city>\n" +
		                 "            <code>SE1 8HP</code>\n" +
		                 "            <country>UK</country>\n" +
		                 "         </address>\n" +
		                 "         <phone>+44 203 318 6025</phone>\n" +
		                 "      </europeoffice>\n" +
		                 "      <asiaoffice>\n" +
		                 "         <address>\n" +
		                 "            <no>20</no>\n" +
		                 "            <street>Palm Grove</street>\n" +
		                 "            <city>Colombo 03</city>\n" +
		                 "            <code>10003</code>\n" +
		                 "            <country>LKA</country>\n" +
		                 "         </address>\n" +
		                 "         <phone>+94 11 214 5345</phone>\n" +
		                 "         <fax>+94 11 2145300</fax>\n" +
		                 "      </asiaoffice>\n" +
		                 "   </company>\n";
        String response = sendRequest(getProxyServiceURLHttp("dataMapperOneToOneXmlToXmlTestProxy"),
                                      request, "text/xml");
        Assert.assertEquals(response,
		                    "<company><offices><asiaoffice><fax> +1 408 689 4328</fax><phone> +1 650 745 " +
                            "4499</phone><address>WSO220Colombo 03</address></asiaoffice><europeoffice><fax>+94 11 " +
                            "2145300</fax><phone>+94 11 214 5345</phone><address>WSO220Colombo " +
                            "03</address></europeoffice><usoffice><phone>+44 203 318 " +
                            "6025</phone><address>WSO2787CA</address></usoffice></offices></company>");
	}

    /**
     * This method contains the test case for mapping single json object element without arrays
     * to a single json object element without array
     *
     * @throws Exception
     */
	@Test(groups = { "wso2.esb" }, description = "Datamapper simple one to one json to json conversion")
	public void testOneToOneJsonToJson() throws Exception {
		uploadResourcesToGovernanceRegistry(REGISTRY_ROOT_PATH + "json_to_json/",
		                                    ARTIFACT_ROOT_PATH + "json_to_json" + File.separator);

		String request = "{\n" +
		                 "    \"name\": \"WSO2\",\n" +
		                 "    \"usoffice\": {\n" +
		                 "      \"address\": {\n" +
		                 "        \"no\": \"787\",\n" +
		                 "        \"street\": \"Castro Street,Mountain View\",\n" +
		                 "        \"city\": \"CA\",\n" +
		                 "        \"code\": \"94041\",\n" +
		                 "        \"country\": \"US\"\n" +
		                 "      },\n" +
		                 "      \"phone\": \" +1 650 745 4499\",\n" +
		                 "      \"fax\": \" +1 408 689 4328\"\n" +
		                 "    },\n" +
		                 "    \"europeoffice\": {\n" +
		                 "      \"address\": {\n" +
		                 "        \"no\": \"2-6 \",\n" +
		                 "        \"street\": \"Boundary Row\",\n" +
		                 "        \"city\": \"London\",\n" +
		                 "        \"code\": \"SE1 8HP\",\n" +
		                 "        \"country\": \"UK\"\n" +
		                 "      },\n" +
		                 "      \"phone\": \"+44 203 318 6025\",\n" +
		                 "      \"fax\": \"+44 11 2145300\"\n" +
		                 "    },\n" +
		                 "    \"asiaoffice\": {\n" +
		                 "      \"address\": {\n" +
		                 "        \"no\": \"20\",\n" +
		                 "        \"street\": \"Palm Grove\",\n" +
		                 "        \"city\": \"Colombo 03\",\n" +
		                 "        \"code\": \"10003\",\n" +
		                 "        \"country\": \"LKA\"\n" +
		                 "      },\n" +
		                 "      \"phone\": \"+94 11 214 5345\",\n" +
		                 "      \"fax\": \"+94 11 2145300\"\n" +
		                 "    }\n" +
		                 "}\n";

        String response = sendRequest(getProxyServiceURLHttp("dataMapperOneToOneJsonToJsonTestProxy"),
                                      request, "application/json");
        Assert.assertEquals(response,
		                    "{\"offices\":{\"usoffice\":{\"address\":\"WSO2787CA\",\"phone\":\" +1 650 745 4499\"," +
                            "\"fax\":\" +1 408 689 4328\"},\"europeoffice\":{\"address\":\"WSO22-6 London\"," +
                            "\"phone\":\"+44 203 318 6025\",\"fax\":\"+44 11 2145300\"}," +
                            "\"asiaoffice\":{\"address\":\"WSO220Colombo 03\",\"phone\":\"+94 11 214 5345\"," +
                            "\"fax\":\"+94 11 2145300\"}}}");
	}

    /**
     * This method contains the test case for mapping single xml object with elements containing
     * underscore in name to a single xml object with elements containing underscore in name
     *
     * @throws Exception
     */
    @Test(groups = { "wso2.esb" }, description = "Data-mapper conversion of input xml messages with underscore "
            + "element names for xml messages with element names with underscore")
    public void testXmlWithUnderscoreToXmlWithUnderscore() throws Exception {
        uploadResourcesToGovernanceRegistry(REGISTRY_ROOT_PATH + "xml_un_to_xml_un/",
                ARTIFACT_ROOT_PATH + "xml_un_to_xml_un" + File.separator);
        String expectedResponse = "<test xmlns:sf=\"urn:sobject.partner.soap.sforce.com\""
                + " xmlns:axis2ns11=\"urn:partner.soap.sforce.com\""
                + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><axis2ns11:records_un "
                + "xsi:type=\"sf:sObject\"><sf:type>Account</sf:type><sf:Id>001E0000002SFO2IAO</sf:Id>"
                + "<sf:CreatedDate>2011-03-15T00:15:00.000Z</sf:CreatedDate><sf:Name>WSO2</sf:Name>"
                + "</axis2ns11:records_un></test>";
        String request = "<test>\n" + "        <axis2ns11:records_un xmlns:axis2ns11=\"urn:partner.soap.sforce.com\" "
                + "xmlns:sf=\"urn:sobject.partner.soap.sforce.com\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                + "                 xsi:type=\"sf:sObject\">\n"
                + "            <sf:type xmlns:sf=\"urn:sobject.partner.soap.sforce.com\">Account</sf:type>\n"
                + "            <sf:Id xmlns:sf=\"urn:sobject.partner.soap.sforce.com\">001E0000002SFO2IAO</sf:Id>\n"
                + "            <sf:CreatedDate xmlns:sf=\"urn:sobject.partner.soap.sforce.com\">"
                + "2011-03-15T00:15:00.000Z</sf:CreatedDate>\n"
                + "            <sf:Id xmlns:sf=\"urn:sobject.partner.soap.sforce.com\">001E0000002SFO2IAO</sf:Id>\n"
                + "            <sf:Name xmlns:sf=\"urn:sobject.partner.soap.sforce.com\">WSO2</sf:Name>\n"
                + "        </axis2ns11:records_un>\n" + "</test>";
        String response = sendRequest(
                getProxyServiceURLHttp("dataMapperXmlWithUnderscoreToXmlWithUnderscoreTestProxy"),
                request, "text/xml");
        Assert.assertEquals(response,expectedResponse);
    }

	/**
	 * @throws Exception exceptions during execution
	 */
	@Test(groups = {"wso2.esb"}, description = "Datamapper simple one to one xml to xml " +
											   "conversion using xslt transformation")
	public void testOneToOneXmlToXmlUsingXSLT() throws Exception {
		uploadResourcesToGovernanceRegistryWithXSLTStyleSheet(REGISTRY_ROOT_PATH + "xml_to_xml_using_xslt/",
															  ARTIFACT_ROOT_PATH + "xml_to_xml_using_xslt" + File
																	  .separator);

		String request = "   <company>\n" +
						 "      <name>WSO2</name>\n" +
						 "      <usoffice>\n" +
						 "         <address>\n" +
						 "            <no>787</no>\n" +
						 "            <street>Castro Street,Mountain View</street>\n" +
						 "            <city>CA</city>\n" +
						 "            <code>94041</code>\n" +
						 "            <country>US</country>\n" +
						 "         </address>\n" +
						 "         <phone> +1 650 745 4499</phone>\n" +
						 "         <fax> +1 408 689 4328</fax>\n" +
						 "      </usoffice>\n" +
						 "      <europeoffice>\n" +
						 "         <address>\n" +
						 "            <no>2-6 </no>\n" +
						 "            <street>Boundary Row</street>\n" +
						 "            <city>London</city>\n" +
						 "            <code>SE1 8HP</code>\n" +
						 "            <country>UK</country>\n" +
						 "         </address>\n" +
						 "         <phone>+44 203 318 6025</phone>\n" +
						 "      </europeoffice>\n" +
						 "      <asiaoffice>\n" +
						 "         <address>\n" +
						 "            <no>20</no>\n" +
						 "            <street>Palm Grove</street>\n" +
						 "            <city>Colombo 03</city>\n" +
						 "            <code>10003</code>\n" +
						 "            <country>LKA</country>\n" +
						 "         </address>\n" +
						 "         <phone>+94 11 214 5345</phone>\n" +
						 "         <fax>+94 11 2145300</fax>\n" +
						 "      </asiaoffice>\n" +
						 "   </company>\n";
		String response = sendRequest(getProxyServiceURLHttp
											  ("dataMapperOneToOneXmlToXmlUsingXSLTTestProxy"),
									  request, "text/xml");
		Assert.assertEquals(response,
							"<company><offices><asiaoffice><fax> +1 408 689 4328</fax><phone> +1 650 745 " +
							"4499</phone><address>WSO220Colombo " +
							"03</address></asiaoffice><europeoffice><fax>+94 11 " +
							"2145300</fax><phone>+94 11 214 5345</phone><address>WSO220Colombo " +
							"03</address></europeoffice><usoffice><phone>+44 203 318 " +
							"6025</phone><address>WSO2787CA</address></usoffice></offices></company>");
	}

	/**
	 * @throws Exception exceptions during execution
	 */
	@Test(groups = {"wso2.esb"}, description = "Datamapper simple one to one xml to xml conversion when xslt "
											   + "stylesheet available but not xslt compatible")
	public void testOneToOneXmlToXmlNotXSLTCompatible() throws Exception {
		uploadResourcesToGovernanceRegistryWithXSLTStyleSheet(REGISTRY_ROOT_PATH + "xml_to_xml_not_xslt_compatible/",
															  ARTIFACT_ROOT_PATH + "xml_to_xml_not_xslt_compatible" +
															  File.separator);

		String request = "   <company>\n" +
						 "      <name>WSO2</name>\n" +
						 "      <usoffice>\n" +
						 "         <address>\n" +
						 "            <no>787</no>\n" +
						 "            <street>Castro Street,Mountain View</street>\n" +
						 "            <city>CA</city>\n" +
						 "            <code>94041</code>\n" +
						 "            <country>US</country>\n" +
						 "         </address>\n" +
						 "         <phone> +1 650 745 4499</phone>\n" +
						 "         <fax> +1 408 689 4328</fax>\n" +
						 "      </usoffice>\n" +
						 "      <europeoffice>\n" +
						 "         <address>\n" +
						 "            <no>2-6 </no>\n" +
						 "            <street>Boundary Row</street>\n" +
						 "            <city>London</city>\n" +
						 "            <code>SE1 8HP</code>\n" +
						 "            <country>UK</country>\n" +
						 "         </address>\n" +
						 "         <phone>+44 203 318 6025</phone>\n" +
						 "      </europeoffice>\n" +
						 "      <asiaoffice>\n" +
						 "         <address>\n" +
						 "            <no>20</no>\n" +
						 "            <street>Palm Grove</street>\n" +
						 "            <city>Colombo 03</city>\n" +
						 "            <code>10003</code>\n" +
						 "            <country>LKA</country>\n" +
						 "         </address>\n" +
						 "         <phone>+94 11 214 5345</phone>\n" +
						 "         <fax>+94 11 2145300</fax>\n" +
						 "      </asiaoffice>\n" +
						 "   </company>\n";
		String response = sendRequest(getProxyServiceURLHttp
											  ("dataMapperOneToOneXmlToXmlNotXSLTCompatibleTestProxy"),
									  request, "text/xml");
		Assert.assertEquals(response,
							"<company><offices><asiaoffice><fax> +1 408 689 4328</fax><phone> +1 650 745 " +
							"4499</phone><address>WSO220Colombo " +
							"03</address></asiaoffice><europeoffice><fax>+94 11 " +
							"2145300</fax><phone>+94 11 214 5345</phone><address>WSO220Colombo " +
							"03</address></europeoffice><usoffice><phone>+44 203 318 " +
							"6025</phone><address>WSO2787CA</address></usoffice></offices></company>");
	}

}
