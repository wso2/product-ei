/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.sap;

import org.apache.http.HttpResponse;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.esb.sap.utils.Util;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.SimpleHttpClient;

import static org.testng.Assert.assertTrue;

public class SapIdocTest extends ESBIntegrationTest {

    public static final String MEDIA_TYPE_TEXT_XML = "text/xml";
    public static final int HTTP_SC_OK = 200;

    @BeforeClass()
    public void init() throws Exception {
        super.init();

    }

    /**
     * Sends an IDoc to the SAP systems and asserts for the successful response.
     *
     * @throws Exception if an error occurs while sending the IDoc.
     */
    @Test(groups = {"wso2.esb"}, description = "Test ESB as an IDoc Sender")
    public void testSendIdocUsingCallBlocking() throws Exception {
        SimpleHttpClient soapClient = new SimpleHttpClient();
        String payload = "<_-DSD_-ROUTEACCOUNT_CORDER002>\n"
                         + "         <IDOC BEGIN=\"1\">\n"
                         + "            <EDI_DC40 SEGMENT=\"1\">\n"
                         + "               <TABNAM>EDI_DC40</TABNAM>\n"
                         + "               <MANDT>405</MANDT>\n"
                         + "               <DOCREL>700</DOCREL>\n"
                         + "               <STATUS>30</STATUS>\n"
                         + "               <DIRECT>1</DIRECT>\n"
                         + "               <OUTMOD>2</OUTMOD>\n"
                         + "               <IDOCTYP>/DSD/ROUTEACCOUNT_CORDER002</IDOCTYP>\n"
                         + "               <MESTYP>/DSD/ROUTEACCOUNT_CORDER0</MESTYP>\n"
                         + "               <STDMES>/DSD/R</STDMES>\n"
                         + "               <SNDPOR>SAPCCR</SNDPOR>\n"
                         + "               <SNDPRT>LS</SNDPRT>\n"
                         + "               <SNDPRN>WSO2ESB</SNDPRN>\n"
                         + "               <RCVPOR>SAP_GW_IDO</RCVPOR>\n"
                         + "               <RCVPRT>LS</RCVPRT>\n"
                         + "               <RCVPRN>WSO2ESB</RCVPRN>\n"
                         + "               <CREDAT>20160816</CREDAT>\n"
                         + "               <CRETIM>132507</CRETIM>\n"
                         + "            </EDI_DC40>\n"
                         + "            <_-DSD_-E1BPRAGENERALHD SEGMENT=\"1\">\n"
                         + "               <TOUR_ID>2</TOUR_ID>\n"
                         + "               <MISSION_ID>2</MISSION_ID>\n"
                         + "            </_-DSD_-E1BPRAGENERALHD>\n"
                         + "            <_-DSD_-E1BPRAORDERHD2 SEGMENT=\"1\">\n"
                         + "               <VISIT_ID>0</VISIT_ID>\n"
                         + "               <HH_ORDER>1</HH_ORDER>\n"
                         + "               <ORDER_TIMESTAMP>1</ORDER_TIMESTAMP>\n"
                         + "               <ORDER_TIMEZONE>1</ORDER_TIMEZONE>\n"
                         + "               <REASON>1</REASON>\n"
                         + "               <PO_NUM>1</PO_NUM>\n"
                         + "               <ORD_TIMESTAMP>1</ORD_TIMESTAMP>\n"
                         + "               <ORD_TIMEZONE>1</ORD_TIMEZONE>\n"
                         + "            </_-DSD_-E1BPRAORDERHD2>\n"
                         + "            <_-DSD_-E1BPRAORDERITM2 SEGMENT=\"1\">\n"
                         + "               <VISIT_ID>0</VISIT_ID>\n"
                         + "               <HH_ORDER>2</HH_ORDER>\n"
                         + "               <HH_ORDER_ITM>2</HH_ORDER_ITM>\n"
                         + "               <MATERIAL>2</MATERIAL>\n"
                         + "               <QUANTITY>2</QUANTITY>\n"
                         + "               <UOM>2</UOM>\n"
                         + "               <TA_CODE>2</TA_CODE>\n"
                         + "               <REASON>2</REASON>\n"
                         + "               <ORD_TIMESTAMP>2</ORD_TIMESTAMP>\n"
                         + "               <ORD_TIMEZONE>2</ORD_TIMEZONE>\n"
                         + "               <SPEC_RETURN>0</SPEC_RETURN>\n"
                         + "            </_-DSD_-E1BPRAORDERITM2>\n"
                         + "            <_-DSD_-E1BPRAORDERCOND2 SEGMENT=\"1\">\n"
                         + "               <VISIT_ID>0</VISIT_ID>\n"
                         + "               <HH_ORDER>3</HH_ORDER>\n"
                         + "               <HH_ORDER_ITM>3</HH_ORDER_ITM>\n"
                         + "               <COND_TYPE>3</COND_TYPE>\n"
                         + "               <AMOUNT>3</AMOUNT>\n"
                         + "               <CURRENCY>3</CURRENCY>\n"
                         + "               <CURR_ISO>3</CURR_ISO>\n"
                         + "            </_-DSD_-E1BPRAORDERCOND2>\n"
                         + "            <E1BPPAREX SEGMENT=\"1\">\n"
                         + "               <STRUCTURE>0</STRUCTURE>\n"
                         + "               <VALUEPART1>4</VALUEPART1>\n"
                         + "               <VALUEPART2>5</VALUEPART2>\n"
                         + "               <VALUEPART3>6</VALUEPART3>\n"
                         + "               <VALUEPART4>7</VALUEPART4>\n"
                         + "            </E1BPPAREX>\n"
                         + "         </IDOC>\n"
                         + "      </_-DSD_-ROUTEACCOUNT_CORDER002>\n";
        HttpResponse response = soapClient.doPost(getProxyServiceURLHttp("sapIdocUsingCallBlockingTestProxy"),
                                                  null, payload, MEDIA_TYPE_TEXT_XML);
        Assert.assertEquals(response.getStatusLine().getStatusCode(), HTTP_SC_OK, "incorrect response code received");
        String responseString = Util.getResponsePayload(response);
        assertTrue(responseString.contains("success"),
                   "Incorrect response received. Received response: " + responseString);
        assertTrue(!responseString.contains("<transaction-id>null</transaction-id>"),
                   "Transaction Id not present in the response. Received response: " + responseString);
    }

    @AfterClass(alwaysRun = true)
    public void end() throws Exception {
        super.cleanup();
    }
}
