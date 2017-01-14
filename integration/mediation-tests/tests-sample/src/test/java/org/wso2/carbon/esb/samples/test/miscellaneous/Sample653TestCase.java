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
package org.wso2.carbon.esb.samples.test.miscellaneous;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.common.TestConfigurationProvider;

import java.io.File;
import java.io.UnsupportedEncodingException;

public class Sample653TestCase extends ESBIntegrationTest {

    private ServerConfigurationManager serverManager = null;

    @BeforeClass(alwaysRun = true)
    public void startJMSBrokerAndConfigureESB() throws Exception {
        super.init();
        serverManager = new ServerConfigurationManager(context);
//setting <parameter name="priorityConfigFile" locked="false">repository/samples/resources/priority/priority-configuration.xml</parameter>
        serverManager.applyConfiguration(new File(TestConfigurationProvider.getResourceLocation()
                + File.separator + "artifacts" + File.separator + "ESB"
                + File.separator + "priority" + File.separator + "axis2.xml"));

        super.init();
        loadSampleESBConfiguration(150);
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {

        //reverting the changes done to esb sever
        super.cleanup();
        Thread.sleep(5000);
        if (serverManager != null) {
            serverManager.restoreToLastConfiguration();
        }

    }
    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "NHTTP Transport Priority Based Dispatching ")
    public void testPriorityBasedMessages() throws Exception {

        Sender senderIBM = new Sender();
        senderIBM.init("IBM", "1");

        Sender senderMSTF = new Sender();
        senderMSTF.init("MSTF", "10");

        senderIBM.start();
        senderMSTF.start();

        Thread.sleep(15000);
        Assert.assertTrue(senderMSTF.getResponseTime() >= senderIBM.getResponseTime()
                , "Symbol with higher priority header took more time than Symbol with lower priority");

    }

    class Sender extends Thread {

        private DefaultHttpClient client = new DefaultHttpClient();
        private ResponseHandler<String> response = new BasicResponseHandler();
        private long responseTime = 0L;
        private HttpPost httpget = new HttpPost(getProxyServiceURLHttp("StockQuoteProxy"));

        public void init(String symbol, String priority) {

            String soapRequest = "<?xml version='1.0' encoding='UTF-8'?>" +
                    "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"" +
                    " xmlns:ser=\"http://services.samples\" xmlns:xsd=\"http://services.samples/xsd\">" +
                    "   <soapenv:Header/>" +
                    "   <soapenv:Body>" +
                    "      <m0:getQuote xmlns:m0=\"http://services.samples\">\n" +
                    "           <m0:request>\n" +
                    "              <m0:symbol>" + symbol +"</m0:symbol>\n" +
                    "           </m0:request>\n" +
                    "        </m0:getQuote>" +
                    "   </soapenv:Body>" +
                    "</soapenv:Envelope>";


            httpget.addHeader("SOAPAction","getQuote");
            httpget.addHeader("priority", priority);

            StringEntity stringEntity= null;

            try {
                stringEntity = new StringEntity(soapRequest);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            stringEntity.setContentType("text/xml; charset=utf-8");
            httpget.setEntity(stringEntity);
        }

        @Override
        public void run() {
            long currentTime = System.currentTimeMillis();
            for (int i = 0; i < 2000; i++) {
                try {
                    client.execute(httpget, response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            long afterTime = System.currentTimeMillis();
            responseTime += (afterTime - currentTime);

        }

        public long getResponseTime () {
            return responseTime;
        }
    }

}
