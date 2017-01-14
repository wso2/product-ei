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

package org.wso2.esb.integration.common.utils.clients.stockquoteclient;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.esb.integration.common.utils.clients.axis2client.SecureAxisServiceClient;

import java.io.File;

public class SecureStockQuoteClient {

    private SecureAxisServiceClient secureClient;

    public SecureStockQuoteClient() {
        secureClient = new SecureAxisServiceClient();
    }


    public OMElement sendSecuredSimpleStockQuoteRequest(String userName, String password,
                                                        String trpUrl,
                                                        String securityPolicyPath, String symbol)
            throws Exception {

        return secureClient.sendReceive(userName, password, trpUrl, "getQuote"
                , createStandardRequest(symbol), securityPolicyPath
                , "alice", "bob"
                , FrameworkPathUtil.getSystemResourceLocation() + "keystores"
                  + File.separator + "products" + File.separator + "store.jks", "password");

    }

public OMElement sendSecuredSimpleStockQuoteRequest(String userName, String password,
                                                        String trpUrl,
                                                        int securityScenarioId, String symbol)
            throws Exception {

        return secureClient.sendReceive(userName, password, trpUrl, "getQuote", createStandardRequest(symbol)
                , FrameworkPathUtil.getSystemResourceLocation() + File.separator + "security"+ File.separator +"policies"+ File.separator + "scenario" + securityScenarioId + "-policy.xml"
                , "alice", "bob"
                , FrameworkPathUtil.getSystemResourceLocation() + File.separator + "keystores"
                  + File.separator + "products" + File.separator + "store.jks", "password");

    }



    private OMElement createStandardRequest(String symbol) {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://services.samples", "ns");
        OMElement method = fac.createOMElement("getQuote", omNs);
        OMElement value1 = fac.createOMElement("request", omNs);
        OMElement value2 = fac.createOMElement("symbol", omNs);

        value2.addChild(fac.createOMText(value1, symbol));
        value1.addChild(value2);
        method.addChild(value1);

        return method;
    }
}
