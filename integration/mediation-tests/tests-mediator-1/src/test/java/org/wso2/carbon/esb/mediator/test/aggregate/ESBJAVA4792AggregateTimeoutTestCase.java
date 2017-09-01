/*
*  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/
package org.wso2.carbon.esb.mediator.test.aggregate;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.jaxen.JaxenException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.servers.axis2.SampleAxis2Server;

import javax.xml.stream.XMLStreamException;

/**
 * This test case will make sure that onComplete in Aggregate mediator will only call one time.
 * This happens when a message received after the aggregate time out, It also try to create a aggregate and
 * after the time out , It is also calling onComplete. After Aggregate timeout, It should ignore the
 * messages received
 * https://wso2.org/jira/browse/ESBJAVA-4792
 */
public class ESBJAVA4792AggregateTimeoutTestCase extends ESBIntegrationTest {
    private SampleAxis2Server axis2Server1;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        axis2Server1 = new SampleAxis2Server("test_axis2_server_9001.xml");
        axis2Server1.deployService("LBServiceWithSleep");
        axis2Server1.start();
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/aggregate/aggregateConfig.xml");
    }


    @Test(groups = "wso2.esb", description = "Make sure that on complete is not triggered when message received after " +
                                             "aggregator timeout when iterator is used")
    public void checkOnCompleteExecutionInIterator() throws Exception {
        LogViewerClient logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(),getSessionCookie());
        logViewerClient.clearLogs();
        OMElement payload = getSleepOperationRequestForIterator();
        OMElement response = axis2Client.send(getProxyServiceURLHttp("timeoutIterator"), null, "sleepOperation", payload);
        Assert.assertEquals(countLoadElement(response), 2, "Response must have two aggregated responses");
        //wait a last response to come to aggregator
        Thread.sleep(10000);
        LogEvent[] logEvents = logViewerClient.getAllRemoteSystemLogs();
        int onCompleteCount = 0;
        if (logEvents != null) {
            for (LogEvent log : logEvents) {
                System.out.println(log.getMessage());
                if (log.getMessage().contains("On Complete Triggered in Iterator for ESBJAVA4792AggregateTimeoutTestCase")) {
                    onCompleteCount++;
                }
            }
        }
        Assert.assertEquals(onCompleteCount, 1, "OnComplete has been triggered more than expecting");
    }

    @Test(groups = "wso2.esb", description = "Make sure that on complete is not triggered when message received after " +
                                             "aggregator timeout when clone is used")
    public void checkOnCompleteExecutionInClone() throws Exception {
        LogViewerClient logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(),getSessionCookie());
        logViewerClient.clearLogs();
        OMElement payload = getSleepOperationRequest();
        OMElement response = axis2Client.send(getProxyServiceURLHttps("timeoutClone"), null, "sleepOperation", payload);
        Assert.assertEquals(countLoadElement(response), 2, "Response must have two aggregated responses");
        //wait a last response to come to aggregator
        Thread.sleep(10000);
        LogEvent[] logEvents = logViewerClient.getAllRemoteSystemLogs();
        int onCompleteCount = 0;
        if (logEvents != null) {
            for (LogEvent log : logEvents) {
                System.out.println(log.getMessage());
                if (log.getMessage().contains("On Complete Triggered in Clone for ESBJAVA4792AggregateTimeoutTestCase")) {
                    onCompleteCount++;
                }
            }
        }
        Assert.assertEquals(onCompleteCount, 1, "OnComplete has been triggered more than expecting");
    }


    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        try {
            super.cleanup();
        }finally {
            axis2Server1.stop();
        }
    }

    private OMElement getSleepOperationRequestForIterator() throws XMLStreamException {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMElement omeSleep = fac.createOMElement("sleepOperation", null);
        OMElement omeLoad1 = fac.createOMElement("load", null);
        omeLoad1.setText("1000");
        omeSleep.addChild(omeLoad1);

        OMElement omeLoad2 = fac.createOMElement("load", null);
        omeLoad2.setText("1000");
        omeSleep.addChild(omeLoad2);

        OMElement omeLoad3 = fac.createOMElement("load", null);
        omeLoad3.setText("8000");
        omeSleep.addChild(omeLoad3);


        return omeSleep;

    }

    private OMElement getSleepOperationRequest() throws XMLStreamException {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMElement omeSleep = fac.createOMElement("sleepOperation", null);

        OMElement omeLoad = fac.createOMElement("load", null);
        omeLoad.setText("8000");
        omeSleep.addChild(omeLoad);


        return omeSleep;

    }

    private int countLoadElement(OMElement response) throws JaxenException {
        AXIOMXPath xPath = new AXIOMXPath("//load");
        return xPath.selectNodes(response.cloneOMElement()).size();
    }
}
