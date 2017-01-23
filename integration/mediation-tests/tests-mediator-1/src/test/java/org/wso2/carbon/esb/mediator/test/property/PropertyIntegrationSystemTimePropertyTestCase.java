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

package org.wso2.carbon.esb.mediator.test.property;

import org.apache.axiom.om.OMElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * This class tests the functionality of the SYSTEM_TIME property
 */

public class PropertyIntegrationSystemTimePropertyTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath
                ("/artifacts/ESB/mediatorconfig/property/SYSTEM_TIME.xml");

    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        super.cleanup();
    }

    @Test(groups = "wso2.esb", description = "Test return of the current time in milliseconds")
    public void testSystemTime() throws Exception {

        OMElement response = axis2Client.sendSimpleStockQuoteRequest(
                getProxyServiceURLHttp("MyProxy"), null
                , "Wso2");

        assertNotNull(response, "Time returned is Null");

        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        log.debug("Actual Time Is " + dateFormat.format(date));

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(Long.parseLong(response.getText()));
        String serverTime =
                new SimpleDateFormat("HH:mm").format(cal.getTime());

        assertEquals(serverTime, dateFormat.format(date),
                     "Time returned from the property and actual time mismatch");

    }


}
