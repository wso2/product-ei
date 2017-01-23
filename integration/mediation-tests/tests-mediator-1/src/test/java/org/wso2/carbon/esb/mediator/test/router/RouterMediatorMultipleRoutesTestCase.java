/*
 * Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.mediator.test.router;

import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.esb.integration.common.utils.servers.axis2.SampleAxis2Server;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

/**
 * Tests message mediation with multiple routes
 */

public class RouterMediatorMultipleRoutesTestCase extends ESBIntegrationTest {
    private SampleAxis2Server axis2Server1;
    private SampleAxis2Server axis2Server2;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        init();
        axis2Server1 = new SampleAxis2Server("test_axis2_server_9001.xml");
        axis2Server2 = new SampleAxis2Server("test_axis2_server_9002.xml");

        axis2Server1.deployService(SampleAxis2Server.SIMPLE_STOCK_QUOTE_SERVICE);

        axis2Server2.deployService(SampleAxis2Server.SIMPLE_STOCK_QUOTE_SERVICE);
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.esb", description = "Tests message whether message is routed correctly")
    public void testRoutes() throws Exception {
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/router/router_multiple_routs_test.xml");

        //start server one only. Requests to other servers will return null
        axis2Server1.start();
        OMElement response =
                axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "WSO2");
        Assert.assertTrue(response.toString().contains("WSO2"));
        try{
            response=null;
            response =
                    axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "IBM");
        }catch(Exception e){

        }
        Assert.assertTrue(response==null,"Response should be null");
        axis2Server1.stop();

        //start server one only. Requests to other servers will return null
        axis2Server2.start();
        response=null;
        response =
                axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "IBM");
        Assert.assertTrue(response.toString().contains("IBM"));
        try{
            response=null;
            response =
                    axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "WSO2");
        }
        catch (Exception e){

        }
        Assert.assertTrue(response==null,"Response should be null");
        axis2Server2.stop();

    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        if(axis2Server1.isStarted()){
            axis2Server1.stop();
        }
        if(axis2Server2.isStarted()){
            axis2Server2.stop();
        }
        axis2Server1=null;
        axis2Server2=null;
        super.cleanup();
    }

}


