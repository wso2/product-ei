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

package org.wso2.carbon.esb.mediator.test.clone;

import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.servers.axis2.SampleAxis2Server;

import javax.xml.namespace.QName;
import java.util.Iterator;

/*
 * Tests a message burst
 * - for ~5 minutes
 * - with burst delay=5 sec
 * - & burst duration=10sec
 */

public class CloneBurstTestCase extends ESBIntegrationTest {

    int timeDuration = 1; // Overall test duration in minutes
    int burstDuration = 10; // burst duration in seconds
    int delay = 5; // Delay between bursts in seconds

    private SampleAxis2Server axis2Server1;
    private SampleAxis2Server axis2Server2;
    private Trigger trigger;
    Thread[] threads;

    @BeforeClass(groups = "wso2.esb")
    public void setEnvironment() throws Exception {
        init();
        trigger = new Trigger();
        esbUtils.isProxyServiceExist(contextUrls.getBackEndUrl(), sessionCookie, "CloneAndAggregateTestProxy");
        esbUtils.isSequenceExist(contextUrls.getBackEndUrl(), sessionCookie, "cloningMessagesSeq");
        esbUtils.isSequenceExist(contextUrls.getBackEndUrl(), sessionCookie, "aggregateMessagesSeq");

        axis2Server1 = new SampleAxis2Server("test_axis2_server_9001.xml");
        axis2Server2 = new SampleAxis2Server("test_axis2_server_9002.xml");

        axis2Server1.deployService(SampleAxis2Server.SIMPLE_STOCK_QUOTE_SERVICE);
        axis2Server1.start();
        axis2Server2.deployService(SampleAxis2Server.SIMPLE_STOCK_QUOTE_SERVICE);
        axis2Server2.start();
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.esb", description = "Tests message burst", enabled = false)
    public void testBurstMessage() throws Exception {

        MessageSender[] senders = new MessageSender[10];
        threads = new Thread[10];
        for (int i = 0; i < senders.length; i++) {
            senders[i] = new MessageSender("WSO2", trigger);
            threads[i] = new Thread(senders[i]);
            threads[i].start();
        }

        Long time = System.currentTimeMillis();
        while (System.currentTimeMillis() <= (time + (timeDuration * 60 * 1000))) {
            trigger.setTriggered(true);
            Thread.sleep(burstDuration * 1000);
            trigger.setTriggered(false);
            Thread.sleep(delay * 1000);
        }

        trigger.setStopped(true);

        for (int i = 0; i < threads.length; i++) {
            threads[i].stop(); // Use of deprected method to make sure all
            // threds are ended. Tests will still work if we
            // removed this
            threads[i].join();


        }

    }

    @AfterClass(groups = "wso2.esb")
    public void close() throws Exception {
        axis2Server1.stop();
        axis2Server2.stop();
        trigger.setStopped(true);

        if (axis2Server1.isStarted()) {
            axis2Server1.stop();
        }
        if (axis2Server2.isStarted()) {
            axis2Server2.stop();
        }
        threads = null;
        axis2Server1 = null;
        axis2Server2 = null;
        super.cleanup();

    }


    /*
      * Following inner class is used to trigger all threads to send requests to
      * ESB at once
      */

    class Trigger {
        private boolean isTriggered = false;
        private boolean isStopped = false;

        public void setTriggered(boolean b) {
            isTriggered = b;
        }

        public boolean isTriggered() {
            return isTriggered;
        }

        public boolean isStopped() {
            return isStopped;
        }

        public void setStopped(boolean b) {
            this.isStopped = b;
        }

    }

    /* Following class run in threads waiting to be triggered */

    class MessageSender implements Runnable {

        private String symbol;
        private Trigger trigger;
        private CloneClient client = new CloneClient();

        public MessageSender(String symbol, Trigger trigger) {
            this.symbol = symbol;
            this.trigger = trigger;
        }

        public void run() {
            while (!trigger.isStopped()) {
                if (trigger.isTriggered()) {
                    String response = null;
                    Iterator iterator = null;
                    try {
                        response =
                                client.getResponse(getProxyServiceURLHttp("CloneAndAggregateTestProxy"),
                                                   "WSO2");
                        Assert.assertNotNull(response);
                        OMElement envelope = client.toOMElement(response);
                        OMElement soapBody = envelope.getFirstElement();
                        iterator =
                                soapBody.getChildrenWithName(new QName(
                                        "http://services.samples",
                                        "getQuoteResponse"));
                    } catch (Exception e) {
                        log.error(e);
                    } finally {
                        client.destroy();
                    }

                    int i = 0;
                    while (iterator.hasNext()) {
                        i++;
                        OMElement getQuote = (OMElement) iterator.next();
                        Assert.assertTrue(getQuote.toString().contains("WSO2"));
                    }
                    Assert.assertTrue(i == 2);
                }
            }

        }

    }
}
