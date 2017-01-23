/*
*  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
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
package org.wso2.carbon.esb.mediator.test.callOut;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

/**
 * This test cass is written to track the issue reported in https://wso2.org/jira/browse/ESBJAVA-1721
 * With this test case it will load a proxy configuration which send messages to an endpoint using
 * call out mediatior. In that it will set the property OUT_ONLY='true' and check whether there
 * are any errors occuring
 * */

public class CallOutMediatorWithOutOnlyPropertyTest extends ESBIntegrationTest{
     @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/callout/CallOutMediatorOutOnlyTest.xml");
    }

    @Test(groups = {"wso2.esb"},description = "Call")
    public void callOutMediatorWithOutOnlyPropertyTest() throws AxisFault {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("CallOutMediatorOutOnlyTestProxy"), null, "WSO2");
        Assert.assertFalse(response.toString().contains("<ax21:ErrorCode>401000</ax21:ErrorCode>")," Error is using call out mediator with OUT ONLY='true' property");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}
