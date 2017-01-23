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
package org.wso2.carbon.esb.mediator.test.throttle.utils;

import org.apache.axiom.om.OMElement;
import org.wso2.esb.integration.common.utils.clients.stockquoteclient.StockQuoteClient;
import java.util.List;

public class ConcurrencyAndRequestThrottleTestClient implements Runnable {
    private StockQuoteClient axis2Client;
    private String proxyServiceURL;
    private List list;
    private ThrottleTestCounter concurrencyThrottleCounter;
    private ThrottleTestCounter requestThrottleCounter;
    private int throttleMaxMsgCount;


    public ConcurrencyAndRequestThrottleTestClient(String proxyServiceURL,
                                                   List list,
                                                   ThrottleTestCounter ConcurrencyThrottleCounter,
                                                   ThrottleTestCounter requestThrottleCounter,
                                                   int throttleMaxMsgCount) {
        this.proxyServiceURL=proxyServiceURL;
        this.list=list;
        this.concurrencyThrottleCounter=ConcurrencyThrottleCounter;
        this.requestThrottleCounter=requestThrottleCounter;
        this.throttleMaxMsgCount=throttleMaxMsgCount;
        axis2Client=new StockQuoteClient();
    }


    @Override
    public void run() {

        try {
            OMElement response = axis2Client.sendSimpleStockQuoteRequest(proxyServiceURL, null, "WSO2");
            if(response.toString().contains("WSO2")){
                list.add("Access Granted");

                try{
//                    since one request is already sent; "throttleMaxMagCount" th request will be Denied
                    for (int i = 0; i < throttleMaxMsgCount; i++) {
                        axis2Client.sendSimpleStockQuoteRequest(proxyServiceURL, null, "WSO2");
                    }
                }catch (Exception e){
                    if(e.getMessage().contains("**Access Denied**")){
                    requestThrottleCounter.increment();
                    }

                }
            }
        } catch (Exception e) {
            if(e.getMessage().contains("**Access Denied**")){
                list.add("Access Denied");
            }
        }
        concurrencyThrottleCounter.increment();
        axis2Client.destroy();
    }
}
