/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.mediator.test.smooks;

import org.apache.axiom.om.OMElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.carbon.esb.mediator.test.smooks.utils.CSVInputRequestUtil;

import java.util.Iterator;

import static org.testng.Assert.assertEquals;

/**
 * This tests smooks mediator where it verifies the complete XML creation of smooks transformation
 */
public class LargeCSVTransformSmooksTestCase extends ESBIntegrationTest {

    private final int RECORD_COUNT = 11;

    @BeforeClass(alwaysRun = true)
    public void uploadSynapseConfig() throws Exception{
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/smooks/large_csv_smooks_test.xml");
    }

    /**                                            "
     * This test checks the completeness of the resulting XML, when we perform a smooks transformation in a csv file
     * which is large in size(>4K)
     */
    @Test(groups = {"wso2.esb"}, description = "Tests the smooks transformation when large CSV files are sent")
    public void testLargeCSVTransformSmooks() throws Exception {
        OMElement response;
        CSVInputRequestUtil testCSVInputRequest = new CSVInputRequestUtil();
        response = testCSVInputRequest.sendReceive(getMainSequenceURL(), generateCSVInput()) ;
        Iterator csvRecs = response.getChildrenWithLocalName("csv-record");

        int recordCount = 0;
        while (csvRecs.hasNext()){
            Object o =csvRecs.next();
            recordCount++;
        }
        assertEquals(recordCount,RECORD_COUNT,"Smooks Mediator couldn't transform large CSV files(>4K) ");
    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        super.cleanup();
    }


    private String generateCSVInput(){
        StringBuilder csvInput = new StringBuilder();
        csvInput.append("EXTRACT\n");
        for(int i=0 ; i<11; i++){
            csvInput = csvInput.append("DETAIL|128|2012-07-12|2|8550|Smith|Patrick||US|30|99|500|G&A " +
                                       "Function||||||E12F3C8A2D55412B9F9F|33|ADCDE|USD|UNITED STATES|" +
                                       "2012-07-09|2012-06-30|2012-07-11|testJW2079|Y|N|N|827.4200|827.4200" +
                                       "|Non VAT Expense Policy||30|99|500|G&A Function|||G&A Function|||||||||||" +
                                       "|||US|8550||46|1410|BE1|172|REG|Business Meal (attendees)|2012-06-19|" +
                                       "USD|1.0000|M|N|New product discussion||Lure|N|N|2||1|||||||||||||||||||" +
                                       "||||||||||||||||||||||US||||0.0000|150.0000|150.0000|150.0000|150.0000|" +
                                       "CASH|Cash|||||||||||||||||||||||||||||||US|US-CA|HOME|||Company|" +
                                       "Company/Employee Pseudo Payment Code|Employee|Company/Employee" +
                                       " Pseudo Payment Code|70210|DR|+150.0000|2044|||||||||||||||||||" +
                                       "173|100.0000|||||||||||||||||||||||||||||||||||||||||||||||||||||||" +
                                       "|0.0000|150.0000|0.0000|150.0000|Testing123||||||\n") ;
        }
        return csvInput.toString();
    }
}



