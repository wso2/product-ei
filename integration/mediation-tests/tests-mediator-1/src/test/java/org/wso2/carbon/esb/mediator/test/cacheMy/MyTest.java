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

package org.wso2.carbon.esb.mediator.test.cacheMy;

import org.apache.axiom.om.OMElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.xml.namespace.QName;

import static org.testng.Assert.assertTrue;

/**
 * This class will test cache mediator which has large value to cache timeout property
 * in it's configuration
 */
public class MyTest extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void uploadSynapseConfig() throws Exception {
        super.init();
    }


    @Test(groups = {"wso2.esb"}, description = "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")
    public void testLargeCacheTimeOut() throws Exception {


        OMElement response;
        response = axis2Client.sendSimpleStockQuoteRequest(null, "http://localhost:9000/services/SimpleStockQuoteService", "WSO2");

       String changeValue = response.getFirstElement().getFirstChildWithName(new QName
                                                                              ("http://services.samples/xsd", "change")).getText();
      Thread.sleep(60000);
        System.out.println(changeValue);
//
//        response = axis2Client.sendSimpleStockQuoteRequest(null, getMainSequenceURL(), "WSO2");
//
//        String newChangeValue = response.getFirstElement().getFirstChildWithName(new QName
//                                                                                 ("http://services.samples/xsd", "change")).getText();
//        Thread.sleep(60000);
//
//        response = axis2Client.sendSimpleStockQuoteRequest(null, getMainSequenceURL(), "WSO2");
//
//        String newChange1Value = response.getFirstElement().getFirstChildWithName(new QName
//                                                                                  ("http://services.samples/xsd", "change")).getText();
//        Thread.sleep(65000);
//
//        response = axis2Client.sendSimpleStockQuoteRequest(null, getMainSequenceURL(), "WSO2");
//
//        String newChange2Value = response.getFirstElement().getFirstChildWithName(new QName
//                                                                                  ("http://services.samples/xsd", "change")).getText();
//
//        log.info("In First Message :" + changeValue);
//        log.info("In Second Message :" + newChangeValue);
//        log.info("In Third Message :" + newChange1Value);
//        log.info("In Fourth Message :" + newChange2Value);
//
//        /**
//         * The 'change' value after 30 mins should not be equal to the initial 'change' value. Other values would have
//         * been fetched from the cache,therefore they are identical.
//         */
//
//        assertTrue(changeValue.equals(newChangeValue) && changeValue.equals(newChange1Value)
//                   && !changeValue.equals(newChange2Value), "Response caching didn't work with a very large time out value ");
//    }

//    @AfterClass(alwaysRun = true)
//    public void close() throws Exception {
//        super.cleanup();
   }
}

