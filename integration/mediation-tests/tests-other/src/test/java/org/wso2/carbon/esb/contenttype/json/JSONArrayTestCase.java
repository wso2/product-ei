/*
*Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.contenttype.json;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * This test class can be used to verify JSON arrays : basically xml to json array conversion scenario
 */
public class JSONArrayTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath
                ("/artifacts/ESB/jaxrs/xmltojsonarray.xml");
    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        super.cleanup();
    }

    @Test(groups = "wso2.esb", description = "Conversion of xml to json array response")
    public void testXMLToJSONArray() throws Exception {

        String xmlPayload = "<StockQuotes>" +
                " <Stock> " +
                "<Symbol>IBM</Symbol> " +
                "<Last>0.00</Last> " +
                "<Date>15/12/2015</Date> " +
                "<Time>N/A</Time> " +
                "<Change>N/A</Change> " +
                "<Open>N/A</Open> " +
                "<High>N/A</High> " +
                "<Low>N/A</Low> " +
                "<Volume>N/A</Volume> " +
                "<MktCap>N/A</MktCap> " +
                "<PreviousClose>N/A</PreviousClose> " +
                "<PercentageChange>N/A</PercentageChange> " +
                "<AnnRange>N/A - N/A</AnnRange> " +
                "<Earns>N/A</Earns> " +
                "<P-E>N/A</P-E> " +
                "<Name>IBM</Name> " +
                "</Stock> " +
                "<Stock> " +
                "<Symbol>WSO2</Symbol> " +
                "<Last>0.00</Last> " +
                "<Date>15/12/2015</Date> " +
                "<Time>N/A</Time> " +
                "<Change>N/A</Change> " +
                "<Open>N/A</Open> " +
                "<High>N/A</High> " +
                "<Low>N/A</Low> " +
                "<Volume>N/A</Volume> " +
                "<MktCap>N/A</MktCap> " +
                "<PreviousClose>N/A</PreviousClose> " +
                "<PercentageChange>N/A</PercentageChange> " +
                "<AnnRange>N/A - N/A</AnnRange> " +
                "<Earns>N/A</Earns> " +
                "<P-E>N/A</P-E> " +
                "<Name>WSO2</Name> " +
                "</Stock> " +
                "</StockQuotes>";

        URL url = new URL(getApiInvocationURL("Transform"));

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/xml");

        OutputStream os = conn.getOutputStream();

        os.write(xmlPayload.getBytes());
        os.flush();

        assertTrue(conn.getResponseCode() == HttpURLConnection.HTTP_OK,
                "Response Code Mismatch. Expected 200 : Received " + conn.getResponseCode());


        BufferedReader br = new BufferedReader(new InputStreamReader(
                (conn.getInputStream()), "UTF-8"));

        String response = br.readLine();

        os.close();  // closing the OutputStream
        br.close();  // closing the BufferedReader-Stream

        assertNotNull(response, "Response is Null");
        assertTrue(response.contains("{ \"StockQuotes\": { \"Stock\":"), "Response is not in JSON");
        assertTrue(response.contains("IBM"), "Response does not contain Second JSON array element");
        assertTrue(response.contains("WSO2"), "Response does not contain first JSON array element");

    }
}
