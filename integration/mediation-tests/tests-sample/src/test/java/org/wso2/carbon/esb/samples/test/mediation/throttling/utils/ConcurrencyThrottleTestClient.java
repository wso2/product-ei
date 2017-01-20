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
package org.wso2.carbon.esb.samples.test.mediation.throttling.utils;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.wso2.carbon.automation.test.utils.axis2client.AxisServiceClient;

import javax.xml.stream.XMLStreamException;
import java.util.List;


public class ConcurrencyThrottleTestClient implements Runnable {

    private AxisServiceClient axis2Client;
    private String endpointUri;
    private List list;
    private ThrottleTestCounter counter;


    public ConcurrencyThrottleTestClient(String endpointUri, List list,
                                         ThrottleTestCounter counter) {
        this.endpointUri = endpointUri;
        this.list = list;
        this.counter = counter;
        axis2Client = new AxisServiceClient();
    }


    @Override
    public void run() {
        try {
            OMElement response = axis2Client.sendReceive(getSleepOperationRequest(), endpointUri, "sleepOperation");
            if (response.toString().contains("Response from server")) {
                addToList(list, "Access Granted");
            }

        } catch (Exception e) {
            if (e.getMessage().contains("**Access Denied**")) {
                addToList(list, "Access Denied");
            }
        }
        counter.increment();
    }

    private static synchronized void addToList(List list, String message) {
        list.add(message);
    }

    private OMElement getSleepOperationRequest() throws XMLStreamException {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMElement omeSleep = fac.createOMElement("sleepOperation", null);
        OMElement omeLoad = fac.createOMElement("load", null);
        omeLoad.setText("2000");
        omeSleep.addChild(omeLoad);
        return omeSleep;

    }
}
