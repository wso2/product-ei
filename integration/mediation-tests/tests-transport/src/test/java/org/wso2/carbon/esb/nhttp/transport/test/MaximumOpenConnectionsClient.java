/*
*Copyright (c) 2005-2013, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.nhttp.transport.test;

import org.apache.axiom.om.OMElement;
import org.wso2.esb.integration.common.utils.clients.stockquoteclient.StockQuoteClient;
import java.util.List;


public class MaximumOpenConnectionsClient implements Runnable {

    private StockQuoteClient axis2Client;
    private String endpointURL;

    private static int deniedRequests = 0;

    public MaximumOpenConnectionsClient(String endpointURL) {
        this.endpointURL=endpointURL;
        axis2Client=new StockQuoteClient();
    }

    public void run() {
        try {
            OMElement response = axis2Client.sendSimpleStockQuoteRequest(endpointURL, null, "WSO2");
            //System.out.println("\n\n\n\n\nRESPONSE: " + response.toString() + "\n\n\n\n\n");
        } catch (Exception e) {
            //System.out.println("\n\n\n\n\nMESSAGE: " + e.getMessage() + "\n\n\n\n");
            if(e.getMessage().contains("Connection refused") || e.getMessage().contains("failed to respond")){
                this.increaseDeniedRequest();
            }
        }
        axis2Client.destroy();
    }

    private synchronized void increaseDeniedRequest() {
        deniedRequests += 1;
    }

    public static int getDeniedRequests() {
        return deniedRequests;
    }
}