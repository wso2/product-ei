/*
*  Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.esb.tenant.test;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.axis2client.ConfigurationContextProvider;
import org.wso2.carbon.integration.common.admin.client.AuthenticatorClient;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.integration.common.admin.client.TenantManagementServiceClient;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

/**
 * Created by shameera on 7/8/14.
 */
public class ServiceChainingTest extends ESBIntegrationTest {
    @BeforeClass(alwaysRun = true)
    public void createTenant() throws Exception {
        super.init();

    }

    @Test(groups = {"wso2.esb"})
    public void testTenantIDInTenantResponsePath() throws Exception {
        // create a tenant
        TenantManagementServiceClient tenantMgtAdminServiceClient = new TenantManagementServiceClient(contextUrls.getBackEndUrl(), sessionCookie);
        tenantMgtAdminServiceClient.addTenant("t5.com", "jhonporter", "jhon", "demo");
        // log as tenant
        AuthenticatorClient authClient = new AuthenticatorClient(contextUrls.getBackEndUrl());
        String session = authClient.login("jhon@t5.com", "jhonporter", "localhost");
        // load configuration in tenant space
        esbUtils.loadESBConfigurationFrom("artifacts/ESB/ServiceChainingConfig.xml", contextUrls.getBackEndUrl(), session);
        // Create service client
        ServiceClient sc = getServiceClient("http://localhost:8480/services/t/t5.com/ServiceChainingProxy", null,
                                            "wso2");
        sc.fireAndForget(createStandardSimpleRequest("wso2"));
        // Get logs by tenant name
        LogViewerClient logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), sessionCookie);
        LogEvent[] logs = logViewerClient.getLogs("ALL", "RECEIVE", "t5.com", "");
        Assert.assertNotNull(logs);
        LogEvent receiveSeqLog_1 = getLogEventByMessage(logs, "DEBUG SEQ 1 = FIRST RECEIVE SEQUENCE");
        Assert.assertNotNull(receiveSeqLog_1);
        LogEvent receiveSeqLog_2 = getLogEventByMessage(logs, "DEBUG SEQ 2 = SECOND RECEIVE SEQUENCE");
        Assert.assertNotNull(receiveSeqLog_2);
    }

    @AfterClass(alwaysRun = true)
    public void cleanup() throws Exception {
        super.cleanup();
    }
    
    private LogEvent getLogEventByMessage(LogEvent[] logs, String msg) {
        for (LogEvent evt : logs) {
            if (evt.getMessage().equals(msg)) {
                return evt;
            }
        }
        return null;
    }
    private ServiceClient getServiceClient(String trpUrl, String addUrl, String operation)
            throws AxisFault
    {
        Options options = new Options();
        ServiceClient serviceClient;
        if ((addUrl != null) && (!"null".equals(addUrl)))
        {
            serviceClient = new ServiceClient(ConfigurationContextProvider.getInstance().getConfigurationContext(), null);
            serviceClient.engageModule("addressing");
            options.setTo(new EndpointReference(addUrl));
        }
        else
        {
            serviceClient = new ServiceClient();
        }
        if ((trpUrl != null) && (!"null".equals(trpUrl))) {
            options.setProperty("TransportURL", trpUrl);
        }
        options.setAction("urn:" + operation);
        serviceClient.setOptions(options);

        return serviceClient;
    }
    private OMElement createStandardSimpleRequest(String symbol)
    {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://services.samples", "ns");
        OMElement method = fac.createOMElement("getSimpleQuote", omNs);
        OMElement value1 = fac.createOMElement("symbol", omNs);

        value1.addChild(fac.createOMText(method, symbol));
        method.addChild(value1);

        return method;
    }

}
