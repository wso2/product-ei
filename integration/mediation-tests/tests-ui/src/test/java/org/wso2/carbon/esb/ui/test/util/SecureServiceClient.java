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
package org.wso2.carbon.esb.ui.test.util;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.automation.core.utils.UserInfo;
import org.wso2.carbon.automation.core.utils.frameworkutils.FrameworkFactory;
import org.wso2.carbon.automation.utils.axis2client.SecureAxisServiceClient;

import java.io.File;

public class SecureServiceClient {
    private SecureAxisServiceClient secureClient;

    public SecureServiceClient() {
        secureClient = new SecureAxisServiceClient();
    }


    public OMElement sendSecuredStockQuoteRequest(UserInfo userInfo, String trpUrl,
                                                  int securityPolicyId,
                                                  String symbol) throws Exception {

        return sendSecuredRequest(userInfo, trpUrl, securityPolicyId, createSimpleStockQuoteRequest(symbol), "getQuote");

    }

    public OMElement sendSecuredRequest(UserInfo userInfo, String trpUrl, int securityPolicyId,
                                        OMElement payload, String action) throws Exception {
        boolean isTenant = FrameworkFactory.getFrameworkProperties(ProductConstant.ESB_SERVER_NAME)
                .getEnvironmentSettings().is_runningOnStratos();
        String policyPath = ProductConstant.getSecurityScenarios() + File.separator +
                            "scenario" + securityPolicyId + "-policy.xml";
        String keyStorePath;
        String userCertAlias;
        String encryptionUser;
        String keyStorePassword;

        if (isTenant) {
            keyStorePath = ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + File.separator + "security"
                           + File.separator + "keystore" + File.separator + "client.jks";
            userCertAlias = "client";
            encryptionUser = "service";
            keyStorePassword = "automation";

        } else {
            keyStorePath = ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + File.separator + "keystores"
                           + File.separator + "products" + File.separator + "wso2carbon.jks";
            userCertAlias = "wso2carbon";
            encryptionUser = "wso2carbon";
            keyStorePassword = "wso2carbon";
        }
        return secureClient.sendReceive(userInfo.getUserName(), userInfo.getPassword(), trpUrl, action
                , payload, policyPath, userCertAlias, encryptionUser, keyStorePath, keyStorePassword);

    }

    private OMElement createSimpleStockQuoteRequest(String symbol) {
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
